package com.isport.brandapp.home.fragment


import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import brandapp.isport.com.basicres.BaseApp
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog
import brandapp.isport.com.basicres.commonutil.*
import brandapp.isport.com.basicres.service.observe.BatteryLowObservable
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.isport.blelibrary.ISportAgent
import com.isport.blelibrary.db.CommonInterFace.DeviceMessureData
import com.isport.blelibrary.db.action.DeviceInformationTableAction
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_DeviceInfoModelAction
import com.isport.blelibrary.db.action.s002.S002_DetailDataModelAction
import com.isport.blelibrary.db.table.scale.Scale_FourElectrode_DataModel
import com.isport.blelibrary.deviceEntry.impl.BaseDevice
import com.isport.blelibrary.deviceEntry.interfaces.IDeviceType
import com.isport.blelibrary.interfaces.BleReciveListener
import com.isport.blelibrary.managers.Watch7018Manager
import com.isport.blelibrary.observe.BatteryChangeObservable
import com.isport.blelibrary.observe.RopeSyncDataObservable
import com.isport.blelibrary.observe.bean.BatteryChangeBean
import com.isport.blelibrary.result.IResult
import com.isport.blelibrary.result.impl.watch.DeviceMessureDataResult
import com.isport.blelibrary.utils.BleRequest
import com.isport.blelibrary.utils.Constants
import com.isport.blelibrary.utils.Logger
import com.isport.brandapp.App
import com.isport.brandapp.AppConfiguration
import com.isport.brandapp.home.DeviceMainActivity
import com.isport.brandapp.home.adpter.AdapterMainDeviceList
import com.isport.brandapp.home.bean.AdviceBean
import com.isport.brandapp.home.bean.MainDeviceBean
import com.isport.brandapp.home.bean.ScacleBean
import com.isport.brandapp.home.presenter.DeviceListPresenter
import com.isport.brandapp.home.presenter.W81DataPresenter
import com.isport.brandapp.home.view.DeviceListView
import com.isport.brandapp.R
import com.isport.brandapp.bean.DeviceBean
import com.isport.brandapp.bind.ActivityAllDevice
import com.isport.brandapp.bind.ActivityDeviceSetting
import com.isport.brandapp.device.bracelet.ActivityBraceletMain
import com.isport.brandapp.device.bracelet.braceletPresenter.WeatherPresenter
import com.isport.brandapp.device.f18.F18DeviceMainActivity
import com.isport.brandapp.device.scale.ScaleMainActivity
import com.isport.brandapp.device.watch.ActivityWatchMain
import com.isport.brandapp.device.f18.F18WatchManagerActivity
import com.isport.brandapp.dialog.CommuniteDeviceAddDialog
import com.isport.brandapp.dialog.CommuniteDeviceSettingDialog
import com.isport.brandapp.login.ActivityWebView
import com.isport.brandapp.ropeskipping.RopeSkippingActivity
import com.isport.brandapp.ropeskipping.setting.RopeDeviceSettingActivity
import com.isport.brandapp.ropeskipping.util.Preference
import com.isport.brandapp.util.ActivitySwitcher
import com.isport.brandapp.util.AppSP
import com.isport.brandapp.util.DeviceTypeUtil
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.AutoDisposeConverter
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.app_fragment_device.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import phone.gym.jkcq.com.commonres.common.JkConfiguration
import java.util.*
import kotlin.collections.HashMap


/**
 * 数据主页面
 */
class FragmnetMainDeviceList() : Fragment(), DeviceListView, Observer, View.OnTouchListener {

     var tg  = "FragmnetMainDeviceList"


    private var lastX = 0
    private var lastY: Int = 0
    var IMAGE_SIZE = 150

    //需要记录连接的mac地址
    //var locationServiceHelper: LocationServiceHelper? = null


    var mDataList = mutableListOf<MainDeviceBean>()
    var mAdvice = mutableListOf<AdviceBean>()
    lateinit var mMessageAdapter: AdapterMainDeviceList
    var mCurrentMessage: MainDeviceBean? = null
    var communiteDeviceSetDialog: CommuniteDeviceSettingDialog? = null;
    var communiteDeviceAddDialog: CommuniteDeviceAddDialog? = null;
    var mCurrentPosition = 0
    var currentPageNumber = 1

