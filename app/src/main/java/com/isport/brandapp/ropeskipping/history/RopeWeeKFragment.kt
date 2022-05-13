package com.isport.brandapp.ropeskipping.history;

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.isport.brandapp.ropeskipping.history.adpter.CenterLayoutManager
import com.isport.brandapp.ropeskipping.history.bean.BarInfo
import com.isport.brandapp.ropeskipping.history.bean.HistoryDateBean
import com.isport.brandapp.ropeskipping.history.bean.WeekBean
import com.isport.brandapp.ropeskipping.response.ResponseDailySummaries
import com.jkcq.homebike.history.adpter.BarAdapter
import com.jkcq.homebike.history.adpter.HistoryWeekDetailAdapter
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import kotlinx.android.synthetic.main.app_fragment_rope_week.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import phone.gym.jkcq.com.commonres.common.JkConfiguration
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//周的柱状图
class RopeWeeKFragment : Fragment(), RopeSkippingView {
    //柱状图的变量初始化
    var mDataList = mutableListOf<BarInfo>()
    lateinit var mBarAdapter: BarAdapter
    lateinit var mCenterLayoutManager: CenterLayoutManager

    //运动详情的初始化
    var mDetailBean = mutableListOf<ResponseDailySummaries>()
    lateinit var mHistoryDayDetailAdapter: HistoryWeekDetailAdapter


    var mCurrentMessage: BarInfo? = null
    var mCurrentPosition = 0
    var currentPageNumber = 1

    val mRopeSkippingPresenter: RopeSkippingPresenter by lazy { RopeSkippingPresenter(this@RopeWeeKFragment) }

    var date: Int = 0
    lateinit var deviceId: String
    lateinit var userId: String
    lateinit var strStartWeek: String
    lateinit var strEndWeek: String
    lateinit var userInfoBean: UserInfoBean
    lateinit var deviceBean: DeviceBean
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date = arguments!!.getInt("date")
        deviceId = arguments!!.getString("deviceId").toString()
        userId = TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp())
        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()))
        strStartWeek = arguments!!.getString("startdate").toString()
        strEndWeek = arguments!!.getString("enddate").toString()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        // deviceBean = (arguments!!.getSerializable(JkConfiguration.DEVICE) as DeviceBean?)!!
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_fragment_rope_week, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBarRec()
        initSportDetailRec()
        //initSportDetailRec()
        initEvent()
        tv_update_time.text = "$strStartWeek~$strEndWeek"

        initData()
    }

    fun initEvent() {
        iv_data_left.setOnClickListener {
            EventBus.getDefault().post(MessageEvent(MessageEvent.DayAdd))
        }
        iv_data_right.setOnClickListener {
            EventBus.getDefault().post(MessageEvent(MessageEvent.DaySub))

        }
    }

    fun initData() {

        mRopeSkippingPresenter.getSummary(TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance), strStartWeek, "WEEK");
        mRopeSkippingPresenter.getBraceletWeekData(date)

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

    fun initBarRec() {
        mCenterLayoutManager = CenterLayoutManager(activity, RecyclerView.HORIZONTAL, true)
        recyclerview_sport.layoutManager = mCenterLayoutManager
        mBarAdapter = BarAdapter(mDataList, 7)
        recyclerview_sport.adapter = mBarAdapter

        mBarAdapter.setOnItemClickListener(OnItemClickListener { adapter, view, position ->

            if (TextUtils.isEmpty(mDataList.get(position).date)) {
                return@OnItemClickListener;
            }

            /*  mCenterLayoutManager.smoothScrollToPosition(
                      recyclerview_sport,
                      RecyclerView.State(),
                      position
              )*/
            for (user in mDataList) {
                user.isSelect = false;
            }

            mDataList.get(position).isSelect = true;
            adapter.notifyDataSetChanged()
        })
    }

    fun initSportDetailRec() {
        recycle_sport_detail.layoutManager = LinearLayoutManager(activity)

        var weekBean = WeekBean()
        val list: ArrayList<DailyBrief>? = ArrayList()
        /* list!!.add(DailyBrief())
         list!!.add(DailyBrief())
         list!!.add(DailyBrief())*/
        weekBean.list = list
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
            //adapter.notifyItemChanged(position, 101)

            /*intent.putExtra("positon", position)
            intent.putExtra("fromUserId", userId)
            intent.putExtra("list", mList)
            intent.putExtra("size", mList.size)
            intent.putExtra("isLast", isLast)
            intent.putExtra("currentPage", page)
            intent.putExtra("videoType", videoType)*/
        })
    }


    fun setSumData(dis: String, count: String, time: String, cal: String, hw: String) {
        /* tv_dis_value.text = dis
         tv_view_sport_count.text = count
         sportview_time.setValueText(time)
         sportview_cal.setValueText(cal)
         sportview_hw.setValueText(hw)*/
    }

    fun setCurrentDate(value: String) {
        // tv_date_current_value.text = value
    }

    var list: ArrayList<DailyBrief>? = ArrayList()
    override fun sucessDailyBrief(summary: MutableList<DailyBrief>?) {
        // TODO("Not yet implemented")
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
            mDataList.clear()
            mBarAdapter.notifyDataSetChanged()
            mBarAdapter.setEmptyView(R.layout.include_rope_empty_view)
        } else {
            var maxValue = 0

            summary!!.forEach {

                var date = it.day
                var bean = mDataList.findLast {
                    it.date.equals(date)
                }
                if (maxValue < it.totalSkippingNum) {
                    maxValue = it.totalSkippingNum
                }
                bean!!.currentValue = it.totalSkippingNum

            }

            mDataList.forEach {
                if (it.date.equals(summary.get(0).day)) {
                    it.isSelect = true
                }
            }

            mDataList.forEach {
                it.maxVlaue = maxValue
            }

            mBarAdapter.notifyDataSetChanged()

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
        //  TODO("Not yet implemented")
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    var containCurrentDay = false;
    override fun sucessWeekStr(summary: MutableList<HistoryDateBean>?) {
        mDataList.clear()
        containCurrentDay = false
        summary!!.forEach {
            if (it.date.equals(dateFormat.format(Date()))) {
                containCurrentDay = true
            }
            mDataList.add(BarInfo(it.date, it.mdDate))
        }
        if (containCurrentDay) {
            iv_data_right.visibility = View.INVISIBLE
        } else {
            iv_data_right.visibility = View.VISIBLE
        }
        mBarAdapter.notifyDataSetChanged()
        mRopeSkippingPresenter.getdailySummaries(mDataList.get(0).date, date, "WEEK", TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))

    }

    override fun onRespondError(message: String?) {
        //  TODO("Not yet implemented")
    }
}