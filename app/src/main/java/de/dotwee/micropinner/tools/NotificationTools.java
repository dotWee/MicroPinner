package de.dotwee.micropinner.tools;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
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

    private static final String CHANNEL_NAME = "pin_channel";
    private static final String TAG = NotificationTools.class.getSimpleName();

    /** Needed for later android versions, see:
     * https://stackoverflow.com/questions/67045607/how-to-resolve-missing-pendingintent-mutability-flag-lint-warning-in-android-a
     */
    private static final int FLAG_IMMUTABLE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;

    @NonNull
    private static PendingIntent getPinIntent(@NonNull Context context, @NonNull PinSpec pin) {
        Intent resultIntent = new Intent(context, MainDialog.class);
        resultIntent.putExtra(EXTRA_INTENT, pin);

        return PendingIntent.getActivity(context, (int) pin.getId(), resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);
    }

    @NonNull
    @TargetApi(26)
    private static NotificationChannel getNotificationChannel(int pinPriority) {

        int importance;
        switch (pinPriority) {
            case Notification.PRIORITY_DEFAULT:
                importance = NotificationManager.IMPORTANCE_DEFAULT;
                break;

            case Notification.PRIORITY_MIN:
                importance = NotificationManager.IMPORTANCE_MIN;
                break;

            case Notification.PRIORITY_LOW:
                importance = NotificationManager.IMPORTANCE_LOW;
                break;

            case Notification.PRIORITY_HIGH:
                importance = NotificationManager.IMPORTANCE_HIGH;
                break;

            default:
                importance = NotificationManager.IMPORTANCE_UNSPECIFIED;
                break;
        }

        return new NotificationChannel(NotificationChannel.DEFAULT_CHANNEL_ID, CHANNEL_NAME, importance);
    }

    public static void notify(@NonNull Context context, @NonNull PinSpec pin) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_NAME).setContentTitle(pin.getTitle())
                        .setContentText(pin.getContent())
                        .setSmallIcon(R.drawable.ic_notif_star)
                        .setPriority(pin.getPriority())
                        .setVisibility(pin.getVisibility())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(pin.getContent()))
                        .setContentIntent(getPinIntent(context, pin))

                        .setDeleteIntent(PendingIntent.getBroadcast(context, (int) pin.getId(),
                                new Intent(context, OnDeleteReceiver.class).setAction("notification_cancelled")
                                        .putExtra(EXTRA_INTENT, pin), PendingIntent.FLAG_CANCEL_CURRENT | FLAG_IMMUTABLE))
                        .setOngoing(pin.isPersistent());

        if (pin.isShowActions()) {
            builder.addAction(R.drawable.ic_action_clip,
                    context.getString(R.string.message_save_to_clipboard),
                    PendingIntent.getBroadcast(context, (int) pin.getId(),
                            new Intent(context, OnClipReceiver.class).putExtra(EXTRA_INTENT, pin),
                            PendingIntent.FLAG_CANCEL_CURRENT | FLAG_IMMUTABLE));
        }

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                /* Create or update. */
                NotificationChannel channel = new NotificationChannel(CHANNEL_NAME,
                        "Pins",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            Log.i(TAG, "Send notification with pin id " + pin.getIdAsInt() + " to system");
            Notification notification = builder.build();

            notificationManager.notify(pin.getIdAsInt(), notification);
        } else {
            Log.w(TAG, "NotificationManager is null! Couldn't send notification!");
        }
    }
}