    val mFragPresenter: DeviceListPresenter by lazy { DeviceListPresenter(this@FragmnetMainDeviceList) }
    val weatherPresenter: WeatherPresenter by lazy { WeatherPresenter(this@FragmnetMainDeviceList) }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_fragment_device, container, false)
    }


    fun initEvent() {
        ISportAgent.getInstance().registerListener(mBleReciveListener)

        // iv_add.setOnTouchListener(this)

        iv_add?.setOnClickListener {

            if (AppConfiguration.deviceMainBeanList != null && AppConfiguration.deviceMainBeanList.size > 0) {
                startActivity(Intent(activity, ActivityAllDevice::class.java))
            } else {
                ActivitySwitcher.goScanActivty(activity, JkConfiguration.DeviceType.ALL)
            }


        }
        iv_def?.setOnClickListener {
            if (AppConfiguration.isConnected) {
                S002_DetailDataModelAction.deletAll()
                ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_TEST_RESET);
            } else {
                ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
            }
        }

    }

    private val mBleReciveListener: BleReciveListener = object : BleReciveListener {
        override fun onConnResult(isConn: Boolean, isConnectByUser: Boolean, baseDevice: BaseDevice) {

            Logger.myLog(tg,"fragmentMainDeviceList isConn$isConn baseDevice=${baseDevice.toString()}")
            AppConfiguration.braceletID = baseDevice.deviceName
            if (!isConn) {
                AppConfiguration.isConnected = false
                AppConfiguration.currentConnectDevice = null
                mDataList.forEach {
                    it.isConn = false
                    it.connState = 0;
                    mMessageAdapter.notifyDataSetChanged()
                }
            } else {
//                var deviceB = DeviceBean(baseDevice.getDeviceType(),baseDevice.getDeviceName())
//                AppConfiguration.deviceMainBeanList.put(baseDevice.deviceType,deviceB)

                mDataList.forEach {
                    Logger.myLog(tg,"--------保存的集合="+it.toString())
                }


                var macStr = baseDevice.address;
                AppSP.putString(context, AppSP.WATCH_MAC, macStr)
                //连接成功
                if (Constants.wristbandWeather == null) {
                    weatherPresenter.getWeather(Constants.mLocationLatitude.toDouble(), Constants.mLocationLongitude.toDouble(), Constants.cityName, 814)
                }
                AppConfiguration.hasSynced = true
                AppSP.macMap.put(baseDevice.deviceName, baseDevice.address)
                if (DeviceTypeUtil.isContainW55X(baseDevice.deviceType)) {
                    AppSP.putString(context, AppSP.FORM_DFU, "false")
                }
                AppConfiguration.isConnected = isConn
                AppConfiguration.currentConnectDevice = baseDevice!!
                var bean = mDataList.findLast {
                    Logger.myLog(tg,"--------baseDevice.deviceType="+baseDevice.deviceType)
                    it.deviceType == baseDevice.deviceType
                }
                //把连接状态都改成false 再去重新赋值现在连接的状态
                mDataList.forEach {
                    it.isConn = false
                    it.connState = 0;
                }

                Logger.myLog(tg,"------bean==nul="+(bean == null))
                if (bean != null) {
                    val battery = getVersionOrBattery(bean?.deviceType, bean?.devicename)
                    bean.isConn = isConn
                    bean.connState = 1;
                    bean.battery = battery
                    mMessageAdapter.notifyDataSetChanged()
                }
            }

            var connSaveMac =  AppSP.getString(context, AppSP.WATCH_MAC, "")
            Logger.myLog(tg, "------连接成功保存Mac=$connSaveMac")

        }

        override fun setDataSuccess(s: String) {}
        val isBindScale: Boolean
            get() {
                if (AppConfiguration.deviceMainBeanList == null || AppConfiguration.deviceMainBeanList.size == 0) {
                    return false
                }
                return if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
                    true
                } else false
            }

        override fun receiveData(mResult: IResult) {

            AppConfiguration.hasSynced = true
            try {
                if (mResult != null) when (mResult.type) {

                    IResult.DEVICE_MESSURE -> {

                        var deviceMessureDataResult = mResult as DeviceMessureDataResult
                        var deviceName = deviceMessureDataResult.getMac()
                        when (deviceMessureDataResult.messureType) {
                            DeviceMessureData.update_weather -> {
                                var devicetype = 814
                                if (AppConfiguration.currentConnectDevice != null) {
                                    devicetype = AppConfiguration.currentConnectDevice.deviceType
                                }
                                weatherPresenter.getWeather(Constants.mLocationLatitude.toDouble(), Constants.mLocationLongitude.toDouble(), Constants.cityName, devicetype)
                            }
                        }
                    }


                    IResult.WATCH_W516_SETTING -> if (AppConfiguration.hasSynced) {
                        /*Intent intentHr = new Intent(context, ActivityWatchHeartRateIng.class);
                                intentHr.putExtra(JkConfiguration.DEVICE, deviceBean.deviceName);
                                startActivity(intentHr);*/
                    }

                    else -> {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun onConnecting(baseDevice: BaseDevice) {
            var bean = mDataList.findLast {
                it.deviceType == baseDevice.deviceType
            }
            //0,1,2
            bean?.connState = 1;
            mMessageAdapter.notifyDataSetChanged()
        }

        override fun onBattreyOrVersion(baseDevice: BaseDevice) {
            var bean = mDataList.findLast {
                it.deviceType == baseDevice.deviceType
            }
            if (bean != null) {
                bean.battery = getVersionOrBattery(bean.deviceType, bean.devicename)

            }
            mMessageAdapter.notifyDataSetChanged()
        }
    }

    fun initView() {
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
        /*rece = view!!.findViewById(R.id.recyclerview_device_list)
        view_top = view!!.findViewById(R.id.view_top)
        iv_add = view!!.findViewById(R.id.iv_add)
        iv_def = view!!.findViewById(R.id.iv_def)
        mBanner = view!!.findViewById(R.id.tv_banner)*/


    }

    var isFirst = false;
    override fun onResume() {
        super.onResume()
        if (!isFirst) {
            isFirst = true
        } else {
            mFragPresenter.getDeviceList(false, false, true, false)
        }
        tv_banner.start()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            tv_banner.start()
            mFragPresenter.getDeviceList(false, false, true, false)
            sysncDevcieDataToServer(JkConfiguration.DeviceType.ROPE_SKIPPING)
        } else {
            /* if (locationServiceHelper != null) {
                 locationServiceHelper!!.stopLocation()
             }*/
            tv_banner.stop()
        }
    }

    override fun onStop() {
        super.onStop()
        tv_banner.stop()
        /*locationServiceHelper = LocationServiceHelper(activity)
        locationServiceHelper!!.stopLocation()*/
    }

    override fun onPause() {
        super.onPause()
    }

    fun initData() {
        /*  locationServiceHelper = LocationServiceHelper(activity)
          locationServiceHelper!!.startLocation()*/

        //IMAGE_SIZE = DisplayUtils.dip2px(context, 160f)
        if (!addDeviceAddDeviceFirst) {
            var communiteDeviceAddDialog = CommuniteDeviceAddDialog(activity, R.style.AnimRight)
            communiteDeviceAddDialog.showDialog()
        }

        /* CustomDialogUtil.customDialog(activity,
                 R.layout.dialog_device_guide,
                 R.style.theme_customer_progress_dialog,
                 Gravity.CENTER_HORIZONTAL,
                 WindowManager.LayoutParams.WRAP_CONTENT,
                 WindowManager.LayoutParams.WRAP_CONTENT,
                 R.style.AnimRight);*/
        mFragPresenter.getAdviceList()
        BatteryChangeObservable.getInstance().addObserver(this)
        RopeSyncDataObservable.getInstance().addObserver(this)
        recyclerview_device_list.layoutManager = LinearLayoutManager(activity)
        // mDataList.add(MainDeviceBean())
        mMessageAdapter = AdapterMainDeviceList(mDataList)
        recyclerview_device_list.adapter = mMessageAdapter
        mMessageAdapter.setOnItemChildClickListener(object : OnItemChildClickListener {
            override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {

                if (ViewMultiClickUtil.onMultiClick()) {
                    return
                }

                mCurrentMessage = mDataList.get(position)
                mCurrentPosition = position
                var deviceType = mDataList.get(position).deviceType
                when (view.id) {
                    R.id.iv_setting -> {

                        if(context == null)
                            return


//                        val intent = Intent(context, F18WatchManagerActivity::class.java)
//                        intent.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceMainBeanList.get(deviceType))
//                        startActivity(intent)

                        if (!mCurrentMessage!!.isConn) {
                            if (mCurrentMessage!!.deviceType == JkConfiguration.DeviceType.BODYFAT) {   //体脂称
                                val intent = Intent(context, ActivityDeviceSetting::class.java)
                                intent.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceMainBeanList.get(deviceType))
                                startActivity(intent)
                            } else {
                                if (!AppUtil.isOpenBle()) {
                                    openBlueDialog()
                                    return
                                }

                                showConnDialog()
                            }
                            return
                        }

                        if(DeviceTypeUtil.isConnectF18(deviceType)){
                            var db = AppConfiguration.deviceMainBeanList[deviceType];
                            if (db != null) {
                                AppConfiguration.braceletID = db.getDeviceID()
                            }
                        val intent = Intent(context, F18WatchManagerActivity::class.java)
                        intent.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceMainBeanList.get(deviceType))
                        startActivity(intent)

                            return
                        }

                        if (DeviceTypeUtil.isContainWatch(deviceType)) {   //手表设置页面
                            //if (AppConfiguration.hasSynced) {
                            val intent2 = Intent(context, ActivityWatchMain::class.java)
                            //bean.mac = ISportAgent.getInstance().currnetDevice.address

                            Logger.myLog(tg,"--------手表设置="+Gson().toJson(AppConfiguration.deviceMainBeanList)+"\n"+AppConfiguration.deviceMainBeanList.get(deviceType).toString())
                            var db = AppConfiguration.deviceMainBeanList[deviceType];
                            if (db != null) {
                                AppConfiguration.braceletID = db.getDeviceID()
                            }
                            intent2.putExtra(JkConfiguration.DEVICE, db )
                            startActivity(intent2)
                            /* } else {
                                ToastUtils.showToast(activity, R.string.sync_data)
                            }*/
                        } else if (DeviceTypeUtil.isContainWrishBrand(deviceType) || DeviceTypeUtil.isContaintW81(deviceType)) {
                            Logger.myLog(tg,"onDeviceItemListener11" + System.currentTimeMillis())
                            //if (AppConfiguration.hasSynced) {
                            val intent2 = Intent(context, ActivityBraceletMain::class.java)
                            intent2.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceMainBeanList.get(deviceType))
                            startActivity(intent2)
                            /* } else {
                                ToastUtils.showToast(context, R.string.sync_data)
                            }*/
                        } else if (deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {   //跳绳设置
                            startActivity(Intent(context, RopeDeviceSettingActivity::class.java))
                        } else if (deviceType == JkConfiguration.DeviceType.BODYFAT) {
                            val intent = Intent(context, ActivityDeviceSetting::class.java)
                            intent.putExtra(JkConfiguration.DEVICE, AppConfiguration.deviceMainBeanList.get(deviceType))
                            startActivity(intent)
                        }
                    }
                    R.id.mShadowLayout -> {
                        when (deviceType) {
                            JkConfiguration.DeviceType.ROPE_SKIPPING -> {   //跳绳页面
                                startActivity(Intent(context, RopeSkippingActivity::class.java))
                            }
                            JkConfiguration.DeviceType.BODYFAT -> {
                                startActivity(Intent(context, ScaleMainActivity::class.java))
                            }

                            JkConfiguration.DeviceType.Watch_F18->{
                                var db = AppConfiguration.deviceMainBeanList[deviceType];
                                if (db != null) {
                                    AppConfiguration.braceletID = db.getDeviceID()
                                }
                                startActivity(Intent(context, F18DeviceMainActivity::class.java))
                            }

                            else -> {  //手表手环数据显示主页面
                                startActivity(Intent(context, DeviceMainActivity::class.java))
                            }


                        }
                        //跳转到主页
                    }
                }
            }
        })
        currentPageNumber = 1

        mFragPresenter.getDeviceList(true, false, true, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initSportDetailRec()
        initEvent()
        initView()
        initData()

    }


    //轮播图
    lateinit var adapter: BannerImageAdapter
    fun inibana() {
        adapter = BannerImageAdapter(mAdvice)

        tv_banner?.let {
            it.setIndicator(CircleIndicator(activity))
            it.adapter = adapter
        }
        tv_banner.start()
        tv_banner?.setOnBannerListener { data, position ->
            var bean = data as AdviceBean
           // Log.e("TAG view_banner", "" + bean)
            if (TextUtils.isEmpty(data.jumpUrl)) {
            } else {
                val intent = Intent(context, ActivityWebView::class.java)
                intent.putExtra("title", data.name)
                intent.putExtra("url", data.jumpUrl)
                startActivity(intent)
            }
        }

    }

    fun initImmersionBar() {
        ImmersionBar.with(this).statusBarDarkFont(true)
                .init()
        // TODO("Not yet implemented")
    }

    var deviceSetdeviceFirst: Boolean by Preference(Preference.DEVICE_SETTING_FIRST, false)
    var addDeviceAddDeviceFirst: Boolean by Preference(Preference.ADD_DEVICE_FIRST, false)

    fun showDeviceSetDialog() {

        if (!deviceSetdeviceFirst && AppConfiguration.deviceMainBeanList.size > 0 && addDeviceAddDeviceFirst) {
            if (communiteDeviceSetDialog != null && communiteDeviceSetDialog!!.isShow) {
                return
            }
            communiteDeviceSetDialog = CommuniteDeviceSettingDialog(activity, R.style.AnimSetDevice)
            communiteDeviceSetDialog?.showDialog()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun Event(messageEvent: MessageEvent) {
        val currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516)

        Logger.myLog(tg,"---eventMsg="+messageEvent.msg);
        when (messageEvent.msg) {
            MessageEvent.update_location -> {

            }
            MessageEvent.ADD_DEVICE_FIRST_PRESS -> {
                addDeviceAddDeviceFirst = true
                showDeviceSetDialog()


            }
            MessageEvent.DEVICE_SETTING_FIRST_PRESS -> {
                deviceSetdeviceFirst = true
            }
            MessageEvent.UNBIND_DEVICE_SUCCESS -> {
                mFragPresenter.getDeviceList(false, false, true, false);
            }
            //解绑其他设备后不要重连手表
            MessageEvent.BIND_DEVICE_SUCCESS -> {
                val baseDevice = messageEvent.baseDevice
                //绑定成功直接切换设备
                AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, baseDevice.deviceType)
                Logger.myLog(tg,"TAG 绑定设备成功 == " + baseDevice.deviceType+" "+baseDevice.address)
                when (baseDevice.deviceType) {
                    JkConfiguration.DeviceType.BRAND_W311, JkConfiguration.DeviceType.Brand_W520, JkConfiguration.DeviceType.WATCH_W516, JkConfiguration.DeviceType.SLEEP -> {
                        AppSP.putString(context, AppSP.WATCH_MAC, baseDevice.address)
                        AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.deviceName)
                    }
                    JkConfiguration.DeviceType.BODYFAT -> {
                        //TODO 如果没有绑定直接返回了改怎么显示
                        //绑定体脂秤刷新列表后,由于已经连接过手表，列表李有手表，默认设备可能被设置为手表了，此处应做不设置为默认设备为手表的操作
                        AppSP.putString(context, AppSP.SCALE_MAC, baseDevice.address)
                        AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.address)
                        // deviceConSuccess(baseDevice)
                    }
                    else -> {
                        AppSP.putString(context, AppSP.WATCH_MAC, baseDevice.address)
                        AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, baseDevice.deviceName)
                    }
                }
                mFragPresenter.connectDevice(baseDevice.deviceName, baseDevice.address, baseDevice.deviceType, true, false)
                // connectWatchOrBraceletf
                //绑定成功先取本地的数据再刷服务器的数据

                //不要去连接
                mFragPresenter.getDeviceList(false, false, false, false)
            }

        }
    }

    //连接F18手表
    private fun connF18Device(isConnectByUser: Boolean, deviceType: Int,deviceBean: DeviceBean){
        val f18Mac = DeviceTypeUtil.getW81Mac(deviceBean.deviceID)
        Logger.myLog(tg,"--------获取的Mac地址="+f18Mac)
        autoConnDevice(f18Mac)
    }


    private fun autoConnDevice(f18Mac : String) {
        try {
            //未连接
            if ( !Watch7018Manager.getWatch7018Manager().isConnected) {
                val fs = App.getInstance().f18ConnStatusService
                // fs?.scanningDevice(f18Mac)
                fs?.autoScann(f18Mac)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    @Synchronized
    private fun connectWatchOrBracelet(isConnectByUser: Boolean, deviceType: Int) {
        /*   if (System.currentTimeMillis() - connectTime < 2000) {
            return;
        }*/
        Constants.CAN_RECONNECT = true
        Logger.myLog(tg,"kkk == " + isConnectByUser + "deviceType:" + deviceType)
        val currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516)
        //是连接状态，则不用连接了
        if (AppConfiguration.isConnected && currentDeviceType == deviceType) {
            Logger.myLog(tg,"kkk == 已经是连接状态了，不用连接了")
            return
        }
        if (!AppUtil.isOpenBle()) {
            openBlueDialog()
            return
        }
        var deviceBean: DeviceBean? = null
        /* if (currentDeviceType != deviceType) {

         }*/
        if (AppConfiguration.deviceMainBeanList.containsKey(deviceType)) {
            deviceBean = AppConfiguration.deviceMainBeanList[deviceType]
        }


        if(deviceType == IDeviceType.TYPE_WATCH_7018){
            if (deviceBean != null) {
                connF18Device(isConnectByUser,deviceType,deviceBean)
            }
            return
        }

        Logger.myLog(tg,"---deviceBean="+(deviceBean.toString()))

        if (deviceBean != null) {
            AppSP.putInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, deviceBean.deviceType)
            AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, deviceBean.deviceID)
            if (deviceBean.deviceType == JkConfiguration.DeviceType.BODYFAT) {
                AppSP.putString(context, AppSP.SCALE_MAC, deviceBean.mac)
            } else {
                if (!TextUtils.isEmpty(deviceBean.mac)) {
                    AppSP.putString(context, AppSP.WATCH_MAC, deviceBean.mac)
                }
                if (DeviceTypeUtil.isContainW81(deviceBean.deviceType)) {
                    AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW81Mac(deviceBean.deviceName))
                    Logger.myLog(tg," DeviceTypeUtil.getW81Mac(deviceBean.deviceName):" + DeviceTypeUtil.getW81Mac(deviceBean.deviceName))
                }
            }
            if (AppSP.macMap.containsKey(deviceBean.deviceName)) {
                AppSP.putString(context, AppSP.WATCH_MAC, "" + AppSP.macMap!!.get(deviceBean.deviceName))
            }
        }
        //当前设备不是手表，则设置为手表
        val currentName = AppSP.getString(context, AppSP.DEVICE_CURRENTNAME, "")
        //W81系列的Mac地址就在名字的上面可以取出出来
        if (DeviceTypeUtil.isContainW81(deviceType)) {
            AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW81Mac(currentName))
        }
        Logger.myLog(tg,"kkk == mac为空，重新扫描 deviceType == $deviceType" + "AppSP.getString(context, AppSP.FORM_DFU)=" + AppSP.getString(context, AppSP.FORM_DFU, "false"))

        //重新升级后需要重新扫描连接 所以还是先扫描再连接计较方便
