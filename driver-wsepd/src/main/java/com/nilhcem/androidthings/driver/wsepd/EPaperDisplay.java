package com.nilhcem.androidthings.driver.wsepd;

import java.io.IOException;

public interface EPaperDisplay extends AutoCloseable {

    void clear() throws IOException;

    void setPixels(byte[] pixels) throws IOException;

    void refresh() throws IOException;

    @Override
    void close() throws IOException;

}
