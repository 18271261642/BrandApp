package com.isport.brandapp.device.f18.dial;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.isport.blelibrary.interfaces.OnF18DialStatusListener;
import com.isport.blelibrary.managers.Watch7018Manager;
import com.isport.brandapp.App;
import com.isport.brandapp.R;
import com.isport.brandapp.device.f18.OnF18ItemClickListener;
import com.isport.brandapp.net.RetrofitClient;
import com.isport.brandapp.util.DownloadUtils;
import com.isport.brandapp.util.InitCommonParms;
import com.isport.brandapp.util.onDownloadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.BaseFragment;
import brandapp.isport.com.basicres.commonbean.BaseDbPar;
import brandapp.isport.com.basicres.commonbean.BaseUrl;
import brandapp.isport.com.basicres.mvp.NetworkBoundResource;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;

/**
 * 表盘中心，表盘市场
 * Created by Admin
 * Date 2022/2/9
 */
public class F18DialCenterFragment extends BaseFragment {

    private static final String TAG = "F18DialCenterFragment";

    private static  String DOWN_BIN_FILE_PATH ;

    private AlertDialog.Builder permissionDialog;


    public static F18DialCenterFragment getInstance(){

        return new F18DialCenterFragment();
    }


    private DialBottomDialogView dialBottomDialogView;


    private DialCenterAdapter dialCenterAdapter;
    private List<F18DialBean> dialList;
    private RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_f18_dial_center_layout;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.f18DialRecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        dialList = new ArrayList<>();
        dialCenterAdapter = new DialCenterAdapter(dialList,getContext());
        recyclerView.setAdapter(dialCenterAdapter);

        dialCenterAdapter.setOnF18ItemClickListener(new OnF18ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showPreviewDial(position);
            }

            @Override
            public void onChildClick(int position,boolean isCheck) {

            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }

