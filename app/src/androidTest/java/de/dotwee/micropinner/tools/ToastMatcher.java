package de.dotwee.micropinner.tools;

import android.os.IBinder;
import androidx.test.espresso.Root;
import android.view.WindowManager.LayoutParams;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * This class allows to match Toast messages in tests with Espresso.
 *
 * Idea taken from: <a href="https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso">Checking toast message in android espresso - Stack Overflow</a>
 *
 * Usage in test class:
 *
 * <pre>
 * {@code
 * import somepkg.ToastMatcher.Companion.onToast;
 *
 * // To assert a toast does *not* pop up:
 * onView(withText("text")).inRoot(new ToastMatcher()).check(doesNotExist());
 * onView(withText(textId)).inRoot(new ToastMatcher()).check(doesNotExist());
 *
 * // To assert a toast does pop up:
 * onView(withText("text")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
 * onView(withText(textId)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
 * }
 */
public class ToastMatcher extends TypeSafeMatcher<Root> {

    /** Default for maximum number of retries to wait for the toast to pop up */
    private static final int DEFAULT_MAX_FAILURES = 5;

    /** Restrict number of false results from matchesSafely to avoid endless loop */
    private int failures = 0;
    private final int maxFailures;

    public ToastMatcher() {
        this(DEFAULT_MAX_FAILURES);
    }
    public ToastMatcher(int maxFailures) {
        this.maxFailures = maxFailures;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    @Override
    public boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;
        if (type == LayoutParams.TYPE_TOAST || type == LayoutParams.TYPE_APPLICATION_OVERLAY) {
            IBinder windowToken = root.getDecorView().getWindowToken();
            IBinder appToken = root.getDecorView().getApplicationWindowToken();
            if (windowToken == appToken) {
                // windowToken == appToken means this window isn't contained by any other windows.
                // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                return true;
            }
        }
        // Method is called again if false is returned which is useful because a toast may take some time to pop up. But for
        // obvious reasons an infinite wait isn't of help. So false is only returned as often as maxFailures specifies.
        return (++failures >= maxFailures);
    }

}