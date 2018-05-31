package com.smartnsoft.androidthings.driver.wsepdhat;

import android.graphics.Bitmap;
import android.view.View;

import java.io.IOException;

public interface EPaperDisplay extends AutoCloseable {

    void clear();

    void setPixels(PaletteImage.Palette[] pixels);

    void setPixels(Bitmap bitmap);

    void setPixels(View view);

    void setPixels(String text);

    void setPixels(ImageConverter.TextWrapper text);

    void refresh() throws IOException;

    @Override
    void close() throws IOException;

}
