package de.dotwee.micropinner.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.receiver.OnBootReceiver;
import de.dotwee.micropinner.receiver.OnDeleteReceiver;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.tools.PreferencesHandler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Switch.OnCheckedChangeListener {
    private final static String LOG_TAG = "MainActivity";

    NotificationManager notificationManager;
    PreferencesHandler preferencesHandler;
    PinHandler.Pin parentPin;
    boolean hasParentPin;
    MainView mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main);

        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        preferencesHandler = PreferencesHandler.getInstance(this);
        mainView = new MainView(this, this, this);
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

            case R.id.checkBoxEnableRestore:
                preferencesHandler.setRestoreEnabled(mainView.checkBoxEnableRestore.isChecked());
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
}
