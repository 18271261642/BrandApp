package com.isport.brandapp.device.f18.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;
import com.isport.brandapp.wu.bean.TempInfo;
import com.isport.brandapp.wu.view.SingleTemptureView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/2/24
 */
public class SingleTempAdapter extends RecyclerView.Adapter<SingleTempAdapter.SingleTempViewHolder> {


    private OnF18ItemClickListener onF18ItemClickListener;

    public void setOnF18ItemClickListener(OnF18ItemClickListener onF18ItemClickListener) {
        this.onF18ItemClickListener = onF18ItemClickListener;
    }

    private List<TempInfo> tempList;
    private Context mContext;

    public SingleTempAdapter(List<TempInfo> tempList, Context mContext) {
        this.tempList = tempList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SingleTempViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_single_temp_layout,viewGroup,false);
        return new SingleTempViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleTempViewHolder singleTempViewHolder, int i) {
        singleTempViewHolder.singleTemptureView.setMaxValue(tempList.get(i).getTempUnitl().equals("0") ? 42 : 110);
        singleTempViewHolder.singleTemptureView.setTempInfo(tempList.get(i));
        singleTempViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = singleTempViewHolder.getLayoutPosition();
                if(onF18ItemClickListener != null)
                    onF18ItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tempList.size();
    }

    class SingleTempViewHolder extends RecyclerView.ViewHolder{

        private SingleTemptureView singleTemptureView;

        public SingleTempViewHolder(@NonNull View itemView) {
            super(itemView);
            singleTemptureView = itemView.findViewById(R.id.itemSingleTempView);
        }
    }
}
