package com.isport.brandapp.device.f18;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.model.F18ContactBean;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Admin
 * Date 2022/1/19
 */
public class F18ContactAdapter extends RecyclerView.Adapter<F18ContactAdapter.F18ContactViewHolder> {


    private OnF18LongClickListener onF18LongClickListener;

    public void setOnF18LongClickListener(OnF18LongClickListener onF18LongClickListener) {
        this.onF18LongClickListener = onF18LongClickListener;
    }

    private Context mContext;
    private List<F18ContactBean> list;

    public F18ContactAdapter(Context mContext, List<F18ContactBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public F18ContactViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_f18_contact_layout,viewGroup,false);
        return new F18ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull F18ContactViewHolder f18ContactViewHolder, int i) {
        f18ContactViewHolder.nameTv.setText(list.get(i).getContactName());
        f18ContactViewHolder.numberTv.setText(list.get(i).getContactNumber());

        f18ContactViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onF18LongClickListener != null){
                    int position = f18ContactViewHolder.getLayoutPosition();
                    onF18LongClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class F18ContactViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTv,numberTv;


        public F18ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.itemF18ContactNameTv);
            numberTv = itemView.findViewById(R.id.itemF18ContactNumberTv);
        }
    }
}
