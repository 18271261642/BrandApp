package com.isport.brandapp.device.f18.dial;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.htsmart.wristband2.dial.DialDrawer;
import com.htsmart.wristband2.dial.DialWriter;
import com.isport.blelibrary.interfaces.OnF18DialStatusListener;
import com.isport.blelibrary.managers.F18SyncStatus;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;
import com.isport.brandapp.device.f18.view.F18CusDialSelectDialogView;
import com.isport.brandapp.device.f18.view.F18DialStyleDialogView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.BaseFragment;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import phone.gym.jkcq.com.commonres.commonutil.BitmapUtils;
import phone.gym.jkcq.com.commonres.commonutil.PhotoChoosePopUtil;

/**
 * 自定义表盘
 * Created by Admin
 * Date 2022/2/9
 */
public class CustomizeF18DialFragment extends BaseFragment {

    private static final String TAG = "CustomizeF18DialFragment";

    private static final int CHOOSE_ALBUM_RESULT_CODE = 0x00;
    private static final int PHOTO_CUT_CODE = 0x01;

    private String localDialFilePath ;

    //裁剪保存的路径
    private String cropImgPath;

    private Button f18DialTxtStyleBtn;

    //开始设置表盘
    private TextView setCusDialBtn;

    private F18DialStyleDialogView f18DialStyleDialogView;

    private RecyclerView localRecyclerView;
    private DialCenterAdapter dialCenterAdapter;
    private List<F18DialBean> dialList;

    private PhotoChoosePopUtil photoChoosePopUtil;

    private File selectTmpFile;

    private F18CusDialSelectDialogView f18CusDialSelectDialogView;

    private ImageView showTmpImgView;

    //字体的样式下标0~4
    private int txtStylePosition = 0;
    //提示是否删除的dialog
    private AlertDialog.Builder deleteAlert;

