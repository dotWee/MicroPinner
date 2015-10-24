package de.dotwee.micropinner.ui;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.ListTools;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.tools.PreferencesHandler;
import de.dotwee.micropinner.tools.SpinnerTools;

/**
 * Created by Lukas Wolfsteiner on 12.10.2015.
 */
public class MainView implements MainActivity.ViewWrapper {
    private static final String LOG_TAG = "MainView";
    public final int[] advancedViewIds, clickViewIds;
    private final Switch.OnCheckedChangeListener onCheckedChangeListener;
    private final View.OnClickListener onClickListener;
    private final View.OnLongClickListener onLongClickListener;
    private final PreferencesHandler preferencesHandler;
    private final Activity activity;

    public Button buttonCancel;
    public Switch switchAdvanced;
    public CheckBox checkBoxShowNewPin;
    private CheckBox checkBoxPersistentPin;
    private Spinner spinnerVisibility;
    private Spinner spinnerPriority;
    private EditText editTextContent;
    private EditText editTextTitle;
    private TextView dialogTitle;

    public MainView(MainActivity activity) {
        this.preferencesHandler = PreferencesHandler.getInstance(activity);

        this.advancedViewIds = ListTools.getAdvancedViewIds();
        this.clickViewIds = ListTools.getClickableViewIds();

        this.onCheckedChangeListener = activity;
        this.onLongClickListener = activity;
        this.onClickListener = activity;
        this.activity = activity;
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
        dialogTitle = (TextView) findView(R.id.dialogTitle);
        buttonCancel = (Button) findView(R.id.buttonCancel);

        checkBoxShowNewPin = (CheckBox) findView(R.id.checkBoxNewPin);
        checkBoxPersistentPin = (CheckBox) findView(R.id.checkBoxPersistentPin);

        // edit texts for title and content
        editTextContent = (EditText) findView(R.id.editTextContent);
        editTextTitle = (EditText) findView(R.id.editTextTitle);

        // 'advanced' switch
        switchAdvanced = (Switch) findView(R.id.switchAdvanced);
        switchAdvanced.setOnCheckedChangeListener(onCheckedChangeListener);
        switchAdvanced.setOnLongClickListener(onLongClickListener);

        // spinner for priority and visibility
        spinnerPriority = (Spinner) findView(R.id.spinnerPriority);
        spinnerVisibility = (Spinner) findView(R.id.spinnerVisibility);
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

        spinnerPriority.setSelection(
                SpinnerTools.getPriorityPosition(pin.getPriority()),
                true // true for animation
        );


        spinnerVisibility.setSelection(
                SpinnerTools.getVisibilityPosition(pin.getVisibility()),
                true // true for animation
        );

        checkBoxPersistentPin.setChecked(pin.isPersistent());
    }

    @Override
    public void onViewExpand(boolean expand) {
        for (int viewId : advancedViewIds)
            findView(viewId).setVisibility(expand ? View.VISIBLE : View.GONE);
    }

    @Override
    public View findView(int id) {
        return activity.findViewById(android.R.id.content).findViewById(id);
    }

    @Override
    public void adaptSpinner() {
        SpinnerTools.setVisibilityAdapter(activity.getResources(), spinnerVisibility);

        SpinnerTools.setPriorityAdapter(activity.getResources(), spinnerPriority);
    }

    @Override
    public void adaptLists() {
        for (int viewId : clickViewIds)
            findView(viewId).setOnClickListener(onClickListener);
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
        return SpinnerTools.getPriorityResource(spinnerPriority.getSelectedItemPosition());
    }

    @Override
    public int getVisibility() {
        return SpinnerTools.getVisibilityResource(spinnerVisibility.getSelectedItemPosition());
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
