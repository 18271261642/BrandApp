package com.isport.brandapp.banner.recycleView.holder;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * Description：
 *
 * @Author：桑小年
 * @Data：2016/11/8 11:58
 */
public class HeardHolder<T> extends CustomPeakHolder<T> {
    public HeardHolder(View itemView) {
        super(itemView);
    }

    public HeardHolder(List datas, View itemView) {
        super(datas, itemView);
    }

    public HeardHolder(Context context, List lists, int itemID) {
        super(context, lists, itemID);
    }

}

