package com.isport.brandapp.home.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.home.fragment.BarChartView;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.holder.CustomHolder;
import com.isport.brandapp.util.AppSP;
import com.isport.brandapp.util.DeviceTypeUtil;
import com.isport.brandapp.wu.util.HeartRateConvertUtils;

import java.util.List;

import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.LoadImageUtil;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @创建者 bear
 * @创建时间 2019/3/28 19:42
 * @描述
 */
public class DataRealHeartRateHolder extends CustomHolder<String> {
    BarChartView bar_hr_view;
    TextView tv_hr_value;
    TextView tv_hr_intensity;
    ImageView checkbox_hr_state, iv_no_hr_data, iv_mengban;
    RelativeLayout layout_hr_no_con;
    View layout_hr_strong, layout_data_tope;


    public DataRealHeartRateHolder(View itemView) {
        super(itemView);
    }

    public DataRealHeartRateHolder(List<String> datas, View itemView) {
        super(datas, itemView);
    }

    public DataRealHeartRateHolder(Context context, final List<String> lists, int itemID) {


        super(context, lists, itemID);

        Log.e("DataRealHeartRateHolder", "DataRealHeartRateHolder");


        // layout_hr_strong = itemView.findViewById(R.id.layout_hr_strong);
        layout_hr_strong = itemView.findViewById(R.id.layout_data);
        bar_hr_view = itemView.findViewById(R.id.bar_hr_view);
        layout_data_tope = itemView.findViewById(R.id.layout_data_tope);
        iv_no_hr_data = itemView.findViewById(R.id.iv_no_hr_data);
        iv_mengban = itemView.findViewById(R.id.iv_mengban);
        tv_hr_value = itemView.findViewById(R.id.tv_hr_value);
        tv_hr_intensity = itemView.findViewById(R.id.tv_hr_intensity);
        layout_hr_no_con = itemView.findViewById(R.id.layout_hr_no_con);
        checkbox_hr_state = itemView.findViewById(R.id.checkbox_hr_state);

        showcheckbox_hr_state();

        layout_hr_strong.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //给GridView设置Adapter，在adapter的getView中获取GridView的高度，在这个回调之前获取的高度都是0
                //处理完后remove掉，至于为什么，后面有解释
                Log.e("hasData", "addOnGlobalLayoutListener");
                defUpdateUI();
                LoadImageUtil.getInstance().loadGifHr(context, R.drawable.bg_main_hr, iv_no_hr_data);
                layout_hr_strong.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);
            }
        });


        checkbox_hr_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ViewMultiClickUtil.onMultiClick()) {
                    return;
                }

                int currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516);

                currentType = DeviceTypeUtil.getCurrentBindDeviceType();

                if (AppConfiguration.deviceBeanList == null && AppConfiguration.deviceBeanList.size() == 0) {
                    Toast.makeText(BaseApp.getApp(), context.getString(R.string.app_disconnect_device), Toast.LENGTH_LONG).show();
                    return;
                } else if (!AppConfiguration.deviceBeanList.containsKey(currentType)) {
                    Toast.makeText(BaseApp.getApp(), context.getString(R.string.app_disconnect_device), Toast.LENGTH_LONG).show();
                    return;
                } else if (AppConfiguration.deviceBeanList.containsKey(currentType)) {
                    if (!AppConfiguration.isConnected) {
                        Toast.makeText(BaseApp.getApp(), context.getString(R.string.app_disconnect_device), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if (!AppConfiguration.hasSynced) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.sync_data));
                    return;
                }
                count = 0;
                if (checkbox_hr_state.getTag().equals("open")) {
                    if (onHeartRateSwitch != null) {
                        onHeartRateSwitch.onOpen(false);
                    }
                    //ISportAgent.getInstance().requestBle(BleRequest.W556_HR_SWITCH, false);

                    setChexState(false);
                } else {
                    if (onHeartRateSwitch != null) {
                        onHeartRateSwitch.onOpen(true);
                    }
                    /*if (onHeartRateSwitch != null) {
                        onHeartRateSwitch.onOpen(true);
                    }*/
                    // ISportAgent.getInstance().requestBle(BleRequest.W556_HR_SWITCH, true);
                    setChexState(true);
                }

            }
        });


    }

    public void showcheckbox_hr_state() {
        if (DeviceTypeUtil.isShowRealSwitch()) {
            checkbox_hr_state.setVisibility(View.INVISIBLE);
        } else {
            checkbox_hr_state.setVisibility(View.VISIBLE);
        }

    }

    private OnHeartRateSwitch onHeartRateSwitch;

    public void setOnHeartRateSwitch(OnHeartRateSwitch onHeartRateSwitch) {
        this.onHeartRateSwitch = onHeartRateSwitch;
    }

    public interface OnHeartRateSwitch {
        void onOpen(boolean isOpen);
    }


    public void clearData() {
        // bar_hr_view.clearAll();
    }


