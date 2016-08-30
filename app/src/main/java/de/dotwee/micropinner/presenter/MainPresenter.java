package de.dotwee.micropinner.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;

import de.dotwee.micropinner.database.PinSpec;

/**
 * Created by Lukas Wolfsteiner on 29.10.2015.
 */
public interface MainPresenter {
    String TAG = MainPresenter.class.getSimpleName();

    /**
     * This method handles a long-click on a switch
     */
    void onSwitchHold();

    /**
     * This method handles the click on the positive dialog button.
     */
    void onButtonPositive();

    /**
     * This method handles the click on the negative dialog button.
     */
    void onButtonNegative();

    /**
     * This method restores CheckBoxes and Switches to their saved state.
     */
    void restore();

    /**
     * This method handles the click on the show-actions checkbox.
     */
    void onShowActions();

    /**
     * This method handles the expand action.
     *
     * @param expand If view should expand or not.
     */
    void onViewExpand(boolean expand);

    /**
     * This method checks if a parent pin exists.
     */
    boolean hasParentPin();

    /**
     * This method creates a {@link PinSpec} from the view.
     *
     * @return New instance of a {@link PinSpec}.
     * @throws Exception if pin is null or an error appeared on creation
     */
    @NonNull
    PinSpec toPin() throws Exception;

    /**
     * This method returns the corresponding view of the presenter.
     *
     * @return A non null {@link Activity} activity.
     */
    @NonNull
    Activity getView();

    /**
     * This method notifies all views about the parent pin.
     */
    void notifyAboutParentPin();

    /**
     * This method handles the visibility of an instance
     * of a {@link PinSpec}
     *
     * @param pin the instance to handle
     */
    void handleParentVisibility(@NonNull PinSpec pin);

    /**
     * This method handles the visibility of an instance
     * of a {@link PinSpec}
     *
     * @param pin the instance to handle
     */
    void handleParentPriority(@NonNull PinSpec pin);

    /**
     * This method handles the visibility of an instance
     * of a {@link PinSpec}
     *
     * @param pin the instance to handle
     */
    void handleParentTitle(@NonNull PinSpec pin);

    /**
     * This method handles the visibility of an instance
     * of a {@link PinSpec}
     *
     * @param pin the instance to handle
     */
    void handleParentContent(@NonNull PinSpec pin);

    /**
     * Created by Lukas Wolfsteiner on 29.10.2015.
     */
    interface Data {
        String LOG_TAG = "Data";

        /**
         * This method reads the value of the visibility spinner widget.
         *
         * @return Value of the content visibility spinner widget.
         */
        int getVisibility();

        /**
         * This method reads the value of the priority spinner widget.
         *
         * @return Value of the content priority spinner widget.
         */
        int getPriority();

        /**
         * This method reads the value of the title editText widget.
         *
         * @return Value of the content title widget.
         */
        String getPinTitle();

        /**
         * This method reads the value of the content editText widget.
         *
         * @return Value of the content editText widget.
         */
        String getPinContent();

        /**
         * This method reads the state of the persistent checkbox widget.
         *
         * @return State of the persistent checkbox.
         */
        boolean isPersistent();

        /**
         * This method reads the state of the show-actions checkbox widget.
         *
         * @return State of the show-actions checkbox.
         */
        boolean showActions();
    }
}