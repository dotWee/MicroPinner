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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Random;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.BootReceiver;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Spinner.OnItemSelectedListener {
    public static final String EXTRA_VISIBILITY = "EXTRA_VISIBILITY", EXTRA_PRIORITY = "EXTRA_PRIORITY", EXTRA_TITLE = "EXTRA_TITLE", EXTRA_CONTENT = "EXTRA_CONTENT", EXTRA_NOTIFICATION = "EXTRA_NOTIFICATION";
    public static final String PREF_FIRSTUSE = "pref_firstuse";
    Spinner spinnerVisibility, spinnerPriority;
    EditText editTextContent, editTextTitle;
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
        setContentView(R.layout.activity_main);

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(PREF_FIRSTUSE, false)) {

            /*
            getPackageManager().setComponentEnabledSetting(
                    new ComponentName(this, MainActivity.class),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
                    */

            Toast.makeText(this, "App-Icon is now hidden.", Toast.LENGTH_SHORT).show();
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
        int i = randomNotificationID();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(i, generatePin(this, _getVisibility(), _getPriority(), i, _getTitle(), _getContent()));
    }

    private int randomNotificationID() {
        int start = 1, end = 256;

        return new Random().nextInt(end - start + 1) + start;
    }

    @Override
    public void onClick(View v) {
        Log.i("MainActivity", "clicked: " + v.getId());
        switch (v.getId()) {
            case R.id.buttonCancel:
                finish();
                break;
            case R.id.buttonPin:
                pinEntry();
                finish();
                break;
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
