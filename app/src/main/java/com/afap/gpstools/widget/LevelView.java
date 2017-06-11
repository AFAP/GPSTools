package com.afap.gpstools.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.afap.gpstools.utils.LogUtil;

/**
 * 水平仪
 */
public class LevelView extends View {

    private float radius_compass = 0;
    private float radius = 0;
    private float pitch = 0; // x轴偏转
    private float roll = 0; // Y轴偏转


    public LevelView(Context context) {
        super(context);
    }

    public LevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    public void setRadius(float r) {
        radius_compass = r;
        radius = r / 3;
        postInvalidate();
    }

    public void updateXY(float x0, float y0) {
        pitch = x0;
        roll = y0;
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (radius == 0) {
            return;
        }

        int w = getMeasuredWidth();
        float offset_y = (float) (radius_compass * Math.sin(pitch));
        float offset_x = (float) (-radius_compass * Math.sin(roll));

        double p = Math.sqrt(Math.pow(Math.sin(pitch), 2) + Math.pow(Math.sin(roll), 2));
        float pr = p > 1 ? 1 : (float) p;
        float r = radius * (1 - pr);
        canvas.translate(w / 2 + offset_x, w / 2 + offset_y); // 移动原点到中心位置

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAlpha(50);
        paint.setAntiAlias(true); //去除锯齿
        paint.setStyle(Paint.Style.FILL); //设置空心
        // 最外围的圆圈
        canvas.drawCircle(0, 0, r, paint);
    }

}
