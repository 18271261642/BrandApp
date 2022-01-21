package com.isport.brandapp.device.f18;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.htsmart.wristband2.bean.WristbandAlarm;
import com.isport.brandapp.R;
import androidx.annotation.NonNull;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public class F18AlarmRepeatView extends Dialog implements View.OnClickListener {

    private int mRepeat = 1;
    private CharSequence[] mDayValues = null;
    private InnerAdapter mAdapter;
    private Context mContext;

    private Button confirmBtn,cancelBtn;

    private OnF18ItemClickListener f18ItemClickListener;

    public void setF18ItemClickListener(OnF18ItemClickListener f18ItemClickListener) {
        this.f18ItemClickListener = f18ItemClickListener;
    }

    public F18AlarmRepeatView(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f18_alarm_repeat_view);

        confirmBtn = findViewById(R.id.alarmConfirmBtn);
        cancelBtn = findViewById(R.id.alarmCancelBtn);
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        mDayValues = new CharSequence[]{
                mContext.getString(R.string.mon),
                mContext.getString(R.string.tue),
                mContext.getString(R.string.wed),
                mContext.getString(R.string.thu),
                mContext.getString(R.string.fri),
                mContext.getString(R.string.sat),
                mContext.getString(R.string.sun)
        };

        ListView listView = findViewById(R.id.list_view);
        mAdapter = new InnerAdapter();
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isRepeatOn = WristbandAlarm.isRepeatEnableIndex(mRepeat, position);
                if (isRepeatOn) {
                    mRepeat = WristbandAlarm.setRepeatEnableIndex(mRepeat, position, false);
                } else {
                    mRepeat = WristbandAlarm.setRepeatEnableIndex(mRepeat, position, true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });


    }

    public int getmRepeat() {
        return mRepeat;
    }

    public void setmRepeat(int mRepeat) {
        this.mRepeat = mRepeat;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.alarmConfirmBtn){
            dismiss();
            if(f18ItemClickListener != null)
                f18ItemClickListener.onItemClick(mRepeat);
        }

        if(v.getId() == R.id.alarmCancelBtn){
            dismiss();
        }
    }

    private class InnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_alarm_repeat, parent, false);
            }
            TextView text_tv = ViewHolderUtils.get(convertView, R.id.text_tv);
            ImageView select_img = ViewHolderUtils.get(convertView, R.id.select_img);
            text_tv.setText(mDayValues[position]);
            select_img.setVisibility(WristbandAlarm.isRepeatEnableIndex(mRepeat, position) ? View.VISIBLE : View.INVISIBLE);
            return convertView;
        }
    }
}
