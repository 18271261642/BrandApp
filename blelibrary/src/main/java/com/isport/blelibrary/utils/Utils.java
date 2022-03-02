package com.isport.blelibrary.utils;

import android.text.TextUtils;

import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;

/**
 * @Author
 * @Date 2018/9/30
 * @Fuction
 */

public class Utils {

    private static final int DATA_TYPE_FLAGS = 0x01;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL = 0x02;
    private static final int DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE = 0x03;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL = 0x04;
    private static final int DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE = 0x05;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL = 0x06;
    private static final int DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE = 0x07;
    private static final int DATA_TYPE_LOCAL_NAME_SHORT = 0x08;
    private static final int DATA_TYPE_LOCAL_NAME_COMPLETE = 0x09;
    private static final int DATA_TYPE_TX_POWER_LEVEL = 0x0A;
    private static final int DATA_TYPE_SERVICE_DATA = 0x16;
    private static final int DATA_TYPE_MANUFACTURER_SPECIFIC_DATA = 0xFF;

    //Mason add for byte display
    public static String bytes2HexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] addFF(byte[] bt, int start, int end) {
        for (int i = start; i <= end; i++) {
            bt[i] = (byte) 0xff;
        }
        return bt;
    }

    public static byte[] addOO(byte[] bt, int start, int end) {
        for (int i = start; i <= end; i++) {
            bt[i] = (byte) 0x00;
        }
        return bt;
    }

    // 两个数字的分别的字符形式传入，转出byte值,src0是高位，src1是低位
    public static byte uniteBytes(char src0, char src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }

    public static int byte2Int(byte bt) {
        return bt & 0x000000ff;
    }

    /**
     * 将byte转换为一个长度为8的byte数组，数组每个值代表bit
     */
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }


    public static void sleep(long time) {
        try {
            Thread.currentThread();
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* public static String parseFromBytes(byte[] scanRecord) {
         if (scanRecord == null) {
             return null;
         }

         int currentPos = 0;
         int advertiseFlag = -1;
         List<ParcelUuid> serviceUuids = new ArrayList<ParcelUuid>();
         String localName = null;
         int txPowerLevel = Integer.MIN_VALUE;

         SparseArray<byte[]> manufacturerData = new SparseArray<byte[]>();
         Map<ParcelUuid, byte[]> serviceData = new ArrayMap<ParcelUuid, byte[]>();
         List<UUID> list16Uuids = new ArrayList<>();
         List<UUID> list32Uuids = new ArrayList<>();
         List<UUID> list128Uuids = new ArrayList<>();
         try {
             while (currentPos < scanRecord.length) {
                 // length is unsigned int.
                 int length = scanRecord[currentPos++] & 0xFF;
                 if (length == 0) {
                     break;
                 }
                 // Note the length includes the length of the field type itself.
                 int dataLength = length - 1;
                 // fieldType is unsigned int.
                 int fieldType = scanRecord[currentPos++] & 0xFF;
                 switch (fieldType) {
                     case DATA_TYPE_FLAGS:
                         advertiseFlag = scanRecord[currentPos] & 0xFF;
                         break;
                     case DATA_TYPE_SERVICE_UUIDS_16_BIT_PARTIAL:
                     case DATA_TYPE_SERVICE_UUIDS_16_BIT_COMPLETE:

                         break;
                     case DATA_TYPE_SERVICE_UUIDS_32_BIT_PARTIAL:
                     case DATA_TYPE_SERVICE_UUIDS_32_BIT_COMPLETE:
                         break;
                     case DATA_TYPE_SERVICE_UUIDS_128_BIT_PARTIAL:
                     case DATA_TYPE_SERVICE_UUIDS_128_BIT_COMPLETE:
                         break;
                     case DATA_TYPE_LOCAL_NAME_SHORT:
                     case DATA_TYPE_LOCAL_NAME_COMPLETE:
                         localName = new String(
                                 extractBytes(scanRecord, currentPos, dataLength));
                         break;
                     case DATA_TYPE_TX_POWER_LEVEL:
                         txPowerLevel = scanRecord[currentPos];
                         break;
                     case DATA_TYPE_SERVICE_DATA:
                         // The first two bytes of the service data are service data UUID in little
                         // endian. The rest bytes are service data.

                         break;
                     case DATA_TYPE_MANUFACTURER_SPECIFIC_DATA:
                         // The first two bytes of the manufacturer specific data are
                         // manufacturer ids in little endian.
                         int manufacturerId = ((scanRecord[currentPos + 1] & 0xFF) << 8) +
                                 (scanRecord[currentPos] & 0xFF);
                         byte[] manufacturerDataBytes = extractBytes(scanRecord, currentPos + 2,
                                 dataLength - 2);
                         manufacturerData.put(manufacturerId, manufacturerDataBytes);
                         break;
                     default:
                         // Just ignore, we don't handle such data type.
                         break;
                 }
                 currentPos += dataLength;
             }

             if (serviceUuids.isEmpty()) {
                 serviceUuids = null;
             }
             return localName;
         } catch (Exception e) {

             return null;
         }
     }
 */
    private static byte[] extractBytes(byte[] scanRecord, int start, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(scanRecord, start, bytes, 0, length);
        return bytes;
    }

    public static String replaceDeviceNameToCC431(String deviceName, float version) {
        if (deviceName == null)
            return "";
        deviceName = deviceName.split("_")[0];
        return deviceName;
    }

    public static String replaceDeviceMac(String mac) {
        if (mac == null)
            return "";
        mac = mac.replaceAll(":", "").toLowerCase();
        return mac;
    }

    public static String replaceDeviceMacUpperCase(String mac) {
        if (TextUtils.isEmpty(mac))
            return "";
        mac = mac.replaceAll(":", "").toUpperCase();
        return mac;
    }


    public static boolean isContainsDFU(String deviceName) {
        //Logger.myLog("isContainsDFU:" + deviceName + "deviceName.contains(Constants.DFU_W814_MODE):" + deviceName.contains(Constants.DFU_W814_MODE));
        deviceName = deviceName.toUpperCase();
        return (deviceName.contains(Constants.DFU_MODE) || deviceName.contains(Constants.DFU_W814_MODE));
    }

    public static String resetDeviceMac(String mac) {
        if (TextUtils.isEmpty(mac))
            return "";
        if (mac.contains(":")) {
            return mac;
        }
        char[] chars = mac.toUpperCase().toCharArray();
        if (chars.length != 12) {
            return "";
        }
        for (int i = 0; i < chars.length; i++) {
//            Logger.myLog("chars "+chars[i]);
        }
        char[] result = new char[chars.length + 5];
        result[0] = chars[0];
        result[1] = chars[1];
        result[2] = ':';
        result[3] = chars[2];
        result[4] = chars[3];
        result[5] = ':';
        result[6] = chars[4];
        result[7] = chars[5];
        result[8] = ':';
        result[9] = chars[6];
        result[10] = chars[7];
        result[11] = ':';
        result[12] = chars[8];
        result[13] = chars[9];
        result[14] = ':';
        result[15] = chars[10];
        result[16] = chars[11];
        String s1 = String.valueOf(result);
        return s1;
    }

    public static String resetDeviceVersion(String version) {
        if (version == null)
            return "";
        char[] chars = version.toUpperCase().toCharArray();
        char[] result = new char[chars.length + 2];
        result[0] = chars[0];
        result[1] = '.';
        result[2] = chars[1];
        result[3] = '.';
        result[4] = chars[2];
        String s1 = String.valueOf(result);
        return s1;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0 || str.trim().equals("null");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    //private static DbUtils dbUtils;
    public static int byteArrayToInt(byte[] data) {
        int value = 0;
        if (data != null && data.length > 0) {
            int len = data.length - 1;
            for (int i = len; i >= 0; i--) {
                value += ((data[len - i] & 0x00ff) << (8 * i));
            }
        }
        return value;
    }

    public static int byteArrayToInt1(byte[] data) {
        int value = 0;
        value = (byteToInt(data[1]) << 8) + byteToInt(data[0]);
        return value;
    }

    public static int byteToInt(byte data) {
        return (data & 0x00ff);
    }

    public static String byteToString(byte data) {
        return String.format("%02X ", data);
    }

    public static String getBleDeviceName(int type, byte[] record) {
        byte[] data = null;
        int index = 0;
        while (index < record.length) {
            int len = record[index] & 0xFF;
            int tp = record[index + 1] & 0xFF;
            if (index + len + 1 > 31) {
                break;
            } else if (len == 0) {
                break;
            }
            if (type == tp) {
                data = new byte[len - 1];
                for (int i = 0; i < len - 1; i++) {
                    data[i] = record[index + 2 + i];
                }
                break;
            }
            index += (len + 1);
        }

        if (data != null) {
            return new String(data);
        }

        return null;
    }

    public static boolean isGB2312(String str) {
        for (int i = 0; i < str.length(); i++) {
            String bb = str.substring(i, i + 1);
// 生成一个Pattern,同时编译一个正则表达式,其中的u4E00("一"的unicode编码)-\u9FA5("龥"的unicode编码)
            boolean cc = java.util.regex.Pattern.matches("[\u4E00-\u9FA5]", bb);
            if (cc == false) {
                return cc;
            }
        }
        return true;
    }



    public static boolean isCanRe(int deviceType) {
        if (deviceType == IDeviceType.TYPE_BRAND_W813 || deviceType == IDeviceType.TYPE_BRAND_W814 || deviceType == IDeviceType.TYPE_BRAND_W812 || deviceType == IDeviceType.TYPE_BRAND_W819 ||
                deviceType == IDeviceType.TYPE_BRAND_W910||deviceType == IDeviceType.TYPE_BRAND_W817) {
            return true;
        } else {
            return false;
        }
    }

    //dialog 升級
    public static boolean isDialogUpgrade(int deviceType) {
        if (deviceType == IDeviceType.TYPE_BRAND_W813 || deviceType == IDeviceType.TYPE_BRAND_W814 || deviceType == IDeviceType.TYPE_BRAND_W819) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isSmallScreen(int deviceType) {
        if ( deviceType == IDeviceType.TYPE_BRAND_W814) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isContainsW81(String deviceName) {
        if (deviceName.startsWith(Constants.WATCH_9101_FILTER)
                || deviceName.startsWith(Constants.WATCH_9102_FILTER)
                || deviceName.equals(Constants.WATCH_819_FILTER)
                || deviceName.equals(Constants.WATCH_813_FILTER)
                || deviceName.equals(Constants.WATCH_812_FILTER)
                || deviceName.equals(Constants.BRAND_811_FILTER)
                || deviceName.equals(Constants.BRAND_814_FILTER)
                || deviceName.equals(Constants.WATCH_817_FILTER)) {
            return true;

        } else {
            return false;
        }
    }
}
