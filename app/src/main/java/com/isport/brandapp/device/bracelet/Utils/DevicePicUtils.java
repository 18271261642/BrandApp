package com.isport.brandapp.device.bracelet.Utils;

import android.widget.ImageView;

import com.isport.brandapp.R;

import phone.gym.jkcq.com.commonres.common.JkConfiguration;

public class DevicePicUtils {


    public static void setHeadPic(int currentType, ImageView ivWatch) {
        switch (currentType) {
            case JkConfiguration.DeviceType.WATCH_W516:
                ivWatch.setImageResource(R.drawable.icon_upgrade_w516);
                break;
            case JkConfiguration.DeviceType.Watch_W556:
                ivWatch.setImageResource(R.drawable.icon_w526_pic);
                break;
            case JkConfiguration.DeviceType.BRAND_W311:
                ivWatch.setImageResource(R.drawable.icon_w311_pic);
                break;
            case JkConfiguration.DeviceType.Brand_W520:
                ivWatch.setImageResource(R.drawable.icon_w520_pic);
                break;
            case JkConfiguration.DeviceType.BRAND_W307J:
                ivWatch.setImageResource(R.drawable.icon_w307j_pic);
                break;
            case JkConfiguration.DeviceType.Brand_W811:
                ivWatch.setImageResource(R.drawable.icon_w811_pic);
                break;
            case JkConfiguration.DeviceType.Brand_W814:
                ivWatch.setImageResource(R.drawable.icon_w814_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W812:
                ivWatch.setImageResource(R.drawable.icon_w812_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W813:
                ivWatch.setImageResource(R.drawable.icon_w813_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W819:
                ivWatch.setImageResource(R.drawable.icon_w819_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W910:
                ivWatch.setImageResource(R.drawable.icon_w910_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W817:
                ivWatch.setImageResource(R.drawable.icon_w817_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W557:
                ivWatch.setImageResource(R.drawable.icon_w557_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W812B:
                ivWatch.setImageResource(R.drawable.icon_w812b_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W560B:
                ivWatch.setImageResource(R.drawable.icon_w560_pic);
                break;
            case JkConfiguration.DeviceType.Watch_W560:
                ivWatch.setImageResource(R.drawable.icon_device_w560);
                break;

        }
    }


}
