package phone.gym.jkcq.com.socialmodule.video.cut;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import brandapp.isport.com.basicres.commonutil.Logger;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import iknow.android.utils.UnitConverter;
import iknow.android.utils.callback.SingleCallback;
import iknow.android.utils.thread.BackgroundExecutor;
import iknow.android.utils.thread.UiThreadExecutor;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;
import phone.gym.jkcq.com.socialmodule.R;
import phone.gym.jkcq.com.socialmodule.util.CacheUtil;
import phone.gym.jkcq.com.socialmodule.util.UriUtil;
import phone.gym.jkcq.com.socialmodule.video.ffmpeg.FfmpegSupport;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public class VideoTrimmerView extends FrameLayout implements IVideoTrimmerView {

    private static final String TAG = VideoTrimmerView.class.getSimpleName();

    public long MIN_SHOOT_DURATION = 3000L;// 最小剪辑时间3s

    //最大剪辑时长
    public int VIDEO_MAX_TIME = 15;// 15秒
    public long MAX_SHOOT_DURATION = VIDEO_MAX_TIME * 1000L;//视频最多剪切多长时间10s
    public int MAX_COUNT_RANGE = 15;  //seekBar的区域内一共有多少张图片
    private int SCREEN_WIDTH_FULL = ScreenUtils.getScreenWidth();
    public int RECYCLER_VIEW_PADDING = UnitConverter.dpToPx(35);
    public int VIDEO_FRAMES_WIDTH = SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2;
    public int THUMB_WIDTH = (SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2) / VIDEO_MAX_TIME;
    private int THUMB_HEIGHT = UnitConverter.dpToPx(50);
    private int mMaxWidth = VIDEO_FRAMES_WIDTH;
    private Context mContext;
    private RelativeLayout mLinearVideo;
    private ZVideoView mVideoView;
    private ImageView mPlayView;
    private RecyclerView mVideoThumbRecyclerView;
    private RangeSeekBarView mRangeSeekBarView;
    private LinearLayout mSeekBarLayout;
    private ImageView mRedProgressIcon;
    private TextView mVideoShootTipTv;
    private float mAverageMsPx;//每毫秒所占的px
    private float averagePxMs;//每px所占用的ms毫秒
    private Uri mSourceUri;
    private VideoTrimListener mOnTrimVideoListener;
    private int mDuration = 0;
    private VideoTrimmerAdapter mVideoThumbAdapter;
    private boolean isFromRestore = false;
    //new
    private long mLeftProgressPos, mRightProgressPos;
    private long mRedProgressBarPos = 0;
    private long scrollPos = 0;
    private int mScaledTouchSlop;
    private int lastScrollX;
    private boolean isSeeking;
    private boolean isOverScaledTouchSlop;
    private int mThumbsTotalCount;
    private ValueAnimator mRedProgressAnimator;
    private Handler mAnimationHandler = new Handler();

    public VideoTrimmerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoTrimmerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.video_trimmer_view, this, true);

        mLinearVideo = findViewById(R.id.layout_surface_view);
        mVideoView = findViewById(R.id.video_loader);
        mPlayView = findViewById(R.id.icon_video_play);
        mSeekBarLayout = findViewById(R.id.seekBarLayout);
        mRedProgressIcon = findViewById(R.id.positionIcon);
        mVideoShootTipTv = findViewById(R.id.video_shoot_tip);
        mVideoThumbRecyclerView = findViewById(R.id.video_frames_recyclerView);
        mVideoThumbRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mVideoThumbAdapter = new VideoTrimmerAdapter(mContext, VIDEO_FRAMES_WIDTH / MAX_COUNT_RANGE);
        mVideoThumbRecyclerView.setAdapter(mVideoThumbAdapter);
        mVideoThumbRecyclerView.addOnScrollListener(mOnScrollListener);
        setUpListeners();
    }

    private void initRangeSeekBarView() {

        if(mDuration == 0 || MAX_SHOOT_DURATION == 0){
            ToastUtils.showToast(getContext(),"视频市场过短!");
            return;
        }

        if (mDuration <= MAX_SHOOT_DURATION) {
            MAX_SHOOT_DURATION = mDuration;
            VIDEO_MAX_TIME = (int) MAX_SHOOT_DURATION / 1000;
            THUMB_WIDTH = (SCREEN_WIDTH_FULL - RECYCLER_VIEW_PADDING * 2) / VIDEO_MAX_TIME;
        }
        if (mRangeSeekBarView != null) return;
        mLeftProgressPos = 0;
        if (mDuration <= MAX_SHOOT_DURATION) {
            mThumbsTotalCount = MAX_COUNT_RANGE;
            mRightProgressPos = mDuration;
        } else {
            mThumbsTotalCount = (int) (mDuration * 1.0f / (MAX_SHOOT_DURATION * 1.0f) * MAX_COUNT_RANGE);
            mRightProgressPos = MAX_SHOOT_DURATION;
        }
        mVideoThumbRecyclerView.addItemDecoration(new SpacesItemDecoration2(RECYCLER_VIEW_PADDING, mThumbsTotalCount));
        mRangeSeekBarView = new RangeSeekBarView(mContext, mLeftProgressPos, mRightProgressPos);
        mRangeSeekBarView.setSelectedMinValue(mLeftProgressPos);
        mRangeSeekBarView.setSelectedMaxValue(mRightProgressPos);
        mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
        mRangeSeekBarView.setMinShootTime(MIN_SHOOT_DURATION);
        mRangeSeekBarView.setNotifyWhileDragging(true);
        mRangeSeekBarView.setOnRangeSeekBarChangeListener(mOnRangeSeekBarChangeListener);
        mSeekBarLayout.addView(mRangeSeekBarView);
        if (mThumbsTotalCount - MAX_COUNT_RANGE > 0) {
            mAverageMsPx = (mDuration - MAX_SHOOT_DURATION) / (float) (mThumbsTotalCount - MAX_COUNT_RANGE);
        } else {
            mAverageMsPx = 0f;
        }
        averagePxMs = (mMaxWidth * 1.0f / (mRightProgressPos - mLeftProgressPos));
    }

    public void initVideoByURI(final Uri videoURI) {
        mSourceUri = videoURI;
//        mVideoView.setVideoURI(videoURI);
        mVideoView.setVideoURI(mSourceUri);
        mVideoView.requestFocus();
        mVideoShootTipTv.setText(String.format(mContext.getResources().getString(R.string.video_shoot_tip), VIDEO_MAX_TIME));
    }

    private void startShootVideoThumbs(final Context context, final Uri videoUri, int totalThumbsCount, long startPosition, long endPosition) {
        VideoTrimmerUtil.shootVideoThumbInBackground(context, videoUri, totalThumbsCount, startPosition, endPosition, THUMB_WIDTH, THUMB_HEIGHT,
                new SingleCallback<Bitmap, Integer>() {
                    @Override
                    public void onSingleCallback(final Bitmap bitmap, final Integer interval) {
                        if (bitmap != null) {
                            UiThreadExecutor.runTask("", new Runnable() {
                                @Override
                                public void run() {
                                    mVideoThumbAdapter.addBitmaps(bitmap);
                                }
                            }, 0L);
                        }
                    }
                });
    }

    private void onCancelClicked() {
        mOnTrimVideoListener.onCancel();
    }

    private void videoPrepared(MediaPlayer mp) {
        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();

        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = mLinearVideo.getWidth();
        int screenHeight = mLinearVideo.getHeight();

        if (videoHeight > videoWidth) {
            lp.width = screenWidth;
            lp.height = screenHeight;
        } else {
            lp.width = screenWidth;
            float r = videoHeight / (float) videoWidth;
            lp.height = (int) (lp.width * r);
        }
        mVideoView.setLayoutParams(lp);
        mDuration = (mVideoView.getDuration() / 1000) * 1000;//去除尾部不足1s的视频
        if (!getRestoreState()) {
            seekTo((int) mRedProgressBarPos);
        } else {
            setRestoreState(false);
            seekTo((int) mRedProgressBarPos);
        }
        initRangeSeekBarView();
        startShootVideoThumbs(mContext, mSourceUri, mThumbsTotalCount, 0, mDuration);
    }

    private void videoCompleted() {
        seekTo(mLeftProgressPos);
        setPlayPauseViewIcon(false);
    }

    private void onVideoReset() {
        mVideoView.pause();
        setPlayPauseViewIcon(false);
    }

    private void playVideoOrPause() {
        mRedProgressBarPos = mVideoView.getCurrentPosition();
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            pauseRedProgressAnimation();
        } else {
            mVideoView.start();
            playingRedProgressAnimation();
        }
        setPlayPauseViewIcon(mVideoView.isPlaying());
    }

    public void onVideoPause() {
        if (mVideoView.isPlaying()) {
            seekTo(mLeftProgressPos);//复位
            mVideoView.pause();
            setPlayPauseViewIcon(false);
            mRedProgressIcon.setVisibility(GONE);
        }
    }

    public void setOnTrimVideoListener(VideoTrimListener onTrimVideoListener) {
        mOnTrimVideoListener = onTrimVideoListener;
    }

    private void setUpListeners() {
        findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClicked();
            }
        });

        findViewById(R.id.finishBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveClicked();
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                videoPrepared(mp);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoCompleted();
            }
        });
        mPlayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideoOrPause();
            }
        });
    }

    private String mSourceVideoPath;

    private void onSaveClicked() {
        mProgressCache = 0;
        if (mRightProgressPos - mLeftProgressPos < MIN_SHOOT_DURATION) {
            Toast.makeText(mContext, R.string.invalide_video_can_not_upload, Toast.LENGTH_SHORT).show();
        } else {
            mVideoView.pause();
            mSourceVideoPath = UriUtil.getPath(getContext(), mSourceUri);
            mOnTrimVideoListener.onStartTrim();
            runFFmpegRxJava();

//      VideoTrimmerUtil.trim(mContext,
//          mSourceUri.getPath(),
//          StorageUtil.getCacheDir(),
//          mLeftProgressPos,
//          mRightProgressPos,
//          mOnTrimVideoListener);
        }
    }

    private void seekTo(long msec) {
        mVideoView.seekTo((int) msec);
        Log.d(TAG, "seekTo = " + msec);
    }

    private boolean getRestoreState() {
        return isFromRestore;
    }

    public void setRestoreState(boolean fromRestore) {
        isFromRestore = fromRestore;
    }

    private void setPlayPauseViewIcon(boolean isPlaying) {
        mPlayView.setImageResource(isPlaying ? R.drawable.ic_video_pause_black : R.drawable.ic_video_play_black);
    }

    private final RangeSeekBarView.OnRangeSeekBarChangeListener mOnRangeSeekBarChangeListener = new RangeSeekBarView.OnRangeSeekBarChangeListener() {
        @Override
        public void onRangeSeekBarValuesChanged(RangeSeekBarView bar, long minValue, long maxValue, int action, boolean isMin,
                                                RangeSeekBarView.Thumb pressedThumb) {
            Log.d(TAG, "-----minValue----->>>>>>" + minValue);
            Log.d(TAG, "-----maxValue----->>>>>>" + maxValue);
            mLeftProgressPos = minValue + scrollPos;
            mRedProgressBarPos = mLeftProgressPos;
            mRightProgressPos = maxValue + scrollPos;
            Log.d(TAG, "-----mLeftProgressPos----->>>>>>" + mLeftProgressPos);
            Log.d(TAG, "-----mRightProgressPos----->>>>>>" + mRightProgressPos);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isSeeking = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    isSeeking = true;
                    seekTo((int) (pressedThumb == RangeSeekBarView.Thumb.MIN ? mLeftProgressPos : mRightProgressPos));
                    break;
                case MotionEvent.ACTION_UP:
                    isSeeking = false;
                    seekTo((int) mLeftProgressPos);
                    break;
                default:
                    break;
            }

            mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
        }
    };

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.d(TAG, "newState = " + newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            isSeeking = false;
            int scrollX = calcScrollXDistance();
            //达不到滑动的距离
            if (Math.abs(lastScrollX - scrollX) < mScaledTouchSlop) {
                isOverScaledTouchSlop = false;
                return;
            }
            isOverScaledTouchSlop = true;
            //初始状态,why ? 因为默认的时候有35dp的空白！
            if (scrollX == -RECYCLER_VIEW_PADDING) {
                scrollPos = 0;
            } else {
                isSeeking = true;
                scrollPos = (long) (mAverageMsPx * (RECYCLER_VIEW_PADDING + scrollX) / THUMB_WIDTH);
                mLeftProgressPos = mRangeSeekBarView.getSelectedMinValue() + scrollPos;
                mRightProgressPos = mRangeSeekBarView.getSelectedMaxValue() + scrollPos;
                Log.d(TAG, "onScrolled >>>> mLeftProgressPos = " + mLeftProgressPos);
                mRedProgressBarPos = mLeftProgressPos;
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    setPlayPauseViewIcon(false);
                }
                mRedProgressIcon.setVisibility(GONE);
                seekTo(mLeftProgressPos);
                mRangeSeekBarView.setStartEndTime(mLeftProgressPos, mRightProgressPos);
                mRangeSeekBarView.invalidate();
            }
            lastScrollX = scrollX;
        }
    };

    /**
     * 水平滑动了多少px
     */
    private int calcScrollXDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mVideoThumbRecyclerView.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleChildView = layoutManager.findViewByPosition(position);
        int itemWidth = firstVisibleChildView.getWidth();
        return (position) * itemWidth - firstVisibleChildView.getLeft();
    }

    private void playingRedProgressAnimation() {
        pauseRedProgressAnimation();
        playingAnimation();
        mAnimationHandler.post(mAnimationRunnable);
    }

    private void playingAnimation() {
        if (mRedProgressIcon.getVisibility() == View.GONE) {
            mRedProgressIcon.setVisibility(View.VISIBLE);
        }
        final LayoutParams params = (LayoutParams) mRedProgressIcon.getLayoutParams();
        int start = (int) (RECYCLER_VIEW_PADDING + (mRedProgressBarPos - scrollPos) * averagePxMs);
        int end = (int) (RECYCLER_VIEW_PADDING + (mRightProgressPos - scrollPos) * averagePxMs);
        mRedProgressAnimator = ValueAnimator.ofInt(start, end).setDuration((mRightProgressPos - scrollPos) - (mRedProgressBarPos - scrollPos));
        mRedProgressAnimator.setInterpolator(new LinearInterpolator());
        mRedProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.leftMargin = (int) animation.getAnimatedValue();
                mRedProgressIcon.setLayoutParams(params);
                Log.d(TAG, "----onAnimationUpdate--->>>>>>>" + mRedProgressBarPos);
            }
        });
        mRedProgressAnimator.start();
    }

    private void pauseRedProgressAnimation() {
        mRedProgressIcon.clearAnimation();
        if (mRedProgressAnimator != null && mRedProgressAnimator.isRunning()) {
            mAnimationHandler.removeCallbacks(mAnimationRunnable);
            mRedProgressAnimator.cancel();
        }
    }

    private Runnable mAnimationRunnable = new Runnable() {

        @Override
        public void run() {
            updateVideoProgress();
        }
    };

    private void updateVideoProgress() {
        long currentPosition = mVideoView.getCurrentPosition();
        Log.i(TAG, "updateVideoProgress currentPosition = " + currentPosition);
        if (currentPosition >= (mRightProgressPos)) {
            mRedProgressBarPos = mLeftProgressPos;
            pauseRedProgressAnimation();
            onVideoPause();
        } else {
            mAnimationHandler.post(mAnimationRunnable);
        }
    }

    /**
     * Cancel trim thread execut action when finish
     */
    @Override
    public void onDestroy() {
        BackgroundExecutor.cancelAll("", true);
        UiThreadExecutor.cancelAll("");
    }

    /**
     * rxjava方式调用
     */
    private MyRxFFmpegSubscriber myRxFFmpegSubscriber;

    private void runFFmpegRxJava() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                final String outputName = "trimmedVideo_cut" + timeStamp + ".mp4";
                mOutPath = CacheUtil.getVideoCache() + "/" + outputName;
                String start = VideoTrimmerUtil.convertSecondsToTime(mLeftProgressPos / 1000);
                String duration = "" + (mRightProgressPos - mLeftProgressPos) / 1000;
                String videoRate = CutUtil.calBitRa(mSourceVideoPath);
                String[] cmd;
                cmd = FfmpegSupport.getCutVideoCommandV(mSourceVideoPath, mOutPath,
                        start, duration, videoRate);
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
//            if (((mRightProgressPos - mLeftProgressPos)/1000) <= 5) {
//                mOnTrimVideoListener.onFinishTrim(mOutPath);
//            } else {
//                runCompresorRxJava();
//            }
            //裁剪完直接压缩
            // Logger.e("VideoTrimmerView", "onFinish"+"  mediaInfo="+RxFFmpegInvoke.getInstance().getMediaInfo(mOutPath));
            // runCompresorRxJava(mOutPath);
