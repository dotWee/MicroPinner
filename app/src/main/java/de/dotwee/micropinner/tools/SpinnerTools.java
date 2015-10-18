package de.dotwee.micropinner.tools;

import android.content.res.Resources;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import de.dotwee.micropinner.R;

/**
 * Created by Lukas Wolfsteiner on 17.10.2015.
 */
public class SpinnerTools {
    private static final String LOG_TAG = "SpinnerTools";

    public static void setVisibilityAdapter(Resources resources, Spinner spinner) {
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(
                spinner.getContext(),
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.array_visibilities)
        );

        visibilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(visibilityAdapter);
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
}
