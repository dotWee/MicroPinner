package de.dotwee.micropinner.presenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.receiver.OnDeleteReceiver;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.tools.PreferencesHandler;
import de.dotwee.micropinner.view.MainActivity;

/**
 * Created by Lukas Wolfsteiner on 29.10.2015.
 */
public class MainPresenterImpl implements MainPresenter {
    private static final String LOG_TAG = "MainPresenterImpl";
    private final PreferencesHandler preferencesHandler;
    private final NotificationManager notificationManager;
    private final MainActivity mainActivity;
    private final PinHandler pinHandler;
    private final Intent intent;
    private PinHandler.Pin parentPin;

    public MainPresenterImpl(@NonNull MainActivity mainActivity, @NonNull Intent intent) {
        this.preferencesHandler = PreferencesHandler.getInstance(mainActivity);
        this.pinHandler = new PinHandler(mainActivity);
        this.mainActivity = mainActivity;
        this.intent = intent;

        notificationManager = (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyAboutParentPin();

        // check if first use
        if (preferencesHandler.isFirstUse()) {

            // friendly notification that visibility is broken for SDK < 21
            if (Build.VERSION.SDK_INT < 21) {
                Toast.makeText(mainActivity, mainActivity.getResources().getText(R.string.message_visibility_unsupported), Toast.LENGTH_LONG).show();
            }
        }

        // restore the switch's state if advanced is enabled
        if (preferencesHandler.isAdvancedUsed()) {

            Switch advancedSwitch = (Switch) mainActivity.findViewById(R.id.switchAdvanced);
            if (advancedSwitch != null) {

                advancedSwitch.setChecked(true);
            }
        }

        // restore show-actions checkbox
        if (preferencesHandler.isNotificationActionsEnabled()) {
            CheckBox checkBox = (CheckBox) mainActivity.findViewById(R.id.checkBoxShowActions);
            checkBox.setChecked(true);
        }

        this.onViewExpand(preferencesHandler.isAdvancedUsed());
    }

    /**
     * This method handles a long-click on a switch
     */
    @Override
    public void onSwitchHold() {
        boolean previous = preferencesHandler.isLightThemeEnabled();
        preferencesHandler.setLightThemeEnabled(!previous);

        // recreate activity to apply theme
        mainActivity.recreate();
    }

    /**
     * This method handles the click on the positive dialog button.
     */
    @Override
    public void onButtonPositive() {
        PinHandler.Pin newPin;

        try {
            newPin = toPin();

            if (hasParentPin()) {
                newPin.setId(parentPin.getId());
            }

            pinHandler.persistPin(newPin);
            mainActivity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles the click on the negative dialog button.
     */
    @Override
    public void onButtonNegative() {
        if (hasParentPin()) {
            notificationManager.cancel(parentPin.getId());

            Intent intent = new Intent(mainActivity, OnDeleteReceiver.class);
            intent.putExtra(PinHandler.Pin.EXTRA_INTENT, parentPin);
            mainActivity.sendBroadcast(intent);
        }

        mainActivity.finish();
    }

    /**
     * This method handles the click on the show-actions checkbox.
     */
    @Override
    public void onShowActions() {
        CheckBox checkBox = (CheckBox) mainActivity.findViewById(R.id.checkBoxShowActions);
        preferencesHandler.setNotificationActionsEnabled(checkBox.isChecked());
    }

    /**
     * This method handles the expand action.
     *
     * @param expand If view should expand or not.
     */
    @Override
    public void onViewExpand(boolean expand) {
        int[] expandedIds = new int[]{R.id.checkBoxPersistentPin, R.id.checkBoxShowActions};

        for (int id : expandedIds) {
            View view = mainActivity.findViewById(id);

            if (view != null) {
                view.setVisibility(expand ? View.VISIBLE : View.GONE);
            }
        }

        preferencesHandler.setAdvancedUse(expand);
    }

    /**
     * This method checks if a parent pin exists.
     */
    @Override
    public boolean hasParentPin() {
        if (intent != null) {
            Serializable extra = intent.getSerializableExtra(PinHandler.Pin.EXTRA_INTENT);

            if (extra != null && extra instanceof PinHandler.Pin) {
                this.parentPin = (PinHandler.Pin) extra;
                return true;
            }
        }

        return false;
    }

    /**
     * This method creates a {@link PinHandler.Pin} from the view.
     *
     * @return A not null {@link PinHandler.Pin}.
     * @throws Exception if pin is null or an error appeared on creation
     */
    @NonNull
    @Override
    public PinHandler.Pin toPin() throws Exception {
        if (mainActivity.getPinTitle().isEmpty()) {

            Toast.makeText(mainActivity, R.string.message_empty_title, Toast.LENGTH_SHORT).show();
            throw new Exception(mainActivity.getString(R.string.message_empty_title));

        } else return new PinHandler.Pin(
                mainActivity.getVisibility(),
                mainActivity.getPriority(),
                mainActivity.getPinTitle(),
                mainActivity.getPinContent(),
                mainActivity.isPersistent(),
                mainActivity.showActions()
        );
    }

    /**
     * This method returns the corresponding view of the presenter.
     *
     * @return A non null {@link AppCompatActivity} activity.
     */
    @NonNull
    @Override
    public MainActivity getView() {
        return this.mainActivity;
    }

    /**
     * This method notifies all layouts about the parent pin.
     */
    @Override
    public void notifyAboutParentPin() {
        boolean state = hasParentPin();

        TextView textViewTitle = (TextView) mainActivity.findViewById(R.id.dialogTitle);
        if (textViewTitle != null) {
            textViewTitle.setText(
                    state ? R.string.edit_name : R.string.app_name
            );
        }

        Button buttonNegative = (Button) mainActivity.findViewById(R.id.buttonCancel);
        if (buttonNegative != null) {
            buttonNegative.setText(
                    state ? R.string.dialog_action_delete : R.string.dialog_action_cancel
            );
        }

        if (state) {

            handleParentVisibility(parentPin);
            handleParentPriority(parentPin);

            handleParentTitle(parentPin);
            handleParentContent(parentPin);

            CheckBox checkBoxPersistent = (CheckBox) mainActivity.findViewById(R.id.checkBoxPersistentPin);
            if (checkBoxPersistent != null) {

                checkBoxPersistent.setChecked(parentPin.isPersistent());
            }

        }
    }

    @Override
    public void handleParentVisibility(@NonNull PinHandler.Pin pin) {

        Spinner spinnerVisibility = (Spinner) mainActivity.findViewById(R.id.spinnerVisibility);
        if (spinnerVisibility != null) {
            int visibilityPosition;

            switch (parentPin.getVisibility()) {
                case Notification.VISIBILITY_PUBLIC:
                    visibilityPosition = 0;
                    break;

                case Notification.VISIBILITY_PRIVATE:
                    visibilityPosition = 1;
                    break;

                case Notification.VISIBILITY_SECRET:
                    visibilityPosition = 2;
                    break;

                default:
                    visibilityPosition = 0;
                    break;
            }

            spinnerVisibility.setSelection(visibilityPosition, true);
        }
    }

    @Override
    public void handleParentPriority(@NonNull PinHandler.Pin pin) {

        Spinner spinnerPriority = (Spinner) mainActivity.findViewById(R.id.spinnerPriority);
        if (spinnerPriority != null) {
            int priorityPosition;

            switch (parentPin.getPriority()) {
                case Notification.PRIORITY_DEFAULT:
                    priorityPosition = 0;
                    break;

                case Notification.PRIORITY_MIN:
                    priorityPosition = 1;
                    break;

                case Notification.PRIORITY_LOW:
                    priorityPosition = 2;
                    break;

                case Notification.PRIORITY_HIGH:
                    priorityPosition = 3;
                    break;

                default:
                    priorityPosition = 0;
                    break;
            }

            spinnerPriority.setSelection(priorityPosition, true);
        }
    }

    @Override
    public void handleParentTitle(@NonNull PinHandler.Pin pin) {

        EditText editTextTitle = (EditText) mainActivity.findViewById(R.id.editTextTitle);
        if (editTextTitle != null) {

            editTextTitle.setText(pin.getTitle());
        }
    }

    @Override
    public void handleParentContent(@NonNull PinHandler.Pin pin) {

        EditText editTextContent = (EditText) mainActivity.findViewById(R.id.editTextContent);
        if (editTextContent != null) {

            editTextContent.setText(pin.getContent());
        }
    }
}
