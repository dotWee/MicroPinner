package de.dotwee.micropinner.view;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dotwee.micropinner.Constants;
import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.PinHandler;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Lukas Wolfsteiner on 06.11.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RunWith(AndroidJUnit4.class)
public class MainDialogParentPinTest {
    private static final String LOG_TAG = "MainDialogParentPinTest";

    /**
     * Preferred JUnit 4 mechanism of specifying the
     * activity to be launched before each test
     */
    @Rule
    public ActivityTestRule<MainDialog> activityTestRule =
            new ActivityTestRule<>(MainDialog.class);

    @Before
    public void setUp() throws Exception {

        final Intent testIntent =
                new Intent(activityTestRule.getActivity(), MainDialog.class).putExtra(
                        PinHandler.Pin.EXTRA_INTENT, Constants.testPin);

        Intents.init();
        activityTestRule.launchActivity(testIntent);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDialogTitle() throws Exception {

        // verify changed dialog title
        onView(ViewMatchers.withId(R.id.dialogTitle)).check(matches(withText(R.string.edit_name)));
    }

    @Test
    public void testDialogButtons() throws Exception {

        // verify changed buttons
        onView(withId(R.id.buttonCancel)).check(matches(withText(R.string.dialog_action_delete)));
    }

    @Test
    public void testPinTitle() throws Exception {

        // verify pin title
        onView(withId(R.id.editTextTitle)).check(matches(withText(Constants.testPinTitle)));
    }

    @Test
    public void testPinContent() throws Exception {

        // verify pin content
        onView(withId(R.id.editTextContent)).check(matches(withText(Constants.testPinContent)));
    }

    /**
     * This method verifies the pin's priority.
     *
     * @throws Exception
     */
    @Test
    public void testPinPriority() throws Exception {

        // verify selected priority
        onView(withId(R.id.spinnerPriority)).check(matches(withSpinnerText(R.string.priority_high)));
    }

    /**
     * This method verifies the pin's visibility.
     *
     * @throws Exception
     */
    @Test
    public void testPinVisibility() throws Exception {

        onView(withId(R.id.spinnerVisibility)).check(
                matches(withSpinnerText(R.string.visibility_private)));
    }

    /**
     * This method verifies the pin's persistence.
     *
     * @throws Exception
     */
    @Test
    public void testPinPersistence() throws Exception {

        onView(withId(R.id.checkBoxPersistentPin)).check(matches(isChecked()));
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
}
