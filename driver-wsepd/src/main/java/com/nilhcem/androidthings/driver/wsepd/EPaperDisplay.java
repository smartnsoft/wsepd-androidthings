package com.nilhcem.androidthings.driver.wsepd;

import android.graphics.Bitmap;
import android.view.View;

import java.io.IOException;

public interface EPaperDisplay extends AutoCloseable {

    void clear() throws IOException;

    void setPixels(byte[] pixels) throws IOException;

    void setPixels(Bitmap bitmap) throws IOException;

    void setPixels(View view) throws IOException;

    void setPixels(String text) throws IOException;

    void setPixels(ImageConverter.TextWrapper text) throws IOException;

    void refresh() throws IOException;

    @Override
    void close() throws IOException;

}
