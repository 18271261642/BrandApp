package com.isport.brandapp.device.f18.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;
import com.isport.brandapp.wu.bean.DrawRecDataBean;
import com.isport.brandapp.wu.view.SingleHeartView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/2/21
 */
public class SignalHeartAdapter extends RecyclerView.Adapter<SignalHeartAdapter.SignalHeartViewHolder> {


    private OnF18ItemClickListener onF18ItemClickListener;

    private int maxValue;

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setOnF18ItemClickListener(OnF18ItemClickListener onF18ItemClickListener) {
        this.onF18ItemClickListener = onF18ItemClickListener;
    }

    private List<DrawRecDataBean> beansList;
    private Context mContext;

    public SignalHeartAdapter(List<DrawRecDataBean> beansList, Context mContext) {
        this.beansList = beansList;
        this.mContext = mContext;
    }


    public SignalHeartAdapter(int maxValue, List<DrawRecDataBean> beansList, Context mContext) {
        this.maxValue = maxValue;
        this.beansList = beansList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SignalHeartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_signle_heart_layout,viewGroup,false);
        return new SignalHeartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignalHeartViewHolder signalHeartViewHolder, int i) {
        signalHeartViewHolder.singleHeartView.setMaxValue(maxValue);
        signalHeartViewHolder.singleHeartView.setDrawRecDataBean(beansList.get(i));
        signalHeartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = signalHeartViewHolder.getLayoutPosition();
                if(onF18ItemClickListener != null)
                    onF18ItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beansList.size();
    }

    class SignalHeartViewHolder extends RecyclerView.ViewHolder{

        private SingleHeartView singleHeartView;

        public SignalHeartViewHolder(@NonNull View itemView) {
            super(itemView);
            singleHeartView = itemView.findViewById(R.id.singleHeartView);
        }
    }
}