//
            mOnTrimVideoListener.onFinishTrim(mOutPath);
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            if (progress > 0) {
                // mOnTrimVideoListener.onTriming(progress / 2);
                mOnTrimVideoListener.onTriming(progress);
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

    private MyCompressSubscriber mMyCompressSubscriber;
    private String mOutPath;
    private int mProgressCache;
    private String mCompressPath;
    private int mProgress;

    private void runCompresorRxJava(String outPath) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final String outputName = "trimmedVideo_" + timeStamp + ".mp4";
        mCompressPath = CacheUtil.getVideoCache() + "/" + outputName;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(outPath);
        String value = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
        Long rate = Long.parseLong(value);

        long video = rate / 1000;
        String videoRate = rate / 1000 + "k";

        Logger.e("VideoTrimmerView", "rate=" + rate + ",videoRate=" + videoRate);
        String[] commands = FfmpegSupport.getCompressCommand(outPath, mCompressPath, videoRate);
//        String[] commands = FfmpegSupport.getTransformCommand(mOutPath, mCompressPath);
        mMyCompressSubscriber = new MyCompressSubscriber();
        //开始执行FFmpeg命令
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                RxFFmpegInvoke.getInstance()
                        .runCommandRxJava(commands)
                        .subscribe(mMyCompressSubscriber);
            }
        }, 1500);

    }

    // 这里设为静态内部类，防止内存泄露
    public class MyCompressSubscriber extends RxFFmpegSubscriber {


        public MyCompressSubscriber() {
        }

        @Override
        public void onFinish() {
            if (mProgress > 8) {
                mOnTrimVideoListener.onFinishTrim(mCompressPath);
            } else {
                mProgressCache++;
                //压缩元数据失败
                if (mProgressCache > 3) {
                    mOnTrimVideoListener.onFailed();
                } else {
                    //3次剪切机会
                    if (mProgressCache == 3) {
                        if (mDuration <= MAX_SHOOT_DURATION) {
                            runCompresorRxJava(mSourceVideoPath);
                        } else {
                            mOnTrimVideoListener.onFailed();
                            RxFFmpegInvoke.getInstance().onClean();
                            RxFFmpegInvoke.getInstance().exit();
                        }
//                    mOnTrimVideoListener.onFinishTrim(mOutPath);
                    } else {
                        runFFmpegRxJava();
                    }
                }

            }

            Logger.e("VideoTrimmerView", "onFinish=");
        }

        @Override
        public void onProgress(int progress, long progressTime) {
            mProgress = progress;
            if (mProgressCache > progress) {
                mOnTrimVideoListener.onTriming(mProgressCache + 50);
            } else {
                mOnTrimVideoListener.onTriming(50 + progress / 2);
            }

            Logger.e("VideoTrimmerView", "progress=" + progress);
        }

        @Override
        public void onCancel() {
            Logger.e("VideoTrimmerView", "onCancel=");
        }

        @Override
        public void onError(String message) {
            Logger.e("VideoTrimmerView", "MyCompressorSubscriber onError=");
        }
    }

}
