package de.dotwee.micropinner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.dotwee.micropinner.tools.NotificationTools;

/**
 * This receiver is used when the app is updated to ensure notifications are restored immediately.
 *
 * @see <a href="https://stackoverflow.com/questions/26475721/push-notification-after-app-was-updated">android - Push Notification After App Was Updated - Stack Overflow</a>
 */
public class OnUpdateReceiver extends BroadcastReceiver {
    private final static String TAG = OnBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(@NonNull Context context, @Nullable Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.w(TAG,
                    "Intent (and its action) must be not null to work with it, returning without work");
            return;
        }

        if (!intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
            Log.w(TAG, "OnUpdateReceiver's intent actions is not "
                    + Intent.ACTION_MY_PACKAGE_REPLACED
                    + ", returning without work");
            return;
        }

        NotificationTools.restoreNotifications(context);
    }
}