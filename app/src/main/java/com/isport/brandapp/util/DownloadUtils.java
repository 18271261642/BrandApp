package com.isport.brandapp.util;

import android.util.Log;

import com.isport.brandapp.R;
import com.isport.brandapp.banner.recycleView.utils.ToastUtil;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import brandapp.isport.com.basicres.BaseApp;
import brandapp.isport.com.basicres.commonutil.UIUtils;
import phone.gym.jkcq.com.commonres.commonutil.Arith;

/*
 *
 *
 * @author mhj
 * Create at 2019/3/26 12:03
 */
public class DownloadUtils {

    private DownloadUtils() {

    }

    private static class SingletonHolder {
        private final static DownloadUtils instance = new DownloadUtils();
    }

    public static DownloadUtils getInstance() {
        return SingletonHolder.instance;
    }


    ArrayList<DownloadTask> addTask = new ArrayList<>();
    DownloadTask[] tasks;

    public void downLoadSenceCancle() {
        tasks = new DownloadTask[addTask.size()];
        for (int i = 0; i < addTask.size(); i++) {
            tasks[i] = addTask.get(i);
        }
        DownloadTask.cancel(tasks);
    }

    DownloadTask task;
    long increaseLength, fileTotalLength;

    public void downBin(String url, String path, String fileName,final onDownloadListener listener) {
        File file = null;
        file = new File(path);
        if (file == null) {
            return;
        }
        task = new DownloadTask.Builder(url, file)
                .setFilename(fileName)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(30)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();
        task.enqueue(new DownloadListener() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                Log.e("shao", "-------taskStart----");
                increaseLength = 0;
            }

            @Override
            public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {
                Log.e("shao", "-------connectTrialStart----");
            }

            @Override
            public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                Log.e("shao", "-------connectTrialEnd----");
            }

            @Override
            public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {
                Log.e("shao", "-------downloadFromBeginning----" + " fileLength: " + info.getTotalLength());
                fileTotalLength = info.getTotalLength();
                listener.onStart((float) Arith.div(fileTotalLength, 1024 * 1024, 2));
            }

            @Override
            public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
                Log.e("shao", "-------downloadFromBreakpoint----" + " fileLength: " + info.getTotalLength());
            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
                Log.e("shao", "-------connectStart----");
            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                Log.e("shao", "-------connectEnd----");
            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                Log.e("shao", "-------fetchStart----" + " blockIndex: " + blockIndex + " contentLength : " + contentLength);
            }

            @Override
            public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
                Log.e("shao", "-------fetchProgress----" + increaseBytes);
                increaseLength = increaseLength + increaseBytes;
                if ((float) (increaseLength / (double) fileTotalLength) >= 100) {
                    listener.onProgress(100);
                } else {
                    listener.onProgress((float) (increaseLength / (double) fileTotalLength));
                }
            }

            @Override
            public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                Log.e("shao", "-------fetchEnd----" + " blockIndex " + blockIndex + " contentLength: " + contentLength);
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                if (increaseLength == 0 && fileTotalLength == 0) {
                    //失败
                    clear();
                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    listener.onFail();

                } else {
                    if (increaseLength >= fileTotalLength) {
                        clear();
                        listener.onComplete();
                    } else {
                        clear();
                        listener.onFail();
                    }
                }
            }
        });
    }



    public void downBin(String url, String path, String fileName,final onDownloadListener listener,int interval) {
        File file = null;
        file = new File(path);
        if (file == null) {
            return;
        }
        task = new DownloadTask.Builder(url, file)
                .setFilename(fileName)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(interval)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();
        task.enqueue(new DownloadListener() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                Log.e("shao", "-------taskStart----");
                increaseLength = 0;
            }

            @Override
            public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {
                Log.e("shao", "-------connectTrialStart----");
            }

            @Override
            public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                Log.e("shao", "-------connectTrialEnd----");
            }

            @Override
            public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {
                Log.e("shao", "-------downloadFromBeginning----" + " fileLength: " + info.getTotalLength());
                fileTotalLength = info.getTotalLength();
                listener.onStart((float) Arith.div(fileTotalLength, 1024 * 1024, 2));
            }

            @Override
            public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
                Log.e("shao", "-------downloadFromBreakpoint----" + " fileLength: " + info.getTotalLength());
            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
                Log.e("shao", "-------connectStart----");
            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                Log.e("shao", "-------connectEnd----");
            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                Log.e("shao", "-------fetchStart----" + " blockIndex: " + blockIndex + " contentLength : " + contentLength);
            }

            @Override
            public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
                Log.e("shao", "-------fetchProgress----" + increaseBytes);
                increaseLength = increaseLength + increaseBytes;
                if ((float) (increaseLength / (double) fileTotalLength) >= 100) {
                    listener.onProgress(100);
                } else {
                    listener.onProgress((float) (increaseLength / (double) fileTotalLength));
                }
            }

            @Override
            public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                Log.e("shao", "-------fetchEnd----" + " blockIndex " + blockIndex + " contentLength: " + contentLength);
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                if (increaseLength == 0 && fileTotalLength == 0) {
                    //失败
                    clear();
                    ToastUtil.showTextToast(BaseApp.getApp(), UIUtils.getString(R.string.common_please_check_that_your_network_is_connected));
                    listener.onFail();

                } else {
                    if (increaseLength >= fileTotalLength) {
                        clear();
                        listener.onComplete();
                    } else {
                        clear();
                        listener.onFail();
                    }
                }
            }
        });
    }

    public void clear() {
        this.fileTotalLength = 0;
        this.increaseLength = 0;
    }


}
