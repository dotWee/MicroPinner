package de.dotwee.micropinner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.dotwee.micropinner.tools.BadgeTools;

/**
 * Created by lukas on 10.08.2016.
 */
public class PinProvider {
    static final String TAG = "PinProvider";

    private static PinProvider instance = null;
    private SQLiteDatabase database;
    private PinHelper pinHelper;
    private Context context;
    private String[] columns = {
            PinHelper.COLUMN_ID,
            PinHelper.COLUMN_TITLE,
            PinHelper.COLUMN_CONTENT,
            PinHelper.COLUMN_VISIBILITY,
            PinHelper.COLUMN_PRIORITY,
            PinHelper.COLUMN_PERSISTENT,
            PinHelper.COLUMN_SHOW_ACTIONS
    };

    private PinProvider(Context context) {
        this.context = context;

        pinHelper = new PinHelper(context);
        open();
    }

    public static synchronized PinProvider getInstance(@NonNull Context applicationContext) {
        if (PinProvider.instance == null) {
            PinProvider.instance = new PinProvider(applicationContext);
        }

        return PinProvider.instance;
    }

    public void open() throws SQLException {
        database = pinHelper.getWritableDatabase();
    }

    public void close() {
        pinHelper.close();
    }

    /**
     * This method decides whether a new pin should be created or updated in the database
     *
     * @param pin the pin to write
     * @return the written pin
     */
    @NonNull
    public PinSpec writePin(@NonNull PinSpec pin) {
        if (pin.getId() == -1) {
            return createPin(pin);
        } else return updatePin(pin);
    }

    /**
     * This method creates a pin within the database and gives it a unique id
     *
     * @param pin the pin to create
     * @return the created pin with its id
     */
    @NonNull
    private PinSpec createPin(@NonNull PinSpec pin) {
        ContentValues contentValues = pin.toContentValues();

        long id = database.insert(PinHelper.TABLE_PINS, null, contentValues);
        Log.i(TAG, "Created new pin with id " + id);
        pin.setId(id);

        onDatabaseAction();
        return pin;
    }

    /**
     * This method updates a pin in the database without changing its id
     *
     * @param pin the pin to update
     * @return the updated pin
     */
    @NonNull
    private PinSpec updatePin(@NonNull PinSpec pin) {
        ContentValues contentValues = pin.toContentValues();

        long id = database.update(PinHelper.TABLE_PINS, contentValues, PinHelper.COLUMN_ID + " = " + pin.getId(), null);
        Log.i(TAG, "Updated new pin with id " + id);
        pin.setId(id);

        return pin;
    }

    /**
     * This method deletes a pin from the database
     *
     * @param pin to delete
     * @return deleted pin
     */
    @NonNull
    public PinSpec deletePin(PinSpec pin) {
        long id = pin.getId();

        Log.i(TAG, "Deleting pin with id " + id);
        database.delete(PinHelper.TABLE_PINS, PinHelper.COLUMN_ID + " = " + id, null);
        pin.setId(-1);

        onDatabaseAction();
        return pin;
    }

    public void deleteAll() {
        Log.i(TAG, "Deleting all pins");
        database.delete(PinHelper.TABLE_PINS, null, null);
    }

    /**
     * This method reads a pin from a cursor
     *
     * @param cursor to read from
     * @return the read pin
     */
    @NonNull
    public PinSpec fromCursor(@NonNull Cursor cursor) {
        return new PinSpec(cursor);
    }

    /**
     * This method returns the amount of entries in the pin database
     *
     * @return the amount of entries
     */
    public long count() {
        return DatabaseUtils.queryNumEntries(database, PinHelper.TABLE_PINS);
    }

    /**
     * This method gets called on insert() and delete()
     */
    private void onDatabaseAction() {
        long count = count();

        Log.i(TAG, "onDatabaseAction() count " + count);
        BadgeTools.setBadge(context, (int) count);
    }

    /**
     * This method returns a list of all pins in the database
     *
     * @return list of all pins
     */
    @NonNull
    public List<PinSpec> getAllPins() {
        List<PinSpec> pinList = new ArrayList<>();

        Cursor cursor = database.query(PinHelper.TABLE_PINS, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                PinSpec pinSpec = new PinSpec(cursor);
                pinList.add(pinSpec);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return pinList;
    }

    /**
     * This method returns a map of all pins in the database with their id as key
     *
     * @return map of all pins
     */
    @NonNull
    public Map<Integer, PinSpec> getAllPinsMap() {
        Map<Integer, PinSpec> pinMap = new ArrayMap<>();

        Cursor cursor = database.query(PinHelper.TABLE_PINS, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                PinSpec pinSpec = new PinSpec(cursor);
                pinMap.put(pinSpec.getIdAsInt(), pinSpec);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return pinMap;
    }
}
