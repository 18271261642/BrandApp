package com.isport.brandapp.ropeskipping

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import brandapp.isport.com.basicres.BaseApp
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog
import brandapp.isport.com.basicres.commonbean.CommonFriendRelation
import brandapp.isport.com.basicres.commonbean.UserInfoBean
import brandapp.isport.com.basicres.commonutil.*
import brandapp.isport.com.basicres.mvp.BaseMVPActivity
import brandapp.isport.com.basicres.net.userNet.CommonUserPresenter
import brandapp.isport.com.basicres.net.userNet.CommonUserView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.gyf.immersionbar.ImmersionBar
import com.isport.blelibrary.ISportAgent
import com.isport.blelibrary.db.table.s002.DailyBrief
import com.isport.blelibrary.db.table.s002.Summary
import com.isport.blelibrary.deviceEntry.impl.BaseDevice
import com.isport.blelibrary.interfaces.BleReciveListener
import com.isport.blelibrary.observe.RopeNoDataObservable
import com.isport.blelibrary.observe.RopeRealDataObservable
import com.isport.blelibrary.observe.RopeSyncDataObservable
import com.isport.blelibrary.result.IResult
import com.isport.blelibrary.utils.BleRequest
import com.isport.blelibrary.utils.Logger
import com.isport.brandapp.App
import com.isport.brandapp.AppConfiguration
import com.isport.brandapp.home.customview.MainHeadLayout
import com.isport.brandapp.home.fragment.FragmentNewData
import com.isport.brandapp.home.presenter.DeviceConnPresenter
import com.isport.brandapp.R
import com.isport.brandapp.dialog.CommuniteDeviceSyncGuideDialog
import com.isport.brandapp.login.ActivityWebView
import com.isport.brandapp.ropeskipping.history.RopeReportActivity
import com.isport.brandapp.ropeskipping.history.bean.HistoryDateBean
import com.isport.brandapp.ropeskipping.realsport.RealRopeSkippingActivity
import com.isport.brandapp.ropeskipping.response.ResponseDailySummaries
import com.isport.brandapp.ropeskipping.util.Preference
import com.isport.brandapp.util.ActivitySwitcher
import com.isport.brandapp.util.AppSP
import com.isport.brandapp.util.DateTimeUtils
import com.isport.brandapp.util.DeviceTypeUtil
import com.isport.brandapp.wu.util.HeartRateConvertUtils
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.activity_home_rope_skpping.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import phone.gym.jkcq.com.commonres.common.JkConfiguration
import phone.gym.jkcq.com.commonres.commonutil.UserUtils
import phone.gym.jkcq.com.socialmodule.report.ranking.RopeRankActivity
import java.util.*


/**
 * 跳绳主页面
 */
internal class RopeSkippingActivity() : BaseMVPActivity<RopeSkippingView, RopeSkippingPresenter>(), RopeSkippingView, CommonUserView, MainHeadLayout.ViewMainHeadClickLister {

    var commonUserPresenter: CommonUserPresenter? = null
    var mDeviceConnPresenter: DeviceConnPresenter? = null
    var mDataList = mutableListOf<RopeSkippingBean>()
    lateinit var mMessageAdapter: RopeSkippingAdapter
    var currentPageNumber = 1

    var currentCount = 0
    var currentHeart = 0;
    private var age = 0
    private var sex: String? = null


    override fun getLayoutId(): Int = R.layout.activity_home_rope_skpping
    override fun initHeader() {
        // TODO("Not yet implemented")
        ImmersionBar.with(this).titleBar(view_top).statusBarDarkFont(true)
                .init()
    }


