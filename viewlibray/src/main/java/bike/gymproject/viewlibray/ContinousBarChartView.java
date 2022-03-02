package bike.gymproject.viewlibray;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import bike.gymproject.viewlibray.chart.ContinousBarChartEntity;
import bike.gymproject.viewlibray.chart.ContinousBarChartTotalEntity;
import phone.gym.jkcq.com.commonres.commonutil.CalculateUtil;
import phone.gym.jkcq.com.commonres.commonutil.DisplayUtils;

/**
 *
 */
public class ContinousBarChartView extends View {

    private static final String TAG = "ContinousBarChartView";
    
    private int deviceType = 0;
    /**
     * //第一阶段  清醒
     * // paint.setColor(Color.parseColor("#4BC4FF"))  0xFF4DDA64;
     * paint.setColor(0xFFFD944A);
     * canvas.drawArc(arcRectF, -90, mFirstAngle, false, paint);
     * //第二阶段 浅睡
     * paint.setColor(0xFF50E39C);#4194FF
     * canvas.drawArc(arcRectF, mFirstAngle - 90, mSecondAngle, false, paint);
     * //第三阶段 深睡
     * // paint.setColor(Color.parseColor("#FD944A")) #5856D7;
     * <p>
     * paint.setColor(0xFF4BC4FF);
     * canvas.drawArc(arcRectF, mFirstAngle + mSecondAngle - 90, mThirdAngle, false, paint);
     */
    private final int color[] = {0xFFADADBB, 0xFF4DDA64, 0xFF4194FF, 0xFF5856D7, 0xFF4DDA64};
    private String type[];
    private Context mContext;
    /**
     * 视图的宽和高  刻度区域的最大值
     */
    private int mTotalWidth, mTotalHeight, maxHeight;
    private int paddingRight, paddingBottom, paddingTop, paddingLeft;
    //距离底部的多少 用来显示底部的文字
    private int bottomMargin;
    //距离顶部的多少 用来显示顶部的文字
    private int topMargin;
    private int rightMargin;
    private int leftMargin;
    private Paint tringlePaint;
    /**
     * 画笔 轴 刻度 柱子 点击后的柱子 单位
     */
    private Paint axisPaint, textPaint, barPaint, borderPaint, unitPaint;
    private List<ContinousBarChartEntity> mData;//数据集合
    /**
     * item中的Y轴最大值
     */
    private float maxYValue;
    private int leftMoving;
    /**
     * Y轴最大的刻度值
     */
    private float maxYDivisionValue;
    /**
     * 柱子的矩形
     */
    private RectF mBarRect; //mBarRect,
    private RectF mBgRegt;
    private Rect mBarRectClick;
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
    private int barSpace;
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
    private OnItemBarClickListener mOnItemBarClickListener;
    private GestureDetector mGestureListener;
    Paint paint;
    /**
     * 是否绘制点击效果
     */
    private boolean isDrawBorder;
    /**
     * 点击的地方
     */
    private int mClickPosition = 0;

    //x轴 y轴的单位
    private String unitX;
    private String unitY;

    public void setOnItemBarClickListener(OnItemBarClickListener onRangeBarClickListener) {
        this.mOnItemBarClickListener = onRangeBarClickListener;
    }

    public void setisDrawBorder() {
        isDrawBorder = false;
    }

    public interface OnItemBarClickListener {
        void onClick(int color, int position, int hour, int minute);

        void onSelectSleepState(String value);
    }

    public ContinousBarChartView(Context context) {
        this(context, null);
    }

    public ContinousBarChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContinousBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCurrentType(int type) {
        this.deviceType = type;
    }

