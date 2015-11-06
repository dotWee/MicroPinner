package de.dotwee.micropinner;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import de.dotwee.micropinner.view.MainActivity;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

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
     * This method looks for an EditText with id = R.id.editTextContent,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextContent() {
        final String value = "MicroPinner title input";

        onView(withId(R.id.editTextContent)).perform(typeText(value)).check(matches(withText(value)));
    }

    /**
     * This method clicks the advanced-switch button, which should
     * hide CheckBoxes and verifies their (invisible) state.
     */
    @Test
    public void testExpandMechanism() {
        onView(withId(R.id.switchAdvanced)).perform(ViewActions.click());

        try {
            onView(withText(R.string.input_description_shownewpin)).perform(click());
            onView(withText(R.string.input_description_makepinpersistent)).perform(click());

        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }
    }
}