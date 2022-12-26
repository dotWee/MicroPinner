package de.dotwee.micropinner.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import de.dotwee.micropinner.R;

/**
 * Created by lukas on 25.07.2016.
 */
public class DialogContentView extends AbstractDialogView
        implements CheckBox.OnCheckedChangeListener {
    static final String TAG = DialogContentView.class.getSimpleName();
    private Spinner spinnerVisibility;
    private Spinner spinnerPriority;

    public DialogContentView(Context context) {
        super(context);
    }

    public DialogContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        super.init();

        inflate(getContext(), R.layout.dialog_main_content, this);

        spinnerVisibility = findViewById(R.id.spinnerVisibility);
        setVisibilityAdapter();

        spinnerPriority = findViewById(R.id.spinnerPriority);
        setPriorityAdapter();
    }

    private void setVisibilityAdapter() {
        if (spinnerVisibility != null) {

            ArrayAdapter<String> visibilityAdapter =
                    new ArrayAdapter<>(spinnerVisibility.getContext(), android.R.layout.simple_spinner_item,
                            this.getResources().getStringArray(R.array.array_visibilities));

            visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerVisibility.setAdapter(visibilityAdapter);
        }
    }

    private void setPriorityAdapter() {
        if (spinnerPriority != null) {

            ArrayAdapter<String> priorityAdapter =
                    new ArrayAdapter<>(spinnerPriority.getContext(), android.R.layout.simple_spinner_item,
                            this.getResources().getStringArray(R.array.array_priorities));

            priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPriority.setAdapter(priorityAdapter);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        checkIfPresenterNull();

        if (compoundButton.getId() == R.id.checkBoxShowActions) {
            mainPresenter.onShowActions();
        }
    }
}