    private void init(Context context) {
        mContext = context;
        type = new String[5];
        type[0] = mContext.getString(R.string.sleep_awake);  //清醒
        type[1] = mContext.getString(R.string.light_1sleep);  //快速眼动
        type[2] = mContext.getString(R.string.light_2sleep);  //浅睡
        type[3] = mContext.getString(R.string.sleep_deep);   //深睡
        type[4] = mContext.getString(R.string.sleep_awake);   //清醒

      /*  type[] = new String[]{mContext.getString(R.string.sleep_awake),
                mContext.getString(R.string.light_1sleep),
                mContext.getString(R.string.light_2sleep),
                mContext.getString(R.string.sleep_deep),
                mContext.getString(R.string.sleep_awake)};*/
        topMargin = DisplayUtils.dip2px(getContext(), 15);
        bottomMargin = DisplayUtils.dip2px(getContext(), 10);
        rightMargin = DisplayUtils.dip2px(getContext(), 0);
        leftMargin = DisplayUtils.dip2px(getContext(), 0);

        mGestureListener = new GestureDetector(context, new RangeBarOnGestureListener());

        axisPaint = new Paint();
        axisPaint.setColor(ContextCompat.getColor(mContext, R.color.common_white));
        axisPaint.setStrokeWidth(1);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(DisplayUtils.dip2px(getContext(), 10));

        unitPaint = new Paint();
        unitPaint.setAntiAlias(true);
        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        unitPaint.setTypeface(typeface);
        unitPaint.setTextSize(DisplayUtils.dip2px(getContext(), 10));

        barPaint = new Paint();
        barPaint.setColor(Color.parseColor("#FF0000"));

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DisplayUtils.dip2px(mContext, 1));

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(context.getResources().getColor(R.color.common_white));
        bgPaint.setStyle(Paint.Style.FILL);


        //        barPaint.setStrokeWidth(3);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(Color.WHITE);
//        borderPaint.setAlpha(120);


        mBarRectClick = new Rect(0, 0, 0, 0);
        mBarRect = new RectF(0, 0, 0, 0);
        mBgRegt = new RectF(0, 0, 0, 0);
        mDrawArea = new RectF(0, 0, 0, 0);
    }

    private String startTime;
    private String endTime;

    public void setData(ContinousBarChartTotalEntity continousBarChartTotalEntity, String unitX, String unitY) {

        this.startTime = continousBarChartTotalEntity.startTime;
        this.endTime = continousBarChartTotalEntity.endTime;
        this.mData = continousBarChartTotalEntity.continousBarChartEntitys;
        this.startCalendar = continousBarChartTotalEntity.starCalendar;
        this.unitX = unitX;
        this.unitY = unitY;
        if (mData != null && mData.size() > 0) {
            maxYValue = calculateMax(mData);
            getRange(maxYValue);
        }
        mClickPosition = 0;
        setHourAndMinute();

    }

    /**
     * 计算出Y轴最大值
     *
     * @return
     */
    private float calculateMax(List<ContinousBarChartEntity> list) {
        float start = list.get(0).yValue;
        for (ContinousBarChartEntity entity : list) {
            if (entity.yValue > start) {
                start = entity.yValue;
            }
        }
        return start;
    }

    /**
     * 得到柱状图的最大和最小的分度值
     */
    private void getRange(float maxYValue) {
        int scale = CalculateUtil.getScale(maxYValue);//获取这个最大数 数总共有几位
        float unScaleValue = (float) (maxYValue / Math.pow(10, scale));//最大值除以位数之后剩下的值  比如1200/1000 后剩下1.2
        maxYDivisionValue = (float) (CalculateUtil.getRangeTop(unScaleValue) * Math.pow(10, scale));//获取Y轴的最大的分度值
        mStartX = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        maxHeight = h - getPaddingTop() - getPaddingBottom() - bottomMargin - topMargin;
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();

    }

    //获取滑动范围和指定区域
    private void getArea() {
        //规定1分钟一个BarWidth的间距
        // int size = Integer.parseInt(endTime) + 24 - Integer.parseInt(startTime);//12
        //int miniuteSize = size * 60 / 1;
        // int size = mData.size();//12
        int miniuteSize = mData.size();
        // barWidth = (mTotalWidth - mStartX * 2) * 1.0f / miniuteSize;
        barWidth = (mTotalWidth - mStartX * 2) * 1.0f / miniuteSize;
        barSpace = 0;
        mStartY = mTotalHeight - bottomMargin - paddingBottom;
        mDrawArea = new RectF(mStartX, paddingTop, mTotalWidth - paddingRight - rightMargin, mTotalHeight - paddingBottom);
    }

    private RectF bgRect;
    private Paint bgPaint;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // drawBg(canvas);
        if (mData == null || mData.isEmpty()) return;
        getArea();
        //画左边的Y轴
