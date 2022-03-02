package com.isport.brandapp.wu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isport.blelibrary.utils.Logger;
import com.isport.blelibrary.utils.TimeUtils;
import com.isport.brandapp.R;
import com.isport.brandapp.wu.bean.OnceHrInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.commonutil.UIUtils;

public class OnceHrAdapter extends RecyclerView.Adapter<OnceHrAdapter.MyViewHolder> {

    private List<OnceHrInfo> mDatas;
    private Context mContext;

    public OnceHrAdapter(Context context, List<OnceHrInfo> data) {
        this.mDatas = data;
        mContext = context;
        if (data == null) {
            mDatas = new ArrayList<>();
        }
    }

    public void replaceData(List<OnceHrInfo> data) {
        this.mDatas = data;
        notifyDataSetChanged();
    }

    @Override
    public OnceHrAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_oxy, parent, false));
    }

    @Override
    public void onBindViewHolder(OnceHrAdapter.MyViewHolder holder, int position) {
        Logger.myLog(mDatas.get(position).toString());
        OnceHrInfo info = mDatas.get(position);
        holder.tv_value.setText("" + info.getHeartValue());
        holder.tv_unit.setText(UIUtils.getString(R.string.onece_hr_unit));
        holder.tv_time.setText(TimeUtils.getTimeByyyyyMMddhhmmss(info.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_value;
        TextView tv_time;
        TextView tv_unit;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_unit = itemView.findViewById(R.id.tv_unit);

        }
    }
}
