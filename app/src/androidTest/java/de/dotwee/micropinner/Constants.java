package de.dotwee.micropinner;

import android.app.Notification;

import de.dotwee.micropinner.database.PinSpec;

/**
 * Created by lukas on 20.07.2016.
 */
public final class Constants {
    public static final PinSpec testPin;
    private static final String LOG_TAG = "Constants";
    public static final String testPinTitle = LOG_TAG, testPinContent = LOG_TAG;    // static pin to test

    static {
        testPin = new PinSpec(testPinTitle, testPinContent, Notification.VISIBILITY_PRIVATE, Notification.PRIORITY_HIGH, true, true);
    }
}
