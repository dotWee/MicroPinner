package de.dotwee.micropinner.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Map;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.PinHandler;
import de.dotwee.micropinner.tools.PreferencesHandler;
import de.dotwee.micropinner.ui.MainActivity;

/**
 * Created by Lukas on 09.06.2015.
 */
public class OnBootReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnBootReceiver";
    private final static int DEFAULT_NOTIFICATIONID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // check if the should be shown
        if (PreferencesHandler.getInstance(context).isShowNewPinEnabled())
            notificationManager.notify(DEFAULT_NOTIFICATIONID, getNewPinNotification(context).build());

        else notificationManager.cancel(DEFAULT_NOTIFICATIONID);

        // get all pins
        Map<Integer, PinHandler.Pin> pinMap = new PinHandler(context).getPins();

        // foreach through them all
        for (Map.Entry<Integer, PinHandler.Pin> entry : pinMap.entrySet()) {
            PinHandler.Pin pin = entry.getValue();

            PendingIntent pinIntent = pin.toIntent(context);

            // create a notification out of the object
            Notification pinNotification = pin.toNotification(context, pinIntent);

            // and finally restore it
            notificationManager.notify(pin.getId(), pinNotification);
        }
    }

    /**
     * @param context
     * @return the default "new pin" notification
     */
    private Notification.Builder getNewPinNotification(Context context) {

        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntent(new Intent(context, MainActivity.class))
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder defaultNotification = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.main_name))
                .setContentText(context.getResources().getString(R.string.message_pin_new))
                .setPriority(Notification.PRIORITY_LOW)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_pin_24dp);


        // function .setShowWhen is only available on API => 17
        if (Build.VERSION.SDK_INT >= 17)
            defaultNotification.setShowWhen(false);

        // the visibility api is only available on lollipop and up
        if (Build.VERSION.SDK_INT >= 21)
            defaultNotification.setVisibility(Notification.VISIBILITY_PUBLIC);

        return defaultNotification;
    }
}
