package com.isport.brandapp.ropeskipping.realsport

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetFileDescriptor
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import bike.gymproject.viewlibray.AkrobatNumberTextView
import bike.gymproject.viewlibray.CusScheduleView
import bike.gymproject.viewlibray.chart.LineChartEntity
import brandapp.isport.com.basicres.BaseApp
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog
import brandapp.isport.com.basicres.commonbean.UserInfoBean
import brandapp.isport.com.basicres.commonutil.AppUtil
import brandapp.isport.com.basicres.commonutil.ToastUtils
import brandapp.isport.com.basicres.commonutil.TokenUtil
import brandapp.isport.com.basicres.commonutil.UIUtils
import brandapp.isport.com.basicres.commonview.TitleBarView.OnTitleBarClickListener
import brandapp.isport.com.basicres.mvp.BaseMVPTitleActivity
import brandapp.isport.com.basicres.net.userNet.CommonUserAcacheUtil
import com.google.gson.Gson
import com.isport.blelibrary.ISportAgent
import com.isport.blelibrary.deviceEntry.impl.BaseDevice
import com.isport.blelibrary.entry.RopeRealDataBean
import com.isport.blelibrary.entry.RopeTargetDataBean
import com.isport.blelibrary.interfaces.BleReciveListener
import com.isport.blelibrary.managers.BaseManager
import com.isport.blelibrary.observe.GetRopeTargDataObservable
import com.isport.blelibrary.observe.RopeRealDataObservable
import com.isport.blelibrary.observe.RopeStartOrEndSuccessObservable
import com.isport.blelibrary.result.IResult
import com.isport.blelibrary.utils.*
import com.isport.brandapp.AppConfiguration
import com.isport.brandapp.R
import com.isport.brandapp.device.share.NewShareActivity
import com.isport.brandapp.device.share.ShareBean
import com.isport.brandapp.dialog.RopeCompletyDialog
import com.isport.brandapp.dialog.RopePkCompletyDialog
import com.isport.brandapp.home.presenter.DeviceConnPresenter
import com.isport.brandapp.login.model.Challeng
import com.isport.brandapp.ropeskipping.realsport.bean.RopeSportTypeBean
import com.isport.brandapp.ropeskipping.realsport.dialog.SelectPopupWindow
import com.isport.brandapp.ropeskipping.setting.RopeAppSettingActivity
import com.isport.brandapp.ropeskipping.speakutil.SpeakUtil
import com.isport.brandapp.ropeskipping.util.Preference
import com.isport.brandapp.util.AppSP
import com.isport.brandapp.util.DateTimeUtils
import com.isport.brandapp.util.DeviceTypeUtil
import com.isport.brandapp.util.UserAcacheUtil
import com.isport.brandapp.view.CountDownDialogView
import com.isport.brandapp.wu.util.HeartRateConvertUtils
import io.reactivex.disposables.Disposable
//test_rope
//import kotlinx.android.synthetic.main.activity_real_rope_skpping.*
import kotlinx.android.synthetic.main.activity_real_rope_skpping.*
import kotlinx.android.synthetic.main.app_fragment_rope_day.*
import kotlinx.android.synthetic.main.app_fragment_rope_month.*
import phone.gym.jkcq.com.commonres.common.JkConfiguration
import phone.gym.jkcq.com.commonres.commonutil.CommonDateUtil
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils
import phone.gym.jkcq.com.commonres.commonutil.UserUtils
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

//????????????
internal class RealRopeSkippingActivity() : BaseMVPTitleActivity<RealRopeSkippingView, RealRopeSkippingPresenter>(), RealRopeSkippingView {

    val tgs = "RealRopeSkippingActivity"

    var isSettingSuccess = false;

    var isCanStart = false;
    //????????????


    var mSelectPopupWindow: SelectPopupWindow? = null
    var mDeviceConnPresenter: DeviceConnPresenter? = null

    //???????????????bean
    var bean: Challeng? = null

    var isClick = false;
    var isUP = true;
    var isClickStart = false;
    var isviewClick = true;
    var isLockComple = false;
    var mCurrentProgress = 0;
    var sportBean: RopeSportTypeBean? = null
    var animator: ObjectAnimator? = null
    var currentHearHr = 0
    var currentTime = ""
    var currentRopeCount = ""
    var currentToalCal = ""
    var currentRopeRes = 1
    var currentRopeName = ""
    var targetBean: RopeTargetDataBean? = null

    //??????
    var challengeRank = "--"

    //??????????????????
    var layout_bottom_value  : LinearLayout ?=null
    var layout_bottom : LinearLayout ?= null

    //???????????????
    private var ropeChallengeLayout : LinearLayout ?=null
    //?????????????????????
    var ropeChallengeKcalTv : AkrobatNumberTextView?= null
    //????????????
    var ropeChallengeHeartTv : AkrobatNumberTextView?= null
    //??????????????????
    var ropeChallengeNumTv : AkrobatNumberTextView?= null
    //????????????
    var ropeChallengeCusScheduleView : CusScheduleView?= null



    override fun getLayoutId(): Int = R.layout.activity_real_rope_skpping

    override fun initHeader() {
        // TODO("Not yet implemented")
        /*ImmersionBar.with(this).statusBarDarkFont(true)
                .init()*/


    }

    var tag = ""
    var conn = "conn"
    var disCon = "disCon"



