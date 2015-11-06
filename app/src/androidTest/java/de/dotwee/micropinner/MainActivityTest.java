package de.dotwee.micropinner;

import android.app.Notification;
import android.content.Intent;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.view.MainActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private static final String LOG_TAG = "MainActivityTest";

    /**
     * Preferred JUnit 4 mechanism of specifying the activity to be launched before each test
     */
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    /**
     * This method looks for an EditText with id = R.id.editTextTitle,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextTitle() {
        final String value = "MicroPinner title input";

        onView(withId(R.id.editTextTitle)).perform(typeText(value)).check(matches(withText(value)));
    }

    /**
     * This method recreates the main activity and verifies the focus,
     * which should be on the title EditText.
     */
    @Test
    public void testEditTextFocus() {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().recreate();
            }
        });

        onView(withId(R.id.editTextTitle)).check(matches(isFocusable()));
    }

    /**
     * This method looks for an EditText with id = R.id.editTextContent,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextContent() {
        final String value = "MicroPinner title input";

        onView(withId(R.id.editTextContent)).perform(typeText(value)).check(matches(withText(value)));
    }

    /**
     * This method performs an empty input on the title EditText and
     * clicks on the pin-button. Verifies if a Toast appears.
     */
    @Test
    public void testEmptyTitleToast() {

        // perform empty input
        onView(withId(R.id.editTextTitle)).perform(typeText(""));

        // click pin button
        onView(withText(R.string.dialog_action_pin)).perform(click());

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
    public void testExpandMechanism() {
        onView(withId(R.id.switchAdvanced)).perform(click());

        try {
            onView(withText(R.string.input_description_shownewpin)).perform(click());
            onView(withText(R.string.input_description_makepinpersistent)).perform(click());

        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method verifies the parent-pin mechanism using a
     * custom pin and intents.
     */
    @Test
    public void testParentPinIntent() {
        final PinHandler.Pin testPin = new PinHandler.Pin(
                Notification.VISIBILITY_PRIVATE,
                Notification.PRIORITY_HIGH,
                LOG_TAG,
                null,
                true
        );

        final Intent testIntent = new Intent(activityTestRule.getActivity(), MainActivity.class)
                .putExtra(PinHandler.Pin.EXTRA_INTENT, testPin);

        Intents.init();
        activityTestRule.launchActivity(testIntent);

        // verify changed buttons
        onView(withId(R.id.buttonCancel)).check(matches(withText(R.string.dialog_action_delete)));

        // verify pin content
        onView(withId(R.id.editTextTitle)).check(matches(withText(LOG_TAG)));

        Intents.release();
    }
}