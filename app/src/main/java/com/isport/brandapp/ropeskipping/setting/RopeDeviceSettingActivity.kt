package com.isport.brandapp.ropeskipping.setting

import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.View
import bike.gymproject.viewlibray.ItemDeviceSettingView
import brandapp.isport.com.basicres.BaseApp
import brandapp.isport.com.basicres.BaseTitleActivity
import brandapp.isport.com.basicres.commonutil.*
import brandapp.isport.com.basicres.commonview.TitleBarView
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil
import com.isport.blelibrary.ISportAgent
import com.isport.blelibrary.db.action.DeviceInformationTableAction
import com.isport.blelibrary.db.action.bracelet_w311.Bracelet_W311_DeviceInfoModelAction
import com.isport.blelibrary.db.table.DeviceInformationTable
import com.isport.blelibrary.deviceEntry.impl.BaseDevice
import com.isport.blelibrary.interfaces.BleReciveListener
import com.isport.blelibrary.observe.RopeNoDataObservable
import com.isport.blelibrary.observe.RopeSyncDataObservable
import com.isport.blelibrary.observe.SyncProgressObservable
import com.isport.blelibrary.result.IResult
import com.isport.blelibrary.utils.BleRequest
import com.isport.blelibrary.utils.Logger
import com.isport.brandapp.AppConfiguration
import com.isport.brandapp.R
import com.isport.brandapp.bean.DeviceBean
import com.isport.brandapp.bind.presenter.BindPresenter
import com.isport.brandapp.bind.view.BindBaseView
import com.isport.brandapp.device.bracelet.playW311.PlayW311Activity
import com.isport.brandapp.device.publicpage.GoActivityUtil
import com.isport.brandapp.dialog.UnBindDeviceDialog
import com.isport.brandapp.dialog.UnbindStateCallBack
import com.isport.brandapp.ropeskipping.realsport.dialog.SelectPopupWindow
import com.isport.brandapp.ropeskipping.util.Preference
import com.isport.brandapp.sport.bean.SportSettingBean
import com.isport.brandapp.upgrade.bean.DeviceUpgradeBean
import com.isport.brandapp.upgrade.present.DevcieUpgradePresent
import com.isport.brandapp.upgrade.view.DeviceUpgradeView
import com.isport.brandapp.util.ActivitySwitcher
import com.isport.brandapp.util.DeviceTypeUtil
import com.isport.brandapp.wu.util.HeartRateConvertUtils
import kotlinx.android.synthetic.main.app_activity_rope_device_setting.*
import org.greenrobot.eventbus.EventBus
import phone.gym.jkcq.com.commonres.common.JkConfiguration
import phone.gym.jkcq.com.commonres.commonutil.UserUtils
import java.util.*

/**
 * 跳绳设置
 */
internal class RopeDeviceSettingActivity() : BaseTitleActivity(), DeviceUpgradeView, BindBaseView {


    val mUnbind: BindPresenter by lazy { BindPresenter(this@RopeDeviceSettingActivity) }

    private var age = 0
    private var sex: String? = null
    var mSelectPopupWindow: SelectPopupWindow? = null
    var sportDetail = SportSettingBean()
    var ropeHrvalue: Int by Preference(Preference.ROPE_Hr_Count, 0)


