package com.isport.blelibrary.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.isport.blelibrary.db.table.bracelet_w311.Bracelet_W311_LiftWristToViewInfoModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BRACELET__W311__LIFT_WRIST_TO_VIEW_INFO_MODEL".
*/
public class Bracelet_W311_LiftWristToViewInfoModelDao extends AbstractDao<Bracelet_W311_LiftWristToViewInfoModel, Long> {

    public static final String TABLENAME = "BRACELET__W311__LIFT_WRIST_TO_VIEW_INFO_MODEL";

    /**
     * Properties of entity Bracelet_W311_LiftWristToViewInfoModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "userId", false, "USER_ID");
        public final static Property DeviceId = new Property(2, String.class, "deviceId", false, "DEVICE_ID");
        public final static Property SwichType = new Property(3, int.class, "swichType", false, "SWICH_TYPE");
        public final static Property StartHour = new Property(4, int.class, "startHour", false, "START_HOUR");
        public final static Property StartMin = new Property(5, int.class, "startMin", false, "START_MIN");
        public final static Property EndHour = new Property(6, int.class, "endHour", false, "END_HOUR");
        public final static Property EndMin = new Property(7, int.class, "endMin", false, "END_MIN");
        public final static Property IsNextDay = new Property(8, boolean.class, "isNextDay", false, "IS_NEXT_DAY");
    };


    public Bracelet_W311_LiftWristToViewInfoModelDao(DaoConfig config) {
        super(config);
    }
    
    public Bracelet_W311_LiftWristToViewInfoModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BRACELET__W311__LIFT_WRIST_TO_VIEW_INFO_MODEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USER_ID\" TEXT," + // 1: userId
                "\"DEVICE_ID\" TEXT," + // 2: deviceId
                "\"SWICH_TYPE\" INTEGER NOT NULL ," + // 3: swichType
                "\"START_HOUR\" INTEGER NOT NULL ," + // 4: startHour
                "\"START_MIN\" INTEGER NOT NULL ," + // 5: startMin
                "\"END_HOUR\" INTEGER NOT NULL ," + // 6: endHour
                "\"END_MIN\" INTEGER NOT NULL ," + // 7: endMin
                "\"IS_NEXT_DAY\" INTEGER NOT NULL );"); // 8: isNextDay
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BRACELET__W311__LIFT_WRIST_TO_VIEW_INFO_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Bracelet_W311_LiftWristToViewInfoModel entity) {
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
        stmt.bindLong(4, entity.getSwichType());
        stmt.bindLong(5, entity.getStartHour());
        stmt.bindLong(6, entity.getStartMin());
        stmt.bindLong(7, entity.getEndHour());
        stmt.bindLong(8, entity.getEndMin());
        stmt.bindLong(9, entity.getIsNextDay() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Bracelet_W311_LiftWristToViewInfoModel entity) {
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
        stmt.bindLong(4, entity.getSwichType());
        stmt.bindLong(5, entity.getStartHour());
        stmt.bindLong(6, entity.getStartMin());
        stmt.bindLong(7, entity.getEndHour());
        stmt.bindLong(8, entity.getEndMin());
        stmt.bindLong(9, entity.getIsNextDay() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Bracelet_W311_LiftWristToViewInfoModel readEntity(Cursor cursor, int offset) {
        Bracelet_W311_LiftWristToViewInfoModel entity = new Bracelet_W311_LiftWristToViewInfoModel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deviceId
            cursor.getInt(offset + 3), // swichType
            cursor.getInt(offset + 4), // startHour
            cursor.getInt(offset + 5), // startMin
            cursor.getInt(offset + 6), // endHour
            cursor.getInt(offset + 7), // endMin
            cursor.getShort(offset + 8) != 0 // isNextDay
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Bracelet_W311_LiftWristToViewInfoModel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSwichType(cursor.getInt(offset + 3));
        entity.setStartHour(cursor.getInt(offset + 4));
        entity.setStartMin(cursor.getInt(offset + 5));
        entity.setEndHour(cursor.getInt(offset + 6));
        entity.setEndMin(cursor.getInt(offset + 7));
        entity.setIsNextDay(cursor.getShort(offset + 8) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Bracelet_W311_LiftWristToViewInfoModel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Bracelet_W311_LiftWristToViewInfoModel entity) {
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
