package com.isport.brandapp.util;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;

public class BleUtil {

    /**
     * 从TelephonyManager中实例化ITelephony,并返回
     */
    static public ITelephony getITelephony(TelephonyManager telMgr)
            throws Exception {
        try {
            @SuppressLint("SoonBlockedPrivateApi") Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod(
                    "getITelephony");
            getITelephonyMethod.setAccessible(true);// 私有化函数也能使用
            return (ITelephony) getITelephonyMethod.invoke(telMgr);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    //自动接听
    public static void autoAnswerPhone(Context c, TelephonyManager tm) {
        try {
            //Log.i(TAG, "autoAnswerPhone");
            ITelephony itelephony = getITelephony(tm);
            // itelephony.silenceRinger();
            itelephony.answerRingingCall();
        } catch (NoSuchMethodError noSuchMethodError){
            noSuchMethodError.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
               // Log.e(TAG, "用于Android2.3及2.3以上的版本上");
                Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
                c.sendOrderedBroadcast(intent,
                        "android.permission.CALL_PRIVILEGED");
                intent = new Intent("android.intent.action.MEDIA_BUTTON");
                keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
                c.sendOrderedBroadcast(intent,
                        "android.permission.CALL_PRIVILEGED");
                Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
                localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                localIntent1.putExtra("state", 1);
                localIntent1.putExtra("microphone", 1);
                localIntent1.putExtra("name", "Headset");
                c.sendOrderedBroadcast(localIntent1,
                        "android.permission.CALL_PRIVILEGED");
                Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
                KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                localIntent2.putExtra("android.intent.extra.KEY_EVENT",
                        localKeyEvent1);
                c.sendOrderedBroadcast(localIntent2,
                        "android.permission.CALL_PRIVILEGED");
                Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
                KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                localIntent3.putExtra("android.intent.extra.KEY_EVENT",
                        localKeyEvent2);
                c.sendOrderedBroadcast(localIntent3,
                        "android.permission.CALL_PRIVILEGED");
                Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
                localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                localIntent4.putExtra("state", 0);
                localIntent4.putExtra("microphone", 1);
                localIntent4.putExtra("name", "Headset");
                c.sendOrderedBroadcast(localIntent4,
                        "android.permission.CALL_PRIVILEGED");
            } catch (Exception e2) {
                e2.printStackTrace();
                Intent meidaButtonIntent = new Intent(
                        Intent.ACTION_MEDIA_BUTTON);
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_HEADSETHOOK);
                meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                c.sendOrderedBroadcast(meidaButtonIntent, null);
            }
        }
    }


    public static  void endCall(Context context) {
        TelephonyManager telMag = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method mthEndCall = null;
        try {
            mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);
            mthEndCall.setAccessible(true);
            ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,
                    (Object[]) null);
            assert iTel != null;
            iTel.endCall();
//            Log.e("", ""+iTel.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
//        Log.e("", "endCall test");
    }

    //挂断电话
    public static void dPhone(){
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            assert telephony != null;
            telephony.endCall();
        }
        catch (Throwable noSuchMethodError){
            noSuchMethodError.printStackTrace();
        }

    }

    /**
     * 通过反射的方式挂断电话
     */
    public static void endcall() {
        try {
            //获取到ServiceManager
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            //获取到ServiceManager里面的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            //通过反射的方法调用方法
            IBinder iBinder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //自动挂断
    public static void endPhone(Context c,TelephonyManager tm) {
        try {
            Method m1 = tm.getClass().getDeclaredMethod("getITelephony",(Class[]) null);
            m1.setAccessible(true);
            Object iTelephony = m1.invoke(tm);

            Method m3 = iTelephony.getClass().getDeclaredMethod("endCall");
            //m2.invoke(iTelephony);
            m3.invoke(iTelephony);

//            Log.i(TAG, "endPhone");
//            ITelephony iTelephony = null;
//            Method getITelephonyMethod = TelephonyManager.class
//                    .getDeclaredMethod("getITelephony", (Class[]) null);
//            getITelephonyMethod.setAccessible(true);
//            iTelephony = (ITelephony) getITelephonyMethod.invoke(tm,
//                    (Object[]) null);
//            if(iTelephony != null){
//                // 挂断电话
//                iTelephony.endCall();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //让手机静音
    public static void setPhoneMute(Context context){
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
            if(audioManager != null){
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                audioManager.getStreamVolume(AudioManager.STREAM_RING);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
