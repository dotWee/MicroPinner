package de.dotwee.micropinner.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.receiver.OnBootReceiver;
import de.dotwee.micropinner.receiver.OnDeleteReceiver;
import de.dotwee.micropinner.receiver.OnNewPinReceiver;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.tools.PreferencesHandler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, Switch.OnCheckedChangeListener {
    private final static String LOG_TAG = "MainActivity";

    private NotificationManager notificationManager;
    private PreferencesHandler preferencesHandler;
    private PinHandler.Pin parentPin;
    private boolean hasParentPin;
    private MainView mainView;

    /**
     * This method checks if the user's device is a tablet, depending on device density.
     *
     * @param context needed to get resources
     * @return true if device screen size is greater than 6 inches
     */
    private static boolean isTablet(Context context) {

        // Compute screen size
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        float width = dm.widthPixels / dm.xdpi;
        float height = dm.heightPixels / dm.ydpi;

        double size = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));

        // Tablet devices should have a screen size greater than 6 inches
        return size >= 6;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A integer value to represent px equivalent to dp depending on device density
     */
    public static int dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return Math.round(dp * (metrics.densityDpi / 160f));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesHandler = PreferencesHandler.getInstance(this);
        if (preferencesHandler.isLightThemeEnabled())
            setTheme(R.style.DialogTheme_Light);

        setContentView(R.layout.dialog_main);

        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        mainView = new MainView(this);
        mainView.onCreate();

        // simulate device-boot by sending a new intent to class OnBootReceiver
        sendBroadcast(new Intent(this, OnBootReceiver.class));

        // hide advanced stuff if it hasn't been used the last time
        mainView.onViewExpand(preferencesHandler.isAdvancedUsed());

        // check if first use
        if (preferencesHandler.isFirstUse())
            // friendly notification that visibility is broken for SDK < 21
            if (Build.VERSION.SDK_INT < 21)
                Toast.makeText(this, getResources().getText(R.string.message_visibility_unsupported), Toast.LENGTH_LONG).show();


        parentPin = (PinHandler.Pin) getIntent().getSerializableExtra(PinHandler.Pin.EXTRA_INTENT);
        if (parentPin != null) {
            hasParentPin = true;

            if (parentPin.isPersistent())
                mainView.buttonCancel.setText(getString(R.string.dialog_action_delete));

            mainView.onViewRestore(parentPin);
        }

    }

    private void pinEntry() {
        if (mainView.isReady()) {
            PinHandler.Pin pin = new PinHandler.Pin(
                    mainView.getVisibility(),
                    mainView.getPriority(),
                    mainView.getTitle(),
                    mainView.getContent(),
                    mainView.isPersistent()
            );

            if (hasParentPin)
                pin.setId(parentPin.getId());

            new PinHandler(this).persistPin(pin);
            finish();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonCancel:
                if (hasParentPin)
                    if (parentPin.isPersistent()) {
                        notificationManager.cancel(parentPin.getId());

                        Intent intent = new Intent(this, OnDeleteReceiver.class);
                        intent.putExtra(PinHandler.Pin.EXTRA_INTENT, parentPin);
                        sendBroadcast(intent);
                    }

                finish();
                break;

            case R.id.buttonPin:
                pinEntry();
                break;

            case R.id.checkBoxNewPin:
                preferencesHandler.setShowNewPinEnabled(mainView.checkBoxShowNewPin.isChecked());
                sendBroadcast(new Intent(this, OnNewPinReceiver.class));
                break;

            case R.id.switchAdvanced:
                mainView.onViewExpand(mainView.switchAdvanced.isChecked());
                preferencesHandler.setAdvancedUse(mainView.switchAdvanced.isChecked());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchAdvanced:
                mainView.onViewExpand(mainView.switchAdvanced.isChecked());
                preferencesHandler.setAdvancedUse(mainView.switchAdvanced.isChecked());
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == mainView.switchAdvanced.getId()) {
            preferencesHandler.setLightThemeEnabled(!preferencesHandler.isLightThemeEnabled());
            this.recreate();
        }

        return false;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (isTablet(this)) {
            setContentView(
                    View.inflate(this, layoutResID, null),
                    new FrameLayout.LayoutParams(
                            dpToPixel(320, this),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );
        } else super.setContentView(layoutResID);
    }

    public interface ViewWrapper {

        /**
         * This method is a post-init.
         */
        void onCreate();

        /**
         * This method inits all the necessary view variables.
         */
        void onViewCreated();

        /**
         *
         */
        void onViewRestore();

        /**
         * @param pin
         */
        void onViewRestore(PinHandler.Pin pin);

        /**
         *
         * @param expand
         */
        void onViewExpand(boolean expand);

        /**
         * @param id
         * @return
         */
        View findView(int id);

        /**
         * This method inits the two spinners visibility and priority with adapters.
         */
        void adaptSpinner();

        /**
         * This method inits the view lists advancedViewList and clickViewList.
         * <p/>
         * Used to simplify view.setOnClickListener() and the advanced view switch.
         */
        void adaptLists();

        /**
         * This method reads the title edittext and returns its value.
         *
         * @return The edittext title's value.
         */
        String getTitle();

        /**
         * This method reads the content edittext and returns its value.
         *
         * @return The edittext content's value.
         */
        String getContent();

        /**
         * This method reads the priority spinner and returns its matching type.
         *
         * @return Either {@see android.app.Notification.PRIORITY_LOW}, {@see android.app.Notification.PRIORITY_MIN}, {@see android.app.Notification.PRIORITY_HIGH} or {@see android.app.Notification.PRIORITY_DEFAULT}.
         */
        int getPriority();

        /**
         * This method reads the visibility spinner and returns its matching type.
         *
         * @return Either {@see android.app.Notification.VISIBILITY_PRIVATE} {@see android.app.Notification.VISIBILITY_SECRET}, {@see android.app.Notification.VISIBILITY_PUBLIC} or 0 if visibility api is not supported.
         */
        int getVisibility();

        /**
         * This method reads the state of the title.
         *
         * @return True is title is not null and not empty.
         */
        boolean isReady();

        /**
         * This method reads the state of the persistent-checkbox
         *
         * @return True if checkbox is checked.
         */
        boolean isPersistent();
    }
}
