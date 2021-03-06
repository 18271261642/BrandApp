package com.isport.blelibrary.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.isport.blelibrary.db.table.w811w814.W81DeviceExerciseData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "W81_DEVICE_EXERCISE_DATA".
*/
public class W81DeviceExerciseDataDao extends AbstractDao<W81DeviceExerciseData, Long> {

    public static final String TABLENAME = "W81_DEVICE_EXERCISE_DATA";

    /**
     * Properties of entity W81DeviceExerciseData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "userId", false, "USER_ID");
        public final static Property DeviceId = new Property(2, String.class, "deviceId", false, "DEVICE_ID");
        public final static Property WristbandSportDetailId = new Property(3, String.class, "wristbandSportDetailId", false, "WRISTBAND_SPORT_DETAIL_ID");
        public final static Property DateStr = new Property(4, String.class, "dateStr", false, "DATE_STR");
        public final static Property StartTimestamp = new Property(5, Long.class, "startTimestamp", false, "START_TIMESTAMP");
        public final static Property EndTimestamp = new Property(6, Long.class, "endTimestamp", false, "END_TIMESTAMP");
        public final static Property VaildTimeLength = new Property(7, String.class, "vaildTimeLength", false, "VAILD_TIME_LENGTH");
        public final static Property ExerciseType = new Property(8, String.class, "exerciseType", false, "EXERCISE_TYPE");
        public final static Property TotalSteps = new Property(9, String.class, "totalSteps", false, "TOTAL_STEPS");
        public final static Property TotalDistance = new Property(10, String.class, "totalDistance", false, "TOTAL_DISTANCE");
        public final static Property TotalCalories = new Property(11, String.class, "totalCalories", false, "TOTAL_CALORIES");
        public final static Property AvgHr = new Property(12, String.class, "avgHr", false, "AVG_HR");
        public final static Property HrArray = new Property(13, String.class, "hrArray", false, "HR_ARRAY");
        public final static Property StepArray = new Property(14, String.class, "stepArray", false, "STEP_ARRAY");
        public final static Property DistanceArray = new Property(15, String.class, "distanceArray", false, "DISTANCE_ARRAY");
        public final static Property CalorieArray = new Property(16, String.class, "calorieArray", false, "CALORIE_ARRAY");
        public final static Property HasHR = new Property(17, int.class, "hasHR", false, "HAS_HR");
        public final static Property TimeInterval = new Property(18, int.class, "timeInterval", false, "TIME_INTERVAL");
        public final static Property StartMeasureTime = new Property(19, long.class, "startMeasureTime", false, "START_MEASURE_TIME");
    };


    public W81DeviceExerciseDataDao(DaoConfig config) {
        super(config);
    }
    
    public W81DeviceExerciseDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"W81_DEVICE_EXERCISE_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USER_ID\" TEXT," + // 1: userId
                "\"DEVICE_ID\" TEXT," + // 2: deviceId
                "\"WRISTBAND_SPORT_DETAIL_ID\" TEXT," + // 3: wristbandSportDetailId
                "\"DATE_STR\" TEXT," + // 4: dateStr
                "\"START_TIMESTAMP\" INTEGER," + // 5: startTimestamp
                "\"END_TIMESTAMP\" INTEGER," + // 6: endTimestamp
                "\"VAILD_TIME_LENGTH\" TEXT," + // 7: vaildTimeLength
                "\"EXERCISE_TYPE\" TEXT," + // 8: exerciseType
                "\"TOTAL_STEPS\" TEXT," + // 9: totalSteps
                "\"TOTAL_DISTANCE\" TEXT," + // 10: totalDistance
                "\"TOTAL_CALORIES\" TEXT," + // 11: totalCalories
                "\"AVG_HR\" TEXT," + // 12: avgHr
                "\"HR_ARRAY\" TEXT," + // 13: hrArray
                "\"STEP_ARRAY\" TEXT," + // 14: stepArray
                "\"DISTANCE_ARRAY\" TEXT," + // 15: distanceArray
                "\"CALORIE_ARRAY\" TEXT," + // 16: calorieArray
                "\"HAS_HR\" INTEGER NOT NULL ," + // 17: hasHR
                "\"TIME_INTERVAL\" INTEGER NOT NULL ," + // 18: timeInterval
                "\"START_MEASURE_TIME\" INTEGER NOT NULL );"); // 19: startMeasureTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"W81_DEVICE_EXERCISE_DATA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, W81DeviceExerciseData entity) {
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
 
        String wristbandSportDetailId = entity.getWristbandSportDetailId();
        if (wristbandSportDetailId != null) {
            stmt.bindString(4, wristbandSportDetailId);
        }
 
        String dateStr = entity.getDateStr();
        if (dateStr != null) {
            stmt.bindString(5, dateStr);
        }
 
        Long startTimestamp = entity.getStartTimestamp();
        if (startTimestamp != null) {
            stmt.bindLong(6, startTimestamp);
        }
 
        Long endTimestamp = entity.getEndTimestamp();
        if (endTimestamp != null) {
            stmt.bindLong(7, endTimestamp);
        }
 
        String vaildTimeLength = entity.getVaildTimeLength();
        if (vaildTimeLength != null) {
            stmt.bindString(8, vaildTimeLength);
        }
 
        String exerciseType = entity.getExerciseType();
        if (exerciseType != null) {
            stmt.bindString(9, exerciseType);
        }
 
        String totalSteps = entity.getTotalSteps();
        if (totalSteps != null) {
            stmt.bindString(10, totalSteps);
        }
 
        String totalDistance = entity.getTotalDistance();
        if (totalDistance != null) {
            stmt.bindString(11, totalDistance);
        }
 
        String totalCalories = entity.getTotalCalories();
        if (totalCalories != null) {
            stmt.bindString(12, totalCalories);
        }
 
        String avgHr = entity.getAvgHr();
        if (avgHr != null) {
            stmt.bindString(13, avgHr);
        }
 
        String hrArray = entity.getHrArray();
        if (hrArray != null) {
            stmt.bindString(14, hrArray);
        }
 
        String stepArray = entity.getStepArray();
        if (stepArray != null) {
            stmt.bindString(15, stepArray);
        }
 
        String distanceArray = entity.getDistanceArray();
        if (distanceArray != null) {
            stmt.bindString(16, distanceArray);
        }
 
        String calorieArray = entity.getCalorieArray();
        if (calorieArray != null) {
            stmt.bindString(17, calorieArray);
        }
        stmt.bindLong(18, entity.getHasHR());
        stmt.bindLong(19, entity.getTimeInterval());
        stmt.bindLong(20, entity.getStartMeasureTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, W81DeviceExerciseData entity) {
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
 
        String wristbandSportDetailId = entity.getWristbandSportDetailId();
        if (wristbandSportDetailId != null) {
            stmt.bindString(4, wristbandSportDetailId);
        }
 
        String dateStr = entity.getDateStr();
        if (dateStr != null) {
            stmt.bindString(5, dateStr);
        }
 
        Long startTimestamp = entity.getStartTimestamp();
        if (startTimestamp != null) {
            stmt.bindLong(6, startTimestamp);
        }
 
        Long endTimestamp = entity.getEndTimestamp();
        if (endTimestamp != null) {
            stmt.bindLong(7, endTimestamp);
        }
 
        String vaildTimeLength = entity.getVaildTimeLength();
        if (vaildTimeLength != null) {
            stmt.bindString(8, vaildTimeLength);
        }
 
        String exerciseType = entity.getExerciseType();
        if (exerciseType != null) {
            stmt.bindString(9, exerciseType);
        }
 
        String totalSteps = entity.getTotalSteps();
        if (totalSteps != null) {
            stmt.bindString(10, totalSteps);
        }
 
        String totalDistance = entity.getTotalDistance();
        if (totalDistance != null) {
            stmt.bindString(11, totalDistance);
        }
 
        String totalCalories = entity.getTotalCalories();
        if (totalCalories != null) {
            stmt.bindString(12, totalCalories);
        }
 
        String avgHr = entity.getAvgHr();
        if (avgHr != null) {
            stmt.bindString(13, avgHr);
        }
 
        String hrArray = entity.getHrArray();
        if (hrArray != null) {
            stmt.bindString(14, hrArray);
        }
 
        String stepArray = entity.getStepArray();
        if (stepArray != null) {
            stmt.bindString(15, stepArray);
        }
 
        String distanceArray = entity.getDistanceArray();
        if (distanceArray != null) {
            stmt.bindString(16, distanceArray);
        }
 
        String calorieArray = entity.getCalorieArray();
        if (calorieArray != null) {
            stmt.bindString(17, calorieArray);
        }
        stmt.bindLong(18, entity.getHasHR());
        stmt.bindLong(19, entity.getTimeInterval());
        stmt.bindLong(20, entity.getStartMeasureTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public W81DeviceExerciseData readEntity(Cursor cursor, int offset) {
        W81DeviceExerciseData entity = new W81DeviceExerciseData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deviceId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // wristbandSportDetailId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // dateStr
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // startTimestamp
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // endTimestamp
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // vaildTimeLength
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // exerciseType
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // totalSteps
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // totalDistance
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // totalCalories
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // avgHr
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // hrArray
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // stepArray
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // distanceArray
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // calorieArray
            cursor.getInt(offset + 17), // hasHR
            cursor.getInt(offset + 18), // timeInterval
            cursor.getLong(offset + 19) // startMeasureTime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, W81DeviceExerciseData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setWristbandSportDetailId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDateStr(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStartTimestamp(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setEndTimestamp(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setVaildTimeLength(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setExerciseType(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTotalSteps(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setTotalDistance(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setTotalCalories(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setAvgHr(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setHrArray(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setStepArray(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setDistanceArray(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setCalorieArray(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setHasHR(cursor.getInt(offset + 17));
        entity.setTimeInterval(cursor.getInt(offset + 18));
        entity.setStartMeasureTime(cursor.getLong(offset + 19));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(W81DeviceExerciseData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(W81DeviceExerciseData entity) {
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
