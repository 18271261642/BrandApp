package bike.gymproject.viewlibray;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import bike.gymproject.viewlibray.chart.BarChartEntity;
import phone.gym.jkcq.com.commonres.common.JkConfiguration;
import phone.gym.jkcq.com.commonres.commonutil.CalculateUtil;
import phone.gym.jkcq.com.commonres.commonutil.CommonDateUtil;
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils;

/**
 * 作者：chs on 2018-04-09 15:39
 * 邮箱：657083984@qq.com
 * 新的柱状图
 */
public class WeekBarChartView extends View {
    private Context mContext;
    /**
     * 视图的宽和高  刻度区域的最大值
     */
    private int mTotalWidth, mTotalHeight, maxHeight;
    private int paddingRight, paddingBottom, paddingTop;
    //柱形图的颜色集合
    private int barColors[];
    //距离底部的多少 用来显示底部的文字
    private int bottomMargin;
    //距离顶部的多少 用来显示顶部的文字
    private int topMargin;
    private int rightMargin;
    private int leftMargin;
    /**
     * 画笔 轴 刻度 柱子 点击后的柱子 单位
     */
    private Paint axisPaintX, axisPaintY, textPaint, barPaint, borderPaint, unitPaint, mPaint, hitTextPaint;
    private List<BarChartEntity> mData;//数据集合
    /**
     * item中的Y轴最大值
     */
    private float maxYValue;
    /**
     * Y轴最大的刻度值
     */
    private float maxYDivisionValue;
    /**
     * 柱子的矩形
     */
    private RectF mBarRect, mBarRectClick;//mBarRect,
    private RectF mMaxRect;//mBarRect,
    /**
     * 绘制的区域
     */
    private RectF mDrawArea;
    /**
     * 每一个bar的宽度
     */
    private float barWidth;
    /**
     * 每个bar之间的距离
     */
    private float barSpace;
    /**
     * 向右边滑动的距离
     */
    private float leftMoving;
    /**
     * 左后一次的x坐标
     */
    private float lastPointX;
    /**
     * 当前移动的距离
     */
    private float movingThisTime = 0.0f;
    /**
     * 右边的最大和最小值
     */
//    private int maxRight, minRight;
    /**
     * 下面两个相当于图表的原点
     */
    private float mStartX;
    private int mStartY;
    /**
     * 柱形图左边的x轴坐标 和右边的x轴坐标
     */
    private List<Float> mBarLeftXPoints = new ArrayList<>();
    private List<Float> mBarRightXPoints = new ArrayList<>();

    /* 用户点击到了无效位置 */
    public static final int INVALID_POSITION = -1;
    // private OnItemBarClickListener mOnItemBarClickListener;
    private GestureDetector mGestureListener;
    /**
     * 是否绘制点击效果
     */
    private boolean isDrawBorder;
    /**
     * 点击的地方
     */
    private int mClickPosition;

    //    //滑动速度相关
//    private VelocityTracker velocityTracker;
//    private Scroller scroller;
//    /**
//     * fling最大速度
//     */
//    private int maxVelocity;
    //x轴 y轴的单位
    private String unitX;
    private String unitY;

  /*  public void setOnItemBarClickListener(OnItemBarClickListener onRangeBarClickListener) {
        this.mOnItemBarClickListener = onRangeBarClickListener;
    }*/

    public interface OnItemBarClickListener {
        void onClick(int position);
    }

    public WeekBarChartView(Context context) {
        super(context);
        init(context);
    }


