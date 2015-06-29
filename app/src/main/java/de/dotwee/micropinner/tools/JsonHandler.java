package de.dotwee.micropinner.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    private static JSONArray remove(final int idx, final JSONArray from) {
        final List<JSONObject> objs = asList(from);
        objs.remove(idx);

        final JSONArray ja = new JSONArray();
        for (final JSONObject obj : objs) {
            ja.put(obj);
        }

        return ja;
    }

    private static List<JSONObject> asList(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            if (obj != null) {
                result.add(obj);
            }
        }
        return result;
    }

    public JSONObject genPinObject(String title, String content, int visibility, int priority, boolean persistent, int notification_id) {
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

    public void restorePins() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            String s = PreferenceManager.getDefaultSharedPreferences(context).getString(ARRAY_KEY, null);
            if (s == null) s = "{}";

            JSONArray jsonArray = new JSONArray(s);
            if (jsonArray.length() != 0) for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String title = jsonObject.getString(MainActivity.EXTRA_TITLE), content = jsonObject.getString(MainActivity.EXTRA_CONTENT);
                int visibility = jsonObject.getInt(MainActivity.EXTRA_VISIBILITY), priority = jsonObject.getInt(MainActivity.EXTRA_PRIORITY), notification_id = jsonObject.getInt(MainActivity.EXTRA_NOTIFICATION);
                boolean persistent = jsonObject.getBoolean(MainActivity.EXTRA_PERSISTENT);

                Notification notification = MainActivity.generatePin(context, visibility, priority, notification_id, title, content, persistent);
                notificationManager.notify(notification_id, notification);
                Log.i(LOG_TAG, "restorePins / restored " + notification_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONArray getJsonArray() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String s = sharedPreferences.getString(ARRAY_KEY, null);
        if (s == null) {
            Log.i(LOG_TAG, "getJsonArray / Returning empty array.");
            s = "{}";
        } else {
            Log.i(LOG_TAG, "getJsonArray / Return: " + s);
        }
        try {
            return new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "getJsonArray / Error while creating JSONArray. Returning null.");
            return null;
        }
    }

    private void writeJsonArray(JSONArray jsonArray) {
        if (jsonArray == null) sharedPreferences.edit().putString(ARRAY_KEY, "{}").commit();
        else {
            sharedPreferences.edit().putString(ARRAY_KEY, jsonArray.toString()).commit();
            Log.i(LOG_TAG, "writeJsonArray / New array: " + jsonArray.toString());
        }
    }

    public void editJsonArray(JSONObject jsonObject) {
        JSONArray jsonArray = getJsonArray();
        if (jsonArray != null) jsonArray.put(jsonObject);
        writeJsonArray(jsonArray);
    }

    public void editJsonArray(int notification_id) {
        JSONArray jsonArray = getJsonArray();
        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.i(LOG_TAG, "editJsonArray / Checking " + i);
                    if (jsonArray.getJSONObject(i).getInt(MainActivity.EXTRA_NOTIFICATION) == notification_id) {
                        Log.i(LOG_TAG, "editJsonArray / Removing...");
                        jsonArray = remove(i, jsonArray);
                    }
                }
            } else Log.i(LOG_TAG, "editJsonArray / Empty json array.");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        writeJsonArray(jsonArray);
    }
}
