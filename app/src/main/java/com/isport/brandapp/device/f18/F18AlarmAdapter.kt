package com.isport.brandapp.device.f18

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.htsmart.wristband2.bean.WristbandAlarm
import com.isport.brandapp.R
import com.isport.brandapp.util.DateTimeUtils

/**
 * Created by Admin
 *Date 2022/1/19
 */
class F18AlarmAdapter(list : MutableList<WristbandAlarm>,context: Context) :
        BaseQuickAdapter<WristbandAlarm, BaseViewHolder>(R.layout.item_f18_alarm_layout) {

    var mDayValuesSimple = arrayOf<kotlin.CharSequence?>(
    context.getString(R.string.mon),
            context.getString(R.string.tue),
            context.getString(R.string.wed),
            context.getString(R.string.thu),
            context.getString(R.string.fri),
            context.getString(R.string.sat),
            context.getString(R.string.sun))

    override fun convert(holder: BaseViewHolder, item: WristbandAlarm) {
        holder.setText(R.id.itemF18AlarmTimeTv,DateTimeUtils.getHouAdMinute(item.hour,item.minute))
        holder.setText(R.id.itemF18AlarmRepeatTv,repeatToSimpleStr(item.repeat))
        holder.setBackgroundResource(R.id.itemF18AlarmSwitchImg,if(item.isEnable) R.drawable.icon_open else R.drawable.icon_close)
    }


    //和AlarmListActivity里的方法一致
    private fun repeatToSimpleStr(repeat: Int): String? {
        var text: String? = null
        var sumDays = 0
        var resultString = ""
        for (i in 0..6) {
            if (WristbandAlarm.isRepeatEnableIndex(repeat, i)) {
                sumDays++
                resultString += mDayValuesSimple.get(i).toString() + " "
            }
        }
        if (sumDays == 7) {
            text = context.getString(R.string.every_day)
        } else if (sumDays == 0) {
            text = "永不" //context.getString(R.string.ds_alarm_repeat_never)
        } else if (sumDays == 5) {
            val sat = !WristbandAlarm.isRepeatEnableIndex(repeat, 5)
            val sun = !WristbandAlarm.isRepeatEnableIndex(repeat, 6)
            if (sat && sun) {
                text = context.getString(R.string.work_day)
            }
        } else if (sumDays == 2) {
            val sat = WristbandAlarm.isRepeatEnableIndex(repeat, 5)
            val sun = WristbandAlarm.isRepeatEnableIndex(repeat, 6)
            if (sat && sun) {
                text = context.getString(R.string.wenkend_day)
            }
        }
        if (text == null) {
            text = resultString
        }
        return text
    }
}