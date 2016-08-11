package de.dotwee.micropinner.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by lukas on 10.08.2016.
 */
public class PinSpec implements Serializable {
    static final String TAG = "PinSpec";


    long id;
    String title;
    String content;

    int visibility;
    int priority;

    boolean persistent;
    boolean showActions;

    public PinSpec(@NonNull String title, @NonNull String content, int visibility, int priority, boolean persistent, boolean showActions) {

        this.id = -1;
        this.title = title;
        this.content = content;
        this.visibility = visibility;
        this.priority = priority;
        this.persistent = persistent;
        this.showActions = showActions;
    }

    public PinSpec(@NonNull Cursor cursor) {
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
        setId(contentValues.getAsLong(PinHelper.COLUMN_ID));
        setTitle(contentValues.getAsString(PinHelper.COLUMN_TITLE));
        setContent(contentValues.getAsString(PinHelper.COLUMN_CONTENT));
        setVisibility(contentValues.getAsInteger(PinHelper.COLUMN_VISIBILITY));
        setPriority(contentValues.getAsInteger(PinHelper.COLUMN_PRIORITY));
        setPersistent(contentValues.getAsBoolean(PinHelper.COLUMN_PERSISTENT));
        setShowActions(contentValues.getAsBoolean(PinHelper.COLUMN_SHOW_ACTIONS));
    }

    public PinSpec(int visibility, int priority, @NonNull String title, @NonNull String content,
                   boolean persistent, boolean showActions) {

        setId(-1);

        setVisibility(visibility);
        setPriority(priority);
        setTitle(title);
        setContent(content);
        setPersistent(persistent);
        setShowActions(showActions);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdAsInt() {
        return (int) id;
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

    public void setContent(@NonNull String content) {
        this.content = content;
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

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isShowActions() {
        return showActions;
    }

    public void setShowActions(boolean showActions) {
        this.showActions = showActions;
    }

    @NonNull
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PinHelper.COLUMN_TITLE, getTitle());
        contentValues.put(PinHelper.COLUMN_CONTENT, getContent());
        contentValues.put(PinHelper.COLUMN_VISIBILITY, getVisibility());
        contentValues.put(PinHelper.COLUMN_PRIORITY, getPriority());
        contentValues.put(PinHelper.COLUMN_PERSISTENT, isPersistent());
        contentValues.put(PinHelper.COLUMN_SHOW_ACTIONS, isShowActions());

        return contentValues;
    }

    @NonNull
    public String toClipString() {
        if (content != null && !content.isEmpty()) {
            return title + " - " + content;
        } else {
            return title;
        }
    }
}
