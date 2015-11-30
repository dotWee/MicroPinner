package de.dotwee.micropinner.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.dotwee.micropinner.tools.PinHandler;

/**
 * Created by Lukas on 26.06.2015.
 *
 * This class is a broadcast receiver for {@link android.app.Notification}
 * DeleteIntents.
 *
 * Intents should contain a serialized pin as extra.
 * If yes, tell the {@link PinHandler} to delete
 * and remove it from the index.
 */
public class OnDeleteReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnDeleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // deserialize our pin from the intent
        PinHandler.Pin pin = (PinHandler.Pin) intent.getSerializableExtra(PinHandler.Pin.EXTRA_INTENT);

        if (pin != null) {
            Log.i(LOG_TAG, "Received deleteIntent from pin " + pin.getId());

            // and tell the pin handler to remove it from the index
            new PinHandler(context).removePin(pin);

        } else
            throw new IllegalArgumentException("Intent did not contain a pin as serialized extra! " + intent.toString());
    }
}
