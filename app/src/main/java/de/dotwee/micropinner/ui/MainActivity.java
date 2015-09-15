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

        mainView = new MainView(this, this, this, this);
        mainView.init();

        // simulate device-boot by sending a new intent to class OnBootReceiver
        sendBroadcast(new Intent(this, OnBootReceiver.class));

        // hide advanced stuff if it hasn't been used the last time
        switchAdvancedLayout(preferencesHandler.isAdvancedUsed());

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

            mainView.restore(parentPin);
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

    private void switchAdvancedLayout(boolean expand) {
        for (View view : mainView.advancedViewList)
            view.setVisibility(expand ? View.VISIBLE : View.GONE);
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
                sendBroadcast(new Intent(this, OnBootReceiver.class));
                break;

            case R.id.switchAdvanced:
                switchAdvancedLayout(mainView.switchAdvanced.isChecked());
                preferencesHandler.setAdvancedUse(mainView.switchAdvanced.isChecked());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchAdvanced:
                switchAdvancedLayout(mainView.switchAdvanced.isChecked());
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
}
