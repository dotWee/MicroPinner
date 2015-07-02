package de.dotwee.micropinner.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.JsonHandler;
import de.dotwee.micropinner.tools.OnBootReceiver;
import de.dotwee.micropinner.tools.OnDeleteReceiver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_VISIBILITY = "EXTRA_VISIBILITY", EXTRA_PRIORITY = "EXTRA_PRIORITY", EXTRA_TITLE = "EXTRA_TITLE", EXTRA_CONTENT = "EXTRA_CONTENT", EXTRA_NOTIFICATION = "EXTRA_NOTIFICATION", EXTRA_PERSISTENT = "EXTRA_PERSISTENT";
    public static final String PREF_FIRSTUSE = "pref_firstuse", PREF_SHOWNEWPIN = "pref_shownewpin";
    public static final String LOG_TAG = "MainActivity";
    public static final boolean DEBUG = true;

    CheckBox checkBoxShowNewPin, checkBoxPersistentPin;
    Spinner spinnerVisibility, spinnerPriority;
    EditText editTextContent, editTextTitle;
    SharedPreferences sharedPreferences;
    Switch switchAdvanced;
    TextView dialogTitle;

    public static Notification generatePin(Context context, int visibility, int priority, int id, String title, String content, boolean persistent) {
        Notification.Builder notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_star_24dp)
                .setPriority(priority)
                .setDeleteIntent(PendingIntent.getBroadcast(context, id, new Intent(context, OnDeleteReceiver.class).setAction("notification_cancelled").putExtra(MainActivity.EXTRA_NOTIFICATION, id), PendingIntent.FLAG_CANCEL_CURRENT))
                .setOngoing(priority == Notification.PRIORITY_MIN | persistent);

        if (Build.VERSION.SDK_INT >= 21) {
            notification.setVisibility(visibility);
        }

        Intent resultIntent = new Intent(context, EditActivity.class);

        resultIntent.putExtra(EXTRA_PERSISTENT, priority == Notification.PRIORITY_MIN | persistent);
        resultIntent.putExtra(EXTRA_NOTIFICATION, id);
        resultIntent.putExtra(EXTRA_CONTENT, content);
        resultIntent.putExtra(EXTRA_TITLE, title);

        resultIntent.putExtra(EXTRA_VISIBILITY, visibility);
        resultIntent.putExtra(EXTRA_PRIORITY, priority);

        notification.setContentIntent(PendingIntent.getActivity(context, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        new JsonHandler(context).append(title, content, visibility, priority, persistent, id);

        return notification.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main);
        new JsonHandler(this).restore();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // setup dialog title
        dialogTitle = (TextView) findViewById(R.id.dialogTitle);
        dialogTitle.setText(getResources().getString(R.string.main_name));

        // setup checkbox and set it to its last instance state
        checkBoxShowNewPin = (CheckBox) findViewById(R.id.checkBoxNewPin);
        checkBoxShowNewPin.setChecked(sharedPreferences.getBoolean(MainActivity.PREF_SHOWNEWPIN, true));
        checkBoxShowNewPin.setOnClickListener(this);

        checkBoxPersistentPin = (CheckBox) findViewById(R.id.checkBoxPersistentPin);
        checkBoxPersistentPin.setOnClickListener(this);

        // declare buttons and edittexts
        findViewById(R.id.buttonCancel).setOnClickListener(this);
        findViewById(R.id.buttonPin).setOnClickListener(this);

        // setup advanced-switch
        switchAdvanced = (Switch) findViewById(R.id.switchAdvanced);
        switchAdvanced.setOnClickListener(this);

        editTextContent = (EditText) findViewById(R.id.editTextContent);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);

        // set focus to the title input
        editTextTitle.performClick();

        // simulate device-boot by sending a new intent to class OnBootReceiver
        sendBroadcast(new Intent(this, OnBootReceiver.class));

        // hide advanced stuff
        switchAdvancedLayout(false);

        // declare spinner
        spinnerPriority = (Spinner) findViewById(R.id.spinnerPriority);
        spinnerPriority.setAdapter(getPriorityAdapter());

        spinnerVisibility = (Spinner) findViewById(R.id.spinnerVisibility);
        spinnerVisibility.setAdapter(getVisibilityAdapter());

        // check if first use
        if (!sharedPreferences.getBoolean(PREF_FIRSTUSE, false)) {

            /* hide icon from launcher
            getPackageManager().setComponentEnabledSetting(
                    new ComponentName(this, MainActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
                    */

            // friendly notification that visibility is broken for SDK < 21
            if (Build.VERSION.SDK_INT < 21)
                Toast.makeText(this, getResources().getText(R.string.message_visibility_unsupported), Toast.LENGTH_LONG).show();

            sharedPreferences.edit().putBoolean(PREF_FIRSTUSE, true).apply();
        }
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
        if (DEBUG) Log.i(LOG_TAG, "Spinner selected: " + selected);

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
        if (DEBUG) Log.i(LOG_TAG, "Spinner selected: " + selected);

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
        String content = _getContent();
        int notificationID = randomNotificationID();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (title.equalsIgnoreCase("") | title.equalsIgnoreCase(null))
            Toast.makeText(this, getResources().getText(R.string.message_empty_title), Toast.LENGTH_SHORT).show();

        else {
            if (DEBUG)
                Log.i(LOG_TAG, "New pin: " + "\nTitle: " + title + "\nContent: " + content + "\nVisibility: " + _getVisibility() + "\nPriority: " + _getPriority());
            notificationManager.notify(notificationID, generatePin(this, _getVisibility(), _getPriority(), notificationID, title, content, _getPersistent()));
            finish();
        }
    }

    private void switchAdvancedLayout(boolean expand) {
        // TODO expand animation

        if (expand) {
            checkBoxShowNewPin.setVisibility(View.VISIBLE);
            checkBoxPersistentPin.setVisibility(View.VISIBLE);
        } else {
            checkBoxShowNewPin.setVisibility(View.GONE);
            checkBoxPersistentPin.setVisibility(View.GONE);
        }
    }

    private int randomNotificationID() {
        int start = 1, end = Integer.MAX_VALUE;

        return new Random().nextInt(end - start + 1) + start;
    }

    @Override
    public void onClick(View v) {
        if (DEBUG) Log.i(LOG_TAG, "clicked: " + v.getId());

        switch (v.getId()) {
            case R.id.buttonCancel:
                finish();
                break;

            case R.id.buttonPin:
                pinEntry();
                break;

            case R.id.checkBoxNewPin:
                sharedPreferences.edit().putBoolean(PREF_SHOWNEWPIN, checkBoxShowNewPin.isChecked()).apply();
                sendBroadcast(new Intent(this, OnBootReceiver.class));
                break;

            case R.id.switchAdvanced:
                if (switchAdvanced.isChecked()) switchAdvancedLayout(true);
                else switchAdvancedLayout(false);
                break;
        }
    }
}
