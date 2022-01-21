package com.isport.brandapp.device.f18;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htsmart.wristband2.bean.WristbandAlarm;
import com.isport.brandapp.R;
import com.isport.brandapp.util.DateTimeUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public class CusF18AlarmAdapter extends RecyclerView.Adapter<CusF18AlarmAdapter.F18ViewHolder> {


    private static Context mContext;
    private List<WristbandAlarm> list;
    private static CharSequence[] mDayValuesSimple;

    private OnF18ItemClickListener onF18ItemClickListener;

    public void setOnF18ItemClickListener(OnF18ItemClickListener onF18ItemClickListener) {
        this.onF18ItemClickListener = onF18ItemClickListener;
    }

    public CusF18AlarmAdapter(Context context, List<WristbandAlarm> list) {
        this.mContext = context;
        this.list = list;
        mDayValuesSimple = new CharSequence[]{    context.getString(R.string.mon),
                context.getString(R.string.tue),
                context.getString(R.string.wed),
                context.getString(R.string.thu),
                context.getString(R.string.fri),
                context.getString(R.string.sat),
                context.getString(R.string.sun)
        };

    }

    @NonNull
    @Override
    public F18ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_f18_alarm_layout,viewGroup,false);
        return new F18ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull F18ViewHolder f18ViewHolder, int i) {
        WristbandAlarm wristbandAlarm = list.get(i);
        f18ViewHolder.alarmName.setText(DateTimeUtils.getHouAdMinute(wristbandAlarm.getHour(),wristbandAlarm.getMinute()));
        f18ViewHolder.weekTv.setText(repeatToSimpleStr(wristbandAlarm.getRepeat()));
        f18ViewHolder.switchImg.setImageResource(wristbandAlarm.isEnable() ? R.drawable.icon_open : R.drawable.icon_close);

        f18ViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onF18ItemClickListener != null){
                    int position = f18ViewHolder.getLayoutPosition();
                    onF18ItemClickListener.onItemClick(position);
                }
            }
        });

        f18ViewHolder.switchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onF18ItemClickListener != null){
                    int position = f18ViewHolder.getLayoutPosition();
                    onF18ItemClickListener.onChildClick(position);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class F18ViewHolder extends RecyclerView.ViewHolder{

        private TextView alarmName;
        private TextView weekTv;
        private ImageView switchImg;


        public F18ViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmName = itemView.findViewById(R.id.itemF18AlarmTimeTv);
            weekTv = itemView.findViewById(R.id.itemF18AlarmRepeatTv);
            switchImg = itemView.findViewById(R.id.itemF18AlarmSwitchImg);
        }
    }

    //和AlarmListActivity里的方法一致
    public static String repeatToSimpleStr(int repeat) {
        String text = null;
        int sumDays = 0;
        String resultString = "";
        for (int i = 0; i < 7; i++) {
            if (WristbandAlarm.isRepeatEnableIndex(repeat, i)) {
                sumDays++;
                resultString += (mDayValuesSimple[i] + " ");
            }
        }
        if (sumDays == 7) {
            text = mContext.getString(R.string.every_day);
        } else if (sumDays == 0) {
            text = "永不";
        } else if (sumDays == 5) {
            boolean sat = !WristbandAlarm.isRepeatEnableIndex(repeat, 5);
            boolean sun = !WristbandAlarm.isRepeatEnableIndex(repeat, 6);
            if (sat && sun) {
                text = mContext.getString(R.string.work_day);
            }
        } else if (sumDays == 2) {
            boolean sat = WristbandAlarm.isRepeatEnableIndex(repeat, 5);
            boolean sun = WristbandAlarm.isRepeatEnableIndex(repeat, 6);
            if (sat && sun) {
                text = mContext.getString(R.string.wenkend_day);
            }
        }
        if (text == null) {
            text = resultString;
        }
        return text;
    }
}
