package de.dotwee.micropinner.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Map;

import de.dotwee.micropinner.tools.PinHandler;

public class OnBootReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnBootReceiver";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if (! intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            throw new IllegalStateException("OnBootReceiver's intent actions is not " + Intent.ACTION_BOOT_COMPLETED);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

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
