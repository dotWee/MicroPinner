package de.dotwee.micropinner.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import de.dotwee.micropinner.R;

/**
 * Created by lukas on 25.07.2016.
 */
public class DialogHeaderView extends AbstractDialogView
        implements Switch.OnCheckedChangeListener, View.OnClickListener, View.OnLongClickListener {

    static final String TAG = "DialogHeaderView";
    private Switch switchAdvanced;

    public DialogHeaderView(Context context) {
        super(context);
    }

    public DialogHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        inflate(getContext(), R.layout.dialog_main_head, this);

        LinearLayout linearLayoutHeader = (LinearLayout) findViewById(R.id.linearLayoutHeader);
        linearLayoutHeader.setOnLongClickListener(this);
        linearLayoutHeader.setOnClickListener(this);

        switchAdvanced = (Switch) findViewById(R.id.switchAdvanced);
        switchAdvanced.setOnCheckedChangeListener(this);
        switchAdvanced.setOnLongClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (mainPresenter == null) {
            throw new IllegalStateException("Passed MainPresenter instance is null");
        }

        switch (compoundButton.getId()) {

            case R.id.switchAdvanced:
                mainPresenter.onViewExpand(isChecked);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.linearLayoutHeader:
                switchAdvanced.performClick();
                break;
        }
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param view The view that was clicked and held.
     * @return true if the callback consumed the long click, false otherwise.
     */
    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {

            case R.id.switchAdvanced:
                mainPresenter.onSwitchHold();
                return true;

            case R.id.linearLayoutHeader:
                mainPresenter.onSwitchHold();
                return true;
        }
        return false;
    }
}
