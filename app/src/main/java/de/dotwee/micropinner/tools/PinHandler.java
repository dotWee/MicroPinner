package de.dotwee.micropinner.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.receiver.OnClipReceiver;
import de.dotwee.micropinner.receiver.OnDeleteReceiver;
import de.dotwee.micropinner.view.MainActivity;

/**
 * Created by lukas on 18.08.2015 - 16:33
 * for project MicroPinner.
 */
public class PinHandler {
    private final static String LOG_TAG = "PinHandler";
    /**
     * Encoder flag bit to omit all line terminators (i.e., the output
     * will be on one long line).
     */
    private final static int BASE64_DEFAULT_FLAG = Base64.NO_WRAP;
    private final NotificationManager notificationManager;
    private final SharedPreferences preferences;
    private final Context context;

    /**
     * Default constructor
     *
     * @param context needed to get access to {@link SharedPreferences} and {@link NotificationManager}
     */
    public PinHandler(@NonNull Context context) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    /**
     * This method checks if a string matches the official base64 regular expression.
     *
     * @param string to check
     * @return true if valid base64, false is invalid or null
     */
    private static boolean isValidBase64(@NonNull String string) {
        final String BASE64_REGEX = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})$";
        return string.matches(BASE64_REGEX);
    }

    /**
     * This method decodes a object from a base64 string.
     *
     * @param serializedObject the object to deserialize
     * @return a unserialized object
     * @throws IllegalArgumentException
     */
    @NonNull
    private static Object deserialize(@NonNull String serializedObject) throws IllegalArgumentException {
        ObjectInputStream objectInputStream;
        Object object = new Object();

        // remove all line separators
        serializedObject = serializedObject.replaceAll("\\r\\n|\\r|\\n", "");

        if (isValidBase64(serializedObject)) {
            byte[] rawData = Base64.decode(serializedObject, BASE64_DEFAULT_FLAG);

            try {
                objectInputStream = new ObjectInputStream(new ByteArrayInputStream(rawData));
                object = objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else
            throw new IllegalArgumentException("Input is not valid base64! \nInput: " + serializedObject);

        return object;
    }

    /**
     * Persist and display a {@param pin} to shared-preferences
     * and notification bar
     */
    public void persistPin(@NonNull Pin pin) {
        PendingIntent pinIntent = pin.toIntent(context);
        Notification pinNotification = pin.toNotification(context, pinIntent);
        notificationManager.notify(pin.getId(), pinNotification);

        preferences.edit().putString(pin.getName(), pin.toString()).apply();
        addToIndex(pin.getId());
    }

    /**
     * This method calls methods to delete a pin from shared preferences and remove it from the index.
     *
     * @param pin to delete / remove
     */
    public void removePin(@NonNull Pin pin) {
        removeFromPreferences(pin);
        removeFromIndex(pin);
    }

    /**
     * This method adds a pin-id to the list of pins.
     *
     * @param id to add to index
     */
    private void addToIndex(int id) {
        List<Integer> ids = getIndex();
        ids.add(id);

        this.writeIndex(ids);
    }

    /**
     * This method deletes all persisted pins.
     */
    public void removeAllPins() {
        removeAllFromIndex();
        removeAllFromPreferences();
    }

    /**
     * This method looks for pin-keys in the preferences and removes them all
     */
    private void removeAllFromPreferences() {
        Map<String, ?> allKeys = preferences.getAll();

        for (Map.Entry<String, ?> entry : allKeys.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("pin_")) {
                preferences.edit().remove(key).apply();
            }
        }
    }

    /**
     * This method resets the index, which removes access to previous pins
     */
    private void removeAllFromIndex() {
        this.writeIndex(new ArrayList<Integer>());
    }

    /**
     * This method removes a pin from the index.
     *
     * @param pin to remove
     */
    private void removeFromIndex(@NonNull Pin pin) {
        List<Integer> oldIndex = getIndex(), newIndex = new ArrayList<>();

        for (Integer id : oldIndex)
            if (id != pin.getId()) newIndex.add(id);

        this.writeIndex(newIndex);
    }

    /**
     * Deleted a {@param pin} from the shared preferences
     */
    private void removeFromPreferences(@NonNull Pin pin) {
        preferences.edit().remove(pin.getName()).apply();
    }

    /**
     * This method returns a map of all pins
     *
     * @return a key-map with all pins
     */
    @NonNull
    public Map<Integer, Pin> getPins() {
        Map<Integer, Pin> pinMap = new HashMap<>();
        List<Integer> ids = getIndex();

        if (!ids.isEmpty())
            for (int id : ids)
                try {
                    pinMap.put(id, getPin(id));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(LOG_TAG, "Pin with id=" + id + " does not exist. Skipping...");
                }

        return pinMap;
    }

    /**
     * This method serializes the {@param index} and writes it to shared preferences
     */
    private void writeIndex(@NonNull List<Integer> index) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String serializedIndex;

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(index);
            objectOutputStream.close();

            serializedIndex = Base64.encodeToString(byteArrayOutputStream.toByteArray(), BASE64_DEFAULT_FLAG);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        preferences.edit().putString("index", serializedIndex).apply();
    }

    /**
     * This method returns the current index of pin ids
     *
     * @return the current index of pins
     */
    @NonNull
    private List<Integer> getIndex() throws IllegalStateException {
        String serializedIndex = preferences.getString("index", null);
        List<Integer> list = new ArrayList<>();
        if (serializedIndex != null) {
            try {
                Object object = deserialize(serializedIndex);

                if (object instanceof List<?>)
                    list = (List<Integer>) object;

                else throw new IllegalStateException("Object is not a instance of List!");
            } catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();

                Log.w(LOG_TAG, "Couldn't deserialize the index. Returning empty list.");
            }
        }

        return list;
    }

    /**
     * This method tries to read a pin from shared preferences by its id
     *
     * @param id of the pin to return
     * @return the specific pin
     * @throws IllegalStateException is pin does not exist or is not actually a pin
     */
    @NonNull
    private Pin getPin(int id) throws IllegalStateException {

        String serializedPin = preferences.getString(Pin.getName(id), null);
        if (serializedPin != null) {
            Object object = deserialize(serializedPin);

            if (object instanceof Pin) {
                Log.i(LOG_TAG, "Successfully deserialized pin " + ((Pin) object).getId());
                return (Pin) object;

            } else throw new IllegalStateException("Deserialize object is not a instance of Pin!");

        } else throw new IllegalArgumentException("Pin does not exist.");
    }

    /**
     * Created by lukas on 18.08.2015 - 16:48
     * for project MicroPinner.
     */
    public static class Pin implements Serializable {
        public final static String EXTRA_INTENT = "IAMAPIN";
        private final static String LOG_TAG = "Pin";

        /* default visibility */
        int visibility = 1;

        /* default priority */
        int priority = 0;

        /* default pin id */
        int id = 0;

        String title = "";

        String content = "";

        boolean persistent = false;

        boolean showActions = false;

        public Pin() {

        }

        public Pin(int visibility, int priority, @NonNull String title, @NonNull String content,
                   boolean persistent, boolean showActions) {

            this.id = new Random().nextInt(Integer.MAX_VALUE - 2) + 1;

            this.visibility = visibility;
            this.priority = priority;
            this.title = title;
            this.content = content;
            this.persistent = persistent;
            this.showActions = showActions;
        }

        @NonNull
        public static String getName(int id) {
            Pin pin = new Pin();
            pin.setId(id);

            return pin.getName();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @NonNull
        public String getTitle() {
            return title;
        }

        public void setTitle(@NonNull String title) {
            this.title = title;
        }

        @NonNull
        public String getContent() {
            return content;
        }

        public int getVisibility() {
            return visibility;
        }

        public int getPriority() {
            return priority;
        }

        public boolean isPersistent() {
            return persistent;
        }

        public boolean showActions() {
            return showActions;
        }

        @NonNull
        public PendingIntent toIntent(@NonNull Context context) {
            Intent resultIntent = new Intent(context, MainActivity.class);
            resultIntent.putExtra(EXTRA_INTENT, this);

            return PendingIntent.getActivity(context, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @NonNull
        public String getName() {
            return "pin_" + id;
        }

        @NonNull
        public Notification toNotification(@NonNull Context context, @NonNull PendingIntent contentIntent) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_notif_star)
                    .setPriority(priority)
                    .setVisibility(visibility)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setContentIntent(contentIntent)

                    .setDeleteIntent(PendingIntent.getBroadcast(context, id, new Intent(context, OnDeleteReceiver.class).setAction("notification_cancelled").putExtra(Pin.EXTRA_INTENT, this), PendingIntent.FLAG_CANCEL_CURRENT))
                    .setOngoing(persistent);

            if (showActions()) {
                builder.addAction(
                        R.drawable.ic_action_clip,
                        context.getString(R.string.message_save_to_clipboard),
                        PendingIntent.getBroadcast(context, (id + 1), new Intent(context, OnClipReceiver.class).putExtra(Pin.EXTRA_INTENT, this), PendingIntent.FLAG_CANCEL_CURRENT)
                );
            }

            return builder.build();
        }

        @NonNull
        public String toClipString() {
            if (content != null && !content.isEmpty())
                return title + " - " + content;

            else return title;
        }

        @Nullable
        @Override
        public String toString() {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(this);
                objectOutputStream.close();

                return Base64.encodeToString(byteArrayOutputStream.toByteArray(), BASE64_DEFAULT_FLAG);
            } catch (IOException e) {
                e.printStackTrace();

                Log.w(LOG_TAG, "Couldn't encode object to base64!");
            }

            return null;
        }
    }
}
