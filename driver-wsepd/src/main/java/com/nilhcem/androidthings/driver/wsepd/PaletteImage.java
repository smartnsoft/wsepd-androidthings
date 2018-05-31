package com.nilhcem.androidthings.driver.wsepd;

public final class PaletteImage {

    public enum Palette {
        BLACK,
        COLORED, // Red or Yellow
        WHITE;

        public byte getByteColor() {
            switch (this) {
                case COLORED:
                    return 0b0100;
                case WHITE:
                    return 0b0011;
                case BLACK:
                default:
                    return 0b0000;
            }
        }
    }

    private final Palette[] colors;
    private final int width;

    PaletteImage(Palette[] colors, int width) {
        this.colors = colors;
        this.width = width;
    }

    Palette getPixel(int position) {
        return colors[position];
    }

    int totalPixels() {
        return colors.length;
    }

    int getWidth() {
        return width;
    }

}