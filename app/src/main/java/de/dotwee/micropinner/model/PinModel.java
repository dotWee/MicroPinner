package de.dotwee.micropinner.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by lukas on 12.04.18.
 */
@Entity(
        // Flag to make an entity "active": Active entities have update,
        // delete, and refresh methods.
        active = true,

        // Specifies the name of the table in the database.
        // By default, the name is based on the entities class name.
        nameInDb = "Pins",

        // Whether an all properties constructor should be generated.
        // A no-args constructor is always required.
        generateConstructors = true,

        // Whether getters and setters for properties should be generated if missing.
        generateGettersSetters = true
)
public class PinModel {

    @Id(autoincrement = true)
    private Long id;

    private String title;
    private String content;

    private int visibility;
    private int priority;
    private int notification;

    private boolean persistent;
    private boolean showActions;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 106535368)
    private transient PinModelDao myDao;

    @Generated(hash = 1078354871)
    public PinModel(Long id, String title, String content, int visibility, int priority,
                    int notification, boolean persistent, boolean showActions) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.visibility = visibility;
        this.priority = priority;
        this.notification = notification;
        this.persistent = persistent;
        this.showActions = showActions;
    }

    @Generated(hash = 6338365)
    public PinModel() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getVisibility() {
        return this.visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getNotification() {
        return this.notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public boolean getPersistent() {
        return this.persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean getShowActions() {
        return this.showActions;
    }

    public void setShowActions(boolean showActions) {
        this.showActions = showActions;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 186190709)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPinModelDao() : null;
}
}
