package com.isport.blelibrary.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.isport.blelibrary.db.table.watch.Watch_SmartBand_ScreenTimeModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "WATCH__SMART_BAND__SCREEN_TIME_MODEL".
*/
public class Watch_SmartBand_ScreenTimeModelDao extends AbstractDao<Watch_SmartBand_ScreenTimeModel, Long> {

    public static final String TABLENAME = "WATCH__SMART_BAND__SCREEN_TIME_MODEL";

    /**
     * Properties of entity Watch_SmartBand_ScreenTimeModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceId = new Property(1, String.class, "deviceId", false, "DEVICE_ID");
        public final static Property UserId = new Property(2, String.class, "userId", false, "USER_ID");
        public final static Property Time = new Property(3, int.class, "time", false, "TIME");
    };


    public Watch_SmartBand_ScreenTimeModelDao(DaoConfig config) {
        super(config);
    }
    
    public Watch_SmartBand_ScreenTimeModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WATCH__SMART_BAND__SCREEN_TIME_MODEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"DEVICE_ID\" TEXT," + // 1: deviceId
                "\"USER_ID\" TEXT," + // 2: userId
                "\"TIME\" INTEGER NOT NULL );"); // 3: time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WATCH__SMART_BAND__SCREEN_TIME_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Watch_SmartBand_ScreenTimeModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(2, deviceId);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }
        stmt.bindLong(4, entity.getTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Watch_SmartBand_ScreenTimeModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(2, deviceId);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(3, userId);
        }
        stmt.bindLong(4, entity.getTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Watch_SmartBand_ScreenTimeModel readEntity(Cursor cursor, int offset) {
        Watch_SmartBand_ScreenTimeModel entity = new Watch_SmartBand_ScreenTimeModel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deviceId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // userId
            cursor.getInt(offset + 3) // time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Watch_SmartBand_ScreenTimeModel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTime(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Watch_SmartBand_ScreenTimeModel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Watch_SmartBand_ScreenTimeModel entity) {
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
