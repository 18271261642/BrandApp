package com.isport.brandapp.home.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.isport.blelibrary.ISportAgent;
import com.isport.blelibrary.deviceEntry.impl.BaseDevice;
import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.App;
import com.isport.brandapp.AppConfiguration;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.holder.CustomHolder;
import com.isport.brandapp.util.DeviceTypeUtil;

import java.util.List;

import brandapp.isport.com.basicres.commonutil.UIUtils;
import brandapp.isport.com.basicres.commonutil.ViewMultiClickUtil;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * @创建者 bear
 * @创建时间 2019/3/28 19:42
 * @描述
 */
public class DataHeartRateHolder extends CustomHolder<String> {
    TextView tvUpdateTime;
    ImageView iv_current_type;
    TextView tvOption, tv_current_type_name;
    TextView tvValueH, tvValueM;
    TextView tvUnitH, tvUnitlM;
    int viewType;

    int currentType;

    public DataHeartRateHolder(View itemView) {
        super(itemView);
    }

    public DataHeartRateHolder(List<String> datas, View itemView) {
        super(datas, itemView);
    }

    public DataHeartRateHolder(Context context, final List<String> lists, int itemID, int viewType) {
        super(context, lists, itemID);
        tvUpdateTime = itemView.findViewById(R.id.tv_update_time);
        iv_current_type = itemView.findViewById(R.id.iv_current_type);
        tv_current_type_name = itemView.findViewById(R.id.tv_current_type_name);
        tvOption = itemView.findViewById(R.id.tv_option);
        tvValueH = itemView.findViewById(R.id.tv_value_h);
        tvValueM = itemView.findViewById(R.id.tv_value_m);
        tvUnitH = itemView.findViewById(R.id.tv_unitl_h);
        tvUnitlM = itemView.findViewById(R.id.tv_unitl_m);
        this.viewType = viewType;
        setDefValues();
        tvOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick(v)) {
                    return;
                }
                if (onHeartRateOnclickListenter != null) {
                    onHeartRateOnclickListenter.onHeartRateStateListenter(true);
                }
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewMultiClickUtil.onMultiClick(v)) {
                    return;
                }
                if (onHeartRateItemClickListener != null) {
                    onHeartRateItemClickListener.onHeartRateItemClick(viewType);
                }
            }
        });

    }


    public void setDefValues() {
        /*if (iv_current_type != null) {
            iv_current_type.setImageResource(R.drawable.icon_main_heart);
        }
        if (tv_current_type_name != null) {
            tv_current_type_name.setText(UIUtils.getString(R.string.heart_rate));
        }*/
        if (tvValueM != null) {
            tvValueM.setVisibility(View.GONE);
        }
        if (tvUnitlM != null) {
            tvUnitlM.setVisibility(View.GONE);
        }
        if (tvUnitH != null) {
            tvUnitH.setVisibility(View.VISIBLE);
            tvUnitH.setText(UIUtils.getString(R.string.fragment_data_no_data));
        }
        if (tvValueH != null) {
            tvValueH.setVisibility(View.GONE);
        }

        if (tvUpdateTime != null) {
            tvUpdateTime.setVisibility(View.GONE);
        }
        switch (viewType) {
            case JkConfiguration.BODY_HEARTRATE:
                if (tvOption != null) {
                    tvOption.setText(R.string.detection);
                    tvOption.setEnabled(false);
                }
                tvOption.setVisibility(View.GONE);
                iv_current_type.setImageResource(R.drawable.icon_main_heart);
                tv_current_type_name.setText(UIUtils.getString(R.string.heart_rate));
                break;
            case JkConfiguration.BODY_BLOODPRESSURE:
                tvOption.setVisibility(View.GONE);
                tv_current_type_name.setText(UIUtils.getString(R.string.bp));
                iv_current_type.setImageResource(R.drawable.icon_main_booldpressure);
                break;
            case JkConfiguration.BODY_OXYGEN:
                tvOption.setVisibility(View.GONE);
                tv_current_type_name.setText(UIUtils.getString(R.string.mian_title_oxy));
                iv_current_type.setImageResource(R.drawable.icon_main_oxygen);
                break;
            case JkConfiguration.BODY_ONCE_HR:
                tvOption.setVisibility(View.GONE);
                tv_current_type_name.setText(UIUtils.getString(R.string.mian_title_once_hr));
                iv_current_type.setImageResource(R.drawable.icon_main_once_hr);
                break;
            case JkConfiguration.BODY_EXCERICE:
                tvOption.setVisibility(View.GONE);
                tv_current_type_name.setText(context.getString(R.string.string_f18_device_per));
                updateUI(TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis()), "0", UIUtils.getString(R.string.unit_minute));
                iv_current_type.setImageResource(R.drawable.icon_main_execise);
                break;
            case JkConfiguration.BODY_TEMP:
                tvOption.setVisibility(View.GONE);
                tv_current_type_name.setText(UIUtils.getString(R.string.temp));
                //  updateUI(TimeUtils.getTimeByyyyyMMdd(System.currentTimeMillis()), "0", UIUtils.getString(R.string.temperature_degree_centigrade));
                iv_current_type.setImageResource(R.drawable.icon_main_temp);
                break;
        }

    }

    public void updateUI(String strDate, String valuse, String unitl) {
        if (TextUtils.isEmpty(strDate) || TextUtils.isEmpty(valuse)) {
            setDefValues();
            return;
        }

        tvValueH.setVisibility(View.VISIBLE);
        tvValueH.setText(valuse);

        tvUnitH.setVisibility(View.VISIBLE);
        tvUnitH.setText(unitl);
        tvUpdateTime.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(strDate)) {
            tvUpdateTime.setVisibility(View.GONE);
        } else {
            tvUpdateTime.setVisibility(View.VISIBLE);
            tvUpdateTime.setText(strDate);
        }
    }

