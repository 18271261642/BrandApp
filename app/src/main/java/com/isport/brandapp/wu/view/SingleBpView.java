package com.isport.brandapp.wu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.isport.brandapp.R;
import com.isport.brandapp.wu.bean.BPInfo;
import com.isport.brandapp.wu.util.DimenUtil;

import androidx.annotation.Nullable;

/**
 * Created by Admin
 * Date 2022/2/24
 */
public class SingleBpView extends View {

    private static final String TAG = "SingleBpView";


    private int startRawX;
    private int startRawY;

    private Paint backPaint;
    private Paint lBpPaint; //低压的画笔
    private Paint hBpPaint; //高压的画笔
    //数值的画笔
    private Paint txtPaint;
    //顶部点击图片的画笔
    private Paint imgPaint;

    private float mWidth,mHeight;

    private int maxValue;
    private BPInfo bpInfo;

    //单个柱子的宽度
    private float signalWidth = 30f;
    //间隔
    private float signalInterval = 8f;

    //是否点击，点击绘制顶部图片
    private boolean isClick = false;


    public SingleBpView(Context context) {
        super(context);
        initAttar(context);
    }


    public SingleBpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttar(context);
    }



    private void initAttar(Context context){
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setColor(Color.parseColor("#F7F7F9"));
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setAntiAlias(true);


        hBpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hBpPaint.setColor(Color.parseColor("#FD3C30"));
        hBpPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        hBpPaint.setAntiAlias(true);

        lBpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lBpPaint.setColor(Color.parseColor("#127AFF"));
        lBpPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        lBpPaint.setAntiAlias(true);

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextSize(DimenUtil.dp2px(context,13f));
        txtPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        imgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        imgPaint.setTextAlign(Paint.Align.CENTER);
        imgPaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        Log.e("BP","----宽高="+mWidth+" "+mHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,mHeight);
        canvas.save();

        if(bpInfo == null)
            return;

        Log.e("BP","----bpInfo="+mWidth+" "+mHeight+" "+bpInfo.toString());




        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_move_bottom);

        //中心点
        float mid = (signalInterval/2)+signalWidth;

        //图片的宽高
        float imgWidth = bitmap.getWidth();
        float imgHeight = bitmap.getHeight();
        RectF rectF = new RectF(mid-(imgWidth/2),-mHeight,mid +(imgWidth/2),-mHeight+imgHeight);




        if(bpInfo.isClick())
        canvas.drawBitmap(bitmap,null,rectF,imgPaint);


        //329
        float middleV = (mHeight-imgHeight) / maxValue;

        //绘制高压和低压的数值
        int hBpValue = bpInfo.getSpValue();
        int lBpValue = bpInfo.getDpValue();


        //居中
        float md = signalWidth + signalInterval/2;
        txtPaint.setColor(Color.RED); //md-DimenUtil.getTextWidth(txtPaint,String.valueOf(hBpPaint))/2
        canvas.drawText(String.valueOf(hBpValue),signalWidth/3,-DimenUtil.measureTextHeight(txtPaint) * 3f,txtPaint);

        txtPaint.setColor(Color.BLUE);
        canvas.drawText(String.valueOf(lBpValue),(signalWidth/3)+2f,-DimenUtil.measureTextHeight(txtPaint),txtPaint);

        float startY = -DimenUtil.measureTextHeight(txtPaint) * 5f;


        //绘制两个背景
        RectF rectF1 = new RectF(0f,-maxValue*middleV,signalWidth,startY);
        canvas.drawRoundRect(rectF1,15f,15f,backPaint);

        RectF rectF2 = new RectF(signalInterval+signalWidth,-maxValue*middleV,signalWidth*2+signalInterval,startY);
        canvas.drawRoundRect(rectF2,15f,15f,backPaint);



        //绘制两个血压值柱子
        RectF hVR = new RectF(0f,hBpValue>=maxValue ? -maxValue * middleV : -hBpValue*middleV,signalWidth,startY);
        canvas.drawRoundRect(hVR,15f,15f,hBpPaint);
        float lppValue = -lBpValue*middleV+startY/2;

        RectF lVR = new RectF(signalInterval+signalWidth,Math.abs(lppValue)>=Math.abs(-maxValue*middleV) ? -maxValue* middleV :-lBpValue*middleV+startY/2,signalWidth*2+signalInterval,startY);
        canvas.drawRoundRect(lVR,15f,15f,lBpPaint);






        //绘制头部的图片效果
      //  canvas.drawBitmap(bitmap,0,-maxValue * middleV,imgPaint);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.e(TAG,"--22-dispatchTouchEvent--="+event.getAction());

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.e(TAG,"--22-触摸事件--="+event.getAction());

        if(event.getAction() == MotionEvent.ACTION_DOWN){   //按下


        }

        if(event.getAction() == MotionEvent.ACTION_HOVER_EXIT){

        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startRawX = rawX;
                startRawY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    Log.e(TAG,"-----action_Up=");
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }


    public void setBpInfo(BPInfo bpInfo) {
        this.bpInfo = bpInfo;
        invalidate();
    }
}
