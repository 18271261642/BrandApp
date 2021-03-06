package brandapp.isport.com.basicres.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import brandapp.isport.com.basicres.entry.WatchRealTimeData;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "WATCH_REAL_TIME_DATA".
*/
public class WatchRealTimeDataDao extends AbstractDao<WatchRealTimeData, Long> {

    public static final String TABLENAME = "WATCH_REAL_TIME_DATA";

    /**
     * Properties of entity WatchRealTimeData.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property StepNum = new Property(1, int.class, "stepNum", false, "STEP_NUM");
        public final static Property StepKm = new Property(2, float.class, "stepKm", false, "STEP_KM");
        public final static Property Cal = new Property(3, int.class, "cal", false, "CAL");
        public final static Property Date = new Property(4, String.class, "date", false, "DATE");
        public final static Property Mac = new Property(5, String.class, "mac", false, "MAC");
    };


    public WatchRealTimeDataDao(DaoConfig config) {
        super(config);
    }
    
    public WatchRealTimeDataDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WATCH_REAL_TIME_DATA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"STEP_NUM\" INTEGER NOT NULL ," + // 1: stepNum
                "\"STEP_KM\" REAL NOT NULL ," + // 2: stepKm
                "\"CAL\" INTEGER NOT NULL ," + // 3: cal
                "\"DATE\" TEXT," + // 4: date
                "\"MAC\" TEXT);"); // 5: mac
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WATCH_REAL_TIME_DATA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, WatchRealTimeData entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getStepNum());
        stmt.bindDouble(3, entity.getStepKm());
        stmt.bindLong(4, entity.getCal());
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(5, date);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(6, mac);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, WatchRealTimeData entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getStepNum());
        stmt.bindDouble(3, entity.getStepKm());
        stmt.bindLong(4, entity.getCal());
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(5, date);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(6, mac);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public WatchRealTimeData readEntity(Cursor cursor, int offset) {
        WatchRealTimeData entity = new WatchRealTimeData( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // stepNum
            cursor.getFloat(offset + 2), // stepKm
            cursor.getInt(offset + 3), // cal
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // date
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // mac
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, WatchRealTimeData entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStepNum(cursor.getInt(offset + 1));
        entity.setStepKm(cursor.getFloat(offset + 2));
        entity.setCal(cursor.getInt(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMac(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(WatchRealTimeData entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(WatchRealTimeData entity) {
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
