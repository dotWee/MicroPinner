package de.dotwee.micropinner.tools;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dotwee.micropinner.view.MainDialog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by lukas on 11.08.2016.
 */
@RunWith(AndroidJUnit4.class)
public class PreferencesHandlerTest {

    /**
     * Preferred JUnit 4 mechanism of specifying the
     * activity to be launched before each test
     */
    @Rule
    public ActivityTestRule<MainDialog> activityTestRule =
            new ActivityTestRule<>(MainDialog.class);

    private PreferencesHandler preferencesHandler;

    @Before
    public void setUp() {

        // Removes all previous preferences entries
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activityTestRule.getActivity());
        sharedPreferences.edit().clear().apply();

        preferencesHandler = PreferencesHandler.getInstance(activityTestRule.getActivity());
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