    @SuppressLint("ClickableViewAccessibility")
    override fun initView(view: View?) {

        initViews()

        // tvToValue = view!!.findViewById(R.id.tv_top_value)
        titleBarView?.setOnTitleBarClickListener(object : OnTitleBarClickListener() {
            override fun onLeftClicked(view: View) {
                stopPlayMusic()
                finish()
            }

            override fun onRightClicked(view: View) {
                startActivity(Intent(this@RealRopeSkippingActivity, RopeAppSettingActivity::class.java))
            }
        })


        sport_stop?.setOnTouchListener(View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isUP = false
                    mCurrentProgress = 0
                    startStopPlay()
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    isUP = true
                    if (animator != null) {
                        animator!!.cancel()
                    }
                }
                else -> {
                }
            }
            false
        })

    }



    var currentType = 0
    lateinit var userInfoBean: UserInfoBean
    private var age = 0
    private var sex: String? = null

    override fun initData() {


        userInfoBean = CommonUserAcacheUtil.getUserInfo(TokenUtil.getInstance().getPeopleIdStr(BaseApp.getApp()))
        if (userInfoBean != null) {
            val birthday = userInfoBean.birthday
            age = UserUtils.getAge(birthday)
            sex = userInfoBean.gender
        }
        register()
        isSettingSuccess = false
        initSpeech()
        initDataPlayer()
        //  playFromRawFile(this)
        /*if (AppConfiguration.isConnected && AppConfiguration.currentConnectDevice != null && AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            tv_connect_state.setText(UIUtils.getString(R.string.connect))
        } else {
            tv_connect_state.setText(UIUtils.getString(R.string.disConnect))
        }*/
        //???????????????????????????
        //????????????
        currentType = intent.getIntExtra("ropeSportType", 0)

        if (currentType == JkConfiguration.RopeSportType.Challenge) {
            bean = intent.getSerializableExtra("bean") as Challeng?


            layout_bottom?.visibility  = View.GONE
            layout_bottom_value?.visibility = View.GONE

            ropeChallengeLayout?.visibility = View.VISIBLE

        }
        setDeviceDef()
        initDatas(currentType, isviewClick)
        showHeartView(false)
        isCanBack(true)
        setconState()
        setCalValue("0")
        setHrValue(0)

    }



    override fun initEvent() {


        iv_device_state.setOnClickListener {
            tag = iv_device_state.getTag() as String
            if (tag.equals(disCon)) {
                showConnDialog()
            }

        }

        ISportAgent.getInstance().registerListener(mBleReciveListener)
        RopeRealDataObservable.getInstance().addObserver(this)
        RopeStartOrEndSuccessObservable.getInstance().addObserver(this)
        GetRopeTargDataObservable.getInstance().addObserver(this)
        titleBarView.setRightIcon(R.drawable.icon_rope_setting)

        //????????????
        tv_rope_start.setOnClickListener {

            if (AppConfiguration.isConnected && AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING){
                var cusCountDownView = CountDownDialogView(this)
                cusCountDownView.show()
                cusCountDownView.startShow()
                cusCountDownView.setOnCusCountDownCompleteListener(CountDownDialogView.OnCusCountDownCompleteListener {
                    cusCountDownView.dismiss()

                    //?????????????????????
                    ISportAgent.getInstance().requestBle(BleRequest.rope_start_or_end, 2)
                    sportBean!!.isStart = true
                    isClickStart = true
                })
            }else{
                showConnDialog()
            }

        }

        //????????????
        tv_top_taget_view.setOnClickListener {

            if(!isClickStart)
                return@setOnClickListener

            when (sportBean!!.currentRopeType) {
                JkConfiguration.RopeSportType.Count -> {   //????????????
                    mSelectPopupWindow?.popWindowSelect(
                            this@RealRopeSkippingActivity,
                            tv_top_value,
                            SelectPopupWindow.ROPENUMBER,
                            tv_top_value?.text.toString(), true
                    )
                }
                JkConfiguration.RopeSportType.Time -> {  //????????????

                    mSelectPopupWindow?.setPopupWindowTemp(
                            this@RealRopeSkippingActivity,
                            tv_top_value,
                            50
                    )
                }
            }
        }


        mSelectPopupWindow = SelectPopupWindow(object : SelectPopupWindow.OnSelectPopupListener {
            override fun onSelect(type: String, data: String) {
                when (type) {
                    SelectPopupWindow.BIRTHDAY -> {
                        if (AppConfiguration.isConnected) {
                            val value = data.toInt()
                            if (value >= 30) {
                                if (targetBean != null) {
                                    targetBean?.targetMin = value / 60
                                    targetBean?.targetSec = value % 60
                                    sportBean!!.countdownDucation = targetBean!!.targetMin * 60 + targetBean!!.targetSec
                                    ISportAgent.getInstance().requestBle(BleRequest.rope_set_state, sportBean!!.currentRopeType, targetBean!!.targetMin, targetBean!!.targetSec, targetBean!!.targetCount, 1)
                                    setTopeValue(DateUtil.getRopeFormatTimehhmmss(sportBean!!.countdownDucation.toLong()))
                                    //   tv_top_value.text = (DateUtil.getRopeFormatTimehhmmss(sportBean!!.countdownDucation.toLong()))


                                }
                            } else {
                                ToastUtils.showToast(context, UIUtils.getString(R.string.rope_setting_time_title))
                            }
                        } else {
                            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
                        }
                        // itemview_birthday.setRightText(data)
                    }
                    SelectPopupWindow.ROPENUMBER -> {
                        // targetBean.targetCount=
                        if (AppConfiguration.isConnected) {
                            try {
                                var strings = data.split(" ")
                                // tv_top_value.text = strings[0]

                                setTopeValue(strings[0])
                                targetBean!!.targetCount = strings[0].toInt()
                                ISportAgent.getInstance().requestBle(BleRequest.rope_set_state, sportBean!!.currentRopeType, targetBean!!.targetMin, targetBean!!.targetSec, targetBean!!.targetCount, 1)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {

                            }
                        } else {
                            ToastUtils.showToast(context, UIUtils.getString(R.string.app_disconnect_device))
                        }


                        // mHeight = data
                        // itemview_height.setRightText(data + "cm")
                    }
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        //isPlayMusic()

    }


    fun isCanBack(isCanBack: Boolean) {
        if (isCanBack) {
            tv_rope_start.setTag(end)

        } else {
            tv_rope_start.setTag(start)
        }

        tv_top_taget_view.setShowShadow (isCanBack)
        tv_top_taget_tips.text = if(isCanBack) sportBean?.topTargetTips else sportBean?.topTitle

        isClickStart = isCanBack;
        if (sportBean != null) {
            titleBarView.setInvisibalLeftIcon(!sportBean!!.isStart)
            titleBarView.setRightIconVisible(!sportBean!!.isStart)
        }



    }

    //??????????????????????????????????????????????????????

    override fun update(o: Observable?, arg: Any?) {
        super.update(o, arg)
        //??????????????????
        if (o is RopeRealDataObservable) {
            Logger.myLog(tgs, "-----??????????????????------=" + Gson().toJson(arg))
            handler.post {
                if (arg is Boolean) {
                    if (arg) {
                        if (sportBean!!.isStart) {
                            updateChalleg();
                        }
                    } else {
                        //??????????????????????????????????????????????????????????????????
                        if (sportBean!!.isStart) {
                            isCanStart = true
                        }
                    }
                } else if (arg is RopeRealDataBean) {


                    if (!isCanStart) {
                        return@post
                    }
                    sportBean!!.isStart = true;
                    isCanBack(false)
                    tv_rope_start.visibility = View.GONE
                    layout_end.visibility = View.VISIBLE
                    isviewClick = false;
                    val msg = arg as RopeRealDataBean
                    sportBean!!.currentCount = msg.ropeSumCount.toInt()
                    isCallEnd = false;
                    if (sportBean!!.currentRopeType != msg.ropeType) {
                        isSettingSuccess = false
                       // initDatas(msg.ropeType, isviewClick)
                    }

                    Logger.myLog(tgs,"----??????--bean==null="+Gson().toJson(bean));

                    if (bean != null) {//???????????????
                        if (bean!!.achieveSecond == 0) {   //???????????????????????????????????????????????????
                            sportBean!!.ducation = msg.time.toInt()
                            sportBean!!.topValue = "" + msg.countdown
                            sportBean!!.bottomValue = DateUtil.getRopeFormatTimehhmmss(msg.time)
                            sportBean!!.topTitle = resources.getString(R.string.string_challenge_numbers)
                        } else {    //???????????????
                           // sportBean!!.topValue = "" + msg.countdown


                            sportBean!!.countdownDucation = msg.countdownMin * 60 + msg.countdownSec
                          //  sportBean!!.bottomValue = DateUtil.getRopeFormatTimehhmmss(sportBean!!.countdownDucation.toLong())

                            sportBean!!.topValue =  DateUtil.getRopeFormatTimehhmmss(sportBean!!.countdownDucation.toLong())
                            sportBean!!.bottomValue =""+ msg.countdown

                            sportBean!!.ducation = msg.time.toInt()
                            sportBean!!.topTargetTips = resources.getString(R.string.string_challenge_time)
                            sportBean!!.topTitle = resources.getString(R.string.string_challenge_time)
                        }

                    } else {
                        when (msg.ropeType) {
                            JkConfiguration.RopeSportType.Free -> {   //????????????
                                sportBean!!.ducation = msg.time.toInt()
                                sportBean!!.topValue = "" + msg.ropeSumCount
                                sportBean!!.bottomValue = DateUtil.getRopeFormatTimehhmmss(msg.time)

                            }
                            JkConfiguration.RopeSportType.Count -> {  //????????????
                                sportBean!!.ducation = msg.time.toInt()
                                sportBean!!.topValue = "" + msg.countdown
                                sportBean!!.bottomValue = DateUtil.getRopeFormatTimehhmmss(msg.time)
                            }
                            JkConfiguration.RopeSportType.Time -> {  //????????????
                                sportBean!!.ducation = msg.time.toInt()
                                sportBean!!.countdownDucation = msg.countdownMin * 60 + msg.countdownSec
                                sportBean!!.topValue = DateUtil.getRopeFormatTimehhmmss(sportBean!!.countdownDucation.toLong())
                                sportBean!!.bottomValue = "" + msg.ropeSumCount

                            }

                            JkConfiguration.RopeSportType.Challenge -> {
                                sportBean!!.topValue = "" + msg.countdown

                                sportBean!!.countdownDucation = msg.countdownMin * 60 + msg.countdownSec
                                sportBean!!.bottomValue = DateUtil.getRopeFormatTimehhmmss(sportBean!!.countdownDucation.toLong())
                                sportBean!!.ducation = msg.time.toInt()
                            }

                        }

                    }


                    // isPlayMusic()

                    sportBean!!.currentRopeType = msg.ropeType

                    currentTime = DateUtil.getRopeFormatTimehhmmss(msg.time)
                    currentToalCal = "" + msg.cal
                    currentRopeCount = "" + msg.ropeSumCount
                    setTopeValue(sportBean!!.topValue)
                    setBottomValues(sportBean!!.bottomValue)
                    currentHearHr = msg.realHr
                    setHrValue(currentHearHr)
                    setCalValue("" + msg.cal)
                    startPlayMusic()
                    isRopeTimeOpen(msg.ropeSumCount, sportBean!!.ducation)
                    isRopeCountOpen(msg.ropeSumCount)
                    isRopeHrRemideOpen(msg.realHr)

                    //????????????
                    if(bean != null && bean!!.achieveSecond != 0){
                        ropeChallengeCusScheduleView?.allScheduleValue = bean!!.achieveNum.toFloat()
                        ropeChallengeCusScheduleView?.currScheduleValue = msg.ropeSumCount.toFloat()

                    }else{
                        ropeChallengeCusScheduleView?.allScheduleValue = 100f
                        ropeChallengeCusScheduleView?.currScheduleValue = 0f
                    }
                }
            }

        } else if (o is GetRopeTargDataObservable) {

            Logger.myLog(tgs,"----------??????????????????="+Gson().toJson(arg))

            handler.post {
                isSettingSuccess = true
                Logger.myLog("GetRopeTargDataObservable")
                if (arg is RopeTargetDataBean) {
                    targetBean = arg as RopeTargetDataBean
                    when (targetBean?.ropeType) {
                        JkConfiguration.RopeSportType.Free -> {
                            sportBean!!.isClick = false
                        }
                        JkConfiguration.RopeSportType.Count -> {
                            sportBean!!.isClick = true
                            sportBean!!.topValue = "" + targetBean?.targetCount
                        }
                        JkConfiguration.RopeSportType.Time -> {

                            sportBean!!.countdownDucation = targetBean!!.targetMin * 60 + targetBean!!.targetSec
                            sportBean!!.topValue = DateUtil.getRopeFormatTimehhmmss(sportBean!!.countdownDucation.toLong())
                        }

                        JkConfiguration.RopeSportType.Challenge -> {
                            sportBean!!.isClick = false
                        }
                    }

                    setValue()
                }
            }
        } else if (o is RopeStartOrEndSuccessObservable) {   //???????????????

            Logger.myLog(tgs,"---------???????????????????????????="+Gson().toJson(arg))
            if (arg is RopeRealDataBean) {
                //????????????
                val msg = arg as RopeRealDataBean
                when (msg.ropeType) {
                    JkConfiguration.RopeSportType.Count -> {
                        sportBean!!.topValue = "" + msg.countdown
                    }
                    JkConfiguration.RopeSportType.Time -> {
                        sportBean!!.topValue = "00:" + String.format("%02d", msg.countdownMin) + ":" + String.format("%02d", msg.countdownSec)
                    }
                    JkConfiguration.RopeSportType.Challenge -> {
                    }
                }
                isCanBack(true)
                sportBean!!.isClick = true
                sportBean!!.isStart = false;
                setValue()
            } else if (arg is Boolean) {


                //?????????????????????

                /*handler.post {
                    if (isClickStart) {
                        if (sportBean!!.isStart) {
                            isCanStart = true
                            tv_rope_start.visibility = View.GONE
                            layout_end.visibility = View.VISIBLE
                            sportBean!!.isClick = false
                            sportBean!!.isStart = true
                            startPlayMusic()
                            playStartOrEndSpeak(true)
                            isCanBack(false)
                            setValue()
                        }
                    } else {
                        //????????????
                        updateChalleg()

                    }
                }*/

                if (arg) {
                    //??????
                    handler.post {
                        isCanStart = true
                        tv_rope_start.visibility = View.GONE
                        layout_end.visibility = View.VISIBLE
                        sportBean!!.isClick = false
                        sportBean!!.isStart = true
                        startPlayMusic()
                        playStartOrEndSpeak(true)
                        isCanBack(false)
                       // setValue()
                    }

                } else {
                    //??????
                    handler.post {
                        updateChalleg()
                    }
                }

                //Logger.myLog("RopeStartOrEndSuccessObservable isClickStart=" + isClickStart)


            }


        }
    }

    var handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0x01 -> {
                    if (sportBean!!.isStart) {
                        Constants.CAN_RECONNECT = false
                        disConDialog()
                    }
                    /* Logger.myLog("handler 0x01")
                     RopeCompletyDialog(this@RealRopeSkippingActivity, currentRopeCount, currentTime, currentRopeName, currentToalCal, currentRopeRes, RopeCompletyDialog.OnTypeClickListenter {
                         finish()
                     })*/
                }
            }

        }
    }
    private val mBleReciveListener: BleReciveListener = object : BleReciveListener {
        override fun onConnResult(isConn: Boolean, isConnectByUser: Boolean, baseDevice: BaseDevice) {
            if (isConn) {
                /*  if (ISportAgent.getInstance().currnetDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                      tv_connect_state.setText(UIUtils.getString(R.string.connect))
                  }*/
                setDeviceDef()
                PublicAlertDialog.getInstance().clearShowDialog()
                handler.removeMessages(0x01)
            } else {
                if (!isCallEnd) {
                    // tv_connect_state.setText(UIUtils.getString(R.string.disConnect))
                    handler.removeMessages(0x01)
                    handler.sendEmptyMessageDelayed(0x01, 10000);
                }
            }
            setconState()
        }

        override fun setDataSuccess(s: String) {

        }

        override fun receiveData(mResult: IResult) {

        }

        override fun onConnecting(baseDevice: BaseDevice) {}
        override fun onBattreyOrVersion(baseDevice: BaseDevice) {}
    }




    private fun startStopPlay() {
        isLockComple = false
        mCurrentProgress = 0
        startAnimotion()
    }


    fun startAnimotion() {
        /**
         * ??????????????? ??????????????????????????????
         * ???????????????
         *
         */
        //progreesVaule=5000;
        if (sport_stop != null) {
            //  sportStop.setProgress(0);
            animator = ObjectAnimator.ofFloat(sport_stop, "progress", 0f, 100f)
            animator!!.setDuration(3000)
            animator!!.setInterpolator(FastOutSlowInInterpolator())
            animator!!.start()
            animator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    super.onAnimationCancel(animation)
                    sport_stop.setProgress(0f)
                    animator = null
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    sport_stop.setProgress(0f)

                    if (isUP) {

                    } else {
                        animator = null
                        //	2 ?????????   1 ????????????????????????
                        //

                        if (bean != null) {
                            onIsEndChall()
                        } else {
                            sendEnd()
                        }
                    }
                }

                override fun onAnimationRepeat(animation: Animator) {
                    super.onAnimationRepeat(animation)
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                }

                override fun onAnimationPause(animation: Animator) {
                    super.onAnimationPause(animation)
                }

                override fun onAnimationResume(animation: Animator) {
                    super.onAnimationResume(animation)
                }
            })
        }
    }


    fun sendEnd() {
        if (AppConfiguration.isConnected) {
            ISportAgent.getInstance().requestBle(BleRequest.rope_start_or_end, 1)
        }
    }

    val start = "start"
    var end = "end"

    fun initDatas(currentType: Int, click: Boolean) {

        hrList.clear()
        tv_rope_start.setTag(start)
        layout_end.visibility = View.GONE
        when (currentType) {
            JkConfiguration.RopeSportType.Free -> {   //????????????
                currentRopeName = UIUtils.getString(R.string.rope_free)
                currentRopeRes = R.drawable.icon_rope_free
                sportBean = RopeSportTypeBean(UIUtils.getString(R.string.rope_real_rope_number), UIUtils.getString(R.string.rope_real_setting_taget_number), false, UIUtils.getString(R.string.rope_real_rope_time), "00:00:00", "0",
                        JkConfiguration.RopeSportType.Free, UIUtils.getString(R.string.rope_count_unitl), "")
            }
            JkConfiguration.RopeSportType.Time -> {   //????????????
                currentRopeName = UIUtils.getString(R.string.rope_time)
                currentRopeRes = R.drawable.icon_rope_time
                sportBean = RopeSportTypeBean(UIUtils.getString(R.string.rope_real_setting_countdown_start), UIUtils.getString(R.string.rope_real_setting_taget_time), click, UIUtils.getString(R.string.rope_real_rope_number), "0", "00:00:00",
                        JkConfiguration.RopeSportType.Time, "", UIUtils.getString(R.string.rope_count_unitl))
            }
            JkConfiguration.RopeSportType.Count -> {    //????????????
                currentRopeName = UIUtils.getString(R.string.rope_count)
                currentRopeRes = R.drawable.icon_rope_count
                sportBean = RopeSportTypeBean(UIUtils.getString(R.string.rope_real_setting_taget_number_start), UIUtils.getString(R.string.rope_real_setting_taget_number), click, UIUtils.getString(R.string.rope_real_rope_time), "00:00:00", "0",
                        JkConfiguration.RopeSportType.Count, "", "")
            }

            JkConfiguration.RopeSportType.Challenge -> {   //??????

                Logger.myLog(tgs,"----????????????="+Gson().toJson(bean))
                if (bean != null) {
                    var time = "00:00:00"
                    if (bean!!.achieveSecond != 0) {
                        time = DateUtil.getRopeFormatTimehhmmss(bean!!.achieveSecond.toLong())
                    }
                    currentRopeName = bean!!.name
                    currentRopeRes = R.drawable.icon_rope_change

                    var topViewStr = if (bean!!.challengeItemId.toInt()>= 7) DateUtil.getRopeFormatTimehhmmss(bean!!.achieveSecond.toLong()) else bean!!.achieveNum
                    val goalDesc =  if (bean!!.challengeItemId.toInt()>= 7) resources.getString(R.string.string_challenge_time) else UIUtils.getString(R.string.rope_real_setting_taget_number_start)

                    sportBean = RopeSportTypeBean(goalDesc, UIUtils.getString(R.string.rope_real_setting_taget_number), false, UIUtils.getString(R.string.rope_real_rope_time), time, "" + topViewStr,
                            JkConfiguration.RopeSportType.Count, "", "")
                } else {
                    currentRopeName = UIUtils.getString(R.string.rope_change)
                    currentRopeRes = R.drawable.icon_rope_change
                    sportBean = RopeSportTypeBean(UIUtils.getString(R.string.rope_real_setting_taget_number_start), UIUtils.getString(R.string.rope_real_setting_taget_number), false, UIUtils.getString(R.string.rope_real_rope_time), "00:00:00", "0",
                            JkConfiguration.RopeSportType.Count, "", "")
                }
            }
            JkConfiguration.RopeSportType.Course -> {   //??????
                currentRopeName = UIUtils.getString(R.string.rope_courese)
                sportBean = RopeSportTypeBean(UIUtils.getString(R.string.rope_real_setting_taget_number_start), UIUtils.getString(R.string.rope_real_setting_taget_number), true, UIUtils.getString(R.string.rope_real_rope_time), "00:00:00", "0",
                        JkConfiguration.RopeSportType.Count, "", "")
            }


        }
        titleBarView.setTitle(currentRopeName)
        setValue()

    }

    fun setconState() {
        if (AppConfiguration.isConnected) {
            if (AppConfiguration.currentConnectDevice !== null && AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
                iv_device_state.setTag(conn)
                iv_device_state.setAlpha(1.0f)
            } else {
                iv_device_state.setTag(disCon)
                iv_device_state.setAlpha(0.3f)
            }
        } else {
            iv_device_state.setAlpha(0.3f)
            iv_device_state.setTag(disCon)
        }
    }

    var min = 0;
    var secd = 0;


    fun showHeartView(isShow: Boolean) {
        if (isShow) {
            lineChartView.visibility = View.VISIBLE
            tv_hr_title.visibility = View.VISIBLE
        } else {
            lineChartView.visibility = View.GONE
            tv_hr_title.visibility = View.GONE
        }
    }




    private fun initViews(){
        ropeChallengeLayout = findViewById(R.id.ropeChallengeLayout)
        ropeChallengeKcalTv = findViewById(R.id.ropeChallengeKcalTv)
        ropeChallengeHeartTv = findViewById(R.id.ropeChallengeHeartTv)
        ropeChallengeNumTv = findViewById(R.id.ropeChallengeNumTv)
        ropeChallengeCusScheduleView = findViewById(R.id.ropeChallengeCusScheduleView)

        layout_bottom_value = findViewById(R.id.layout_bottom_value)
        layout_bottom = findViewById(R.id.layout_bottom)
    }



    //???????????????
    fun setCalValue(cal: String) {
        tv_cal_value.text = cal
        ropeChallengeKcalTv?.text = cal
    }


    //?????????????????????
    fun setTopeValue(count: String) {

        Logger.myLog(tgs,"------sportBeansportBean-----="+Gson().toJson(sportBean)+"--bena="+Gson().toJson(bean)+"\n"+Gson().toJson(targetBean)+"\n"+count)

        tv_top_value.text = (count)
        tv_top_value_precent.text = (count)

        Logger.myLog(tgs, "-----?????????currentType=$currentType")

        var pre = 1.0f
        when (currentType) {
            JkConfiguration.RopeSportType.Free -> {
                pre = 1f
            }
            JkConfiguration.RopeSportType.Time -> {
                if (sportBean!!.ducation == 0) {
                    pre = 0f
                } else {
                    if (targetBean != null) {
                        pre = sportBean!!.ducation * 1f / (targetBean!!.targetMin * 60 + targetBean!!.targetSec)
                    }

                }
                //  pre = sportBean?.ducation / sportBean?.countdownDucation

            }
            JkConfiguration.RopeSportType.Count -> {
                if (sportBean!!.currentCount == 0) {
                    pre = 0f
                } else {
                    if (targetBean != null) {
                        pre = sportBean!!.currentCount * 1f / targetBean!!.targetCount
                    }
                }

            }
        }

        if (bean != null) {
            if (sportBean!!.currentCount == 0) {
                pre = 0f
            } else {
                pre = sportBean!!.currentCount * 1f / bean!!.achieveNum
            }

        }


        val linearParams = layout_top_value.layoutParams as RelativeLayout.LayoutParams //?????????textView????????????????????? linearParams.height = 20;// ????????????????????????20

        Logger.myLog(tgs, "--------?????????pre=$pre")
        if (pre != 1f && pre != 0f) {
            if (pre > 0.98f) {
                pre = 0.98f
            }
            /*if (pre < 0.33f) {
                pre = 0.33f
            }*/

        }

        var realHeight = tv_top_value_precent.height
     //   linearParams.height = (realHeight * (1 - pre) + DisplayUtils.dip2px(this, 15f)).toInt() // ????????????????????????30

        Logger.myLog("linearParams" + linearParams.height + "tv_top_value_precent.getHeight()=" + tv_top_value_precent.getHeight() + "linearParams.height" + linearParams.height + "pre=" + pre+" dip="+DisplayUtils.dip2px(this, 15f))

        // && sportBean?.currentRopeType != 0x00
        if(currentType == JkConfiguration.RopeSportType.Free || sportBean?.currentRopeType ==0){

        }else{
            linearParams.height = (realHeight * (1 - pre) + DisplayUtils.dip2px(this, 15f)).toInt() // ??????????????? // ????????????????????????30
            layout_top_value.layoutParams = linearParams //??????????????????????????????????????????
        }


        //layout_top_value.setp

        //  tv_top_value.text = count
    }

    fun setBottomValues(bottom: String) {
        tv_bottom_value.text = bottom

        ropeChallengeNumTv?.text = bottom
    }

    fun setHrValue(hr: Int) {

        // Logger.myLog("RealRopeSkippingActivity:setHrValue=" + hr)

        if (hr > 30) {
            showHeartView(true)
            setLineDataAndShow(hr)
            tv_hr_value.text = "" + hr
            ropeChallengeHeartTv?.text = ""+hr
        } else {
            tv_hr_value.text = UIUtils.getString(R.string.no_data)
            ropeChallengeHeartTv?.text =  UIUtils.getString(R.string.no_data)
        }

        setHeartTvColor(hr)

    }


    //?????????????????????
    private fun setHeartTvColor(heartValue : Int){
        val point = HeartRateConvertUtils.hearRate2Point(heartValue, HeartRateConvertUtils.getMaxHeartRate(age, sex))
        //Logger.myLog("hrList.get(i)" + hrValue + "HeartRateConvertUtils.getMaxHeartRate(age):" + HeartRateConvertUtils.getMaxHeartRate(age, sex) + "age:" + age + "point:" + point)
        //Logger.myLog("age=" + age + "hrValue=" + hrValue + "point=" + point + "sex=" + sex)

        var color = UIUtils.getColor(R.color.common_white)
        when (point) {
            0 -> {
                color = UIUtils.getColor(R.color.color_leisure)
            }
            1 -> {
                color = UIUtils.getColor(R.color.color_warm_up)
            }
            2 -> {
                color = UIUtils.getColor(R.color.color_fat_burning_exercise)
            }
            3 -> {
                color = UIUtils.getColor(R.color.color_aerobic_exercise)
            }
            4 -> {
                color = UIUtils.getColor(R.color.color_anaerobic_exercise)
            }
            5 -> {
                color = UIUtils.getColor(R.color.color_limit)
            }
        }

        ropeChallengeHeartTv?.setTextColor(color)
    }






    fun setValue() {

        Logger.myLog(tgs,"----sportBean="+sportBean.toString())
        setTopeValue(sportBean!!.topValue)
        tv_top_unit.text = (sportBean!!.topUnit)
        tv_top_tips.text = (sportBean!!.topTitle)
        tv_top_taget_tips.text = (sportBean!!.topTargetTips)
        tv_bottom_value.text = (sportBean!!.bottomValue)


        tv_bottom_unitl.text = (sportBean!!.bottomUnit)
        tv_bottom_title.text = (sportBean!!.bottomTitle)
        if (sportBean!!.isClick) {
            tv_top_tips.visibility = View.GONE
        } else {
            tv_top_tips.visibility = View.VISIBLE
        }
        if (sportBean!!.isStart) {
            titleBarView.setRightIconVisible(false)
        } else {
            titleBarView.setRightIconVisible(true)
        }

        //????????????
        if(bean != null && bean!!.achieveSecond != 0){
            ropeChallengeCusScheduleView?.allScheduleValue = bean!!.achieveNum.toFloat()
            ropeChallengeCusScheduleView?.currScheduleValue = sportBean!!.currentCount.toFloat()

        }else{
            ropeChallengeCusScheduleView?.allScheduleValue = 100f
            ropeChallengeCusScheduleView?.currScheduleValue = 0f
        }


    }

    override fun createPresenter(): RealRopeSkippingPresenter {
        mDeviceConnPresenter = DeviceConnPresenter()
        return RealRopeSkippingPresenter(this);
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


    fun notSaveData() {
        PublicAlertDialog.getInstance().showDialogNoCancle(false, "", getString(R.string.rope_time_small_not_save), context, resources.getString(R.string.confirm), object : AlertDialogStateCallBack {
            override fun determine() {
                stopPlayMusic()
                finish()
            }

            override fun cancel() {}
        })
    }

    fun disConDialog() {
        //??????????????????
        stopPlayMusic()
        PublicAlertDialog.getInstance().showDialogNoCancle(false, "", getString(R.string.rope_device_dis_end), context, resources.getString(R.string.confirm), object : AlertDialogStateCallBack {
            override fun determine() {

                updateChalleg()

                //  finish()
            }

            override fun cancel() {}
        })
    }

    override fun onDestroy() {

        super.onDestroy()
        stopPlayMusic()
        Constants.CAN_RECONNECT = true
        if (disposableTimer != null && !disposableTimer!!.isDisposed) {
            disposableTimer!!.dispose()
        }
        handler.removeMessages(0x01)
        unRegister()
        ISportAgent.getInstance().unregisterListener(mBleReciveListener)
        RopeRealDataObservable.getInstance().deleteObserver(this)
        RopeStartOrEndSuccessObservable.getInstance().deleteObserver(this)
        GetRopeTargDataObservable.getInstance().deleteObserver(this)
    }

    var disposableTimer: Disposable? = null
    private val list: ArrayList<Int>? = ArrayList()
    private val times: ArrayList<String>? = ArrayList()


    fun showConnDialog() {
        if (!AppUtil.isOpenBle()) {
            openBlueDialog()
            return
        }
        PublicAlertDialog.getInstance().showDialog("", UIUtils.getString(R.string.main_device_no_conn_title), context, resources.getString(R.string.cancel), resources.getString(R.string.connect), object : AlertDialogStateCallBack {
            override fun determine() {
                //???????????????????????????????????????????????????
                ISportAgent.getInstance().disConDevice(false)
                connectWatchOrBracelet(true, JkConfiguration.DeviceType.ROPE_SKIPPING)
            }

            override fun cancel() {

            }
        }, false)
    }

    @Synchronized
    private fun connectWatchOrBracelet(isConnectByUser: Boolean, deviceType: Int) {
        ISportAgent.getInstance().disConDevice(false)
        if (AppConfiguration.deviceMainBeanList.containsKey(deviceType)) {
            var currentName = AppConfiguration.deviceMainBeanList.get(deviceType)!!.deviceName;
            //??????????????????????????????????????? ??????????????????????????????????????????
            if (DeviceTypeUtil.isContainW55X(deviceType) && AppSP.getString(context, AppSP.FORM_DFU, "false") == "false") {
                val watchMac = DeviceTypeUtil.getW526Mac(currentName, deviceType)
                if (!TextUtils.isEmpty(watchMac)) {
                    AppSP.putString(context, AppSP.WATCH_MAC, DeviceTypeUtil.getW526Mac(currentName, deviceType))
                    AppSP.putString(context, AppSP.DEVICE_CURRENTNAME, currentName)
                }
            }
            val watchMac = AppSP.getString(context, AppSP.WATCH_MAC, "")
            mDeviceConnPresenter!!.connectDevice(currentName, watchMac, deviceType, true, isConnectByUser)
        }
    }


    var isCallEnd = false


    fun updateChalleg() {

        isCanStart = false
        Logger.myLog(tgs, "----updateChalleg Challeg=" + Gson().toJson(sportBean))
        sportBean!!.isStart = false;
        isCanBack(true)
        tv_rope_start.visibility = View.VISIBLE
        layout_end.visibility = View.GONE
        if (!isCallEnd) {
            isCallEnd = true
        } else {
            return
        }


        /*  if (sportBean!!.ducation < 10) {
              notSaveData()
              return
          }*/

        Logger.myLog(tgs, "--------bean=" + Gson().toJson(bean))
        //successExciseSuccess()
        if (bean != null) {

            //?????????
            if (sportBean!!.topValue.equals("0")) {
                mActPresenter.upgradeChalleg(bean!!.challengeItemId, TokenUtil.getInstance().getPeopleIdInt(BaseApp.getApp()))


            } else {
                failChallegUpdate()
            }

        } else {
            Logger.myLog("updateChalleg successExciseSuccess")
            successExciseSuccess()
        }
    }


    fun onIsEndChall() {
        PublicAlertDialog.getInstance().showDialog(UIUtils.getString(R.string.fir_upgrade_title), UIUtils.getString(R.string.rope_challeg_is_end), context, resources.getString(R.string.cancel), resources.getString(R.string.rope_challeg_end), object : AlertDialogStateCallBack {
            override fun determine() {
                sendEnd()
            }

            override fun cancel() {}
        }, false)
    }


    //????????????
    override fun successChallegUpdate(rankId: String) {
        stopPlayMusic()
        var ropetimeOpen: Boolean by Preference(Preference.ROPE_TIME_OPEN, true)
        var ropeCountOpen: Boolean by Preference(Preference.ROPE_COUNT_OPEN, false)
        var ropeHrOpen: Boolean by Preference(Preference.ROPE_Hr_OPEN, true)
        if (ropetimeOpen || ropeCountOpen || ropeHrOpen) {
            speakUti.startSpeaking(UIUtils.getString(R.string.rope_challeg_success), true)
            /* if (!mTts!!.isSpeaking) {

                 appstartSpeakking(UIUtils.getString(R.string.rope_challeg_success))
             } else {
                 readList.put(1, UIUtils.getString(R.string.rope_challeg_success))

             }*/
        }

        mActPresenter.getAllRopeChallengeRank(rankId)

        Logger.myLog(TAG, "------??????=" + Gson().toJson(hrList))

        //??????????????????
        var countHeart = 0

        for(curr in hrList){
            countHeart += curr
        }

        val avgHeart = countHeart / hrList.size

        Logger.myLog(tgs, "--------????????????=$avgHeart")
        if (bean?.achieveSecond != 0) {
            RopePkCompletyDialog(this@RealRopeSkippingActivity, currentRopeName, currentTime + "/" + DateUtil.getRopeFormatTimehhmmss((bean?.achieveSecond)!!.toLong()), currentRopeCount + "/" + bean?.achieveNum, currentToalCal, true, avgHeart, object : RopePkCompletyDialog.OnTypeClickListenter {
                override fun changeDevcieonClick(type: Int) {
                    //handler.removeCallbacks(null)
                    stopPlayMusic()
                    finish()
                }
                override fun changeSuccessClick() {

                    val intent = Intent(context, NewShareActivity::class.java)
                    intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.RopeSportType.ROPE_CHALLENGE)
                    val shareBean = ShareBean()
                    //???????????? ???????????????????????????????????????
                    shareBean.centerValue = currentRopeCount
                    shareBean.one = currentTime
                    shareBean.time = DateTimeUtils.getCurrentDate()
                    //????????????
                    shareBean.ropeAvgHeart = ""+avgHeart
                    //????????????
                    shareBean.challengeDesc = currentRopeName
                    //??????
                    shareBean.challengeRank = challengeRank
                    //??????
                   // shareBean.two = tv_exeCount.text.toString()
                    //??????
                    shareBean.three = currentToalCal
                  //  shareBean.time = tv_update_time.text.toString()

                    intent.putExtra(JkConfiguration.FROM_BEAN, shareBean)
                    startActivity(intent)

                }

            })
        } else {

            RopePkCompletyDialog(this, currentRopeName, currentTime,currentRopeCount + "/" + bean?.achieveNum, currentToalCal,
                    true, avgHeart, object : RopePkCompletyDialog.OnTypeClickListenter {
                override fun changeDevcieonClick(type: Int) {
                    stopPlayMusic()
                    finish()
                }
                override fun changeSuccessClick() {
                    val intent = Intent(context, NewShareActivity::class.java)
                    intent.putExtra(JkConfiguration.FROM_TYPE, JkConfiguration.RopeSportType.ROPE_CHALLENGE)
                    val shareBean = ShareBean()
                    //???????????? ???????????????????????????????????????
                    shareBean.centerValue = currentRopeCount
                    shareBean.one = currentTime
                    shareBean.time = DateTimeUtils.getCurrentDate()
                    //????????????
                    shareBean.ropeAvgHeart = ""+ avgHeart
                    //????????????
                    shareBean.challengeDesc = currentRopeName
                    //??????
                    shareBean.challengeRank = challengeRank
                    //??????
                    // shareBean.two = tv_exeCount.text.toString()
                    //??????
                    shareBean.three = currentToalCal
                   // shareBean.time = tv_update_time.text.toString()

                    intent.putExtra(JkConfiguration.FROM_BEAN, shareBean)
                    startActivity(intent)

                }
            })


//            RopePkCompletyDialog(this@RealRopeSkippingActivity, currentRopeName, currentTime, currentRopeCount + "/" + bean?.achieveNum, currentToalCal, true, 0, RopePkCompletyDialog.OnTypeClickListenter {
//                //    handler.removeCallbacks(null)
//                stopPlayMusic()
//                finish()
//            })
        }

    }

    //????????????????????????
    override fun getAllRopeChallengeRank(rank: String) {
        Logger.myLog(tgs, "-----???????????????=$rank")
        this.challengeRank = rank
    }

    //????????????
    fun failChallegUpdate() {
        stopPlayMusic()
        var ropetimeOpen: Boolean by Preference(Preference.ROPE_TIME_OPEN, true)
        var ropeCountOpen: Boolean by Preference(Preference.ROPE_COUNT_OPEN, false)
        var ropeHrOpen: Boolean by Preference(Preference.ROPE_Hr_OPEN, true)
        if (ropetimeOpen || ropeCountOpen || ropeHrOpen) {
            speakUti.startSpeaking(UIUtils.getString(R.string.rope_challeg_fail), true)
        }

        var avgHeart = 0
        if(hrList.isNotEmpty()){
              //??????????????????
        var countHeart = 0

        for(curr in hrList){
            countHeart += curr
        }

        avgHeart = countHeart / hrList.size
        }



        if (bean?.achieveSecond != 0) {
            RopePkCompletyDialog(this@RealRopeSkippingActivity, currentRopeName, currentTime + "/" + DateUtil.getRopeFormatTimehhmmss((bean?.achieveSecond)!!.toLong()), currentRopeCount + "/" + bean?.achieveNum, currentToalCal, false, avgHeart, object :RopePkCompletyDialog.OnTypeClickListenter {
                override fun changeDevcieonClick(type: Int) {
                    //handler.removeCallbacks(null)
                    stopPlayMusic()
                    finish()
                }

                override fun changeSuccessClick() {

                }


            })
        } else {
            RopePkCompletyDialog(this@RealRopeSkippingActivity, currentRopeName, currentTime, currentRopeCount + "/" + bean?.achieveNum, currentToalCal, false, avgHeart, object : RopePkCompletyDialog.OnTypeClickListenter {
                override fun changeDevcieonClick(type: Int) {
                  //  handler.removeCallbacks(null)
                    stopPlayMusic()
                    finish()
                }

                override fun changeSuccessClick() {

                }


            })
        }
    }

    fun successExciseSuccess() {
        Logger.myLog("successExciseSuccess")
        stopPlayMusic()
        playStartOrEndSpeak(false)

        var avgHeart = 0
        if(hrList.isNotEmpty()){
            //??????????????????
            var countHeart = 0

            for(curr in hrList){
                countHeart += curr
            }

            avgHeart = countHeart / hrList.size
        }

        RopeCompletyDialog(this@RealRopeSkippingActivity, currentRopeCount, currentTime, currentRopeName, currentToalCal, currentRopeRes, avgHeart,RopeCompletyDialog.OnTypeClickListenter {
            //  handler.removeCallbacks(null)

//            var intent = Intent(this, ActivityRopeDetailWebView::class.java)
//            intent.putExtra("title", UIUtils.getString(R.string.rope_dtail))
//            intent.putExtra("urldark", AppConfiguration.ropedetailDarkurl + "?userId=" + TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance) + "&ropeId=" + mDetailBean.get(position).ropeSportDetailId + "&language=" + AppLanguageUtil.getCurrentLanguageStr())
//            intent.putExtra("urlLigh", AppConfiguration.ropedetailLighturl + "?userId=" + TokenUtil.getInstance().getPeopleIdInt(BaseApp.instance) + "&ropeId=" + mDetailBean.get(position).ropeSportDetailId + "&language=" + AppLanguageUtil.getCurrentLanguageStr())
//            startActivity(intent)

             finish()
        })
    }


    fun startPlayMusic() {


        var musicOpen: Boolean by Preference(Preference.MUSIC_SWITCH, true)

        try {
            if (player != null && musicOpen && !player!!.isPlaying) {
                // player!!.prepare()
                player!!.start()
                // player!!.setLooping(true);
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun stopPlayMusic() {
        if (player != null && player!!.isPlaying) {
            player!!.stop()
        }
    }


    fun isRopeTimeOpen(curentcount: Long, duration: Int) {
        var ropetimeOpen: Boolean by Preference(Preference.ROPE_TIME_OPEN, true)
        var ropetime: Int by Preference(Preference.ROPE_TIME, 20)
        Logger.myLog("isRopeTimeOpen curentcount=" + curentcount + "--duration=" + duration + "--ropetimeOpen=" + ropetimeOpen + "---ropetime=" + ropetime + "--UserAcacheUtil.isRopeTime()=" + ropetime + UserAcacheUtil.isRopeTime())
        if (duration > ropetime && ropetimeOpen && UserAcacheUtil.isRopeTime()) {
            playCount(ropetime)
        }


    }


    var lastZheng = 0
    var lastYu = 0
    var currentZheng = 0
    var currentYu = 0

    fun isRopeCountOpen(currentCont: Long) {
        var ropetimeOpen: Boolean by Preference(Preference.ROPE_COUNT_OPEN, false)
        if (ropetimeOpen) {
            if (currentCont == 0L) {
                return
            }
            var ropeCount: Int by Preference(Preference.ROPE_COUNT, 50)
            currentZheng = (currentCont / ropeCount).toInt()
            currentYu = (currentCont % ropeCount).toInt();

            Logger.myLog("currentCont=" + currentCont + "currentZheng=" + currentZheng + ",currentYu=" + currentYu + "lastZheng=" + lastZheng + "lastYu" + lastYu)
            if (lastZheng != currentZheng && currentYu != lastYu && currentYu < 10) {
                lastZheng = currentZheng
                lastYu = currentYu
                if (!TextUtils.isEmpty(currentRopeCount) && !currentRopeCount.equals("0")) {
                    //?????????5????????????????????????
                    playtypeCount()
                }

            }
        }

    }


    fun playtypeCount() {
        if (bean != null) {
            appstartSpeakking(String.format(UIUtils.getString(R.string.rope_count_remid), currentRopeCount, CommonDateUtil.getRemindMin(sportBean!!.ducation, UIUtils.getString(R.string.rope_hour), UIUtils.getString(R.string.rope_min), UIUtils.getString(R.string.rope_sec)), sportBean!!.topValue))
            //??????
        } else {
            if (sportBean != null) {
                when (sportBean!!.currentRopeType) {
                    JkConfiguration.RopeSportType.Free -> {
                        appstartSpeakking(String.format(UIUtils.getString(R.string.rope_free_remid), currentRopeCount, CommonDateUtil.getRemindMin(sportBean!!.ducation, UIUtils.getString(R.string.rope_hour), UIUtils.getString(R.string.rope_min), UIUtils.getString(R.string.rope_sec))))

                    }
                    JkConfiguration.RopeSportType.Count -> {
                        appstartSpeakking(String.format(UIUtils.getString(R.string.rope_count_remid), currentRopeCount, CommonDateUtil.getRemindMin(sportBean!!.ducation, UIUtils.getString(R.string.rope_hour), UIUtils.getString(R.string.rope_min), UIUtils.getString(R.string.rope_sec)), sportBean!!.topValue))

                    }
                    JkConfiguration.RopeSportType.Time -> {

                        appstartSpeakking(String.format(UIUtils.getString(R.string.rope_time_remid), currentRopeCount, CommonDateUtil.getRemindMin(sportBean!!.countdownDucation, UIUtils.getString(R.string.rope_hour), UIUtils.getString(R.string.rope_min), UIUtils.getString(R.string.rope_sec))))

                    }

                    JkConfiguration.RopeSportType.Challenge -> {
                    }

                }
            }
        }
        /*if (!mTts!!.isSpeaking) {
            //?????????5????????????????????????
            appstartSpeakking(UIUtils.getString(R.string.rope_speed_start))
        } else {
            readList.put(1, UIUtils.getString(R.string.rope_speed_start))

        }*/
    }


    fun isRopeHrRemideOpen(hr: Int) {
        var ropeHrOpen: Boolean by Preference(Preference.ROPE_Hr_OPEN, true)
        if (ropeHrOpen) {
            var maxhrRemide: Int by Preference(Preference.ROPE_Hr_Count, 250)
            if (hr > maxhrRemide) {
                playHr()
            }
        }
    }

    lateinit var speakUti: SpeakUtil
    fun initSpeech() {
        // ?????????????????????

        // ?????????????????????
        speakUti = SpeakUtil.getInstance()
        speakUti.initSpeak(this)
    }


    var datas = mutableListOf<LineChartEntity>()
    var hrList = mutableListOf<Int>()
    private fun setLineDataAndShow(hrValue: Int) {
        datas.clear()
        var limint = 0
        var maxHr = 0
        var minHr = 0
        hrList.add(hrValue)
        if (hrList.size >= lineChartView.getCount()) {
            hrList.removeAt(0)
        }
        var len = hrList.size
        for (i in hrList.indices) {
            val hrValue = hrList[i]
            if (hrValue < 30) {
                continue
            }


            val point = HeartRateConvertUtils.hearRate2Point(hrValue, HeartRateConvertUtils.getMaxHeartRate(age, sex))
            //Logger.myLog("hrList.get(i)" + hrValue + "HeartRateConvertUtils.getMaxHeartRate(age):" + HeartRateConvertUtils.getMaxHeartRate(age, sex) + "age:" + age + "point:" + point)
            //Logger.myLog("age=" + age + "hrValue=" + hrValue + "point=" + point + "sex=" + sex)

            var color = UIUtils.getColor(R.color.common_white)
            when (point) {
                0 -> {
                    color = UIUtils.getColor(R.color.color_leisure)
                }
                1 -> {
                    color = UIUtils.getColor(R.color.color_warm_up)
                }
                2 -> {
                    color = UIUtils.getColor(R.color.color_fat_burning_exercise)
                }
                3 -> {
                    color = UIUtils.getColor(R.color.color_aerobic_exercise)
                }
                4 -> {
                    color = UIUtils.getColor(R.color.color_anaerobic_exercise)
                }
                5 -> {
                    limint++
                    color = UIUtils.getColor(R.color.color_limit)
                }
            }
            tv_hr_value.setTextColor(color)
            datas.add(LineChartEntity(i.toString(), (hrList[i].toString() + "").toFloat(), color))
        }
        Logger.myLog("setLineDataAndShow hrList:" + hrList + "len:" + len)

        if (len == 0) {
            len = 1
        }
        Logger.myLog("datas == isContainWatch" + datas.size)
        maxHr = 250
        minHr = 0
        lineChartView.setData(datas, true, maxHr, minHr, "0", "0")
    }

    /**
     *
     * ?????????
     *
     * @param mContext
     */
    var player: MediaPlayer? = MediaPlayer()
    fun initDataPlayer() {
        //1 ?????????AudioManager??????
        val mAudioManager: AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //2 ????????????
        mAudioManager.requestAudioFocus(mAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        val fileDescriptor: AssetFileDescriptor
        try {
            //3 ??????????????????,???????????????????????????????????????assets?????????
            fileDescriptor = getResources().openRawResourceFd(R.raw.music)
            //4 ?????????MediaPlayer??????
            //5 ?????????????????????
            player!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            //6 ?????????????????????????????????????????????????????????????????????????????????????????????????????????
            player!!.setDataSource(fileDescriptor.fileDescriptor,
                    fileDescriptor.startOffset,
                    fileDescriptor.length)
            //7 ??????????????????
            player!!.setLooping(true)
            //8 ????????????
            player!!.setOnPreparedListener(MediaPlayer.OnPreparedListener { //9 ???????????????????????????
                //  mMediaPlayer.start()
                player!!.setVolume(0.5f, 0.5f);
            })
            //10 ????????????
            player!!.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private val mAudioFocusChange: AudioManager.OnAudioFocusChangeListener = object : AudioManager.OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    //?????????????????????,?????????????????????????????????AUDIOFOCUS_GAIN??????
                    //???????????????????????????????????????QQ???????????????????????????
                    //???????????????????????????????????????????????????????????????????????????????????????????????????
                    Log.d(TAG, "AUDIOFOCUS_LOSS")
                    //stop()
                    //????????????????????????????????????????????????????????????
                    //???????????????????????????????????????????????????
                    //mAudioManager.abandonAudioFocus(this)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    //?????????????????????????????????????????????AUDIOFOCUS_GAIN_TRANSIENT???AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE??????
                    //?????????????????????????????????????????????????????????????????????
                    //??????????????????????????????
                    // stop()
                    player!!.setVolume(0.2f, 0.2f);
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT")
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->                     //???????????????????????????????????????
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                AudioManager.AUDIOFOCUS_GAIN -> {
                    //??????????????????????????????????????????????????????????????????
                    //?????????????????????
                    player!!.setVolume(0.5f, 0.5f);
                    Log.d(TAG, "AUDIOFOCUS_GAIN")
                    // start()
                }
            }
        }
    }


    private fun showTip(str: String) {
        /*  mToast.setText(str);
        mToast.show();*/
    }

    var readList = ConcurrentHashMap<Int, String>()


    private fun playCount(sec: Int) {
        //???????????????????????????????????????
        if (!SpeakUtil.mTts!!.isSpeaking) {
            //?????????5????????????????????????
            UserAcacheUtil.saveRopeTime(sec)
            playtypeCount()
        }
    }


    fun playStartOrEndSpeak(isStart: Boolean) {

        Logger.myLog("playStartOrEndSpeak isStart=" + isStart)

        var ropetimeOpen: Boolean by Preference(Preference.ROPE_TIME_OPEN, true)
        var ropeCountOpen: Boolean by Preference(Preference.ROPE_COUNT_OPEN, false)
        var ropeHrOpen: Boolean by Preference(Preference.ROPE_Hr_OPEN, true)
        if (ropetimeOpen || ropeCountOpen || ropeHrOpen) {
            //?????????5????????????????????????
            if (isStart) {
                speakUti.startSpeaking(UIUtils.getString(R.string.rope_speed_start), true)
            } else {
                speakUti.startSpeaking(UIUtils.getString(R.string.rope_speed_end), true)
            }
        }
    }

    private fun playHr() {
        //???????????????????????????????????????
        if (!SpeakUtil.mTts!!.isSpeaking) {
            if (UserAcacheUtil.isHrRemind()) {
                //?????????5????????????????????????
                UserAcacheUtil.saveHrRemind()
                appstartSpeakking(UIUtils.getString(R.string.below_the_hr_tips))
            }
        }
    }


    fun appstartSpeakking(content: String) {
        speakUti.startSpeaking(content, false)

    }

    /**
     * ????????????????????????????????????
     *
     */

    fun setDeviceDef() {
        if (!isSettingSuccess && AppConfiguration.isConnected && AppConfiguration.currentConnectDevice != null && AppConfiguration.currentConnectDevice.deviceType == JkConfiguration.DeviceType.ROPE_SKIPPING) {
            if (bean != null) {
                Logger.myLog("initData:" + bean)
                /**
                 *       type [0,1,2] ????????????[??????,?????????,?????????]
                min sec ?????????????????? ??? ???
                c1 c2 ???????????? ???8??? ???8???
                pk pk??????(0 ????????????,
                1 ??????pk(pk?????????type??????),
                2 ??????pk(???????????????type=2, ???c1 c2????????????????????????))
                 */
                if (bean!!.achieveSecond == 0) {
                    ISportAgent.getInstance().requestBle(BleRequest.rope_set_state, 2, 0, 0, bean!!.achieveNum, 1)
                } else {
                    min = bean!!.achieveSecond / 60
                    secd = bean!!.achieveSecond % 60
                    ISportAgent.getInstance().requestBle(BleRequest.rope_set_state, 2, min, secd, bean!!.achieveNum, 2)
                }
            } else {
                //?????????????????????
                ISportAgent.getInstance().requestBle(BleRequest.rope_set_state_nopar, currentType)
            }
        }
        handler.postDelayed({
            ISportAgent.getInstance().requestBle(BleRequest.Watch_W516_SET_USER, BaseManager.mYear, BaseManager.mMonth, BaseManager.mDay, BaseManager.mSex, BaseManager.mWeight, BaseManager.mHeight.toInt(), 22, 22)

        }, 1000)
    }


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_TIME_CHANGED || action == Intent.ACTION_TIMEZONE_CHANGED) {
            } else if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
                if (state == BluetoothAdapter.STATE_ON) {
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    if (sportBean!!.isStart) {
                        disConDialog()
                    }
                    //????????????????????????????????????,???????????????????????????????????????????????????????????????????????????
                    Logger.myLog("BluetoothAdapter.STATE_OFF")
                    //??????
                    //gattClose();
                    //?????????????????????
                }
            } else if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                val dv = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val key = intent.getStringExtra(BluetoothDevice.EXTRA_PAIRING_KEY)
                if (bondState == BluetoothDevice.BOND_NONE) { //??????????????????????????????????????????????????????
                }
            }
        }
    }

    private fun register() {
        try {
            val filter = IntentFilter()
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            this.registerReceiver(broadcastReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.myLog(e.toString())
        }
    }

    fun unRegister() {
        try {
            this.unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun showRopeIng(isRope : Boolean){
        if(isClickStart){   //?????????

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (event != null) {  //?????????????????????
            if(event.action == 0 && sportBean!!.isStart){
                return false
            }

        }
        return super.onKeyDown(keyCode, event)
    }
}
