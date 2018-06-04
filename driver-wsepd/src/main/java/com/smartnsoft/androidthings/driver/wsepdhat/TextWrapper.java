package com.smartnsoft.androidthings.driver.wsepdhat;

import android.graphics.Color;

public class TextWrapper {
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
