package de.dotwee.micropinner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.util.Map;

import de.dotwee.micropinner.BuildConfig;
import de.dotwee.micropinner.tools.SQLiteStatementsLogger;

/**
 * Created by lukas on 10.08.2016.
 */
public class PinDatabase extends SQLiteOpenHelper {
    /* integer columns */
    static final String COLUMN_ID = "_id";
    /* string columns */
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_CONTENT = "content";
    /* integer columns */
    static final String COLUMN_VISIBILITY = "visibility";
    static final String COLUMN_PRIORITY = "priority";
    /* boolean columns */
    static final String COLUMN_PERSISTENT = "persistent";
    static final String COLUMN_SHOW_ACTIONS = "show_actions";
    private static final String TABLE_PINS = "pins";
    private static final String TAG = PinDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "comments.db";
    private static final int DATABASE_VERSION = 1;
    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PINS + "( "

            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_CONTENT + " text not null, "

            + COLUMN_VISIBILITY + " integer not null, "
            + COLUMN_PRIORITY + " integer not null, "

            + COLUMN_PERSISTENT + " boolean not null, "
            + COLUMN_SHOW_ACTIONS + " boolean not null);";
    private static final String[] columns = {
            PinDatabase.COLUMN_ID,
            PinDatabase.COLUMN_TITLE,
            PinDatabase.COLUMN_CONTENT,
            PinDatabase.COLUMN_VISIBILITY,
            PinDatabase.COLUMN_PRIORITY,
            PinDatabase.COLUMN_PERSISTENT,
            PinDatabase.COLUMN_SHOW_ACTIONS
    };
    private static PinDatabase instance = null;
    private final SQLiteDatabase database;

    private PinDatabase(@NonNull Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
    }

    public static synchronized PinDatabase getInstance(@NonNull Context context) {
        if (PinDatabase.instance == null) {
            PinDatabase.instance = new PinDatabase(context.getApplicationContext());
        }

        return PinDatabase.instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PINS);
        onCreate(sqLiteDatabase);
    }

    /**
     * This method decides whether a new pin should be created or updated in the database
     *
     * @param pin the pin to write
     */
    public void writePin(@NonNull PinSpec pin) {
        Log.i(TAG, "Write pin called for pin " + pin.toString());

        if (pin.getId() == -1) {
            createPin(pin);
        } else {
            updatePin(pin);
        }
    }

    /**
     * This method creates a pin within the database and gives it a unique id
     *
     * @param pin the pin to create
     */
    private void createPin(@NonNull PinSpec pin) {
        ContentValues contentValues = pin.toContentValues();

        if (BuildConfig.DEBUG) {
            SQLiteStatementsLogger.logInsertWithOnConflict(PinDatabase.TABLE_PINS, null, contentValues, SQLiteDatabase.CONFLICT_NONE);
        }
        long id = database.insert(PinDatabase.TABLE_PINS, null, contentValues);
        Log.i(TAG, "Created new pin with id " + id);
        pin.setId(id);

        onDatabaseAction();
    }

    /**
     * This method updates a pin in the database without changing its id
     *
     * @param pin the pin to update
     */
    private void updatePin(@NonNull PinSpec pin) {
        ContentValues contentValues = pin.toContentValues();
        long id = pin.getId();

        String whereClause = PinDatabase.COLUMN_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        if (BuildConfig.DEBUG) {
            SQLiteStatementsLogger.logUpdate(PinDatabase.TABLE_PINS, contentValues, whereClause, whereArgs);
        }
        database.update(PinDatabase.TABLE_PINS, contentValues, whereClause, whereArgs);
        Log.i(TAG, "Updated new pin with id " + id);
        pin.setId(id);

        onDatabaseAction();
    }

    /**
     * This method deletes a pin from the database
     *
     * @param pin to delete
     */
    public void deletePin(PinSpec pin) {
        long id = pin.getId();

        String whereClause = PinDatabase.COLUMN_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        if (BuildConfig.DEBUG) {
            SQLiteStatementsLogger.logDelete(PinDatabase.TABLE_PINS, whereClause, whereArgs);
        }
        boolean success = database.delete(PinDatabase.TABLE_PINS, whereClause, whereArgs) > 0;
        Log.i(TAG, "Deleting pin with id " + id + "; success " + success);
        pin.setId(-1);

        onDatabaseAction();
    }

    public void deleteAll() {
        Log.i(TAG, "Deleting all pins");
        database.delete(PinDatabase.TABLE_PINS, null, null);
    }

    /**
     * This method returns the amount of entries in the pin database
     *
     * @return the amount of entries
     */
    public long count() {
        return DatabaseUtils.queryNumEntries(database, PinDatabase.TABLE_PINS);
    }

    /**
     * This method gets called on insert() and delete()
     */
    private void onDatabaseAction() {
        long count = count();

        Log.i(TAG, "onDatabaseAction() count " + count);
    }


    /**
     * This method returns a map of all pins in the database with their id as key
     *
     * @return map of all pins
     */
    @NonNull
    public Map<Integer, PinSpec> getAllPinsMap() {
        Map<Integer, PinSpec> pinMap = new ArrayMap<>();

        Cursor cursor = database.query(PinDatabase.TABLE_PINS, columns, null, null, null, null, null);
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
