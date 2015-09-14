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
import de.dotwee.micropinner.receiver.OnDeleteReceiver;
import de.dotwee.micropinner.ui.MainActivity;

/**
 * Created by lukas on 18.08.2015 - 16:33
 * for project MicroPinner.
 */
public class PinHandler {
    private final static String LOG_TAG = "PinHandler";
    private final static String BASE64_REGEX = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
    private NotificationManager notificationManager;
    private SharedPreferences preferences;
    private Context context;

    /**
     * Default constructor
     *
     * @param context
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
        return string != null && string.matches(BASE64_REGEX);
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
     * Delete a {@param pin} from preferences and index
     */
    public void removePin(Pin pin) {
        removeFromPreferences(pin);
        removeFromIndex(pin);
    }

    /**
     * Add a {@param id}'s id to the index
     */
    private void addToIndex(int id) {
        List<Integer> ids = getIndex();
        ids.add(id);

        this.writeIndex(ids);
    }

    /**
     * Removes a {@param pin} from the index
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
     * @return all pins in a key-map
     */
    public Map<Integer, Pin> getPins() {
        Map<Integer, Pin> pinMap = new HashMap<>();
        List<Integer> ids = getIndex();

        if (!ids.isEmpty())
            for (int id : ids)
                pinMap.put(id, getPin(id));

        return pinMap;
    }

    /**
     * Serializes the {@param index} and writes it to shared preferences
     * @return true on success, false on error
     */
    private boolean writeIndex(List<Integer> index) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String serializedIndex = "";

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(index);
            objectOutputStream.close();

            serializedIndex = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        preferences.edit().putString("index", serializedIndex).apply();
        return true;
    }

    /**
     * @return the current index of pins
     */
    private List<Integer> getIndex() throws IllegalStateException {
        String serializedIndex = preferences.getString("index", null);
        List<Integer> index = new ArrayList<>();

        if (serializedIndex != null && isValidBase64(serializedIndex)) {
            byte[] indexData = Base64.decode(serializedIndex, Base64.DEFAULT);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(indexData));
                index = (List<Integer>) objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            return index;
        } else throw new IllegalStateException("Serialized pin is not valid base64!");
    }

    /**
     * @return a pin by its {@param id}
     */
    private Pin getPin(int id) throws IllegalStateException {
        String key = "pin_" + id;
        Pin pin = null;

        String serializedPin = preferences.getString(key, null);
        if (serializedPin != null && isValidBase64(serializedPin)) {
            byte[] pinData = Base64.decode(serializedPin, Base64.DEFAULT);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(pinData));
                pin = (Pin) objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            return pin;
        } else throw new IllegalStateException("Serialized pin is not valid base64!");
    }

    /**
     * Created by lukas on 18.08.2015 - 16:48
     * for project MicroPinner.
     */
    public static class Pin implements Serializable {
        public final static String EXTRA_INTENT = "IAMAPIN";
        private final static String LOG_TAG = "Pin";

        int visibility = 1;
        int priority = 0;
        int id = 0;

        String title = "";
        String content = "";

        boolean persistent = false;

        public Pin(int visibility, int priority, String title, String content, boolean persistent) {
            this.id = new Random().nextInt(Integer.MAX_VALUE - 2) + 1;

            this.visibility = visibility;
            this.priority = priority;
            this.title = title;
            this.content = content;
            this.persistent = persistent;
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

        @SuppressWarnings("ResourceType")
        public Notification toNotification(Context context, PendingIntent contentIntent) {
            Notification.Builder notification = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_star_24dp)
                    .setPriority(priority)
                    .setDeleteIntent(PendingIntent.getBroadcast(context, id, new Intent(context, OnDeleteReceiver.class).setAction("notification_cancelled").putExtra(Pin.EXTRA_INTENT, this), PendingIntent.FLAG_CANCEL_CURRENT))
                    .setOngoing(persistent);

            if (Build.VERSION.SDK_INT >= 21) {
                notification.setVisibility(visibility);
            }

            notification.setContentIntent(contentIntent);
            return notification.build();
        }

        @Override
        public String toString() {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(this);
                objectOutputStream.close();

                return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
