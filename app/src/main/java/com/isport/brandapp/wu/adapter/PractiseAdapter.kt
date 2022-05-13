package com.jkcq.homebike.history.adpter

import android.content.Intent
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import brandapp.isport.com.basicres.commonutil.UIUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isport.brandapp.R
import com.isport.brandapp.wu.activity.PractiseDetailActivity
import com.isport.brandapp.wu.adapter.PractiseItemAdapter
import com.isport.brandapp.wu.bean.ExerciseInfo
import com.isport.brandapp.wu.bean.PractiseRecordInfo
import java.util.ArrayList


/**
 *  Created by BeyondWorlds
 *  on 2020/6/30
 */
class PractiseAdapter(data: MutableList<PractiseRecordInfo>) :
        BaseQuickAdapter<PractiseRecordInfo, BaseViewHolder>(R.layout.item_practise_record, data) {
    init {

        addChildClickViewIds(R.id.layout_head)
    }


    private fun updateRecyclerView(recyclerView: RecyclerView) {


    }

    lateinit var historyDayDetailAdapter: PractiseItemAdapter

    private fun setItemRecyclerView(recyclerView: RecyclerView, mExerciseInfos: MutableList<ExerciseInfo>) {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        historyDayDetailAdapter = PractiseItemAdapter(context, mExerciseInfos)
        historyDayDetailAdapter.setOnItemClickListener { v, note, position ->


            if(note.aveRate == null || note.aveRate == "0" || note.aveRate == "--")
                return@setOnItemClickListener

            val intent = Intent(context, PractiseDetailActivity::class.java)
            intent.putExtra("info", note)
            context.startActivity(intent)


            // intent.putExtra("url", AppConfiguration.ropedetailurl + "?userId=" + TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance) + "&ropeId=" + mExerciseInfos.get(position).ropeSportDetailId)
        }
        recyclerView.adapter = historyDayDetailAdapter
    }

    override fun convert(holder: BaseViewHolder, item: PractiseRecordInfo) {
        var rec = holder.getView<RecyclerView>(R.id.rec_day)


        holder.setText(R.id.tv_date, "" + item.key)
        holder.setText(R.id.tv_sum_count, String.format(UIUtils.getString(R.string.total_practise_times), item.data.size))

        if (item.isSelect) {
            holder.setVisible(R.id.rec_day, true)
            if (item!!.data != null) {
                item!!.currentShowList.addAll(item!!.data)
                holder.setImageResource(R.id.iv_arrow_right, R.drawable.icon_rope_up)
                holder.setBackgroundResource(R.id.layout_view, R.drawable.shape_btn_white_20_bg)
                setItemRecyclerView(rec, item!!.currentShowList)
            } else {
                holder.setImageResource(R.id.iv_arrow_right, R.drawable.icon_rope_down)
                holder.setBackgroundColor(R.id.layout_view, Color.TRANSPARENT)
            }
        } else {
            if (item.currentShowList != null) {
                item.currentShowList.clear()
                setItemRecyclerView(rec, item.currentShowList)
            }
            holder.setVisible(R.id.rec_day, false)
            holder.setImageResource(R.id.iv_arrow_right, R.drawable.icon_rope_down)
            holder.setBackgroundColor(R.id.layout_view, Color.TRANSPARENT)
        }

    }
}