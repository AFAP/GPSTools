package com.afap.gpstools.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.location.GpsSatellite;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.afap.gpstools.R;
import com.afap.gpstools.utils.LogUtil;

import java.util.List;

/**
 * 罗盘控件
 */

public class CompassView extends View {
    private final static int SIZI_TEXT_SP = 16;
    private final static int SIZI_TEXT_DEGREE_SP = 10;

    private final static int MARGIN_CIRCLE_DP = 5;
    private final static int POINT_O_R = 3;
    private final static int SIZE_BORDER = 1;
    private final static float SIZE_LINE = 0.5f;


    private boolean finishi = false;
    private List<GpsSatellite> gpsSatellites; // 卫星列表
    private float r, point_o_r, size_border, size_text_degree;


    public CompassView(Context context) {
        super(context);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.d("CompassView", "onDraw");

        Context context = getContext();

        String[] directions = context.getResources().getStringArray(R.array.directions);


        float size_text = sp2px(context, SIZI_TEXT_SP);
        size_text_degree = sp2px(context, SIZI_TEXT_DEGREE_SP);

        float margin_circle = dp2px(context, MARGIN_CIRCLE_DP);
        point_o_r = dp2px(context, POINT_O_R);
        size_border = dp2px(context, SIZE_BORDER);
        float size_line = dp2px(context, SIZE_LINE);

        int w = getMeasuredWidth();

        float o_x = w / 2; // 圆心-X
        float o_y = w / 2; // 圆心-Y
        float r0 = w / 2 - size_text - margin_circle;
        r = r0 - size_text_degree * 1.2f;


        canvas.translate(o_x, o_y); // 移动原点到圆心位置


        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true); //去除锯齿
        canvas.drawCircle(0, 0, point_o_r, paint);
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(size_border);
        // 最外围的圆圈
        canvas.drawCircle(0, 0, r0, paint);
        //内侧圆圈使用虚线显示
        paint.setPathEffect(new DashPathEffect(new float[]{16, 8}, 0));

        canvas.drawCircle(0, 0, r, paint);
        canvas.drawCircle(0, 0, r * 0.66f, paint);
        canvas.drawCircle(0, 0, r * 0.33f, paint);

        paint.setStrokeWidth(size_line);
        canvas.drawLine(-r, 0, r, 0, paint);
        canvas.drawLine(0, -r, 0, r, paint);

        // 旋转至正北方向，开始绘制角度虚线和角度值
//        canvas.rotate(-90);
        paint.setPathEffect(new DashPathEffect(new float[]{6, 3}, 0));

        Paint fontPaint = new Paint();
        fontPaint.setColor(Color.BLACK);
        fontPaint.setAntiAlias(true);//去除锯齿

        for (int i = 0; i < 12; i++) {
            if (i % 3 == 0) {
                fontPaint.setTextSize(size_text);
                String direction = directions[i / 3];
                canvas.drawText(direction, -fontPaint.measureText(direction) / 2, -o_y + size_text, fontPaint);
            }
            fontPaint.setTextSize(size_text_degree);
            canvas.drawLine(0, 0, r, 0, paint);
            String degree = "" + 30 * i;
            canvas.drawText(degree, -fontPaint.measureText(degree) / 2, -r - 0.3f * size_text_degree, fontPaint);

            canvas.rotate(30 * 1);
        }

        canvas.save();

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        finishi = true;


        if (gpsSatellites == null || gpsSatellites.size() == 0) {
            return;
        }

        for (GpsSatellite gpsSatellite : gpsSatellites) {
            int pnr = gpsSatellite.getPrn();
            float azimuth = gpsSatellite.getAzimuth();
            float elevation = gpsSatellite.getElevation();
            if (azimuth == 0 && elevation == 0) {
                continue;
            }
            float x = (float) (Math.cos(azimuth) * elevation * r / 90);
            float y = (float) (Math.sin(azimuth) * elevation * r / 90);

            canvas.drawCircle(x, y, point_o_r, paint);

            LogUtil.d("cc", "snr:" + pnr + "\nelevation:" + elevation);
        }

    }

    public void updateGpsSatellite(List<GpsSatellite> satellites) {
        gpsSatellites = satellites;

        postInvalidate();
    }


    public float getRadius() {
        return r;
    }

    private float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

}
