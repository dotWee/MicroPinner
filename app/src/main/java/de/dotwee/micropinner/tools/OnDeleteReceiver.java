package de.dotwee.micropinner.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.dotwee.micropinner.ui.MainActivity;

/**
 * Created by Lukas on 26.06.2015.
 */
public class OnDeleteReceiver extends BroadcastReceiver {
    private final static String LOG_TAG = "OnDeleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int i = intent.getIntExtra(MainActivity.EXTRA_NOTIFICATION, -1);
        new JsonHandler(context).remove(i);
    }
}
