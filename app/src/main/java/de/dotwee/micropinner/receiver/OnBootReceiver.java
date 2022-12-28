package de.dotwee.micropinner.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import de.dotwee.micropinner.database.PinDatabase;
import de.dotwee.micropinner.database.PinSpec;
import de.dotwee.micropinner.tools.NotificationTools;

public class OnBootReceiver extends BroadcastReceiver {
    private final static String TAG = OnBootReceiver.class.getSimpleName();

    /**
     * Detect the first time the app is started. Can be used to prevent restoring notifications more than once.
     *
     *  @see <a href="https://stackoverflow.com/questions/19042510/detect-the-first-time-an-activity-is-opened-on-this-session">android - Detect the first time an Activity is opened on this session - Stack Overflow</a>&nbsp;
     */
    private static volatile boolean JUST_STARTED = true;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(@NonNull Context context, @Nullable Intent intent) {
        if (!JUST_STARTED) {
            return;
        }
        JUST_STARTED = false;

        // get all pins
        final Map<Integer, PinSpec> pinMap = PinDatabase.getInstance(context).getAllPinsMap();

        // foreach through them all
        for (Map.Entry<Integer, PinSpec> entry : pinMap.entrySet()) {
            PinSpec pin = entry.getValue();

            // TODO: on API level 23 and above we could double check that the notification doesn't already exists before restoring it, see:
            // https://stackoverflow.com/questions/23831214/notificationmanager-get-notification-by-id

            // create a notification from the object and finally restore it
            NotificationTools.notify(context, pin);
        }
    }
}
