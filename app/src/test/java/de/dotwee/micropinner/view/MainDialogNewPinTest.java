package de.dotwee.micropinner.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.PreferencesHandler;

import static org.junit.Assert.assertEquals;

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
     * This method returns an instance of {@link PreferencesHandler}
     * for an activity test rule.
     *
     * @return An instance of {@link PreferencesHandler}
     */
    public PreferencesHandler getPreferencesHandler() {
        return PreferencesHandler.getInstance(mainDialogActivityController.get());
    }
}
