package de.dotwee.micropinner.tools;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by lukas on 30.08.2016.
 */
public class SQLiteStatementsLogger {
    private static final String TAG = SQLiteStatementsLogger.class.getSimpleName();

    private static final String[] CONFLICT_VALUES = new String[]
            {"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};

    public static void logInsertWithOnConflict(String table, String nullColumnHack,
                                               ContentValues initialValues, int conflictAlgorithm) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT");
        sql.append(CONFLICT_VALUES[conflictAlgorithm]);
        sql.append(" INTO ");
        sql.append(table);
        sql.append('(');

        Object[] bindArgs = null;
        int size = (initialValues != null && initialValues.size() > 0)
                ? initialValues.size() : 0;
        if (size > 0) {
            bindArgs = new Object[size];
            int i = 0;
            for (String colName : initialValues.keySet()) {
                sql.append((i > 0) ? "," : "");
                sql.append(colName);
                bindArgs[i++] = initialValues.get(colName);
            }
            sql.append(')');
            sql.append(" VALUES (");
            for (i = 0; i < size; i++) {
                sql.append((i > 0) ? ",?" : "?");
            }
        } else {
            sql.append(nullColumnHack + ") VALUES (NULL");
        }
        sql.append(')');
        sql.append(". (");
        for (Object arg : bindArgs) {
            sql.append(String.valueOf(arg)).append(",");
        }
        sql.deleteCharAt(sql.length() - 1).append(')');
        Log.d(TAG, sql.toString());
    }

    public static void logUpdate(String table, ContentValues values, String whereClause, String[] whereArgs) {
        logUpdateWithOnConflict(table, values, whereClause, whereArgs, 0);
    }

    public static void logUpdateWithOnConflict(String table, ContentValues values,
                                               String whereClause, String[] whereArgs, int conflictAlgorithm) {

        StringBuilder sql = new StringBuilder(120);
        sql.append("UPDATE ");
        sql.append(CONFLICT_VALUES[conflictAlgorithm]);
        sql.append(table);
        sql.append(" SET ");

        // move all bind args to one array
        int setValuesSize = values.size();
        int bindArgsSize = (whereArgs == null) ? setValuesSize : (setValuesSize + whereArgs.length);
        Object[] bindArgs = new Object[bindArgsSize];
        int i = 0;
        for (String colName : values.keySet()) {
            sql.append((i > 0) ? "," : "");
            sql.append(colName);
            bindArgs[i++] = values.get(colName);
            sql.append("=?");
        }
        if (whereArgs != null) {
            for (i = setValuesSize; i < bindArgsSize; i++) {
                bindArgs[i] = whereArgs[i - setValuesSize];
            }
        }
        if (!TextUtils.isEmpty(whereClause)) {
            sql.append(" WHERE ");
            sql.append(whereClause);
        }
        sql.append(". (");
        for (Object arg : bindArgs) {
            sql.append(String.valueOf(arg)).append(",");
        }
        sql.deleteCharAt(sql.length() - 1).append(')');
        Log.d(TAG, sql.toString());
    }

    public static void logDelete(String table, String whereClause, String[] whereArgs) {
        StringBuilder sql = new StringBuilder("DELETE FROM " + table);
        if (!TextUtils.isEmpty(whereClause)) {
            sql.append(" WHERE " + whereClause);
            sql.append(". (");
            for (Object arg : whereArgs) {
                sql.append(String.valueOf(arg)).append(",");
            }
            sql.deleteCharAt(sql.length() - 1).append(')');
        }
        Log.d(TAG, sql.toString());
    }

    public void logInsert(String table, String nullColumnHack, ContentValues values) {
        logInsertWithOnConflict(table, nullColumnHack, values, 0);
    }
}