//连接和未连接的状态不一样

    public void updateState() {
        Logger.myLog("updateUI:AppConfiguration.isConnected" + AppConfiguration.isConnected);

        if (AppConfiguration.isConnected && (DeviceTypeUtil.isContainWrishBrand(currentType) || DeviceTypeUtil.isContainWatch(currentType))) {
            tvOption.setEnabled(true);
        } else {
            tvOption.setEnabled(false);
        }
    }


    public void updateUI(String strDate, int heartRateData) {

        BaseDevice mCurrentDevice = ISportAgent.getInstance().getCurrnetDevice();
        if (mCurrentDevice != null) {
            currentType = mCurrentDevice.deviceType;
        }
        Logger.myLog("updateUI:AppConfiguration.isConnected" + AppConfiguration.isConnected + "currentType:" + currentType);
        if (AppConfiguration.isConnected && (DeviceTypeUtil.isContainWrishBrand(currentType) || DeviceTypeUtil.isContainWatch(currentType))) {
            tvOption.setEnabled(true);
        } else {
            tvOption.setEnabled(false);
        }
        if (TextUtils.isEmpty(strDate) && heartRateData == 0) {
            iv_current_type.setImageResource(R.drawable.icon_main_heart);
            tv_current_type_name.setText(UIUtils.getString(R.string.heart_rate));
            return;
        }

        tvValueH.setVisibility(View.VISIBLE);
        tvValueH.setText(heartRateData + "");

        tvUnitH.setVisibility(View.VISIBLE);
        tvUnitH.setText(UIUtils.getString(R.string.BPM));
        tvUpdateTime.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(strDate)) {
            tvUpdateTime.setVisibility(View.GONE);
        } else {
            tvUpdateTime.setVisibility(View.VISIBLE);
            tvUpdateTime.setText(strDate);
        }
//        && currentDeviceType == JkConfiguration.DeviceType.WATCH_W516

        if (App.isBracelet()) {

            // tvOption.setText(AppConfiguration.isConnected ? R.string.detection : R.string.unconnected_device);
        } else {
            // tvOption.setText(App.isWatch() && AppConfiguration.isConnected ? R.string.detection : R.string.unconnected_device);
        }


    }

    OnHeartRateItemClickListener onHeartRateItemClickListener;
    OnHeartRateOnclickListenter onHeartRateOnclickListenter;

    public void setHeartRateItemClickListener(OnHeartRateItemClickListener onSportItemClickListener, OnHeartRateOnclickListenter onSportOnclickListenter) {
        this.onHeartRateItemClickListener = onSportItemClickListener;
        this.onHeartRateOnclickListenter = onSportOnclickListenter;
    }


    public interface OnHeartRateItemClickListener {
        void onHeartRateItemClick(int viewType);
    }


    public interface OnHeartRateOnclickListenter {
        void onHeartRateStateListenter(boolean isOpen);
    }
}
