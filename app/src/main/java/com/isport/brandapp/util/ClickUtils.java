package com.isport.brandapp.util;

/**
 * Created by Admin
 * Date 2021/11/30
 */
public class ClickUtils {


    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 500;
    }


    public static boolean isFastDoubleClick(long showTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= showTime;
    }
}
