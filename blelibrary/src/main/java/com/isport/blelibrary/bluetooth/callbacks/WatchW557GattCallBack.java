package com.isport.blelibrary.bluetooth.callbacks;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import com.isport.blelibrary.BleConstance;
import com.isport.blelibrary.db.parse.ParseData;
import com.isport.blelibrary.db.parse.ParseS002Data;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType;
import com.isport.blelibrary.interfaces.BluetoothListener;
import com.isport.blelibrary.interfaces.BluetoothSettingSuccessListener;
import com.isport.blelibrary.managers.BaseManager;
import com.isport.blelibrary.observe.BatteryChangeObservable;
import com.isport.blelibrary.observe.GetRopeTargDataObservable;
import com.isport.blelibrary.observe.RopeNoDataObservable;
import com.isport.blelibrary.observe.RopeRealDataObservable;
import com.isport.blelibrary.observe.RopeStartOrEndSuccessObservable;
import com.isport.blelibrary.observe.W560HrSwtchObservable;
import com.isport.blelibrary.utils.Constants;
import com.isport.blelibrary.utils.DateUtil;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.ThreadPoolUtils;
import com.isport.blelibrary.utils.Utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author
 * @Date 2019/2/19
 * @Fuction
 */

public class WatchW557GattCallBack extends BaseGattCallback {

    private static final String TAG = "WatchW557GattCallBack";

    //***********************************************?????????******************************************************//


    public static final String W560_ALARM_OPERATE_ACTION = "com.isport.blelibrary.bluetooth.callbacks.w560_alarm";
    public static final String W560_ALARM_KEY = "is_success";


    protected BlockingQueue<DataBean> cmds = new LinkedBlockingQueue<>();


    private Handler sendHandler = null;//????????????handler
    private static final long PAIRTIMEOUT = 20000;//??????????????????
    protected int sendWhat;

    private static final int Order_send_adjust_mode = 1;//????????????
    private static final int Order_send_exit_adjust_mode = 2;//??????????????????
    private static final int Order_send_exit_adjust_mode_adjusting = 3;//????????????
    private static final int Order_null = 0;//???????????????
    private static boolean isPractisData = false;
    private static int syncRopeStep = 0;//123
    private static boolean isSyncData = false;

    private long mExitTime = 0;

    private List<byte[]> m24HDATA;
    private List<byte[]> mPractiseHDATA;


