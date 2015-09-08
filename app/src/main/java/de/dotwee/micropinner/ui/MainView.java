package de.dotwee.micropinner.ui;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.PreferencesHandler;

/**
 * Created by Lukas Wolfsteiner on 08.09.2015
 */
public class MainView {
    public CheckBox checkBoxShowNewPin, checkBoxPersistentPin, checkBoxEnableRestore;
    public Spinner spinnerVisibility, spinnerPriority;
    public EditText editTextContent, editTextTitle;

    public List<View> advancedViewList, clickViewList;
    public Switch switchAdvanced;
    public TextView dialogTitle;

    private Switch.OnCheckedChangeListener onCheckedChangeListener;
    private View.OnClickListener onClickListener;

    private PreferencesHandler preferencesHandler;
    private Context activity;
    private View view;

    public MainView(Context context, View.OnClickListener onClickListener, Switch.OnCheckedChangeListener onCheckedChangeListener) {
        this.preferencesHandler = PreferencesHandler.getInstance(context);
        this.onCheckedChangeListener = onCheckedChangeListener;
        this.onClickListener = onClickListener;

        this.activity = context;
        this.view = ((MainActivity) activity).findViewById(android.R.id.content);
    }

    /**
     * Main function to coordinate sub-functions.
     */
    public void init() {
        this.find();

        adaptSpinner();
        adaptLists();
        restore();
    }

    /**
     * Setup aaaall the view object by finding them on the context's view.
     */
    private void find() {

        // setup the dialog header and title
        dialogTitle = (TextView) view.findViewById(R.id.dialogTitle);

        checkBoxShowNewPin = (CheckBox) view.findViewById(R.id.checkBoxNewPin);

        checkBoxEnableRestore = (CheckBox) view.findViewById(R.id.checkBoxEnableRestore);
        checkBoxPersistentPin = (CheckBox) view.findViewById(R.id.checkBoxPersistentPin);

        // edit texts for title and content
        editTextContent = (EditText) view.findViewById(R.id.editTextContent);
        editTextTitle = (EditText) view.findViewById(R.id.editTextTitle);

        // 'advanced' switch
        switchAdvanced = (Switch) view.findViewById(R.id.switchAdvanced);
        switchAdvanced.setOnCheckedChangeListener(onCheckedChangeListener);

        // spinner for priority and visibility
        spinnerPriority = (Spinner) view.findViewById(R.id.spinnerPriority);
        spinnerVisibility = (Spinner) view.findViewById(R.id.spinnerVisibility);
    }

    /**
     * Init the spinner with adapters.
     */
    private void adaptSpinner() {
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

    /**
     * Init the view lists advancedViewList and clickViewList.
     * Used to simplify view.setOnClickListener() and the advanced view switch.
     */
    private void adaptLists() {
        advancedViewList = new ArrayList<>();
        advancedViewList.add(checkBoxPersistentPin);
        advancedViewList.add(checkBoxEnableRestore);
        advancedViewList.add(checkBoxShowNewPin);

        clickViewList = new ArrayList<>();
        clickViewList.add(view.findViewById(R.id.buttonCancel));
        clickViewList.add(view.findViewById(R.id.buttonPin));
        clickViewList.add(switchAdvanced);
        clickViewList.addAll(advancedViewList);

        for (View view : clickViewList)
            view.setOnClickListener(onClickListener);
    }

    /**
     * Restore checkBoxes and the main view to their last state and set the view-focus to editTextTitle.
     */
    private void restore() {

        // set the dialog title
        dialogTitle.setText(activity.getResources().getString(R.string.main_name));

        // set focus to the title input
        editTextTitle.performClick();

        switchAdvanced.setChecked(preferencesHandler.isAdvancedUsed());
        checkBoxEnableRestore.setChecked(preferencesHandler.isRestoreEnabled());
        checkBoxShowNewPin.setChecked(preferencesHandler.isShowNewPinEnabled());
    }

    /**
     * Getter for pin's title.
     *
     * @return the content of editTextTitle.
     */
    public String getTitle() {
        return editTextTitle.getText().toString();
    }

    /**
     * Getter for pin's content.
     *
     * @return the content of editTextContent.
     */
    public String getContent() {
        return editTextContent.getText().toString();
    }

    /**
     * @return the selected priority.
     */
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

    /**
     * Checks the availability of the visibility-api.
     *
     * @return the selected visibility or zero if it's not supported.
     */
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

    /**
     * @return the state of the persistent-checkbox.
     */
    public boolean isPersistent() {
        return checkBoxPersistentPin.isChecked();
    }

    /**
     * Check if the title is not null and not empty.
     *
     * @return true if state is acceptable, show a toast if not.
     */
    public boolean isReady() {

        // return true if everything is okay
        if (editTextTitle != null)
            if (!editTextTitle.getText().toString().isEmpty())
                return true;

        // if not ready, show a message and return false
        Toast.makeText(activity, activity.getText(R.string.message_empty_title), Toast.LENGTH_SHORT).show();
        return false;
    }
}
