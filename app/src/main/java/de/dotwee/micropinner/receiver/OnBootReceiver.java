package de.dotwee.micropinner.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Map;

import de.dotwee.micropinner.tools.PinHandler;

public class OnBootReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
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
