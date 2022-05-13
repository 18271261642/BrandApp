package phone.gym.jkcq.com.socialmodule.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Preview;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import brandapp.isport.com.basicres.BaseActivity;
import brandapp.isport.com.basicres.commonutil.Logger;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import phone.gym.jkcq.com.socialmodule.FriendConstant;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.community.ActivitySendDynamic;
import phone.gym.jkcq.com.socialmodule.util.CacheUtil;
import phone.gym.jkcq.com.socialmodule.util.UriUtil;
import phone.gym.jkcq.com.socialmodule.video.cut.CutUtil;
import phone.gym.jkcq.com.socialmodule.video.cut.FriendCutActivity;
import phone.gym.jkcq.com.socialmodule.video.ffmpeg.FfmpegSupport;
import phone.gym.jkcq.com.socialmodule.video.record.Option;
import phone.gym.jkcq.com.socialmodule.video.record.OptionView;

public class FriendCameraActivity extends BaseActivity implements OptionView.Callback {

    private LinearLayout layout_top, rl_bottom;
    private RadioButton btn_record;
    private RadioButton btn_photo;
    private ImageView iv_close;
    private ImageView iv_switch;
    private ImageView iv_full;
    private ImageView iv_start_video;
    private CameraView camera;
    private long mCaptureTime;
    private boolean isStart = false;
    private ProgressBar progress_value;
    private final static boolean USE_FRAME_PROCESSOR = true;
    private final static boolean DECODE_BITMAP = false;
    private static final int REQUEST_VIDEO_CODE = 2;

    private final String VEDIO_FULL = "VEDIO_FULL";
    private final String VEDIO_WRAP = "VEDIO_WRAP";
    private final String VEDIO_START = "start";
    private final String VEDIO_END = "end";
    private final String VEDIO_jump = "jump";

    private Handler mHandler = new Handler();
    private boolean isVideoValid = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_camera;
    }

    @Override
    protected void initView(View view) {
        layout_top = findViewById(R.id.layout_top);
        rl_bottom = findViewById(R.id.rl_bottom);
        btn_record = findViewById(R.id.btn_record);
        btn_photo = findViewById(R.id.btn_photo);
        iv_close = findViewById(R.id.iv_close);
        iv_switch = findViewById(R.id.iv_switch);
        iv_full = findViewById(R.id.iv_full);
        iv_full.setTag(VEDIO_FULL);
        progress_value = findViewById(R.id.progress_value);
        iv_start_video = findViewById(R.id.iv_start_video);
        // iv_start_video.setTag(VEDIO_START);
        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new Listener());

       /* Option option=new Option.VideoCodec();
        option.set(camera,"h264");*/

       /* Option height= new Option.Height();
        Option width=new Option.Width();

        width.set(camera,720);
        height.set(camera,1280);*/


        Log.e(TAG, camera.getPreviewFrameRate() + "");
        iv_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_full.getTag().equals(VEDIO_FULL)) {
                    iv_full.setTag(VEDIO_WRAP);
                    iv_full.setImageResource(R.drawable.icon_send_dynamic_wrap);
                    setCameraFull(camera.getWidth());
                } else if (iv_full.getTag().equals(VEDIO_WRAP)) {
                    iv_full.setTag(VEDIO_FULL);
                    iv_full.setImageResource(R.drawable.icon_send_dynamic_full);
                    setCameraFull(ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });
        iv_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCamera();
            }
        });
        iv_start_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (v.getTag().equals(VEDIO_START)) {
                    layout_top.setVisibility(View.GONE);
                    rl_bottom.setVisibility(View.GONE);
                    iv_start_video.setImageResource(R.drawable.icon_send_dynamic_vedio_end);
                    //可以点击结束
                    startTimer();
                    captureVideoSnapshot();
                    //设置3s以上拍摄才可结束
                    setVideoValid();
                    v.setTag(VEDIO_END);

                } else if (v.getTag().equals(VEDIO_END)) {
                    if (!isVideoValid) {
                        showToast(R.string.invalide_video_can_not_upload);
                    } else {
                        //结束视频拍摄
                        layout_top.setVisibility(View.VISIBLE);
                        rl_bottom.setVisibility(View.VISIBLE);
                        iv_start_video.setImageResource(R.drawable.icon_send_dynamic_start);
                        camera.stopVideo();
                        v.setTag(VEDIO_jump);
                    }
                }
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_start_video.setVisibility(View.VISIBLE);
//                /*Intent intent = new Intent(FriendCameraActivity.this, ActivitySendDynamic.class);
//                intent.putExtra(FriendConstant.VIDEO_PATH, "/data/user/0/com.isport.brandapp/files/video.mp4");
//                startActivity(intent);*/
//                if (!ViewMultiClickUtil.onMultiClick(v)) {
//                    if (!isStart) {
//                        captureVideoSnapshot();
//                        isStart = true;
//                    } else {
//                        isStart = false;
//                        camera.stopVideo();
//                    }
//                }

            }
        });
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, REQUEST_VIDEO_CODE);
                iv_start_video.setVisibility(View.GONE);
                Intent local = new Intent();
