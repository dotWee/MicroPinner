package de.dotwee.micropinner.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.BootReceiver;

/**
 * Created by Lukas on 09.06.2015.
 */
public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String LOG_TAG = "EditActivity";
    NotificationManager notificationManager;
    EditText editTextContent, editTextTitle;
    CheckBox checkBoxPersistentPin;
    Button buttonCancel, buttonPin;
    Switch switchAdvanced;
    TextView dialogTitle;
    Intent receivedIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main);

        receivedIntent = getIntent();
        sendBroadcast(new Intent(this, BootReceiver.class));

        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // setup persistent checkbox
        checkBoxPersistentPin = (CheckBox) findViewById(R.id.checkBoxPersistentPin);
        checkBoxPersistentPin.setOnClickListener(this);

        // setup dialog title
        dialogTitle = (TextView) findViewById(R.id.dialogTitle);
        dialogTitle.setText(getResources().getString(R.string.edit_name));


        // setup advanced-switch
        switchAdvanced = (Switch) findViewById(R.id.switchAdvanced);
        switchAdvanced.setOnClickListener(this);

        // setup buttons
        buttonPin = (Button) findViewById(R.id.buttonPin);
        buttonPin.setOnClickListener(this);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);

        restoreFromIntent();
        switchAdvancedLayout(false);
    }

    void restoreFromIntent() {
        editTextContent = (EditText) findViewById(R.id.editTextContent);
        editTextContent.setText(receivedIntent.getStringExtra(MainActivity.EXTRA_CONTENT));

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextTitle.setText(receivedIntent.getStringExtra(MainActivity.EXTRA_TITLE));

        if (receivedIntent.getBooleanExtra(MainActivity.EXTRA_PERSISTENT, false)) {
            checkBoxPersistentPin.setChecked(true);
            buttonCancel.setText("Delete");
        }
    }

    void updatePin() {
        String newContent = editTextContent.getText().toString();
        String newTitle = editTextTitle.getText().toString();

        if (newTitle.equalsIgnoreCase("") | newTitle.equalsIgnoreCase(null))
            Toast.makeText(this, "The title has to contain text.", Toast.LENGTH_SHORT).show();
        else {
            receivedIntent.putExtra(MainActivity.EXTRA_CONTENT, newContent);
            receivedIntent.putExtra(MainActivity.EXTRA_TITLE, newTitle);

            notificationManager.notify(receivedIntent.getIntExtra(MainActivity.EXTRA_NOTIFICATION, 1), MainActivity.generatePin(
                    this,
                    receivedIntent.getIntExtra(MainActivity.EXTRA_VISIBILITY, 0), // get visibility from intent
                    receivedIntent.getIntExtra(MainActivity.EXTRA_PRIORITY, 0),
                    receivedIntent.getIntExtra(MainActivity.EXTRA_NOTIFICATION, 1),
                    newTitle,
                    newContent,
                    checkBoxPersistentPin.isChecked()
            ));
            finish();
        }
    }

    private void switchAdvancedLayout(boolean expand) {
        // TODO expand animation

        if (expand) {
            checkBoxPersistentPin.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkBoxNewPin).setVisibility(View.GONE);
            checkBoxPersistentPin.setVisibility(View.GONE);

            // hide spinner
            findViewById(R.id.spinnerVisibility).setVisibility(View.GONE);
            findViewById(R.id.spinnerPriority).setVisibility(View.GONE);

            // hide description textviews
            findViewById(R.id.textViewVisibility).setVisibility(View.GONE);
            findViewById(R.id.textViewPriority).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switchAdvanced:
                switchAdvancedLayout(checkBoxPersistentPin.isChecked());
                break;

            case R.id.buttonPin:
                updatePin();
                break;

            case R.id.buttonCancel:
                if (receivedIntent.getBooleanExtra(MainActivity.EXTRA_PERSISTENT, false))
                    notificationManager.cancel(receivedIntent.getIntExtra(MainActivity.EXTRA_NOTIFICATION, 1));
                else finish();
                break;
        }
    }
}