    public WatchW557GattCallBack(BluetoothListener bluetoothListener, Context context, BaseDevice baseDevice, BluetoothSettingSuccessListener successListener) {
        super(bluetoothListener, context, baseDevice);
        this.settingSuccessListener = successListener;
        if (sendHandler == null) {
            sendHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 1:
                            /*if (!hasSyncTime && isConnected) {
                                //??????????????????????????????????????????????????????????????????????????????
                                Logger.myLog("?????????????????????????????????????????????????????????????????????????????? !hasSyncTime:" + !hasSyncTime + "isConnected:" + isConnected);
                                disconnect(false);
                            }*/
                            break;
                        case 2:
                            //syncTime();
                            break;
                        case 3:   //??????OTA??????
                            writeTXCharacteristicItem(new byte[]{0x04, 0x00, (byte) 0xA1, (byte) 0xFE, 0x74, 0x69});
                            //sendHandler.sendEmptyMessageDelayed(0x01, PAIRTIMEOUT);//?????????15s?????????
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    @Override
    public void disconnect(boolean reconnect) {
        super.disconnect(reconnect);
        sendHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            try {
                Thread.sleep(1000);
                List<BluetoothGattService> list = gatt.getServices();
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        BluetoothGattService service = list.get(i);
                        Log.e("TAG service uuid", service.getUuid().toString());
                        if (service.getUuid().equals(Constants.mainW516UUID)) {
                            List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
                            for (int j = 0; j < characteristicList.size(); j++) {
                                BluetoothGattCharacteristic characteristic = characteristicList.get(j);
                                Log.e("TAG char uuid", characteristic.getUuid().toString());
                                if (characteristic.getUuid().equals(Constants.sendW516UUID)) {
                                    mSendChar = characteristic;
                                    Log.e(TAG, "*** mSendChar getCharacteristic***" + mSendChar.toString());
                                    mGattService = service;
                                } else if (characteristic.getUuid().equals(Constants.responceW516UUID)) {
                                    mResponceChar = characteristic;
                                    Log.e(TAG, "***mResponceChar getCharacteristic***" + mResponceChar.toString());
                                } else if (characteristic.getUuid().equals(Constants.receiveDataW516UUID)) {
                                    mReceiveDataChar = characteristic;
                                    Log.e(TAG, "***mResponceChar getCharacteristic***" + mReceiveDataChar.toString());
                                } else if (characteristic.getUuid().equals(Constants.realTimeDataW516UUID)) {
                                    mRealTimeDataChar = characteristic;
                                    Log.e(TAG, "***mRealTimeDataChar getCharacteristic***" + mRealTimeDataChar
                                            .toString());
                                }
                            }
                        } else if (service.getUuid().equals(Constants.HEART_RATE_SERVICE)) {
                            List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
                            for (int j = 0; j < characteristicList.size(); j++) {
                                BluetoothGattCharacteristic characteristic = characteristicList.get(j);
                                Log.e("TAG  char uuid", characteristic.getUuid().toString());
                                if (characteristic.getUuid().equals(Constants.HEART_RATE_CHARACTERISTIC)) {
                                    mHeartRateChar = characteristic;
                                    Log.e(TAG, "*** mHeartRateChar getCharacteristic***" + mHeartRateChar.toString());
                                }
                            }
                        }
                    }
                    if (mSendChar != null && mResponceChar != null && mReceiveDataChar != null && mRealTimeDataChar !=
                            null && mHeartRateChar != null) {
                        Log.e(TAG, "??????????????????,????????????" + bluetoothListener);
//                    enableNotifications(mResponceChar);
                        if (bluetoothListener != null)
                            bluetoothListener.servicesDiscovered();
                    } else {
//                    refreshGatt();
//                    disconnect();
                        if (bluetoothListener != null)
                            bluetoothListener.not_discoverServices();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            Logger.myLog(TAG,"onServicesDiscovered received: " + status);
//            refreshGatt();
//            disconnect();////??????????????????
        }

    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        Logger.myLog(TAG,"onDescriptorWrite" + gatt.getDevice().getName() + "---" + gatt.getDevice().getAddress());
        BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Logger.myLog(TAG,"***????????????***" + characteristic.getUuid().toString());
            if (characteristic.getUuid().equals(Constants.responceW516UUID)) {
                Logger.myLog(TAG,"***??????mReceiveDataChar***");
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        internalEnableNotifications(mReceiveDataChar);
                    }
                }, 200);
            } else if (characteristic.getUuid().equals(Constants.receiveDataW516UUID)) {
                Logger.myLog(TAG,"***??????mRealTimeDataChar***");

                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        internalEnableNotifications(mRealTimeDataChar);
                    }
                }, 200);
            } else if (characteristic.getUuid().equals(Constants.realTimeDataW516UUID)) {
                internalEnableNotifications(mHeartRateChar);
                ///?????????????????????????????????????????????
                isConnected = true;
                connectState = CONNECTED;
                Logger.myLog(TAG,"?????????????????????!");
                mReconnectHandler.removeMessages(0x01);
                try {
                    if (bluetoothListener != null) {
                        bluetoothListener.connected();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Logger.myLog(TAG,"***????????????***" + status);
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        byte[] values = characteristic.getValue();
        if (characteristic.getService().getUuid().equals(Constants.BATTERY_SERVICE) && characteristic.getUuid()
                .equals(Constants.BATTERY_LEVEL_CHARACTERISTIC)) {
            Logger.myLog(TAG,"***onBatteryChanged***" + (values[0] & 0xff));
            // BatteryChangeObservable.getInstance().getBattery((values[0] & 0xff));
        } else if (characteristic.getService().getUuid().equals(Constants.DEVICEINFORMATION_SERVICE) &&
                characteristic.getUuid()
                        .equals(Constants.FIRMWAREREVISION_CHARACTERISTIC)) {
            byte[] version = new byte[5];
            version[0] = values[0];
            version[1] = values[1];
            version[2] = values[2];
            version[3] = values[3];
            version[4] = values[4];
            Logger.myLog(TAG,"***FirmwareVersion111***" + new String(values) + " FirmwareVersion***" + Utils
                    .bytes2HexString(values));
            //??????????????????????????????????????? ????????????

           /* if (!Constants.isDFU) {
                sendHandler.sendEmptyMessageDelayed(0X02, 1000);
            }*/
//            ?????? 04-00-A1-FE-74-69
//            ?????????????????????????????? 02-FF-00-00
//            ????????????????????????02-FF-00-01

            //writeTXCharacteristicItem(new byte[]{0x04, 0x00, (byte) 0xA1, (byte) 0xFE, 0x74, 0x69});
            // sendHandler.sendEmptyMessageDelayed(0x01, PAIRTIMEOUT);//?????????15s?????????
//            syncTime();
            /*if (bluetoothListener != null) {
                bluetoothListener.onGetDeviceVersion(new String(values));
            }*/
        }
    }

    public void handleReceive(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        final String mac = gatt.getDevice().getAddress();
        byte[] values = characteristic.getValue();
        //Logger.myLog(TAG,"-----?????????????????????="+ Arrays.toString(values));
//        ?????? 04-00-A1-FE-74-69
//        ?????????????????????????????? 02-FF-00-00
//        ????????????????????????02-FF-00-01
        if (Constants.IS_DEBUG) {
            final StringBuilder stringBuilder = new StringBuilder(values.length);
            for (byte byteChar : values) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
//                    saveLog(new StringBuilder(DateUtil.dataToString(new Date(), "HH:mm:ss")).append(" ff01 REC ")
// .append
//                            (stringBuilder.toString()).append(" UUID ").append(characteristic.getUuid().toString()));
//            Logger.myLog(" ReceiverCmd " + stringBuilder.toString() + " UUID " + characteristic.getUuid()
//                    .toString());
        }
        if (characteristic.getService().getUuid().equals(Constants.mainW516UUID)) {
            if (characteristic.getUuid().equals(Constants.responceW516UUID)) {
                if (Constants.IS_DEBUG) {
                    final StringBuilder stringBuilder = new StringBuilder(values.length);
                    for (byte byteChar : values) {
                        stringBuilder.append(String.format("%02X ", byteChar));
                    }
                    saveLog(new StringBuilder(DateUtil.dataToString(new Date(), "yyy/MM/dd HH:mm:ss.SSS")).append(" ReceiverCmd ").append(stringBuilder.toString()));
                    Logger.myLog(TAG," ReceiverCmd " + stringBuilder.toString() + " UUID " + characteristic.getUuid()
                            .toString().substring(0, 8));
                }


              Logger.myLog(TAG,"-------??????="+ Arrays.toString(values));

                //??????????????????
                if(values.length>2 && (values[0] ==1 && (values[1] &0xff) == 82)){
                    Intent intent = new Intent();
                    intent.setAction(BleConstance.W560_DIS_CALL_ACTION);
                    intent.putExtra(BleConstance.W560_PHONE_STATUS,(values[2] & 0xff));
                    mContext.sendBroadcast(intent);
                }

                if(values[0] == 1 && values[1] == 20 && values[2] == 1){    //????????????
                    Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                    vibrator.vibrate(3 * 1000);
                }

                //????????????
                if(values[0] == 1 && values[1] == 19){  //????????????
                    Intent intent = new Intent();
                    intent.setAction(BleConstance.W560_MUSIC_CONTROL_STATUS);
                    intent.putExtra(BleConstance.W560_MUSIC_STATUS,values[2] & 0xff);
                    mContext.sendBroadcast(intent);
                }


                //????????????
                if (values != null && values.length >= 2) {
                    //TODO w526?????????????????????????????????
                    if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x00 && Utils.byte2Int
                            (values[3]) == 0x01) {//02-FF-00-01
                        //?????????????????????,???????????????????????????
                        /*if (sendHandler.hasMessages(0x01)) {
                            sendHandler.removeMessages(0x01);
                        }
                        //???????????????????????????????????????
                        if (!Constants.isDFU) {
                            sendHandler.sendEmptyMessageDelayed(0X02, 3000);
                        }*/
                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x32 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-32-00
                        //???????????????????????????????????????
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }

                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x11 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-11-00
                        //???????????????????????????  ?????????/????????????
                        if (sendWhat == Order_send_adjust_mode) {
                            sendWhat = Order_null;
                            if (bluetoothListener != null) {
                                bluetoothListener.onInDemoModeSuccess();
                            }
                        } else if (sendWhat == Order_send_exit_adjust_mode) {
                            //?????????????????????????????????????????????
                            // syncTime();
                        } else if (sendWhat == Order_send_exit_adjust_mode_adjusting) {
                            //?????????????????????????????????????????????
                        } else {
                            sendWhat = Order_null;
                            //syncTime();
                        }
                    }



                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x30 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-30-00
                        //??????????????????????????????????????????

                        Log.e("TAG bluetoothListener", "bluetoothListener=" + bluetoothListener);
                        if (bluetoothListener != null) {
                            bluetoothListener.onSyncTimeSuccess();
                        }

                        if (sendWhat == Order_send_exit_adjust_mode_adjusting || sendWhat == Order_send_exit_adjust_mode || sendWhat == Order_send_adjust_mode) {
                            //????????????????????????,??????????????????
                            sendWhat = Order_null;
                        } else {
                            if (!hasSyncTime) {
                                hasSyncTime = true;
//                            setGeneral();
                            }
                           /* if (bluetoothListener != null) {
                                bluetoothListener.onSettingUserInfoSuccess();
                            }*/

                            // queryUserInfo();//?????????????????????
                           /* if (bluetoothListener != null) {
                                bluetoothListener.onSyncTimeSuccess();
                            }*/
                        }
                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x33 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-33-00
                        //????????????????????????
                        if (bluetoothListener != null) {
                            bluetoothListener.onSettingUserInfoSuccess();
                        }
                    }


                    else if (Utils.byte2Int(values[0]) == 0x0B && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x05) {//0B-FF-05
//                          ??????????????????: brith day: 1981 ??? 12 ??? 12 ??????female????????? 100kg????????? 170??? ???????????? 190??? ???????????? 60
//                                * 0B-FF-05-BD-07-0C-0C-00-E8-03-AA-BE-3C
                        //??????????????????

//                        if (apUserSetting == null) {
//                            apUserSetting = new UserSettingW516();
//                        }

                        //??????????????????,?????????????????????????????????????????????????????????,????????????????????????
                        //?????? ?????? 1991 01 01 ?????? 1?????? height 175 weight 75 maxHeartRate 200  minHeartRate 50
                        if (Utils.byte2Int(values[3]) == 0x00 && Utils.byte2Int(values[4]) == 0x00) {
                            int max = Utils.byte2Int(values[11]) < 30 ? 190 : Utils.byte2Int(values[11]);
                            int min = Utils.byte2Int(values[12]) < 30 ? 60 : Utils.byte2Int(values[12]);
                            setUserInfo(1991, 1, 1, 1, 75, 175, max, min);
//                            apUserSetting.setYear(1991);
//                            apUserSetting.setMonth(1);
//                            apUserSetting.setDay(1);
//                            apUserSetting.setGender(1);
//                            apUserSetting.setWeight(75);
//                            apUserSetting.setHeight(175);
//                            apUserSetting.setMaxHeartRate(max);
//                            apUserSetting.setMinHeartRate(min);
                        } else {
//                            Log.e(TAG, Utils.byteArrayToInt(new byte[]{values[4], values[3]}) + "==year==" + Utils
//                                    .byteArrayToInt(new byte[]{values[3], values[4]}));
//                            Log.e(TAG, Utils.byteArrayToInt(new byte[]{values[9], values[8]}) / 10 + "==weight==" +
//                                    Utils.byteArrayToInt(new byte[]{values[8], values[9]}) / 10);
//                            apUserSetting.setYear(Utils.byteArrayToInt(new byte[]{values[4], values[3]}));
//                            apUserSetting.setWeight(Utils.byteArrayToInt(new byte[]{values[9], values[8]}) / 10);
//                            apUserSetting.setMonth(Utils.byte2Int(values[5]));
//                            apUserSetting.setDay(Utils.byte2Int(values[6]));
//                            apUserSetting.setGender(Utils.byte2Int(values[7]));
//                            apUserSetting.setHeight(Utils.byte2Int(values[10]));
//                            apUserSetting.setMaxHeartRate(Utils.byte2Int(values[11]));
//                            apUserSetting.setMinHeartRate(Utils.byte2Int(values[12]));
                            Logger.myLog(TAG,"??? " + Utils.byteArrayToInt(new byte[]{values[4], values[3]}) + " ??? " +
                                    Utils.byte2Int(values[5]) + " ??? " + Utils.byte2Int(values[6]) + " ??????" +
                                    " " +
                                    +(Utils.byteArrayToInt(new byte[]{values[9], values[8]}) / 10)
                                    + " ?????? " + Utils.byte2Int(values[10]) + " ?????? " + (Utils.byte2Int
                                    (values[7]) == 0 ? "???" : "???") + " ???????????? " + Utils.byte2Int(values[11]) + " ???????????? "
                                    + Utils.byte2Int(values[12]));
                        }
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetUserInfoSuccess();
                        }

                    }


                    /**
                     * ???????????????????????????????????????
                     * 0F FF 01
                     * [3][4] ????????????
                     * [5] = ??????????????????24 ????????????
                     * [6] = 0 ??? ?????????????????????,
                     *       1 ??? ???????????????????????????????????????
                     *       2 ??? ????????????????????????????????????
                     *       3 ??? ?????????
                     * [7].BIT0: 0 ??? OHR sensor ??????    1??? OHR sensor ??????
                     * [7].BIT1: 0 ??? G sensor ??????      1??? G sensor ??????
                     * [7].BIT2: 0 ??? Flash ??????         1??? Flash ??????
                     * [7].BIT3: 0 ??? font Flash ??????    1??? font Flash ??????
                     * [8] = 1;                 // ????????????
                     * [9~16] = V1.09;       // ????????????
                     */
                    else if (Utils.byte2Int(values[0]) == 0x0F && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x01) {//  0F-FF-01
                        //??????????????????
//                        0F-FF-01-20-1C-07-00-00-32-30-30-2E-30-30-2E-30-32

                        //?????????????????????????????????
                        int dataDay = Utils.byte2Int(values[5]);

                        byte[] version = new byte[8];

                        System.arraycopy(values,9,version,0,8);
                        byte[] versionV = new byte[1];
                        versionV[0] = values[8];
                        byte b = 12;
                        String binary = Integer.toBinaryString(b);//?????????2???????????????
                        System.out.println(binary);
                        int i = Integer.parseInt(binary, 2);//???????????????????????????
                        byte d = (byte) i;
                        System.out.println(d);
                        byte[] booleanArray = Utils.getBooleanArray(values[7]);
                        byte[] booleanArrayG = Utils.getBooleanArray((byte) 0x04);


                        StringBuilder stringBuilder = new StringBuilder();
                        for(Byte bt : version){
                            stringBuilder.append(String.format("%02X", bt));
                        }
                        Log.e(TAG,"--------????????????="+Arrays.toString(version)+" "+stringBuilder.toString()+"\n"+new String(version)+"\n"+hexStr2Str(stringBuilder.toString()));

                        boolean is560 = (version[5] == 0 && version[6] == 0 && version[7] == 0);
                        byte[] w560Byte = new byte[5];
                        if(is560){
                            System.arraycopy(version,0,w560Byte,0,5);
                        }

                        Logger.myLog(TAG,"?????????????????? == " + Utils.byteArrayToInt(new byte[]{values[4], values[3]}) + " " +
                                "?????????24???????????? == " + Utils.byte2Int(values[5]) + " ???????????? == " + Utils
                                .byte2Int(values[6]) + " ???????????? == " + Utils.byte2Int(values[7]) + " ????????????????????? == " +
                                Integer.toBinaryString((values[7] & 0xFF) + 0x100).substring(1) + " ????????????" +
                                " == " + Utils.byte2Int(versionV[0]) + " ???????????? == " + (is560 ? new String(w560Byte) :  hexStr2Str(stringBuilder.toString())) + " " +
                                "???????????????bit??? == " + Utils.byte2Int(booleanArray[0]) + " - " + Utils
                                .byte2Int(booleanArray[1]) + " - " + Utils.byte2Int(booleanArray[2]) +
                                "???????????????bit??? == " + Utils.byte2Int(booleanArrayG[0]) + " - " + Utils
                                .byte2Int(booleanArrayG[1]) + " - " + Utils.byte2Int(booleanArrayG[2]) + " - " + Utils
                                .byte2Int(booleanArrayG[3]) + " - " + Utils
                                .byte2Int(booleanArrayG[4]) + " - " + Utils.byte2Int(booleanArrayG[5]) + " - " + Utils
                                .byte2Int(booleanArrayG[6]) + " - " + Utils
                                .byte2Int(booleanArrayG[7]));
                        if (bluetoothListener != null)
                            bluetoothListener.onGetDeviceVersion((is560?new String(w560Byte):hexStr2Str(stringBuilder.toString())));
                        if (!Constants.isDFU) {
                            sendHandler.sendEmptyMessageDelayed(0X02, 200);
                            if (bluetoothListener != null) {
                                bluetoothListener.onGetSettingSuccess();
                            }
                        }


                    }



                    else if (Utils.byte2Int(values[0]) == 0x0a && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x04) {//09-FF-04

                        byte[] tempBtype = new byte[values.length];
                        for (int i = 0; i < values.length; i++) {
                            tempBtype[i] = values[i];
                        }
                        //???????????????????????????


                        //??????????????????
//                        09-FF-04-14-00-00-00-00-00-00-00
                        byte[] booleanArrayG = Utils.getBooleanArray(values[3]);


                        Logger.myLog(TAG,"??????????????????,???????????????bit??? == " + " ????????? == " + (Utils.byte2Int(booleanArrayG[7]) == 0 ? "??????" :
                                "??????")
                                + " ????????? == " + (Utils.byte2Int(booleanArrayG[6]) == 0 ? "??????" : "??????")
                                + " ????????? == " + (Utils.byte2Int(booleanArrayG[5]) == 0 ? "12??????" : "24??????")
                                + " ???????????? == " + (Utils.byte2Int(booleanArrayG[4]) == 0 ? "??????" : "??????")
                                + " 24???????????? == " + (Utils.byte2Int(booleanArrayG[3]) == 0 ? "??????" : "??????")
                                + "???????????? ==" + (Utils.byte2Int(booleanArrayG[2]) == 0 ? "?????????" : "?????????")
                                + " BaseManager.isTmepUnitl:" + BaseManager.isTmepUnitl);

                        BaseManager.isTmepUnitl = Utils.byte2Int(booleanArrayG[2]) + "";
                        if (mBaseDevice != null) {
                            ParseData.saveTempUtil(mBaseDevice.deviceName, BaseManager.mUserId, Utils.byte2Int(booleanArrayG[2]) + "");
                        }
                        if (bluetoothListener != null) {
                            // bluetoothListener.onGetSettingSuccess();
                            bluetoothListener.onsetGeneral(tempBtype);
                        }
                        ParseData.saveGeneral(tempBtype, mBaseDevice);
                    }


                    else if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x15 && Utils.byte2Int
                            (values[2]) == 0x01) {
                        Logger.myLog("??????????????????");
                        if (bluetoothListener != null) {
                            bluetoothListener.takePhoto();
                        }
                    }



                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x09) {
                        Logger.myLog("??????24????????????????????????");
                        W560HrSwtchObservable.getInstance().noDataUpdate(values[3] == 1 ? true : false);
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                        ParseData.save24HrSwitch(mBaseDevice.deviceName, values[3]);
                    }



                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x38) {
                        Logger.myLog("????????????????????????");
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                        //????????????????????????
                    }



                    else if (Utils.byte2Int(values[0]) == 0x04 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x0C) {

                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                            try {
                                int targetStep = Utils.byteArrayToInt(new byte[]{
                                        values[5], values[4], values[3]});
                                Logger.myLog(TAG,"???????????????????????? targetStep" + targetStep);
                                bluetoothListener.onSuccessTargetStep(targetStep);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //????????????????????????
                    }



                    else if (Utils.byte2Int(values[0]) == 0x05 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x0C) {
                        // W560??????????????????
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                            try {
                                int target = Utils.byteArrayToInt(new byte[]{
                                        values[5], values[4], values[3]});
                                //???????????? 0??????????????????1??????????????????2??????????????????
                                int goalType = Utils.byte2Int(values[6]);
//                                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, deviceBean.deviceType);


                                if (Utils.byte2Int(values[6]) == 0x00) {
                                    // ??????
                                    Logger.myLog("???????????????????????? targetStep" + target);
                                    bluetoothListener.onSuccessTargetStep(target);
                                } else if (Utils.byte2Int(values[6]) == 0x01) {
                                    // ??????
                                    Logger.myLog("???????????????????????? targetDistance" + target);
                                    bluetoothListener.onSuccessTargetDistance(target);
                                } else if (Utils.byte2Int(values[6]) == 0x03) {
                                    // ?????????
                                    Logger.myLog("??????????????????????????? targetCalorie" + target);
                                    bluetoothListener.onSuccessTargetCalorie(target);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }



                    else if (Utils.byte2Int(values[0]) == 0x08 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x02) {//08 FF 02
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                        //??????????????????????????????
//                        ?????????00-02
//                        ????????????????????????2018 ??? 12 ??? 13 ??? 14 ??? 15 ??? 16 ???
//                        08-FF-02-E2-07-0C-0D-0E-0F-10
                        Logger.myLog(TAG,"????????????????????? " + Utils.byteArrayToInt(new byte[]{values[4], values[3]}) + "???" +
                                Utils.byte2Int(values[5]) + "???" + Utils.byte2Int(values[6]) + "???" +
                                +Utils.byte2Int(values[7]) + "???" +
                                +Utils.byte2Int(values[8]) + "???" +
                                +Utils.byte2Int(values[9]) + "???");
                    }


                    //??????
                    else if (Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int(values[2]) == 0x07) {//12-FF-07
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                        //??????????????????
//                        ??????:
//                        ????????? 01-07-00 ???????????? 0???10 ??? 10 ?????????????????? 1 ????????????????????????123???
//                        Page 7 of 14
//                        ?????????12-FF-07-03-0A-0A-31-32-33-00-00-00-00-00-00-00-00-00-00-00
                        byte[] booleanArrayG = Utils.getBooleanArray(values[3]);
                        if (Utils.byte2Int(values[3]) == 2) {
                            //??????????????????
                            bluetoothListener.successAlam(Utils.byte2Int(values[3]));
                            //bluetoothListener.onGetSettingSuccess();
                        }
                        if (Utils.byte2Int(values[3]) == 0) {
                            //???????????????
                            Logger.myLog("????????????????????????");
                        } else {
                            byte[] msg = new byte[14];
                            msg[0] = values[6];
                            msg[1] = values[7];
                            msg[2] = values[8];
                            msg[3] = values[9];
                            msg[4] = values[10];
                            msg[5] = values[11];
                            msg[6] = values[12];
                            msg[7] = values[13];
                            msg[8] = values[14];
                            msg[9] = values[15];
                            msg[10] = values[16];
                            msg[11] = values[17];
                            msg[12] = values[18];
                            msg[13] = values[19];
                            Logger.myLog(TAG,"??????????????????,???????????????bit??? == " + " Sunday == " + (Utils.byte2Int(booleanArrayG[7]) == 0 ?
                                    "???" :
                                    "???")
                                    + " Monday  == " + (Utils.byte2Int(booleanArrayG[6]) == 0 ? "???" : "???")
                                    + " Tuesday  == " + (Utils.byte2Int(booleanArrayG[5]) == 0 ? "???" : "???")
                                    + " Wednesday  == " + (Utils.byte2Int(booleanArrayG[4]) == 0 ? "???" :
                                    "???")
                                    + " Thursday  == " + (Utils.byte2Int(booleanArrayG[3]) == 0 ? "???" :
                                    "???")
                                    + " Friday   == " + (Utils.byte2Int(booleanArrayG[2]) == 0 ? "???" : "???")
                                    + " Saturday   == " + (Utils.byte2Int(booleanArrayG[1]) == 0 ? "???" :
                                    "???")
                                    + " ??? " + Utils.byte2Int(values[4]) + " ??? " + Utils.byte2Int
                                    (values[5]) + " ???????????? " + new String(msg));
                        }
                        byte[] tempByte = ParseData.getTempByte(values);
                        ParseData.saveW526Alarm(tempByte, mBaseDevice);

                    }


                    else if (Utils.byte2Int(values[0]) == 0x12 && Utils.byte2Int(values[1]) == 0x35) {
                        Logger.myLog(TAG,"??????W560??????????????????");
                        byte[] tempByte = ParseData.getTempByte(values);
                        ParseData.saveW560Alarm(tempByte, mBaseDevice);

                    }

                    //??????????????????????????????????????????
                    else if (Utils.byte2Int(values[0]) == 0x02 &&
                            Utils.byte2Int(values[1]) == 0xFF &&
                            Utils.byte2Int(values[2]) == 0x35 ) {

                        //0?????????1??????
                        boolean isSuccess =  Utils.byte2Int(values[3]) == 0x00;
                        Intent intent = new Intent();
                        intent.setAction(W560_ALARM_OPERATE_ACTION);
                        intent.putExtra(W560_ALARM_KEY,isSuccess);
                        mContext.sendBroadcast(intent);
                        // W560??????????????????
                        Logger.myLog(TAG,"W560??????????????????");
                        if (bluetoothListener != null) {
                            bluetoothListener.onW560AlarmSettingSuccess();
                        }

                    }


                    else if (Utils.byte2Int(values[0]) == 0x06 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x06) {//0A-FF-06
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                        Logger.myLog("????????????????????????");
                        byte[] tempByte = ParseData.getTempByte(values);
                        ParseData.saveW526Disturb(tempByte, mBaseDevice);

//
                    }



                    else if (Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x10) {
                        Logger.myLog(TAG,"??????????????????" + "index:" + Utils.byte2Int(values[0]));
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                    }



                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x34 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-34-00
                        //????????????????????????
                        Logger.myLog(TAG,"????????????????????????");
                    }



                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x31 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-31-00
                        //????????????????????????
                        Logger.myLog("????????????????????????");
                    }



                    else if (Utils.byte2Int(values[0]) == 0x06 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x08) {
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                        Logger.myLog(TAG,"???????????????????????????????????? == ??????" + Utils.byte2Int(values[3]) + "????????????hour:" + Utils.byte2Int(values[4]) + "????????????min:" + Utils.byte2Int(values[5]) + "????????????hour:" + Utils.byte2Int(values[6]) + "????????????min:" + Utils.byte2Int(values[7]));
                        byte[] tempByte = ParseData.getTempByte(values);
                        ParseData.saveW526RaiseHand(tempByte, mBaseDevice);
                        //??????????????????????????????
                    }


                    else if (Utils.byte2Int(values[0]) == 0x03 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x0b) {
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                        //??????????????????
                        Logger.myLog(TAG,"???????????????????????? == " + Utils.byte2Int(values[3]) + "??????????????????----" + Utils.byte2Int(values[4]) + "???????????????");
                        byte[] tempByte = ParseData.getTempByte(values);
                        ParseData.saveBackLight(tempByte, mBaseDevice);
                    }

                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x1A) {
                        Logger.myLog("???????????????????????? == ");
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x0a) {
                        //??????????????????
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                            bluetoothListener.onSuccessWatchFace(values[3]);
                        }
                        Logger.myLog("???????????????????????? == " + Utils.byte2Int(values[3]) + "??????");
                        byte[] tempByte = ParseData.getTempByte(values);
                        ParseData.saveWatchFace(tempByte, mBaseDevice);
                    }


                    else if (Utils.byte2Int(values[0]) == 0x06 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x03) {//06-FF-03-C8
                        //??????????????????
                        if (bluetoothListener != null) {
                            bluetoothListener.onGetSettingSuccess();
                        }
                        Logger.myLog("???????????????????????? == " + Utils.byte2Int(values[3]) + "??????");
                        byte[] tempByte = ParseData.getTempByte(values);
                        ParseData.saveSedentaryTime(tempByte, mBaseDevice);
                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x0f) {//02-FF-0f
                        Logger.myLog("??????????????????");

                        if (bluetoothListener != null) {
                            bluetoothListener.onSuccessTempSub(values[3]);
                        }

                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x0E) {

                        if (settingSuccessListener != null) {
                            settingSuccessListener.successTempSub(values[3]);
                        }
                    }
                    //??????????????????????????????
                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x50) {
                        if (settingSuccessListener != null) {
                            settingSuccessListener.sendOnceHrSuccess(values[3]);
                        }
                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x40 && Utils.byte2Int
                            (values[3]) == 0x04) {//02-FF-40-04
                        //??????24????????????
                        Logger.myLog("??????24????????????");
                        //??????????????????????????????
                        if (bluetoothListener != null) {
                            bluetoothListener.onSyncSuccess();
                        }
                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x40 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-40-00
                        //??????24????????????
                        isPractisData = false;
                        isSyncData = true;
                        Logger.myLog("???24????????????,???????????????");

                        // bluetoothListener.onSyncStart();

                    } else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x41 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-41-00
                        //????????????24????????????
                        Logger.myLog("????????????24??????????????????");
                    }


                    //?????????????????????
                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x24) {
                        if (Utils.byte2Int
                                (values[3]) == 0x00) {
                            if (m24HDATA != null) {
                                m24HDATA.clear();
                            }
                            isPractisData = false;
                            syncRopeStep = 1;
                        } else if (Utils.byte2Int
                                (values[3]) == 0x04) {
                            //???????????????????????????
                            bluetoothListener.clearSyncCmd();
                        }
                    }

                    //?????????????????????
                    else if (Utils.byte2Int(values[0]) == 0x04 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x23) {

                        if (Utils.byte2Int(values[5]) == 0) {
                            //?????????
                            RopeNoDataObservable.getInstance().noDataUpdate();
                        } else {
                            bluetoothListener.onSyncRopeData();
                        }
                    }

                    //??????????????????
                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x25
                    ) {
                        if (Utils.byte2Int
                                (values[3]) == 0x00) {
                            if (m24HDATA != null) {
                                m24HDATA.clear();
                            }
                            isPractisData = false;
                            syncRopeStep = 2;
                        } else if (Utils.byte2Int
                                (values[3]) == 0x04) {
                            bluetoothListener.clearSyncCmd();
                            //???????????????????????????
                        }
                    }


                    //?????????????????????&??????
                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x26) {

                        if (Utils.byte2Int
                                (values[3]) == 0x00) {

                            if (m24HDATA != null) {
                                m24HDATA.clear();
                            }
                            isPractisData = false;
                            syncRopeStep = 3;
                        } else if (Utils.byte2Int
                                (values[3]) == 0x04) {
                            bluetoothListener.clearSyncCmd();
                            //???????????????????????????
                        }

                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x42 && Utils.byte2Int
                            (values[3]) == 0x00) {//????????? 02-FF-42-00???
                        //???????????????

                        Logger.myLog("???????????????,???????????????");
                        isPractisData = true;
                        isSyncData = true;
                        if (mPractiseHDATA == null) {
                            mPractiseHDATA = Collections.synchronizedList(new ArrayList<byte[]>());
                        }
                        mPractiseHDATA.clear();

                    }


                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x42 && Utils.byte2Int
                            (values[3]) == 0x04) {//????????? 02-FF-42-04
                        //???????????????
                        Logger.myLog("???????????????");
                        //????????????????????????????????????????????????
                        //-1??????????????????
                        if (bluetoothListener != null) {
                            bluetoothListener.onSyncSuccessPractiseData(-1);
                        }
                    }


                    else if (Utils.byte2Int(values[0]) == 0x03 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int(values[2]) == 0x12) {
                        if (bluetoothListener != null) {
                            Logger.myLog("?????????????????? ");
                            bluetoothListener.onGetSettingSuccess();
                        }
                    }


                    else if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x17 && Utils.byte2Int
                            (values[2]) == 01) {
                        Logger.myLog(TAG,"onStartSyncPractiseData");
                        if (bluetoothListener != null) {
                            bluetoothListener.onStartSyncPractiseData(0);
                        }
                    }

                    //??????????????????
                    else if (Utils.byte2Int(values[0]) == 0x03 && Utils.byte2Int(values[1]) == 0x3A && Utils.byte2Int
                            (values[2]) == 01) {
                        if (bluetoothListener != null) {
                            int temp = (Utils.byte2Int(values[4]) << 8) + Utils.byte2Int(values[3]);
                            Logger.myLog("??????????????????" + temp);
                            bluetoothListener.onTempData(temp);
                        }
                    }
                    //????????????
                    else if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x13) {
                        //HandlerCommand.handleMusicController(mContext, values);
                    }

                    //????????????????????????
                    else if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x51) {
                        if (bluetoothListener != null) {
                            Logger.myLog("??????????????????" + Utils.byte2Int(values[2]));
                            bluetoothListener.onSuccessOneHrData(Utils.byte2Int(values[2]));
                        }
                    }


                    //??????????????????
                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0x3c) {
                        if (bluetoothListener != null) {
                            Logger.myLog("??????????????????" + Utils.byte2Int(values[2]) + "---" + Utils.byte2Int(values[3]));
                            bluetoothListener.onBloodData(Utils.byte2Int(values[2]), Utils.byte2Int(values[3]));
                        }
                    }


                    //??????????????????
                    else if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x3e) {
                        if (bluetoothListener != null) {
                            int temp = Utils.byte2Int(values[2]);
                            Logger.myLog("??????????????????" + temp);
                            bluetoothListener.onOxyData(temp);
                        }
                    }


                    //01 0X18 01   ???????????????????????????
                    else if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x18 && Utils.byte2Int
                            (values[2]) == 01) {
                        if (bluetoothListener != null) {
                            bluetoothListener.onStartSyncWheatherData();
                        }
                    }
                    //?????????????????????
                    /**
                     * 05 20 type min sec c1 c2
                     *     05 ????????????????????????????????????5bytes
                     *     20 ????????????
                     *     type 00 01 02 ????????????????????? ????????? ???????????????
                     *     min sec ?????????????????? ??? ???
                     *     c1 c2 ???????????? ???8??? ???8???
                     */

                    else if (Utils.byte2Int(values[0]) == 0x05 && Utils.byte2Int(values[1]) == 0x20) {
                        RopeStartOrEndSuccessObservable.getInstance().successStartOrEnd(true);
                    }

                    /**
                     * 	CMD_GET_SKIP_STATE              = 0X23,         // HOST     ??????????????????&??????
                     * 	0   4
                     *     1   0xFF
                     *     2   CMD_GET_SKIP_STATE
                     * 	3   1 ????????? 2 ???????????????
                     * 	4   ????????????
                     * 	5   0 ???????????????1 ????????????(???????????????????????? 24 25 26 ??????)
                     * 	    ????????????????????????????????????
                     */
                    /*else if (Utils.byte2Int(values[0]) == 0x04 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int(values[1]) == 0x23) {
                        if (values[5] == 1) {
                            bluetoothListener.onSyncRopeData();
                        }
                        //   RopeStartOrEndSuccessObservable.getInstance().successStartOrEnd(true);
                    }*/

                    /**
                     *   ??????????????????  0X28
                     * 	0   6
                     *  1   0xFF
                     *  2   CMD_SEL_SKIP_TYPE
                     * 	3   ????????????
                     * 	4   ????????????
                     * 	5   ????????????
                     * 	6   ????????????8???
                     * 	7   ????????????8???
                     */
                    else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int(values[1]) == 0x28) {
                        RopeStartOrEndSuccessObservable.getInstance().getRopeType(Utils.byte2Int(values[3]), Utils.byte2Int(values[4]), Utils.byte2Int(values[5]), Utils.byte2Int(values[7]) << 8 + Utils.byte2Int(values[6]));
                        // bluetoothListener.onGetSettingSuccess();
                    }
                    //??????????????????
                    else if (Utils.byte2Int(values[0]) == 0x03 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int(values[2]) == 0x22) {
                        if (Utils.byte2Int(values[4]) == 2) {
                            RopeStartOrEndSuccessObservable.getInstance().successStartOrEnd(true);
                        } else {
                            RopeStartOrEndSuccessObservable.getInstance().successStartOrEnd(false);
                        }
                        // bluetoothListener.onGetSettingSuccess();

                    }

                    //????????????????????????
                    if (Utils.byte2Int(values[0]) == 0x7 && Utils.byte2Int(values[1]) == 0x27) {
                        if (bluetoothListener != null) {
                            //1 ????????? 2 ???????????????
                            RopeRealDataObservable.getInstance().successRopeEnd(values[2] == 1 ? true : false);
                            if (Utils.byte2Int(values[8]) == 0x01) {
                                bluetoothListener.onSyncRopeData();
                            }
                        }
                        return;
                    }
                    //??????????????????
                    else if (Utils.byte2Int(values[0]) == 0x06 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int(values[2]) == 0x28) {
                        GetRopeTargDataObservable.getInstance().successTargetData(values);
                        //  bluetoothListener.onGetSettingSuccess();

                    }
                    //????????????01 1c 50
                    else if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x1c) {

                        BatteryChangeObservable.getInstance().getBattery(values[2], mBaseDevice.deviceType, mBaseDevice.getDeviceName(), true);

                        /*if (bluetoothListener != null) {
                            bluetoothListener.onGetBattery(Utils.byte2Int(values[2]));
                        }*/
                    } else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x43 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-43-00
                        //??????????????????????????????
                        Logger.myLog("??????????????????????????????");
                    } else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x80 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-80-00
                        //???????????????????????????
                        Logger.myLog("?????????????????????????????????");
                    } else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x81 && Utils.byte2Int
                            (values[3]) == 0x00) {//02-FF-81-00
                        //???????????????????????????
                        Logger.myLog("?????????????????????????????????????????????");
                    } else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0xE1 && Utils.byte2Int
                            (values[3]) == 0x00) {//02 FF E1 00
                        //?????????????????????LED??????
                        Logger.myLog("??????????????????????????????LED??????");
                    } else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0xE0 && Utils.byte2Int
                            (values[3]) == 0x00) {//02 FF E0 00
                        //????????????
                        Logger.myLog("????????????");
                    } else if (Utils.byte2Int(values[0]) == 0x02 && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0xE4 && Utils.byte2Int
                            (values[3]) == 0x00) {//02 FF E4 00
                        //?????? OHR
                        Logger.myLog("?????? OHR");
                    } else if (Utils.byte2Int(values[0]) == 0x0C && Utils.byte2Int(values[1]) == 0xFF && Utils.byte2Int
                            (values[2]) == 0x82) {//0C-FF-82
                        //??????????????????????????????
//                        ????????? 00-82
//                        ???????????????????????? 10000?????????????????? 2018 ??? 12 ??? 13 ??? 14 ??? 15 ??? 16 ???
//                        0C-FF-82-10-27-00-00-E2-07-0C-0D-0E-0F-10
                        long SN = (Utils.byte2Int(values[6]) << 32) + (Utils.byte2Int(values[5]) << 16) + (Utils
                                .byte2Int(values[4]) << 8) + Utils
                                .byte2Int
                                        (values[3]);
                        int year = (Utils.byte2Int(values[8]) << 8) + Utils.byte2Int(values[7]);
                        int non = Utils.byte2Int(values[9]);
                        int day = Utils.byte2Int(values[10]);
                        int hour = Utils.byte2Int(values[11]);
                        int min = Utils.byte2Int(values[12]);
                        int sec = Utils.byte2Int(values[13]);
                        Logger.myLog("?????????????????????????????? " + "SN == " + SN + " Time == " + year + "???" + non + "???" + day + "???"
                                + hour + "???" + min + "???" + sec + "???");
                    }
                }
            } else if (characteristic.getUuid().equals(Constants.realTimeDataW516UUID)) {
                //?????????????????? ????????????
//                02 5C 00 00 00 00 00 00

                final StringBuilder stringBuilder = new StringBuilder(values.length);
                for (byte byteChar : values) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
                /*Logger.myLog(" ?????????????????? ReceiverCmd " + mBaseDevice + stringBuilder.toString() + " UUID " + characteristic.getUuid()
                        .toString());*/

                //01 0x17 01  ????????????????????????

                if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x17 && Utils.byte2Int
                        (values[2]) == 01) {
                    if (bluetoothListener != null) {
                        if (!isSyncData)
                            bluetoothListener.onStartSyncPractiseData(0);
                    }
                    return;
                }
                // DEVICE   ??????????????????(??????????????????app)
                if (Utils.byte2Int(values[0]) == 0x7 && Utils.byte2Int(values[1]) == 0x27) {
                    if (bluetoothListener != null) {
                        if (Utils.byte2Int(values[8]) == 0x01) {
                            bluetoothListener.onSyncRopeData();
                        }
                    }
                    return;

                }
                //01 0X18 01   ???????????????????????????
                if (Utils.byte2Int(values[0]) == 0x01 && Utils.byte2Int(values[1]) == 0x18 && Utils.byte2Int
                        (values[2]) == 01) {
                    if (bluetoothListener != null) {
                        bluetoothListener.onStartSyncWheatherData();
                    }
                    return;
                }


                try {
                    if (bluetoothListener != null && mBaseDevice != null) {
                        if (mBaseDevice.deviceType == IDeviceType.TYPE_ROPE_S002) {
                            //FD04 ????????????(???????????? 1???/???)
                            //0  ????????????1byts+
                            //1  ???????????????3bytes+
                            //4  ??????1bytes+
                            //5  ?????????3bytes+
                            //8  ?????????4bytes
                            //12 ?????????????????? 2bytes(???????????????)
                            //12 ?????????       2bytes(???????????????)
                            byte[] tempBtype = new byte[values.length];
                            for (int i = 0; i < values.length; i++) {
                                tempBtype[i] = values[i];
                            }
                            ThreadPoolUtils.getInstance().addTask(new Runnable() {
                                @Override
                                public void run() {
                                    RopeRealDataObservable.getInstance().successRealData(tempBtype);
                                }
                            });


                        } else {

                            // Log.e("")

                            bluetoothListener.onRealtimeStepData(Utils.byte2Int(values[1]), Utils.byteArrayToInt(new byte[]{
                                    values[6], values[5], values[4]}), Utils.byteArrayToInt(new byte[]{
                                    values[9], values[8], values[7]}), Utils.byteArrayToInt(new byte[]{
                                    values[12], values[11], values[10]}));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //  Logger.myLog(e.toString());
                }

            } else if (characteristic.getUuid().equals(Constants.receiveDataW516UUID)) {    //??????????????????
                final StringBuilder stringBuilder = new StringBuilder(values.length);
                for (byte byteChar : values) {
                    stringBuilder.append(String.format("%02X ", byteChar));
                }
               /* saveLog(new StringBuilder(DateUtil.dataToString(new Date(), "HH:mm:ss")).append(" ReceiverCmd ")
                        .append
                                (stringBuilder.toString()).append(" UUID ").append(characteristic.getUuid().toString()));*/
                saveLog(new StringBuilder(DateUtil.dataToString(new Date(), "yyyy/MM/dd HH:mm:ss.SSS")).append(" ReceiverCmd ").append(stringBuilder.toString()));

                Logger.myLog(" ReceiverCmd " + stringBuilder.toString() + " UUID " + characteristic.getUuid()
                        .toString());
                if (isPractisData) {
                    if (mPractiseHDATA == null) {
                        mPractiseHDATA = Collections.synchronizedList(new ArrayList<byte[]>());
                    }
                    addPractiseData(values);
                } else {
                    if (m24HDATA == null) {
                        m24HDATA = Collections.synchronizedList(new ArrayList<byte[]>());
                    }

                    add24HDATA(values);
                }
            }
        } else if (characteristic.getService().getUuid().equals(Constants.HEART_RATE_SERVICE)) {
            if (characteristic.getUuid().equals(Constants.HEART_RATE_CHARACTERISTIC)) {
                //Logger.myLog("realvalue" + Utils.byte2Int(values[1]));
                RopeRealDataObservable.getInstance().realHrValue(Utils.byte2Int(values[1]));
                //??????????????????
                if (bluetoothListener != null) {
                    bluetoothListener.onRealtimeHeartRate(Utils.byte2Int(values[1]));
                }
            }
        }
    }

    private void addPractiseData(byte[] values) {
        byte[] valuesResult = new byte[20];
        if (mPractiseHDATA.size() >= 2) {
            System.arraycopy(values, 1, valuesResult, 0, 19);
            // ParseData.parsW526PractiseData(mPractiseHDATA, bluetoothListener, mBaseDevice, mContext);
        } else {
            System.arraycopy(values, 0, valuesResult, 0, 20);
        }

// -----xuqian-----
        mPractiseHDATA.add(valuesResult);
        Logger.myLog("addPractiseData:" + mPractiseHDATA.size());
        int end = Utils.byte2Int(values[0]);
        if (end > 127) {
            isSyncData = false;
            ParseData.parsW526PractiseData(mPractiseHDATA, bluetoothListener, mBaseDevice, mContext);
        }

    }

    private void add24HDATA(byte[] values) {
        byte[] valuesResult = new byte[19];
        System.arraycopy(values, 1, valuesResult, 0, 19);
//        Logger.myLog("Utils.byte2Int(values[0]) = " + Utils.byte2Int(values[0]));
        if (Utils.byte2Int(values[0]) == 0) {
            m24HDATA.clear();
        }
        m24HDATA.add(valuesResult);
        int end = Utils.byte2Int(values[0]);
        Logger.myLog("end " + end + "mBaseDevice");
        //????????????????????????????????????127???????????????
        if (end > 127) {
            //?????????????????????????????????
//            Logger.myLog("end ?????????>127");
            if (mBaseDevice != null) {
                if (mBaseDevice.deviceType == IDeviceType.TYPE_ROPE_S002) {
                    byte[] ropesbyte = new byte[m24HDATA.size() * 19];
                    for (int i = 0; i < m24HDATA.size(); i++) {
                        System.arraycopy(m24HDATA.get(i), 0, ropesbyte, i * 19, 19);
                    }

                    switch (syncRopeStep) {
                        case 1:
                            ThreadPoolUtils.getInstance().addTask(new Runnable() {
                                @Override
                                public void run() {
                                    if (mBaseDevice != null) {
                                        ParseS002Data.parsumdata(ropesbyte, mBaseDevice.deviceName, BaseManager.mUserId);
                                    }
                                }
                            });

                            break;
                        case 2:
                            ThreadPoolUtils.getInstance().addTask(new Runnable() {
                                @Override
                                public void run() {
                                    ParseS002Data.parseStumbleNumData(ropesbyte);
                                }
                            });

                            break;
                        case 3:
                            ThreadPoolUtils.getInstance().addTask(new Runnable() {
                                @Override
                                public void run() {
                                    ParseS002Data.parseHrDetaiData(ropesbyte);
                                }
                            });
                            break;
                    }

                    if (bluetoothListener != null) {
                        bluetoothListener.onGetSettingSuccess();
                    }
                } else {
                    isSyncData = false;
                    ParseData.parseW52624HData(m24HDATA, bluetoothListener, mBaseDevice, mContext);
                }
            } else {
//            Logger.myLog("end ????????????<127");
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Logger.myLog("GATT_WRITE_SUCCESS!");
            if (characteristic != null) {
                byte[] value = characteristic.getValue();
                if (value != null && value.length > 0) {
                    if (Utils.byte2Int(value[0]) == 0x0A && Utils.byte2Int(value[1]) == 0x32) {
//                        {0x09, 0x32, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};


                        byte[] booleanArrayG = Utils.getBooleanArray(value[3]);
                        Logger.myLog("???????????????bit??? == " + " ????????? == " + (Utils.byte2Int(booleanArrayG[7]) == 0 ? "??????" :
                                "??????")
                                + " ????????? == " + (Utils.byte2Int(booleanArrayG[6]) == 0 ? "??????" : "??????")
                                + " ????????? == " + (Utils.byte2Int(booleanArrayG[5]) == 0 ? "12??????" : "24??????")
                                + " ???????????? == " + (Utils.byte2Int(booleanArrayG[4]) == 0 ? "??????" : "??????")
                                + " 24???????????? == " + (Utils.byte2Int(booleanArrayG[3]) == 0 ? "??????" : "??????"));
                        if (bluetoothListener != null) {
                            bluetoothListener.onSetGeneral(Utils.byte2Int(booleanArrayG[7]) == 0, Utils.byte2Int
                                    (booleanArrayG[6]) == 0, Utils.byte2Int(booleanArrayG[5]) == 0, Utils.byte2Int
                                    (booleanArrayG[4]) == 0, Utils.byte2Int(booleanArrayG[3]) == 0);
                        }
                    } else if (Utils.byte2Int(value[0]) == 0x12 && Utils.byte2Int(value[1]) == 0x35) {
                        if (bluetoothListener != null) {
                            Logger.myLog("repeat =111= " + Utils.byte2Int(value[3]));
                            bluetoothListener.onGetSettingSuccess();
                            bluetoothListener.onSetAlarm(Utils.byte2Int(value[3]), Utils.byte2Int(value[4]) + ":" + (Utils.byte2Int(value[5]) < 10 ? ("0" + Utils.byte2Int(value[5])) : Utils.byte2Int(value[5])), "");
                        }
                    } else if (Utils.byte2Int(value[0]) == 0x09 && Utils.byte2Int(value[1]) == 0x34) {
                        byte[] booleanArrayG = Utils.getBooleanArray(value[2]);
                        Logger.myLog("???????????????bit??? == " + " ?????? == " + (Utils.byte2Int(booleanArrayG[7]) == 0 ? "????????????" :
                                "????????????")
                                + " ?????????????????? == " + (Utils.byte2Int(booleanArrayG[6]) == 0 ? "???" : "???")
                                + " ?????????????????? == " + (Utils.byte2Int(booleanArrayG[5]) == 0 ? "???" : "???"));
                        if (bluetoothListener != null) {
                            bluetoothListener.onSetSleepAndNoDisturb(Utils.byte2Int(booleanArrayG[7]) == 0, Utils.byte2Int(booleanArrayG[6]) != 0, Utils.byte2Int(booleanArrayG[5]) != 0,
                                    Utils.byte2Int(value[3]) + ":" + (Utils.byte2Int(value[4]) < 10 ? ("0" + Utils.byte2Int(value[4])) : Utils.byte2Int(value[4])),
                                    Utils.byte2Int(value[5]) + ":" + (Utils.byte2Int(value[6]) < 10 ? ("0" + Utils.byte2Int(value[6])) : Utils.byte2Int(value[6])),
                                    Utils.byte2Int(value[7]) + ":" + (Utils.byte2Int(value[8]) < 10 ? ("0" + Utils.byte2Int(value[8])) : Utils.byte2Int(value[8])),
                                    Utils.byte2Int(value[9]) + ":" + (Utils.byte2Int(value[10]) < 10 ? ("0" + Utils.byte2Int(value[10])) : Utils.byte2Int(value[10])));
                        }
                    } else if (Utils.byte2Int(value[0]) == 0x05 && Utils.byte2Int(value[1]) == 0x31) {
                        if (bluetoothListener != null) {
                            bluetoothListener.onSetSedentary(Utils.byte2Int(value[2]), (Utils.byte2Int(value[3]) < 10 ? ("0" + Utils.byte2Int(value[3])) : Utils.byte2Int(value[3])) + ":" + (Utils.byte2Int(value[4]) < 10 ? ("0" + Utils.byte2Int(value[4])) : Utils.byte2Int(value[4])), (Utils.byte2Int(value[5]) < 10 ? ("0" + Utils.byte2Int(value[5])) : Utils.byte2Int(value[5])) + ":" + (Utils.byte2Int(value[6]) < 10 ? ("0" + Utils.byte2Int(value[6])) : Utils.byte2Int(value[6])));
                        }
                    }
                }
            }
        } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
            Logger.myLog("GATT_WRITE_NOT_PERMITTED");
        } else {
            Logger.myLog("Write failed , Status is " + status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        handleReceive(gatt, characteristic);
    }


    //***************************************************????????????***********************************************//

    /**
     * 2 ???????????????
     * ??????:
     * ???????????????????????????: 2018 ??? 12 ??? 13 ??? 14 ??? 15 ??? 16 ???
     * 07-30-E2-07-0C-0D-0E-0F-10
     * ?????????02-FF-30-00
     */
//    public void syncTime() {
//        if (isConnected) {
//            Calendar instance = Calendar.getInstance();
//            char[] year = String.format("%04x", instance.get(Calendar.YEAR)).toCharArray();
//            int month = instance.get(Calendar.MONTH);
//            int day = instance.get(Calendar.DAY_OF_MONTH);
//            int hour = instance.get(Calendar.HOUR_OF_DAY);
//            int minute = instance.get(Calendar.MINUTE);
//            int second = instance.get(Calendar.SECOND);
//            Logger.myLog(TAG,"--------????????????==="+String.format("%02X ", Utils.uniteBytes(year[2], year[3])) + "***" + String.format("%02X ",
//                    Utils.uniteBytes(year[0], year[1])));
//            byte[] cmds = new byte[]{0x07, 0x30, Utils.uniteBytes(year[2], year[3]), Utils.uniteBytes(year[0],
//                    year[1]),
//                    (byte) (month + 1), (byte) day, (byte) hour, (byte) minute, (byte) second};
//            writeTXCharacteristicItem(cmds);
//        }
//    }

    /**
     * ??????????????????
     * ?????????00-05
     * ?????????
     * ??????????????????: brith day: 1981 ??? 12 ??? 12 ??????female????????? 100kg????????? 170??? ???????????? 190??? ???????????? 60
     * 0B-FF-05-BD-07-0C-0C-00-E8-03-AA-BE-3C
     */
    private void queryUserInfo() {
        if (isConnected) {
            byte[] data = new byte[]{0x00, 0x05};
            writeTXCharacteristicItem(data);
        }
    }

    /**
     * ??????????????????
     * ???????????????????????????: brith day: 1981 ??? 12 ??? 12 ??????female????????? 100kg????????? 170??? ???????????? 190??? ??????
     * ?????? 60
     * 0A-33-BD-07-0C-0C-00-E8-03-AA-BE-3C
     * 0A 33 C7 07 01 01 01 4C 1D AF 00 00
     * ?????????02-FF-33-00
     */
    public void setUserInfo(int year, int month, int day, int sex, int weight, int height, int maxHeartRate, int
            minHeartRate) {
        if (isConnected) {
            char[] yearChar = String.format("%04x", year).toCharArray();
            char[] weightChar = String.format("%04x", weight * 10).toCharArray();

            byte[] data = new byte[]{0x0A, 0x33, Utils.uniteBytes(yearChar[2], yearChar[3]), Utils.uniteBytes
                    (yearChar[0],
                            yearChar[1]), (byte) month, (byte) day, (byte) sex, Utils.uniteBytes(weightChar[2],
                    weightChar[3]), Utils
                    .uniteBytes(weightChar[0],
                    weightChar[1]), (byte) height, (byte) maxHeartRate,
                    (byte) minHeartRate};
            writeTXCharacteristicItem(data);
        }
    }

    /**
     * ????????????
     * ????????????????????????????????????????????????????????????24 ?????????????????????????????? 24 ??????????????????????????????
     * 09-32-00-14-00-00-00-00-00-00-00
     * ?????????02-FF-32-00
     */
    public void setGeneral() {
        if (isConnected) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("0000");
//            if (open24HeartRate){
//                stringBuilder.append("0");
//            }else{
            //????????????24????????????
            stringBuilder.append("1");
//            }
            stringBuilder.append("0");
            stringBuilder.append("1");
            stringBuilder.append("0");
            stringBuilder.append("0");
            int value = Integer.valueOf(stringBuilder.toString(), 2);
            byte[] data = new byte[]{0x09, 0x32, 0x00, (byte) value, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            writeTXCharacteristicItem(data);
        }
    }

    /**
     * reset ????????????
     * ?????? 04-50-A1-FE-74-69
     * ?????????????????????????????? 02-FF-50-01
     * ????????????????????????02-FF-50-00
     */
    public void reset() {
        if (isConnected) {
            byte[] data = new byte[]{0x04, 0x50, (byte) 0xA1, (byte) 0xFE, 0x74, 0x69};
            writeTXCharacteristicItem(data);
        }
    }

    /**
     * ??????
     */
    public void handleCall() {
        if (isConnected) {
            Log.e("handleD", "handleCall");
            byte[] data = new byte[]{(byte) 0x02, 0x10, (byte) 0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            writeTXCharacteristicItem(data);
        }
    }

    /**
     * ??????
     */
    public void handleSms() {
        if (isConnected) {
            if ((System.currentTimeMillis() - mExitTime) > 1000) {
                mExitTime = System.currentTimeMillis();
                Log.e("handleD", "handleSms");
                byte[] data = new byte[]{(byte) 0x02, 0x10, (byte) 0x80, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                writeTXCharacteristicItem(data);
            }
        }
    }

    /**
     * ????????????  ????????????????????????
     *
     * @param ctx
     * @param packagename  whose notifications you want to push to ble device
     * @param notification the notification will be pushed to ble device
     *                     ???????????????????????????????????? 1234567890???12-10-80-00-31-32-33-34-35-36-37-38-39-30-00-00-00-00-00-00
     *                     ?????????02-FF-60-00
     */
    public void handleNotification(Context ctx, String packagename, Notification notification) {
        if (isConnected && Constants.strPkNames.contains(packagename)) {
            Log.e("handleD", "handleNotification");
            byte[] data = new byte[]{(byte) 0x02, 0x10, (byte) 0x80, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            writeTXCharacteristicItem(data);
        }
    }

    /**
     * 0x00 ??????????????????
     * 0x01 ?????????????????? 0 ?????????
     * 0x02 DEMO mode
     * ????????????
     * FF??????demo??????
     *
     * @param mode
     */
    public void switchMode(int mode) {
        if (isConnected) {
            byte[] cmds = new byte[]{0x01, (byte) 0xE2, (byte) mode};
            writeTXCharacteristicItem(cmds);
        }
    }

    public void switchAdjust(boolean enable) {
        if (isConnected) {
            byte[] cmds;
            if (enable) {
                cmds = new byte[]{0x01, 0x11, 0x01};
            } else {
                cmds = new byte[]{0x01, 0x11, (byte) 0xFF};
            }
            if (enable) {
                sendWhat = Order_send_adjust_mode;
            } else {
                sendWhat = Order_send_exit_adjust_mode;
            }
            writeTXCharacteristicItem(cmds);
        }
    }


    /**
     * ????????? ??????
     */
    public void adjustHourAndMin(int degreesHour, int degreesMin) {
        if (isConnected) {
            byte[] cmds = new byte[]{0x03, 0x11, 0x00, (byte) degreesHour, (byte) degreesMin};
            sendWhat = Order_send_exit_adjust_mode_adjusting;
            writeTXCharacteristicItem(cmds);
        }
    }

    //***************************************************????????????***********************************************//


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (status == BluetoothGatt.GATT_SUCCESS) {

        } else {
            hasSyncTime = false;
        }
    }

    public boolean enableNotification() {
        if (mResponceChar != null) {
            return internalEnableNotifications(mResponceChar);
        } else {
            return false;
        }
    }

    /**
     * ????????????????????????
     *
     * @param data
     * @return
     */
    public boolean writeTXCharacteristicItem(byte[] data) {
        if (isConnected && data != null) {
            if (mNRFBluetoothGatt == null) {
                Logger.myLog("Gatt is null");
                return false;
            }
            final StringBuilder stringBuilder = new StringBuilder();
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            saveLog(new StringBuilder(DateUtil.dataToString(new Date(), "yyyy/MM/dd HH:mm:ss.SSS") + " sendCommand " + stringBuilder.toString()));
            Logger.myLog(TAG,"writeTXCharacteristicItem " + stringBuilder.toString());
            mSendChar.setValue(data);
            // boolean status = mNRFBluetoothGatt.writeCharacteristic(mSendChar);

            boolean status = false;
            int mRetry = 0;

            while (!status && isConnected && mNRFBluetoothGatt != null && mRetry < 3) {
                status = mNRFBluetoothGatt.writeCharacteristic(mSendChar);
                mRetry++;
                saveLog(new StringBuilder(DateUtil.dataToString(new Date(), "MM/dd HH:mm:ss") + "Write" + status + ",mRetry:" + mRetry + ",isConnected:" + isConnected));
                if (status) {
                    mSendOk = true;
                }
                if (!mSendOk) {
                    SystemClock.sleep(20);
                }
            }

          /*  int mRetry = 0;
            while (mRetry < 5 && isConnected && mNRFBluetoothGatt != null && !mSendOk) {
                boolean status = mNRFBluetoothGatt.writeCharacteristic(mSendChar);
                mRetry++;
                Logger.myLog("Write data status:" + status + ",mRetry:" + mRetry + ",isConnected:" + isConnected + ",mSendOk:" + mSendOk + ",mNRFBluetoothGatt:" + mNRFBluetoothGatt);
                if (status) {
                    //   mSendOk = true;
                }
                if (!mSendOk) {
                    SystemClock.sleep(200);
                }
            }*/
            return status;
           /* boolean status = mNRFBluetoothGatt.writeCharacteristic(mSendChar);
            if (status == false) {
                Logger.myLog("Write data failed!");
                return status;
            }
            return true;*/
        } else {
            return false;
        }
    }


    public int getQueuryLenth() {
        return cmds.size();
    }

    public void clearQueuryData() {
        cmds.clear();
    }

    public void addQueuryData(DataBean data) {
        cmds.offer(data);
    }

    public DataBean pollQueuryData() {

        return cmds.poll();
    }


    public static String hexStr2Str(String hexStr) {
        try {
            String str = "0123456789ABCDEF";
            char[] hexs = hexStr.toCharArray();
            byte[] bytes = new byte[hexStr.length() / 2];
            int n;
            for (int i = 0; i < bytes.length; i++) {
                n = str.indexOf(hexs[2 * i]) * 16;
                n += str.indexOf(hexs[2 * i + 1]);
                bytes[i] = (byte) (n & 0xff);
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

}
