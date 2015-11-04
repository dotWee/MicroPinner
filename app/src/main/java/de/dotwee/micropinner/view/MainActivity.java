package de.dotwee.micropinner.view;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.dotwee.micropinner.R;
import de.dotwee.micropinner.presenter.MainPresenter;
import de.dotwee.micropinner.presenter.MainPresenterImpl;
import de.dotwee.micropinner.receiver.OnBootReceiver;
import de.dotwee.micropinner.tools.PreferencesHandler;

/**
 * Created by Lukas Wolfsteiner on 29.10.2015.
 */
public class MainActivity extends AppCompatActivity implements MainPresenter.Listeners, MainPresenter.Data, View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String LOG_TAG = "MainActivity";
    private MainPresenter mainPresenter;

    /**
     * This method checks if the user's device is a tablet, depending on the official resource {@link Configuration}.
     *
     * @param context needed to get resources
     * @return true if device screen size is greater than 6 inches
     */
    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesHandler preferencesHandler = PreferencesHandler.getInstance(this);
        if (preferencesHandler.isLightThemeEnabled()) {
            this.setTheme(R.style.DialogTheme_Light);
        }

        this.setContentView(R.layout.dialog_main);

        setVisibilityAdapter();
        setPriorityAdapter();

        this.mainPresenter = new MainPresenterImpl(this, getIntent());

        setOnClickListener(
                R.id.checkBoxPersistentPin,
                R.id.checkBoxNewPin,

                R.id.switchAdvanced,

                R.id.linearLayoutHeader,

                R.id.buttonCancel,
                R.id.buttonPin
        );

        setOnCheckedChangeListener(
                R.id.checkBoxNewPin,

                R.id.switchAdvanced
        );

        setOnLongClickListener(
                R.id.switchAdvanced,
                R.id.linearLayoutHeader
        );

        // simulate device-boot by sending a new intent to class OnBootReceiver
        sendBroadcast(new Intent(this, OnBootReceiver.class));
    }

    @Override
    public void setContentView(int layoutResID) {
        if (isTablet(this)) {

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int newWidth = Math.round(320 * (metrics.densityDpi / 160f));

            setContentView(
                    View.inflate(this, layoutResID, null),
                    new FrameLayout.LayoutParams(
                            newWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
            );

        } else super.setContentView(layoutResID);
    }

    private void setVisibilityAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerVisibility);
        if (spinner != null) {

            ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(
                    spinner.getContext(),
                    android.R.layout.simple_spinner_item,
                    this.getResources().getStringArray(R.array.array_visibilities)
            );

            visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(visibilityAdapter);

            spinner.setAdapter(visibilityAdapter);
        }
    }

    private void setPriorityAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPriority);
        if (spinner != null) {

            ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                    spinner.getContext(),
                    android.R.layout.simple_spinner_item,
                    this.getResources().getStringArray(R.array.array_priorities)
            );

            priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(priorityAdapter);

            spinner.setAdapter(priorityAdapter);
        }
    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

            case R.id.checkBoxNewPin:
                mainPresenter.onCheckBoxClick(isChecked);
                break;

            case R.id.switchAdvanced:
                mainPresenter.onViewExpand(isChecked);
                break;
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonPin:
                mainPresenter.onButtonPositive();
                break;

            case R.id.buttonCancel:
                mainPresenter.onButtonNegative();
                break;

            case R.id.linearLayoutHeader:
                View view = findViewById(R.id.switchAdvanced);
                if (view != null) {

                    view.performClick();
                }
                break;
        }
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {

            case R.id.switchAdvanced:
                mainPresenter.onSwitchHold();
                return true;

            case R.id.linearLayoutHeader:
                View view = findViewById(R.id.switchAdvanced);
                if (view != null) {
                    view.performLongClick();
                }
                return true;
        }


        return false;
    }

    /**
     * This method applies a click-listener to its given view-ids.
     *
     * @param ids The ids to set a click-listener on.
     */
    @Override
    public void setOnClickListener(@NonNull @IdRes int... ids) {
        for (int id : ids) {
            View view = findViewById(id);

            view.setOnClickListener(this);
        }
    }

    /**
     * This method applies a long-click-listener to its given view-ids.
     *
     */
    @Override
    public void setOnLongClickListener(@NonNull @IdRes int... ids) {
        for (int id : ids) {
            View view = findViewById(id);

            if (view != null) {
                view.setOnLongClickListener(this);
            }
        }
    }

    /**
     * This method applies a checked-change-listener to its given view-ids.
     *
     * @param ids The ids to set a checked-change-listener on.
     */
    @Override
    public void setOnCheckedChangeListener(@NonNull @IdRes int... ids) {
        for (int id : ids) {
            CompoundButton compoundButton = (CompoundButton) findViewById(id);

            compoundButton.setOnCheckedChangeListener(this);
        }
    }

    /**
     * This method reads the value of the visibility spinner widget.
     *
     * @return Value of the content visibility spinner widget.
     */
    @Override
    public int getVisibility() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerVisibility);
        if (spinner != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            switch (spinner.getSelectedItemPosition()) {
                case 0:
                    return Notification.VISIBILITY_PUBLIC;

                case 1:
                    return Notification.VISIBILITY_PRIVATE;

                case 2:
                    return Notification.VISIBILITY_SECRET;
            }
        }

        return 0;
    }

    /**
     * This method reads the value of the priority spinner widget.
     *
     * @return Value of the content priority spinner widget.
     */
    @Override
    public int getPriority() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPriority);
        if (spinner != null) {

            switch (spinner.getSelectedItemPosition()) {
                case 0:
                    return Notification.PRIORITY_DEFAULT;

                case 1:
                    return Notification.PRIORITY_MIN;

                case 2:
                    return Notification.PRIORITY_LOW;

                case 3:
                    return Notification.PRIORITY_HIGH;
            }
        }

        return 0;
    }

    /**
     * This method reads the value of the title editText widget.
     *
     * @return Value of the content title widget.
     */
    @Override
    public String getPinTitle() {
        EditText editText = (EditText) findViewById(R.id.editTextTitle);
        if (editText != null) {
            return editText.getText().toString();
        }

        return null;
    }

    /**
     * This method reads the value of the content editText widget.
     *
     * @return Value of the content editText widget.
     */
    @Override
    public String getPinContent() {
        EditText editText = (EditText) findViewById(R.id.editTextContent);
        if (editText != null) {
            return editText.getText().toString();
        }

        return null;
    }

    /**
     * This method reads the state of the persistent checkbox widget.
     *
     * @return State of the persistent checkbox.
     */
    @Override
    public boolean isPersistent() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxPersistentPin);
        return checkBox != null && checkBox.isChecked();

    }
}
