package de.dotwee.micropinner.tools;

import android.graphics.drawable.ColorDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.test.espresso.intent.Checks;
import androidx.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Created by Lukas Wolfsteiner on 06.11.2015.
 */
public final class Matches {

    /**
     * This matcher checks if a TextView displays its text in
     * a specific color.
     *
     * @param color The color to verify.
     * @return Corresponding matcher.
     */
    @NonNull
    public static Matcher<View> withTextColor(@ColorInt final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public boolean matchesSafely(TextView warning) {
                return color == warning.getCurrentTextColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }

    /**
     * This matcher checks if a View displays its background in
     * a specific color.
     *
     * @param color The color to verify.
     * @return Corresponding matcher.
     */
    @NonNull
    public static Matcher<View> withBackgroundColor(@ColorInt final int color) {
        Checks.checkNotNull(color);

        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(View warning) {
                return color == ((ColorDrawable) warning.getBackground()).getColor();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }
}
