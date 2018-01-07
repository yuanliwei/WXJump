package com.ylw.wxjump;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by ylw on 2018/1/3.
 */

class FindKeyObject {
    private static final String TAG = "FindKeyObject";
    private final FlagView flagView;
    private final Msg msg;
    private long sendTime;

    public FindKeyObject(FlagView flagView) {
        this.flagView = flagView;
        this.msg = new Msg();
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        long sleepTime = sendTime + 2500 - System.currentTimeMillis();
                        if (sleepTime < 10) sleepTime = 3000;
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int[] bitmapPixel = GBData.getBitmapPixel();
                    Point size = GBData.getSize();
                    if (bitmapPixel == null) {
                        try {
                            Thread.sleep(80);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    long l = System.currentTimeMillis();
                    findPiece(bitmapPixel, size);
                    Log.i(TAG, "run: find time : " + (System.currentTimeMillis() - l));
                }
            }
        }).start();
    }

    // 找棋子
    private void findPiece(int[] bitmapPixel, Point size) {
        for (int i = 0; i < size.y; i++) {
            for (int j = 0; j < size.x; j++) {
                int c = bitmapPixel[i * size.x + j];
                if ((Color.red(c) > 50 && Color.red(c) < 60)
                        && (Color.green(c) > 40 && Color.green(c) < 50)
                        && (Color.blue(c) > 70 && Color.blue(c) < 80)) {
                    findTarget(j + 6, i + 18, bitmapPixel, size);
                    return;
                }
            }
        }
        // 清空画布
//        flagView.foundPosition(new Rect(), 0);
    }

    // 找落地点
    private void findTarget(int x, int y, int[] bitmapPixel, Point size) {
        int w = size.x;
        int startX = 0;
        int endX = w / 2;
        int startY = (int) (size.y / 2.5);
        int endY = size.y;
        if (x < w / 2) {
            startX = w / 2;
            endX = w;
        }
        // 背景色
        int std = bitmapPixel[startY * w];
        V3 stdV3 = new V3(Color.red(std), Color.green(std), Color.blue(std));
        int[] find = new int[3];
        for (int i = startY; i < endY; i++) {
            for (int j = startX; j < endX; j++) {
                int index = i * w + j;
                int cur = bitmapPixel[index];
                V3 curV3 = new V3(Color.red(cur), Color.green(cur), Color.blue(cur));
                float angle = (float) (curV3.angleTo(stdV3) * 256 * 2 / Math.PI);
                if (angle > 1) {
                    if (find[0] == 0) {
                        Log.i(TAG, "findTarget: angle:" + angle);
                        find[0] = j;
                        find[2] = i;
                    }
                    find[1] = j;
                }
                if (find[0] == 0) {
                    stdV3.set(Color.red(cur), Color.green(cur), Color.blue(cur));
                }
            }
            if (find[0] > 0) {
                break;
            }
        }

        if (find[0] > 0) {
            // 0.57
            float defDir = (float) 0.57;
            float k = (x > w / 2) ? defDir : -defDir;
            int x2 = (find[0] + find[1]) / 2;
            int y2 = (int) (k * (x2 - x) + y);
//            Log.i(TAG, "findTarget: yoffset:" + y2 + " - ry2:" + find[2]);

            if (y2 - 10 < find[2]) y2 = find[2] + 10;
            if (y2 - 20 > find[2]) y2 = (y2 + find[2]) / 2;

            Rect rect = new Rect(x, y, x2, y2);
            int time = (int) (Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2)) / 0.3f * 1.34f);
            Log.i(TAG, "findTarget: time : " + time + " x0:" + x + " y0:" + y + " x1:" + x2 + " y1:" + y2);
            flagView.foundPosition(rect, time);
            msg.send(time, x * y * time);
            sendTime = System.currentTimeMillis() + time;
        }
    }

}

class V3 {
    private float z;
    private float y;
    private float x;

    V3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    double angleTo(V3 v) {
        double theta = this.dot(v) / (Math.sqrt(this.lengthSq() * v.lengthSq()));
        return Math.acos(this.clamp(theta, -1, 1));

    }

    private double dot(V3 v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    private double lengthSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
