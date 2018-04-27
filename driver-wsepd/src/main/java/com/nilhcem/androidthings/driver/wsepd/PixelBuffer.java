package com.nilhcem.androidthings.driver.wsepd;

import android.util.Log;
import android.util.Size;

import static com.nilhcem.androidthings.driver.wsepd.ImageConverter.Orientation.PORTRAIT;

class PixelBuffer {

    private static final int PIXELS_PER_REGION = 2;

    private final PaletteImage.Palette[][] pixelBuffer;

    private final ImageConverter.Orientation orientation;

    private final Size displaySize;

    private final int numberOfPixelRegions;

    PixelBuffer(DeviceType specs, ImageConverter.Orientation orientation) {
        this.orientation = orientation;
        this.displaySize = new Size(specs.xDot, specs.yDot);
        this.numberOfPixelRegions = displaySize.getWidth() * displaySize.getHeight() / PIXELS_PER_REGION;
        pixelBuffer = new PaletteImage.Palette[displaySize.getWidth()][displaySize.getHeight()];
    }

    void setImage(int x, int y, PaletteImage image) {
        int rowCount = 0;
        int pixelCount = 0;
        for (int i = 0; i < image.totalPixels(); i++) {
            int localX = x + pixelCount;
            int localY = y + rowCount;

            PaletteImage.Palette color = image.getPixel(i);
            setPixel(localX, localY, color);

            pixelCount++;
            if (pixelCount == image.getWidth()) {
                rowCount++;
                pixelCount = 0;
            }
        }
    }

    void setPixel(int x, int y, PaletteImage.Palette color) {
        if (x < 0 || x >= getOrientatedWidth()) {
            Log.v("InkyPhat", "Attempt to draw outside of X bounds (x:" + x + " y:" + y + ") Max X is " + getOrientatedWidth());
            return;
        }
        if (y < 0 || y >= getOrientatedHeight()) {
            Log.v("InkyPhat", "Attempt to draw outside of Y bounds (x:" + x + " y:" + y + ") Max Y is " + getOrientatedHeight());
            return;
        }

        if (isIn(PORTRAIT)) {
            pixelBuffer[x][y] = color;
        } else {
            int localX = (displaySize.getWidth() - 1) - y;
            //noinspection SuspiciousNameCombination, its flipped
            int localY = x;
            pixelBuffer[localX][localY] = color;
        }
    }

    private int getOrientatedWidth() {
        return isIn(PORTRAIT) ? displaySize.getWidth() : displaySize.getHeight();
    }

    private int getOrientatedHeight() {
        return isIn(PORTRAIT) ? displaySize.getHeight() : displaySize.getWidth();
    }

    private boolean isIn(ImageConverter.Orientation orientation) {
        return this.orientation == orientation;
    }

    byte[] getDisplayPixels() {
        return mapPaletteArrayToDisplayByteArray(flatten(pixelBuffer));
    }

    private PaletteImage.Palette[] flatten(PaletteImage.Palette[][] twoDimensionalPaletteArray) {
        int width = displaySize.getWidth();
        int height = displaySize.getHeight();
        PaletteImage.Palette[] flattenedArray = new PaletteImage.Palette[width * height];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                PaletteImage.Palette color = twoDimensionalPaletteArray[x][y];
                flattenedArray[index++] = color;
            }
        }
        return flattenedArray;
    }

    /**
     * Every 2 pixels of the display is represented by a byte
     *
     * @param palette an array colors expecting to be drawn
     * @return a byte array representing the palette of a single color
     */
    private byte[] mapPaletteArrayToDisplayByteArray(PaletteImage.Palette[] palette) {
        byte[] display = new byte[this.numberOfPixelRegions];

        for (int index = 0; index < palette.length-1; index += 2) {

            final PaletteImage.Palette firstPixel = palette[index];
            final PaletteImage.Palette secondPixel = palette[index + 1];
            display[index/2] = (byte) (firstPixel.getByteColor() << 4 | secondPixel.getByteColor());
        }

        return display;
    }
}
