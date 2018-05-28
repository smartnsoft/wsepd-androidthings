package com.nilhcem.androidthings.driver.wsepd;

import android.graphics.Color;

class ColorConverter {

    PaletteImage.Palette convertARBG888Color(int color, boolean blackAndWhiteOnly) {
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        if (red > 127 && blue > 127 && green > 127) {
            return PaletteImage.Palette.WHITE;
        }

        if (red > 127 && !blackAndWhiteOnly) {
            return PaletteImage.Palette.COLORED;
        }

        return PaletteImage.Palette.BLACK;
    }

    int convertToInverse(int color) {
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);

        if (red > 127 && blue > 127 && green > 127) {
            return Color.argb(255, 0, 0, 0);
        } else {
            return Color.argb(255, 255, 255, 255);
        }
    }
}
