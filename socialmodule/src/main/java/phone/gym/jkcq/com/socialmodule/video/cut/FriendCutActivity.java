package phone.gym.jkcq.com.socialmodule.video.cut;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.ImmersionBar;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import brandapp.isport.com.basicres.BaseActivity;
import phone.gym.jkcq.com.socialmodule.FriendConstant;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.community.ActivitySendDynamic;
import phone.gym.jkcq.com.socialmodule.util.UriUtil;

//视频裁剪页面
public class FriendCutActivity extends BaseActivity implements VideoTrimListener {

    private VideoTrimmerView trimmer_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildDialog();
        //StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.common_blank), true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_cut;
    }

    @Override
    protected void initView(View view) {
        trimmer_view = view.findViewById(R.id.trimmer_view);
        trimmer_view.setOnTrimVideoListener(this);
    }

    @Override
    protected void initData() {

        String path = getIntent().getStringExtra(FriendConstant.VIDEO_PATH);
//       String path="/data/user/0/com.isport.brandapp/files/video.mp4";
        if (TextUtils.isEmpty(path)) {
            ToastUtils.showLong("没有找到数据");
            finish();
            return;
        }

//       Log.e("FriendCameraActivity","path="+path+" Uripath="+Uri.parse(path).getPath()+" Scheme="+Uri.parse(path).getScheme());
//        Log.e("FriendCameraActivity","path1="+ getUriForFile(path).getPath()+" Scheme="+ getUriForFile(path));
//   trimmer_view.initVideoByURI(Uri.parse(path));
        trimmer_view.initVideoByURI(UriUtil.sUri);
//        trimmer_view.initVideoByURI(getUriForFile(path));
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        trimmer_view.onVideoPause();
        trimmer_view.setRestoreState(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        trimmer_view.onDestroy();
    }

    @Override
    public void onStartTrim() {
//        buildDialog(getResources().getString(R.string.trimming)).show();
        mProgressDialog.show();
    }

    @Override
    public void onTriming(int progress) {
        mProgressDialog.setProgress(progress);
    }

    @Override
    public void onFinishTrim(String url) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        ToastUtils.showLong(getString(R.string.trimmed_done));

        Intent intent = new Intent(FriendCutActivity.this, ActivitySendDynamic.class);
        intent.putExtra(FriendConstant.VIDEO_PATH, url);
        startActivity(intent);

        finish();
    }

    @Override
    public void onCancel() {
        trimmer_view.onDestroy();
        finish();
    }

    @Override
    public void onFailed() {
        ToastUtils.showShort("裁剪失败，请重新裁剪");
        mProgressDialog.dismiss();
    }

    private ProgressDialog mProgressDialog;

    private void buildDialog() {
        mProgressDialog = new ProgressDialog(FriendCutActivity.this);
//设置标题
//        mProgressDialog.setTitle("我是加载框");
//设置提示信息
        mProgressDialog.setMessage(getResources().getString(R.string.trimming));
//设置ProgressDialog 是否可以按返回键取消；
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
//显示ProgressDialog
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.friend_layer_bar_bg));
        mProgressDialog.setMax(100);
    }

    private ProgressDialog buildDialog(String msg) {

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.setMax(100);
        return mProgressDialog;
    }

    public Uri getUriForFile(String path) {
        File file = new File(path);
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .init();
    }
}
