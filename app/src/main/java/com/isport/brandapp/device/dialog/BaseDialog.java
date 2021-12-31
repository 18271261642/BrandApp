package com.isport.brandapp.device.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.isport.brandapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;

/**
 * 功能：dialog基类
 */
public class BaseDialog extends AppCompatDialog implements DialogInterface {
    private DialogController mController;

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mController = new DialogController(this, getWindow());
    }

    /**
     * 设置文字
     *
     * @param viewId 资源Id
     * @param text   文本
     */
    public void setText(int viewId, CharSequence text) {
        mController.setText(viewId, text);
    }

    /**
     * 设置点击事件
     *
     * @param viewId          资源Id
     * @param onClickListener 监听器
     */
    public void setOnClickListener(int viewId, OnClickListener onClickListener) {
        mController.setOnClickListener(viewId, onClickListener);
    }

    /**
     * 设置是否显示或隐藏视图
     *
     * @param viewId  资源Id
     * @param visible 显示或隐藏
     */
    public void setViewVisible(int viewId, int visible) {
        mController.setViewVisible(viewId, visible);
    }


    public static class Builder {
        private DialogController.DialogParams P;

        public Builder(@NonNull Context context) {
            this(context, R.style.BaseDialog);
        }


        public Builder(@NonNull Context context, @StyleRes int themeResId) {
            P = new DialogController.DialogParams(context);
            P.mThemeResId = themeResId;
        }

        /**
         * 设置布局视图
         *
         * @param view 视图
         * @return Builder
         */
        public Builder setContentView(View view) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            return this;
        }

        /**
         * 设置布局视图
         *
         * @param layoutId 资源Id
         * @return Builder
         */
        public Builder setContentView(int layoutId) {
            P.mView = null;
            P.mViewLayoutResId = layoutId;
            return this;
        }

        /**
         * 设置文本
         *
         * @param viewId 对应的视图id
         * @param text   文本
         * @return Builder
         */
        public Builder setText(int viewId, CharSequence text) {
            P.mTextArrays.put(viewId, text);
            return this;
        }

        /**
         * 设置监听
         *
         * @param viewId   对应的视图id
         * @param listener 监听器
         * @return Builder
         */
        public Builder setOnClickListener(int viewId, OnClickListener listener) {
            P.mClickArrays.put(viewId, listener);
            return this;
        }

        public Builder setViewVisible(int viewId, int visible) {
            P.mVisibleArrays.put(viewId, visible);
            return this;
        }

        /***
         * 设置点击窗口外可以取消否
         * @param cancel 布尔值
         * @return Builder
         */
        public Builder setCanceledOnTouchOutside(boolean cancel) {
            P.mCancelable = cancel;
            return this;
        }

        /**
         * 设置取消dialog监听事件
         *
         * @param onCancelListener 监听器
         * @return Builder
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * 设置dialog关闭监听事件
         *
         * @param onDismissListener 关闭监听器
         * @return Builder
         */
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * 按键监听事件
         *
         * @param onKeyListener 按键监听器
         * @return Builder
         */
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        /**
         * 填充宽度为match_parent
         *
         * @return Builder
         */
        public Builder fullWidth() {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        public Builder setWidth(int width) {
            P.mWidth = width;
            return this;
        }

        /**
         * 设置宽度高度
         *
         * @param height 高度
         * @return
         */
        public Builder setHeight(int height) {
            P.mHeight = height;
            return this;
        }

        /**
         * 从底部弹出
         *
         * @param isAnimation 是否需要动画
         * @return
         */
        public Builder fromBottom(boolean isAnimation) {
            if (isAnimation) {
                P.mAnimations = R.style.FromBottomAnimation;
            }
            P.mGravity = Gravity.BOTTOM;
            return this;
        }

        /**
         * 默认动画
         *
         * @return Builder
         */
        public Builder addDefaultAnimation() {
            P.mAnimations = R.style.FromBottomAnimation;
            return this;
        }

        /**
         * 添加动画效果
         *
         * @param styleResId style资源文件
         * @return Builder
         */
        public Builder setAnimation(int styleResId) {
            P.mAnimations = styleResId;
            return this;
        }


        public BaseDialog create() {
            final BaseDialog dialog = new BaseDialog(P.mContext, P.mThemeResId);
            P.apply(dialog.mController);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        public BaseDialog show() {
            final BaseDialog dialog = create();
            if(dialog.isShowing()){
                dialog.dismiss();
            }

            dialog.show();
            return dialog;
        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
