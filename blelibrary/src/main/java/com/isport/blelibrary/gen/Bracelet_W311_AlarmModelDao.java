package com.isport.blelibrary.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_AlarmModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BRACELET__W311__ALARM_MODEL".
*/
public class Bracelet_W311_AlarmModelDao extends AbstractDao<Bracelet_W311_AlarmModel, Long> {

    public static final String TABLENAME = "BRACELET__W311__ALARM_MODEL";

    /**
     * Properties of entity Bracelet_W311_AlarmModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property AlarmId = new Property(1, int.class, "alarmId", false, "ALARM_ID");
        public final static Property UserId = new Property(2, String.class, "userId", false, "USER_ID");
        public final static Property DeviceId = new Property(3, String.class, "deviceId", false, "DEVICE_ID");
        public final static Property RepeatCount = new Property(4, int.class, "repeatCount", false, "REPEAT_COUNT");
        public final static Property TimeString = new Property(5, String.class, "timeString", false, "TIME_STRING");
        public final static Property MessageString = new Property(6, String.class, "messageString", false, "MESSAGE_STRING");
        public final static Property IsOpen = new Property(7, Boolean.class, "isOpen", false, "IS_OPEN");
    };


    public Bracelet_W311_AlarmModelDao(DaoConfig config) {
        super(config);
    }
    
    public Bracelet_W311_AlarmModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BRACELET__W311__ALARM_MODEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ALARM_ID\" INTEGER NOT NULL ," + // 1: alarmId
                "\"USER_ID\" TEXT," + // 2: userId
                "\"DEVICE_ID\" TEXT," + // 3: deviceId
                "\"REPEAT_COUNT\" INTEGER NOT NULL ," + // 4: repeatCount
                "\"TIME_STRING\" TEXT," + // 5: timeString
                "\"MESSAGE_STRING\" TEXT," + // 6: messageString
                "\"IS_OPEN\" INTEGER);"); // 7: isOpen
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BRACELET__W311__ALARM_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Bracelet_W311_AlarmModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getAlarmId());
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(4, deviceId);
        }
        stmt.bindLong(5, entity.getRepeatCount());
 
        String timeString = entity.getTimeString();
        if (timeString != null) {
            stmt.bindString(6, timeString);
        }
 
        String messageString = entity.getMessageString();
        if (messageString != null) {
            stmt.bindString(7, messageString);
        }
 
        Boolean isOpen = entity.getIsOpen();
        if (isOpen != null) {
            stmt.bindLong(8, isOpen ? 1L: 0L);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Bracelet_W311_AlarmModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getAlarmId());
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(4, deviceId);
        }
        stmt.bindLong(5, entity.getRepeatCount());
 
        String timeString = entity.getTimeString();
        if (timeString != null) {
            stmt.bindString(6, timeString);
        }
 
        String messageString = entity.getMessageString();
        if (messageString != null) {
            stmt.bindString(7, messageString);
        }
 
        Boolean isOpen = entity.getIsOpen();
        if (isOpen != null) {
            stmt.bindLong(8, isOpen ? 1L: 0L);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Bracelet_W311_AlarmModel readEntity(Cursor cursor, int offset) {
        Bracelet_W311_AlarmModel entity = new Bracelet_W311_AlarmModel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // alarmId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // deviceId
            cursor.getInt(offset + 4), // repeatCount
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // timeString
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // messageString
            cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0 // isOpen
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Bracelet_W311_AlarmModel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAlarmId(cursor.getInt(offset + 1));
        entity.setUserId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDeviceId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRepeatCount(cursor.getInt(offset + 4));
        entity.setTimeString(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setMessageString(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setIsOpen(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Bracelet_W311_AlarmModel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Bracelet_W311_AlarmModel entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
