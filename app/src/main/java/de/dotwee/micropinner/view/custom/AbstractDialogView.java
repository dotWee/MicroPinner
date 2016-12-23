package de.dotwee.micropinner.view.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import de.dotwee.micropinner.presenter.MainPresenter;

/**
 * Created by lukas on 25.07.2016.
 */
public abstract class AbstractDialogView extends FrameLayout {
    static final String TAG = AbstractDialogView.class.getSimpleName();
    MainPresenter mainPresenter = null;

    public AbstractDialogView(@NonNull Context context) {
        super(context);
        init();
    }

    public AbstractDialogView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AbstractDialogView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setMainPresenter(@NonNull MainPresenter mainPresenter) {
        this.mainPresenter = mainPresenter;
    }

    protected abstract void init();

    final void checkIfPresenterNull() {
        if (mainPresenter == null) {
            throw new IllegalStateException("MainPresenter must be not null");
        }
    }
}
