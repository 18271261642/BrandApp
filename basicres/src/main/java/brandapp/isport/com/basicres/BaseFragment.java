package brandapp.isport.com.basicres;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.gyf.immersionbar.components.SimpleImmersionOwner;
import com.gyf.immersionbar.components.SimpleImmersionProxy;
import com.isport.brandapp.basicres.R;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import brandapp.isport.com.basicres.commonalertdialog.AlertDialogStateCallBack;
import brandapp.isport.com.basicres.commonalertdialog.PublicAlertDialog;
import brandapp.isport.com.basicres.commonutil.NetUtils;
import brandapp.isport.com.basicres.commonutil.ToastUtils;
import phone.gym.jkcq.com.commonres.common.AllocationApi;

public abstract class BaseFragment extends Fragment implements SimpleImmersionOwner {

    public static final String TAG = BaseFragment.class.getSimpleName();

    protected Context context;

    protected BaseApp app;

    private int mLayoutId;

    protected View mView;

    protected LayoutInflater mInflater;

    protected BaseFragment() {
        this.mLayoutId = getLayoutId();
    }

    protected Bundle savedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.mInflater = inflater;
        this.savedInstanceState = savedInstanceState;

        context = getActivity();

        app = (BaseApp) context.getApplicationContext();

        if (null == mView) {
            mView = setBodyView(container);
            initBaseData();
            initView(mView);
            initData();
            initEvent();
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
            Log.e("PARENT","---"+(parent == null));
        }
        return mView;
    }

    public View setBodyView(ViewGroup container) {
        return mInflater.inflate(getLayoutId(), container, false);
    }

    protected void initBaseData() {
        setViewPadding(mView);
    }

    /**
     * ??????layout Id
     *
     * @return layout Id
     */
    protected abstract int getLayoutId();

    /**
     * ???????????????View?????????
     */
    protected abstract void initView(View view);

    /**
     * ?????????????????????
     */
    protected abstract void initData();

    /**
     * View????????????
     */
    protected abstract void initEvent();

    /**
     * ????????????View
     *
     * @return ????????????View
     */
    public View getCurrentView() {
        return mView;
    }

    /**
     * ????????????View Params?????????????????????
     *
     * @param layoutParams
     */
    public void setCurrentViewPararms(FrameLayout.LayoutParams layoutParams) {
        if (null == mView) {
            return;
        }
        mView.setLayoutParams(layoutParams);
    }

    /**
     * ????????????View??????Params
     *
     * @return
     */
    public FrameLayout.LayoutParams getCurrentViewParams() {
        if (null == mView) {
            return null;
        }
        return (FrameLayout.LayoutParams) mView.getLayoutParams();
    }

    @Override
    public void startActivity(Intent intent) {
        getActivity().startActivity(intent);
    }

    /**
     * onActivityResult ????????????
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void netError() {
        if (!AllocationApi.isNetwork && !AllocationApi.isShowHint) {
            PublicAlertDialog.getInstance().showDialog("", getResources().getString(R.string.common_no_network), context, getResources().getString(R.string.common_dialog_cancel), getResources().getString(R.string.common_dialog_ok), new AlertDialogStateCallBack() {
                @Override
                public void determine() {
                    NetUtils.openNet(getActivity());
                }

                @Override
                public void cancel() {

                }
            }, false);
        }
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public boolean checkNet() {
        if (NetUtils.hasNetwork(BaseApp.getApp())) {
            return true;
        } else {
            ToastUtils.showToast(context, R.string.common_please_check_that_your_network_is_connected);
            return false;
        }
    }

    public void setViewPadding(View view) {
        if (null == view) {
            return;
        }

        if (!(getActivity() instanceof BaseActivity)) {
            return;
        }

        if (!((BaseActivity) getActivity()).setImmersionType()) {
            ((BaseActivity) getActivity()).setViewPadding(view);
        }
    }

    public <X> AutoDisposeConverter<X> bindAutoDispose() {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY));
    }

    /**
     * ImmersionBar?????????
     */
    private SimpleImmersionProxy mSimpleImmersionProxy = new SimpleImmersionProxy(this);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mSimpleImmersionProxy.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSimpleImmersionProxy.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSimpleImmersionProxy.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mSimpleImmersionProxy.onHiddenChanged(hidden);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSimpleImmersionProxy.onConfigurationChanged(newConfig);
    }

    /**
     * ????????????????????????????????????true????????????????????????initImmersionBar??????
     * Immersion bar enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean immersionBarEnabled() {
        return true;
    }

    long lastClickTime;

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        return timeD <= 500;
    }
}
