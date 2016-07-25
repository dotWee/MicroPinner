package de.dotwee.micropinner.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by lukas on 18.08.2015 - 16:11
 * for project MicroPinner.
 */
public class PreferencesHandler {
    private static final String PREF_FIRST_USE = "pref_firstuse";
    private static final String PREF_ADVANCED_USE = "pref_advanceduse";
    private static final String PREF_LIGHT_THEME = "pref_lighttheme";
    private static final String PREF_SHOW_NOTIFICATION_ACTIONS = "pref_shownotificationactions";

    private final static String LOG_TAG = "PreferencesHandler";
    private static PreferencesHandler instance;
    private final SharedPreferences preferences;

    private PreferencesHandler(@NonNull Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized PreferencesHandler getInstance(Context context) {
        if (instance == null) instance = new PreferencesHandler(context);

        return instance;
    }

    public boolean isFirstUse() {
        boolean ret = false;

        if (!preferences.contains(PREF_FIRST_USE)) {
            preferences.edit().putBoolean(PREF_FIRST_USE, true).apply();
            ret = true;
        }

        return ret;
    }

    public boolean isAdvancedUsed() {
        return preferences.getBoolean(PREF_ADVANCED_USE, false);
    }

    public void setAdvancedUse(boolean b) {
        applyPreference(PREF_ADVANCED_USE, b);
    }

    public boolean isLightThemeEnabled() {
        return preferences.getBoolean(PREF_LIGHT_THEME, true);
    }

    public void setLightThemeEnabled(boolean b) {
        applyPreference(PREF_LIGHT_THEME, b);
    }

    public boolean isNotificationActionsEnabled() {
        return preferences.getBoolean(PREF_SHOW_NOTIFICATION_ACTIONS, false);
    }

    public void setNotificationActionsEnabled(boolean b) {
        applyPreference(PREF_SHOW_NOTIFICATION_ACTIONS, b);
    }

    private void applyPreference(@NonNull String key, boolean state) {
        preferences.edit().putBoolean(key, state).apply();
    }
}
