package brandapp.isport.com.basicres.commonpermissionmanage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.isport.blelibrary.utils.Logger;
import com.isport.brandapp.basicres.R;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.annotation.NonNull;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;
import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonutil.StringUtil;
import io.reactivex.functions.Consumer;


/**
 * Created by on 2017/12/4 0004.
 *
 * @author
 */
public class PermissionManageUtil {
    Context mContext;
    private OnGetPermissionListener permissionListener;

    /**
     * 监听进过设置里边修改权限
     *
     * @param setPermissionListener
     */
    public void setSetPermissionListener(OnSetPermissionListener setPermissionListener) {
        this.setPermissionListener = setPermissionListener;
    }

    private OnSetPermissionListener setPermissionListener;

    public PermissionManageUtil(Context context) {
        mContext = context;
    }

    private static final String TAG = "PermissionManageUtil";

    /**
     * @param rxPermission
     * @param
     * @param desc               对于请求该权限的描述
     * @param permissionListener
     */
    public void requestPermissions(RxPermissions rxPermission, final String permissions, final String desc,
                                   final OnGetPermissionListener permissionListener) {
        boolean granted = rxPermission.isGranted(Manifest.permission.ACCESS_FINE_LOCATION);
        Logger.myLog(granted ? "有权限" : "没有权限");
        //如果版本是低于6.0 那么则不需要运行时申请权限 直接走有权限的逻辑
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionListener.onGetPermissionYes();
            return;
        } else {
            //如果是可以申请运行时的权限的时候，申请权限的内容没有的情况下 则返回申请权限没有获取到
            if (TextUtils.isEmpty(permissions)) {
                permissionListener.onGetPermissionNo();
                return;
            }
        }
        rxPermission
                .requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Logger.myLog("用户已经同意该权限");
                            processingResults(rxPermission,permissions, true, permissionListener, desc);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            if (permission.name.equals(Manifest.permission.READ_PHONE_STATE)) {
                                // 用户拒绝了该权限，由于是必须要的权限 那么必须要弹窗提醒
                                Logger.myLog("用户拒绝了该权限，由于是必须要的权限 那么必须要弹窗提醒");
                                processingResults(rxPermission,permissions, true, permissionListener, desc);
                            } else {
                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                Logger.myLog("用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框");
                                processingResults(rxPermission,permissions, false, permissionListener, desc);
                            }
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Logger.myLog("用户拒绝了该权限，并且选中『不再询问』");
                            processingResults(rxPermission,permissions, true, permissionListener, desc);
                        }
                    }
                });

    }

    /**
     * 以权限组的形式申请，不需要写描述
     *
     * @param rxPermission
     * @param permissions
     * @param permissionListener
     */
    public void requestPermissionsGroup(RxPermissions rxPermission, String[] permissions,
                                        OnGetPermissionListener permissionListener) {
        //如果版本是低于6.0 那么则不需要运行时申请权限 直接走有权限的逻辑
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionListener.onGetPermissionYes();
            return;
        } else {
            //如果是可以申请运行时的权限的时候，申请权限的内容没有的情况下 则返回申请权限没有获取到
            if (permissions.length == 0) {
                permissionListener.onGetPermissionNo();
                return;
            }
        }
        String desc = "";
        if (permissions == PermissionGroup.STORAGE) {
            desc = mContext.getResources().getString(R.string.permission_storage);
        } else if (permissions == PermissionGroup.CONTACTS) {
            desc = mContext.getResources().getString(R.string.permission_contacts);
        } else if (permissions == PermissionGroup.LOCATION) {
            desc = mContext.getResources().getString(R.string.permission_location);
        } else if (permissions == PermissionGroup.PHONE) {
            desc = mContext.getResources().getString(R.string.permission_phone);
        } else if (permissions == PermissionGroup.CAMERA) {
            desc = mContext.getResources().getString(R.string.permission_photograph);
        } else if (permissions == PermissionGroup.MICROPHONE) {
            desc = mContext.getResources().getString(R.string.permission_recording);
        } else if (permissions == PermissionGroup.SHORTCUT) {
            desc = mContext.getResources().getString(R.string.permission_shortcut);
        } else if (permissions == PermissionGroup.CAMERA_STORAGE) {
            desc = mContext.getResources().getString(R.string.permission_storage_camera);
        }
        requestPermissionsGroup(rxPermission, permissions, desc, permissionListener);
    }

    /**
     * 预定义权限组申请
     *
     * @param rxPermission
     * @param
     * @param permissionListener
     */
    private void requestPermissionsGroup(RxPermissions rxPermission, final String[] permissions, final String desc,
                                         final OnGetPermissionListener permissionListener) {
        rxPermission
                .requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permissions[permissions.length - 1].equals(permission.name)) {
                            if (permission.granted) {
                                // 用户已经同意该权限
                                processingResults(rxPermission,permissions, true, permissionListener, desc);
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                //如果是一定要给的权限 比如说是存储权限 那么不给就一定要弹框
                                if (permissions == PermissionGroup.STORAGE) {
                                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                    processingResults(rxPermission,permissions, true, permissionListener, desc);
                                } else {
                                    processingResults(rxPermission,permissions, false, permissionListener, desc);

                                }
                            } else {
                                // 用户拒绝了该权限，并且选中『不再询问』
                                processingResults(rxPermission,permissions, true, permissionListener, desc);


                            }
                        }
                    }
                });
    }

    /**
     * 处理获取权限返回数据
     *
     * @param deniedPermissions 勾选不再提醒 为true，此时需要给出弹框提醒用户
     */
    private void processingResults(RxPermissions rxPermission,String deniedPermissions, boolean noMoreInquiries,
                                   OnGetPermissionListener permissionListener, String desc) {
        if (hasPermission(mContext, deniedPermissions)) {
            // TODO 执行拥有权限时的下一步。
            Logger.myLog("执行拥有权限时的下一步");
            permissionListener.onGetPermissionYes();
        } else {
            // 第二种：用自定义的提示语。
            if (noMoreInquiries) {
                Logger.myLog("弹dialog");
                // TODO: 2019/2/19 对申请的权限做判断，查看系统是否已经打开了
//                if (rxPermission.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Manifest.permission.ACCESS_FINE_LOCATION
                    //有权限
//                    if (permissionListener != null)
//                        permissionListener.onGetPermissionYes();
//                } else {
                    //没有权限
                    dialog(desc, permissionListener);
//                }
            } else {
                if (permissionListener != null)
                    permissionListener.onGetPermissionNo();
//                ToastUtil.showMessage(desc);
            }
        }
    }

    /**
     * 处理获取权限返回数据
     *
     * @param deniedPermissions
     * @param noMoreInquiries   勾选不再提醒 为true，此时需要给出弹框提醒用户
     */
    private void processingResults(RxPermissions rxPermission,String[] deniedPermissions, boolean noMoreInquiries,
                                   OnGetPermissionListener permissionListener, String desc) {
        if (hasPermissionGroup(mContext, deniedPermissions)) {
            // TODO 执行拥有权限时的下一步。
            if (permissionListener != null)
                permissionListener.onGetPermissionYes();
        } else {
            // 第二种：用自定义的提示语。
            if (noMoreInquiries) {
//                dialog(desc, permissionListener);
            } else {
                if (permissionListener != null)
                    permissionListener.onGetPermissionNo();
            }
        }
    }

    /**
     * 判断单个权限是否拥有
     *
     * @param context
     * @param permission
     * @return
     */
    public boolean hasPermission(@NonNull Context context, @NonNull String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        int result = ContextCompat.checkSelfPermission(context, permission);
        if (result == PackageManager.PERMISSION_DENIED) {
            Log.e(TAG, "PERMISSION_DENIED");
            return false;
        }

        String op = AppOpsManagerCompat.permissionToOp(permission);
        if (!TextUtils.isEmpty(op)) {
            result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result != AppOpsManagerCompat.MODE_ALLOWED) {
                Log.e(TAG, "MODE_ALLOWED");
                return false;
            }
        }
        return true;
    }

    /**
     * 以权限组的形式判断是否拥有权限
     *
     * @param context           {@link Context}.
     * @param deniedPermissions one or more permissions.
     * @return true, other wise is false.
     */
    public boolean hasPermissionGroup(
            @NonNull Context context,
            @NonNull String[] deniedPermissions) {

        if (deniedPermissions.length == 0) {
            return false;
        }

        for (String permission : deniedPermissions) {
            boolean has = hasPermission(context, permission);
            if (!has) {
                return false;
            }
        }
        return true;
    }

    private String lastDesc;

    /**
     * 因为用户标记不在询问 获取权限 所以需要弹框提醒
     *
     * @param desc
     */
    public void dialog(String desc, final OnGetPermissionListener permissionListener) {
        if (lastDesc != null && lastDesc.equals(desc)) {
            return;
        }
        if (StringUtil.isBlank(desc)) {
            return;
        }
        PublicAlertDialog.getInstance().showDialogWithContentAndTitle(mContext.getString(R.string.permission_tips),
                desc, mContext, mContext.getString(R.string.permission_gosetting), mContext.getString(R.string.common_dialog_cancel), new
                        AlertDialogStateCallBack() {

                            @Override
                            public void determine() {
                                goSetting();
//                                                                                      PermissionSettingPage.start
//                                                                                              (mContext, false);
                                lastDesc = null;

//                                                                                      if (setPermissionListener !=
// null)
//                                                                                          setPermissionListener
//
// .onSetPermissionNo();
                                if (permissionListener != null)
                                    permissionListener
                                            .onGetPermissionNo();
                            }

                            @Override
                            public void cancel() {
                                Log.e(TAG, "cancel");
                                lastDesc = null;
                                if (permissionListener != null)
                                    permissionListener
                                            .onGetPermissionNo();
                            }
                        });
        lastDesc = desc;
    }

    public void goSetting() {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
            intent.setData(uri);
            mContext.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static interface OnGetPermissionListener {
        void onGetPermissionYes();

        void onGetPermissionNo();
    }

    public static interface OnSetPermissionListener {
        //跳到设置中之后设置权限给一个回调
        void onSetPermissionNo();
    }
}
