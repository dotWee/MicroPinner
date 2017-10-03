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
    private static final String TAG = PinSpec.class.getSimpleName();


    private long id;
    private String title;
    private String content;

    private int visibility;
    private int priority;

    private boolean persistent;
    private boolean showActions;

    public PinSpec(@NonNull String title, @NonNull String content, int visibility, int priority, boolean persistent, boolean showActions) {

        this.id = -1;
        this.title = title;
        this.content = content;
        this.visibility = visibility;
        this.priority = priority;
        this.persistent = persistent;
        this.showActions = showActions;
    }

    PinSpec(@NonNull Cursor cursor) {
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
        setId(contentValues.getAsLong(PinDatabase.COLUMN_ID));
        setTitle(contentValues.getAsString(PinDatabase.COLUMN_TITLE));
        setContent(contentValues.getAsString(PinDatabase.COLUMN_CONTENT));
        setVisibility(contentValues.getAsInteger(PinDatabase.COLUMN_VISIBILITY));
        setPriority(contentValues.getAsInteger(PinDatabase.COLUMN_PRIORITY));
        setPersistent(contentValues.getAsInteger(PinDatabase.COLUMN_PERSISTENT) != 0);
        setShowActions(contentValues.getAsInteger(PinDatabase.COLUMN_SHOW_ACTIONS) != 0);
    }

    private PinSpec(int visibility, int priority, @NonNull String title, @NonNull String content,
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

    private void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    private void setContent(@NonNull String content) {
        this.content = content;
    }

    public int getVisibility() {
        return visibility;
    }

    private void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getPriority() {
        return priority;
    }

    private void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isPersistent() {
        return persistent;
    }

    private void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isShowActions() {
        return showActions;
    }

    private void setShowActions(boolean showActions) {
        this.showActions = showActions;
    }

    @NonNull
    ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(PinDatabase.COLUMN_TITLE, getTitle());
        contentValues.put(PinDatabase.COLUMN_CONTENT, getContent());
        contentValues.put(PinDatabase.COLUMN_VISIBILITY, getVisibility());
        contentValues.put(PinDatabase.COLUMN_PRIORITY, getPriority());
        contentValues.put(PinDatabase.COLUMN_PERSISTENT, isPersistent() ? 1 : 0);
        contentValues.put(PinDatabase.COLUMN_SHOW_ACTIONS, isShowActions() ? 1: 0);

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

    @Override
    public String toString() {
        return "PinSpec{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", visibility=" + visibility +
                ", priority=" + priority +
                ", persistent=" + persistent +
                ", showActions=" + showActions +
                '}';
    }
}
