package com.ylw.wxjump;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by ylw on 2017/12/23.
 */

public class GBData {
    private static final String TAG = "GBData";
    static ImageReader reader;
    static String ipaddr;
    private static Bitmap bitmap;
    private static int[] pixel;
    private static Point size;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private synchronized static int getPixel(int x, int y) {
        if (reader == null) {
            Log.w(TAG, "getColor: reader is null");
            return -1;
        }

        // 截图
        Image image = reader.acquireNextImage();

        if (image == null) {
            return -1;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        }
        bitmap.copyPixelsFromBuffer(buffer);

        // 使用小图加快搜索速度
        int w = (int) (bitmap.getWidth() * 0.3) - 2;
        int h = (int) (bitmap.getHeight() * 0.3);
        Bitmap bitmapSm = zoomBitmap(bitmap);
        if (pixel == null) {
            pixel = new int[w * h];
            size = new Point(w, h);
        }
        bitmapSm.getPixels(pixel, 0, w, 0, 0, w, h);
        image.close();
        return bitmap.getPixel(x, y);
    }

    synchronized static int[] getBitmapPixel() {
        int result = getPixel(0, 0);
        if (result == -1) return null;
        return GBData.pixel;
    }

    static Point getSize() {
        return size;
    }

    /**
     * 放大缩小图片
     */
    private static Bitmap zoomBitmap(Bitmap bitmap) {
        int w = (int) (bitmap.getWidth() * 0.3) - 2;
        int h = (int) (bitmap.getHeight() * 0.3);
        Bitmap bitmap1 = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap1);
        Matrix matrix = new Matrix();
        float scale = 0.3f;
        matrix.postScale(scale, scale);
        canvas.drawBitmap(bitmap, matrix, new Paint());
        return bitmap1;
    }
}
