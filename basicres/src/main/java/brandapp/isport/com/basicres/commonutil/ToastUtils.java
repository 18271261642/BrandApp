package brandapp.isport.com.basicres.commonutil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Toast工具类，下一个Toast提示前会取消掉上一个Toast
 */
@SuppressLint("InflateParams")
public class ToastUtils {

    private static Handler handler = new Handler(Looper.getMainLooper());

    // private static Toast toast = null;
    private static volatile Toast toast = null;

    public static void showToast(final Context context, final String msg) {
        if (null == context) {
            return;
        }
        showToast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(final Context context, final int msg) {
        if (null == context) {
            return;
        }
        showToast(context, context.getString(msg), Toast.LENGTH_SHORT);
    }

    public static void showToastLong(final Context context, final String msg) {
        if (null == context) {
            return;
        }
        showToast(context, msg, Toast.LENGTH_LONG);
    }

    public static void showToastLong(final Context context, final int msg) {
        if (null == context) {
            return;
        }
        showToast(context, context.getString(msg), Toast.LENGTH_LONG);
    }

    public static void showToast(final Context context, final String msg, final int len) {
        try {
            if (null == context || null == handler || TextUtils.isEmpty(msg) || msg.contains("没有访问权限！")) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (ToastUtils.class) {
                        if (toast != null) {
                            if (VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                                cancelToast();
                            }
                            toast.setText(msg);
                            toast.setDuration(len);
                        } else {
                            toast = Toast.makeText(UIUtils.getContext(), msg, len);
                            toast.setDuration(len);
                        }
                        //  toast = Toast.makeText(context.getApplicationContext(), msg, len);
                        // toast.setDuration(len);
                     //   toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("showToast e = " + e.getMessage());
        }
    }

    public static void cancelToast() {
        try {
            if (null == toast || null == handler) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                    toast = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reset() {

        cancelToast();
    }


}