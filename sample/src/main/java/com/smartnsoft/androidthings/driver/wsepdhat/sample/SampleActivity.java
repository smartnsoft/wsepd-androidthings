package com.smartnsoft.androidthings.driver.wsepdhat.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import com.smartnsoft.androidthings.driver.wsepdhat.sample.R;
import com.smartnsoft.androidthings.driver.wsepdhat.DeviceType;
import com.smartnsoft.androidthings.driver.wsepdhat.EPaperDisplay;
import com.smartnsoft.androidthings.driver.wsepdhat.EPaperDisplayFactory;
import com.smartnsoft.androidthings.driver.wsepdhat.ImageConverter.Orientation;
import com.smartnsoft.androidthings.driver.wsepdhat.ImageConverter.TextWrapper;
import com.smartnsoft.androidthings.driver.wsepdhat.PaletteImage;

import java.io.IOException;

public class SampleActivity
        extends Activity {

    private static final String TAG = SampleActivity.class.getSimpleName();

    public static class ScreenPinout {

        // when plugging the hat on the raspberry
        public static final ScreenPinout raspberry = new ScreenPinout("SPI0.0", "BCM24", "BCM17", "BCM25");

        // when using same pins as the hat but changing reset GPIO from pin 11 to pin 15
        public static final ScreenPinout imx7d = new ScreenPinout("SPI3.1", "GPIO6_IO12", "GPIO1_IO10", "GPIO5_IO00");


        private final String spiName;

        private final String busyGPIO;

        private final String resetGPIO;

        private final String dcGPIO;

        ScreenPinout(String spiName, String busyGPIO, String resetGPIO, String dcGPIO) {
            this.spiName = spiName;
            this.busyGPIO = busyGPIO;
            this.resetGPIO = resetGPIO;
            this.dcGPIO = dcGPIO;
        }
    }

    private EPaperDisplay display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            final ScreenPinout screenPinout;
            if (Build.BOARD.equals("rpi3")) {
                screenPinout = ScreenPinout.raspberry;
            } else if (Build.BOARD.equals("imx7d")) {
                screenPinout = ScreenPinout.imx7d;
            } else {
                screenPinout = null;
            }

            display = EPaperDisplayFactory.create(screenPinout.spiName, screenPinout.busyGPIO, screenPinout.resetGPIO, screenPinout.dcGPIO, DeviceType.Preset.EPD5X8A, Orientation.LANDSCAPE);

//            displayBitmapFromResource(R.drawable.rocket);

//            displayPalette();

            displayDummyText();

//            displayLayout();

//            clearDisplay();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing display", e);
        }
    }

    private void displayPalette() throws IOException {
        Log.d(TAG, "Sending dummy palette array to the screen !");
        final Size screenSize = DeviceType.Preset.EPD7X5B.deviceType.getScreenSize();
        final PaletteImage.Palette[] colors = new PaletteImage.Palette[screenSize.getWidth() * screenSize.getHeight()];
        for (int i = 0; i < colors.length; i++) {
            if (i % 2 == 0 || i % 5 == 0) {
                colors[i] = PaletteImage.Palette.COLORED;
            } else if (i % 3 == 0 || i % 7 == 0) {
                colors[i] = PaletteImage.Palette.BLACK;
            } else {
                colors[i] = PaletteImage.Palette.WHITE;
            }
        }
        display.setPixels(colors);
        display.refresh();
        Log.d(TAG, "Dummy palette array has been displayed on the screen !");
    }

    private void displayDummyText() throws IOException {
        Log.d(TAG, "Sending dummy text to the screen !");
        display.setPixels(new TextWrapper(Color.YELLOW, getResources().getDimensionPixelSize(R.dimen.huge_text_size), "Hello, World !"));
        display.refresh();
        Log.d(TAG, "Text has been displayed on the screen !");
    }

    private void displayLayout() throws IOException {
        Log.d(TAG, "Sending layout to the screen !");
        final View root = getLayoutInflater().inflate(R.layout.dummy_user_review, null, false);
        final ViewGroup informationLayout = root.findViewById(R.id.informations);
        informationLayout.addView(new InformationLayout(getApplicationContext(), "Quality", 4.55f));
        informationLayout.addView(new InformationLayout(getApplicationContext(), "Price", 3.15f));
        informationLayout.addView(new InformationLayout(getApplicationContext(), "Usefulness", 1.00f));
        display.setPixels(root);
        display.refresh();
        Log.d(TAG, "Layout has been diplayed on the screen !");
    }

    private void clearDisplay() throws IOException {
        Log.d(TAG, "Sending clear command to the screen !");
        display.clear();
        display.refresh();
        Log.d(TAG, "Cleared !");
    }

    private void displayBitmapFromResource(@DrawableRes int drawableID) throws IOException {
        Log.d(TAG, "Sending bitmap data to the screen !");
        final Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableID);
        displayBitmap(bmp);
    }

    private void displayBitmap(@NonNull Bitmap bitmap) throws IOException {
        display.setPixels(bitmap);
        display.refresh();
        Log.d(TAG, "Bitmap displayed to the screen !");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            display.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing display", e);
        }
    }

}
