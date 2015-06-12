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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.BootReceiver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Spinner.OnItemSelectedListener {
    public static final String EXTRA_VISIBILITY = "EXTRA_VISIBILITY", EXTRA_PRIORITY = "EXTRA_PRIORITY", EXTRA_TITLE = "EXTRA_TITLE", EXTRA_CONTENT = "EXTRA_CONTENT", EXTRA_NOTIFICATION = "EXTRA_NOTIFICATION";
    public static final boolean DEBUG = false;
    public static final String PREF_FIRSTUSE = "pref_firstuse", PREF_SHOWNEWPIN = "pref_shownewpin";
    SharedPreferences sharedPreferences;
    Spinner spinnerVisibility, spinnerPriority;
    EditText editTextContent, editTextTitle;
    CheckBox checkBoxShowNewPin;
    TextView dialogTitle;
    Button buttonCancel, buttonPin;
    private int VISIBILITY_SELECTED = 1;
    private int PRIORITY_SELECTED = 0;

    public static Notification generatePin(Context context, int visibility, int priority, int id, String title, String content) {
        Notification.Builder notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(priority)
                .setSmallIcon(R.drawable.ic_star_24dp);

        if (Build.VERSION.SDK_INT >= 21) {
            notification.setVisibility(visibility);
        }

        Intent resultIntent = new Intent(context, EditActivity.class);
        resultIntent.putExtra(EXTRA_NOTIFICATION, id);
        resultIntent.putExtra(EXTRA_CONTENT, content);
        resultIntent.putExtra(EXTRA_TITLE, title);

        resultIntent.putExtra(EXTRA_VISIBILITY, visibility);
        resultIntent.putExtra(EXTRA_PRIORITY, priority);

        notification.setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        return notification.build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main);

        dialogTitle = (TextView) findViewById(R.id.dialogTitle);
        dialogTitle.setText(getResources().getString(R.string.main_name));

        checkBoxShowNewPin = (CheckBox) findViewById(R.id.checkBoxNewPin);
        checkBoxShowNewPin.setChecked(sharedPreferences.getBoolean(MainActivity.PREF_SHOWNEWPIN, false));
        checkBoxShowNewPin.setOnClickListener(this);

        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonPin = (Button) findViewById(R.id.buttonPin);

        editTextContent = (EditText) findViewById(R.id.editTextContent);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextTitle.performClick();

        sendBroadcast(new Intent(this, BootReceiver.class));

        buttonCancel.setOnClickListener(this);
        buttonPin.setOnClickListener(this);

        spinnerVisibility = (Spinner) findViewById(R.id.spinnerVisibility);
        spinnerVisibility.setOnItemSelectedListener(this);
        spinnerVisibility.setAdapter(getVisibilityAdapter());

        spinnerPriority = (Spinner) findViewById(R.id.spinnerPriority);
        spinnerPriority.setOnItemSelectedListener(this);
        spinnerPriority.setAdapter(getPriorityAdapter());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(PREF_FIRSTUSE, false)) {

            /*
            getPackageManager().setComponentEnabledSetting(
                    new ComponentName(this, MainActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
                    */

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
        return VISIBILITY_SELECTED;
    }

    public int _getPriority() {
        return PRIORITY_SELECTED;
    }

    public String _getTitle() {
        return editTextTitle.getText().toString();
    }

    public String _getContent() {
        return editTextContent.getText().toString();
    }

    private void pinEntry() {
        String title = _getTitle();
        String content = _getContent();
        int notificationID = randomNotificationID();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (title.equalsIgnoreCase("") | title.equalsIgnoreCase(null))
            Toast.makeText(this, "The title has to contain text.", Toast.LENGTH_SHORT).show();

        else {
            if (DEBUG)
                Log.i("Main Activity", "New pin: " + "\nTitle: " + title + "\nContent: " + content + "\nVisibility: " + _getVisibility() + "\nPriority: " + _getPriority());
            notificationManager.notify(notificationID, generatePin(this, _getVisibility(), _getPriority(), notificationID, title, content));
            finish();
        }
    }

    private int randomNotificationID() {
        int start = 1, end = 256;

        return new Random().nextInt(end - start + 1) + start;
    }

    @Override
    public void onClick(View v) {
        if (DEBUG) Log.i("MainActivity", "clicked: " + v.getId());
        switch (v.getId()) {
            case R.id.buttonCancel:
                finish();
                break;
            case R.id.buttonPin:
                pinEntry();
                break;
            case R.id.checkBoxNewPin:
                if (checkBoxShowNewPin.isChecked())
                    sharedPreferences.edit().putBoolean(PREF_SHOWNEWPIN, false).apply();
                else sharedPreferences.edit().putBoolean(PREF_SHOWNEWPIN, true).apply();
                sendBroadcast(new Intent(this, BootReceiver.class));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();

        switch (selected) {
            case "Low":
                PRIORITY_SELECTED = Notification.PRIORITY_LOW;
                break;

            case "Default":
                PRIORITY_SELECTED = Notification.PRIORITY_DEFAULT;
                break;

            case "High":
                PRIORITY_SELECTED = Notification.PRIORITY_HIGH;
                break;
        }

        if (Build.VERSION.SDK_INT >= 21) {
            switch (selected) {
                case "Private":
                    VISIBILITY_SELECTED = Notification.VISIBILITY_PRIVATE;
                    break;

                case "Public":
                    VISIBILITY_SELECTED = Notification.VISIBILITY_PUBLIC;
                    break;

                case "Secret":
                    VISIBILITY_SELECTED = Notification.VISIBILITY_SECRET;
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
