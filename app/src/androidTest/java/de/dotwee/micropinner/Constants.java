package de.dotwee.micropinner;

import android.app.Notification;

import de.dotwee.micropinner.tools.PinHandler;

/**
 * Created by lukas on 20.07.2016.
 */
public final class Constants {
    static final String LOG_TAG = "Constants";

    // static pin to test
    public static final PinHandler.Pin testPin = new PinHandler.Pin(
            Notification.VISIBILITY_PRIVATE,
            Notification.PRIORITY_HIGH,
            LOG_TAG,
            LOG_TAG,
            true,
            true
    );
}