    override fun initData() {
        try {
            devcieUpgradePresent = DevcieUpgradePresent(this)
            if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.ROPE_SKIPPING)) {
                val bean: DeviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.ROPE_SKIPPING)
                        ?: return
                if(bean.deviceName == null)
                    return
                deviceInfoByDeviceId = DeviceInformationTableAction.findDeviceInfoByDeviceId(bean!!.deviceName)
                if (deviceInfoByDeviceId != null) {
                    iv_watch_stable_version.setContentText("V" + deviceInfoByDeviceId.version)
                    devcieUpgradePresent!!.getDeviceUpgradeInfo(JkConfiguration.DeviceType.ROPE_SKIPPING)
                }
            }

            val deviceBean = AppConfiguration.deviceMainBeanList.get(JkConfiguration.DeviceType.ROPE_SKIPPING)
            if (deviceBean != null) {
                var battery = getVersionOrBattery(deviceBean!!.deviceType, deviceBean.deviceName)
                iv_battery.setProgress(battery)
                tv_battery.text = "" + battery + "%"
            }
            setConnectState()
            val userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()))
                    ?: return

            if (userInfoBean != null) {
                age = UserUtils.getAge(userInfoBean.birthday)
                sex = userInfoBean.gender
                var maxHr = HeartRateConvertUtils.getMaxHeartRate(age, sex)

                if (ropeHrvalue == 0) {
                    ropeHrvalue = (0.75 * maxHr).toInt()
                }
                sportDetail.currentHrValue = ropeHrvalue
                sportDetail.hrMaxValue = 250
                sportDetail.hrMinValue = (maxHr * 0.70f).toInt()
                item_hr.setTitleText("" + sportDetail.currentHrValue + UIUtils.getString(R.string.bmp_unit))
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    private val mBleReciveListener: BleReciveListener = object : BleReciveListener {
        override fun onConnResult(isConn: Boolean, isConnectByUser: Boolean, baseDevice: BaseDevice) {


            //这里值显示是跳绳的设备的连接状态

            if (!isConn) {
                var device = ISportAgent.getInstance().currnetDevice
                if (device != null && device.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                    tv_device_con_state.text = UIUtils.getString(R.string.connect_fail)
                }
            } else {
                setConnectState()
            }


        }

        override fun setDataSuccess(s: String) {

        }


        override fun receiveData(mResult: IResult) {

        }

        override fun onConnecting(baseDevice: BaseDevice) {}
        override fun onBattreyOrVersion(baseDevice: BaseDevice) {
        }
    }

    fun setConnectState() {
        if (AppConfiguration.currentConnectDevice != null) {
            if (AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING && AppConfiguration.isConnected) {
                tv_device_con_state.text = UIUtils.getString(R.string.connected)
            } else {
                tv_device_con_state.text = UIUtils.getString(R.string.connect_fail)
            }
        } else {
            tv_device_con_state.text = UIUtils.getString(R.string.connect_fail)
        }
    }

    private fun getVersionOrBattery(deviceType: Int, deviceName: String): Int {
        //是W516的设备信息 w81的版本信息存在一起
        if (DeviceTypeUtil.isContainWatch(deviceType) || DeviceTypeUtil.isContaintW81(deviceType) || deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            val deviceInfoByDeviceId = DeviceInformationTableAction.findDeviceInfoByDeviceId(deviceName)
            if (deviceInfoByDeviceId != null) {
                Logger.myLog("getVersionOrBattery:$deviceInfoByDeviceId")
                return deviceInfoByDeviceId.battery
                //getDeviceBattery(deviceType, deviceInfoByDeviceId.getBattery());
            }
        } else if (DeviceTypeUtil.isContainWrishBrand(deviceType)) {
            val model = Bracelet_W311_DeviceInfoModelAction.findBraceletW311DeviceInfo(TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()), deviceName)
            Logger.myLog("getVersionOrBattery:$model")
            if (model != null) {
                return model.powerLevel
            }
        }
        return 0
    }

    private var devcieUpgradePresent: DevcieUpgradePresent? = null
    /* var mDataList = mutableListOf<MessageInfo>()
     lateinit var mMessageAdapter: MessageAdapter
     var mCurrentMessage: MessageInfo? = null
     var mCurrentPosition = 0
     var currentPageNumber = 1*/


    override fun getLayoutId(): Int = R.layout.app_activity_rope_device_setting

    override fun initEvent() {
        ISportAgent.getInstance().registerListener(mBleReciveListener)
        RopeSyncDataObservable.getInstance().addObserver(this)
        RopeNoDataObservable.getInstance().addObserver(this)
        iv_braclet_play.setOnContentClickListener(ItemDeviceSettingView.OnContentClickListener {
            val guideintent = Intent(context, PlayW311Activity::class.java)
            guideintent.putExtra(JkConfiguration.DEVICE, JkConfiguration.DeviceType.ROPE_SKIPPING)
            startActivity(guideintent)
        })
        iv_watch_stable_version.setOnContentClickListener(ItemDeviceSettingView.OnContentClickListener {
            //跳转到升级页面
            // if (deviceBean != null) {
            var base = ISportAgent.getInstance().currnetDevice
            if (base != null) {
                ActivitySwitcher.goDFUAct(this@RopeDeviceSettingActivity, JkConfiguration.DeviceType.ROPE_SKIPPING, base.deviceName, base.address, true)
            } else {
                //  ActivitySwitcher.goDFUAct(this@RopeDeviceSettingActivity, JkConfiguration.DeviceType.ROPE_SKIPPING, base!!.deviceName, base!!.address, true)
            }
            // }
        })
        item_hr.setOnContentClickListener(ItemDeviceSettingView.OnContentClickListener {
            if (!ViewMultiClickUtil.onMultiClick()) {
                mSelectPopupWindow?.popWindowSelect(
                        this@RopeDeviceSettingActivity,
                        item_hr,
                        SelectPopupWindow.ROPE_HR_VALUE,
                        sportDetail.currentHrValue.toString(), sportDetail.hrMinValue, sportDetail.hrMaxValue, 1, false
                )
            }
        })
    }

    lateinit var deviceInfoByDeviceId: DeviceInformationTable

    override fun initView(view: View?) {
        mSelectPopupWindow = SelectPopupWindow(object : SelectPopupWindow.OnSelectPopupListener {
            override fun onSelect(type: String, data: String) {
                when (type) {
                    SelectPopupWindow.ROPE_HR_VALUE -> {
                        Logger.myLog("ROPE_COUNT_RE=" + data)
                        sportDetail.currentHrValue = data.toInt()
                        ropeHrvalue = sportDetail.currentHrValue
                        item_hr.setTitleText(data + UIUtils.getString(R.string.bmp_unit))
                        ISportAgent.getInstance().requestBle(BleRequest.rope_set_maxhr, sportDetail.currentHrValue)
                        //setTimeValue()
                        //ropeCount = data.toInt()

                        // itemview_birthday.setRightText(data)
                    }

                }
            }

        })


    }


    override fun initHeader() {
        titleBarView.setTitle(getString(R.string.rope_device))
        titleBarView.setRightIcon(R.drawable.icon_rope_unbind)
        titleBarView.setOnTitleBarClickListener(object : TitleBarView.OnTitleBarClickListener() {
            override fun onRightClicked(view: View?) {
                //解绑设备
                showUnbindDialog()
            }

            override fun onLeftClicked(view: View?) {
                finish()
            }
        })
    }

    var handler: Handler = Handler()

    override fun update(o: Observable?, arg: Any?) {
        super.update(o, arg)
        Logger.myLog("noDataUpdate1")
        if (o is RopeSyncDataObservable) {
            SyncProgressObservable.getInstance().hide()
            mDeviceBean?.let { unBindDevice(it, false) }
        } else if (o is RopeNoDataObservable) {
            Logger.myLog("noDataUpdate2")
            SyncProgressObservable.getInstance().hide()
            mDeviceBean?.let { unBindDevice(it, false) }
        }

    }

    override fun successDeviceUpgradeInfo(deviceUpgradeBean: DeviceUpgradeBean?) {
        //   TODO("Not yet implemented")
        if (deviceUpgradeBean != null && deviceInfoByDeviceId != null) {
            var currentVersion = if (TextUtils.isEmpty(deviceInfoByDeviceId.getVersion())) "" else deviceInfoByDeviceId.getVersion()
            val serVersion = if (TextUtils.isEmpty(deviceUpgradeBean.appVersionName)) "" else deviceUpgradeBean.appVersionName
            if (currentVersion.contains("V")) {
                currentVersion = currentVersion.replace("V", "")
            }
            if (currentVersion.contains("v")) {
                currentVersion = currentVersion.replace("v", "")
            }
            val strFirmwareVersion = String.format(resources.getString(R.string.app_device_version),
                    currentVersion)
            if (currentVersion == serVersion) {
                iv_watch_stable_version.setContentText(strFirmwareVersion + UIUtils.getString(R.string.no_update))
            } else {
                iv_watch_stable_version.setContentText(strFirmwareVersion + UIUtils.getString(R.string.has_update))
            }
        }
    }

    override fun updateFail() {

        // TODO("Not yet implemented")
    }

    override fun updateWatchHistoryDataSuccess(deviceBean: DeviceBean?) {
        // TODO("Not yet implemented")
    }

    override fun updateSleepDataSuccess(deviceBean: DeviceBean?) {
        // TODO("Not yet implemented")
    }

    override fun onUnBindSuccess() {

        //解绑的是当前连接的设备,需要断连设备
        EventBus.getDefault().post(MessageEvent(MessageEvent.UNBIND_DEVICE_SUCCESS))
        Logger.myLog("解绑成功")
        val currnetDevice = ISportAgent.getInstance().currnetDevice
        if (currnetDevice != null && currnetDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            ISportAgent.getInstance().unbind(false)
        }
        GoActivityUtil.goActivityUnbindDevice(JkConfiguration.DeviceType.ROPE_SKIPPING, this@RopeDeviceSettingActivity)
        finish()
    }

    override fun updateWatchDataSuccess(deviceBean: DeviceBean?) {
    }

    override fun onRespondError(message: String?) {
    }


    var mDeviceBean: DeviceBean? = null
    fun showUnbindDialog() {
        //w516 w311 w520只会存在一个

        if (AppConfiguration.deviceMainBeanList.containsKey(JkConfiguration.DeviceType.ROPE_SKIPPING)) {
            mDeviceBean = AppConfiguration.deviceMainBeanList[JkConfiguration.DeviceType.ROPE_SKIPPING]!!
        }
        if (mDeviceBean == null) {
            return
        }
        UnBindDeviceDialog(this, JkConfiguration.DeviceType.ROPE_SKIPPING, true, object : UnbindStateCallBack {
            override fun synUnbind() {
                if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected))
                    return
                }
                if (AppConfiguration.isConnected) {
                    val device = ISportAgent.getInstance().currnetDevice
                    var deviceType = 0
                    if (device != null) {
                        deviceType = device.deviceType
                    }
                    if (deviceType != JkConfiguration.DeviceType.ROPE_SKIPPING) {
                        ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
                        return
                    }

                    ISportAgent.getInstance().requestBle(BleRequest.bracelet_sync_data)

                } else {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
                }
            }

            override fun dirctUnbind() {
                if (!NetUtils.hasNetwork(BaseApp.getApp())) {
                    ToastUtils.showToast(context, UIUtils.getString(R.string.common_please_check_that_your_network_is_connected))
                    return
                }
                unBindDevice(mDeviceBean!!, true)
            }

            override fun cancel() {}
        }, JkConfiguration.DeviceType.SLEEP)
    }

    var currentType = 0
    private fun unBindDevice(deviceBean: DeviceBean, isDe: Boolean) {
        if (mDeviceBean == null) {
            return
        }
        handler.post {
            currentType = deviceBean.deviceType
            Logger.myLog("点击去解绑 == $currentType")
            mUnbind.unBind(deviceBean, isDe)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        RopeSyncDataObservable.getInstance().deleteObserver(this)
        RopeNoDataObservable.getInstance().deleteObserver(this)
        ISportAgent.getInstance().unregisterListener(mBleReciveListener)
    }

}
