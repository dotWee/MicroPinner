package de.dotwee.micropinner.receiver;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.database.PinSpec;
import de.dotwee.micropinner.tools.NotificationTools;

/**
 * Created by Lukas Wolfsteiner on 08.10.2015.
 * <p>
 * This class represents a broadcast receiver for
 * {@link android.app.Notification} OnAction intents.
 * <p>
 * Intents should contain a serialized {@link PinSpec}
 * as extra.
 * <p>
 * If yes, the onReceive method will copy the serialized
 * pin-content to the clipboard and notify the user with
 * a {@link Toast}.
 */
public class OnClipReceiver extends BroadcastReceiver {
    private final static String TAG = OnClipReceiver.class.getSimpleName();

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {

        PinSpec pin = (PinSpec) intent.getSerializableExtra(NotificationTools.EXTRA_INTENT);

        if (pin != null) {
            Log.i(TAG, "Received clipIntent from pin " + pin.getId());

            ClipboardManager clipboard =
                    (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            //noinspection ConstantConditions
            clipboard.setPrimaryClip(ClipData.newPlainText(null, pin.toClipString()));

            Toast.makeText(context, context.getString(R.string.message_clipped_pin), Toast.LENGTH_SHORT)
                    .show();
        } else {
            throw new IllegalArgumentException(
                    "Intent did not contain a pin as serialized extra! " + intent.toString());
        }
    }
}