//连接和未连接的状态不一样

    int count = 0;

    public void setCheckbox_hr_state() {
        count++;
        if (count == 5) {
            checkbox_hr_state.setImageResource(R.drawable.icon_open);
            checkbox_hr_state.setTag("open");
            count = 0;
        }
    }


    public void setChexState(boolean isCheck) {
        count = 0;
        showcheckbox_hr_state();
        if (isCheck) {
            checkbox_hr_state.setImageResource(R.drawable.icon_open);
            checkbox_hr_state.setTag("open");
        } else {
            // close();
            checkbox_hr_state.setImageResource(R.drawable.icon_close);
            checkbox_hr_state.setTag("close");
        }
    }


    public void setLineDataAndShow(int hrValue, int age, String sex, boolean isHide) {


        int pre = (int) HeartRateConvertUtils.hearRate2Percent(hrValue,
                HeartRateConvertUtils.getMaxHeartRate(age, sex));
        int point = HeartRateConvertUtils.hearRate2Point(
                hrValue,
                HeartRateConvertUtils.getMaxHeartRate(age, sex)
        );

        Log.e("setLineDataAndShow", "hrValue=" + hrValue + "pre=" + pre);

        if (!isHide) {
            tv_hr_value.setText(hrValue + "");
            tv_hr_intensity.setText(pre + "");
        }
        //Logger.myLog("hrList.get(i)" + hrValue + "HeartRateConvertUtils.getMaxHeartRate(age):" + HeartRateConvertUtils.getMaxHeartRate(age, sex) + "age:" + age + "point:" + point)
        //Logger.myLog("age=" + age + "hrValue=" + hrValue + "point=" + point + "sex=" + sex)

        int color = context.getResources().getColor(R.color.common_white);
        switch (point) {
            case 0: {
                color = context.getResources().getColor(R.color.color_leisure);
                if (!isHide) {
                    layout_hr_strong.setBackgroundResource(R.drawable.shape_hr_color_1);
                }
                break;
            }
            case 1: {
                color = context.getResources().getColor(R.color.color_warm_up);
                if (!isHide) {
                    layout_hr_strong.setBackgroundResource(R.drawable.shape_hr_color_2);
                }
                break;
            }
            case 2: {
                color = context.getResources().getColor(R.color.color_fat_burning_exercise);
                if (!isHide) {
                    layout_hr_strong.setBackgroundResource(R.drawable.shape_hr_color_3);
                }
                break;
            }
            case 3: {
                color = context.getResources().getColor(R.color.color_aerobic_exercise);
                layout_hr_strong.setBackgroundResource(R.drawable.shape_hr_color_4);
                break;
            }
            case 4: {
                color = context.getResources().getColor(R.color.color_anaerobic_exercise);
                if (!isHide) {
                    layout_hr_strong.setBackgroundResource(R.drawable.shape_hr_color_5);
                }
                break;
            }
            case 5: {
                color = context.getResources().getColor(R.color.color_limit);
                if (!isHide) {
                    layout_hr_strong.setBackgroundResource(R.drawable.shape_hr_color_6);
                }
                break;
            }
        }

        bar_hr_view.addData(hrValue, color, isHide);
    }


    public void bleCloseUpdateUi() {
        hasData(false);
    }

    public void defUpdateUI() {
        setChexState(false);
        hasData(false);
        tv_hr_value.setText("--");
        tv_hr_intensity.setText("--");
        bar_hr_view.clearData();
    }


    //没有数据5个0就显示没有数据


    public void hidelayout_hr() {
        layout_hr_no_con.setVisibility(View.INVISIBLE);
    }


    public void hasData(boolean isConn) {
        layout_hr_no_con.setVisibility(isConn ? View.GONE : View.VISIBLE);
        try {
            if (isConn) {
                if (layout_hr_no_con.getVisibility() == View.GONE) {
                    return;
                }
            } else {

            }

            if (layout_hr_no_con.getVisibility() == View.VISIBLE) {

            } else {

                if (isConn) {
                } else {
                    layout_hr_no_con.setVisibility(View.GONE);
                   /* Bitmap bitmap = getDrawing(layout_data_tope);
                    Log.e("hasData", "hasData" + bitmap);

                    if (bitmap == null) {
                        return;
                    }
                    UtilBitmap.blurImageView(context, iv_mengban, 25f, 0x4d000000, bitmap);*/

                }

            }
            if (!isConn) {
                LoadImageUtil.getInstance().loadGifHr(context, R.drawable.bg_main_hr, iv_no_hr_data);
            }

            //   layout_data.setVisibility(isConn ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        bleCloseUpdateUi();
      /*  try {
            if (layout_hr_no_con.getVisibility() == View.VISIBLE) {
                return;
            }
            Bitmap bitmap = getDrawing(layout_data);
            Log.e("hasData", "hasData" + bitmap);

            if (bitmap == null) {
                return;
            }
            LoadImageUtil.getInstance().loadGif(context, R.drawable.bg_main_hr, iv_no_hr_data);
            UtilBitmap.blurImageView(context, iv_mengban, 25f, 0x4d000000, bitmap);
            layout_hr_no_con.setVisibility(View.VISIBLE);
            // layout_data.setVisibility(View.INVISIBLE);

        } catch (Exception e) {

        }*/

    }


/*
    public static Bitmap loadBitmapFromView(View v, boolean isParemt) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), HDConstantSet.BITMAP_QUALITY);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }*/


}
