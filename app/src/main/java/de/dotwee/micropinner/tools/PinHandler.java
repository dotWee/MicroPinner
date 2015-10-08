package de.dotwee.micropinner.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
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
import de.dotwee.micropinner.ui.MainActivity;

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
    public PinHandler(Context context) {
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
    private static boolean isValidBase64(String string) {
        final String BASE64_REGEX = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})$";
        if (string != null) if (string.matches(BASE64_REGEX)) return true;

        return false;
    }

    /**
     * This method decodes a object from a base64 string.
     *
     * @param serializedObject the object to deserialize
     * @return a deserialized object
     * @throws IllegalArgumentException
     */
    private static Object deserialize(String serializedObject) throws IllegalArgumentException {
        ObjectInputStream objectInputStream;
        Object object = new Object();

        if (serializedObject != null) {

            // remove all line seperators
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

        } else throw new IllegalArgumentException("Input is null.");
        return object;
    }

    /**
     * Persist and display a {@param pin} to shared-preferences
     * and notification bar
     */
    public void persistPin(Pin pin) {
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
    public void removePin(Pin pin) {
        removeFromPreferences(pin);
        removeFromIndex(pin);
    }

    /**
     * This method add a pin-id to the list of undeleted pins.
     *
     * @param id to add to index
     */
    private void addToIndex(int id) {
        List<Integer> ids = getIndex();
        ids.add(id);

        this.writeIndex(ids);
    }

    /**
     * This method removes a pin from the index.
     *
     * @param pin to remove
     */
    private void removeFromIndex(Pin pin) {
        List<Integer> oldIndex = getIndex(), newIndex = new ArrayList<>();

        for (Integer id : oldIndex)
            if (id != pin.getId()) newIndex.add(id);

        this.writeIndex(newIndex);
    }

    /**
     * Deleted a {@param pin} from the shared preferences
     */
    private void removeFromPreferences(Pin pin) {
        preferences.edit().remove(pin.getName()).apply();
    }

    /**
     * This method returns a map of all pins
     *
     * @return a key-map with all pins
     */
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
    private void writeIndex(List<Integer> index) {
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
    private List<Integer> getIndex() throws IllegalStateException {
        String serializedIndex = preferences.getString("index", null);
        List<Integer> list = new ArrayList<>();
        try {
            Object object = deserialize(serializedIndex);

            if (object instanceof List<?>)
                list = (List<Integer>) object;

            else throw new IllegalStateException("Deserialized object is not a instance of List!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            e.printStackTrace();

            Log.w(LOG_TAG, "Couldn't deserialize the index. Returning emply list.");
        }

        return list;
    }

    /**
     * This method tries to read a pin from shared preferences by its id
     *
     * @param id of the pin to return
     * @return the specific pin
     * @throws Exception is pin does not exist or is not actually a pin
     */
    private Pin getPin(int id) throws Exception {

        String serializedPin = preferences.getString(Pin.getName(id), null);
        if (serializedPin != null) {
            Object object = deserialize(serializedPin);

            if (object instanceof Pin) {
                Log.i(LOG_TAG, "Successfully deserialized pin " + ((Pin) object).getId());
                return (Pin) object;

            } else throw new IllegalStateException("Deserialized object is not a instance of Pin!");

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

        public Pin() {

        }

        public Pin(int visibility, int priority, String title, String content, boolean persistent) {
            this.id = new Random().nextInt(Integer.MAX_VALUE - 2) + 1;

            this.visibility = visibility;
            this.priority = priority;
            this.title = title;
            this.content = content;
            this.persistent = persistent;
        }

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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public boolean isPersistent() {
            return persistent;
        }

        public PendingIntent toIntent(Context context) {
            Intent resultIntent = new Intent(context, MainActivity.class);
            resultIntent.putExtra(EXTRA_INTENT, this);

            return PendingIntent.getActivity(context, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        public String getName() {
            return "pin_" + id;
        }

        public Notification toNotification(Context context, PendingIntent contentIntent) {
            //noinspection ResourceType
            Notification.Builder notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_notif_star)
                    .setPriority(priority)
                    .addAction(
                            R.drawable.ic_action_clip,
                            context.getString(R.string.message_save_to_clipboard),
                            PendingIntent.getBroadcast(context, (id + 1), new Intent(context, OnClipReceiver.class).putExtra(Pin.EXTRA_INTENT, this), PendingIntent.FLAG_CANCEL_CURRENT)
                    )
                    .setDeleteIntent(PendingIntent.getBroadcast(context, id, new Intent(context, OnDeleteReceiver.class).setAction("notification_cancelled").putExtra(Pin.EXTRA_INTENT, this), PendingIntent.FLAG_CANCEL_CURRENT))
                    .setOngoing(persistent);

            if (Build.VERSION.SDK_INT >= 21) {
                notification.setVisibility(visibility);
            }

            notification.setContentIntent(contentIntent);
            return notification.build();
        }

        public String toClipString() {
            if (content != null && !content.isEmpty())
                return title + " - " + content;

            else return title;
        }

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
