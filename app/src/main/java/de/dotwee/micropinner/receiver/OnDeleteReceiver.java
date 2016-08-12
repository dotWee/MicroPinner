package de.dotwee.micropinner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import de.dotwee.micropinner.database.PinDatabase;
import de.dotwee.micropinner.database.PinSpec;
import de.dotwee.micropinner.tools.NotificationTools;

/**
 * Created by Lukas on 26.06.2015.
 * <p>
 * This class is a broadcast receiver for {@link android.app.Notification}
 * DeleteIntents.
 * <p>
 * Intents should contain a serialized pin as extra.
 * If yes, tell the {@link PinDatabase} to delete the pin
 */
public class OnDeleteReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnDeleteReceiver";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {

        // deserialize our pin from the intent
        PinSpec pin = (PinSpec) intent.getSerializableExtra(NotificationTools.EXTRA_INTENT);

        if (pin != null) {
            Log.i(LOG_TAG, "Received deleteIntent from pin " + pin.getId());

            // and tell the pin handler to remove it from the index
            PinDatabase.getInstance(context).deletePin(pin);
        } else {
            throw new IllegalArgumentException(
                    "Intent did not contain a pin as serialized extra! " + intent.toString());
        }
    }
}
