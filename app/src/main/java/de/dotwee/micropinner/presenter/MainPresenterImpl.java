package de.dotwee.micropinner.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.database.PinDatabase;
import de.dotwee.micropinner.database.PinSpec;
import de.dotwee.micropinner.receiver.OnDeleteReceiver;
import de.dotwee.micropinner.tools.NotificationTools;
import de.dotwee.micropinner.tools.PreferencesHandler;

/**
 * Created by Lukas Wolfsteiner on 29.10.2015.
 */
public class MainPresenterImpl implements MainPresenter {
    private static final String TAG = MainPresenterImpl.class.getSimpleName();
    private final PreferencesHandler preferencesHandler;
    private final NotificationManager notificationManager;
    private final Activity activity;
    private final int activityPermissionRequestCode;

    private final PinDatabase pinDatabase;
    private final Intent intent;
    private PinSpec parentPin;

    public MainPresenterImpl(@NonNull Activity activity, @NonNull Intent intent, int permissionRequestCode) {
        this.preferencesHandler = PreferencesHandler.getInstance(activity);
        this.activity = activity;
        this.activityPermissionRequestCode = permissionRequestCode;
        this.intent = intent;

        pinDatabase = PinDatabase.getInstance(activity.getApplicationContext());

        notificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        // check if first use
        if (preferencesHandler.isFirstUse()) {

            // friendly notification that visibility is broken for SDK < 21
            if (Build.VERSION.SDK_INT < 21) {
                Toast.makeText(activity,
                        activity.getResources().getText(R.string.message_visibility_unsupported),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * This method handles a long-click on a switch
     */
    @Override
    public void onSwitchHold() {
        Toast.makeText(activity, "Theme will change automatically by day and night.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(@NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission has been granted.
            createPin();
        } else {
            // Permission request was denied.
            Toast.makeText(activity,
                    activity.getResources().getText(R.string.message_notifications_permission_denied),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * For sdk version <code>33</code> we need to request permission to send notifications.
     *
     * Requests the {@link android.Manifest.permission#POST_NOTIFICATIONS} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     *
     * @see <a href="https://stackoverflow.com/questions/74201274/changed-targetsdkversion-to-33-from-30-and-now-notifications-are-not-coming-up">android - changed targetSdkVersion to 33 from 30 and now notifications are not coming up - Stack Overflow</a>
     * @see <a href="https://github.com/android/permissions-samples/blob/c4573f1218cef81295b519592718ecef3b144095/RuntimePermissionsBasic/Application/src/main/java/com/example/android/basicpermissions/MainActivity.java">permissions-samples/MainActivity.java · android/permissions-samples</a>
     * @see <a href="https://developer.android.com/training/permissions/requesting#java">Request app permissions - Android Developers</a>
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermission() {
        // Request the permission. The result will be received in the activity's onRequestPermissionResult().
        ActivityCompat.requestPermissions(activity,
            new String[]{Manifest.permission.POST_NOTIFICATIONS}, activityPermissionRequestCode);
    }

    private void createPin() {
        PinSpec newPin;

        try {
            newPin = toPin();

            if (hasParentPin()) {
                newPin.setId(parentPin.getId());
            }

            pinDatabase.writePin(newPin);
            NotificationTools.notify(activity, newPin);

            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles the click on the positive dialog button.
     */
    @Override
    public void onButtonPositive() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // No permission needed:
            createPin();
        } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted:
            createPin();
        } else {
            // Permission is missing and must be requested.
            requestNotificationPermission();
        }
    }

    /**
     * This method handles the click on the negative dialog button.
     */
    @Override
    public void onButtonNegative() {
        if (hasParentPin()) {
            notificationManager.cancel(parentPin.getIdAsInt());

            Intent intent = new Intent(activity, OnDeleteReceiver.class);
            intent.putExtra(NotificationTools.EXTRA_INTENT, parentPin);
            activity.sendBroadcast(intent);
        }

        activity.finish();
    }

    @Override
    public void restore() {

        // restore the switch's state if advanced is enabled
        if (preferencesHandler.isAdvancedUsed()) {

            SwitchCompat advancedSwitch = activity.findViewById(R.id.switchAdvanced);
            if (advancedSwitch != null) {

                advancedSwitch.setChecked(true);
            }
        }

        // restore show-actions checkbox
        if (preferencesHandler.isNotificationActionsEnabled()) {
            CheckBox checkBox = activity.findViewById(R.id.checkBoxShowActions);
            checkBox.setChecked(true);
        }

        // restore advanced layout
        this.onViewExpand(preferencesHandler.isAdvancedUsed());

        // notify about provided intent
        notifyAboutParentPin();
    }

    /**
     * This method handles the click on the show-actions checkbox.
     */
    @Override
    public void onShowActions() {
        CheckBox checkBox = activity.findViewById(R.id.checkBoxShowActions);
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
            View view = activity.findViewById(id);

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
            Serializable extra = intent.getSerializableExtra(NotificationTools.EXTRA_INTENT);

            if (extra instanceof PinSpec) {
                this.parentPin = (PinSpec) extra;
                return true;
            }
        }

        return false;
    }

    /**
     * This method creates a {@link PinSpec} from the view.
     *
     * @return A not null {@link PinSpec}.
     * @throws Exception if pin is null or an error appeared on creation
     */
    @NonNull
    @Override
    public PinSpec toPin() throws Exception {
        if (activity instanceof Data) {
            Data data = (Data) activity;

            if (data.getPinTitle().isEmpty()) {

                Toast.makeText(activity, R.string.message_empty_title, Toast.LENGTH_SHORT).show();
                throw new Exception(activity.getString(R.string.message_empty_title));
            } else {
                return new PinSpec(data.getPinTitle(), data.getPinContent(), data.getVisibility(), data.getPriority(), data.isPersistent(), data.showActions());
            }
        } else {
            throw new IllegalStateException("Activity does not implement the Data callback");
        }
    }

    /**
     * This method returns the corresponding view of the presenter.
     *
     * @return A non null {@link Activity} activity.
     */
    @NonNull
    @Override
    public Activity getView() {
        return this.activity;
    }

    /**
     * This method notifies all layouts about the parent pin.
     */
    @Override
    public void notifyAboutParentPin() {
        boolean state = hasParentPin();

        TextView textViewTitle = activity.findViewById(R.id.dialogTitle);
        if (textViewTitle != null) {
            textViewTitle.setText(state ? R.string.edit_name : R.string.app_name);
        }

        Button buttonNegative = activity.findViewById(R.id.buttonCancel);
        if (buttonNegative != null) {
            buttonNegative.setText(state ? R.string.dialog_action_delete : R.string.dialog_action_cancel);
        }

        if (state) {

            handleParentVisibility(parentPin);
            handleParentPriority(parentPin);

            handleParentTitle(parentPin);
            handleParentContent(parentPin);

            CheckBox checkBoxPersistent = activity.findViewById(R.id.checkBoxPersistentPin);
            if (checkBoxPersistent != null) {

                checkBoxPersistent.setChecked(parentPin.isPersistent());
            }
        }
    }

    @Override
    public void handleParentVisibility(@NonNull PinSpec pin) {

        Spinner spinnerVisibility = activity.findViewById(R.id.spinnerVisibility);
        if (spinnerVisibility != null) {
            int visibilityPosition;

            switch (parentPin.getVisibility()) {
                case NotificationCompat.VISIBILITY_PUBLIC:
                default:
                    visibilityPosition = 0;
                    break;

                case NotificationCompat.VISIBILITY_PRIVATE:
                    visibilityPosition = 1;
                    break;

                case NotificationCompat.VISIBILITY_SECRET:
                    visibilityPosition = 2;
                    break;
            }

            spinnerVisibility.setSelection(visibilityPosition, true);
        }
    }

    @Override
    public void handleParentPriority(@NonNull PinSpec pin) {

        Spinner spinnerPriority = activity.findViewById(R.id.spinnerPriority);
        if (spinnerPriority != null) {
            int priorityPosition;

            switch (parentPin.getPriority()) {
                default:
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
            }

            spinnerPriority.setSelection(priorityPosition, true);
        }
    }

    @Override
    public void handleParentTitle(@NonNull PinSpec pin) {

        EditText editTextTitle = activity.findViewById(R.id.editTextTitle);
        if (editTextTitle != null) {

            editTextTitle.setText(pin.getTitle());
        }
    }

    @Override
    public void handleParentContent(@NonNull PinSpec pin) {

        EditText editTextContent = activity.findViewById(R.id.editTextContent);
        if (editTextContent != null) {

            editTextContent.setText(pin.getContent());
        }
    }
}
