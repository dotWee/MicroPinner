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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.dotwee.micropinner.R;
import de.dotwee.micropinner.ui.EditActivity;

/**
 * Created by lukas on 18.08.2015 - 16:33
 * for project MicroPinner.
 */
public class PinHandler {
    private final static String LOG_TAG = "PinHandler";
    private NotificationManager notificationManager;
    private SharedPreferences preferences;
    private Context context;

    public PinHandler(Context context) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public void persistPin(Pin pin) {
        PendingIntent pinIntent = pin.toIntent(context);
        Notification pinNotification = pin.toNotification(context, pinIntent);
        notificationManager.notify(pin.getId(), pinNotification);

        preferences.edit().putString("pin_" + pin.getId(), pin.toString()).apply();
        addToIndex(pin.getId());
    }

    public void removePin(Pin pin) {
        removeFromIndex(pin.getId());
    }

    private void addToIndex(int id) {
        String index = preferences.getString("index", null);
        if (index == null) index = String.valueOf(id);
        else index = index + "," + id;

        preferences.edit().putString("index", index).apply();
    }

    private void removeFromIndex(int id) {
        Iterator<Integer> ids = getIndex().iterator();
        StringBuilder newIndex = new StringBuilder();


        while (ids.hasNext())
            if (ids.next() == id) ids.remove();
            else newIndex.append(",").append(ids.next());

        preferences.edit().putString("index", newIndex.toString().substring(1)).apply();
    }

    public Map<Integer, Pin> getPins() {
        Map<Integer, Pin> pinMap = new HashMap<>();
        List<Integer> ids = getIndex();

        if (!ids.isEmpty())
            for (int id : ids)
                pinMap.put(id, getPin(id));

        return pinMap;
    }

    private List<Integer> getIndex() {
        String index = preferences.getString("index", null);
        List<Integer> ids = new ArrayList<>();

        if (index != null && !index.isEmpty()) {
            String[] plainIds = index.split(",");

            for (String id : plainIds)
                ids.add(Integer.parseInt(id));
        }

        return ids;
    }

    private Pin getPin(int id) {
        String key = "pin_" + id;
        Pin pin = null;

        String serializedPin = preferences.getString(key, null);
        if (serializedPin != null) {
            byte[] pinData = Base64.decode(serializedPin, Base64.DEFAULT);
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(pinData));
                pin = (Pin) objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return pin;
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

        public Pin(int visibility, int priority, int id, String title, String content, boolean persistent) {
            this.visibility = visibility;
            this.priority = priority;
            this.id = id;
            this.title = title;
            this.content = content;
            this.persistent = persistent;
        }

        public int getVisibility() {
            return visibility;
        }

        public void setVisibility(int visibility) {
            this.visibility = visibility;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
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

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isPersistent() {
            return persistent;
        }

        public void setPersistent(boolean persistent) {
            this.persistent = persistent;
        }

        public PendingIntent toIntent(Context context) {
            Intent resultIntent = new Intent(context, EditActivity.class);
            resultIntent.putExtra(EXTRA_INTENT, this);

            return PendingIntent.getActivity(context, id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
