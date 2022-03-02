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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


/*
 * 通用Item控件封装 包含提示点/复选框等
 * classes : view
 * @author 苗恒聚
 * V 1.0.0
 * Create at 2016/10/9 8:52
 */
public class ItemView extends LinearLayout {

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
    private TextView titleNext;

    private ImageView ivIcon;

    /**
     * 右边内容
     */
    private TextView contentTextView;

    private CheckBox checkBox;

    private View rightLayout;



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
    private boolean showCheckModel, showCheckModelChange, showRadioModelChange;

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

    private RelativeLayout layoutBg;
    private WatchModeCheckBox changeCheckBox;
    private RadioGroup radioGroup;
    private RadioButton rbAuto;
    private RadioButton rbActive;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initBase(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        showCheckModelChange = a.getBoolean(R.styleable.ItemView_showCheckModelChange, false);
        showRadioModelChange = a.getBoolean(R.styleable.ItemView_showRadioModelChange, false);
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
        LayoutInflater.from(getContext()).inflate(R.layout.view_remind_item, this, true);

        titleTextView = (TextView) findViewById(R.id.view_car_remind_text);
        titleNext = findViewById(R.id.view_car_remind_content_next);
        layoutBg = findViewById(R.id.layout_bg);
        contentTextView = (TextView) findViewById(R.id.view_car_remind_content_text);
        checkBox = (CheckBox) findViewById(R.id.view_car_remind_radio);
        changeCheckBox = (WatchModeCheckBox) findViewById(R.id.view_mode_change_radio);
        radioGroup = (RadioGroup) findViewById(R.id.rg_sleep_setting);
        rbAuto = (RadioButton) findViewById(R.id.rb_auto);
        rbActive = (RadioButton) findViewById(R.id.rb_active);
        rightLayout = findViewById(R.id.view_car_remind_right_layout);
        ivIcon = findViewById(R.id.iv_icon);
        //remindImageView = (RoundImageView) findViewById(R.id.view_car_remind_red);
        arrowImageView = (ImageView) findViewById(R.id.view_car_remind_arrow);
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
        showBottomLine = false;

        setShowCheckModel(showCheckModel);
        setShowCheckModelChange(showCheckModelChange);
        setShowRadioModelChange(showRadioModelChange);
        setShowRemind(showRemind);
        setChecked(isChecked);
        setShowArrow(showArrow);
        ivIcon.setVisibility(GONE);
        setRemindIcon(remindIcon);
    }

    private void setShowRadioModelChange(boolean showRadioModelChange) {
        this.showRadioModelChange = showRadioModelChange;
        radioGroup.setVisibility(showRadioModelChange ? View.VISIBLE : View.GONE);
        rightLayout.setVisibility(!showRadioModelChange ? View.VISIBLE : View.GONE);
    }

    public void setRadioChange(boolean isActive) {
        radioGroup.check(isActive ? R.id.rb_active : R.id.rb_auto);
    }

    public boolean isRadioChange() {
        return rbActive.isChecked();
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

    public void setContentText(String text) {
        if (null == contentTextView) {
            return;
        }
        this.contentText = text;
        contentTextView.setVisibility(View.VISIBLE);
        contentTextView.setText(TextUtils.isEmpty(text) ? "" : text);
    }

    public void showBottomLine(boolean isShow) {
    }

    public String getContentText() {
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

    public void showNext(String nextText) {
        if (titleNext != null) {
            titleNext.setVisibility(VISIBLE);
            titleNext.setText(nextText);
        }
    }

    public void hideNext() {
        if (titleNext != null) {
            titleNext.setVisibility(GONE);
        }
    }


    public void setContentEm(){
        if(contentTextView == null)
            return;
        contentTextView.setMaxLines(15);
        contentTextView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
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

    public void setShowCheckModel(boolean showCheckModel) {
        this.showCheckModel = showCheckModel;
        checkBox.setVisibility(showCheckModel ? View.VISIBLE : View.GONE);
        rightLayout.setVisibility(!showCheckModel ? View.VISIBLE : View.GONE);
    }

    public void setShowCheckModelChange(boolean showCheckModelChange) {
        this.showCheckModelChange = showCheckModelChange;
        changeCheckBox.setVisibility(showCheckModelChange ? View.VISIBLE : View.GONE);
        rightLayout.setVisibility(!showCheckModelChange ? View.VISIBLE : View.GONE);
    }

    public void setCheckBox(boolean isChecked) {
        checkBox.setChecked(isChecked);
    }

    public void setCheckBoxChange(boolean isChecked) {
        changeCheckBox.setChecked(isChecked);
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

    public boolean isChangeChecked() {
        return changeCheckBox.isChecked();
    }

    public boolean isModeChecked() {
        return changeCheckBox.isChecked();
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

    public void setOnModeCheckedChangeListener(final OnItemViewModeCheckedChangeListener listener) {
        changeCheckBox.setOnModeCheckBoxChangeListener(new WatchModeCheckBox.OnModeCheckBoxChangeListener() {
            @Override
            public void onModeCheckBoxChanged(int id, boolean isChecked) {
                if (listener != null) {
                    listener.onModeCheckedChanged(id, isChecked);
                }
            }
        });
    }

    public void setOnRadioCheckedChangeListener(final OnItemViewRadioCheckedChangeListener listener) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_auto) {
                    if (listener != null) {
                        listener.onRadioCheckedChanged(i, false);
                    }
                } else if (i == R.id.rb_active) {
                    if (listener != null) {
                        listener.onRadioCheckedChanged(i, true);
                    }
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

    public interface OnItemViewModeCheckedChangeListener {
        void onModeCheckedChanged(int id, boolean isChecked);
    }

    public interface OnItemViewRadioCheckedChangeListener {
        void onRadioCheckedChanged(int id, boolean isActive);
    }

    public interface OnContentClickListener {
        void onContentClick();
    }


}