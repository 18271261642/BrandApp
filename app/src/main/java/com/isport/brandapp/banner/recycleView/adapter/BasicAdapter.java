package com.isport.brandapp.banner.recycleView.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.isport.brandapp.banner.recycleView.holder.CustomHolder;
import com.isport.brandapp.banner.recycleView.holder.CustomPeakHolder;
import com.isport.brandapp.banner.recycleView.inter.CustomAdapterListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Description：
 * <p>
 * Author： 桑小年
 * <p>
 * Data： 2016/11/7 16:43
 */
public class BasicAdapter<T> extends RecyclerView.Adapter {
    protected List<T>                  lists  = new ArrayList<>();
    public    int                      itemID;
    protected List<CustomPeakHolder>   heards = new ArrayList<CustomPeakHolder>();
    protected List<CustomPeakHolder>   foots  = new ArrayList<CustomPeakHolder>();
    protected List<CustomPeakHolder>   tops   = new ArrayList<CustomPeakHolder>();
    protected List<CustomPeakHolder>   booms  = new ArrayList<>();
    public    CustomAdapterListener<T> listener;

    public Context context;

    public static final int BODY = 100000;
    public static final int TOP = 100001;
    public static final int FOOT = 100002;

    public static final int BODY_BANNER = 100003;
    public static final int BODY_END = 100004;
    public static final int BODY_HEADER = 100005;
    public static final int BODY_DEVICE = 100006;
    public static final int BODY_DEVICE1 = 100007;
    public static final int BODY_DEVICE2 = 100008;
    public static final int BODY_SCALE = 100009;
    public static final int BODY_HEARTRATE = 100010;
    public static final int BODY_SLEEP = 100011;
    public static final int BODY_OXYGEN = 100012;
    public static final int BODY_BLOODPRESSURE = 100013;
    public static final int BODY_EXCERICE = 100014;
    public static final int BODY_ONECE_HR = 100015;
    public static final int BODY_TEMP = 100016;


    protected BasicAdapter(Context context, List<T> lists, int itemID, CustomAdapterListener<T> listener) {
        if (lists != null) {
            this.lists = lists;
        }
        this.itemID = itemID;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (listener != null) {
            return listener.getItemTypeByPosition();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CustomHolder holder = null;
        if (listener != null) {
            holder = listener.getHolderByViewType(context, lists, viewType);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (listener != null) {
            listener.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return lists.size() + heards.size() + foots.size() + tops.size() + booms.size();
    }


    /**
     * 添加头布局
     *
     * @param heardHolder 头布局的holder
     */
    public void addHead(CustomPeakHolder heardHolder) {
        heards.add(heardHolder);
    }

    /**
     * 添加头布局
     *
     * @param heardHolder 头布局的holder
     */
    public void addHead(int index, CustomPeakHolder heardHolder) {

        heards.add(index, heardHolder);
    }

    /**
     * 添加脚布局
     *
     * @param footHolder
     */
    public void addFoots(CustomPeakHolder footHolder) {
        foots.add(footHolder);
    }

    /**
     * 添加顶部刷新局
     *
     * @param topHolder
     */
    public void addTop(CustomPeakHolder topHolder) {
        tops.clear();
        tops.add(topHolder);
    }


    /**
     * 添加顶部刷新局
     *
     * @param boomHolder
     */
    public void addBoom(CustomPeakHolder boomHolder) {
        booms.clear();
        booms.add(boomHolder);
    }


    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeader(position) || isFooter(position))
                            ? gridManager.getSpanCount() : 1;
                }


            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }


    public boolean isFooter(int position) {
        return position >= heards.size() + lists.size() + tops.size() && position < tops.size() + heards.size() + lists.size() + foots.size() + booms.size();
    }

    public boolean isHeader(int position) {
        return position >= 0 && position < heards.size() + tops.size();
    }

    public List<T> getBodyLists() {
        return lists;
    }

    public void upData(List<T> list) {
        this.lists = list;
        notifyDataSetChanged();

    }

    public List<CustomPeakHolder> getHeards() {
        return heards;
    }

    public List<CustomPeakHolder> getFoots() {
        return foots;
    }

    public List<CustomPeakHolder> getTops() {
        return tops;
    }

    public List<CustomPeakHolder> getBooms() {
        return booms;
    }
}
