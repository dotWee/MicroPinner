package de.dotwee.micropinner.view;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatDelegate;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import de.dotwee.micropinner.R;

import static de.dotwee.micropinner.tools.ThemeTools.changeUiMode;
import static de.dotwee.micropinner.tools.ThemeTools.getAccentColor;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class MainDialogTest {
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
    public void testThemeLightCaptionAccent() {
        changeUiMode(mainDialogActivityController, AppCompatDelegate.MODE_NIGHT_NO);
        MainDialog mainDialog = mainDialogActivityController.get();

        TextView textViewCaptionTitle = mainDialog.findViewById(R.id.textViewCaptionTitle);
        int colorCaptionTitleExpected = getAccentColor(mainDialog, true);
        int colorCaptionTitleActual = textViewCaptionTitle.getCurrentTextColor();
        assertEquals(colorCaptionTitleExpected, colorCaptionTitleActual);

        TextView textViewCaptionContent = mainDialog.findViewById(R.id.textViewCaptionContent);
        int colorCaptionContentExpected = getAccentColor(mainDialog, true);
        int colorCaptionContentActual = textViewCaptionContent.getCurrentTextColor();
        assertEquals(colorCaptionContentExpected, colorCaptionContentActual);

        TextView textViewCaptionPriority = mainDialog.findViewById(R.id.textViewCaptionPriority);
        int colorCaptionPriorityExpected = getAccentColor(mainDialog, true);
        int colorCaptionPriorityActual = textViewCaptionPriority.getCurrentTextColor();
        assertEquals(colorCaptionPriorityExpected, colorCaptionPriorityActual);

        TextView textViewCaptionVisibility = mainDialog.findViewById(R.id.textViewCaptionVisibility);
        int colorCaptionVisibilityExpected = getAccentColor(mainDialog, true);
        int colorCaptionVisibilityActual = textViewCaptionVisibility.getCurrentTextColor();
        assertEquals(colorCaptionVisibilityExpected, colorCaptionVisibilityActual);
    }
}