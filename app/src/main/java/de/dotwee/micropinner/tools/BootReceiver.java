package de.dotwee.micropinner.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.ui.MainActivity;

/**
 * Created by Lukas on 09.06.2015.
 */
public class BootReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainIntent);

        PendingIntent mainPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder defaultNotification = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.main_name))
                .setContentText("Click to pin something new.")
                .setPriority(Notification.PRIORITY_LOW)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentIntent(mainPendingIntent)
                .setSmallIcon(R.drawable.ic_pin_24dp);

        if (Build.VERSION.SDK_INT >= 21) {
            defaultNotification.setColor(context.getResources().getColor(R.color.accent));
            defaultNotification.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, defaultNotification.build());
    }

}
