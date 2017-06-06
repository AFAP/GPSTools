package com.afap.gpstools.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.afap.gpstools.utils.LogUtil;

/**
 * 指针
 */
public class PointerView extends View {
    public PointerView(Context context) {
        super(context);
    }

    public PointerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PointerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.d("PointerView", "onDraw");

        int w = getMeasuredWidth();
        canvas.translate(w / 2, w / 2); // 移动原点到中心位置

        float h = w * 0.36f;
        float r = w * 0.04f;

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAlpha(164);
        paint.setAntiAlias(true); //去除锯齿
        paint.setStyle(Paint.Style.FILL); //设置空心

        Path path = new Path();
        path.moveTo(-r, 0);
        path.lineTo(0, h);
        path.lineTo(r, 0);
        canvas.drawPath(path, paint);

        path.reset();
        path.moveTo(-r, 0);
        path.lineTo(0, -h);
        path.lineTo(r, 0);


        paint.setColor(Color.RED);
        paint.setAlpha(164);
        canvas.drawPath(path, paint);
    }


}
