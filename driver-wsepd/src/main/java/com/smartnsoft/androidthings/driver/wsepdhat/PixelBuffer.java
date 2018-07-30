package com.smartnsoft.androidthings.driver.wsepdhat;

import android.util.Log;
import android.util.Size;

import java.util.Arrays;

class PixelBuffer {

    static final byte WHITE_PIXEL_GROUP_BYTE = (byte) (PaletteImage.Palette.WHITE.getByteColor() << 4 | PaletteImage.Palette.WHITE.getByteColor());

    private static final int PIXELS_PER_REGION = 2;

    private final PaletteImage.Palette[][] pixelBuffer;

    @EPaperDisplay.ScreenOrientation
    private final int orientation;

    private final Size displaySize;

    private final DeviceType specs;

    private final int numberOfPixelRegions;

    PixelBuffer(DeviceType specs, @EPaperDisplay.ScreenOrientation int orientation) {
        this.orientation = orientation;
        this.specs = specs;
        this.displaySize = specs.getScreenSize();
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
            Log.v("PixelBuffer", "Attempt to draw outside of X bounds (x:" + x + " y:" + y + ") Max X is " + getOrientatedWidth());
            return;
        }
        if (y < 0 || y >= getOrientatedHeight()) {
            Log.v("PixelBuffer", "Attempt to draw outside of Y bounds (x:" + x + " y:" + y + ") Max Y is " + getOrientatedHeight());
            return;
        }

        if (isIn(EPaperDisplay.ORIENTATION_LANDSCAPE)) {
            pixelBuffer[x][y] = color;
        } else {
            int localX = (displaySize.getWidth() - 1) - y;
            //noinspection SuspiciousNameCombination, its flipped
            int localY = x;
            pixelBuffer[localX][localY] = color;
        }
    }

    private int getOrientatedWidth() {
        return isIn(EPaperDisplay.ORIENTATION_LANDSCAPE) ? displaySize.getWidth() : displaySize.getHeight();
    }

    private int getOrientatedHeight() {
        return isIn(EPaperDisplay.ORIENTATION_LANDSCAPE) ? displaySize.getHeight() : displaySize.getWidth();
    }

    private boolean isIn(@EPaperDisplay.ScreenOrientation int orientation) {
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
    byte[] mapPaletteArrayToDisplayByteArray(PaletteImage.Palette[] palette) {
        byte[] display = new byte[this.numberOfPixelRegions];

        for (int index = 0; index < palette.length - 1; index += PIXELS_PER_REGION) {

            final PaletteImage.Palette firstPixel = palette[index];
            final PaletteImage.Palette secondPixel = palette[index + 1];

            if (firstPixel != null && secondPixel != null) {
                display[index / PIXELS_PER_REGION] = (byte) (firstPixel.getByteColor() << 4 | secondPixel.getByteColor());
            } else if (firstPixel != null) {
                display[index / PIXELS_PER_REGION] = (byte) (firstPixel.getByteColor() << 4);
            } else if (secondPixel != null) {
                display[index / PIXELS_PER_REGION] = (byte) (secondPixel.getByteColor());
            } else {
                display[index / PIXELS_PER_REGION] = PixelBuffer.WHITE_PIXEL_GROUP_BYTE;
            }
        }

        if (palette.length < display.length) {
            Arrays.fill(display, palette.length / PIXELS_PER_REGION, display.length, PixelBuffer.WHITE_PIXEL_GROUP_BYTE);
        }

        return display;
    }
}
