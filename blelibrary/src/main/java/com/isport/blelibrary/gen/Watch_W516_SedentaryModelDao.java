package com.isport.blelibrary.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.isport.blelibrary.db.table.watch_w516.Watch_W516_SedentaryModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "WATCH__W516__SEDENTARY_MODEL".
*/
public class Watch_W516_SedentaryModelDao extends AbstractDao<Watch_W516_SedentaryModel, Long> {

    public static final String TABLENAME = "WATCH__W516__SEDENTARY_MODEL";

    /**
     * Properties of entity Watch_W516_SedentaryModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "userId", false, "USER_ID");
        public final static Property DeviceId = new Property(2, String.class, "deviceId", false, "DEVICE_ID");
        public final static Property IsEnable = new Property(3, boolean.class, "isEnable", false, "IS_ENABLE");
        public final static Property LongSitTimeLong = new Property(4, int.class, "longSitTimeLong", false, "LONG_SIT_TIME_LONG");
        public final static Property LongSitStartTime = new Property(5, String.class, "longSitStartTime", false, "LONG_SIT_START_TIME");
        public final static Property LongSitEndTime = new Property(6, String.class, "longSitEndTime", false, "LONG_SIT_END_TIME");
    };


    public Watch_W516_SedentaryModelDao(DaoConfig config) {
        super(config);
    }
    
    public Watch_W516_SedentaryModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WATCH__W516__SEDENTARY_MODEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USER_ID\" TEXT," + // 1: userId
                "\"DEVICE_ID\" TEXT," + // 2: deviceId
                "\"IS_ENABLE\" INTEGER NOT NULL ," + // 3: isEnable
                "\"LONG_SIT_TIME_LONG\" INTEGER NOT NULL ," + // 4: longSitTimeLong
                "\"LONG_SIT_START_TIME\" TEXT," + // 5: longSitStartTime
                "\"LONG_SIT_END_TIME\" TEXT);"); // 6: longSitEndTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WATCH__W516__SEDENTARY_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Watch_W516_SedentaryModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(3, deviceId);
        }
        stmt.bindLong(4, entity.getIsEnable() ? 1L: 0L);
        stmt.bindLong(5, entity.getLongSitTimeLong());
 
        String longSitStartTime = entity.getLongSitStartTime();
        if (longSitStartTime != null) {
            stmt.bindString(6, longSitStartTime);
        }
 
        String longSitEndTime = entity.getLongSitEndTime();
        if (longSitEndTime != null) {
            stmt.bindString(7, longSitEndTime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Watch_W516_SedentaryModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String deviceId = entity.getDeviceId();
        if (deviceId != null) {
            stmt.bindString(3, deviceId);
        }
        stmt.bindLong(4, entity.getIsEnable() ? 1L: 0L);
        stmt.bindLong(5, entity.getLongSitTimeLong());
 
        String longSitStartTime = entity.getLongSitStartTime();
        if (longSitStartTime != null) {
            stmt.bindString(6, longSitStartTime);
        }
 
        String longSitEndTime = entity.getLongSitEndTime();
        if (longSitEndTime != null) {
            stmt.bindString(7, longSitEndTime);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Watch_W516_SedentaryModel readEntity(Cursor cursor, int offset) {
        Watch_W516_SedentaryModel entity = new Watch_W516_SedentaryModel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deviceId
            cursor.getShort(offset + 3) != 0, // isEnable
            cursor.getInt(offset + 4), // longSitTimeLong
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // longSitStartTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // longSitEndTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Watch_W516_SedentaryModel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIsEnable(cursor.getShort(offset + 3) != 0);
        entity.setLongSitTimeLong(cursor.getInt(offset + 4));
        entity.setLongSitStartTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLongSitEndTime(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Watch_W516_SedentaryModel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Watch_W516_SedentaryModel entity) {
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
