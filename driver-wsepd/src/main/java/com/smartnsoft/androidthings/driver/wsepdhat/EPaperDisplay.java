package com.smartnsoft.androidthings.driver.wsepdhat;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;

import java.io.IOException;

public interface EPaperDisplay extends AutoCloseable {

    /**
     * Clears the display buffer in the driver. You still have to call {@link #refresh()} after that to get an
     * empty screen.
     */
    void clear();

    /**
     * Boolean value that allows the integrator to choose whether to turn off the display aftere each refresh of the
     * screen to save some battery.
     *
     * @return true if the display turns off after each refresh
     */
    boolean shouldSleepAfterDisplay();

    /**
     * Writes these colors in the screen buffer. If the array is smaller than what is needed by the
     * screen, white pixels will be used to fill missing ones. If the array is bigger, the maximum
     * amount of pixel will be displayed and discard the rest.
     *
     * @param paletteArray The array of palette which must be displayed on the screen
     * @throws NullPointerException if the specified array is null
     */
    void setPixels(@NonNull PaletteImage.Palette[] paletteArray);

    /**
     * Writes this bitmap in the screen buffer. If the bitmap is bigger than the screen, it will be
     * downsized to fit on X or Y axis depending on the biggest one for the bitmap.
     *
     * @param bitmap The bitmap which must be displayed on the screen
     * @throws NullPointerException if the specified array is null
     */
    void setPixels(@NonNull Bitmap bitmap);

    /**
     * Writes this view in the screen buffer after measuring it.
     * If the orientation of the display requires it, the view will be rotated 90 degree clockwise.
     *
     * @param view The view that must be displayed on the screen
     * @throws NullPointerException if the specified array is null
     */
    void setPixels(@NonNull View view);

    /**
     * Writes this text in the screen buffer with the default text size.
     *
     * @param text The text that must be displayed on the screen
     * @throws NullPointerException if the specified array is null
     */
    void setPixels(@NonNull String text);

    /**
     * Writes this text in the screen buffer with the given text size and color if provided.
     *
     * @param text The {@link TextWrapper} text that must be displayed on the screen
     * @throws NullPointerException if the specified array is null
     */
    void setPixels(@NonNull TextWrapper text);

    /**
     * Sends the current display buffer to the ePaper HAT.
     * This operation will take from 5 seconds to 2 minutes.
     *
     * @throws IOException if the board pin cannot be used
     */
    void refresh() throws IOException;

    /**
     * Closes every pin used by the display.
     *
     * @throws IOException if the board pin cannot be used
     */
    @Override
    void close() throws IOException;

}
