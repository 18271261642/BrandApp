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
import android.view.View;

import com.isport.brandapp.R;
import com.isport.brandapp.wu.bean.TempInfo;
import com.isport.brandapp.wu.util.DimenUtil;

import androidx.annotation.Nullable;

/**
 * 自定义单个的温度view
 * Created by Admin
 * Date 2022/2/24
 */
public class SingleTemptureView extends View {


    private Paint backPaint;
    private Paint currPaint;
    //数值的画笔
    private Paint txtPaint;

    private float mWidth,mHeight;

    private TempInfo tempInfo;

    //点击头部的bg图片
    private Paint topImgPaint;

    //最大值
    private float maxValue;
    //每个柱子的宽度
    private float mCurrWidth = 55f;

    public SingleTemptureView(Context context) {
        super(context);
        initAttr(context);
    }

    public SingleTemptureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context);
    }


    private void initAttr(Context context){
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setColor(Color.parseColor("#F7F7F9"));
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setAntiAlias(true);

        currPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currPaint.setColor(getResources().getColor(R.color.common_view_color));
        currPaint.setStyle(Paint.Style.FILL);
        currPaint.setAntiAlias(true);

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //txtPaint.setColor(Color.parseColor("#F7F7F9"));
        txtPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(DimenUtil.dp2px(getContext(),13f));


        topImgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        topImgPaint.setStyle(Paint.Style.FILL);
        topImgPaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,mHeight);
        canvas.save();

        if(tempInfo == null)
            return;



        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_move_bottom);
        //图片的宽高
        float imgWidth = bitmap.getWidth();
        float imgHeight = bitmap.getHeight();
        RectF imgRectF = new RectF((mCurrWidth/2)-(imgWidth/3)+2f,-mHeight,imgWidth+9f,-mHeight+imgHeight);
        if(tempInfo.isClick())
            canvas.drawBitmap(bitmap,null,imgRectF,topImgPaint);



        float middleV = (mHeight -imgHeight)/ maxValue;

        float mRightPoint = mWidth / 2;
        Log.e("TAG","----温度="+tempInfo.toString());
        //判断是摄氏度还是华摄氏度
        float tempValue = Float.valueOf(tempInfo.getTempUnitl().equals("0") ? tempInfo.getCentigrade() : tempInfo.getFahrenheit());
        //绘制底部的数值
        String txt = String.valueOf(tempInfo.getTempUnitl().equals("0") ? tempInfo.getCentigrade() : tempInfo.getFahrenheit());

        Log.e("TMP","-----数值宽度="+DimenUtil.getTextWidth(txtPaint,txt));
        //mRightPoint- DimenUtil.getTextWidth(txtPaint,txt)/2
        canvas.drawText(txt,mWidth/2 - (DimenUtil.getTextWidth(txtPaint,txt)/2),-DimenUtil.measureTextHeight(txtPaint),txtPaint);

        // currPaint.setColor(drawRecDataBean.getColors());

        //总的背景
        RectF bgRectF = new RectF(mRightPoint-mCurrWidth/2,-maxValue* middleV,mRightPoint+mCurrWidth/2,-DimenUtil.measureTextHeight(txtPaint)*2.5f);
        canvas.drawRoundRect(bgRectF,mCurrWidth/2,mCurrWidth/2,backPaint);

        RectF rectF = new RectF(mRightPoint-mCurrWidth/2,-tempValue * middleV,mRightPoint+mCurrWidth/2,-DimenUtil.measureTextHeight(txtPaint)*2.5f);
        canvas.drawRoundRect(rectF,mCurrWidth/2,mCurrWidth/2,currPaint);
    }


    public TempInfo getTempInfo() {
        return tempInfo;
    }

    public void setTempInfo(TempInfo tempInfo) {
        this.tempInfo = tempInfo;
        invalidate();
    }


    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }
}
