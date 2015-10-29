package de.dotwee.micropinner.receiver;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.tools.PinHandler;

/**
 * Created by Lukas Wolfsteiner on 08.10.2015.
 */
public class OnClipReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnClipReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        PinHandler.Pin pin = (PinHandler.Pin)
                intent.getSerializableExtra(PinHandler.Pin.EXTRA_INTENT);

        if (pin != null) {
            Log.i(LOG_TAG, "Received clipIntent from pin " + pin.getId());

            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(null, pin.toClipString()));

            Toast.makeText(context, context.getString(R.string.message_clipped_pin), Toast.LENGTH_SHORT).show();
        } else
            Log.w(LOG_TAG, "Intent did not contain a pin as serialized extra! " + intent.toString());
    }
}
