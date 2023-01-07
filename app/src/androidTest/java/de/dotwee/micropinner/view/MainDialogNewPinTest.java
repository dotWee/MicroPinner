package de.dotwee.micropinner.view;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.database.PinDatabase;
import de.dotwee.micropinner.tools.Matches;
import de.dotwee.micropinner.tools.PreferencesHandler;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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
public class MainDialogNewPinTest {
    private static final String LOG_TAG = "MainDialogNewPinTest";
    /**
     * Preferred JUnit 4 mechanism of specifying the
     * activity to be launched before each test
     */
    @Rule
    public ActivityTestRule<MainDialog> activityTestRule =
            new ActivityTestRule<>(MainDialog.class);

    /**
     * This method verifies the advanced-switch's functionality.
     *
     * @throws Exception
     */
    @Test
    public void testAdvancedSwitch() throws Exception {
        boolean currentState = getPreferencesHandler(activityTestRule).isAdvancedUsed();
        onView(withId(R.id.switchAdvanced)).perform(click());
        assertEquals(!currentState, getPreferencesHandler(activityTestRule).isAdvancedUsed());

        onView(withId(R.id.switchAdvanced)).perform(click());
        assertEquals(currentState, getPreferencesHandler(activityTestRule).isAdvancedUsed());
    }

    /**
     * This method looks for an EditText with id = R.id.editTextTitle,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextTitle() throws Exception {
        final String value = "MicroPinner title input";

        onView(withId(R.id.editTextTitle)).perform(typeText(value)).check(matches(withText(value)));
    }

    /**
     * This method recreates the main activity and verifies the focus,
     * which should be on the title EditText.
     */
    @Test
    public void testEditTextFocus() throws Exception {
        activityTestRule.getActivity().runOnUiThread(() -> activityTestRule.getActivity().recreate());

        onView(withId(R.id.editTextTitle)).check(matches(isFocusable()));
    }

    /**
     * This method looks for an EditText with id = R.id.editTextContent,
     * performs some inout and verifies the EditText's entered value.
     */
    @Test
    public void testEditTextContent() throws Exception {
        final String value = "MicroPinner title input";

        onView(withId(R.id.editTextContent)).perform(typeText(value)).check(matches(withText(value)));
    }

    /**
     * This method performs an empty input on the title EditText and
     * clicks on the pin-button. Verifies if a Toast appears.
     */
    @Test
    public void testEmptyTitleToast() throws Exception {

        // perform empty input
        onView(withId(R.id.editTextTitle)).perform(typeText(""));

        // click pin button
        onView(withText(R.string.dialog_action_pin)).perform(click());

        // can't see toast if another toast is already present
        onView(withText(R.string.message_visibility_unsupported)).inRoot(Matches.isToast())
                .check(doesNotExist());

        // verify toast existence
        onView(withText(R.string.message_empty_title)).inRoot(Matches.isToast())
                .check(matches(isDisplayed()));
    }

    /**
     * This method clicks the advanced-switch button, which should
     * hide CheckBoxes and verifies their (invisible) state.
     */
    @Test
    public void testExpandMechanismThroughSwitch() throws Exception {

        onView(withId(R.id.switchAdvanced)).perform(click());
        if (getPreferencesHandler(activityTestRule).isAdvancedUsed()) {

            onView(withId(R.id.switchAdvanced)).perform(click());
        } else recreateActivity(activityTestRule);

        // CheckBoxes should be not visible
        onView(withId(R.id.checkBoxPersistentPin)).check(matches(not(isDisplayed())));
        onView(withId(R.id.checkBoxShowActions)).check(matches(not(isDisplayed())));
    }

    /**
     * This method clicks the header layout, which should
     * hide CheckBoxes and verifies their (invisible) state.
     */
    @Test
    public void testExpandMechanismThroughHeader() throws Exception {

        onView(withId(R.id.linearLayoutHeader)).perform(click());
        if (getPreferencesHandler(activityTestRule).isAdvancedUsed()) {

            onView(withId(R.id.linearLayoutHeader)).perform(click());
        } else recreateActivity(activityTestRule);

        // CheckBoxes should be not visible
        onView(withId(R.id.checkBoxPersistentPin)).check(matches(not(isDisplayed())));
        onView(withId(R.id.checkBoxShowActions)).check(matches(not(isDisplayed())));
    }

    /**
     * This method verifies the preference mechanism for the advanced usage.
     */
    @Test
    public void testPreferencesAdvanced() throws Exception {
        PreferencesHandler preferencesHandler = getPreferencesHandler(activityTestRule);

        preferencesHandler.setAdvancedUse(true);
        assertTrue(preferencesHandler.isAdvancedUsed());
    }

    /**
     * This method verifies the persist mechanism for user-created pins.
     */
    @Test
    public void testUserCreateNewPin() throws Exception {
        recreateActivity(activityTestRule);

        PinDatabase pinDatabase = PinDatabase.getInstance(activityTestRule.getActivity().getApplicationContext());
        pinDatabase.deleteAll();

        long previousPinAmount = pinDatabase.count();

        // enter a title
        onView(withId(R.id.editTextTitle)).perform(typeText(LOG_TAG));

        // mark with high priority
        onView(withId(R.id.spinnerPriority)).perform(click());
        String highPriority = activityTestRule.getActivity().getString(R.string.priority_high);
        onData(allOf(is(instanceOf(String.class)), is(highPriority))).perform(click());
        onView(withId(R.id.spinnerPriority)).check(matches(withSpinnerText(R.string.priority_high)));

        // mark with private visibility
        onView(withId(R.id.spinnerVisibility)).perform(click());
        String privateVisibility =
                activityTestRule.getActivity().getString(R.string.visibility_private);
        onData(allOf(is(instanceOf(String.class)), is(privateVisibility))).perform(click());
        onView(withId(R.id.spinnerVisibility)).check(
                matches(withSpinnerText(R.string.visibility_private)));

        // expand layout
        if (!getPreferencesHandler(activityTestRule).isAdvancedUsed()) {
            onView(withId(R.id.switchAdvanced)).perform(click()).check(matches(isChecked()));
        }

        // mark as persistent
        if (!activityTestRule.getActivity().isPersistent()) {
            onView(withId(R.id.checkBoxPersistentPin)).perform(click()).check(matches(isChecked()));
        }

        // select persist button
        onView(withId(R.id.buttonPin)).perform(click());

        // make sure pin exists
        long newPinAmount = pinDatabase.count();
        assertEquals(previousPinAmount + 1, newPinAmount);
    }
}