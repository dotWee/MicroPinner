package de.dotwee.micropinner.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Lukas on 26.06.2015.
 */
public class OnDeleteReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnDeleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        PinHandler.Pin pin = (PinHandler.Pin) intent.getSerializableExtra(PinHandler.Pin.EXTRA_INTENT);
        new PinHandler(context).removePin(pin);
    }
}