    @Override
    protected void initData() {
        DOWN_BIN_FILE_PATH = Environment.getExternalStorageDirectory().getPath()+"/Download/";
        getDialListData();

        XXPermissions.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> list, boolean b) {

            }

        });

        File dialFile = new File(DOWN_BIN_FILE_PATH+"dial.bin");
        boolean isSaveDial =  dialFile.isFile();
        if(!isSaveDial)
            downLoadDial();

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void initImmersionBar() {

    }



    private void showPreviewDial(int index){
        dialBottomDialogView = new DialBottomDialogView(getContext());
        dialBottomDialogView.show();
        dialBottomDialogView.setCancelable(false);
        dialBottomDialogView.setPreviewUrl(dialList.get(index).getPreviewImgUrl());
        dialBottomDialogView.setCancelVisible(false);
        dialBottomDialogView.setOnF18DialViewClickListener(new DialBottomDialogView.OnF18DialViewClickListener() {
            @Override
            public void onSureClick(int position) {
                dialBottomDialogView.dismiss();
            }

            @Override
            public void onCancelClick(int position) {

            }

            @Override
            public void onOperateClick(int position) {
                downloadBinFile(dialList.get(index).getBinUrl(),dialList.get(index).getDialNum());
            }
        });
    }


    private void downloadBinFile(String url,String fileName){

       boolean isW = XXPermissions.isGranted(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(!isW){
            XXPermissions.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> list, boolean b) {
                    Log.e(TAG,"-------写权限="+b);
                }

                @Override
                public void onDenied(List<String> permissions, boolean never) {
                    if(never){
                        showPermissDialog();

                    }
                }
            }   );

            return;
        }
        dialBottomDialogView.setDialogBtnStatus(getResources().getString(R.string.string_download_so));
     DownloadUtils.getInstance().downBin(url, DOWN_BIN_FILE_PATH, fileName+".bin", new onDownloadListener() {
            @Override
            public void onStart(float length) {
                Log.e(TAG,"----onStart="+length);
            }

            @Override
            public void onProgress(float progress) {
                Log.e(TAG,"----onProgress="+progress);
                dialBottomDialogView.setDialogBtnStatus(String.format(getString(R.string.file_downlod_present),(progress * 100)+""));
            }

            @Override
            public void onComplete() {
                Log.e(TAG,"----onComplete--");
                dialBottomDialogView.setDialogBtnStatus(getResources().getString(R.string.string_download_complete));
                setDialToDevice(DOWN_BIN_FILE_PATH+fileName+".bin");

            }

            @Override
            public void onFail() {
                Log.e(TAG,"----onFail--");
                dialBottomDialogView.setDialogBtnStatus("下载失败，请重新下载");
            }
        });
    }


    private void setDialToDevice(String dialFile){
        if(dialFile == null)
            return;
        Log.e(TAG,"-----表盘路径="+dialFile);
        Watch7018Manager.getWatch7018Manager(getActivity()).setF18DialToDevice(new File(dialFile), (byte) 0, new OnF18DialStatusListener() {
            @Override
            public void startDial() {
                if(dialBottomDialogView != null)
                    dialBottomDialogView.setDialogBtnStatus(getResources().getString(R.string.string_upgrad_so_on));
            }

            @Override
            public void onError(int errorType, int errorCode) {
                if(dialBottomDialogView != null)
                    dialBottomDialogView.setDialogBtnStatus(getResources().getString(R.string.device_upgrade_fail)+errorCode+" "+errorType);
            }

            @Override
            public void onStateChanged(int state, boolean cancelable) {
                if(dialBottomDialogView != null)
                    dialBottomDialogView.setDialogBtnStatus(getResources().getString(R.string.string_upgrad_so_on));
            }

            @Override
            public void onProgressChanged(int progress) {
                if(dialBottomDialogView != null)
                    dialBottomDialogView.setDialogBtnStatus(String.format(getResources().getString(R.string.device_upgrade_present),progress+""));
            }

            @Override
            public void onSuccess() {
                if(dialBottomDialogView != null)
                    dialBottomDialogView.setDialogBtnStatus(getResources().getString(R.string.device_upgrade_success));
            }
        });
    }



    //获取表盘
    private void getDialListData(){
        new NetworkBoundResource<List<F18DialBean>>(){

            @Override
            public Observable<List<F18DialBean>> getFromDb() {
                return null;
            }

            @Override
            public Observable<List<F18DialBean>> getNoCacheData() {
                return null;
            }

            @Override
            public boolean shouldFetchRemoteSource() {
                return false;
            }

            @Override
            public boolean shouldStandAlone() {
                return false;
            }

            @Override
            public Observable<List<F18DialBean>> getRemoteSource() {
                InitCommonParms<List<F18DialBean>, BaseUrl, BaseDbPar> initCommonParms = new InitCommonParms<>();
                BaseUrl baseUrl = new BaseUrl();
                baseUrl.object = 7018;
                return (Observable<List<F18DialBean>>) RetrofitClient.getInstance().post(initCommonParms
                        .setPostBody
                                (!(App.appType() == App.httpType)).setBaseUrl(baseUrl).setType(JkConfiguration.RequstType.GET_F18_DEVICE_DIAL_LIST).getPostBody());

            }

            @Override
            public void saveRemoteSource(List<F18DialBean> remoteSource) {

            }
        }.getAsObservable().subscribe(new Observer<List<F18DialBean>>() {
            @Override
            public void onSubscribe(@NonNull Disposable disposable) {

            }

            @Override
            public void onNext(@NonNull List<F18DialBean> f18DialBeans) {
                dialList.clear();
                dialList.addAll(f18DialBeans);
                dialCenterAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(@NonNull Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }



    private void downLoadDial(){
        boolean isWrite = XXPermissions.isPermanentDenied(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(isWrite){
            XXPermissions.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> list, boolean b) {


                }
            });

            return;
        }

        DownloadUtils.getInstance().downBin("https://api.mini-banana.com/common_files/dial.bin", DOWN_BIN_FILE_PATH, "dial.bin", new onDownloadListener() {
            @Override
            public void onStart(float length) {
                Log.e("TAG","----onStart="+length);

            }

            @Override
            public void onProgress(float progress) {
                Log.e("TAG","----onProgress="+progress);

            }

            @Override
            public void onComplete() {
                Log.e("TAG","----onComplete--");


            }

            @Override
            public void onFail() {
                Log.e("TAG","----onFail--");

            }
        });
    }


    private void showPermissDialog(){
        permissionDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.tips))
                .setMessage("存储权限已拒绝，无法使用此功能，是否手动打开?")
                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        XXPermissions.startPermissionActivity(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                })
                .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog at = permissionDialog.create();
        at.show();
        Button sureBtn = at.getButton(DialogInterface.BUTTON_POSITIVE);
        Button cancelBtn = at.getButton(DialogInterface.BUTTON_NEGATIVE);

        sureBtn.setTextColor(Color.BLACK);
        cancelBtn.setTextColor(Color.BLACK);
    }
}
