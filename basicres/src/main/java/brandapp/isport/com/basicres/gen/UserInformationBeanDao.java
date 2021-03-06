package brandapp.isport.com.basicres.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import brandapp.isport.com.basicres.entry.UserInformationBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_INFORMATION_BEAN".
*/
public class UserInformationBeanDao extends AbstractDao<UserInformationBean, Long> {

    public static final String TABLENAME = "USER_INFORMATION_BEAN";

    /**
     * Properties of entity UserInformationBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserId = new Property(1, String.class, "userId", false, "USER_ID");
        public final static Property NickName = new Property(2, String.class, "nickName", false, "NICK_NAME");
        public final static Property CreatTime = new Property(3, long.class, "creatTime", false, "CREAT_TIME");
        public final static Property Gender = new Property(4, String.class, "gender", false, "GENDER");
        public final static Property BodyHeight = new Property(5, int.class, "bodyHeight", false, "BODY_HEIGHT");
        public final static Property BodyWeight = new Property(6, float.class, "bodyWeight", false, "BODY_WEIGHT");
        public final static Property Birthday = new Property(7, String.class, "birthday", false, "BIRTHDAY");
        public final static Property Age = new Property(8, int.class, "age", false, "AGE");
        public final static Property StepTarget = new Property(9, int.class, "stepTarget", false, "STEP_TARGET");
        public final static Property StepTargetDate = new Property(10, String.class, "stepTargetDate", false, "STEP_TARGET_DATE");
        public final static Property BodyWeightTarget = new Property(11, int.class, "bodyWeightTarget", false, "BODY_WEIGHT_TARGET");
        public final static Property BodyWeightTargetDate = new Property(12, String.class, "bodyWeightTargetDate", false, "BODY_WEIGHT_TARGET_DATE");
        public final static Property SleepTarget = new Property(13, long.class, "sleepTarget", false, "SLEEP_TARGET");
        public final static Property SleepTargetDate = new Property(14, String.class, "sleepTargetDate", false, "SLEEP_TARGET_DATE");
        public final static Property HeadImage = new Property(15, String.class, "headImage", false, "HEAD_IMAGE");
        public final static Property HeadImage_s = new Property(16, String.class, "headImage_s", false, "HEAD_IMAGE_S");
        public final static Property UseNetwork = new Property(17, boolean.class, "useNetwork", false, "USE_NETWORK");
        public final static Property LastConnectDevice = new Property(18, String.class, "lastConnectDevice", false, "LAST_CONNECT_DEVICE");
    };


    public UserInformationBeanDao(DaoConfig config) {
        super(config);
    }
    
    public UserInformationBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_INFORMATION_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USER_ID\" TEXT," + // 1: userId
                "\"NICK_NAME\" TEXT," + // 2: nickName
                "\"CREAT_TIME\" INTEGER NOT NULL ," + // 3: creatTime
                "\"GENDER\" TEXT," + // 4: gender
                "\"BODY_HEIGHT\" INTEGER NOT NULL ," + // 5: bodyHeight
                "\"BODY_WEIGHT\" REAL NOT NULL ," + // 6: bodyWeight
                "\"BIRTHDAY\" TEXT," + // 7: birthday
                "\"AGE\" INTEGER NOT NULL ," + // 8: age
                "\"STEP_TARGET\" INTEGER NOT NULL ," + // 9: stepTarget
                "\"STEP_TARGET_DATE\" TEXT," + // 10: stepTargetDate
                "\"BODY_WEIGHT_TARGET\" INTEGER NOT NULL ," + // 11: bodyWeightTarget
                "\"BODY_WEIGHT_TARGET_DATE\" TEXT," + // 12: bodyWeightTargetDate
                "\"SLEEP_TARGET\" INTEGER NOT NULL ," + // 13: sleepTarget
                "\"SLEEP_TARGET_DATE\" TEXT," + // 14: sleepTargetDate
                "\"HEAD_IMAGE\" TEXT," + // 15: headImage
                "\"HEAD_IMAGE_S\" TEXT," + // 16: headImage_s
                "\"USE_NETWORK\" INTEGER NOT NULL ," + // 17: useNetwork
                "\"LAST_CONNECT_DEVICE\" TEXT);"); // 18: lastConnectDevice
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_INFORMATION_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, UserInformationBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(3, nickName);
        }
        stmt.bindLong(4, entity.getCreatTime());
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(5, gender);
        }
        stmt.bindLong(6, entity.getBodyHeight());
        stmt.bindDouble(7, entity.getBodyWeight());
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(8, birthday);
        }
        stmt.bindLong(9, entity.getAge());
        stmt.bindLong(10, entity.getStepTarget());
 
        String stepTargetDate = entity.getStepTargetDate();
        if (stepTargetDate != null) {
            stmt.bindString(11, stepTargetDate);
        }
        stmt.bindLong(12, entity.getBodyWeightTarget());
 
        String bodyWeightTargetDate = entity.getBodyWeightTargetDate();
        if (bodyWeightTargetDate != null) {
            stmt.bindString(13, bodyWeightTargetDate);
        }
        stmt.bindLong(14, entity.getSleepTarget());
 
        String sleepTargetDate = entity.getSleepTargetDate();
        if (sleepTargetDate != null) {
            stmt.bindString(15, sleepTargetDate);
        }
 
        String headImage = entity.getHeadImage();
        if (headImage != null) {
            stmt.bindString(16, headImage);
        }
 
        String headImage_s = entity.getHeadImage_s();
        if (headImage_s != null) {
            stmt.bindString(17, headImage_s);
        }
        stmt.bindLong(18, entity.getUseNetwork() ? 1L: 0L);
 
        String lastConnectDevice = entity.getLastConnectDevice();
        if (lastConnectDevice != null) {
            stmt.bindString(19, lastConnectDevice);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, UserInformationBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(2, userId);
        }
 
        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(3, nickName);
        }
        stmt.bindLong(4, entity.getCreatTime());
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(5, gender);
        }
        stmt.bindLong(6, entity.getBodyHeight());
        stmt.bindDouble(7, entity.getBodyWeight());
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(8, birthday);
        }
        stmt.bindLong(9, entity.getAge());
        stmt.bindLong(10, entity.getStepTarget());
 
        String stepTargetDate = entity.getStepTargetDate();
        if (stepTargetDate != null) {
            stmt.bindString(11, stepTargetDate);
        }
        stmt.bindLong(12, entity.getBodyWeightTarget());
 
        String bodyWeightTargetDate = entity.getBodyWeightTargetDate();
        if (bodyWeightTargetDate != null) {
            stmt.bindString(13, bodyWeightTargetDate);
        }
        stmt.bindLong(14, entity.getSleepTarget());
 
        String sleepTargetDate = entity.getSleepTargetDate();
        if (sleepTargetDate != null) {
            stmt.bindString(15, sleepTargetDate);
        }
 
        String headImage = entity.getHeadImage();
        if (headImage != null) {
            stmt.bindString(16, headImage);
        }
 
        String headImage_s = entity.getHeadImage_s();
        if (headImage_s != null) {
            stmt.bindString(17, headImage_s);
        }
        stmt.bindLong(18, entity.getUseNetwork() ? 1L: 0L);
 
        String lastConnectDevice = entity.getLastConnectDevice();
        if (lastConnectDevice != null) {
            stmt.bindString(19, lastConnectDevice);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public UserInformationBean readEntity(Cursor cursor, int offset) {
        UserInformationBean entity = new UserInformationBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // nickName
            cursor.getLong(offset + 3), // creatTime
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // gender
            cursor.getInt(offset + 5), // bodyHeight
            cursor.getFloat(offset + 6), // bodyWeight
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // birthday
            cursor.getInt(offset + 8), // age
            cursor.getInt(offset + 9), // stepTarget
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // stepTargetDate
            cursor.getInt(offset + 11), // bodyWeightTarget
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // bodyWeightTargetDate
            cursor.getLong(offset + 13), // sleepTarget
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // sleepTargetDate
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // headImage
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // headImage_s
            cursor.getShort(offset + 17) != 0, // useNetwork
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18) // lastConnectDevice
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, UserInformationBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNickName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCreatTime(cursor.getLong(offset + 3));
        entity.setGender(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBodyHeight(cursor.getInt(offset + 5));
        entity.setBodyWeight(cursor.getFloat(offset + 6));
        entity.setBirthday(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAge(cursor.getInt(offset + 8));
        entity.setStepTarget(cursor.getInt(offset + 9));
        entity.setStepTargetDate(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setBodyWeightTarget(cursor.getInt(offset + 11));
        entity.setBodyWeightTargetDate(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSleepTarget(cursor.getLong(offset + 13));
        entity.setSleepTargetDate(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setHeadImage(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setHeadImage_s(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setUseNetwork(cursor.getShort(offset + 17) != 0);
        entity.setLastConnectDevice(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(UserInformationBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(UserInformationBean entity) {
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
