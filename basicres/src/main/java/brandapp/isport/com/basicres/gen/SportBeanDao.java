package brandapp.isport.com.basicres.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import brandapp.isport.com.basicres.entry.SportBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SPORT_BEAN".
*/
public class SportBeanDao extends AbstractDao<SportBean, Long> {

    public static final String TABLENAME = "SPORT_BEAN";

    /**
     * Properties of entity SportBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "userId", false, "USER_ID");
        public final static Property PublicId = new Property(2, String.class, "publicId", false, "PUBLIC_ID");
        public final static Property Sporttype = new Property(3, int.class, "sporttype", false, "SPORTTYPE");
        public final static Property AvgPace = new Property(4, String.class, "avgPace", false, "AVG_PACE");
        public final static Property AvgSpeed = new Property(5, String.class, "avgSpeed", false, "AVG_SPEED");
        public final static Property Calories = new Property(6, String.class, "calories", false, "CALORIES");
        public final static Property Distance = new Property(7, String.class, "distance", false, "DISTANCE");
        public final static Property EndTimestamp = new Property(8, long.class, "endTimestamp", false, "END_TIMESTAMP");
        public final static Property StartTimestamp = new Property(9, long.class, "startTimestamp", false, "START_TIMESTAMP");
        public final static Property MaxHeartRate = new Property(10, String.class, "maxHeartRate", false, "MAX_HEART_RATE");
        public final static Property AvgHeartRate = new Property(11, String.class, "avgHeartRate", false, "AVG_HEART_RATE");
        public final static Property MaxPace = new Property(12, String.class, "maxPace", false, "MAX_PACE");
        public final static Property MinHeartRate = new Property(13, String.class, "minHeartRate", false, "MIN_HEART_RATE");
        public final static Property MinPace = new Property(14, String.class, "minPace", false, "MIN_PACE");
        public final static Property Step = new Property(15, String.class, "step", false, "STEP");
        public final static Property TimeLong = new Property(16, int.class, "timeLong", false, "TIME_LONG");
        public final static Property PaceArr = new Property(17, String.class, "paceArr", false, "PACE_ARR");
        public final static Property HeartRateArr = new Property(18, String.class, "heartRateArr", false, "HEART_RATE_ARR");
        public final static Property CoorArr = new Property(19, String.class, "coorArr", false, "COOR_ARR");
    };


    public SportBeanDao(DaoConfig config) {
        super(config);
    }
    
    public SportBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SPORT_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USER_ID\" TEXT," + // 1: userId
                "\"PUBLIC_ID\" TEXT," + // 2: publicId
                "\"SPORTTYPE\" INTEGER NOT NULL ," + // 3: sporttype
                "\"AVG_PACE\" TEXT," + // 4: avgPace
                "\"AVG_SPEED\" TEXT," + // 5: avgSpeed
                "\"CALORIES\" TEXT," + // 6: calories
                "\"DISTANCE\" TEXT," + // 7: distance
                "\"END_TIMESTAMP\" INTEGER NOT NULL ," + // 8: endTimestamp
                "\"START_TIMESTAMP\" INTEGER NOT NULL ," + // 9: startTimestamp
                "\"MAX_HEART_RATE\" TEXT," + // 10: maxHeartRate
                "\"AVG_HEART_RATE\" TEXT," + // 11: avgHeartRate
                "\"MAX_PACE\" TEXT," + // 12: maxPace
                "\"MIN_HEART_RATE\" TEXT," + // 13: minHeartRate
                "\"MIN_PACE\" TEXT," + // 14: minPace
                "\"STEP\" TEXT," + // 15: step
                "\"TIME_LONG\" INTEGER NOT NULL ," + // 16: timeLong
                "\"PACE_ARR\" TEXT," + // 17: paceArr
                "\"HEART_RATE_ARR\" TEXT," + // 18: heartRateArr
                "\"COOR_ARR\" TEXT);"); // 19: coorArr
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SPORT_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SportBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String publicId = entity.getPublicId();
        if (publicId != null) {
            stmt.bindString(3, publicId);
        }
        stmt.bindLong(4, entity.getSporttype());
 
        String avgPace = entity.getAvgPace();
        if (avgPace != null) {
            stmt.bindString(5, avgPace);
        }
 
        String avgSpeed = entity.getAvgSpeed();
        if (avgSpeed != null) {
            stmt.bindString(6, avgSpeed);
        }
 
        String calories = entity.getCalories();
        if (calories != null) {
            stmt.bindString(7, calories);
        }
 
        String distance = entity.getDistance();
        if (distance != null) {
            stmt.bindString(8, distance);
        }
        stmt.bindLong(9, entity.getEndTimestamp());
        stmt.bindLong(10, entity.getStartTimestamp());
 
        String maxHeartRate = entity.getMaxHeartRate();
        if (maxHeartRate != null) {
            stmt.bindString(11, maxHeartRate);
        }
 
        String avgHeartRate = entity.getAvgHeartRate();
        if (avgHeartRate != null) {
            stmt.bindString(12, avgHeartRate);
        }
 
        String maxPace = entity.getMaxPace();
        if (maxPace != null) {
            stmt.bindString(13, maxPace);
        }
 
        String minHeartRate = entity.getMinHeartRate();
        if (minHeartRate != null) {
            stmt.bindString(14, minHeartRate);
        }
 
        String minPace = entity.getMinPace();
        if (minPace != null) {
            stmt.bindString(15, minPace);
        }
 
        String step = entity.getStep();
        if (step != null) {
            stmt.bindString(16, step);
        }
        stmt.bindLong(17, entity.getTimeLong());
 
        String paceArr = entity.getPaceArr();
        if (paceArr != null) {
            stmt.bindString(18, paceArr);
        }
 
        String heartRateArr = entity.getHeartRateArr();
        if (heartRateArr != null) {
            stmt.bindString(19, heartRateArr);
        }
 
        String coorArr = entity.getCoorArr();
        if (coorArr != null) {
            stmt.bindString(20, coorArr);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SportBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String publicId = entity.getPublicId();
        if (publicId != null) {
            stmt.bindString(3, publicId);
        }
        stmt.bindLong(4, entity.getSporttype());
 
        String avgPace = entity.getAvgPace();
        if (avgPace != null) {
            stmt.bindString(5, avgPace);
        }
 
        String avgSpeed = entity.getAvgSpeed();
        if (avgSpeed != null) {
            stmt.bindString(6, avgSpeed);
        }
 
        String calories = entity.getCalories();
        if (calories != null) {
            stmt.bindString(7, calories);
        }
 
        String distance = entity.getDistance();
        if (distance != null) {
            stmt.bindString(8, distance);
        }
        stmt.bindLong(9, entity.getEndTimestamp());
        stmt.bindLong(10, entity.getStartTimestamp());
 
        String maxHeartRate = entity.getMaxHeartRate();
        if (maxHeartRate != null) {
            stmt.bindString(11, maxHeartRate);
        }
 
        String avgHeartRate = entity.getAvgHeartRate();
        if (avgHeartRate != null) {
            stmt.bindString(12, avgHeartRate);
        }
 
        String maxPace = entity.getMaxPace();
        if (maxPace != null) {
            stmt.bindString(13, maxPace);
        }
 
        String minHeartRate = entity.getMinHeartRate();
        if (minHeartRate != null) {
            stmt.bindString(14, minHeartRate);
        }
 
        String minPace = entity.getMinPace();
        if (minPace != null) {
            stmt.bindString(15, minPace);
        }
 
        String step = entity.getStep();
        if (step != null) {
            stmt.bindString(16, step);
        }
        stmt.bindLong(17, entity.getTimeLong());
 
        String paceArr = entity.getPaceArr();
        if (paceArr != null) {
            stmt.bindString(18, paceArr);
        }
 
        String heartRateArr = entity.getHeartRateArr();
        if (heartRateArr != null) {
            stmt.bindString(19, heartRateArr);
        }
 
        String coorArr = entity.getCoorArr();
        if (coorArr != null) {
            stmt.bindString(20, coorArr);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SportBean readEntity(Cursor cursor, int offset) {
        SportBean entity = new SportBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // publicId
            cursor.getInt(offset + 3), // sporttype
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // avgPace
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // avgSpeed
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // calories
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // distance
            cursor.getLong(offset + 8), // endTimestamp
            cursor.getLong(offset + 9), // startTimestamp
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // maxHeartRate
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // avgHeartRate
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // maxPace
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // minHeartRate
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // minPace
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // step
            cursor.getInt(offset + 16), // timeLong
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // paceArr
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // heartRateArr
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19) // coorArr
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SportBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPublicId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSporttype(cursor.getInt(offset + 3));
        entity.setAvgPace(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAvgSpeed(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCalories(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDistance(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setEndTimestamp(cursor.getLong(offset + 8));
        entity.setStartTimestamp(cursor.getLong(offset + 9));
        entity.setMaxHeartRate(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setAvgHeartRate(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setMaxPace(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setMinHeartRate(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setMinPace(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setStep(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setTimeLong(cursor.getInt(offset + 16));
        entity.setPaceArr(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setHeartRateArr(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setCoorArr(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SportBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SportBean entity) {
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