    private val mBleReciveListener: BleReciveListener = object : BleReciveListener {
        override fun onConnResult(isConn: Boolean, isConnectByUser: Boolean, baseDevice: BaseDevice) {


            Logger.myLog("onConnResutl isConn=" + isConn + "baseDevice=" + baseDevice + "AppConfiguration.currentConnectDevice=" + AppConfiguration.currentConnectDevice)
            //这里值显示是跳绳的设备的连接状态

            if (!isConn) {
                var device = ISportAgent.getInstance().currnetDevice
                if (device != null && device.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                    layout_mainHeadLayout.showProgressBar(false)
                    layout_mainHeadLayout.setIconDeviceAlp(0.5f)
                    layout_mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.disConnect))
                }
            } else {
                setConnectState()
            }


        }

        override fun setDataSuccess(s: String) {

        }


        override fun receiveData(mResult: IResult) {

        }

        override fun onConnecting(baseDevice: BaseDevice) {
            if (baseDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                devcieConnecting()
            }
        }

        override fun onBattreyOrVersion(baseDevice: BaseDevice) {
        }
    }

    override fun initEvent() {
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
        ISportAgent.getInstance().registerListener(mBleReciveListener)
        home_refresh.setOnRefreshListener(OnRefreshListener { refreshLayout ->
            if (AppConfiguration.deviceMainBeanList == null || !AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.ROPE_SKIPPING)) {
                refreshLayout.finishRefresh()
                //refreshLayout.closeHeaderOrFooter();
                return@OnRefreshListener
            }
            if (!AppUtil.isOpenBle()) {
                refreshLayout.finishRefresh()
                return@OnRefreshListener
            }
            if (!AppConfiguration.isConnected) {
                //提示未连接设备
                //ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device));
                refreshLayout.finishRefresh()
            } else {
                home_refresh.finishRefresh(1000)
                startSyncDevice()
                onSyncOnclick()
            }
        })


        layout_mainHeadLayout.setViewClickLister(this)


        //历史记录
        tv_history.setOnClickListener {
            startActivity(Intent(this, RopeReportActivity::class.java))
        }
    }

    /**
     * 周期达标按钮点击,同步手表数据
     */
    fun onSyncOnclick() {
        // TODO: 2019/4/11 正在同步中，要处理
        if (AppConfiguration.isConnected) {
            //正在同步不能让他同步
            val device = ISportAgent.getInstance().currnetDevice
            if (device != null) {
                if (device.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                    ISportAgent.getInstance().requestBle(BleRequest.bracelet_sync_data)
                }
            } else {
                ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
            }
        } else {
            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
        }
    }


    fun openBlueDialog() {
        PublicAlertDialog.getInstance().showDialog("", context.resources.getString(R.string.bonlala_open_blue), context, resources.getString(R.string.app_bluetoothadapter_turnoff), resources.getString(R.string.app_bluetoothadapter_turnon), object : AlertDialogStateCallBack {
            override fun determine() {
//                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivity(intent);
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                bluetoothAdapter?.enable()
            }

            override fun cancel() {}
        }, false)
    }


    override fun update(o: Observable?, arg: Any?) {
        super.update(o, arg)
        if (o is RopeRealDataObservable) {

            if (arg is Int) {
                if (TextUtils.isEmpty(sex)) {
                    return
                }
                handler.post {
                    if (arg > 30) {
                        HeartRateConvertUtils.hrValueColor(arg, HeartRateConvertUtils.getMaxHeartRate(age, sex), tv_hr_value)
                        tv_hr_value.text = "" + arg
                        tv_hr_unit.visibility = View.VISIBLE
                    } else {
                        tv_hr_unit.visibility = View.GONE
                        tv_hr_value.text = UIUtils.getString(R.string.no_data)
                        tv_hr_value.setTextColor(UIUtils.getColor(R.color.common_text_color))
                    }
                }
                if (handler.hasMessages(0x02)) {
                    handler.removeMessages(0x02)
                }
                handler.sendEmptyMessageDelayed(0x02, 2000)
            }

            /*val msg = arg as RopeRealDataBean
*/


            // Logger.myLog("RopeSkipping:" + msg)
        } else if (o is RopeSyncDataObservable) {
            handler.post {
                endSyncDevice()

            }

        } else if (o is RopeNoDataObservable) {
            handler.post {
                endSyncDevice()
            }
        }
    }

    override fun initView(view: View?) {
        recyclerview_ropeskipp.layoutManager = GridLayoutManager(this, 2)
        mDataList.add(RopeSkippingBean(R.drawable.icon_rope_ranking, UIUtils.getString(R.string.rope_raking), R.drawable.shape_rope_ranking_bg, JkConfiguration.RopeSportType.Ranking))
        mDataList.add(RopeSkippingBean(R.drawable.icon_rope_count, UIUtils.getString(R.string.rope_count), R.drawable.shape_rope_ranking_bg, JkConfiguration.RopeSportType.Count))
        mDataList.add(RopeSkippingBean(R.drawable.icon_rope_time, UIUtils.getString(R.string.rope_time), R.drawable.shape_rope_ranking_bg, JkConfiguration.RopeSportType.Time))
        mDataList.add(RopeSkippingBean(R.drawable.icon_rope_free, UIUtils.getString(R.string.rope_free), R.drawable.shape_rope_ranking_bg, JkConfiguration.RopeSportType.Free))
        mDataList.add(RopeSkippingBean(R.drawable.icon_rope_change, UIUtils.getString(R.string.rope_change), R.drawable.shape_rope_ranking_bg, JkConfiguration.RopeSportType.Challenge))
       // mDataList.add(RopeSkippingBean(R.drawable.icon_rope_course, UIUtils.getString(R.string.rope_courese), R.drawable.shape_rope_ranking_bg, JkConfiguration.RopeSportType.Course))

        mMessageAdapter = RopeSkippingAdapter(mDataList)
        recyclerview_ropeskipp.adapter = mMessageAdapter

        //        smart_refresh.setEnableLoadMore(true);

        mMessageAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {

                Logger.myLog("mDataList.get(position).ropeSportType" + mDataList.get(position).ropeSportType)
                when (mDataList.get(position).ropeSportType) {
                    JkConfiguration.RopeSportType.Free -> {   //自由训练
                        var intent = Intent(this@RopeSkippingActivity, RealRopeSkippingActivity::class.java)
                        intent.putExtra("ropeSportType", JkConfiguration.RopeSportType.Free)
                        startActivity(intent)
                    }
                    JkConfiguration.RopeSportType.Time -> {    //计时训练
                        var intent = Intent(this@RopeSkippingActivity, RealRopeSkippingActivity::class.java)
                        intent.putExtra("ropeSportType", JkConfiguration.RopeSportType.Time)
                        startActivity(intent)
                    }
                    JkConfiguration.RopeSportType.Count -> {   //计数训练
                        var intent = Intent(this@RopeSkippingActivity, RealRopeSkippingActivity::class.java)
                        intent.putExtra("ropeSportType", JkConfiguration.RopeSportType.Count)
                        startActivity(intent)
                    }

                    JkConfiguration.RopeSportType.Ranking -> {   //排行榜
                        var intent = Intent(this@RopeSkippingActivity, RopeRankActivity::class.java)
                        startActivity(intent)
                    }
                    JkConfiguration.RopeSportType.Challenge -> {    //关卡挑战
                        var intent = Intent(this@RopeSkippingActivity, ActivityWebView::class.java)
                        intent.putExtra("title", UIUtils.getString(R.string.rope_change))
                        intent.putExtra("url", AppConfiguration.challegeurl)
                        startActivity(intent)
                        /* if (AppConfiguration.isConnected && AppConfiguration.currentConnectDevice != null && AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {

                         } else {
                             ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
                         }*/
                    }
                    JkConfiguration.RopeSportType.Course -> {   //跳绳课程
                        var intent = Intent(this@RopeSkippingActivity, ActivityWebView::class.java)
                        intent.putExtra("title", "线上课程")
                        intent.putExtra("url", AppConfiguration.ropeCourseUrl)
                        startActivity(intent)
                    }
                }

            }
        })


    }

    var ropeDeviceSync: Boolean by Preference(Preference.ROPE_SYNC_FIRST, false)
    override fun initData() {

        if (!ropeDeviceSync) {
            var communiteDeviceAddDialog = CommuniteDeviceSyncGuideDialog(this, R.style.AnimTop)
            communiteDeviceAddDialog.showDialog()
        }
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(broadcastReceiver, filter)
        RopeNoDataObservable.getInstance().addObserver(this)
        //rope_detail_url
        mActPresenter.getRopeUrl(TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))
        mActPresenter.getCourseUrl(TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))
        //chanllege
        mActPresenter.getRopeUrlChanlleg(TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))

        setConnectState()
        currentPageNumber = 1
    }


    fun setConnectState() {
        if (AppConfiguration.currentConnectDevice != null) {
            if (AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING && AppConfiguration.isConnected) {
                setHeadDevcieType(true)
                isShowOptin()
                deviceConnectSucess()
            } else {
                isUnConShowOptin()
                setHeadDevcieType(false)
                deviceConnectDis()
                handler.removeMessages(0x01)
            }
        } else {
            isUnConShowOptin()
            isShowOptin()
            setHeadDevcieType(false)
            deviceConnectDis()


        }
    }

    /**
     * 蓝牙开关的广播监听
     */
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                Log.e("BleService", "ACTION_STATE_CHANGED")
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
                if (state == BluetoothAdapter.STATE_ON) {
                    //蓝牙打开有两种情况，
                    /**
                     * 1.未添加设备
                     * 2.已经添加设备
                     */
                    isShowOptin()
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    //蓝牙关闭需要及时被知晓
                    isShowOptin()
                }
            }
        }
    }


    override fun createPresenter(): RopeSkippingPresenter {
        commonUserPresenter = CommonUserPresenter(this)
        mDeviceConnPresenter = DeviceConnPresenter()
        return RopeSkippingPresenter(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun Event(messageEvent: MessageEvent) {
        val currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516)
        when (messageEvent.msg) {

            MessageEvent.ROPE_DATA_UPGRADE_SUCCESS -> {
                mActPresenter.getSummary(TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance), DateTimeUtils.getCurrentDate(), "ALL")
            }
            MessageEvent.ADD_DEVICE_FIRST_PRESS -> {
                ropeDeviceSync = true
            }
        }
    }


    override fun onDestroy() {
        ISportAgent.getInstance().unregisterListener(mBleReciveListener)
        RopeNoDataObservable.getInstance().deleteObserver(this)
       // handler.removeCallbacks(null)
        EventBus.getDefault().unregister(this)
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }


    fun updateData(name: String?, headUrl: String?) {
        try {
            tv_nikeName.text = name
            if (App.appType() == App.httpType) {
                LoadImageUtil.getInstance().loadCirc(context, headUrl, iv_head)
            } else {
                if (!TextUtils.isEmpty(headUrl)) {
                    iv_head.setImageBitmap(BitmapFactory.decodeFile(headUrl))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.myLog(e.toString())
        }

    }

    override fun onResume() {
        super.onResume()
        mActPresenter.getSummary(TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance), DateTimeUtils.getCurrentDate(), "ALL")
        commonUserPresenter!!.getUserinfo(TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))
        RopeRealDataObservable.getInstance().addObserver(this)

    }

    override fun onPause() {
        super.onPause()
        RopeRealDataObservable.getInstance().deleteObserver(this)

    }

    override fun onSuccessUserInfo(userInfoBean: UserInfoBean?) {

        AppConfiguration.saveUserInfo(userInfoBean)

        if (userInfoBean != null) {
            age = UserUtils.getAge(userInfoBean.birthday)
            sex = userInfoBean.gender
            updateData(userInfoBean!!.nickName, userInfoBean!!.headUrl)
        }
        //  TODO("Not yet implemented")
    }

    override fun onSuccessUserFriendRelation(userInfoBean: CommonFriendRelation?) {
        //TODO("Not yet implemented")
    }

    override fun sucessDailyBrief(summary: MutableList<DailyBrief>?) {
        // TODO("Not yet implemented")
    }


    override fun successgetSummaryData(summary: Summary?) {
        tv_rope_count.text = summary!!.totalSkippingNum
        tv_exeCount.text = summary.totalTimes
        tv_cal.text = summary.totalCalories

        var hour = summary.hour.toInt() / 60
        var min = summary.hour.toInt() % 60

        /* if (hour == 0) {
             tv_hour_unit.visibility = View.GONE
             tv_hour.visibility = View.GONE

         } else {*/

        //}
        tv_hour.text = "" + hour
        tv_min.text = "" + min
    }

    override fun sucessDaysInMonth(summary: MutableList<String>?) {
        // TODO("Not yet implemented")
    }

    override fun sucessSummaries(summary: MutableList<ResponseDailySummaries>?) {
        // TODO("Not yet implemented")
    }

    override fun sucessMonthStr(summary: MutableList<HistoryDateBean>?) {
        TODO("Not yet implemented")
    }

    override fun sucessWeekStr(summary: MutableList<HistoryDateBean>?) {
        TODO("Not yet implemented")
    }

    @Synchronized
    private fun connectWatchOrBracelet(isConnectByUser: Boolean, deviceType: Int) {

        if (AppConfiguration.deviceMainBeanList.containsKey(deviceType)) {
            var currentName = AppConfiguration.deviceMainBeanList.get(deviceType)!!.deviceName;
            //重新升级后需要重新扫描连接 所以还是先扫描再连接计较方便
            if (DeviceTypeUtil.isContainW55X(deviceType) && AppSP.getString(context, AppSP.FORM_DFU, "false") == "false") {
                val watchMac = DeviceTypeUtil.getW526Mac(currentName, deviceType)
                if (!TextUtils.isEmpty(watchMac)) {
                    AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW526Mac(currentName, deviceType))
                }
            }
            ISportAgent.getInstance().disConDevice(false)
            val watchMac = AppSP.getString(context, AppSP.WATCH_MAC, "")
            mDeviceConnPresenter!!.connectDevice(currentName, watchMac, deviceType, true, isConnectByUser)
            //需要一个连接超时的
            handler.sendEmptyMessageDelayed(0x01, 20000)
            layout_mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.disConnect))
        }
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0x01 -> {
                    deviceConnectDis()
                    /* Logger.myLog("handler 0x01")
                     RopeCompletyDialog(this@RealRopeSkippingActivity, currentRopeCount, currentTime, currentRopeName, currentToalCal, currentRopeRes, RopeCompletyDialog.OnTypeClickListenter {
                         finish()
                     })*/
                }
                0x02 -> {
                    tv_hr_unit.visibility = View.GONE
                    tv_hr_value.text = UIUtils.getString(R.string.no_data)
                    tv_hr_value.setTextColor(UIUtils.getColor(R.color.common_text_color))
                }
                0x05 -> {
                    //超时连接
                    Logger.myLog("hander 0x05 deviceConFail")
                    //defaultConnectState(true);
                    // deviceConFail()
                }
            }

        }
    }


    /**
     * 点击取扫描,体脂秤扫描会弹出请上称提示
     *
     * @param isScale
     */
    private fun startScan(isScale: Boolean, isConnectByUser: Boolean) {
        if (AppUtil.isOpenBle()) {
            val currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516)
            Logger.myLog("mFragPresenter.scan(currentType)$currentType")
            setScanTimeOut()
            mDeviceConnPresenter!!.scan(currentType, isScale)
        } else {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, FragmentNewData.REQCODE_OPEN_BT)
        }
    }

    /**
     * 连接超时监听30秒
     */
    private fun setScanTimeOut() {
        if (handler.hasMessages(0x05)) {
            handler.removeMessages(0x05)
        }
        //scanHandler.sendEmptyMessage(0x05);
        handler.sendEmptyMessageDelayed(0x05, 20000)
        Logger.myLog("setScanTimeOut")
    }

    override fun onMainBack() {
        finish()
    }

    override fun onViewOptionClikelister(type: String?) {
        // TODO("Not yet implemented")

        if (type == MainHeadLayout.TAG_ADD) {
            //跳转到添加设备
            ActivitySwitcher.goBindAct(context)
        } else if (type == MainHeadLayout.TAG_CONNECT) {
            //连接设备  如果只连接了一个体脂秤，就直接去连接体脂秤  否则连接了手环就直接去重新连接手环
            //headConnDevice(true);
            //当前显示的是那个设备为连接就去连接那个设备
            val currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516)
            Logger.myLog("onViewOptionClikelister currentDeviceType:$currentDeviceType")
            if (DeviceTypeUtil.isContainW81(currentDeviceType)) {
                //  BraceletW811W814Manager.deviceConnState = CRPBleConnectionStateListener.STATE_DISCONNECTED;
            }
            connectWatchOrBracelet(true, JkConfiguration.DeviceType.ROPE_SKIPPING)
        } else if (type == MainHeadLayout.TAG_OPENBLE) {
            //弹出蓝牙对话框
            openBlueDialog()
        }
    }

    //头上的按钮是否显示，如果是连接状态就不要显示
    fun isShowOptin() {
        layout_mainHeadLayout.showProgressBar(false)
        layout_mainHeadLayout.setIconDeviceAlp(0.5f)
        if (!AppUtil.isOpenBle()) {
            layout_mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.app_enable), MainHeadLayout.TAG_OPENBLE, UIUtils.getString(R.string.fragment_main_no_connect_open_ble))
        } else {
            layout_mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.fragment_main_click_connect), MainHeadLayout.TAG_CONNECT, UIUtils.getString(R.string.disConnect))
        }
    }


    fun startSyncDevice() {
        AppConfiguration.hasSynced = false
        layout_mainHeadLayout.showProgressBar(true)
        layout_mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.sync_data))
    }

    fun endSyncDevice() {
        AppConfiguration.hasSynced = true
        layout_mainHeadLayout.showProgressBar(false)
        layout_mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.connected))
    }

    fun isUnConShowOptin() {
        if (!AppUtil.isOpenBle()) {
            // layout_mainHeadLayout.setIconDeviceAlp(0.5f)
            layout_mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.app_enable), MainHeadLayout.TAG_OPENBLE, UIUtils.getString(R.string.fragment_main_no_connect_open_ble))
        } else {
            // layout_mainHeadLayout.setIconDeviceAlp(1f)
            layout_mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.fragment_main_click_connect), MainHeadLayout.TAG_CONNECT, UIUtils.getString(R.string.disConnect))
            // layout_mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.app_enable), MainHeadLayout.TAG_CONNECT, UIUtils.getString(R.string.fragment_main_no_connect_open_ble))

        }
    }

    fun setHeadDevcieType(isStartConnect: Boolean) {
        layout_mainHeadLayout.setIconDeviceIcon(R.drawable.icon_main_device_list_s002)
        if (isStartConnect) {
            layout_mainHeadLayout.setIconDeviceAlp(0.5f)
        } else {
            layout_mainHeadLayout.setIconDeviceAlp(1f)
        }
    }

    fun deviceConnectSucess() {
        handler.removeMessages(0x01)
        layout_mainHeadLayout.showProgressBar(false)
        layout_mainHeadLayout.setIconDeviceAlp(1f)
        layout_mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.connected))
    }

    fun deviceConnectDis() {
        layout_mainHeadLayout.showProgressBar(false)
        layout_mainHeadLayout.showOptionButton(true, UIUtils.getString(R.string.disConnect))
        layout_mainHeadLayout.setIconDeviceAlp(0.5f)
    }

    fun devcieConnecting() {
        Logger.myLog("BraceletW811W814Manager devcieConnecting")
        layout_mainHeadLayout.showProgressBar(true)
        layout_mainHeadLayout.showOptionButton(false, UIUtils.getString(R.string.app_isconnecting))
    }


}
