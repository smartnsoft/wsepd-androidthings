package com.nilhcem.androidthings.driver.wsepd;

import android.graphics.*;
import android.util.Size;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static com.nilhcem.androidthings.driver.wsepd.ImageConverter.Orientation.PORTRAIT;

public class ImageConverter {

    public static class TextWrapper {
        final int textColor;
        final int textSize;
        final String text;

        public TextWrapper(int textColor, int textSize, String text) {
            this.textColor = textColor;
            this.textSize = textSize;
            this.text = text;
        }

        public TextWrapper(int textColor, String text) {
            this(textColor, 20, text);
        }

        public TextWrapper(String text) {
            this(Color.BLACK, 20, text);
        }
    }

    public enum Orientation {
        LANDSCAPE, PORTRAIT
    }

    private final Size displaySize;
    private final DeviceType specs;
    private final Orientation orientation;
    private final ImageScaler imageScaler;
    private final ColorConverter colorConverter;

    ImageConverter(DeviceType specs, Orientation orientation) {
        this.orientation = orientation;
        this.specs = specs;
        this.imageScaler = new ImageScaler();
        this.colorConverter = new ColorConverter();
        this.displaySize = specs.getScreenSize();
    }

    PaletteImage convertImage(Bitmap input, ImageScaler.Scale scale) {
        return translateImage(filterImage(input, scale));
    }

    PaletteImage translateImage(Bitmap input) {
        int width = input.getWidth();
        int height = input.getHeight();
        int[] pixels = new int[width * height];
        input.getPixels(pixels, 0, width, 0, 0, width, height);
        PaletteImage.Palette[] colors = new PaletteImage.Palette[width * height];
        for (int i = 0, pixelsLength = pixels.length; i < pixelsLength; i++) {
            colors[i] = colorConverter.convertARBG888Color(pixels[i], specs.isBlackAndWhiteOnly);
        }
        return new PaletteImage(colors, width);
    }

    Bitmap filterImage(Bitmap sourceBitmap, ImageScaler.Scale scale) {
        return scaleToScreenBounds(sourceBitmap, scale);
    }

    private Bitmap scaleToScreenBounds(Bitmap sourceBitmap, ImageScaler.Scale scale) {
        int bitmapWidth = sourceBitmap.getWidth();
        int bitmapHeight = sourceBitmap.getHeight();
        if (bitmapWidth < getOrientatedWidth() && bitmapHeight < getOrientatedHeight()) {
            return sourceBitmap;
        }

        switch (scale) {
            case FIT_XY:
                return imageScaler.fitXY(sourceBitmap, getOrientatedWidth(), getOrientatedHeight());
            case FIT_X_OR_Y:
                return imageScaler.fitXorY(sourceBitmap, getOrientatedWidth(), getOrientatedHeight());
            default:
                throw new IllegalStateException("Unsupported scale type of " + scale);
        }
    }

    private int getOrientatedWidth() {
        return isIn(PORTRAIT) ? displaySize.getWidth() : displaySize.getHeight();
    }

    private int getOrientatedHeight() {
        return isIn(PORTRAIT) ? displaySize.getHeight() : displaySize.getWidth();
    }

    private boolean isIn(Orientation orientation) {
        return this.orientation == orientation;
    }

    public PaletteImage convertText(String text, int maxTextSize, int color) {
        return convertImage(textAsBitmap(text, maxTextSize, color), ImageScaler.Scale.FIT_X_OR_Y);
    }

    public PaletteImage convertText(TextWrapper text) {
        return convertImage(textAsBitmap(text.text, text.textSize, text.textColor), ImageScaler.Scale.FIT_X_OR_Y);
    }

    public PaletteImage convertText(String text, int color) {
        return convertImage(textAsBitmap(text, 20, color), ImageScaler.Scale.FIT_X_OR_Y);
    }

    private Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 0.5f);
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(colorConverter.convertToInverse(textColor));
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
}
