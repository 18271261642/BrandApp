package brandapp.isport.com.basicres;

import android.os.Build;
import android.text.TextUtils;
import android.util.LruCache;

import java.util.Iterator;
import java.util.Map;

import brandapp.isport.com.basicres.commonutil.Logger;

/*
 * 创建Activity管理器，所有Activity集中管理销毁
 * classes : com.snscity.globalexchange.base
 * @author 苗恒聚
 * V 1.0.0
 * Create at 2016-1-18 11:28
 */
public class ActivityManager {

    /**
     * Activity缓存，所有Activity必须继承于@link{BaseActivity}
     */
    LruCache<String, BaseActivity> activityLru;

    private static ActivityManager instance;

    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public ActivityManager() {
        activityLru = new LruCache<>(12);
    }

    // 添加Activity到容器中
    public synchronized void putActivity(String tag, BaseActivity activity) {
        if (null == activity || TextUtils.isEmpty(tag)) {
            return;
        }
        Logger.d("ActivityManager putActivity tag = " + tag);
        activityLru.put(tag, activity);
    }

    public synchronized void removeActivity(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        Logger.d("ActivityManager removeActivity tag = " + tag);
        activityLru.remove(tag);
    }

    public synchronized void finishAllActivity() {
        finishAllActivity(null);
    }






    /**
     * 关闭所有存在的Activity
     */
    public synchronized void finishAllActivity(final String className) {
        if (null == activityLru || activityLru.size() <= 0) {
            return;
        }
        try {
            Map<String, BaseActivity> activityMap = activityLru.snapshot();
            if (null == activityMap) {
                return;
            }

            for (Iterator<BaseActivity> i = activityMap.values().iterator(); i.hasNext(); ) {
                BaseActivity baseActivity = i.next();
                if (null == baseActivity) {
                    continue;
                }

                Logger.e("n = " + baseActivity.getClass().getSimpleName() + " ,m = " + (null == className ? "" : className));
                if (TextUtils.equals(baseActivity.getClass().getSimpleName(), className)) {
                    continue;
                }

                if (Build.VERSION.SDK_INT >= 17 && baseActivity.isDestroyed()) {
                    continue;
                }
                Logger.e("size = " + activityLru.size());
                baseActivity.finish();
                Logger.e("size = " + activityLru.size());
            }

            activityLru.trimToSize(activityLru.maxSize());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("ActivityManager finishAllActivity error. " + e.getMessage());
        }
    }
    public synchronized void finishAllActivity(final String className,final String className2) {
        if (null == activityLru || activityLru.size() <= 0) {
            return;
        }
        try {
            Map<String, BaseActivity> activityMap = activityLru.snapshot();
            if (null == activityMap) {
                return;
            }

            for (Iterator<BaseActivity> i = activityMap.values().iterator(); i.hasNext(); ) {
                BaseActivity baseActivity = i.next();
                if (null == baseActivity) {
                    continue;
                }

                Logger.e("n = " + baseActivity.getClass().getSimpleName() + " ,m = " + (null == className ? "" : className));
                if (TextUtils.equals(baseActivity.getClass().getSimpleName(), className)||TextUtils.equals(baseActivity.getClass().getSimpleName(), className2)) {
                    continue;
                }

                if (Build.VERSION.SDK_INT >= 17 && baseActivity.isDestroyed()) {
                    continue;
                }
                Logger.e("size = " + activityLru.size());
                baseActivity.finish();
                Logger.e("size = " + activityLru.size());
            }

            activityLru.trimToSize(activityLru.maxSize());
        } catch (Exception e) {
            Logger.d("ActivityManager finishAllActivity error. " + e.getMessage());
        }
    }

    /**
     * 返回当前打开的页面数量
     *
     * @return
     */
    public synchronized int getAllActivitySize() {
        if (null == activityLru) {
            return 0;
        }
        return activityLru.size();
    }
}