package de.dotwee.micropinner.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.database.PinSpec;
import de.dotwee.micropinner.receiver.OnClipReceiver;
import de.dotwee.micropinner.receiver.OnDeleteReceiver;
import de.dotwee.micropinner.view.MainDialog;

/**
 * Created by lukas on 10.08.2016.
 */
public class NotificationTools {
    public final static String EXTRA_INTENT = "IAMAPIN";
    private static final String TAG = NotificationTools.class.getSimpleName();

    @NonNull
    private static PendingIntent getPinIntent(@NonNull Context context, @NonNull PinSpec pin) {
        Intent resultIntent = new Intent(context, MainDialog.class);
        resultIntent.putExtra(EXTRA_INTENT, pin);

        return PendingIntent.getActivity(context, (int) pin.getId(), resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void notify(@NonNull Context context, @NonNull PinSpec pin) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context).setContentTitle(pin.getTitle())
                        .setContentText(pin.getContent())
                        .setSmallIcon(R.drawable.ic_notif_star)
                        .setPriority(pin.getPriority())
                        .setVisibility(pin.getVisibility())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(pin.getContent()))
                        .setContentIntent(getPinIntent(context, pin))

                        .setDeleteIntent(PendingIntent.getBroadcast(context, (int) pin.getId(),
                                new Intent(context, OnDeleteReceiver.class).setAction("notification_cancelled")
                                        .putExtra(EXTRA_INTENT, pin), PendingIntent.FLAG_CANCEL_CURRENT))
                        .setOngoing(pin.isPersistent());

        if (pin.isShowActions()) {
            builder.addAction(R.drawable.ic_action_clip,
                    context.getString(R.string.message_save_to_clipboard),
                    PendingIntent.getBroadcast(context, (int) pin.getId(),
                            new Intent(context, OnClipReceiver.class).putExtra(EXTRA_INTENT, pin),
                            PendingIntent.FLAG_CANCEL_CURRENT));
        }

        Notification notification = builder.build();

        Log.i(TAG, "Send notification with pin id " + pin.getIdAsInt() + " to system");
        notificationManager.notify(pin.getIdAsInt(), notification);
    }
}