    public WeekBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeekBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mContext = context;
        xList.add(4);
        xList.add(8);
        xList.add(12);
        xList.add(16);
        xList.add(20);
        xList.add(24);
//        barWidth = DensityUtil.dip2px(getContext(), 20);
//        barSpace = DensityUtil.dip2px(getContext(), 20);
        topMargin = DisplayUtils.dip2px(getContext(), 20);
        bottomMargin = DisplayUtils.dip2px(getContext(), 30);
        rightMargin = DisplayUtils.dip2px(getContext(), 16);
        leftMargin = DisplayUtils.dip2px(getContext(), 10);

//        scroller = new Scroller(context);
//        maxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mGestureListener = new GestureDetector(context, new RangeBarOnGestureListener());


        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(DisplayUtils.dip2px(getContext(), 12));
            textPaint.setColor(context.getResources().getColor(R.color.common_tips_color));//#F3546A
        }

        if (axisPaintX == null) {
            axisPaintX = new Paint();
            axisPaintX.setStyle(Paint.Style.STROKE);
            axisPaintX.setColor(context.getResources().getColor(R.color.common_tips_color));//#F3546A
            // mPaint.setShadowLayer(3, 0, 0, ContextCompat.getColor(mContex, R.color.common_text_color));
            axisPaintX.setStrokeWidth(2);
            //axisPaintX.setPathEffect(new DashPathEffect(new float[]{15, 2}, 0));
            //axisPaintX.setDither(true);
            //axisPaintX.setAntiAlias(true);
        }
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(context.getResources().getColor(R.color.common_item_bar_line_color));//#F3546A
            // mPaint.setShadowLayer(3, 0, 0, ContextCompat.getColor(mContex, R.color.common_text_color));
            mPaint.setStrokeWidth(2);
           /* mPaint.setPathEffect(new DashPathEffect(new float[]{15, 2}, 0));
            mPaint.setDither(true);
            mPaint.setAntiAlias(true);*/
        }
        unitPaint = new Paint();
        unitPaint.setAntiAlias(true);
        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        unitPaint.setTypeface(typeface);
        unitPaint.setTextSize(DisplayUtils.dip2px(getContext(), 10));

        barPaint = new Paint();
        barPaint.setColor(barColors != null && barColors.length > 0 ? barColors[0] : Color.parseColor("#1DCE74"));
