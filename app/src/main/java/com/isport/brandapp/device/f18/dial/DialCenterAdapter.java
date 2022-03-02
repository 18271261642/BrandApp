package com.isport.brandapp.device.f18.dial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.commonview.RoundImageView;

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
        if(list.get(i).getStatus()==-1){    //drawable下的图片，添加图片按钮
            f18DialViewHolder.imgView.setImageResource(Integer.parseInt(url));
        }else {
            RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(110));
            Glide.with(mContext).load(url).apply(requestOptions).into(f18DialViewHolder.imgView);
          //  LoadImageUtil.getInstance().loadCirc(mContext, url, f18DialViewHolder.imgView);
         //   Glide.with(mContext).load(url).into(f18DialViewHolder.imgView);
        }


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

        private RoundImageView imgView;

        public F18DialViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.itemF18DialImgView);
        }
    }
}
