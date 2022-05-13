package com.isport.brandapp.ropeskipping.history;

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import bike.gymproject.viewlibray.chart.BarChartEntity
import brandapp.isport.com.basicres.BaseApp
import brandapp.isport.com.basicres.commonbean.UserInfoBean
import brandapp.isport.com.basicres.commonutil.MessageEvent
import brandapp.isport.com.basicres.commonutil.TokenUtil
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.isport.blelibrary.db.table.s002.DailyBrief
import com.isport.blelibrary.db.table.s002.Summary
import com.isport.blelibrary.utils.Logger
import com.isport.brandapp.R
import com.isport.brandapp.bean.DeviceBean
import com.isport.brandapp.device.share.NewShareActivity
import com.isport.brandapp.device.share.ShareBean
import com.isport.brandapp.ropeskipping.RopeSkippingPresenter
import com.isport.brandapp.ropeskipping.RopeSkippingView
import com.isport.brandapp.ropeskipping.history.bean.BarInfo
import com.isport.brandapp.ropeskipping.history.bean.HistoryDateBean
import com.isport.brandapp.ropeskipping.history.bean.WeekBean
import com.isport.brandapp.ropeskipping.response.ResponseDailySummaries
import com.jkcq.homebike.history.adpter.HistoryWeekDetailAdapter
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import kotlinx.android.synthetic.main.app_fragment_rope_month.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import phone.gym.jkcq.com.commonres.common.JkConfiguration
import java.text.SimpleDateFormat
import java.util.*
//月柱状图
class RopeMonthFragment : Fragment(), RopeSkippingView {
    //柱状图的变量初始化
    var strDate = mutableListOf<String>()
    lateinit var updateDate: String
    var stepList = mutableListOf<Int>()
    var mDataList = mutableListOf<BarInfo>()

    val mRopeSkippingPresenter: RopeSkippingPresenter by lazy { RopeSkippingPresenter(this@RopeMonthFragment) }

    //运动详情的初始化
    var mDetailBean = mutableListOf<ResponseDailySummaries>()
    lateinit var mHistoryDayDetailAdapter: HistoryWeekDetailAdapter

    var mCurrentMessage: BarInfo? = null
    var mCurrentPosition = 0
    var currentPageNumber = 1

