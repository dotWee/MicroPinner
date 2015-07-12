package de.dotwee.micropinner.tools;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.dotwee.micropinner.ui.MainActivity;

/**
 * Created by Lukas on 26.06.2015.
 */
public class JsonHandler {
    public final static String ARRAY_KEY = "pins";
    private final static String LOG_TAG = "JsonHandler";
    private SharedPreferences sharedPreferences;
    private Context context;

    public JsonHandler(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public JSONObject generate(String title, String content, int visibility, int priority, boolean persistent, int notification_id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(MainActivity.EXTRA_TITLE, title);
            jsonObject.put(MainActivity.EXTRA_CONTENT, content);
            jsonObject.put(MainActivity.EXTRA_VISIBILITY, visibility);
            jsonObject.put(MainActivity.EXTRA_PRIORITY, priority);

            jsonObject.put(MainActivity.EXTRA_PERSISTENT, persistent);
            jsonObject.put(MainActivity.EXTRA_NOTIFICATION, notification_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void restore() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        JSONArray jsonArray = get();

        if (jsonArray != null)
            try {
            if (jsonArray.length() != 0) for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getString(MainActivity.EXTRA_TITLE), content = jsonObject.getString(MainActivity.EXTRA_CONTENT);
                int visibility = jsonObject.getInt(MainActivity.EXTRA_VISIBILITY),
                        priority = jsonObject.getInt(MainActivity.EXTRA_PRIORITY),
                        notification_id = jsonObject.getInt(MainActivity.EXTRA_NOTIFICATION);

                boolean persistent = jsonObject.getBoolean(MainActivity.EXTRA_PERSISTENT);

                notificationManager.notify(notification_id, MainActivity.generatePin(context, visibility, priority, notification_id, title, content, persistent));
                Log.i(LOG_TAG, "restore / Restored " + jsonObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray get() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String s = sharedPreferences.getString(ARRAY_KEY, null);
        if (s == null) s = "[]";

        Log.i(LOG_TAG, "get / Return: " + s);

        try {
            return new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "get / Error while creating JSONArray. Returning null.");
            return null;
        }
    }

    private void write(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.toString().isEmpty()) {
            sharedPreferences.edit().putString(ARRAY_KEY, "{}").commit();
            Log.i(LOG_TAG, "write / Writing empty array.");
        }
        else {
            sharedPreferences.edit().putString(ARRAY_KEY, jsonArray.toString()).commit();
            Log.i(LOG_TAG, "write / New array: " + jsonArray.toString());
        }
    }

    public void append(String title, String content, int visibility, int priority, boolean persistent, int notification_id) {
        JSONObject jsonObject = generate(title, content, visibility, priority, persistent, notification_id);
        append(jsonObject);
    }

    public void append(JSONObject jsonObject) {
        JSONArray jsonArray = get();
        if (jsonArray != null) jsonArray.put(jsonObject);
        write(jsonArray);
    }

    public void edit(String title, String content, int visibility, int priority, boolean persistent, int notification_id) {
        JSONObject jsonObject = generate(title, content, visibility, priority, persistent, notification_id);
        remove(notification_id);
        append(jsonObject);
    }

    public void remove(int notification_id) {
        Log.i(LOG_TAG, "remove / Remove " + notification_id);
        JSONArray jsonArray = get(), newArray = new JSONArray();

        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.i(LOG_TAG, "remove / Checking " + i);

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getInt(MainActivity.EXTRA_NOTIFICATION) == notification_id)
                        Log.i(LOG_TAG, "remove / Skipping " + notification_id);
                    else newArray.put(jsonObject);
                }
            } else Log.i(LOG_TAG, "remove / Empty json array.");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        write(newArray);
    }
}
