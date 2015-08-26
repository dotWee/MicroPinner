package de.dotwee.micropinner.ui;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import de.dotwee.micropinner.R;
import de.dotwee.micropinner.receiver.OnBootReceiver;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.tools.PreferencesHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Switch.OnCheckedChangeListener {
    public static final String LOG_TAG = "MainActivity";
    public static final boolean DEBUG = true;
    PreferencesHandler preferencesHandler;

    CheckBox checkBoxShowNewPin, checkBoxPersistentPin, checkBoxEnableRestore;
    Spinner spinnerVisibility, spinnerPriority;
    EditText editTextContent, editTextTitle;
    List<View> advancedViewList, clickViewList;
    Switch switchAdvanced;
    TextView dialogTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main);

        clickViewList = new ArrayList<>();
        advancedViewList = new ArrayList<>();
        preferencesHandler = PreferencesHandler.getInstance(this);

        // setup dialog title
        dialogTitle = (TextView) findViewById(R.id.dialogTitle);
        dialogTitle.setText(getResources().getString(R.string.main_name));

        // setup checkbox and set it to its last instance state
        checkBoxShowNewPin = (CheckBox) findViewById(R.id.checkBoxNewPin);
        checkBoxShowNewPin.setChecked(preferencesHandler.isShowNewPinEnabled());
        advancedViewList.add(checkBoxShowNewPin);

        checkBoxEnableRestore = (CheckBox) findViewById(R.id.checkBoxEnableRestore);
        checkBoxEnableRestore.setChecked(preferencesHandler.isRestoreEnabled());
        advancedViewList.add(checkBoxEnableRestore);

        checkBoxPersistentPin = (CheckBox) findViewById(R.id.checkBoxPersistentPin);
        advancedViewList.add(checkBoxPersistentPin);

        // declare buttons and edittexts
        clickViewList.add(findViewById(R.id.buttonCancel));
        clickViewList.add(findViewById(R.id.buttonPin));

        // setup advanced-switch
        switchAdvanced = (Switch) findViewById(R.id.switchAdvanced);
        switchAdvanced.setChecked(preferencesHandler.isAdvancedUsed());
        switchAdvanced.setOnCheckedChangeListener(this);
        clickViewList.add(switchAdvanced);

        editTextContent = (EditText) findViewById(R.id.editTextContent);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);

        // set focus to the title input
        editTextTitle.performClick();

        // simulate device-boot by sending a new intent to class OnBootReceiver
        sendBroadcast(new Intent(this, OnBootReceiver.class));

        // hide advanced stuff
        switchAdvancedLayout(preferencesHandler.isAdvancedUsed());

        // declare spinner
        spinnerPriority = (Spinner) findViewById(R.id.spinnerPriority);
        spinnerPriority.setAdapter(getPriorityAdapter());

        spinnerVisibility = (Spinner) findViewById(R.id.spinnerVisibility);
        spinnerVisibility.setAdapter(getVisibilityAdapter());

        clickViewList.addAll(advancedViewList);
        for (View view : clickViewList)
            view.setOnClickListener(this);

        // check if first use
        if (preferencesHandler.isFirstUse())
            // friendly notification that visibility is broken for SDK < 21
            if (Build.VERSION.SDK_INT < 21)
                Toast.makeText(this, getResources().getText(R.string.message_visibility_unsupported), Toast.LENGTH_LONG).show();
    }

    private ArrayAdapter<String> getPriorityAdapter() {
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.array_priorities));
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return priorityAdapter;
    }

    private ArrayAdapter<String> getVisibilityAdapter() {
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.array_visibilities));
        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return visibilityAdapter;
    }

    public int _getVisibility() {
        String selected = spinnerVisibility.getSelectedItem().toString();
        Log.i(LOG_TAG, "Spinner selected: " + selected);

        if (Build.VERSION.SDK_INT >= 21) {
            if (selected.equalsIgnoreCase(getResources().getString(R.string.visibility_private)))
                return Notification.VISIBILITY_PRIVATE;
            else if (selected.equalsIgnoreCase(getResources().getString(R.string.visibility_secret)))
                return Notification.VISIBILITY_SECRET;
            else return Notification.VISIBILITY_PUBLIC;
        } else return 0;
    }

    public int _getPriority() {
        String selected = spinnerPriority.getSelectedItem().toString();
        Log.i(LOG_TAG, "Spinner selected: " + selected);

        if (selected.equalsIgnoreCase(getResources().getString(R.string.priority_low)))
            return Notification.PRIORITY_LOW;
        else if (selected.equalsIgnoreCase(getResources().getString(R.string.priority_min)))
            return Notification.PRIORITY_MIN;
        else if (selected.equalsIgnoreCase(getResources().getString(R.string.priority_high)))
            return Notification.PRIORITY_HIGH;
        else return Notification.PRIORITY_DEFAULT;
    }

    public String _getTitle() {
        return editTextTitle.getText().toString();
    }

    public String _getContent() {
        return editTextContent.getText().toString();
    }

    public boolean _getPersistent() {
        return checkBoxPersistentPin.isChecked();
    }

    private void pinEntry() {
        String title = _getTitle();
        int notificationID = randomNotificationID();

        if (title.equalsIgnoreCase("") | title.equalsIgnoreCase(null))
            Toast.makeText(this, getResources().getText(R.string.message_empty_title), Toast.LENGTH_SHORT).show();

        else {
            PinHandler.Pin pin = new PinHandler.Pin(_getVisibility(), _getPriority(), notificationID, _getTitle(), _getContent(), _getPersistent());
            new PinHandler(this).persistPin(pin);
            finish();
        }
    }

    private void switchAdvancedLayout(boolean expand) {
        // TODO expand animation

        for (View view : advancedViewList)
            view.setVisibility(expand ? View.VISIBLE : View.GONE);
    }

    private int randomNotificationID() {
        int start = 1, end = Integer.MAX_VALUE;

        return new Random().nextInt(end - start + 1) + start;
    }

    @Override
    public void onClick(View v) {
        Log.i(LOG_TAG, "clicked: " + v.getId());

        switch (v.getId()) {
            case R.id.buttonCancel:
                finish();
                break;

            case R.id.buttonPin:
                pinEntry();
                break;

            case R.id.checkBoxNewPin:
                preferencesHandler.setShowNewPinEnabled(checkBoxShowNewPin.isChecked());
                sendBroadcast(new Intent(this, OnBootReceiver.class));
                break;

            case R.id.checkBoxEnableRestore:
                preferencesHandler.setRestoreEnabled(checkBoxEnableRestore.isChecked());
                break;

            case R.id.switchAdvanced:
                switchAdvancedLayout(switchAdvanced.isChecked());
                preferencesHandler.setAdvancedUse(switchAdvanced.isChecked());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchAdvanced:
                switchAdvancedLayout(switchAdvanced.isChecked());
                preferencesHandler.setAdvancedUse(switchAdvanced.isChecked());
                break;
        }
    }
}
