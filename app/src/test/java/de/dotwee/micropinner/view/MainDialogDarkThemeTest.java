package de.dotwee.micropinner.view;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import de.dotwee.micropinner.R;

import static de.dotwee.micropinner.tools.ThemeTools.getAccentColor;
import static de.dotwee.micropinner.tools.ThemeTools.getBackgroundColor;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class MainDialogDarkThemeTest {
    ActivityController<MainDialog> mainDialogActivityController;

    @Before
    public void setUp() {
        mainDialogActivityController = Robolectric.buildActivity(MainDialog.class).create().start();
    }

    /**
     * This method verifies the light theme's caption accent color.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Test
    @Config(qualifiers = "night")
    public void testThemeLightCaptionAccent() {
        MainDialog mainDialog = mainDialogActivityController.get();

        TextView textViewCaptionTitle = mainDialog.findViewById(R.id.textViewCaptionTitle);
        int colorCaptionTitleExpected = getAccentColor(mainDialog, false);
        int colorCaptionTitleActual = textViewCaptionTitle.getCurrentTextColor();
        assertEquals(colorCaptionTitleExpected, colorCaptionTitleActual);

        TextView textViewCaptionContent = mainDialog.findViewById(R.id.textViewCaptionContent);
        int colorCaptionContentExpected = getAccentColor(mainDialog, false);
        int colorCaptionContentActual = textViewCaptionContent.getCurrentTextColor();
        assertEquals(colorCaptionContentExpected, colorCaptionContentActual);

        TextView textViewCaptionPriority = mainDialog.findViewById(R.id.textViewCaptionPriority);
        int colorCaptionPriorityExpected = getAccentColor(mainDialog, false);
        int colorCaptionPriorityActual = textViewCaptionPriority.getCurrentTextColor();
        assertEquals(colorCaptionPriorityExpected, colorCaptionPriorityActual);

        TextView textViewCaptionVisibility = mainDialog.findViewById(R.id.textViewCaptionVisibility);
        int colorCaptionVisibilityExpected = getAccentColor(mainDialog, false);
        int colorCaptionVisibilityActual = textViewCaptionVisibility.getCurrentTextColor();
        assertEquals(colorCaptionVisibilityExpected, colorCaptionVisibilityActual);
    }

    /**
     * This method verifies the light theme's background.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Test
    @Config(qualifiers = "night")
    public void testThemeLightBackground() throws Exception {
        MainDialog mainDialog = mainDialogActivityController.get();

        View view = mainDialog.findViewById(android.R.id.content);
        int expectedColor = getBackgroundColor(mainDialogActivityController.get(), false);
        int actualColor = ((ColorDrawable) view.getBackground()).getColor();
        assertEquals(expectedColor, actualColor);
    }
}
