package de.dotwee.micropinner.view.custom;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.dotwee.micropinner.BuildConfig;
import de.dotwee.micropinner.R;

/**
 * Created by lukas on 25.07.2016.
 */
public class DialogFooterView extends AbstractDialogView implements View.OnClickListener {
    private static final String TAG = DialogFooterView.class.getSimpleName();

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
        super.init();

        inflate(getContext(), R.layout.dialog_main_footer, this);

        Button buttonPin = this.findViewById(R.id.buttonPin);
        buttonPin.setOnClickListener(this);

        Button buttonCancel = this.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(@NonNull View view) {
        checkIfPresenterNull();

        int id = view.getId();
        if (id == R.id.buttonPin) {
            mainPresenter.onButtonPositive();
        } else if (id == R.id.buttonCancel) {
            mainPresenter.onButtonNegative();
        } else {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Registered click on unknown view");
            }
        }
    }
}
