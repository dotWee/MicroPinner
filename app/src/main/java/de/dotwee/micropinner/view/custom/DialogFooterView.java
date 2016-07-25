package de.dotwee.micropinner.view.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.dotwee.micropinner.R;

/**
 * Created by lukas on 25.07.2016.
 */
public class DialogFooterView extends AbstractDialogView implements View.OnClickListener {
    private static final String TAG = "DialogFooterView";
    private Button buttonPin;
    private Button buttonCancel;

    public DialogFooterView(Context context) {
        super(context);
    }

    public DialogFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        inflate(getContext(), R.layout.dialog_main_footer, this);

        buttonPin = (Button) this.findViewById(R.id.buttonPin);
        buttonPin.setOnClickListener(this);

        buttonCancel = (Button) this.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.buttonPin:
                mainPresenter.onButtonPositive();
                break;

            case R.id.buttonCancel:
                mainPresenter.onButtonNegative();
                break;

            default:
                Log.w(TAG, "Registered click on unknown view");
                break;
        }
    }
}
