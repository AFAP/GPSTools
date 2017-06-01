package com.afap.gpstools.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.afap.gpstools.R;

/**
 * 罗盘控件
 */

public class CompassView extends View {
    private final static int SIZI_TEXT_SP = 16;
    private final static int MARGIN_CIRCLE_DP = 5;
    private final static int POINT_O_R = 3;
    private final static int SIZE_BORDER = 1;
    private final static float SIZE_LINE = 0.5f;


    public CompassView(Context context) {
        super(context);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMySize(100, widthMeasureSpec);

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Context context = getContext();

        String east = context.getString(R.string.east);
        String west = context.getString(R.string.west);
        String south = context.getString(R.string.south);
        String north = context.getString(R.string.north);

        float size_text = sp2px(context, SIZI_TEXT_SP);
        float margin_circle = dp2px(context, MARGIN_CIRCLE_DP);
        float point_o_r = dp2px(context, POINT_O_R);
        float size_border = dp2px(context, SIZE_BORDER);
        float size_line = dp2px(context, SIZE_LINE);

        int w = getMeasuredWidth();

        float o_x = getLeft() + w / 2; // 圆心-X
        float o_y = getTop() + w / 2; // 圆心-Y
        float r = w / 2 - size_text - margin_circle;


        Paint fontPaint = new Paint();
        fontPaint.setColor(Color.BLACK);
//        fontPaint.setTypeface(Typeface.DEFAULT_BOLD);
        fontPaint.setAntiAlias(true);//去除锯齿
        fontPaint.setTextSize(size_text);

        canvas.translate(o_x, o_y);

        canvas.drawText(north, -fontPaint.measureText(north) / 2, -o_y + size_text, fontPaint);
        canvas.rotate(90);
        canvas.drawText(east, -fontPaint.measureText(east) / 2, -o_y + size_text, fontPaint);
        canvas.rotate(90);
        canvas.drawText(south, -fontPaint.measureText(south) / 2, -o_y + size_text, fontPaint);
        canvas.rotate(90);
        canvas.drawText(west, -fontPaint.measureText(west) / 2, -o_y + size_text, fontPaint);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);//去除锯齿
        canvas.drawCircle(0, 0, point_o_r, paint);
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(size_border);
        canvas.drawCircle(0, 0, r, paint);
        canvas.drawCircle(0, 0, r * 0.66f, paint);
        canvas.drawCircle(0, 0, r * 0.33f, paint);

        paint.setStrokeWidth(size_line);
        canvas.drawLine(-r, 0, r, 0, paint);
        canvas.drawLine(0, -r, 0, r, paint);

    }

    private float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

}
