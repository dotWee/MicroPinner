package de.dotwee.micropinner.tools;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.service.notification.StatusBarNotification;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.database.PinDatabase;
import de.dotwee.micropinner.database.PinSpec;
import de.dotwee.micropinner.receiver.OnClipReceiver;
import de.dotwee.micropinner.receiver.OnDeleteReceiver;
import de.dotwee.micropinner.view.MainDialog;

/**
 * Created by lukas on 10.08.2016.
 */
public class NotificationTools {
    /**
     * Name of extra data inside intents that contains a PinSpec object with data about the parent pin.
     */
    public final static String EXTRA_INTENT = "IAMAPIN";

    /**
     * Used in app version 2.2.0 and earlier.
     */
    private static final String CHANNEL_NAME_OLD = "pin_channel";
    private static final String CHANNEL_NAME_PUBLIC = "pin_channel_public";
    private static final String CHANNEL_NAME_PRIVATE = "pin_channel_private";
    private static final String CHANNEL_NAME_SECRET = "pin_channel_secret";

    private static final String TAG = NotificationTools.class.getSimpleName();

    /** Needed for later android versions, see:
     * https://stackoverflow.com/questions/67045607/how-to-resolve-missing-pendingintent-mutability-flag-lint-warning-in-android-a
     */
    private static final int FLAG_IMMUTABLE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;

    /**
     * Detect the first time the app is started. Used to prevent restoring notifications more than once.
     *
     *  @see <a href="https://stackoverflow.com/questions/19042510/detect-the-first-time-an-activity-is-opened-on-this-session">android - Detect the first time an Activity is opened on this session - Stack Overflow</a>&nbsp;
     */
    private static volatile boolean JUST_STARTED = true;

    public static void restoreNotifications(@NonNull Context context) {
        if (!JUST_STARTED) {
            return;
        }
        JUST_STARTED = false;

        // get all pins
        final Map<Integer, PinSpec> pinMap = PinDatabase.getInstance(context).getAllPinsMap();

        @Nullable final Map<Integer, StatusBarNotification> activeNotifications = getActiveNotifications(context);

        // foreach through them all
        for (Map.Entry<Integer, PinSpec> entry : pinMap.entrySet()) {
            PinSpec pin = entry.getValue();

            // On API level 23 and above we double check that the notification doesn't already exists before restoring it.
            if (activeNotifications != null && activeNotifications.containsKey(pin.getIdAsInt())) {
                Log.i(TAG, "skipped restoring notification with id " + pin.getId());
                continue;
            }

            // create a notification from the object and finally restore it
            NotificationTools.notify(context, pin);
        }
    }

