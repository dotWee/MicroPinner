package de.dotwee.micropinner.view;


import android.app.Notification;
import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.database.PinSpec;
import de.dotwee.micropinner.tools.NotificationTools;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class MainDialogParentPinTest {
    PinSpec testPin = new PinSpec(getClass().getCanonicalName(), getClass().getName(), Notification.VISIBILITY_PRIVATE, Notification.PRIORITY_HIGH, true, true);
    ActivityController<MainDialog> mainDialogActivityController;
    MainDialog mainDialog;

    @Before
    public void setUp() {
        final Intent testIntent =
                new Intent().putExtra(NotificationTools.EXTRA_INTENT, testPin);

        mainDialogActivityController = Robolectric.buildActivity(MainDialog.class, testIntent).create();
        mainDialog = mainDialogActivityController.get();
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDialogTitle() throws Exception {

        TextView textViewDialogTitle = mainDialog.findViewById(R.id.dialogTitle);
        String actual = textViewDialogTitle.getText().toString();
        String expected = mainDialog.getString(R.string.edit_name);

        // verify changed dialog title
        assertEquals(expected, actual);
    }

    @Test
    public void testDialogButtons() throws Exception {

        Button buttonCancel = mainDialog.findViewById(R.id.buttonCancel);
        String actual = buttonCancel.getText().toString();
        String expected = mainDialog.getString(R.string.dialog_action_delete);

        // verify changed buttons
        assertEquals(expected, actual);
    }

    @Test
    public void testPinTitle() throws Exception {

        EditText editTextTitle = mainDialog.findViewById(R.id.editTextTitle);
        String actual = editTextTitle.getText().toString();
        String expected = testPin.getTitle();

        // verify pin title
        assertEquals(expected, actual);
    }

    @Test
    public void testPinContent() throws Exception {

        EditText editTextContent = mainDialog.findViewById(R.id.editTextContent);
        String actual = editTextContent.getText().toString();
        String expected = testPin.getContent();

        // verify pin content
        assertEquals(expected, actual);
    }

    /**
     * This method verifies the pin's priority.
     *
     * @throws Exception
     */
    @Test
    public void testPinPriority() throws Exception {
        int actual = mainDialog.getPriority();
        int expected = Notification.PRIORITY_HIGH;

        // verify selected priority
        assertEquals(expected, actual);
    }

    /**
     * This method verifies the pin's visibility.
     *
     * @throws Exception
     */
    @Test
    public void testPinVisibility() throws Exception {
        int actual = mainDialog.getVisibility();
        int expected = Notification.VISIBILITY_PRIVATE;

        // verify selected visibility
        assertEquals(expected, actual);
    }

    /**
     * This method verifies the pin's persistence.
     *
     * @throws Exception
     */
    @Test
    public void testPinPersistence() throws Exception {
        CheckBox checkBoxPersistence = mainDialog.findViewById(R.id.checkBoxPersistentPin);
        boolean actual = checkBoxPersistence.isChecked();
        boolean expected = testPin.isPersistent();

        assertEquals(expected, actual);
    }
}
