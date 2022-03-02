package phone.gym.jkcq.com.commonres.commonutil;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import phone.gym.jkcq.com.commonres.R;

/*
 * 照片选择PopWindow
 *
 * @author mhj
 * Create at 2017/11/6 11:29
 */
public class PhotoChoosePopUtil implements View.OnClickListener {

    private Context context;

    private PopupWindowUtils popupWindowUtils;

    private OnPhotoChooseListener onPhotoChooseListener;

    private View contentView;

    private View viewChooseHide;
    private TextView tvCamera;
    private TextView tvPhotograph;
    private TextView tvChooseCancel;
    private String str1, str2;

    public PhotoChoosePopUtil(Context context) {
        this.context = context;
        init();
        initView();
        initEvent();
    }

    public PhotoChoosePopUtil(Context context, String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
        this.context = context;
        init();
        initView();
        initEvent();
    }

    public PhotoChoosePopUtil(Context context, String str1, boolean hideView) {
        this.str1 = str1;
        this.context = context;
        init();
        initView();
        initEvent();
        tvCamera.setVisibility(View.GONE);
    }

    private void init() {
        if (null == popupWindowUtils) {
            popupWindowUtils = new PopupWindowUtils();
        }

        contentView = View.inflate(context, R.layout.app_view_photo_picker_pop, null);
        popupWindowUtils.initPopupWindow(context, contentView);

        popupWindowUtils.setPopupWindowWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindowUtils.setPopupWindowHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindowUtils.setAnimationStyle(R.style.pop_anim_style_fade);

        popupWindowUtils.setPopupBackground(Color.parseColor("#66000000"));
    }

    private void initView() {
        viewChooseHide = contentView.findViewById(R.id.view_choose_hide);

        tvCamera = (TextView) contentView.findViewById(R.id.tv_camera);
        tvPhotograph = (TextView) contentView.findViewById(R.id.tv_photograph);
        tvChooseCancel = (TextView) contentView.findViewById(R.id.tv_choose_cancel);
        if (!TextUtils.isEmpty(str1)) {
            tvPhotograph.setText(str1);
        }
        if (!TextUtils.isEmpty(str2)) {
            tvCamera.setText(str2);
        }
    }


    public void setPhotoTxt(String txt){
        tvPhotograph.setText(txt);
    }


    public void setTvCameraVisiable(boolean isVisiable){
        tvCamera.setVisibility(isVisiable ? View.VISIBLE : View.GONE);
    }

    private void initEvent() {
        viewChooseHide.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
        tvPhotograph.setOnClickListener(this);
        tvChooseCancel.setOnClickListener(this);
    }

    public void show(View view) {
        popupWindowUtils.showPopupWindow(view, Gravity.BOTTOM, 0, 0);
    }

    public void setOnPhotoChooseListener(OnPhotoChooseListener onPhotoChooseListener) {
        this.onPhotoChooseListener = onPhotoChooseListener;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.view_choose_hide) {
            dismissPopupWindow();
        } else if (v.getId() == R.id.tv_choose_cancel) {
            dismissPopupWindow();
        } else if (v.getId() == R.id.tv_camera) {
            dismissPopupWindow();
            if (onPhotoChooseListener != null) {
                onPhotoChooseListener.onChooseCamera();
            }
        } else if (v.getId() == R.id.tv_photograph) {
            dismissPopupWindow();
            if (onPhotoChooseListener != null) {
                onPhotoChooseListener.onChoosePhotograph();
            }
        }

    }

    public void dismissPopupWindow() {
        if (onPhotoChooseListener != null) {
            popupWindowUtils.dismissPopupWindow();
        }
    }

    public interface OnPhotoChooseListener {

        /**
         * 拍照
         */
        void onChooseCamera();

        /**
         * 从文件夹选择
         */
        void onChoosePhotograph();
    }
}