//        barPaint.setStrokeWidth(3);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(Color.parseColor("#1DCE74"));
        borderPaint.setStrokeWidth(DisplayUtils.dip2px(mContext, 2));

        mBarRectClick = new RectF(0, 0, 0, 0);
        mBarRect = new RectF(0, 0, 0, 0);
        mMaxRect = new RectF(0, 0, 0, 0);
        mDrawArea = new RectF(0, 0, 0, 0);

        hitTextPaint = new Paint();

        hitTextPaint.setAntiAlias(true);
        hitTextPaint.setStyle(Paint.Style.FILL);
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
        hitTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(List<BarChartEntity> list, int colors[], String unitX, String unitY) {
        this.mData = list;
        this.barColors = colors;
        this.unitX = unitX;
        this.unitY = unitY;
        if (list != null && list.size() > 0) {
            maxYValue = calculateMax(list);
            getRange(maxYValue);
        }
    }

    List<String> dateList = new ArrayList<>();

    public void setWeekDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    boolean isSport;
    int currentDeviceType = JkConfiguration.DeviceType.WATCH_W516;

    public void setSportMode(boolean isSport) {
        this.isSport = isSport;
    }

    public void setCurrentType(int type) {
        this.currentDeviceType = type;
    }

    /**
     * 计算出Y轴最大值
     *
     * @return
     */

    float minValue = 0;
    float maxValue = 0;

    ArrayList<Float> Ylist = new ArrayList<>();

    private float calculateMax(List<BarChartEntity> list) {
        maxValue = list.get(0).getSum();
        minValue = list.get(0).getSum();

        for (BarChartEntity entity : list) {
            if (entity.getSum() > maxValue) {
                maxValue = entity.getSum();
            }
            if (entity.getSum() < minValue) {
                minValue = entity.getSum();
            }
        }
        Ylist.add(0f);
        Ylist.add(minValue);
        Ylist.add(maxValue);
        Log.e("calculateMax:", "maxVlaue=" + maxValue + "minValue=" + minValue);
        /*if (start > 30) {
            if (start % 30 != 0) {
                start = start + 30 - (start % 30);
            }calculateMax::
        } else if (start >= 0 && start <= 30) {
            if (start % 3 != 0) {
                start = start + 3 - (start % 3);
            }
        }*/
        return maxValue;
    }

    /**
     * 得到柱状图的最大和最小的分度值
     */
    private void getRange(float maxYValue) {
        int scale = CalculateUtil.getScale(maxYValue);//获取这个最大数 数总共有几位
        float unScaleValue = (float) (maxYValue / Math.pow(10, scale));//最大值除以位数之后剩下的值  比如1200/1000 后剩下1.2
        maxYDivisionValue = (float) (unScaleValue * Math.pow(10, scale));//获取Y轴的最大的分度值

        mStartX = textPaint.measureText((maxYValue) + "") + DisplayUtils.dip2px(mContext, 10);

        // mStartX = CalculateUtil.getDivisionTextMaxWidth(maxYDivisionValue, mContext);
        //mStartX = DisplayUtils.dip2px(mContext, 50);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalWidth = w - DisplayUtils.dip2px(mContext, 20);
        mTotalHeight = h;
        maxHeight = h - getPaddingTop() - getPaddingBottom() - bottomMargin - topMargin;
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();

    }

    //获取滑动范围和指定区域 mDarwBarWith
    private void getArea() {
        float mDarwBarWith = 0f;
        mDarwBarWith = (mTotalWidth - mStartX) / (mData.size());
        if (dateList.size() == 7) {


            barWidth = DisplayUtils.dip2px(mContext, 16);


            //barSpace = (mTotalWidth - 2 * mStartX) / mData.size() / 2 - DisplayUtils.dip2px(mContext, 2f);
        } else if (dateList.size() == 24) {

            barWidth = DisplayUtils.dip2px(mContext, 8);
        } else {
            barWidth = DisplayUtils.dip2px(mContext, 6);
        }
        barSpace = (mDarwBarWith - barWidth);
        mStartY = mTotalHeight - bottomMargin - paddingBottom;
        mDrawArea = new RectF(mStartX - DisplayUtils.dip2px(mContext, 10), paddingTop, mTotalWidth + DisplayUtils.dip2px(mContext, 10), mTotalHeight - paddingBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mData == null || mData.isEmpty()) return;
        canvas.drawColor(Color.WHITE);
        getArea();
        drawScaleLine(canvas);
        drawBar(canvas);
    }

    ArrayList<Integer> xList = new ArrayList<>();

    /* private void drawX24AxisText(Canvas canvas) {





     }
 */
   /* private void drawXAxisText(Canvas canvas) {

        if (dateList.size() == 7) {
            *//*for (int i = 0; i < dateList.size(); i++) {
                String xAxis = dateList.get(i);
                float x;
                x = (int) (mStartX + barWidth * i + barSpace * (i + 1));
                canvas.drawText(xAxis, x, mTotalHeight - bottomMargin * 2 / 3 + 10, textPaint);
            }*//*
        } else if (dateList.size() == 0) {
            //这里设置 x 轴的字一条最多显示3个，大于三个就换行
            drawX24AxisText(canvas);
            *//*for (int i = 0; i <= mData.size() - 1; i++) {
                int xAxis = Integer.parseInt(mData.get(i).getxLabel());
                if (xAxis % 6 == 0) {
                    int temp = xAxis;
                    String text = temp + "   ";
                    float x = xAxis == 24 ? (mBarLeftXPoints.get(i) - (textPaint.measureText(text) * 3 / 2 - barWidth)) : (mBarLeftXPoints.get(i) - (textPaint.measureText(text) - barWidth) / 2);
                    canvas.drawText(text, x, mTotalHeight - bottomMargin * 2 / 3 + 10, textPaint);
                }
            }*//*
        } else {
           *//* String text = dateList.get(0);
            if (TextUtils.isEmpty(text)) {
                text = "";
            }
            float x = leftMargin * 3.5f;
            canvas.drawText(text, x, mTotalHeight - bottomMargin * 2 / 3 + DisplayUtils.dip2px(mContext, 5), textPaint);
            for (int i = 1; i < 28; i++) {
                text = dateList.get(i);
                if (TextUtils.isEmpty(text)) {
                    text = "";
                }
                if ((i + 1) % 10 == 0 && mBarLeftXPoints.size() > i) {
                    try {
                        x = (mBarLeftXPoints.get(i) - (textPaint.measureText(text) * 3 / 2 - barWidth));
                        canvas.drawText(text, x, mTotalHeight - bottomMargin * 2 / 3 + +DisplayUtils.dip2px(mContext, 5), textPaint);
                    } catch (Exception e) {

                    }

                }
            }
            text = dateList.get(dateList.size() - 1);
            x = mTotalWidth - (textPaint.measureText(text)) * 1.8f;
            canvas.drawText(text, x, mTotalHeight - bottomMargin * 2 / 3 + DisplayUtils.dip2px(mContext, 5), textPaint);*//*
        }
    }
*/
    private float percent = 1f;
    private TimeInterpolator pointInterpolator = new DecelerateInterpolator();

    public void startAnimation() {
       /* ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(2000);
        mAnimator.setInterpolator(pointInterpolator);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();*/
        invalidate();
    }

    float lineHeight;
    float width;
    float clearanceWidth;//间隔

    private void drawBar(Canvas canvas) {


        Log.e("drawBar", "mData.size()=" + mData.size());
        mBarLeftXPoints.clear();
        mBarRightXPoints.clear();
        mBarRect.bottom = mStartY;
        mMaxRect.bottom = mStartY;


        clearanceWidth = DisplayUtils.dip2px(mContext, 28) / (mData.size() / 7f);
        width = (mTotalWidth - mStartX - clearanceWidth * (mData.size() - 1)) / mData.size();


        for (int i = 0; i < mData.size(); i++) {
            mBarRect.left = (int) ((i * (width + clearanceWidth)) + mStartX);
            mBarRect.top = (int) (mStartY - ((maxHeight * (mData.get(i).getyValue()[0] / maxYDivisionValue)) * percent));
            mBarRect.right = (int) (mBarRect.left + width);
            mMaxRect.left = (int) ((i * (width + clearanceWidth)) + mStartX);
            mMaxRect.top = (int) (mStartY - ((maxHeight * (maxValue / maxYDivisionValue)) * percent));
            mMaxRect.right = (int) (mBarRect.left + width);
            String xAxis = dateList.get(i);
            canvas.drawText(xAxis, (mBarRect.right - ((mBarRect.right - mBarRect.left) / 2) - (textPaint.measureText(xAxis) / 2)), mStartY + (textPaint.measureText("24")), textPaint);
            barPaint.setColor(Color.parseColor("#F7F7F9"));
            canvas.drawRoundRect(mMaxRect, DisplayUtils.dip2px(mContext, 6), DisplayUtils.dip2px(mContext, 6), barPaint);

            if (isDrawBorder && mClickPosition == i) {
                barPaint.setColor(Color.parseColor("#4DDA64"));
            } else {
                barPaint.setColor(Color.parseColor("#804dda64"));
            }
            canvas.drawRoundRect(mBarRect, DisplayUtils.dip2px(mContext, 6), DisplayUtils.dip2px(mContext, 6), barPaint);


            mBarLeftXPoints.add(mMaxRect.left);
            mBarRightXPoints.add(mMaxRect.right);
        }
        if (isDrawBorder) {
            drawHint(canvas);
        }
    }

    private void drawCircle(Canvas canvas, float left, float top, float right, float bottom) {
        RectF rectF = new RectF(left, top, right, bottom);
        //画左边黑色半圆
        canvas.drawArc(rectF, 180, 180, true, barPaint);
    }

    // private int len = 200;

    private RectF drawRoundRect(Canvas canvas, float len) {

        if (centerX == 0) {
            identifyWhichItemClick(mBarLeftXPoints.get(mClickPosition) + DisplayUtils.dip2px(mContext, 10), mBarLeftXPoints.get(mClickPosition) + DisplayUtils.dip2px(mContext, 10));
        }
        int left = (int) (centerX - len);
        int right = (int) (centerX + len);
        RectF rect = new RectF(left, (int) ((topMargin) - DisplayUtils.dip2px(mContext, 20)), right, (topMargin));
        return rect;
    }

    float step, cal, dis;


    private void drawHint(Canvas canvas) {
        if (mData.size() <= mClickPosition) {
            return;
        }
        BarChartEntity barChartEntity = mData.get(mClickPosition);
        if (barChartEntity == null) {
            return;
        }

        //String text = barChartEntity.getxLabel() + ":00  " + (int) step + String.format(UIUtils.getString(R.string.bar_tips),step+"","22","33");
        step = barChartEntity.getyValue()[0];
        if (barChartEntity.getyValue().length >= 2) {
            cal = barChartEntity.getyValue()[1];
        }
        if (barChartEntity.getyValue().length >= 3) {
            dis = barChartEntity.getyValue()[2];
        }
        String text;
        if (isSport) {
            text = step + "";
        } else {
            String showStep;
            /*if (step >= 10000) {
                showStep = CommonDateUtil.formatOnePoint(step / 1000) + "k";
            } else {

            }*/
            showStep = CommonDateUtil.formatInterger(step);
            //text = String.format(UIUtils.getString(R.string.bar_tips), showStep + "", dis + "", CommonDateUtil.formatInterger(cal) + "");
            text = showStep;
        }
        RectF rect = drawRoundRect(canvas, hitTextPaint.measureText(text));

        Paint.FontMetrics fontMetrics = hitTextPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        hitTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        hitTextPaint.setColor(Color.parseColor("#4DDA64"));
        hitTextPaint.setTextSize(DisplayUtils.dip2px(mContext, 12));
        canvas.drawText(text, rect.centerX(), baseLineY, hitTextPaint);


        hitTextPaint.setColor(Color.parseColor("#6E6E77"));
        hitTextPaint.setTypeface(Typeface.DEFAULT);
        hitTextPaint.setTextSize(DisplayUtils.dip2px(mContext, 8));
        canvas.drawText(dateList.get(mClickPosition), rect.centerX(), rect.bottom, hitTextPaint);
    }

    /**
     * Y轴上的text (1)当最大值大于1 的时候 将其分成5份 计算每个部分的高度  分成几份可以自己定
     * （2）当最大值大于0小于1的时候  也是将最大值分成5份
     * （3）当为0的时候使用默认的值
     */
    private void drawScaleLine(Canvas canvas) {
        float eachHeight = (maxHeight / 3f);
        float textValue = 0;


        //画一条最大值和画一条最小值

        int y = (int) (mStartY - ((maxHeight * (maxValue / maxYDivisionValue)) * percent) + barWidth / 2);
        // int y = (int) (mStartY - ((maxHeight * (minValue/ maxYDivisionValue)) * percent) + barWidth / 2);

        for (int i = 0; i < Ylist.size(); i++) {
            // float startY = mStartY - eachHeight * i;
            float startY = (int) (mStartY - (maxHeight * (Ylist.get(i) / maxYDivisionValue)) * percent);
            float drawNumber = 0;
            String text = null;
            //因为图表分了5条线，如果能除不进，需要显示小数点不然数据不准确
            // text = CommonDateUtil.formatInterger(maxValue.multiply(fen).floatValue());
            drawNumber = Ylist.get(i);
            //  text = CommonDateUtil.formatInterger(maxYValue);
            if (drawNumber > 10000) {
                text = CommonDateUtil.formatOnePoint(drawNumber / 1000) + "k";
            } else {
                if (drawNumber < 1 && drawNumber != 0) {
                    text = CommonDateUtil.formatTwoPointThree(drawNumber);
                } else {
                    text = CommonDateUtil.formatInterger(drawNumber);

                }

                Log.e("drawScaleLine", "drawNumber=" + drawNumber + "text=" + text);
            }

            if (text.length() == 1) {
                canvas.drawText(text, mStartX - DisplayUtils.dip2px(mContext, 10) - textPaint.measureText(text), startY + textPaint.measureText("0") / 2, textPaint);
            } else {
                canvas.drawText(text, mStartX - DisplayUtils.dip2px(mContext, 10) - textPaint.measureText(text), startY + textPaint.measureText("0") / 2, textPaint);

            }
            if (i == 0) {
                // canvas.drawLine(mStartX - DisplayUtils.dip2px(mContext, 10), startY, mTotalWidth , startY, axisPaintX);
                //  canvas.drawLine(mStartX, startY, mTotalWidth + DisplayUtils.dip2px(mContext, 20) - paddingRight - rightMargin, startY, axisPaintX);

            } else {
                // canvas.drawLine(mStartX, startY, mTotalWidth + DisplayUtils.dip2px(mContext, 20) - paddingRight - rightMargin, startY, mPaint);

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastPointX = event.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float movex = event.getX();
//                movingThisTime = lastPointX - movex;
//                leftMoving = leftMoving + movingThisTime;
//                lastPointX = movex;
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                invalidate();
//                lastPointX = event.getX();
//                break;
//            case MotionEvent.ACTION_CANCEL:
////                recycleVelocityTracker();
//                break;
//            default:
//                return super.onTouchEvent(event);
//        }
        if (mGestureListener != null) {
            mGestureListener.onTouchEvent(event);
        }
        return true;
    }

    /**
     * 点击
     */
    private class RangeBarOnGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            Log.e("GestureListener", "onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.e("GestureListener", "onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            int position = identifyWhichItemClick(e.getX(), e.getY());
            Log.e("GestureListener", "onSingleTapUp position=" + position);
            if (position != INVALID_POSITION) {
                //mOnItemBarClickListener.onClick(position);
                setClicked(position);
                invalidate();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e("GestureListener", "onScroll");

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

    }

    /**
     * 设置选中的位置
     *
     * @param position
     */
    public void setClicked(int position) {
        isDrawBorder = true;
        mClickPosition = position;
    }

    private float centerX = 0;

    /**
     * 根据点击的手势位置识别是第几个柱图被点击
     *
     * @param x
     * @param y
     * @return -1时表示点击的是无效位置
     */
    private int identifyWhichItemClick(float x, float y) {
        float leftx = 0;
        float rightx = 0;
        if (mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                leftx = mBarLeftXPoints.get(i);
                rightx = mBarRightXPoints.get(i);
                if (x < leftx) {
                    break;
                }
                if (leftx <= x && x <= rightx) {
                    centerX = (leftx + rightx) / 2;
                    return i;
                }
            }
        }
        return INVALID_POSITION;
    }

    public void setmClickPosition(int mClickPosition) {
        isDrawBorder = true;
        this.mClickPosition = mClickPosition;
        invalidate();
    }
}
