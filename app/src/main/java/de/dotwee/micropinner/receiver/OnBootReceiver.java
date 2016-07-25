package de.dotwee.micropinner.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Map;

import de.dotwee.micropinner.tools.PinHandler;

public class OnBootReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnBootReceiver";

    @Override
    public void onReceive(@NonNull Context context, @Nullable Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.w(LOG_TAG,
                    "Intent (and its action) must be not null to work with it, returning without work");
            return;
        }

        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.w(LOG_TAG, "OnBootReceiver's intent actions is not "
                    + Intent.ACTION_BOOT_COMPLETED
                    + ", returning without work");
            return;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // get all pins
        final Map<Integer, PinHandler.Pin> pinMap = new PinHandler(context).getPins();

        // foreach through them all
        for (Map.Entry<Integer, PinHandler.Pin> entry : pinMap.entrySet()) {
            PinHandler.Pin pin = entry.getValue();

            PendingIntent pinIntent = pin.toIntent(context);

            // create a notification from the object
            Notification pinNotification = pin.toNotification(context, pinIntent);

            // and finally restore it
            notificationManager.notify(pin.getId(), pinNotification);
        }
    }
}
