package de.dotwee.micropinner.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.PreferencesHandler;
import de.dotwee.micropinner.ui.MainActivity;

/**
 * Created by Lukas Wolfsteiner on 17.10.2015.
 */
public class OnNewPinReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "OnNewPinReceiver";

    /**
     * The static and final notification id for "new pin" notifications
     */
    private static final int DEFAULT_NOTIFICATIONID = 0;

    /**
     * This method returns a {@link android.app.Notification.Builder}, made for "New pin" notifications
     *
     * @param context
     * @return the default "new pin" notification
     */
    private static Notification getNotification(Context context) {

        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntent(new Intent(context, MainActivity.class))
                .getPendingIntent(DEFAULT_NOTIFICATIONID, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder defaultNotification = new Notification.Builder(context)
                .setContentTitle(context.getResources().getString(R.string.main_name))
                .setContentText(context.getResources().getString(R.string.message_pin_new))
                .setPriority(Notification.PRIORITY_LOW)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notif_pin);


        // function .setShowWhen is only available on API => 17
        if (Build.VERSION.SDK_INT >= 17)
            defaultNotification.setShowWhen(false);

        // the visibility api is only available on lollipop and up
        if (Build.VERSION.SDK_INT >= 21)
            defaultNotification.setVisibility(Notification.VISIBILITY_PUBLIC);

        return defaultNotification.build();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean showNotification = PreferencesHandler.getInstance(context).isShowNewPinEnabled();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (showNotification) notificationManager.notify(
                DEFAULT_NOTIFICATIONID,
                getNotification(context)
        );

        else notificationManager.cancel(
                DEFAULT_NOTIFICATIONID
        );

    }
}
