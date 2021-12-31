
package com.isport.blelibrary.observe;

import android.util.Log;

import com.isport.blelibrary.entry.RopeRealDataBean;
import com.isport.blelibrary.observe.bean.EndBean;
import com.isport.blelibrary.utils.Utils;

import java.util.Observable;

/**
 * ClassName:NetProgressBar <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Date: 2017年4月19日 上午11:31:46 <br/>
 *
 * @author Administrator
 */
public class RopeRealDataObservable extends Observable {

    private static final String TAG = "RopeRealDataObservable";

    private static RopeRealDataObservable obser;


    @Override
    public synchronized void setChanged() {
        super.setChanged();
    }

    private RopeRealDataObservable() {
        super();
    }

    public static RopeRealDataObservable getInstance() {
        if (obser == null) {
            synchronized (RopeRealDataObservable.class) {
                if (obser == null) {
                    obser = new RopeRealDataObservable();
                }
            }
        }
        return obser;
    }

    public void successEndDaya(byte[] data) {
        /**
         *     CMD_SKIP_UPLOAD					= 0X27, 		// DEVICE   跳绳状态更新(跳绳完成通知app)
         *     0   7
         *     1   CMD_SKIP_UPLOAD
         * 	2   1 为停止 2 为开始跳绳
         * 	3   跳绳类型
         * 	4   倒计时分
         * 	5   倒计时秒
         * 	6   倒计数低8位
         * 	7   倒计数高8位
         * 	8   0 为无数据，1 为有数据(有数据时需要执行 24 25 26 命令)
         */

    }

    public void successRealData(byte[] data) {
        //00 80 01 00 47 84 0E 00 2D 00 00 00
        Log.e(TAG,"--------RopeType="+data[0]);
        try {

            RopeRealDataBean ropeRealDataBean = new RopeRealDataBean();
            ropeRealDataBean.setRopeType(data[0]);
            long sumCount = (Utils.byte2Int(data[3]) << 16) + (Utils.byte2Int(data[2]) << 8) + Utils
                    .byte2Int
                            (data[1]);
            ropeRealDataBean.setRopeSumCount(sumCount);
            ropeRealDataBean.setRealHr(Utils.byte2Int(data[4]));
            long time = (Utils.byte2Int(data[7]) << 16) + (Utils.byte2Int(data[6]) << 8) + Utils
                    .byte2Int
                            (data[5]);
            ropeRealDataBean.setTime(time);
            long cal = (Utils.byte2Int(data[11]) << 24 + Utils.byte2Int(data[10]) << 16) + (Utils.byte2Int(data[9]) << 8) + Utils
                    .byte2Int
                            (data[8]);
            ropeRealDataBean.setCal(cal);

            ropeRealDataBean.setCountdownMin(Utils.byte2Int(data[12]));
            if(data.length<12)
                return;
            ropeRealDataBean.setCountdownSec(Utils.byte2Int(data[13]));
            int countdown = (Utils.byte2Int(data[15]) << 8) + Utils.byte2Int(data[14]);
            ropeRealDataBean.setCountdown(countdown);
            RopeRealDataObservable.getInstance().setChanged();
            RopeRealDataObservable.getInstance().notifyObservers(ropeRealDataBean);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void realHrValue(Integer value) {
        RopeRealDataObservable.getInstance().setChanged();
        RopeRealDataObservable.getInstance().notifyObservers(value);
    }

    public void successRopeEnd(Boolean isEnd) {
        RopeRealDataObservable.getInstance().setChanged();
        RopeRealDataObservable.getInstance().notifyObservers(isEnd);
    }

    public void successRopeEnd(boolean isEnd, boolean isSendDevice) {
        EndBean endBean = new EndBean();
        endBean.setStart(isEnd);
        endBean.setEndDevice(isSendDevice);
        RopeRealDataObservable.getInstance().setChanged();
        RopeRealDataObservable.getInstance().notifyObservers(endBean);
    }


}