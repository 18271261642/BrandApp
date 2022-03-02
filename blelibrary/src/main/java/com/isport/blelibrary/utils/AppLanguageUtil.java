package com.isport.blelibrary.utils;

import android.content.Context;

import com.crrepa.ble.conn.type.CRPDeviceLanguageType;

import java.util.Locale;

public class AppLanguageUtil {

    public static String getCurrentLanguage() {
        Locale locale = Locale.getDefault();

        String lang = locale.getLanguage() + "-" + locale.getCountry();
        // String lang = locale.getLanguage() + "-" + locale.getCountry();

        return lang;

    }


    public static int sendTypeLanguage() {
        String language = getCurrentLanguage();
        if (language.toLowerCase().contains("zh"))
            return CRPDeviceLanguageType.LANGUAGE_CHINESE;
        else
            return CRPDeviceLanguageType.LANGUAGE_ENGLISH;
    }

    public static String getCurrentLanguageStr() {
        String language = getCurrentLanguage();
        if (language.contains("zh"))
            return "zh";
        else
            return "en";
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.contains("zh"))
            return true;
        else
            return false;
    }
}
