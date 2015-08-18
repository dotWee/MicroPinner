package de.dotwee.micropinner.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

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
        Log.i(LOG_TAG, "Received boot-intent.");


        Intent mainIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainIntent);

        PendingIntent mainPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder defaultNotification = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.main_name))
                .setContentText(context.getResources().getString(R.string.message_pin_new))
                .setPriority(Notification.PRIORITY_LOW)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentIntent(mainPendingIntent)
                .setSmallIcon(R.drawable.ic_pin_24dp);

        if (Build.VERSION.SDK_INT >= 17) defaultNotification.setShowWhen(false);

        if (Build.VERSION.SDK_INT >= 21) {
            defaultNotification.setColor(context.getResources().getColor(R.color.accent));
            defaultNotification.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        if (PreferencesHandler.getInstance(context).isShowNewPinEnabled())
            notificationManager.notify(DEFAULT_NOTIFICATIONID, defaultNotification.build());
        else notificationManager.cancel(DEFAULT_NOTIFICATIONID);

        if (PreferencesHandler.getInstance(context).isRestoreEnabled()) {
            Map<Integer, PinHandler.Pin> pinMap = new PinHandler(context).getPins();
            for (Map.Entry<Integer, PinHandler.Pin> entry : pinMap.entrySet()) {
                PinHandler.Pin pin = entry.getValue();

                PendingIntent pinIntent = pin.toIntent(context);
                Notification pinNotification = pin.toNotification(context, pinIntent);
                notificationManager.notify(pin.getId(), pinNotification);
            }
        }
    }

}
