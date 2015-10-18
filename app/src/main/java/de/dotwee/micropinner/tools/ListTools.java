package de.dotwee.micropinner.tools;

import de.dotwee.micropinner.R;

/**
 * Created by Lukas Wolfsteiner on 17.10.2015.
 */
public class ListTools {
    private static final String LOG_TAG = "ListTools";

    public static int[] getAdvancedViewIds() {
        return new int[]{
                R.id.checkBoxPersistentPin,
                R.id.checkBoxNewPin
        };
    }

    public static int[] getClickableViewIds() {
        return new int[]{
                R.id.checkBoxPersistentPin,
                R.id.checkBoxNewPin,

                R.id.switchAdvanced,
                R.id.buttonCancel,
                R.id.buttonPin
        };
    }
}