//        if (DeviceTypeUtil.isContainW55X(deviceType) && AppSP.getString(context, AppSP.FORM_DFU, "false") == "false") {
//            val watchMac = DeviceTypeUtil.getW526Mac(currentName, deviceType)
//            if (!TextUtils.isEmpty(watchMac)) {
//                AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW526Mac(currentName, deviceType))
//            }
//        }
        val watchMac = AppSP.getString(context, AppSP.WATCH_MAC, "")
        if (TextUtils.isEmpty(watchMac)) {
            //如果已经在扫描了就不需要再进行扫描了
            if (ISportAgent.getInstance().isDeviceStartScan) {
                Logger.myLog(tg,"设备已经开始扫描了不需要再一次进行扫描")
                return
            }
            Logger.myLog(tg,"kkk == mac为空，重新扫描 currentName == $currentName")
            requestPermission()
        } else {
            // TODO: 2019/1/12 需要和ios平台适配
            if (watchMac.contains(":")) {
                Logger.myLog(tg,"kkk == mac不为空，直连$watchMac currentName == $currentName")
                mFragPresenter.connectDevice(currentName, watchMac, deviceType, true, isConnectByUser)
                //setConnectTimeOut()
            } else {
                Logger.myLog(tg,"kkk == mac不为空，但不是真实mac，扫描$watchMac currentName == $currentName")
                startScan(false, isConnectByUser)
            }
        }
    }


    fun requestPermission() {
//        PermissionUtil.checkPermission(
//                activity!!,
//                arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.CAMERA
//                ),
//                permissonCallback = object : PermissionUtil.OnPermissonCallback {
//                    override fun isGrant(grant: Boolean) {
//                        if (grant) {
//                            //  toast("success")
//                            startScan(false, true)
//                        } else {
//                            // toast("failed")
//                        }
//                    }
//                })
    }

    fun openBlueDialog() {
        PublicAlertDialog.getInstance().showDialog("", activity?.getString(R.string.bonlala_open_blue), context, resources.getString(R.string.app_bluetoothadapter_turnoff), resources.getString(R.string.app_bluetoothadapter_turnon), object : AlertDialogStateCallBack {
            override fun determine() {
//                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivity(intent);
                BluetoothAdapter.getDefaultAdapter()?.enable()
            }

            override fun cancel() {}
        }, false)
    }

    override fun onScanFinish() {
        TODO("Not yet implemented")
    }

    fun setHeadPic(deviceType: Int) {

    }


    fun updateItem(list: ArrayList<MainDeviceBean>?) {

        if (list != null && list.size > 0) {
            var baseDevice = ISportAgent.getInstance().currnetDevice;
            Logger.myLog(tg,"updateItem------baseDevice=" + baseDevice+"isConn="+AppConfiguration.isConnected)
            if (baseDevice != null) {
//                var deviceBean = DeviceBean(baseDevice.deviceType,baseDevice.getDeviceName())
//                AppConfiguration.braceletID = baseDevice.deviceName
//                AppConfiguration.deviceMainBeanList.put(baseDevice.deviceType,deviceBean)
                var bean = list.findLast {
                    it.deviceType == baseDevice.deviceType
                }
                if (bean != null) {
                    bean.isConn = AppConfiguration.isConnected
                    bean.battery = getVersionOrBattery(baseDevice.deviceType, baseDevice.deviceName)
                }
            }
            mDataList.clear()
            updateScale(list)
            mDataList.addAll(list);
            mMessageAdapter.setList(mDataList)
        } else {
            mDataList.clear()
            mMessageAdapter.notifyDataSetChanged()
            mMessageAdapter.setEmptyView(getEmptempView()!!)
        }
        Logger.myLog(tg,"successGetDeviceListFormDB---" + mDataList?.size)

        EventBus.getDefault().post(MessageEvent(MessageEvent.GET_BIND_DEVICELIST_SUCCESS))
    }


    override fun successGetDeviceListFormHttp(deviceBeanHashMap: HashMap<Int, DeviceBean>?, list: ArrayList<MainDeviceBean>?, show: Boolean, reConnect: Boolean, isNeedConn: Boolean) {

        Logger.myLog(tg,"----successGetDeviceListFormHttp="+deviceBeanHashMap.toString()+" "+Gson().toJson(list))

        AppConfiguration.deviceMainBeanList = deviceBeanHashMap

        Logger.myLog(tg,"----------httttpp="+Gson().toJson(AppConfiguration.deviceMainBeanList))

//        AppConfiguration.deviceBeanList = HashMap<Int, DeviceBean>()
        if (AppConfiguration.deviceMainBeanList != null) {

            for (deviceType in AppConfiguration.deviceMainBeanList.keys) {
                val deviceBean = AppConfiguration.deviceMainBeanList[deviceType]
                if (deviceBean!!.currentType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                    continue
                }
                AppConfiguration.deviceBeanList[deviceType] = deviceBean
                AppConfiguration.deviceMainBeanList[deviceType] = deviceBean
            }
        }
        Logger.myLog(tg,"getDeviceList" + AppConfiguration.deviceMainBeanList.toString() +" 22="+AppConfiguration.deviceBeanList.toString())
        mDataList.clear()
        updateItem(list)
        if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BODYFAT)) {
            mFragPresenter.getMainScaleDataFromHttp(true)
        }
        val currentDeviceType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516)
        //是连接状态，则不用连接了
        /*if (!AppConfiguration.isConnected) {
            connectWatchOrBracelet(false, currentDeviceType)
            Logger.myLog("kkk == 已经是连接状态了，不用连接了")
            return
        }*/
        showDeviceSetDialog()

        if (isNeedConn) {
            connectDevice()
        }
    }

    override fun <T : Any?> bindAutoDispose(): AutoDisposeConverter<T> {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY))
    }

    override fun getAdviceList(adviceBeans: MutableList<AdviceBean>?) {

        Logger.myLog(tg,"getAdviceList=" + adviceBeans)


        mAdvice.clear()
        adviceBeans?.forEach {
            Logger.myLog(tg,"getAdviceList1=" + it.imageUrl)
            mAdvice.add(it)
        }

        var startUrl = mAdvice.get(0)

        /* mAdvice.removeAt(0)
         mAdvice.add(mAdvice.size - 1, startUrl)
 */
        inibana()
        // adapter.notifyDataSetChanged()
    }

    override fun successGetDeviceListFormDB(deviceBeanHashMap: HashMap<Int, DeviceBean>?, list: ArrayList<MainDeviceBean>?, show: Boolean, reConnect: Boolean, isNeedConn: Boolean) {

        Logger.myLog("successGetDeviceListFormDB---=" + list?.size+" map="+Gson().toJson(deviceBeanHashMap)+" list="+Gson().toJson(list))

        AppConfiguration.deviceMainBeanList = deviceBeanHashMap
       // AppConfiguration.deviceBeanList = HashMap<Int, DeviceBean>()

        if (AppConfiguration.deviceMainBeanList != null) {
            for (deviceType in AppConfiguration.deviceMainBeanList.keys) {
                val deviceBean = AppConfiguration.deviceMainBeanList[deviceType]
                if (deviceBean!!.currentType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                    continue
                }
                if (deviceBean!!.currentType == JkConfiguration.DeviceType.BODYFAT) {
                    continue
                }
                AppConfiguration.deviceBeanList.put(deviceType, deviceBean)
            }
        }
        mDataList.clear()
        if (DeviceTypeUtil.isContainWatch() || DeviceTypeUtil.isContainBrand() || DeviceTypeUtil.isContainRope()) {
            connectDevice()
            mFragPresenter.getDeviceList(false, false, true, false)
        } else {
            mFragPresenter.getDeviceList(false, false, true, true)
        }

        //
        updateItem(list)

    }

    fun connectDevice() {
        if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.WATCH_W516)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.WATCH_W516)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W311)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.BRAND_W311)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W520)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Brand_W520)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.BRAND_W307J)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.BRAND_W307J)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W813)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W813)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W819)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W819)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W910)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W910)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W556)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W556)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W557)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W557)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812B)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W812B)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560B)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W560B)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W560)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W560)
        }
        else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W812)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W812)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_W817)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_W817)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Brand_W814)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Brand_W814)
        } else if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.ROPE_SKIPPING)) {
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.ROPE_SKIPPING)
        }

        //F18
        else if(AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.Watch_F18)){
            connectWatchOrBracelet(false, JkConfiguration.DeviceType.Watch_F18)
        }
    }

    var lastScacleBean: ScacleBean? = null

    override fun successGetMainScaleDataFromDB(scacleBeans: ArrayList<ScacleBean>?, scale_fourElectrode_dataModel: Scale_FourElectrode_DataModel?, show: Boolean) {

        if (scacleBeans != null && scacleBeans.size > 0) {
            lastScacleBean = scacleBeans!!.get(scacleBeans.size - 1)
            Logger.myLog("successGetMainScaleDataFromDB" + "successGetMainScaleDataFromDB")
            updateScale()
            mMessageAdapter.notifyDataSetChanged()
        }


        //  TODO("Not yet implemented")

        /*val mutableIterator = mDataList.iterator()

        mutableIterator.next()
        mutableIterator.remove()*/
        // println("After removal: $mDataList")
    }

    override fun onRespondError(message: String?) {
    }


    fun updateScale(list: ArrayList<MainDeviceBean>?) {
        var bean = list!!.findLast {
            it.deviceType == JkConfiguration.DeviceType.BODYFAT
        }
        Logger.myLog("successGetMainScaleDataFromDB" + bean)
        if (bean != null) {
            if (lastScacleBean != null) {
                bean.scaleWeight = lastScacleBean!!.weight
                bean.scaleTime = lastScacleBean!!.strDate
            }

        }
    }

    fun updateScale() {
        var bean = mDataList!!.findLast {
            it.deviceType == JkConfiguration.DeviceType.BODYFAT
        }
        Logger.myLog("successGetMainScaleDataFromDB" + bean)
        if (bean != null) {
            if (lastScacleBean != null) {
                bean.scaleWeight = lastScacleBean!!.weight
                bean.scaleTime = lastScacleBean!!.strDate
            }

        }
    }

    override fun onScan(listDevicesMap: MutableMap<String, BaseDevice>?) {
        val currentName = AppSP.getString(context, AppSP.DEVICE_CURRENTNAME, "")
        if (listDevicesMap != null && listDevicesMap.size > 0) {
            val currentType = AppSP.getInt(context, AppSP.DEVICE_CURRENTDEVICETYPE, JkConfiguration.DeviceType.WATCH_W516)
            Logger.myLog("onScan" + currentType + "currentName:" + currentName + "currentType" + currentType)
            when (currentType) {
                JkConfiguration.DeviceType.BODYFAT -> {
                }
                JkConfiguration.DeviceType.WATCH_W516, JkConfiguration.DeviceType.BRAND_W311, JkConfiguration.DeviceType.Brand_W520, JkConfiguration.DeviceType.Brand_W814, JkConfiguration.DeviceType.Watch_W812, JkConfiguration.DeviceType.Watch_W817, JkConfiguration.DeviceType.Watch_W813, JkConfiguration.DeviceType.Watch_W819, JkConfiguration.DeviceType.Watch_W910, JkConfiguration.DeviceType.Watch_W556, JkConfiguration.DeviceType.Watch_W557 -> if (listDevicesMap.containsKey(currentName)) {
                    val baseDevice = listDevicesMap[currentName]
                    mFragPresenter.cancelScan()
                    AppSP.putString(context, AppSP.WATCH_MAC, baseDevice!!.getAddress())
                    Logger.myLog(tg + currentType + "currentName:" + currentName + "currentType" + currentType + "-------" + baseDevice)
                    connectWatchOrBracelet(false, baseDevice.deviceType)
                }
                else -> if (listDevicesMap.containsKey(currentName)) {
                    val baseDevice = listDevicesMap[currentName]
                    mFragPresenter.cancelScan()
                    AppSP.putString(context, AppSP.WATCH_MAC, baseDevice!!.getAddress())
                    Logger.myLog("onScan" + currentType + "currentName:" + currentName + "currentType" + currentType + "-------" + baseDevice)
                    connectWatchOrBracelet(true, baseDevice!!.deviceType)
                }
            }
        }
    }

    override fun onScan(key: String?, baseDevice: BaseDevice?) {
        // TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        ISportAgent.getInstance().unregisterListener(mBleReciveListener)
        BatteryChangeObservable.getInstance().deleteObserver(this)
        RopeSyncDataObservable.getInstance().deleteObserver(this)
        // AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, deviceBeanScale.deviceID);
        EventBus.getDefault().unregister(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        // TODO("Not yet implemented")
        if (o is BatteryChangeObservable) {
            var batteryChangeBean = arg as BatteryChangeBean
            var bean = mDataList.findLast {
                it.deviceType == batteryChangeBean.deviceType
            }
            if (bean != null) {
                if (batteryChangeBean.battery == bean!!.battery) {
                    return
                }
                if (batteryChangeBean.battery < 5) {
                    BatteryLowObservable.getInstance().show()
                }
                bean!!.battery = batteryChangeBean.battery;
                AppConfiguration.currentConnectDevice.battery = bean.battery
                //  mMessageAdapter.notifyDataSetChanged()
            }

        } else if (o is RopeSyncDataObservable) {
            handler.post {
                if (AppConfiguration.isConnected) {
                    // S002_DetailDataModelAction.deletAll()
                    //同步完成后去清除历史数据
                    ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_TEST_RESET);
                } else {
                    //  ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
                }
                sysncDevcieDataToServer(JkConfiguration.DeviceType.ROPE_SKIPPING)
            }

        }
    }


    private var w81DataPresenter: W81DataPresenter? = null
    fun sysncDevcieDataToServer(deviceType: Int) {
        w81DataPresenter = W81DataPresenter(this)
        if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
            val deviceBean = AppConfiguration.deviceMainBeanList[deviceType]
        } else if (DeviceTypeUtil.isContainW81(deviceType)) {
            //上传W81的数据
            val deviceBean = AppConfiguration.deviceMainBeanList[deviceType]
        } else if (DeviceTypeUtil.isContainWatch(deviceType)) {
            val deviceBean = AppConfiguration.deviceMainBeanList[deviceType]
        } else if (deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            if (AppConfiguration.deviceMainBeanList.containsKey(deviceType)) {
                val deviceBean = AppConfiguration.deviceMainBeanList[deviceType]
                w81DataPresenter!!.getupdateRope(deviceBean!!.deviceName, TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))
            }
            // S002DeviceDataRepository.requstUpgradeRopeData(AppConfiguration.braceletID, TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance))
        } else {

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
            mFragPresenter.scan(currentType, isScale)
        } else {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, FragmentNewData.REQCODE_OPEN_BT)
        }
    }

    private val scanHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Logger.myLog("hander 0x05" + msg.what)
            when (msg.what) {
                0x05 -> {
                    Logger.myLog("hander 0x05 deviceConFail")
                    //defaultConnectState(true);
                    // deviceConFail()
                }
            }
        }
    }

    /**
     * 连接超时监听30秒
     */
    private fun setScanTimeOut() {
        if (scanHandler.hasMessages(0x05)) {
            scanHandler.removeMessages(0x05)
        }
        //scanHandler.sendEmptyMessage(0x05);
        scanHandler.sendEmptyMessageDelayed(0x05, 20000)
        Logger.myLog("setScanTimeOut")
    }


    var handler: Handler = Handler()


    fun showConnDialog() {
        PublicAlertDialog.getInstance().showDialog("", UIUtils.getString(R.string.main_device_no_conn_title), context, resources.getString(R.string.cancel), resources.getString(R.string.connect), object : AlertDialogStateCallBack {
            override fun determine() {
                //需要去重新设置当前的设备和设备类型
                ISportAgent.getInstance().disConDevice(false)
                handler.postDelayed({
                    connectWatchOrBracelet(true, mCurrentMessage!!.deviceType)
                }, 2000);

            }

            override fun cancel() {}
        }, false)
    }


    fun getEmptempView(): View? {
        val notDataView = layoutInflater.inflate(R.layout.tempty_main_empty, recyclerview_device_list, false)
        val iv_empty_view = notDataView.findViewById<ImageView>(R.id.iv_empty_view)
        LoadImageUtil.getInstance().loadGif(activity, R.drawable.device_main_emty, iv_empty_view)
        notDataView.setOnClickListener { }
        return notDataView
    }

    private fun getVersionOrBattery(deviceType: Int, deviceName: String): Int {
        //是W516的设备信息 w81的版本信息存在一起
        if (DeviceTypeUtil.isContainF18(deviceName) || DeviceTypeUtil.isContainWatch(deviceType)
                || DeviceTypeUtil.isContaintW81(deviceType) || deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING
                || deviceType == IDeviceType.TYPE_WATCH_7018) {
            val deviceInfoByDeviceId = DeviceInformationTableAction.findDeviceInfoByDeviceId(deviceName)
            if (deviceInfoByDeviceId != null) {
                Logger.myLog(tg,"---111-getVersionOrBattery:$deviceInfoByDeviceId")
                return deviceInfoByDeviceId.battery
                //getDeviceBattery(deviceType, deviceInfoByDeviceId.getBattery());
            }
        } else if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
            val model = Bracelet_W311_DeviceInfoModelAction.findBraceletW311DeviceInfo(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceName)
            Logger.myLog(tg,"---222-getVersionOrBattery:$model")
            if (model != null) {
                return model.powerLevel
            }
        }
        return 0
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        //TODO("Not yet implemented")
        when (event!!.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                //将点下的点的坐标保存
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                //计算出需要移动的距离
                val dx = event.rawX.toInt() - lastX
                val dy = event.rawY.toInt() - lastY
                //将移动距离加上，现在本身距离边框的位置
                val left = view!!.left + dx
                val top = view!!.top + dy
                //获取到layoutParams然后改变属性，在设置回去
                val layoutParams: FrameLayout.LayoutParams = view!!
                        .getLayoutParams() as FrameLayout.LayoutParams
                layoutParams.height = IMAGE_SIZE
                layoutParams.width = IMAGE_SIZE
                layoutParams.leftMargin = left
                layoutParams.topMargin = top
                view!!.layoutParams = layoutParams
                //记录最后一次移动的位置
                lastX = event.rawX.toInt()
                lastY = event.rawY.toInt()
            }
        }
        //刷新界面
        //刷新界面
        layout_top.invalidate()
        return true
    }


}