//        canvas.drawLine(mStartX, mStartY+bottomMargin/2, mStartX, topMargin / 2, axisPaint);
        //画右边的Y轴
//        canvas.drawLine(mTotalWidth - paddingRight - rightMargin, mStartY+bottomMargin/2, mTotalWidth - paddingRight - rightMargin, topMargin / 2, axisPaint);
        //绘制刻度线 和 刻度
//        drawScaleLine(canvas);
        //调用clipRect()方法后，只会显示被裁剪的区域
//        canvas.clipRect(mDrawArea.left, mDrawArea.top, mDrawArea.right, mDrawArea.bottom + mDrawArea.height());
        //绘制柱子
        drawBar(canvas);
        //绘制X轴的text
        drawXAxisText(canvas);
    }

    private void drawXAxisText(Canvas canvas) {
        //这里设置 x 轴的字一条最多显示3个，大于三个就换行
        // int totalPeriod = Integer.parseInt(endTime) + 24 - Integer.parseInt(startTime);
        textPaint.setColor(Color.parseColor("#2F2F33"));
        textPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        canvas.drawText(startTime, mStartX + (textPaint.measureText(startTime)) / 2, mStartY - DisplayUtils.dip2px(mContext, 40), textPaint);
        //canvas.drawText(endTime, mData.size() * barWidth, mTotalHeight - bottomMargin * 2 / 3 + DisplayUtils.dip2px(mContext, 5), textPaint);
        canvas.drawText(endTime, mTotalWidth - (textPaint.measureText(endTime)) * 1.5f, mStartY - DisplayUtils.dip2px(mContext, 40), textPaint);


      /*  int size = 4;
        int distance = totalPeriod / size;//3
        for (int i = 0; i <= size; i++) {//0--4
            int xAxis = Integer.parseInt(startTime) + distance * i;
            if (xAxis > 24) {
                xAxis = xAxis - 24;
            }
            String text = xAxis + ":00";
            float left = mStartX + distance * i * 60 * barWidth;
            canvas.drawText(text, left - (textPaint.measureText(text)) / 2, mTotalHeight - bottomMargin * 2 / 3 + 10, textPaint);
        }*/
    }

    private float percent = 1f;
    private TimeInterpolator pointInterpolator = new DecelerateInterpolator();

    public void startAnimation() {
        ValueAnimator mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(2000);
        mAnimator.setInterpolator(pointInterpolator);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mAnimator.start();
    }

    Rect src;
    Rect dst;
    private float lastEndX;
    Paint gbPaint;

    boolean isHasSleep = false;

    private void drawBar(Canvas canvas) {
        mBarLeftXPoints.clear();
        mBarRightXPoints.clear();
       /* Rect topRect = new Rect(0, 0, mTotalWidth, mStartY);
        Rect bottomRect = new Rect(0, mStartY, mTotalWidth, mTotalHeight);
        canvas.drawRect(topRect, borderPaint);
        canvas.drawRect(bottomRect, borderPaint);*/
        lastEndX = (int) mStartX;
        mBarRect.bottom = mStartY;
        mBarRect.top = mStartY - DisplayUtils.dip2px(mContext, 30);
        mBarRect.left = 0;
        mBarRect.right = mTotalWidth;

        mBgRegt.bottom = mStartY;
        mBgRegt.top = mStartY - DisplayUtils.dip2px(mContext, 30);
        mBgRegt.left = 0;
        mBgRegt.right = mTotalWidth;
        isHasSleep = false;
        for (int i = 0; i < mData.size(); i++) {
            barPaint.setColor(color[mData.get(i).type]);
            if (mData.get(i).type != 0) {
                isHasSleep = true;
            }
            mBarRect.top = mStartY - DisplayUtils.dip2px(mContext, 30);//(int) ((maxHeight * (mData.get(i).yValue / maxYDivisionValue)) * percent);
            int scale = mData.get(i).period;//间隔时间多少份
            float length = barWidth * (scale);
            // float length = barWidth * (scale + 1);

            mBarRect.left = lastEndX;// + barSpace * (i + 1) - leftMoving
            if (i == mData.size() - 1) {
                mBarRect.right = mTotalWidth;
            } else {
                mBarRect.right = (mBarRect.left + length);
            }
            lastEndX = (lastEndX + length);

            canvas.drawRect(mBarRect, barPaint);
            //canvas.drawCircle(left + DisplayUtils.dip2px(mContext,10), DisplayUtils.dip2px(mContext,10), barPaint);
            mBarLeftXPoints.add(mBarRect.left);
            mBarRightXPoints.add(mBarRect.right);
        }
        // 还原混合模式

        if (isHasSleep) {
            drawTringle(canvas);
        }
//        drawHint();
        gbPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        Bitmap girlBitmap = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.bg_sleep_mengceng)).getBitmap();
        src = new Rect();
        src.set(0, 0, girlBitmap.getWidth(), girlBitmap.getHeight());
        dst = new Rect();
        dst.set(0, mStartY - DisplayUtils.dip2px(mContext, 30), mTotalWidth, mStartY);
        canvas.drawBitmap(girlBitmap, src, dst, gbPaint);

    }

    LinearGradient lg;

    private void drawTringle(Canvas canvas) {

        paint.setColor(color[mData.get(mClickPosition).type]);
        paint.setShader(null);
        int len = DisplayUtils.dip2px(mContext, 5);
        if (centerX < DisplayUtils.dip2px(mContext, 5)) {
            centerX = DisplayUtils.dip2px(mContext, 5);
        } else if (centerX + DisplayUtils.dip2px(mContext, 5) >= mTotalWidth) {
            centerX = mTotalWidth - DisplayUtils.dip2px(mContext, 5);
        }
        int left = (int) (centerX - len);
        int right = (int) (centerX + len);
        Path path2 = new Path();
        path2.moveTo(left, mStartY + DisplayUtils.dip2px(mContext, 5));
        path2.lineTo(right, mStartY + DisplayUtils.dip2px(mContext, 5));
        path2.lineTo(centerX, mStartY + DisplayUtils.dip2px(mContext, 5) - len);
        path2.close();
        canvas.drawPath(path2, paint);
        int startColor = mContext.getResources().getColor(R.color.white_0);
        int endColor = mContext.getResources().getColor(R.color.white);

        lg = new LinearGradient(centerX, mStartY - len + 1, centerX, mStartY - DisplayUtils.dip2px(mContext, 30), endColor, startColor, Shader.TileMode.MIRROR);
        paint.setShader(lg);
        canvas.drawLine(centerX, mStartY, centerX, mStartY - DisplayUtils.dip2px(mContext, 30), paint);

    }


    private Calendar startCalendar;

    private void drawHint(MotionEvent e) {
        int position = identifyWhichItemClick(e.getX(), e.getY());
        setClicked(position);
        setHourAndMinute();
        if(mData == null)
            return;
       // Log.e(TAG,"----drawHint----="+position);
        if (mData != null && mClickPosition >= mData.size()) {
            return;
        }
        assert mData != null;
        if(mData.size()==0)
            return;
        ContinousBarChartEntity barChartEntity = mData.get(mClickPosition);

        String startTime = SleepFormatUtils.sleepTimeFormatByIndex(startPosition, startCalendar);
        String endTime = "";
        if (deviceType == 0) {
            endTime = SleepFormatUtils.sleepTimeEndFormatByIndex(endPosition , startCalendar);
        } else {
            endTime = SleepFormatUtils.sleepTimeEndFormatByIndex(endPosition + 1, startCalendar);
        }
        //这里画已个点击的矩形区域
        String text = type[barChartEntity.type] + startTime + "～" + endTime;//barChartEntity.getxLabel()+":00  "+barChartEntity.getyValue();
        if (mOnItemBarClickListener != null) {
            if (isHasSleep) {
                mOnItemBarClickListener.onSelectSleepState(text);
                mOnItemBarClickListener.onClick(color[mData.get(mClickPosition).type], 0, hour, minute);
            } else {
                mOnItemBarClickListener.onSelectSleepState("");
                mOnItemBarClickListener.onClick(color[mData.get(mClickPosition).type], 0, 0, 0);
            }
        }
    }


    private String getTime(int position) {
        int hour = Integer.parseInt(startTime) + position / 60;
        if (hour > 24) {
            hour = hour - 24;
        }
        int minute = position % 60;
        return hour + ":" + minute;
    }

    int startPosition, endPosition;

    private void setHourAndMinute() {

        if (mData == null || mData.size() ==0) {
            hour = 0;
            minute = 0;
        } else {

            ContinousBarChartEntity barChartEntity = mData.get(mClickPosition);
            int targetType = barChartEntity.type;

            startPosition = mClickPosition;
            endPosition = mClickPosition;

            for (int i = mClickPosition; i >= 0; i--) {
                if (mData.get(i).type == targetType) {
                    startPosition = i;
                } else {
                    break;
                }
            }

            for (int i = mClickPosition; i < mData.size(); i++) {
                if (mData.get(i).type == targetType) {
                    endPosition = i;
                } else {
                    break;
                }
            }
            //由于index是由0开始要+1,而且要包含startPosition，所以还要+1
            int lenTime = endPosition - startPosition + 1;
            if (lenTime > 0) {
                hour = lenTime / 60;
                minute = lenTime % 60;
            } else {
                hour = 0;
                minute = 0;
            }
        }

    }


    /**
     * Y轴上的text (1)当最大值大于1 的时候 将其分成5份 计算每个部分的高度  分成几份可以自己定
     * （2）当最大值大于0小于1的时候  也是将最大值分成5份
     * （3）当为0的时候使用默认的值
     */
    private void drawScaleLine(Canvas canvas) {
        float eachHeight = (maxHeight / 3f);
        float textValue = 0;
        if (maxYValue > 1) {
            for (int i = 0; i <= 3; i++) {
                float startY = mStartY - eachHeight * i;
                BigDecimal maxValue = new BigDecimal(maxYDivisionValue);
                BigDecimal fen = new BigDecimal(0.2 * i);
                String text = null;
                //因为图表分了5条线，如果能除不进，需要显示小数点不然数据不准确
                if (maxYDivisionValue % 3 != 0) {
                    text = String.valueOf(maxValue.multiply(fen).floatValue());
                } else {
                    text = String.valueOf(maxValue.multiply(fen).longValue());
                }
                canvas.drawText(text, mStartX - textPaint.measureText(text) - 5, startY + textPaint.measureText("0") / 2, textPaint);
                canvas.drawLine(mStartX, startY, mTotalWidth - paddingRight - rightMargin, startY, axisPaint);
            }
        } else if (maxYValue > 0 && maxYValue <= 1) {
            for (int i = 0; i <= 3; i++) {
                float startY = mStartY - eachHeight * i;
                textValue = CalculateUtil.numMathMul(maxYDivisionValue, (float) (0.2 * i));
                String text = String.valueOf(textValue);
                canvas.drawText(text, mStartX - textPaint.measureText(text) - 5, startY + textPaint.measureText("0") / 2, textPaint);
                canvas.drawLine(mStartX, startY, mTotalWidth - paddingRight - rightMargin, startY, axisPaint);
            }
        } else {
            for (int i = 0; i <= 3; i++) {
                float startY = mStartY - eachHeight * i;
                String text = String.valueOf(10 * i);
                canvas.drawText(text, mStartX - textPaint.measureText(text) - 5, startY + textPaint.measureText("0") / 2, textPaint);
                canvas.drawLine(mStartX, startY, mTotalWidth - paddingRight - rightMargin, startY, axisPaint);
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
////                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
////                invalidate();
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
           // Log.e(TAG,"-----onDown----="+e.getAction());
            drawHint(e);
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Log.e(TAG,"-----onSingleTapUp----="+e.getAction());
            int position = identifyWhichItemClick(e.getX(), e.getY());
           // Log.e(TAG,"----position="+position);
            //如果是清醒状态是不能点
            if (position != INVALID_POSITION && mOnItemBarClickListener != null && mData.size() > 1) {

                setClicked(position);
                ContinousBarChartEntity barChartEntity = mData.get(mClickPosition);
                if (barChartEntity.type != 0) {
                    setHourAndMinute();
                    mOnItemBarClickListener.onClick(color[mData.get(mClickPosition).type], position, hour, minute);
                    invalidate();
                }

            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
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

    private int hour, minute;

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
}
