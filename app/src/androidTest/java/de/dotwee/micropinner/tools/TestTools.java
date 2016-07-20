package de.dotwee.micropinner.tools;

import android.support.test.rule.ActivityTestRule;

import de.dotwee.micropinner.view.MainActivity;

/**
 * Created by lukas on 20.07.2016.
 */
public final class TestTools {
    static final String TAG = "TestTools";

    /**
     * This method returns an instance of {@link PreferencesHandler}
     * for an activity test rule.
     *
     * @param activityTestRule Source to get the PreferenceHandler.
     *
     * @return An instance of {@link PreferencesHandler}
     */
    public static PreferencesHandler getPreferencesHandler(ActivityTestRule<MainActivity> activityTestRule) {
        return PreferencesHandler.getInstance(activityTestRule.getActivity());
    }

    /**
     * This method recreates the main activity in order to apply
     * themes or reload the preference cache.
     *
     * @param activityTestRule Source to get access to the activity.
     */
    public static void recreateActivity(final ActivityTestRule<MainActivity> activityTestRule) {
        activityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityTestRule.getActivity().recreate();
            }
        });
    }
}
