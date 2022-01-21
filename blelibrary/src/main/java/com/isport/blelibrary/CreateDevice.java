package com.isport.blelibrary;

import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.impl.DFUDevice;
import com.isport.blelibrary.deviceEntry.impl.S002BDevice;
import com.isport.blelibrary.deviceEntry.impl.ScaleDevice;
import com.isport.blelibrary.deviceEntry.impl.SleepDevice;
import com.isport.blelibrary.deviceEntry.impl.W307JDevice;
import com.isport.blelibrary.deviceEntry.impl.W311Device;
import com.isport.blelibrary.deviceEntry.impl.W520Device;
import com.isport.blelibrary.deviceEntry.impl.W526Device;
import com.isport.blelibrary.deviceEntry.impl.W557Device;
import com.isport.blelibrary.deviceEntry.impl.W560BDevice;
import com.isport.blelibrary.deviceEntry.impl.W560Device;
import com.isport.blelibrary.deviceEntry.impl.W7018Device;
import com.isport.blelibrary.deviceEntry.impl.W812BDevice;
import com.isport.blelibrary.deviceEntry.impl.W812Device;
import com.isport.blelibrary.deviceEntry.impl.W813Device;
import com.isport.blelibrary.deviceEntry.impl.W814Device;
import com.isport.blelibrary.deviceEntry.impl.W817Device;
import com.isport.blelibrary.deviceEntry.impl.W819Device;
import com.isport.blelibrary.deviceEntry.impl.W910Device;
import com.isport.blelibrary.deviceEntry.impl.Watch516Device;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.Utils;

import java.util.Arrays;

public class CreateDevice {

    private static final String TAG = "CreateDevice";

    public CreateDevice() {

    }

    public BaseDevice createDevcie(String name, String address, String filterStr, boolean isDFU) {
        Logger.myLog(TAG,"---createDevcie="+name+"\n"+address+"\n"+filterStr+"\n"+isDFU);
        BaseDevice baseDevice = null;

        if(name.startsWith(Constants.WATCH_7018_FILTER)){

            return create7018Device(name,address);
        }


        if (name.startsWith(Constants.SCALE_FILTER) || name.startsWith
                ("Chipsea")) {
            baseDevice = createScaleDevice(name, address);
            return baseDevice;
        } else if (name.startsWith(Constants.SLEEP_FILTER)) {
            baseDevice = createSleepDevice(name, address);
            return baseDevice;
        } else if (name.startsWith(Constants.BRAND_FILTER)) {
            createW311Device
                    (name, address);
            return baseDevice;
        }
        if (name.startsWith(Constants.SCALE_FILTER) || name.startsWith("Chipsea")) {
            baseDevice = createScaleDevice(name, address);
            Logger.myLog(TAG,"返回体脂称" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.SLEEP_FILTER)) {
            baseDevice = createSleepDevice
                    (name, address);
            Logger.myLog(TAG,"返回睡眠带" + baseDevice.deviceType + " baseDevice ID == " + baseDevice.toString());
            return baseDevice;
        } else if (name.startsWith(Constants.BRAND_FILTER)) {
            baseDevice = createW311Device
                    (name, address);
            Logger.myLog(TAG,"返回手环W311" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.BRAND_520_FILTER)) {
            baseDevice = createW520Device(name, address);
            Logger.myLog(TAG,"返回手环W520" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.BRAND_W307J_FILTER)) {
            baseDevice = createW307JDevice(name, address);
            Logger.myLog(TAG,"返回手环307J" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.WATCH_FILTER)) {
            baseDevice = createWatch516Device
                    (name, address);
            Logger.myLog(TAG,"返回手表" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.ROPE_S002_FILTER)) {
            baseDevice = createS002(name, address);
            Logger.myLog(TAG,"返回手表" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.WATCH_812_FILTER)) {
            if(name.length() == 4 && name.toLowerCase().equals("w812")){
                baseDevice = createW812(name, address);
                return baseDevice;
            }

            String[] names = name.split(" ");
            if (names[0].equals(Constants.WATCH_812_FILTER) && Constants.WATCH_812_FILTER.equals(filterStr)) {
                baseDevice = createW812(name, address);
                Logger.myLog(TAG,"返回createW812 filterStr:" + filterStr + "----names[0]" + names[0]);
                return baseDevice;
            } else if (names[0].equals(Constants.WATCH_812B_FILTER) && Constants.WATCH_812B_FILTER.equals(filterStr)) {
                baseDevice = createW812B(name, address);
                Logger.myLog(TAG,"返回createW812B filterStr:" + filterStr + "----names[0]" + names[0]);
                return baseDevice;
            } else if (filterStr.equals("all")) {
                if (names[0].equals(Constants.WATCH_812_FILTER)) {
                    baseDevice = createW812(name, address);
                    return baseDevice;
                } else if (names[0].equals(Constants.WATCH_812B_FILTER)) {
                    baseDevice = createW812B(name, address);
                    return baseDevice;
                }
            }

        } else if (name.startsWith(Constants.WATCH_813_FILTER)) {
            baseDevice = createW813(name, address);
            Logger.myLog(TAG,"返回createW813" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.WATCH_819_FILTER)) {
            baseDevice = createW819(name, address);
            Logger.myLog(TAG,"返回createW819" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.BRAND_814_FILTER)) {
            baseDevice = createW814(name, address);
            Logger.myLog(TAG,"返回createW814" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.WATCH_910_FILTER) || name.startsWith(Constants.WATCH_9102_FILTER)) {
            baseDevice = createW910Device(name, address);
            Logger.myLog(TAG,"返回createW910Device" + baseDevice.deviceType);
            return baseDevice;
        } else if (name.startsWith(Constants.WATCH_556_FILTER)) {
            baseDevice = createW526(name, address);
            Logger.myLog(TAG,"返回createW526Device" + baseDevice.deviceType);
            return baseDevice;
        } else if (Utils.isContainsDFU(name)) {
            if (isDFU) {
                baseDevice = createDFUDevice(name, address);
                Logger.myLog(TAG,"返回createisContainsDFU" + baseDevice.deviceType + "baseDevice:" + baseDevice);
                return baseDevice;
            }

        } else if (name.startsWith(Constants.WATCH_817_FILTER)) {
            baseDevice = createW817(name, address);
            return baseDevice;
        } else if (name.startsWith(Constants.WATCH_557_FILTER)) {
            baseDevice = createW557(name, address);
            return baseDevice;
        }
        //W560
        else if(name.contains(Constants.WATCH_560_FILTER) || name.contains(Constants.WATCH_W560_FILTER_2)){
            if( name.length()>10 ){
                String subStr = name.substring(0,11);
                if(subStr.equals(Constants.WATCH_W560_FILTER_2)){
                    baseDevice =  createW560(name, address);
                    return baseDevice;
                }

            }
            String[] names = name.split(" ");
            Logger.myLog(TAG,"-----截取="+ Arrays.toString(names));

            if (names[0].equals(Constants.WATCH_560_FILTER) && Constants.WATCH_560_FILTER.equals(filterStr)) {
                baseDevice = createW560(name, address);
                Logger.myLog(TAG,"WATCH_560_FILTER filterStr:" + filterStr + "----names[0]" + names[0]);
                return baseDevice;
            } else if (names[0].equals(Constants.WATCH_560B_FILTER) && Constants.WATCH_560B_FILTER.equals(filterStr)) {
                baseDevice = createW560B(name, address);
                Logger.myLog(TAG,"WATCH_560B_FILTER filterStr:" + filterStr + "----names[0]" + names[0]);
                return baseDevice;
            }
            //W560C与560B同样功能
            else if(names[0].toLowerCase().equals("w560c")){
                baseDevice = createW560B(name, address);
                return baseDevice;
            }

            else if (filterStr.equals("all")) {
                if (names[0].equals(Constants.WATCH_560B_FILTER)) {
                    baseDevice = createW560B(name, address);
                    return baseDevice;
                }
                else if (names[0].equals(Constants.WATCH_560_FILTER)) {
                    baseDevice = createW560(name, address);
                    return baseDevice;
                }
            }

        }
        return baseDevice;
    }


