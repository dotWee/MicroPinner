package de.dotwee.micropinner.ui;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.tools.PreferencesHandler;

/**
 * Created by Lukas Wolfsteiner on 12.10.2015.
 */
public class MainView implements MainActivity.ViewWrapper {
    private static final String LOG_TAG = "MainView";
    private final Switch.OnCheckedChangeListener onCheckedChangeListener;
    private final View.OnClickListener onClickListener;
    private final View.OnLongClickListener onLongClickListener;
    private final PreferencesHandler preferencesHandler;
    private final Context activity;
    private final View view;
    public CheckBox checkBoxShowNewPin;
    public List<View> advancedViewList;
    public Button buttonCancel;
    public Switch switchAdvanced;
    private CheckBox checkBoxPersistentPin;
    private Spinner spinnerVisibility;
    private Spinner spinnerPriority;
    private EditText editTextContent;
    private EditText editTextTitle;
    private TextView dialogTitle;

    public MainView(Context context, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, Switch.OnCheckedChangeListener onCheckedChangeListener) {
        this.preferencesHandler = PreferencesHandler.getInstance(context);
        this.onCheckedChangeListener = onCheckedChangeListener;
        this.onLongClickListener = onLongClickListener;
        this.onClickListener = onClickListener;

        this.activity = context;
        this.view = ((MainActivity) activity).findViewById(android.R.id.content);
    }

    @Override
    public void onCreate() {
        onViewCreated();
        onViewRestore();

        adaptSpinner();
        adaptLists();
    }

    @Override
    public void onViewCreated() {
        // setup the dialog header and title
        dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        checkBoxShowNewPin = (CheckBox) view.findViewById(R.id.checkBoxNewPin);
        checkBoxPersistentPin = (CheckBox) view.findViewById(R.id.checkBoxPersistentPin);

        // edit texts for title and content
        editTextContent = (EditText) view.findViewById(R.id.editTextContent);
        editTextTitle = (EditText) view.findViewById(R.id.editTextTitle);

        // 'advanced' switch
        switchAdvanced = (Switch) view.findViewById(R.id.switchAdvanced);
        switchAdvanced.setOnCheckedChangeListener(onCheckedChangeListener);
        switchAdvanced.setOnLongClickListener(onLongClickListener);

        // spinner for priority and visibility
        spinnerPriority = (Spinner) view.findViewById(R.id.spinnerPriority);
        spinnerVisibility = (Spinner) view.findViewById(R.id.spinnerVisibility);
    }

    @Override
    public void onViewRestore() {
        // set the dialog title
        dialogTitle.setText(activity.getResources().getString(R.string.main_name));

        // set focus to the title input
        editTextTitle.performClick();

        switchAdvanced.setChecked(preferencesHandler.isAdvancedUsed());
        checkBoxShowNewPin.setChecked(preferencesHandler.isShowNewPinEnabled());
    }

    @Override
    public void onViewRestore(PinHandler.Pin pin) {

        // set the dialog title
        dialogTitle.setText(activity.getResources().getString(R.string.edit_name));

        editTextTitle.setText(pin.getTitle());
        editTextContent.setText(pin.getContent());
        checkBoxPersistentPin.setChecked(pin.isPersistent());
    }

    @Override
    public void adaptSpinner() {
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_item,
                activity.getResources().getStringArray(R.array.array_visibilities)
        );

        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVisibility.setAdapter(visibilityAdapter);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_item,
                activity.getResources().getStringArray(R.array.array_priorities)
        );

        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(priorityAdapter);
    }

    @Override
    public void adaptLists() {
        advancedViewList = new ArrayList<>();
        advancedViewList.add(checkBoxPersistentPin);
        advancedViewList.add(checkBoxShowNewPin);

        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(view.findViewById(R.id.buttonCancel));
        clickViewList.add(view.findViewById(R.id.buttonPin));
        clickViewList.add(switchAdvanced);
        clickViewList.addAll(advancedViewList);

        for (View view : clickViewList)
            view.setOnClickListener(onClickListener);
    }

    @Override
    public String getTitle() {
        return editTextTitle.getText().toString();
    }

    @Override
    public String getContent() {
        return editTextContent.getText().toString();
    }

    @Override
    public int getPriority() {
        String selected = spinnerPriority.getSelectedItem().toString();

        if (selected.equalsIgnoreCase(activity.getString(R.string.priority_low)))
            return Notification.PRIORITY_LOW;
        else if (selected.equalsIgnoreCase(activity.getString(R.string.priority_min)))
            return Notification.PRIORITY_MIN;
        else if (selected.equalsIgnoreCase(activity.getString(R.string.priority_high)))
            return Notification.PRIORITY_HIGH;
        else return Notification.PRIORITY_DEFAULT;
    }

    @Override
    public int getVisibility() {
        String selected = spinnerVisibility.getSelectedItem().toString();

        // check availability
        if (Build.VERSION.SDK_INT >= 21) {
            if (selected.equalsIgnoreCase(activity.getString(R.string.visibility_private)))
                return Notification.VISIBILITY_PRIVATE;

            else if (selected.equalsIgnoreCase(activity.getString(R.string.visibility_secret)))
                return Notification.VISIBILITY_SECRET;

            else return Notification.VISIBILITY_PUBLIC;
        } else return 0;
    }

    @Override
    public boolean isReady() {

        // return true if everything is okay
        if (editTextTitle != null)
            if (!editTextTitle.getText().toString().isEmpty())
                return true;

        // if not ready, show a message and return false
        Toast.makeText(activity, activity.getText(R.string.message_empty_title), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean isPersistent() {
        return checkBoxPersistentPin.isChecked();
    }
}
