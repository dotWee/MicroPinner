package de.dotwee.micropinner.tools;

import android.app.Notification;
import android.content.res.Resources;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import de.dotwee.micropinner.R;

/**
 * Created by Lukas Wolfsteiner on 17.10.2015.
 */
public class SpinnerTools {
    private static final String LOG_TAG = "SpinnerTools";
    private static final int UNSUPPORTED = ArrayAdapter.NO_SELECTION;

    public static void setVisibilityAdapter(Resources resources, Spinner spinner) {
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(
                spinner.getContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.array_visibilities)
        );

        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(visibilityAdapter);
    }

    public static int getVisibilityResource(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) return UNSUPPORTED;

        switch (position) {
            case 0:
                return Notification.VISIBILITY_PUBLIC;

            case 1:
                return Notification.VISIBILITY_PRIVATE;

            case 2:
                return Notification.VISIBILITY_SECRET;

            default:
                return 0;
        }
    }

    public static int getVisibilityPosition(int visibility) {
        switch (visibility) {
            case Notification.VISIBILITY_PUBLIC:
                return 0;

            case Notification.VISIBILITY_PRIVATE:
                return 1;

            case Notification.VISIBILITY_SECRET:
                return 2;

            default:
                return 0;
        }
    }

    public static void setPriorityAdapter(Resources resources, Spinner spinner) {
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                spinner.getContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.array_priorities)
        );

        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(priorityAdapter);
    }

    public static int getPriorityResource(int position) {
        switch (position) {
            case 0:
                return Notification.PRIORITY_DEFAULT;

            case 1:
                return Notification.PRIORITY_MIN;

            case 2:
                return Notification.PRIORITY_LOW;

            case 3:
                return Notification.PRIORITY_HIGH;

            default:
                return 0;
        }
    }

    public static int getPriorityPosition(int priority) {
        switch (priority) {
            case Notification.PRIORITY_DEFAULT:
                return 0;

            case Notification.PRIORITY_MIN:
                return 1;

            case Notification.PRIORITY_LOW:
                return 2;

            case Notification.PRIORITY_HIGH:
                return 3;

            default:
                return 0;
        }
    }
}