    public BaseDevice createScaleDevice(String name, String mac) {
        return new ScaleDevice(name, mac);
    }

    public BaseDevice createSleepDevice(String name, String mac) {
        return new SleepDevice(name, mac);
    }


    public BaseDevice createWatch516Device(String name, String mac) {
        return new Watch516Device(name, mac);
    }

    public BaseDevice createW311Device(String name, String mac) {
        return new W311Device(name, mac);
    }

    public BaseDevice createW812(String name, String mac) {
        return new W812Device(name, mac);
    }

    public BaseDevice createW526(String name, String mac) {
        return new W526Device(name, mac);
    }

    public BaseDevice createW817(String name, String mac) {
        return new W817Device(name, mac);
    }

    public BaseDevice createW557(String name, String mac) {
        return new W557Device(name, mac);
    }

    public BaseDevice createW560B(String name, String mac) {
        return new W560BDevice(name, mac);
    }
    public BaseDevice createW560(String name, String mac) {
        return new W560Device(name, mac);
    }

    public BaseDevice createW812B(String name, String mac) {
        return new W812BDevice(name, mac);
    }

    public BaseDevice createS002(String name, String mac) {
        return new S002BDevice(name, mac);
    }

    public BaseDevice createW819(String name, String mac) {
        return new W819Device(name, mac);
    }

    public BaseDevice createW813(String name, String mac) {
        return new W813Device(name, mac);
    }

    public BaseDevice createW814(String name, String mac) {
        return new W814Device(name, mac);
    }

    public BaseDevice createDFUDevice(String name, String mac) {
        return new DFUDevice(name, mac);
    }

    public BaseDevice createW520Device(String name, String mac) {
        return new W520Device(name, mac);
    }

    public BaseDevice createW307JDevice(String name, String mac) {
        return new W307JDevice(name, mac);
    }

    public BaseDevice createW910Device(String name, String mac) {
        return new W910Device(name, mac);
    }

    public BaseDevice create7018Device(String name,String mac){
        return new W7018Device(name,mac);
    }
}
