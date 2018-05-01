package de.dotwee.micropinner.tools;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import de.dotwee.micropinner.view.MainDialog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by lukas on 11.08.2016.
 */
@RunWith(RobolectricTestRunner.class)
public class PreferencesHandlerTest {
    private PreferencesHandler preferencesHandler;
    ActivityController<MainDialog> mainDialogActivityController;

    @Before
    public void setUp() {
        mainDialogActivityController = Robolectric.buildActivity(MainDialog.class).create().start();

        // Removes all previous preferences entries
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainDialogActivityController.get());
        sharedPreferences.edit().clear().apply();

        preferencesHandler = PreferencesHandler.getInstance(mainDialogActivityController.get());
    }

    @Test
    public void testIsFirstUse() throws Exception {

        // should be enabled since preferences has been cleared
        assertTrue(preferencesHandler.isFirstUse());
    }

    @Test
    public void testIsAdvancedUsed() throws Exception {

        // should be disabled by default
        assertFalse(preferencesHandler.isAdvancedUsed());

        // tell preference handler to enable the advanced layout
        preferencesHandler.setAdvancedUse(true);

        // should be enabled now
        assertTrue(preferencesHandler.isAdvancedUsed());
    }

    @Test
    public void testIsNotificationActionsEnabled() throws Exception {

        // should be disabled by default
        assertFalse(preferencesHandler.isNotificationActionsEnabled());

        // tell preference handler to enable the advanced layout
        preferencesHandler.setNotificationActionsEnabled(true);

        // should be enabled now
        assertTrue(preferencesHandler.isNotificationActionsEnabled());
    }
}