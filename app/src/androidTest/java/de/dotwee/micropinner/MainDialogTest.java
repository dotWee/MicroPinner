package de.dotwee.micropinner;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dotwee.micropinner.view.MainDialog;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dotwee.micropinner.IsEqualTrimmingAndIgnoringCase.equalToTrimmingAndIgnoringCase;
import static de.dotwee.micropinner.VisibleViewMatcher.isVisible;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainDialogTest {

  @Rule
  public ActivityTestRule<MainDialog> mActivityTestRule =
      new ActivityTestRule<>(MainDialog.class);

  @Test
  public void mainDialogTest() {
    System.out.println("Starting run of ETGTestCaseForPR");
    ViewInteraction android_widget_Spinner =
        onView(
            allOf(
                withId(R.id.spinnerVisibility),
                isVisible(),
                isDescendantOfA(withId(R.id.dialogContentView))));
    android_widget_Spinner.perform(getLongClickAction());

    Espresso.pressBackUnconditionally();

    ViewInteraction android_widget_EditText =
        onView(
            allOf(
                withId(R.id.editTextContent),
                withTextOrHint(equalToTrimmingAndIgnoringCase("Content")),
                isVisible(),
                isDescendantOfA(withId(R.id.dialogContentView))));
    android_widget_EditText.perform(replaceText("holoptychiid"));

    ViewInteraction android_widget_Switch =
        onView(
            allOf(
                withId(R.id.switchAdvanced),
                isVisible(),
                isDescendantOfA(
                    allOf(
                        withId(R.id.linearLayoutHeader),
                        isDescendantOfA(withId(R.id.dialogHeaderView))))));
    android_widget_Switch.perform(getClickAction());

    ViewInteraction android_widget_LinearLayout =
        onView(
            allOf(
                withId(R.id.linearLayoutHeader),
                isVisible(),
                hasDescendant(
                    allOf(
                        withId(R.id.dialogTitle),
                        withTextOrHint(equalToTrimmingAndIgnoringCase("MicroPinner")))),
                hasDescendant(withId(R.id.switchAdvanced)),
                isDescendantOfA(withId(R.id.dialogHeaderView))));
    android_widget_LinearLayout.perform(getLongClickAction());

    ViewInteraction android_widget_CheckBox =
        onView(
            allOf(
                withId(R.id.checkBoxShowActions),
                withTextOrHint(equalToTrimmingAndIgnoringCase("Show notification actions")),
                isVisible(),
                isDescendantOfA(withId(R.id.dialogContentView))));
    android_widget_CheckBox.perform(getClickAction());

    ViewInteraction android_widget_EditText2 =
        onView(
            allOf(
                withId(R.id.editTextTitle),
                withTextOrHint(equalToTrimmingAndIgnoringCase("Title")),
                isVisible(),
                isDescendantOfA(withId(R.id.dialogContentView))));
    android_widget_EditText2.perform(replaceText("tetraspgia"));

    ViewInteraction android_widget_Button =
        onView(
            allOf(
                withId(R.id.buttonPin),
                withTextOrHint(equalToTrimmingAndIgnoringCase("PIN")),
                isVisible(),
                isDescendantOfA(withId(R.id.dialogFooterView))));
    android_widget_Button.perform(getClickAction());
  }

  private static Matcher<View> withTextOrHint(final Matcher<String> stringMatcher) {
    return anyOf(withText(stringMatcher), withHint(stringMatcher));
  }

  private ClickWithoutDisplayConstraint getClickAction() {
    return new ClickWithoutDisplayConstraint(
        Tap.SINGLE,
        GeneralLocation.VISIBLE_CENTER,
        Press.FINGER,
        InputDevice.SOURCE_UNKNOWN,
        MotionEvent.BUTTON_PRIMARY);
  }

  private ClickWithoutDisplayConstraint getLongClickAction() {
    return new ClickWithoutDisplayConstraint(
        Tap.LONG,
        GeneralLocation.CENTER,
        Press.FINGER,
        InputDevice.SOURCE_UNKNOWN,
        MotionEvent.BUTTON_PRIMARY);
  }
}
