package de.dotwee.micropinner.view;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.Matches;
import de.dotwee.micropinner.tools.PreferencesHandler;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dotwee.micropinner.tools.TestTools.getPreferencesHandler;
import static de.dotwee.micropinner.tools.TestTools.recreateActivity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Lukas Wolfsteiner on 06.11.2015.
 */
@RunWith(AndroidJUnit4.class)
public class MainDialogThemeTest {
    private static final String LOG_TAG = "MainDialogThemeTest";

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
        getPreferencesHandler(activityTestRule).setLightThemeEnabled(false);
        recreateActivity(activityTestRule);
    }

    /**
     * This method verifies the theme-change mechanism through
     * multiple long-clicks on the header.
     */
    @Test
    public void testThemeChangeThroughHeader() throws Exception {
        boolean lightThemeEnabled = getPreferencesHandler(activityTestRule).isLightThemeEnabled();
        onView(withId(R.id.linearLayoutHeader)).perform(longClick());

        // boolean should be inverted now
        assertEquals(!lightThemeEnabled, getPreferencesHandler(activityTestRule).isLightThemeEnabled());
    }

    /**
     * This method verifies the theme-change mechanism through
     * multiple long-clicks on the advanced-switch.
     */
    @Test
    public void testThemeChangeThroughSwitch() throws Exception {
        boolean lightThemeEnabled = getPreferencesHandler(activityTestRule).isLightThemeEnabled();
        onView(withId(R.id.switchAdvanced)).perform(longClick());

        // boolean should be inverted now
        assertEquals(!lightThemeEnabled, getPreferencesHandler(activityTestRule).isLightThemeEnabled());
    }

    /**
     * This method verifies the light theme's accent.
     */
    @Test
    public void testThemeLightAccent() throws Exception {
        preferencesHandler = getPreferencesHandler(activityTestRule);

        preferencesHandler.setLightThemeEnabled(true);
        assertTrue(preferencesHandler.isLightThemeEnabled());

        // recreate activity to apply theme
        recreateActivity(activityTestRule);

        int accentColor = ContextCompat.getColor(activityTestRule.getActivity(), R.color.accent);

        // check color for all TextView descriptions
        for (int description : new int[]{
                R.string.input_description_title, R.string.input_description_content,
                R.string.input_description_priority, R.string.input_description_visibility
        }) {
            onView(withText(description)).check(matches(Matches.withTextColor(accentColor)));
        }
    }

    /**
     * This method verifies the light theme's background.
     */
    @Test
    public void testThemeLightBackground() throws Exception {
        preferencesHandler = getPreferencesHandler(activityTestRule);

        preferencesHandler.setLightThemeEnabled(true);
        assertTrue(preferencesHandler.isLightThemeEnabled());

        // recreate activity to apply theme
        recreateActivity(activityTestRule);

        int backgroundColor = ContextCompat.getColor(activityTestRule.getActivity(), R.color.background);

        onView(withId(android.R.id.content)).check(matches(Matches.withBackgroundColor(backgroundColor)));
    }

    /**
     * This method verifies the light theme's accent.
     */
    @Test
    public void testThemeDarkAccent() throws Exception {
        preferencesHandler = getPreferencesHandler(activityTestRule);

        preferencesHandler.setLightThemeEnabled(false);
        assertFalse(preferencesHandler.isLightThemeEnabled());

        // recreate activity to apply theme
        recreateActivity(activityTestRule);

        // check color for all TextView descriptions
        for (int description : new int[]{
                R.string.input_description_title, R.string.input_description_content,
                R.string.input_description_priority, R.string.input_description_visibility
        }) {
            onView(withText(description)).check(matches(Matches.withTextColor(
                    ContextCompat.getColor(activityTestRule.getActivity(), R.color.accent_dark)
            )));
        }
    }

    /**
     * This method verifies the dark theme's background.
     */
    @Test
    public void testThemeDarkBackground() throws Exception {
        preferencesHandler = getPreferencesHandler(activityTestRule);

        preferencesHandler.setLightThemeEnabled(false);
        assertFalse(preferencesHandler.isLightThemeEnabled());

        // recreate activity to apply theme
        recreateActivity(activityTestRule);

        onView(withId(android.R.id.content)).check(matches(Matches.withBackgroundColor(
                ContextCompat.getColor(activityTestRule.getActivity(), R.color.background_dark))));
    }
}
