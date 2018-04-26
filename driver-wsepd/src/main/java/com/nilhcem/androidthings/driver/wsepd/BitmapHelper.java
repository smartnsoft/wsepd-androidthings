package com.nilhcem.androidthings.driver.wsepd;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.nio.ByteBuffer;

public class BitmapHelper {

    private static final int GRADIENT_CUTOFF = 170; // Tune for gradient picker on grayscale images.

    /**
     * Converts a bitmap image to LCD screen data and returns the screen data as bytes.
     *
     * @param bmp The bitmap image that you want to convert to screen data.
     * @return A byte array with pixel data.
     */
    public static byte[] bmpToBytes(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        int xSize = width;
        int ySize = ((height % 8 == 0) ? height : height + (8 - height % 8)) / 8;
        byte[] pixels = new byte[xSize * ySize];

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                byte b = 0;
                for (int z = 0; z < 8; z++) {
                    int pixel = bmp.getPixel(x, (y * 8) + z);

                    if ((pixel & 0xff) > GRADIENT_CUTOFF) {
                        b |= 1 << (7 - z);
                    }
                }
                pixels[(x * ySize) + y] = b;
            }
        }
        return pixels;
    }

    public static byte[] bmpToGrayscaleBytes(Bitmap bitmap) {
        final Bitmap bm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        final Canvas c = new Canvas(bm);
        final Paint paint = new Paint();
        final ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        final ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);

        final int bufferSize = bm.getRowBytes() * bm.getHeight();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
        bm.copyPixelsToBuffer(byteBuffer);
        bm.recycle();
        return byteBuffer.array();
    }

}