//                local.setType("video/*;image/*");
                local.setType("video/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, REQUEST_VIDEO_CODE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVideoValid = false;
    }

    @Override
    protected void initData() {
        buildDialog();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initHeader() {
        // StatusBarCompat.setStatusBarColor(getWindow(), getResources().getColor(R.color.transparent), true);

    }

    private void setVideoValid() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isVideoValid = true;
            }
        }, 3000);
    }

    /**
     * 开始视频录制
     */
    private void captureVideoSnapshot() {
        if (camera.isTakingVideo()) {
            // message("Already taking video.", false);
            return;
        }
        if (camera.getPreview() != Preview.GL_SURFACE) {
            // message("Video snapshots are only allowed with the GL_SURFACE preview.", true);
            return;
        }
//        message("Recording snapshot for 15 seconds...", true);
        Log.e(TAG, "takeVideoSnapshot");

        //设置最大录制时长
        camera.takeVideoSnapshot(new File(getFilesDir(), "video.mp4"), 15 * 1000);
//        camera.stopVideo();
    }

    @Override
    public <T> boolean onValueChanged(@NonNull Option<T> option, @NonNull T value, @NonNull String name) {
        if ((option instanceof Option.Width || option instanceof Option.Height)) {
            Preview preview = camera.getPreview();
            boolean wrapContent = (Integer) value == ViewGroup.LayoutParams.WRAP_CONTENT;
            if (preview == Preview.SURFACE && !wrapContent) {
                /*message("The SurfaceView preview does not support width or height changes. " +
                        "The view will act as WRAP_CONTENT by default.", true);*/
                return false;
            }
        }
        option.set(camera, value);
//        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
//        b.setState(BottomSheetBehavior.STATE_HIDDEN);
        //message("Changed " + option.getName() + " to " + name, false);
        return true;
    }

    private class Listener extends CameraListener {

        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
           /* Option frameRate = new Option.PreviewFrameRate();
            frameRate.set(camera, 20);
            camera.getVideoSize();*/
            Log.e(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            Log.e(TAG, "onCameraError");
            //message("Got CameraException #" + exception.getReason(), true);
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            Log.e(TAG, "onPictureTaken");
            if (camera.isTakingVideo()) {
                // message("Captured while taking video. Size=" + result.getSize(), false);
                return;
            }

            // This can happen if picture was taken with a gesture.
            long callbackTime = System.currentTimeMillis();
            if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
//            PicturePreviewActivity.setPictureResult(result);
//            Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
//            intent.putExtra("delay", callbackTime - mCaptureTime);
//            startActivity(intent);
//            mCaptureTime = 0;
        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);
            if (disposableTimer != null && !disposableTimer.isDisposed()) {
                disposableTimer.dispose();
            }
            progress_value.setProgress(0);
            Log.e(TAG, "onVideoTaken path=" + result.getFile().getAbsolutePath() + "  path1=" + result.getFile().getPath());

           /* Intent intent = new Intent(FriendCameraActivity.this, ActivitySendDynamic.class);
            intent.putExtra(FriendConstant.VIDEO_PATH, result.getFile().getAbsolutePath());
            startActivity(intent);*/
            //这里需要进行
            //

            runFFmpegRxJava(result.getFile().getAbsolutePath());
            mProgressDialog.show();
        }


        /**
         * rxjava方式调用
         */
        String mOutPath;
        private MyRxFFmpegSubscriber myRxFFmpegSubscriber;

        private void runFFmpegRxJava(String path) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    final String outputName = "trimmedVideo_compress" + timeStamp + ".mp4";
                    mOutPath = CacheUtil.getVideoCache() + "/" + outputName;
                    String videoRate = CutUtil.calBitRa(path);
                    String[] cmd;
                    cmd = FfmpegSupport.getCompressCommand(path, mOutPath,
                            videoRate);
                    myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
                    RxFFmpegInvoke.getInstance()
                            .runCommandRxJava(cmd)
                            .subscribe(myRxFFmpegSubscriber);
                }
            }, 500);
        }

        // 这里设为静态内部类，防止内存泄露
        public class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {


            public MyRxFFmpegSubscriber() {
            }

            @Override
            public void onFinish() {
//
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                ToastUtils.showLong(getString(R.string.trimmed_done));
                Intent intent = new Intent(FriendCameraActivity.this, ActivitySendDynamic.class);
                intent.putExtra(FriendConstant.VIDEO_PATH, mOutPath);
                startActivity(intent);
                // mOnTrimVideoListener.onFinishTrim(mOutPath);
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                if (progress > 0) {
                    // mOnTrimVideoListener.onTriming(progress / 2);
                    mProgressDialog.setProgress(progress);
                    //mOnTrimVideoListener.onTriming(progress );
                }
                Logger.e("VideoTrimmerView", "progress=" + progress);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(String message) {
                Logger.e("VideoTrimmerView", "onError=");
            }
        }


        @Override
        public void onVideoRecordingStart() {
            super.onVideoRecordingStart();
            Log.e(TAG, "onVideoRecordingStart");
        }

        @Override
        public void onVideoRecordingEnd() {
            super.onVideoRecordingEnd();
            Log.e(TAG, "onVideoRecordingEnd");
        }

        @Override
        public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);
            Log.e(TAG, "onExposureCorrectionChanged");
        }

        @Override
        public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
            Log.e(TAG, "onZoomChanged:" + newValue);
            // message("Zoom:" + newValue, false);
        }
    }

    private void message(@NonNull String content, boolean important) {
        if (important) {
            Toast.makeText(this, content, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode);
        if (requestCode == REQUEST_VIDEO_CODE) {

            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                UriUtil.sUri = uri;
                Logger.e(TAG, "uriScheme=" + uri.getScheme() + " path=" + UriUtil.getPath(this, uri));
                videoFun(uri);
            }
        }
    }

    Disposable disposableTimer;

    public void startTimer() {

        try {
            if (disposableTimer != null && !disposableTimer.isDisposed()) {
                disposableTimer.dispose();
            }
            disposableTimer = Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {

                @Override
                public void accept(Long aLong) throws Exception {


                    long progress = aLong;
                    progress_value.setProgress((int) progress + 1);

                    if (aLong == 14) {
                        disposableTimer.dispose();
                    }


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void videoFun(Uri uri) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                String videoPath = UriUtil.getPath(FriendCameraActivity.this, uri);
                Intent intent = new Intent(FriendCameraActivity.this, FriendCutActivity.class);
                intent.putExtra(FriendConstant.VIDEO_PATH, videoPath);
                startActivity(intent);
            }
        }, 500);

//        Logger.e(TAG, "videoPath=" + videoPath+" realPath="+ UriUtil.getPath(this,uri));
//        Cursor cursor = getContentResolver().query(uri,  null, null,  null, null);
//        if(cursor!=null){
//            cursor.moveToFirst();
//            String v_path = cursor.getString(1); // 图片文件路径
//            String v_size = cursor.getString(2); // 图片大小
//            String v_name = cursor.getString(3); // 图片文件名
//            Log.e(TAG,"v_path="+v_path);
//            Log.e(TAG,"v_size="+v_size);
//            Log.e(TAG,"v_name="+v_name);
//        }
//        else{
//            Log.e(TAG,"cursor is null");
//        }
//        // String imgNo = cursor.getString(0); // 图片编号

    }

    private void mediaFun(Uri uri) {
        String[] filePathColumn = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String videoPath = cursor.getString(columnIndex);
        cursor.close();
        Intent intent = new Intent(FriendCameraActivity.this, FriendCutActivity.class);
        intent.putExtra(FriendConstant.VIDEO_PATH, videoPath);
        startActivity(intent);
        Logger.e(TAG, "videoPath=" + videoPath);
    }


    private void toggleCamera() {
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        switch (camera.toggleFacing()) {
            case BACK:
                //message("Switched to back camera!", false);
                break;

            case FRONT:
                //message("Switched to front camera!", false);
                break;
        }
    }

    public void setCameraFull(int value) {
        // Option optionWidth = new Option.Width();
        Option optionHeight = new Option.Height();
       /* if ((optionWidth instanceof Option.Width || optionHeight instanceof Option.Height)) {
            Preview preview = camera.getPreview();
            boolean wrapContent = (Integer) value == ViewGroup.LayoutParams.WRAP_CONTENT;
            if (preview == Preview.SURFACE && !wrapContent) {
                message("The SurfaceView preview does not support width or height changes. " +
                        "The view will act as WRAP_CONTENT by default.", true);
                return;
            }
        }*/
        if (optionHeight instanceof Option.Height) {
            Preview preview = camera.getPreview();
            boolean wrapContent = (Integer) value == ViewGroup.LayoutParams.WRAP_CONTENT;
            if (preview == Preview.SURFACE && !wrapContent) {
                /*message("The SurfaceView preview does not support width or height changes. " +
                        "The view will act as WRAP_CONTENT by default.", true);*/
                return;
            }
        }
        optionHeight.set(camera, value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        iv_start_video.setTag(VEDIO_START);
    }

    private ProgressDialog mProgressDialog;

    private void buildDialog() {
        mProgressDialog = new ProgressDialog(FriendCameraActivity.this);
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
}