    public static CustomizeF18DialFragment getInstance(){
        return new CustomizeF18DialFragment();
    }



    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == -1){
                String delePath = (String) msg.obj;
                readLocalFile();
            }

            if(msg.what == 0x00){
                ArrayList<File> ltList = (ArrayList<File>) msg.obj;

               // Log.e("TAG","-------查询的图片="+ltList.size());

                if(ltList != null && ltList.size()>0){
                    for(File f : ltList){
                        F18DialBean fb = new F18DialBean();
                        fb.setStatus(0);
                        fb.setPreviewImgUrl(f.getAbsolutePath());
                        dialList.add(fb);
                    }
                }
                F18DialBean f18DialBean = new F18DialBean();
                f18DialBean.setPreviewImgUrl(R.drawable.icon_f18_diai_add+"");
                f18DialBean.setStatus(-1);
                dialList.add(dialList.size()==0 ? 0 : dialList.size(),f18DialBean);
                dialCenterAdapter.notifyDataSetChanged();
            }

            if(msg.what == 0x01){
                readLocalFile();
            }

            if(msg.what == 0x08){
                if(getActivity() == null || getActivity().isFinishing())
                    return;
                try {
                    File ft = (File) msg.obj;
                    Log.e("TAG","-----生成的bin="+ft.getName()+" "+ft.getAbsolutePath());

                    Watch7018Manager.getWatch7018Manager().setF18DialToDevice(ft, (byte) 0x00, new OnF18DialStatusListener() {
                        @Override
                        public void startDial() {

                        }

                        @Override
                        public void onError(int errorType, int errorCode) {
                            if(f18CusDialSelectDialogView != null)
                                f18CusDialSelectDialogView.setSyncStatus(getResources().getString(R.string.device_upgrade_fail));
                        }

                        @Override
                        public void onStateChanged(int state, boolean cancelable) {

                        }

                        @Override
                        public void onProgressChanged(int progress) {
                            if(f18CusDialSelectDialogView != null)
                                f18CusDialSelectDialogView.setSyncStatus(String.format(getResources().getString(R.string.device_upgrade_present),progress+""));
                        }

                        @Override
                        public void onSuccess() {
                            if(f18CusDialSelectDialogView != null)
                                f18CusDialSelectDialogView.setSyncStatus(getResources().getString(R.string.device_upgrade_success));
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    };



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cus_f18_dial_layout;
    }

    @Override
    protected void initView(View view) {
        findViews(view);

        localRecyclerView = view.findViewById(R.id.f18CusDialRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        localRecyclerView.setLayoutManager(gridLayoutManager);
        dialList = new ArrayList<>();
        dialCenterAdapter = new DialCenterAdapter(dialList,getContext());
        localRecyclerView.setAdapter(dialCenterAdapter);

        dialCenterAdapter.setOnF18ItemClickListener(new OnF18ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(position == dialList.size()-1){  //最后一个图片
                    showPhotoChoosePop();
                }else{
                  showChooseDialog(dialList.get(position),position);
                }
            }

            @Override
            public void onChildClick(int position, boolean isCheck) {

            }

            @Override
            public void onLongClick(int position) {

            }
        });

    }



    @Override
    protected void initData() {

        XXPermissions.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> list, boolean b) {

            }
        });

       //Watch7018Manager.getWatch7018Manager().readDeviceDial();

        localDialFilePath = Environment.getExternalStorageDirectory().getPath()+"/Download/";
        cropImgPath = Environment.getExternalStorageDirectory().getPath()+"/Download/Bonlala";


        File f = new File(cropImgPath);
        if(!f.exists())
            FileUtil.createDir(cropImgPath);

        readLocalFile();
    }

    //位置选择的弹框
    private void showChooseDialog(F18DialBean f18DialBean,int position){
        if(getActivity() == null)
            return;
        if(f18CusDialSelectDialogView == null)
            f18CusDialSelectDialogView = new F18CusDialSelectDialogView(getContext(),R.style.myDialog);
        f18CusDialSelectDialogView.show();
        f18CusDialSelectDialogView.setSyncStatus(getResources().getString(R.string.string_set_dial));
        f18CusDialSelectDialogView.setSrcResources(f18DialBean.getPreviewImgUrl());
        f18CusDialSelectDialogView.setStyleSmallBg(txtStylePosition);
        f18CusDialSelectDialogView.setCancelable(false);

        f18CusDialSelectDialogView.setOnF18ItemClickListener(new OnF18ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                f18CusDialSelectDialogView.setBgShowOrGone(position);
            }

            //删除
            @Override
            public void onChildClick(int position, boolean isCheck) {
                if(Watch7018Manager.getWatch7018Manager().getF18SyncStatus() == F18SyncStatus.SYNC_DIAL_ING){
                    ToastUtil.showTextToast(getResources().getString(R.string.string_upgrad_so_on));
                    return;
                }
                alertDelete(f18DialBean);
            }

            //开始设置表盘
            @Override
            public void onLongClick(int position) {
                if(Watch7018Manager.getWatch7018Manager().getF18SyncStatus() == F18SyncStatus.SYNC_DIAL_ING){
                    ToastUtil.showTextToast(getResources().getString(R.string.string_upgrad_so_on));
                    return;
                }
                startSetCusDial(f18DialBean.getPreviewImgUrl(),position);
            }
        });


    }

    //提示是否删除
    private void alertDelete(F18DialBean fileUrlBean){
        File f = new File(fileUrlBean.getPreviewImgUrl());
            AlertDialog.Builder deleteAlert = new AlertDialog.Builder(getContext())
                    .setTitle(getResources().getString(R.string.tips))
                    .setMessage(getResources().getString(R.string.string_alert_delete))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(f18CusDialSelectDialogView != null){
                                f18CusDialSelectDialogView.dismiss();
                            }
                            dialog.dismiss();
                            boolean isGetP = XXPermissions.isPermanentDenied(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if(isGetP){
                                Toast.makeText(getActivity(),getResources().getString(R.string.string_no_read_permission),Toast.LENGTH_SHORT).show();
                                return;
                            }
                            FileUtil.deleteFile(f);
                            readLocalFile();
//
                        }
                    }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if(f18CusDialSelectDialogView != null){
                                f18CusDialSelectDialogView.show();
                            }
                        }
                    });
        AlertDialog alertDialog = deleteAlert.show();
        Button sureBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button cancelBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        sureBtn.setTextColor(getResources().getColor(R.color.main_number_color));
        cancelBtn.setTextColor(getResources().getColor(R.color.main_number_color));
    }


    private DialDrawer.Position getPosition(int position){
        if(position == 0)
            return DialDrawer.Position.TOP;
        if(position == 1){
            return DialDrawer.Position.LEFT;
        }
        if(position == 2)
            return DialDrawer.Position.RIGHT;
        if(position == 3)
            return DialDrawer.Position.BOTTOM;
        return DialDrawer.Position.BOTTOM;
    }



    private void startSetCusDial(String bgUrl,int position){
        try {

          String fileS = localDialFilePath+"dial.bin";
          File file = new File(fileS);
          if(!file.isFile()){
              ToastUtil.init(getActivity());
              ToastUtil.showTextToast(getResources().getString(R.string.string_curr_no_file));
              return;
          }
          if(f18CusDialSelectDialogView != null)
              f18CusDialSelectDialogView.setSyncStatus(getResources().getString(R.string.string_upgrad_so_on));
          //背景的bitmap
            Bitmap bgBitmap = BitmapFactory.decodeFile(bgUrl);

            //样式的bitmap
            Bitmap styleBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_f18_dial_style1);
            //创建所需的资源
            DialDrawer.Shape shape = DialDrawer.Shape.createRectangle(360,360,0);

            Bitmap backgroundBitmap = DialDrawer.createDialBackground(bgBitmap,shape,DialDrawer.ScaleType.fromId(0));
            //预览
            Bitmap previewBitmap = DialDrawer.createDialPreview(bgBitmap,styleBitmap,shape,DialDrawer.ScaleType.fromId(0), getPosition(position),360,360,360);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    DialWriter dialWriter = new DialWriter(file,backgroundBitmap,previewBitmap, getPosition(position),true);
                    File resultFile = new File(localDialFilePath,"tempb_"+file.getName());
                    dialWriter.setCopyFile(resultFile);
                    dialWriter.setAutoScalePreview(true);
                   // dialWriter.setAutoScaleBackground(true);
                    try {
                        File isEx = dialWriter.execute();

                        Message message  = handler.obtainMessage();
                        message.what = 0x08;
                        message.obj = isEx;
                        handler.sendMessage(message);
                    } catch (DialWriter.WriterException e) {
                        e.printStackTrace();
                        if(f18CusDialSelectDialogView != null)
                            f18CusDialSelectDialogView.setSyncStatus(getResources().getString(R.string.device_upgrade_fail));
                        Watch7018Manager.getWatch7018Manager().setF18SyncStatus(F18SyncStatus.SYNC_DIAL_FAIL);
                    }
                }
            }).start();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //读取目录的背景图片
    private void readLocalFile(){
        dialList.clear();
        dialCenterAdapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<File> localFList = FileUtil.refreshFileList(cropImgPath);
                Message message = handler.obtainMessage();
                message.what = 0x00;
                message.obj = localFList;
                handler.sendMessage(message);
            }
        }).start();
    }



    @Override
    protected void initEvent() {

    }

    @Override
    public void initImmersionBar() {

    }

    private void findViews(View view){

        showTmpImgView = view.findViewById(R.id.showTmpImgView);

        localRecyclerView = view.findViewById(R.id.f18CusDialRecyclerView);
        f18DialTxtStyleBtn = view.findViewById(R.id.f18DialTxtStyleBtn);
        f18DialTxtStyleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTxtStyle();
            }
        });


    }

    //选择样式
    private void showTxtStyle(){
        if(getActivity() == null)
            return;
        if(f18DialStyleDialogView == null)
            f18DialStyleDialogView = new F18DialStyleDialogView(getActivity(),R.style.myDialog);
        f18DialStyleDialogView.show();
        f18DialStyleDialogView.setOnStyleTxtListener(new OnF18ItemClickListener() {
            @Override
            public void onItemClick(int position) {     //字體样式颜色，0,1,2,3,4
                txtStylePosition = position;
                Watch7018Manager.getWatch7018Manager().setCusTxtColorStyle(0,new byte[]{(byte) position});
            }

            @Override
            public void onChildClick(int position, boolean isCheck) {

            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }

    //选择图片
    private void showPhotoChoosePop() {
        if (null == photoChoosePopUtil) {
            photoChoosePopUtil = new PhotoChoosePopUtil(context);
        }
        if(getActivity() == null)
            return;
        photoChoosePopUtil.show(getActivity().getWindow().getDecorView());
      //  photoChoosePopUtil.setTvCameraVisiable(false);
        photoChoosePopUtil.setPhotoTxt(getResources().getString(R.string.string_choose_pick));
        photoChoosePopUtil.setOnPhotoChooseListener(new PhotoChoosePopUtil.OnPhotoChooseListener() {
            @Override
            public void onChooseCamera() {
                checkCamera();

            }

            @Override
            public void onChoosePhotograph() {
               // checkFileWritePermissions();
                checkPermission();

            }
        });
    }

    //拍照
    private void checkCamera() {
        if(XXPermissions.isPermanentDenied(getActivity(),Manifest.permission.CAMERA)){
            XXPermissions.with(getActivity()).permission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> list, boolean b) {

                }
            });
        }else{
            openCamera();

//            ISCameraConfig config = new ISCameraConfig.Builder()
//                    .needCrop(true) // 裁剪
//                    .cropSize(1, 1, 360, 360)
//                    .build();
//            ISNav.getInstance().toCameraActivity(getActivity(), config, 0x08);

            //takePhoto();
         //   cameraPic();
        }
    }


    private void checkPermission(){
        XXPermissions.with(this).permission( Manifest.permission.CAMERA).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> list, boolean b) {

            }
        });

        XXPermissions.with(this).permission(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> list, boolean b) {

            }
        });

        boolean isGetP = XXPermissions.isPermanentDenied(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE);
        if(isGetP){
            Toast.makeText(getActivity(),getResources().getString(R.string.string_no_read_permission),Toast.LENGTH_SHORT).show();
            return;
        }

        chooseAlbumFile();
    }

    private File tempFile;

    /*
     * 从相机获取
     */
    public void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (FileUtil.isSDExists()) {
            String date = String.valueOf(System.currentTimeMillis());
            tempFile = new File(Environment.getExternalStorageDirectory().getPath()+"/DCIM/", date + ".jpg");
            if (!tempFile.exists()) {
                FileUtil.createFile(tempFile.getAbsolutePath());
            }

            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(context.getApplicationContext(),  "com.isport.brandapp.fileprovider", tempFile);
            } else {
                uri = Uri.fromFile(tempFile);
            }
            Logger.myLog(TAG, "uri=" + uri.getPath());
            Log.e("TAG", "----tempFile=" + tempFile.getName()+" "+tempFile.getPath());
            String path = BitmapUtils.getRealFilePath(context, uri);
            Logger.myLog(TAG, "tempFileRealFilePath" + path);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 88);
        }
    }




    private void chooseAlbumFile(){
        String savePath = Environment.getExternalStorageDirectory().getPath()+"/DCIM/";
        Matisse.from(CustomizeF18DialFragment.this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .captureStrategy(new CaptureStrategy(false,  "com.isport.brandapp.fileprovider",savePath))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(CHOOSE_ALBUM_RESULT_CODE);

    }



    /**
     * 获取uri
     *
     * @param context
     * @param file
     * @return
     */
    public Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, "com.isport.brandapp" + ".fileprovider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         Log.e("TAG","---requestCode="+requestCode+"---resultCode="+resultCode);

         if(requestCode == 88 && resultCode == Activity.RESULT_OK){ //系统相机拍照
             Uri uri = getUriForFile(getActivity(), tempFile);
             String path = BitmapUtils.getRealFilePath(context, uri);

             Log.e("TAG","--------相机拍照="+path+" "+uri);

             startPhotoZoom(uri);
         }



        if (requestCode == CHOOSE_ALBUM_RESULT_CODE && resultCode == Activity.RESULT_OK) {  //相册选择
            List<Uri> mSelected = Matisse.obtainResult(data);
            List<String> strList = Matisse.obtainPathResult(data);
            Log.e("TAG", "mSelected: " + mSelected+"\n"+new Gson().toJson(strList));

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
                startPhotoZoom(mSelected.get(0));
            }else{
                Uri uri = Uri.fromFile(new File(strList.get(0)));
                startPhotoZoom(uri);
            }

        }



        if(requestCode == 0x08 && resultCode == Activity.RESULT_OK){
            if(data == null){
                handler.removeMessages(0x01);
                return;
            }

            String path = data.getStringExtra("result"); // 图片地址
            Log.e("TAG","----拍照返回="+path);
            F18DialBean f18DialBean = new F18DialBean();
            f18DialBean.setPreviewImgUrl(path);
            f18DialBean.setStatus(0);
            showChooseDialog(f18DialBean,0);


        }


        if(requestCode == PHOTO_CUT_CODE && resultCode == Activity.RESULT_OK){
            try {

                // 得到图片的全路径
                Uri cropUri = data.getData();
                if(cropUri == null){

                    Log.e("TAG","------uir为空了="+cropImgPath);

                    F18DialBean f18DialBean = new F18DialBean();
                    f18DialBean.setPreviewImgUrl(cropImgPath);
                    f18DialBean.setStatus(0);
                    showChooseDialog(f18DialBean,0);
                    cropImgPath =  Environment.getExternalStorageDirectory().getPath()+"/Download/Bonlala";
                    readLocalFile();
                    return;
                }

                Log.e("TAG","----裁剪后uri="+cropUri.toString());
                cropImgPath = Environment.getExternalStorageDirectory().getPath()+"/Download/Bonlala/";
               // handler.sendEmptyMessageDelayed(0x01,100);
                String resultPath =  BitmapUtils.getRealFilePath(context, cropUri);
                readLocalFile();
                Log.e("TAG","---裁剪后的地址="+resultPath+" "+cropUri.toString());
                F18DialBean f18DialBean = new F18DialBean();
                f18DialBean.setPreviewImgUrl(resultPath);
                f18DialBean.setStatus(0);
                showChooseDialog(f18DialBean,0);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 调用系统裁剪
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        try {
            int size = (int) (ScreenUtils.getScreenWidth() * 1.0);
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags( Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //裁剪后输出路径
            if (FileUtil.isSDExists()) {
                String date = String.valueOf(System.currentTimeMillis());
                cropImgPath =cropImgPath + "/" + date + ".jpg";
                File  cutFile = new File(cropImgPath);
                if (!cutFile.exists()) {
                    FileUtil.createFile(cutFile.getAbsolutePath());
                }
                Uri cRui = Uri.fromFile(cutFile);

                if (Build.VERSION.SDK_INT >= 24) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if(getContext() == null){
                        Toast.makeText(BaseApp.getApp().getApplicationContext(),"getContext = null",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    cRui = FileProvider.getUriForFile(getContext(),
                            getContext().getPackageName() + ".fileprovider",
                            cutFile);
                }else{
                     cRui = Uri.fromFile(cutFile);
                }

                Log.e("TAG","-cRyd="+cRui.toString());

//                Uri cu = FileProvider.getUriForFile(getActivity(),"com.isport.brandapp.fileprovider",cutFile);
//                ClipData clipData = ClipData.newRawUri(null,cu);
//                intent.setClipData(clipData);
                //所有版本这里都这样调用
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cutFile));
//               intent.putExtra(MediaStore.EXTRA_OUTPUT,  getUriForFile(NewShareActivity.this,cutFile));
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,  uri);
                //输入图片路径
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");

                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);

                intent.putExtra("outputX", 360);
                intent.putExtra("outputY", 360);
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("return-data", false);

                startActivityForResult(intent, PHOTO_CUT_CODE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
