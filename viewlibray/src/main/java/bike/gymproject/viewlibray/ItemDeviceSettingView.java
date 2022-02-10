package bike.gymproject.viewlibray;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/*
 * 通用Item控件封装 包含提示点/复选框等
 * classes : view
 * @author 苗恒聚
 * V 1.0.0
 * Create at 2016/10/9 8:52
 */
public class ItemDeviceSettingView extends LinearLayout {

    /**
     * 标题
     */
    private String titleText;

    private int titleColor;
    private float titleSize;

    /**
     * 右边详细内容
     */
    private String contentText;
    private int contentColor;
    private float contentSize;

    private TextView titleTextView;

    private ImageView ivIcon;

    private RelativeLayout layoutBg;

    /**
     * 右边内容
     */
    private TextView contentTextView;

    private CheckBox checkBox;


    /**
     * 右边箭头
     */
    private ImageView arrowImageView;

    /**
     * 是否显示小红点
     */
    private boolean showRemind;

    /**
     * 设置选择模式
     */
    private boolean showCheckModel;

    /**
     * 是否显示箭头
     */
    private boolean showArrow;
    /**
     * 是否显示底部的线条
     */
    private boolean showBottomLine;

    /**
     * 复选框是否选中
     */
    private boolean isChecked;

    private Drawable remindIcon;


    public ItemDeviceSettingView(Context context) {
        this(context, null);
    }

    public ItemDeviceSettingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemDeviceSettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initBase(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemDeviceSettingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initBase(context, attrs, defStyleAttr);
    }

    private void initBase(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemView,
                defStyleAttr, 0);
        titleText = a.getString(R.styleable.ItemView_itemText);
        titleColor = a.getColor(R.styleable.ItemView_itemTextColor, context.getResources().getColor(R.color.common_white));

        contentText = a.getString(R.styleable.ItemView_contentText);
        contentSize = a.getDimension(R.styleable.ItemView_contentSize, 15);
        titleSize = a.getDimension(R.styleable.ItemView_itemviewTitleSize, 15);
        contentColor = a.getColor(R.styleable.ItemView_contentTextColor, context.getResources().getColor(R.color.common_white));
        showRemind = a.getBoolean(R.styleable.ItemView_showRemind, false);
        showArrow = a.getBoolean(R.styleable.ItemView_showArrow, true);
        showBottomLine = a.getBoolean(R.styleable.ItemView_showBottomLine, false);
        showCheckModel = a.getBoolean(R.styleable.ItemView_showCheckModel, false);
        isChecked = a.getBoolean(R.styleable.ItemView_isChecked, false);
        remindIcon = a.getDrawable(R.styleable.ItemView_remindIcon);
        a.recycle();


        initView();
        setListener();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_device_setting_remind_item, this, true);


        ivIcon = findViewById(R.id.iv_icon);
        titleTextView = (TextView) findViewById(R.id.view_car_remind_text);
        contentTextView = (TextView) findViewById(R.id.view_car_remind_content_text);
        arrowImageView = (ImageView) findViewById(R.id.view_car_remind_arrow);
        checkBox = (CheckBox) findViewById(R.id.view_car_remind_radio);
        layoutBg = findViewById(R.id.layout_bg);


        //remindImageView = (RoundImageView) findViewById(R.id.view_car_remind_red);

        //setCheckBox(showCheckModel);
        checkBox.setVisibility(showCheckModel ? VISIBLE : GONE);
        titleTextView.setTextColor(titleColor);
        contentTextView.setTextColor(contentColor);
    }

    private void initData() {
        if (!TextUtils.isEmpty(titleText)) {
            titleTextView.setText(titleText);
        }

        if (!TextUtils.isEmpty(contentText)) {
            contentTextView.setVisibility(View.VISIBLE);
            contentTextView.setText(contentText);
        }

        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentSize);


        setShowRemind(showRemind);
        setChecked(isChecked);
        setShowArrow(showArrow);
        ivIcon.setVisibility(GONE);
        setRemindIcon(remindIcon);
    }


    private void setListener() {

    }

    public void setTitleText(int resId) {
        if ((resId >>> 24) < 2) {
            return;
        }
        setTitleText(getContext().getString(resId));
    }

    public void setTitleText(String text) {
        if (null == titleTextView) {
            return;
        }
        this.titleText = text;
        titleTextView.setText(TextUtils.isEmpty(text) ? "" : text);
    }

    public void setContentText(int resId) {
        if ((resId >>> 24) < 2) {
            return;
        }
        setContentText(getContext().getString(resId));
    }

    public void setRightContentTxt(String txt){
        contentTextView.setVisibility(VISIBLE);
        contentTextView.setText(txt);
        postInvalidate();
    }

    public void setContentText(String text) {
        if (null == contentTextView) {
            return;
        }
        this.contentText = text;
        contentTextView.setVisibility(View.VISIBLE);
        contentTextView.setText(TextUtils.isEmpty(text) ? "" : text);
        invalidate();
    }

    public void showBottomLine(boolean isShow) {
    }

    public String getLeftTextValue() {
        return titleText;
    }

    public String getRightTextValue() {
        return contentText;
    }

    public void setRemindIcon(int resId) {
        ivIcon.setVisibility(View.VISIBLE);
        ivIcon.setImageResource(resId);
    }

    public void setRemindIcon(Drawable remindIcon) {
        this.remindIcon = remindIcon;
        if (null == remindIcon) {
            return;
        }
        // iv_icon.setCompoundDrawables(remindIcon, null, null, null);

        ivIcon.setVisibility(VISIBLE);
        ivIcon.setImageDrawable(this.remindIcon);
    }


    /**
     * 根据资源ID获取Drawable/设置边框
     *
     * @param resId
     * @return
     */
    private Drawable getDrawable(int resId) {
        if ((resId >>> 24) < 2) {
            return null;
        }
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = getContext().getDrawable(resId);
        } else {
            drawable = getContext().getResources().getDrawable(resId);
        }

        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        return drawable;
    }


    /*public RoundImageView getRemindImageView() {
        return remindImageView;
    }*/


    public void setBg(int res) {
        layoutBg.setBackgroundResource(res);


    }

    public void setShowRemind(boolean showRemind) {
        this.showRemind = showRemind;
        //remindImageView.setVisibility(showRemind ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置右边箭头显示或者隐藏 默认显示
     *
     * @param showArrow
     */
    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
        arrowImageView.setVisibility(showArrow ? View.VISIBLE : View.GONE);
    }

    public void setHideArrow() {
        if (arrowImageView != null) {
            arrowImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void setShowArrow(int resId) {
        if (arrowImageView != null) {
            arrowImageView.setVisibility(View.VISIBLE);
            arrowImageView.setImageResource(resId);
        }
    }


    public void setCheckBox(boolean isChecked) {
        checkBox.setChecked(isChecked);
    }


    public boolean isShowCheckModel() {
        return showCheckModel;
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }


    public void setOnCheckedChangeListener(final OnItemViewCheckedChangeListener listener) {
        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCheckedChanged(getId(), checkBox.isChecked());
                }
            }
        });
    }


    public void setOnContentClickListener(final OnContentClickListener listener) {
        layoutBg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onContentClick();
                }
            }
        });
    }

    public interface OnItemViewCheckedChangeListener {
        void onCheckedChanged(int id, boolean isChecked);
    }


    public interface OnContentClickListener {
        void onContentClick();
    }


}