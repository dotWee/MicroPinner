package de.dotwee.micropinner.tools;

import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import org.robolectric.android.controller.ActivityController;

import de.dotwee.micropinner.R;

public class ThemeTools {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @ColorInt
    public static int getAccentColor(AppCompatActivity activity, boolean light) {
        Configuration configuration = new Configuration();
        configuration.uiMode = light ? Configuration.UI_MODE_NIGHT_NO : Configuration.UI_MODE_NIGHT_YES;

        return ContextCompat.getColor(activity.createConfigurationContext(configuration), R.color.accent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @ColorInt
    public static int getBackgroundColor(@NonNull AppCompatActivity activity, boolean light) {
        Configuration configuration = new Configuration();
        configuration.uiMode = light ? Configuration.UI_MODE_NIGHT_NO : Configuration.UI_MODE_NIGHT_YES;

        return ContextCompat.getColor(activity.createConfigurationContext(configuration), R.color.background);
    }

    public static void changeUiMode(@NonNull ActivityController activityController, int mode) {
        AppCompatActivity activity = (AppCompatActivity) activityController.get();

        activity.runOnUiThread(() -> activity.getDelegate().setLocalNightMode(mode));
        activityController.restart();
    }
}
