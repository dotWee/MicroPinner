package de.dotwee.micropinner.view;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.database.PinDatabase;
import de.dotwee.micropinner.tools.PreferencesHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class MainDialogNewPinTest {
    ActivityController<MainDialog> mainDialogActivityController;

    @Before
    public void setUp() {
        mainDialogActivityController = Robolectric.buildActivity(MainDialog.class).create().start();
    }

    /**
     * This method verifies the advanced-switch's functionality.
     *
     * @throws Exception
     */
    @Test
    public void testAdvancedSwitch() throws Exception {
        boolean currentState = getPreferencesHandler().isAdvancedUsed();
        MainDialog mainDialog = mainDialogActivityController.get();

        mainDialog.findViewById(R.id.switchAdvanced).performClick();
        assertEquals(!currentState, getPreferencesHandler().isAdvancedUsed());

        mainDialog.findViewById(R.id.switchAdvanced).performClick();
        assertEquals(currentState, getPreferencesHandler().isAdvancedUsed());
    }

    /**
     * This method looks for an EditText with id = R.id.editTextTitle,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextTitle() throws Exception {
        final String value = "MicroPinner title input";
        MainDialog mainDialog = mainDialogActivityController.get();

        EditText editText = mainDialog.findViewById(R.id.editTextTitle);
        editText.setText(value, TextView.BufferType.EDITABLE);

        assertEquals(value, editText.getText().toString());
    }

    /**
     * This method recreates the main activity and verifies the focus,
     * which should be on the title EditText.
     */
    @Test
    public void testEditTextFocus() throws Exception {
        MainDialog mainDialog = mainDialogActivityController.get();

        EditText editText = mainDialog.findViewById(R.id.editTextTitle);
        assertEquals(View.FOCUSABLE, editText.getFocusable());
    }

    /**
     * This method looks for an EditText with id = R.id.editTextContent,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextContent() throws Exception {
        final String value = "MicroPinner title input";
        MainDialog mainDialog = mainDialogActivityController.get();

        EditText editText = mainDialog.findViewById(R.id.editTextContent);
        editText.setText(value, TextView.BufferType.EDITABLE);

        assertEquals(value, editText.getText().toString());
    }

    /**
     * This method clicks the advanced-switch button, which should
     * hide CheckBoxes and verifies their (invisible) state.
     */
    @Test
    public void testExpandMechanismThroughSwitch() throws Exception {
        MainDialog mainDialog = mainDialogActivityController.get();

        CheckBox checkBoxPersistentPin = mainDialog.findViewById(R.id.checkBoxPersistentPin);
        CheckBox checkBoxShowActions = mainDialog.findViewById(R.id.checkBoxShowActions);

        Switch switchAdvanced = mainDialog.findViewById(R.id.switchAdvanced);
        assertEquals(getPreferencesHandler().isAdvancedUsed() ? View.VISIBLE : View.GONE, checkBoxPersistentPin.getVisibility());
        assertEquals(getPreferencesHandler().isAdvancedUsed() ? View.VISIBLE : View.GONE, checkBoxShowActions.getVisibility());

        // change state
        switchAdvanced.performClick();

        assertEquals(getPreferencesHandler().isAdvancedUsed() ? View.VISIBLE : View.GONE, checkBoxPersistentPin.getVisibility());
        assertEquals(getPreferencesHandler().isAdvancedUsed() ? View.VISIBLE : View.GONE, checkBoxShowActions.getVisibility());
    }

    /**
     * This method clicks the header layout, which should
     * hide CheckBoxes and verifies their (invisible) state.
     */
    @Test
    public void testExpandMechanismThroughHeader() throws Exception {
        MainDialog mainDialog = mainDialogActivityController.get();

        CheckBox checkBoxPersistentPin = mainDialog.findViewById(R.id.checkBoxPersistentPin);
        CheckBox checkBoxShowActions = mainDialog.findViewById(R.id.checkBoxShowActions);
        LinearLayout linearLayout = mainDialog.findViewById(R.id.linearLayoutHeader);

        assertEquals(getPreferencesHandler().isAdvancedUsed() ? View.VISIBLE : View.GONE, checkBoxPersistentPin.getVisibility());
        assertEquals(getPreferencesHandler().isAdvancedUsed() ? View.VISIBLE : View.GONE, checkBoxShowActions.getVisibility());

        // change state
        linearLayout.performClick();

        assertEquals(getPreferencesHandler().isAdvancedUsed() ? View.VISIBLE : View.GONE, checkBoxPersistentPin.getVisibility());
        assertEquals(getPreferencesHandler().isAdvancedUsed() ? View.VISIBLE : View.GONE, checkBoxShowActions.getVisibility());
    }

    /**
     * This method verifies the preference mechanism for the advanced usage.
     */
    @Test
    public void testPreferencesAdvanced() throws Exception {
        PreferencesHandler preferencesHandler = getPreferencesHandler();

        preferencesHandler.setAdvancedUse(true);
        assertTrue(preferencesHandler.isAdvancedUsed());
    }

    /**
     * This method verifies the persist mechanism for user-created pins.
     */
    @Test
    public void testUserCreateNewPin() throws Exception {
        MainDialog mainDialog = mainDialogActivityController.get();

        PinDatabase pinDatabase = PinDatabase.getInstance(mainDialog.getApplicationContext());
        pinDatabase.deleteAll();

        long previousPinAmount = pinDatabase.count();

        // enter a title
        EditText editTextTitle = mainDialog.findViewById(R.id.editTextTitle);
        editTextTitle.setText(getClass().getCanonicalName(), TextView.BufferType.EDITABLE);

        // expand layout
        Switch switchAdvanced = mainDialog.findViewById(R.id.switchAdvanced);
        if (!getPreferencesHandler().isAdvancedUsed()) {
            switchAdvanced.performClick();
        }
        assertTrue(switchAdvanced.isChecked());

        // mark as persistent
        CheckBox checkBoxPersistentPin = mainDialog.findViewById(R.id.checkBoxPersistentPin);
        if (!mainDialog.isPersistent()) {
            checkBoxPersistentPin.performClick();
        }
        assertTrue(checkBoxPersistentPin.isChecked());

        // select persist button
        Button button = mainDialog.findViewById(R.id.buttonPin);
        button.performClick();

        // make sure pin exists
        long newPinAmount = PinDatabase.getInstance(mainDialog.getApplicationContext()).count();
        assertEquals(previousPinAmount + 1, newPinAmount);
    }

    /**
     * This method returns an instance of {@link PreferencesHandler}
     * for an activity test rule.
     *
     * @return An instance of {@link PreferencesHandler}
     */
    public PreferencesHandler getPreferencesHandler() {
        return PreferencesHandler.getInstance(mainDialogActivityController.get());
    }
}
