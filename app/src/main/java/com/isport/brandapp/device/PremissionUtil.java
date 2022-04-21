package com.isport.brandapp.device;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import brandapp.isport.com.basicres.commonpermissionmanage.PermissionGroup;
import brandapp.isport.com.basicres.commonpermissionmanage.PermissionManageUtil;
import brandapp.isport.com.basicres.commonutil.FileUtil;
import brandapp.isport.com.basicres.commonutil.ThreadPoolUtils;
import brandapp.isport.com.basicres.service.observe.NetProgressObservable;

public class PremissionUtil {

    /**
     * 打开相机和存储权限
     */
    File picFile;

    public void checkCameraPersiomm(Context context, FragmentActivity activity, ViewGroup layout, String type) {
        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
        permissionManage.requestPermissionsGroup(new RxPermissions(activity),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil.OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
//                        NetProgressObservable.getInstance().show("正在生成图片,请稍等");
                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                // ThreadPoolUtils.getInstance().addTask(new ShootTask(scrollView, ActivityScaleReport.this, ActivityScaleReport.this));
                                picFile = getFullScreenBitmap(layout, context);
                                if (onResultLister != null) {
                                    onResultLister.OnResultYes(picFile, type);
                                }
                                NetProgressObservable.getInstance().hide();
                                // initLuBanRxJava(getFullScreenBitmap(scrollView));
                            }
                        });

                    }

                    @Override
                    public void onGetPermissionNo() {
                        if (onResultLister != null) {
                            onResultLister.OnresultNo(null);
                        }

                    }
                });

    }



    public void checkCameraPersiomm(Context context, FragmentActivity activity, ViewGroup layout, String type,String saveImgUrl) {
        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
        permissionManage.requestPermissionsGroup(new RxPermissions(activity),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil.OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
//                        NetProgressObservable.getInstance().show("正在生成图片,请稍等");
                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                // ThreadPoolUtils.getInstance().addTask(new ShootTask(scrollView, ActivityScaleReport.this, ActivityScaleReport.this));
                                picFile = getFullScreenBitmap(layout, context,saveImgUrl);
                                if (onResultLister != null) {
                                    onResultLister.OnResultYes(picFile, type);
                                }
                                NetProgressObservable.getInstance().hide();
                                // initLuBanRxJava(getFullScreenBitmap(scrollView));
                            }
                        });

                    }

                    @Override
                    public void onGetPermissionNo() {
                        if (onResultLister != null) {
                            onResultLister.OnresultNo(null);
                        }

                    }
                });

    }







    public void checkNewShareCameraPersiomm(Context context, FragmentActivity activity, ViewGroup layout, String type) {
        PermissionManageUtil permissionManage = new PermissionManageUtil(context);
        permissionManage.requestPermissionsGroup(new RxPermissions(activity),
                PermissionGroup.CAMERA_STORAGE, new PermissionManageUtil.OnGetPermissionListener() {
                    @Override
                    public void onGetPermissionYes() {
//                        NetProgressObservable.getInstance().show("正在生成图片,请稍等");
                        ThreadPoolUtils.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                // ThreadPoolUtils.getInstance().addTask(new ShootTask(scrollView, ActivityScaleReport.this, ActivityScaleReport.this));
                                picFile = getNewShareFullScreenBitmap(layout, context);
                                if (onResultLister != null) {
                                    onResultLister.OnResultYes(picFile, type);
                                }
                                NetProgressObservable.getInstance().hide();
                                // initLuBanRxJava(getFullScreenBitmap(scrollView));
                            }
                        });

                    }

                    @Override
                    public void onGetPermissionNo() {
                        if (onResultLister != null) {
                            onResultLister.OnresultNo(null);
                        }

                    }
                });

    }


    /**
     * 获取长截图
     *
     * @return
     */
    public File getFullScreenBitmap(ViewGroup scrollVew, Context context) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollVew.getChildCount(); i++) {
            h += scrollVew.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
       /* bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h - DisplayUtils.dip2px(context, 80),
                Bitmap.Config.ARGB_8888);*/
        bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollVew.draw(canvas);
        // 测试输出
        return FileUtil.writeImage(bitmap, FileUtil.getImageFile(FileUtil.getPhotoFileName()), 100);
    }


    /**
     * 获取长截图
     *
     * @return
     */
    public File getFullScreenBitmap(ViewGroup scrollVew, Context context,String saveUrl) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollVew.getChildCount(); i++) {
            h += scrollVew.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
       /* bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h - DisplayUtils.dip2px(context, 80),
                Bitmap.Config.ARGB_8888);*/
        bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollVew.draw(canvas);
        // 测试输出
        return FileUtil.writeImage(bitmap,new File(saveUrl,FileUtil.getPhotoFileName()), 100);
    }


    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    public File getNewShareFullScreenBitmap(ViewGroup scrollVew, Context context) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollVew.getChildCount(); i++) {
            h += scrollVew.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
      /*  bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h - DisplayUtils.dip2px(context, 80),
                Bitmap.Config.ARGB_8888);*/
        bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollVew.draw(canvas);
        // 测试输出


        return FileUtil.writeImage(bitmap, FileUtil.getImageFile(FileUtil.getPhotoFileName()), 100);
    }


    public File getNewShareFullScreenBitmap(ViewGroup scrollVew, Context context,String savedPath) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < scrollVew.getChildCount(); i++) {
            h += scrollVew.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
      /*  bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h - DisplayUtils.dip2px(context, 80),
                Bitmap.Config.ARGB_8888);*/
        bitmap = Bitmap.createBitmap(scrollVew.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollVew.draw(canvas);
        // 测试输出


        return FileUtil.writeImage(bitmap, new File(savedPath, FileUtil.getPhotoFileName()), 100);
    }




    OnResult onResultLister;


    public void setOnResultLister(OnResult onResultLister) {
        this.onResultLister = onResultLister;
    }


    public interface OnResult {
        public void OnResultYes(File file, String type);

        public void OnresultNo(File file);
    }

}
