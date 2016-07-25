package de.dotwee.micropinner.view;

import android.app.Notification;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Switch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.tools.PreferencesHandler;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dotwee.micropinner.tools.TestTools.getPreferencesHandler;
import static de.dotwee.micropinner.tools.TestTools.recreateActivity;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityNewPinTest {
    private static final String LOG_TAG = "MainActivityNewPinTest";
    /**
     * Preferred JUnit 4 mechanism of specifying the
     * activity to be launched before each test
     */
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
    private PreferencesHandler preferencesHandler;

    /**
     * This method verifies the advanced-switch's functionality.
     *
     * @throws Exception
     */
    @Test
    public void testAdvancedSwitch() throws Exception {
        boolean currentState = getPreferencesHandler(activityTestRule).isAdvancedUsed();
        onView(withId(R.id.switchAdvanced))
                .perform(click());
        assertEquals(! currentState, getPreferencesHandler(activityTestRule).isAdvancedUsed());

        onView(withId(R.id.switchAdvanced))
                .perform(click());
        assertEquals(currentState, getPreferencesHandler(activityTestRule).isAdvancedUsed());
    }

    /**
     * This method looks for an EditText with id = R.id.editTextTitle,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextTitle() throws Exception {
        final String value = "MicroPinner title input";

        onView(withId(R.id.editTextTitle))
                .perform(typeText(value))
                .check(matches(withText(value)));
    }

    /**
     * This method recreates the main activity and verifies the focus,
     * which should be on the title EditText.
     */
    @Test
    public void testEditTextFocus() throws Exception {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().recreate();
            }
        });

        onView(withId(R.id.editTextTitle))
                .check(matches(isFocusable()));
    }

    /**
     * This method looks for an EditText with id = R.id.editTextContent,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextContent() throws Exception {
        final String value = "MicroPinner title input";

        onView(withId(R.id.editTextContent))
                .perform(typeText(value))
                .check(matches(withText(value)));
    }

    /**
     * This method performs an empty input on the title EditText and
     * clicks on the pin-button. Verifies if a Toast appears.
     */
    @Test
    public void testEmptyTitleToast() throws Exception {

        // perform empty input
        onView(withId(R.id.editTextTitle))
                .perform(typeText(""));

        // click pin button
        onView(withText(R.string.dialog_action_pin))
                .perform(click());

        // verify toast existence
        onView(withText(R.string.message_empty_title))
                .inRoot(withDecorView(not(activityTestRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    /**
     * This method clicks the advanced-switch button, which should
     * hide CheckBoxes and verifies their (invisible) state.
     */
    @Test
    public void testExpandMechanism() throws Exception {
        getPreferencesHandler(activityTestRule).setAdvancedUse(false);
        recreateActivity(activityTestRule);

        // perform click on the advanced switch
        onView(withId(R.id.switchAdvanced))
                .perform(click());

        // checkBox should be not visible
        onView(withId(R.id.checkBoxPersistentPin))
                .check(matches(not(isChecked())));
    }

    /**
     * This method verifies the preference mechanism for the advanced usage.
     */
    @Test
    public void testPreferencesAdvanced() throws Exception {
        preferencesHandler = getPreferencesHandler(activityTestRule);

        preferencesHandler.setAdvancedUse(true);
        assertTrue(preferencesHandler.isAdvancedUsed());
    }

    /**
     * This method verifies the persist mechanism for user-created pins.
     */
    @Test
    public void testUserCreateNewPin() throws Exception {
        recreateActivity(activityTestRule);

        PinHandler pinHandler = new PinHandler(activityTestRule.getActivity().getApplicationContext());
        pinHandler.removeAllPins();

        // enter a title
        onView(withId(R.id.editTextTitle))
                .perform(typeText(LOG_TAG));

        // mark with high priority
        onView(withId(R.id.spinnerPriority))
                .perform(click());
        String highPriority = activityTestRule.getActivity().getString(R.string.priority_high);
        onData(allOf(is(instanceOf(String.class)), is(highPriority)))
                .perform(click());
        onView(withId(R.id.spinnerPriority))
                .check(matches(withSpinnerText(R.string.priority_high)));

        // mark with private visibility
        onView(withId(R.id.spinnerVisibility))
                .perform(click());
        String privateVisibility = activityTestRule.getActivity().getString(R.string.visibility_private);
        onData(allOf(is(instanceOf(String.class)), is(privateVisibility)))
                .perform(click());
        onView(withId(R.id.spinnerVisibility))
                .check(matches(withSpinnerText(R.string.visibility_private)));

        // expand layout
        if (! ((Switch) activityTestRule.getActivity().findViewById(R.id.switchAdvanced)).isChecked()) {
            onView(withId(R.id.switchAdvanced))
                    .perform(click())
                    .check(matches(isChecked()));
        }

        // mark as persistent
        if (! activityTestRule.getActivity().isPersistent()) {
            onView(withId(R.id.checkBoxPersistentPin))
                    .perform(click())
                    .check(matches(isChecked()));
        }

        // select persist button
        onView(withId(R.id.buttonPin))
                .perform(click());

        // make sure pin exists
        Map<Integer, PinHandler.Pin> pins = pinHandler.getPins();
        for (Map.Entry<Integer, PinHandler.Pin> pinEntry : pins.entrySet()) {
            String title = pinEntry.getValue().getTitle();
            assertEquals(LOG_TAG, title);

            int priority = pinEntry.getValue().getPriority();
            assertEquals(Notification.PRIORITY_HIGH, priority);

            int visibility = pinEntry.getValue().getVisibility();
            assertEquals(Notification.VISIBILITY_PRIVATE, visibility);

            boolean isPersistent = pinEntry.getValue().isPersistent();
            assertTrue(isPersistent);
        }
    }
}