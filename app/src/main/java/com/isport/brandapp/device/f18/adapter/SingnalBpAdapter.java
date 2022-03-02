package com.isport.brandapp.device.f18.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.view.SingleBpView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 血压左右滑动adapter
 * Created by Admin
 * Date 2022/2/24
 */
public class SingnalBpAdapter extends RecyclerView.Adapter<SingnalBpAdapter.SingnalBpViewHolder> {


    private OnF18ItemClickListener onF18ItemClickListener;

    public void setOnF18ItemClickListener(OnF18ItemClickListener onF18ItemClickListener) {
        this.onF18ItemClickListener = onF18ItemClickListener;
    }

    private List<BPInfo> list;
    private Context context;

    public SingnalBpAdapter(List<BPInfo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SingnalBpViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_single_bp_layout,viewGroup,false);
        return new SingnalBpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingnalBpViewHolder singnalBpViewHolder, int i) {
        singnalBpViewHolder.singleBpView.setMaxValue(160);
        singnalBpViewHolder.singleBpView.setBpInfo(list.get(i));

        singnalBpViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = singnalBpViewHolder.getLayoutPosition();
                if(onF18ItemClickListener != null)
                    onF18ItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SingnalBpViewHolder extends RecyclerView.ViewHolder{

        private SingleBpView singleBpView;

        public SingnalBpViewHolder(@NonNull View itemView) {
            super(itemView);
            singleBpView = itemView.findViewById(R.id.itemSingleBpView);
        }
    }
}
