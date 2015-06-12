package de.dotwee.micropinner.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.BootReceiver;

/**
 * Created by Lukas on 09.06.2015.
 */
public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String LOG_TAG = "EditActivity";
    EditText editTextContent, editTextTitle;
    Button buttonCancel, buttonPin;
    Intent receivedIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main);

        receivedIntent = getIntent();
        sendBroadcast(new Intent(this, BootReceiver.class));

        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);

        buttonPin = (Button) findViewById(R.id.buttonPin);
        buttonPin.setOnClickListener(this);

        editTextContent = (EditText) findViewById(R.id.editTextContent);
        editTextContent.setText(receivedIntent.getStringExtra(MainActivity.EXTRA_CONTENT));

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextTitle.setText(receivedIntent.getStringExtra(MainActivity.EXTRA_TITLE));

        // hide spinner
        findViewById(R.id.spinnerVisibility).setVisibility(View.GONE);
        findViewById(R.id.spinnerPriority).setVisibility(View.GONE);
    }

    void updatePin() {
        String newContent = editTextContent.getText().toString();
        String newTitle = editTextTitle.getText().toString();

        if (newTitle.equalsIgnoreCase("") | newTitle.equalsIgnoreCase(null))
            Toast.makeText(this, "The title has to contain text.", Toast.LENGTH_SHORT).show();
        else {
            receivedIntent.putExtra(MainActivity.EXTRA_CONTENT, newContent);
            receivedIntent.putExtra(MainActivity.EXTRA_TITLE, newTitle);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(receivedIntent.getIntExtra(MainActivity.EXTRA_NOTIFICATION, 1), MainActivity.generatePin(
                    this,
                    receivedIntent.getIntExtra(MainActivity.EXTRA_VISIBILITY, 0), // get visibility from intent
                    receivedIntent.getIntExtra(MainActivity.EXTRA_PRIORITY, 0),
                    receivedIntent.getIntExtra(MainActivity.EXTRA_NOTIFICATION, 1),
                    newTitle,
                    newContent
            ));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonPin:
                updatePin();
                finish();
                break;

            case R.id.buttonCancel:
                finish();
                break;
        }
    }
}
