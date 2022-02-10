package com.isport.brandapp.device.f18.dial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/2/9
 */
public class DialCenterAdapter extends RecyclerView.Adapter<DialCenterAdapter.F18DialViewHolder> {

    private List<F18DialBean> list;
    private Context mContext;

    private OnF18ItemClickListener onF18ItemClickListener;

    public OnF18ItemClickListener getOnF18ItemClickListener() {
        return onF18ItemClickListener;
    }

    public void setOnF18ItemClickListener(OnF18ItemClickListener onF18ItemClickListener) {
        this.onF18ItemClickListener = onF18ItemClickListener;
    }

    public DialCenterAdapter(List<F18DialBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public F18DialViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_f18_dial_center_layout,viewGroup,false);
        return new F18DialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull F18DialViewHolder f18DialViewHolder, int i) {
        String url = list.get(i).getPreviewImgUrl();

        Glide.with(mContext).load(url).into(f18DialViewHolder.imgView);

        f18DialViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = f18DialViewHolder.getAdapterPosition();
                if(onF18ItemClickListener != null)
                    onF18ItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class F18DialViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgView;

        public F18DialViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.itemF18DialImgView);
        }
    }
}
