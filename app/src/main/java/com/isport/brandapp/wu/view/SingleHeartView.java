package com.isport.brandapp.wu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.isport.brandapp.R;
import com.isport.brandapp.wu.bean.DrawRecDataBean;
import com.isport.brandapp.wu.util.DimenUtil;

import androidx.annotation.Nullable;

/**
 * Created by Admin
 * Date 2022/2/21
 */
public class SingleHeartView extends View {

    private static final String TAG = "SingleHeartView";

    private Paint backPaint;
    private Paint currPaint;
    //数值的画笔
    private Paint txtPaint;

    //点击头部的bg图片
    private Paint topImgPaint;

    private float mWidth,mHeight;




    private int currValue;
    private int maxValue;


    //每个柱子的宽度
    private float mCurrWidth = 50f;

    private DrawRecDataBean drawRecDataBean;

    public SingleHeartView(Context context) {
        super(context);
    }

    public SingleHeartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }


    private void initAttrs(AttributeSet attributeSet){
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setColor(Color.parseColor("#F7F7F9"));
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setAntiAlias(true);

        currPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      //  currPaint.setColor(drawRecDataBean==null?  Color.parseColor("#BDC1C7") : drawRecDataBean.getColors());
        currPaint.setColor(getResources().getColor(R.color.common_view_color));
        currPaint.setStyle(Paint.Style.FILL);
        currPaint.setAntiAlias(true);

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //txtPaint.setColor(Color.parseColor("#F7F7F9"));
        txtPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(DimenUtil.dp2px(getContext(),14f));


        topImgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        topImgPaint.setStyle(Paint.Style.FILL);
        topImgPaint.setAntiAlias(true);

       // bgBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon_bottom_move);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        Log.e(TAG,"----宽度="+mWidth+" 高度="+mHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(0,mHeight);
        canvas.save();

        if(drawRecDataBean == null)
            return;



        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_move_bottom);
        //图片的宽高
        float imgWidth = bitmap.getWidth();
        float imgHeight = bitmap.getHeight();
        RectF imgRectF = new RectF((mCurrWidth/2)-(imgWidth/3)+2f,-mHeight,imgWidth+8f,-mHeight+imgHeight);

        //Rect srcRect = new Rect((int)(mCurrWidth / 2 - (imgWidth / 2)),-(int) mHeight,(int) imgWidth,-(int)( mHeight+imgHeight));

        if(drawRecDataBean.isClick())
            canvas.drawBitmap(bitmap,null,imgRectF,topImgPaint);


        float middleV = (mHeight-imgHeight) / maxValue;

        float mRightPoint = mWidth / 2;

        //绘制底部的数值
        String txt = String.valueOf(drawRecDataBean.getValue());
        canvas.drawText(txt,mRightPoint- DimenUtil.getTextWidth(txtPaint,txt)/2,-DimenUtil.measureTextHeight(txtPaint),txtPaint);

       // currPaint.setColor(drawRecDataBean.getColors());

        //总的背景
        RectF bgRectF = new RectF(mRightPoint-mCurrWidth/2,-maxValue * middleV,mRightPoint+mCurrWidth/2,-DimenUtil.measureTextHeight(txtPaint)*2.5f);
        canvas.drawRoundRect(bgRectF,mCurrWidth/2,mCurrWidth/2,backPaint);

        currPaint.setColor(drawRecDataBean.getColors());
        Log.e("BP","------value="+drawRecDataBean.getValue());
        RectF rectF = new RectF(mRightPoint-mCurrWidth/2,-drawRecDataBean.getValue() * middleV,mRightPoint+mCurrWidth/2,-DimenUtil.measureTextHeight(txtPaint)*2.5f);
        canvas.drawRoundRect(rectF,mCurrWidth/2,mCurrWidth/2,currPaint);

    }


    public void setCurrValue(int currValue) {
        this.currValue = currValue;
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public DrawRecDataBean getDrawRecDataBean() {
        return drawRecDataBean;
    }

    public void setDrawRecDataBean(DrawRecDataBean drawRecDataBean) {
        this.drawRecDataBean = drawRecDataBean;
        invalidate();

    }


    public int getMaxValue() {
        return maxValue;
    }
}
