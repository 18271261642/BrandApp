package com.isport.brandapp.login

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.isport.brandapp.R
import kotlinx.android.synthetic.main.activity_show_permission_layout.*
import no.nordicsemi.android.dfu.internal.manifest.Manifest
import test.Test2Activity

/**
 * 权限展示页面
 * Created by Admin
 *Date 2021/10/12
 */
class ShowPermissionActivity : AppCompatActivity(),View.OnClickListener {


    private var bluetoothAdapter : BluetoothAdapter?= null

    private val OPEN_GPS_CODE = 0x00
    private val REQUEST_PERMISSION_CODE = 0x01

    val instance by lazy {this}

    private val handle : Handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if(msg.what == 0x02){
                verifyPermission()
            }
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_permission_layout)

        initViews()

        verifyPermission()


    }

    private fun initViews() {
        permissionBleLayout.setOnClickListener(this)
        permissionGPSLayout.setOnClickListener(this)
        permissionLocalLayout.setOnClickListener(this)
        permissionCameraLayout.setOnClickListener(this)
        permissionReadWriteLayout.setOnClickListener(this)
        permissionPhoneStatusLayout.setOnClickListener(this)
        permissionContactLayout.setOnClickListener(this)
        showPermissionBtn.setOnClickListener(this)

        permissionPhoneStatusLayout.setOnLongClickListener {
            startActivity(Intent(this,Test2Activity::class.java))
            return@setOnLongClickListener true
        }
    }


    private fun verifyPermission(){
        //蓝牙状态
        val isBleOpen = isOpenBle()
        permissBleStatusImg.setImageResource(if (isBleOpen == true) R.drawable.ic_permission_done else R.drawable.ic_item_menu_click)

        //GPS状态
        val isGPSOpen = openGPSLocal(this)
        permissGPSStatusImg.setImageResource(if (isGPSOpen)R.drawable.ic_permission_done else R.drawable.ic_item_menu_click)

        //位置权限
        val isLocal = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        permissLocaStatusImg.setImageResource(if(isLocal)R.drawable.ic_permission_done else R.drawable.ic_item_menu_click)

        //相机使用权限
        val isCamera = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        permissCameraStatusImg.setImageResource(if(isCamera)R.drawable.ic_permission_done else R.drawable.ic_item_menu_click)

        //文件读写
        val isReadAdWrite = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        permissReadWriteStatusImg.setImageResource(if(isReadAdWrite)R.drawable.ic_permission_done else R.drawable.ic_item_menu_click)


        //电话权限
        val isPhone = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
        permissPhoneImg.setImageResource(if(isPhone)R.drawable.ic_permission_done else R.drawable.ic_item_menu_click)


        //联系人权限
        val isContact = ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        permissContactStatusImg.setImageResource(if(isContact)R.drawable.ic_permission_done else R.drawable.ic_item_menu_click)
    }




    override fun onClick(p0: View?) {
        if (p0 != null) {
            if(p0.id == R.id.permissionBleLayout){  //打开蓝牙开关
                if(isOpenBle() == false)
                    turnOnBlue(this,10 * 1000,0x03)
            }

            if(p0.id == R.id.permissionGPSLayout){      //GPS
                if(!openGPSLocal(this))
                    openGPS()

            }

            if(p0.id == R.id.permissionLocalLayout){    //位置
                requestPermission(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION))
            }

            if(p0.id == R.id.permissionCameraLayout){   //相机
                requestPermission(arrayOf(android.Manifest.permission.CAMERA))
            }

            if(p0.id == R.id.permissionReadWriteLayout){    //读写存储
                requestPermission(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }

            if(p0.id == R.id.permissionPhoneStatusLayout){     //电话权限
                requestPermission(arrayOf(android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.CALL_PHONE))

            }

            if(p0.id == R.id.permissionContactLayout){     //通讯录
                if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                    goToDeviceSetting()
                    return
                }
                requestPermission(arrayOf(android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.READ_CALL_LOG,android.Manifest.permission.CALL_PHONE))
            }


            if(p0.id == R.id.showPermissionBtn){
                val intent  = Intent()
                setResult(0x02,intent)
                finish()
            }

        }
    }

    //去设置页面
    private fun goToDeviceSetting(){
        val permiss  =  PermissionManageUtil(this)
        permiss.goSetting()

    }


    //请求权限
    private fun requestPermission(permissStr: Array<String>){
      //  ActivityCompat.requestPermissions(this, permissStr, REQUEST_PERMISSION_CODE)
        XXPermissions.with(instance).permission(permissStr).request { p0, p1 ->

        };
       // ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS),REQUEST_PERMISSION_CODE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ANSWER_PHONE_CALLS), REQUEST_PERMISSION_CODE)
        }

        handle.sendEmptyMessage(0x02)

    }

    //是否打开GPS开关
    private fun openGPSLocal(context: Context): Boolean {
        return try {
            val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    //判断蓝牙是否打开
    private fun isOpenBle(): Boolean? {
        val bleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bleManager.adapter
        return bluetoothAdapter?.isEnabled
    }

    //打开GPS
    private fun openGPS() {
        try {
            val intent = Intent()
            intent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            startActivityForResult(intent, OPEN_GPS_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //打开蓝牙
    fun turnOnBlue(activity: Activity, visiableTime: Int, requestCode: Int) {
        try {
            // 请求打开 Bluetooth
            val requestBluetoothOn = Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE)
            // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
            requestBluetoothOn.action = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE
            // 设置 Bluetooth 设备可见时间
            requestBluetoothOn.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    visiableTime)
            // 请求开启 Bluetooth
            activity.startActivityForResult(requestBluetoothOn,
                    requestCode)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handle.sendEmptyMessage(0x02)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handle.sendEmptyMessage(0x02)
    }

}