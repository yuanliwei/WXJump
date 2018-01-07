package com.ylw.wxjump;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * 画标记的View
 * <p>
 * Created by ylw on 2018/1/3.
 */

class FlagView extends View {
    private Paint paint;
    private Rect rect;
    private int time;

    public FlagView(Context context) {
        super(context);
        init();
    }


    public FlagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.time == 0) return;
        Point size = GBData.getSize();

        float scale = getWidth() / size.x;

        int dx = 50;
        int dy = 30;

        int x1 = (int) (rect.left * scale) + dx;
        int y1 = (int) (rect.top * scale) + dy;
        int x2 = (int) (rect.right * scale) + dx;
        int y2 = (int) (rect.bottom * scale) + dy;

        paint.setStrokeWidth(3);
        paint.setColor(0xffff0000);
        canvas.drawLine(x1, y1, x2, y2, paint);
        paint.setStrokeWidth(12);
        canvas.drawPoint(x1, y1, paint);
        canvas.drawPoint(x2, y2, paint);

        paint.setTextSize(90);
        canvas.drawText("press time:" + time + "ms", 130, getHeight() - 230, paint);
        paint.setTextSize(40);
        canvas.drawText(GBData.ipaddr + "/" + time, 130, getHeight() - 130, paint);
        paint.setColor(0xff0000ff);

    }

    public void foundPosition(Rect rect, int time) {
        this.time = time;
        this.rect.set(rect);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                postInvalidate();
            }
        });
    }
}