    var date: Int = 0
    lateinit var deviceId: String
    lateinit var userId: String
    lateinit var strStartWeek: String
    lateinit var strEndWeek: String
    lateinit var userInfoBean: UserInfoBean
    lateinit var deviceBean: DeviceBean

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_fragment_rope_month, container, false)
    }

    fun initData() {
        mRopeSkippingPresenter.getSummary(TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance), dateFormatyyyyMMdd.format(Date(date * 1000L)), "MONTH");
        mRopeSkippingPresenter.getRopeMonthData(date)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun Event(messageEvent: MessageEvent) {

        Logger.myLog("messageEvent.msg" + messageEvent.msg)
        when (messageEvent.msg) {
            MessageEvent.share -> if (userVisibleHint) {
                createShareBean()
            }
        }
    }

    fun createShareBean() {


        val intent = Intent(activity, NewShareActivity::class.java)
        //        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.DeviceType.ROPE_SKIPPING)
        val shareBean = ShareBean()
        //目标比例 设置的目标值与当前值的比列
        shareBean.centerValue = tv_rope_count.text.toString()
        shareBean.one = tv_hour.text.toString()
        //里程
        shareBean.two = tv_exeCount.text.toString()
        //消耗
        shareBean.three = tv_cal.text.toString()
        shareBean.time = tv_update_time.text.toString()

        intent.putExtra(JkConfiguration.FROM_BEAN, shareBean)
        startActivity(intent)
    }


    private val dateFormat = SimpleDateFormat("yyyy-MM")
    private val dateFormatyyyyMMdd = SimpleDateFormat("yyyy-MM-dd")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date = arguments!!.getInt("date")
        deviceId = arguments!!.getString("deviceId").toString()
        userId = TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp())
        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()))
        strStartWeek = arguments!!.getString("startdate").toString()
        strEndWeek = arguments!!.getString("enddate").toString()
        updateDate = dateFormat.format(Date(date * 1000L))
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBarRec()
        initSportDetailRec()
        //initSportDetailRec()
        tv_update_time.text = updateDate
        initEvnet()
        initData()


    }

    fun initEvnet() {
        if (updateDate.equals(dateFormat.format(Date()))) {
            iv_data_right.visibility = View.INVISIBLE
        } else {
            iv_data_right.visibility = View.VISIBLE
        }
        iv_data_left.setOnClickListener {
            EventBus.getDefault().post(MessageEvent(MessageEvent.DayAdd))
        }
        iv_data_right.setOnClickListener {
            EventBus.getDefault().post(MessageEvent(MessageEvent.DaySub))

        }
    }

    private fun setWeekBarChartData(stepList: MutableList<Int>?, date: MutableList<String>) {
        val datas: MutableList<BarChartEntity> = ArrayList()
        if (stepList == null) {
            for (i in 0..30) {
                datas.add(BarChartEntity(i.toString(), arrayOf(0f, 0f, 0f)))
            }
        } else {
            for (i in stepList.indices) {
                if (i < stepList.size) {
                    //val cal = StepArithmeticUtil.stepsConversionCaloriesFloat(userInfoBean.getWeight().toFloat(), stepList[i].toLong()).toInt()
                    // val dis = StepArithmeticUtil.stepsConversionDistanceFloat(userInfoBean.getHeight().toFloat(), userInfoBean.getGender(), stepList[i].toLong())
                    datas.add(BarChartEntity(i.toString(), arrayOf(stepList[i] * 1.0f, 0f, 0f)))
                } else {
                    datas.add(BarChartEntity(i.toString(), arrayOf(0f, 0f, 0f)))
                }
            }
            /* if (datas.size() == 31) {
                datas.add(new BarChartEntity(String.valueOf(0), new Float[]{Float.valueOf(0), Float.valueOf(0), Float.valueOf(0)}));
                date.add(date.get(date.size() - 1));
            }*/
        }
        var index = 0
        var clickPostion = 0
        while (index < stepList!!.size) {
            if (stepList.get(index) > 0) {
                clickPostion = index
            }
            //println("item at $index is ${items[index]}")
            index++
        }

        //  barChartView.setOnItemBarClickListener { position -> Log.e("TAG", "点击了：$position") }
        barChartView.setData(datas, intArrayOf(Color.parseColor("#6FC5F4")), "分组", "数量")
        barChartView.setWeekDateList(date)
        barChartView.setCurrentType(516)
        barChartView.startAnimation()
        handler.postDelayed({
            barChartView.setmClickPosition(clickPostion)
        }, 50)


    }

    var handler: Handler = Handler()

    fun initBarRec() {
        //  barChartView
    }

    fun initSportDetailRec() {
        recycle_sport_detail.layoutManager = LinearLayoutManager(activity)

        val list: ArrayList<DailyBrief>? = ArrayList()
        mHistoryDayDetailAdapter = HistoryWeekDetailAdapter(mDetailBean)
        recycle_sport_detail.adapter = mHistoryDayDetailAdapter


        mHistoryDayDetailAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (mDetailBean.get(position).isOpen) {
                mDetailBean.get(position).isOpen = false
                mHistoryDayDetailAdapter.notifyDataSetChanged()
            } else {


                mDetailBean.forEach {
                    it.isOpen = false
                }

                if (mDetailBean.get(position).list == null || mDetailBean.get(position).list.size == 0) {
                    currentDetailIndex = position
                    mRopeSkippingPresenter.getDailyBrief(mDetailBean.get(position).day, TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))

                } else {
                    mDetailBean.get(position).isOpen = true
                    mHistoryDayDetailAdapter.notifyDataSetChanged()
                }
            }
        }

        mHistoryDayDetailAdapter.setOnItemClickListener(OnItemClickListener { adapter, view, position ->

        })
    }


    var list: ArrayList<DailyBrief>? = ArrayList()
    override fun sucessDailyBrief(summary: MutableList<DailyBrief>?) {
        list!!.clear()
        summary!!.forEach {
            list!!.add(it)
        }
        if (mDetailBean.size > currentDetailIndex) {
            mDetailBean.get(currentDetailIndex).isOpen = true
            mDetailBean.get(currentDetailIndex).list
                    .addAll(list!!)
        }

        mHistoryDayDetailAdapter.notifyDataSetChanged()
    }

    override fun <T : Any?> bindAutoDispose(): AutoDisposeConverter<T> {
        //  TODO("Not yet implemented")
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY))
    }

    override fun successgetSummaryData(summary: Summary?) {
        //  TODO("Not yet implemented")
        tv_rope_count.text = summary!!.totalSkippingNum
        tv_exeCount.text = summary!!.totalTimes
        tv_cal.text = summary!!.totalCalories
        tv_hour.text = summary!!.strTime
    }


    var currentDetailIndex = 0
    override fun sucessDaysInMonth(summary: MutableList<String>?) {
        // TODO("Not yet implemented")
    }

    override fun sucessSummaries(summary: MutableList<ResponseDailySummaries>?) {


        if (summary == null || summary.size == 0) {
            barChartView.visibility = View.GONE
            iv_no_data.visibility = View.VISIBLE
        } else {
            barChartView.visibility = View.VISIBLE
            iv_no_data.visibility = View.GONE
            var maxValue = 0
            stepList.clear()
            strDate.clear()
            summary?.forEach {

                var date = it.day
                var bean = mDataList.findLast {
                    it.date.equals(date)
                }
                bean!!.currentValue = it.totalSkippingNum

            }

            mDataList.forEach {
                stepList.add(it.currentValue)
                strDate.add(it.mdDate)
            }


            setWeekBarChartData(stepList, strDate)


            mDetailBean.clear()
            mDetailBean.addAll(summary)
            if (mDetailBean.size != 0) {
                currentDetailIndex = 0
                mDetailBean.get(0).isOpen = true
                mRopeSkippingPresenter.getDailyBrief(mDetailBean.get(0).day, TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))
            }
            mHistoryDayDetailAdapter.notifyDataSetChanged()
        }

        // TODO("Not yet implemented")
    }

    override fun sucessMonthStr(summary: MutableList<HistoryDateBean>?) {
        mDataList.clear()
        summary!!.forEach {
            mDataList.add(BarInfo(it.date, it.mdDate))
        }
        // mBarAdapter.notifyDataSetChanged()
        if (mDataList.size != 0) {
            mRopeSkippingPresenter.getdailySummaries(mDataList.get(0).date, date, "MONTH", TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))
        }
    }

    override fun sucessWeekStr(summary: MutableList<HistoryDateBean>?) {


    }

    override fun onRespondError(message: String?) {
        //  TODO("Not yet implemented")
    }
}