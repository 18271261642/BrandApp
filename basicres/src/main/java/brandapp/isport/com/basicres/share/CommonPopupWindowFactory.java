package brandapp.isport.com.basicres.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;
import com.isport.brandapp.basicres.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;

import androidx.core.content.FileProvider;

/**
 * created by wq on 2019/5/9
 */
public class CommonPopupWindowFactory {
    private volatile static CommonPopupWindowFactory sWindowFactory;

    public static CommonPopupWindowFactory getInstance() {
        if (sWindowFactory == null) {
            synchronized (CommonPopupWindowFactory.class) {
                if (sWindowFactory == null) {
                    sWindowFactory = new CommonPopupWindowFactory();
                }
            }
        }
        return sWindowFactory;
    }

    public void showShareWindow(Activity activity, ShareBean shareBean) {
        PopupWindow popupWindow = new PopupWindow(activity);
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
//        popupWindow.setWidth(screenWidth);
//        popupWindow.setHeight(screenHeight);
        View view = LayoutInflater.from(activity).inflate(R.layout.sharing_interface, null);
        ImageView iv_qq = view.findViewById(R.id.iv_qq);
        ImageView iv_weibo = view.findViewById(R.id.iv_weibo);
        ImageView iv_wechat = view.findViewById(R.id.iv_wechat);
        ImageView iv_friend = view.findViewById(R.id.iv_friend);
        ImageView iv_more = view.findViewById(R.id.iv_more);
//        View view_more = view.findViewById(R.id.line_more);
        iv_more.setVisibility(View.GONE);
//        view_more.setVisibility(View.GONE);
        TextView tv_cancel = view.findViewById(R.id.tv_sharing_cancle);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        iv_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
//                        .withMedia(ShareHelper.getUMweb(activity, shareBean))
//                        .setCallback(mUMShareListener)
//                        .share();
                popupWindow.dismiss();
                // shareImageFile(shareBean.getBitmap(),"qq",activity);
                shareImageFile(shareBean.bitmap, "qq", activity);
            }
        });
        iv_weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UMShareAPI.get(activity).isInstall(activity,SHARE_MEDIA.SINA)){
                    Toast.makeText(activity, activity.getResources().getString(R.string.string_share_notinstall_desc), Toast.LENGTH_SHORT).show();
                    return;
                }
//                new ShareAction(activity).setPlatform(SHARE_MEDIA.SINA)
//                        .withMedia(ShareHelper.getUMweb(activity, shareBean))
//                        .setCallback(mUMShareListener)
//                        .share();
                popupWindow.dismiss();
                shareImageFile(shareBean.bitmap, "weibo", activity);
            }
        });
        iv_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
//                        .withMedia(ShareHelper.getUMweb(activity, shareBean))
//                        .setCallback(mUMShareListener)
//                        .share();
                popupWindow.dismiss();
                shareImageFile(shareBean.bitmap, "wechat", activity);
            }
        });
        iv_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
//                        .withMedia(ShareHelper.getUMweb(activity, shareBean))
//                        .setCallback(mUMShareListener)
//                        .share();
                popupWindow.dismiss();
                shareImageFile(shareBean.bitmap, "friend", activity);
            }
        });
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow.dismiss();
//            }
//        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                shareFile(shareBean.getShareUrl(), activity);
                popupWindow.dismiss();
                shareImageFile(shareBean.bitmap, "more", activity);
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }


    public void shareImageFile(Bitmap bitmap, String type, Activity activity) {

        if (type.equals("more")) {
//            shareFile(file,activity);
        } else if (type.equals("facebook") || type.equals("twitter")) {
//            shareFaceBook();
        } else {
            SHARE_MEDIA share_media = SHARE_MEDIA.QQ;
            switch (type) {
                case "qq":
                    share_media = SHARE_MEDIA.QQ;
                    break;
                case "wechat":
                    share_media = SHARE_MEDIA.WEIXIN;

                    break;
                case "friend":
                    share_media = SHARE_MEDIA.WEIXIN_CIRCLE;

                    break;
                case "weibo":
                    share_media = SHARE_MEDIA.SINA;

                    break;

            }

            UMImage image = ShareHelper.getUMWeb(activity, bitmap);
            new ShareAction(activity).setPlatform(share_media)
                    .withMedia(image)
                    .setCallback(mUMShareListener)
                    .share();
           /* UMImage image = ShareHelper.getUMWeb(activity, bitmap);
            new ShareAction(activity).setPlatform(share_media)
                    .withMedia(image)
                   .setCallback(mUMShareListener)
                    .share();*/
        }
    }

    public void shareFile(File file, Activity activity) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri = null;
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // "项目包名.fileprovider"即是在清单文件中配置的authorities
                uri = FileProvider.getUriForFile(activity, "com.jkcq.gym.phone.fileProvider",
                        file);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType("image/*");//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(Intent.createChooser(share, "Share Image"));
        }
    }

    public void shareFile(String url, Context context) {

        Intent share = new Intent(Intent.ACTION_SEND);
        Uri uri = null;
        //Platformutil.isInstallApp(context, PlatformUtil.PACKAGE_WECHAT);
        // 判断版本大于等于7.0
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, url);
        share.setType("text/plain");//此处可发送多种文件
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // share.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
        context.startActivity(Intent.createChooser(share, "Share Image"));
    }

    public interface DismissListener {
        void dismiss();
    }

    UMShareListener mUMShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            // ToastUtils.showShort(" 分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            // ToastUtils.showShort(" 分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            // ToastUtils.showShort(" 分享失败");
        }
    };
}