    /**
     * Get active notifications on API 23 and later.
     * @return Null on API 22 and earlier, otherwise a map with notification ids as keys and info about the notifications as values.
     * @see <a href="https://stackoverflow.com/questions/23831214/notificationmanager-get-notification-by-id/47345498#47345498">android - notificationManager get notification by Id - Stack Overflow</a>
     */
    @Nullable
    private static Map<Integer, StatusBarNotification> getActiveNotifications(@NonNull Context context) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return null;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            return null;
        }
        StatusBarNotification[] barNotifications = notificationManager.getActiveNotifications();

        Map<Integer, StatusBarNotification> notificationMap = new HashMap<>();
        for(StatusBarNotification notification: barNotifications) {
            notificationMap.put(notification.getId(), notification);
        }

        return notificationMap;
    }

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

        return new NotificationChannel(NotificationChannel.DEFAULT_CHANNEL_ID, CHANNEL_NAME_PUBLIC, importance);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void createOrUpdateNotificationChannels(@NonNull Context context, @NonNull NotificationManager notificationManager) {
        // Use low importance in order to not make a sound when creating a notification.
        // If this is too low then the user should be able to manually change channel settings, so this seems like a sensible default.
        // See: https://developer.android.com/develop/ui/views/notifications/channels#importance
        final int importance = NotificationManager.IMPORTANCE_LOW;

        // Delete old channel used in version 2.2.0 and earlier:
        notificationManager.deleteNotificationChannel(CHANNEL_NAME_OLD);

        // Create one channel per visibility level to allow user to customize how they are shown on the lock screen:
        NotificationChannel public_channel = new NotificationChannel(CHANNEL_NAME_PUBLIC,
                context.getResources().getString(R.string.notifications_channel_public),
                importance);
        public_channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        public_channel.setShowBadge(false);
        public_channel.enableLights(false);
        public_channel.enableVibration(false);
        notificationManager.createNotificationChannel(public_channel);

        NotificationChannel private_channel = new NotificationChannel(CHANNEL_NAME_PRIVATE,
                context.getResources().getString(R.string.notifications_channel_private),
                importance);
        private_channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        private_channel.setShowBadge(false);
        private_channel.enableLights(false);
        private_channel.enableVibration(false);
        notificationManager.createNotificationChannel(private_channel);

        NotificationChannel secret_channel = new NotificationChannel(CHANNEL_NAME_SECRET,
                context.getResources().getString(R.string.notifications_channel_secret),
                importance);
        secret_channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        secret_channel.setShowBadge(false);
        secret_channel.enableLights(false);
        secret_channel.enableVibration(false);
        notificationManager.createNotificationChannel(secret_channel);
    }

    private static String getChannelName(@NonNull PinSpec pin) {
        switch (pin.getVisibility()) {
            case NotificationCompat.VISIBILITY_PUBLIC:
                return CHANNEL_NAME_PUBLIC;
            case NotificationCompat.VISIBILITY_PRIVATE:
                return CHANNEL_NAME_PRIVATE;
            case NotificationCompat.VISIBILITY_SECRET:
                return CHANNEL_NAME_SECRET;
            default:
                throw new RuntimeException("Unknown visibility value");
        }
    }

    private static CharSequence styledText(CharSequence text, StyleSpan style) {
        // https://stackoverflow.com/questions/70698860/how-to-bold-title-in-notification
        Spannable content = new SpannableString(text);
        content.setSpan(style, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return content;
    }

    public static void notify(@NonNull Context context, @NonNull PinSpec pin) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channel_id = getChannelName(pin);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channel_id)
                        .setContentTitle(pin.getTitle())
                        .setContentText(pin.getContent())
                        .setSmallIcon(R.drawable.ic_notif_star)
                        .setOnlyAlertOnce(true)
                        .setSilent(true)
                        .setCategory(NotificationCompat.CATEGORY_REMINDER)
                        .setPriority(pin.getPriority())
                        .setVisibility(pin.getVisibility())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(pin.getContent()))
                        .setContentIntent(getPinIntent(context, pin))

                        .setDeleteIntent(PendingIntent.getBroadcast(context, (int) pin.getId(),
                                new Intent(context, OnDeleteReceiver.class).setAction("notification_cancelled")
                                        .putExtra(EXTRA_INTENT, pin), PendingIntent.FLAG_CANCEL_CURRENT | FLAG_IMMUTABLE))
                        .setOngoing(pin.isPersistent());

        if (pin.getVisibility() == NotificationCompat.VISIBILITY_PRIVATE && !pin.getContent().isEmpty()) {
            // If visibility is hidden then an alternative notification can be shown on the lock screen:
            // More info: https://developer.android.com/develop/ui/views/notifications/build-notification#lockscreenNotification
            // More info: https://gabrieltanner.org/blog/android-notifications-overview/

            // Show "Contents hidden" placeholder as italic:
            // https://stackoverflow.com/questions/70698860/how-to-bold-title-in-notification
            CharSequence hiddenContent = styledText(
                    context.getResources().getText(R.string.message_hidden_private_content),
                    new StyleSpan(Typeface.ITALIC)
            );

            NotificationCompat.Builder publicBuilder = new NotificationCompat.Builder(context, channel_id)
                    .setContentTitle(pin.getTitle())
                    .setContentText(hiddenContent)
                    .setContentTitle(pin.getTitle())
                    .setSmallIcon(R.drawable.ic_notif_star)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            builder.setPublicVersion(publicBuilder.build());
        }

        if (pin.isShowActions()) {
            builder.addAction(R.drawable.ic_action_clip,
                    context.getString(R.string.message_save_to_clipboard),
                    PendingIntent.getBroadcast(context, (int) pin.getId(),
                            new Intent(context, OnClipReceiver.class).putExtra(EXTRA_INTENT, pin),
                            PendingIntent.FLAG_CANCEL_CURRENT | FLAG_IMMUTABLE));
        }

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createOrUpdateNotificationChannels(context, notificationManager);
            }

            Log.i(TAG, "Send notification with pin id " + pin.getIdAsInt() + " to system");
            Notification notification = builder.build();

            notificationManager.notify(pin.getIdAsInt(), notification);
        } else {
            Log.w(TAG, "NotificationManager is null! Couldn't send notification!");
        }
    }
}
