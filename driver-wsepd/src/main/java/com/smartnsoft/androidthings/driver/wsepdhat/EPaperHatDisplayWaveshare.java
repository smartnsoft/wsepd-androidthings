package com.smartnsoft.androidthings.driver.wsepdhat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.MeasureSpec;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.SpiDevice;
import com.smartnsoft.androidthings.driver.wsepdhat.ImageConverter.Orientation;
import com.smartnsoft.androidthings.driver.wsepdhat.PaletteImage.Palette;

import java.io.IOException;
import java.util.Arrays;

public class EPaperHatDisplayWaveshare extends AbstractEPaperDisplayWaveshare {

    private static final byte CMD_PANEL_SETTING = 0x00;
    private static final byte CMD_POWER_SETTING = 0x01;
    private static final byte CMD_POWER_OFF = 0x02;
    private static final byte POWER_OFF_SEQUENCE_SETTING = 0x03;
    private static final byte CMD_POWER_ON = 0x04;
    private static final byte POWER_ON_MEASURE = 0x05;
    private static final byte CMD_BOOSTER_SOFT_START = 0x06;
    private static final byte DEEP_SLEEP = 0x07;
    private static final byte DATA_START_TRANSMISSION_1 = 0x10;
    private static final byte DATA_STOP = 0x11;
    private static final byte DISPLAY_REFRESH = 0x12;
    private static final byte IMAGE_PROCESS = 0x13;
    private static final byte LUT_FOR_VCOM = 0x20;
    private static final byte LUT_BLUE = 0x21;
    private static final byte LUT_WHITE = 0x22;
    private static final byte LUT_GRAY_1 = 0x23;
    private static final byte LUT_GRAY_2 = 0x24;
    private static final byte LUT_RED_0 = 0x25;
    private static final byte LUT_RED_1 = 0x26;
    private static final byte LUT_RED_2 = 0x27;
    private static final byte LUT_RED_3 = 0x28;
    private static final byte LUT_XON = 0x29;
    private static final byte CMD_PLL_CONTROL = 0x30;
    private static final byte CMD_TEMPERATURE_SENSOR_COMMAND = 0x40;
    private static final byte CMD_TEMPERATURE_CALIBRATION = 0x41;
    private static final byte TEMPERATURE_SENSOR_WRITE = 0x42;
    private static final byte TEMPERATURE_SENSOR_READ = 0x43;
    private static final byte CMD_VCOM_AND_DATA_INTERVAL_SETTING = 0x50;
    private static final byte LOW_POWER_DETECTION = 0x51;
    private static final byte CMD_TCON_SETTING = 0x60;
    private static final byte CMD_TCON_RESOLUTION = 0x61;
    private static final byte SPI_FLASH_CONTROL = 0x65;
    private static final byte REVISION = 0x70;
    private static final byte GET_STATUS = 0x71;
    private static final byte AUTO_MEASUREMENT_VCOM = (byte) 0x80;
    private static final byte READ_VCOM_VALUE = (byte) 0x81;
    private static final byte CMD_VCM_DC_SETTING = (byte) 0x82;

    private PixelBuffer pixelBuffer;
    private ImageConverter imageConverter;
    private boolean shouldSleepAfterDisplay = true;

    EPaperHatDisplayWaveshare(SpiDevice spiDevice, Gpio busyGpio, Gpio rstGpio, Gpio dcGpio, DeviceType deviceType, Orientation orientation) throws IOException {
        this(spiDevice, busyGpio, rstGpio, dcGpio, deviceType, orientation, true);
    }

    EPaperHatDisplayWaveshare(SpiDevice spiDevice, Gpio busyGpio, Gpio rstGpio, Gpio dcGpio, DeviceType deviceType, Orientation orientation, boolean shoudldSleepAfterDisplay) throws IOException {
        super(spiDevice, busyGpio, rstGpio, dcGpio, deviceType, orientation);
        pixelBuffer = new PixelBuffer(deviceType, orientation);
        imageConverter = new ImageConverter(deviceType, orientation);
        shoudldSleepAfterDisplay = shouldSleepAfterDisplay;
    }

    @Override
    public void clear() {
        Arrays.fill(buffer, PixelBuffer.WHITE_PIXEL_GROUP_BYTE);
    }

    @Override
    public boolean shouldSleepAfterDisplay()
    {
        return shouldSleepAfterDisplay;
    }

    @Override
    public void setPixels(@NonNull Palette[] pixels) {
        final byte[] output = pixelBuffer.mapPaletteArrayToDisplayByteArray(pixels);
        System.arraycopy(output, 0, buffer, 0, output.length);
    }

    @Override
    public void setPixels(@NonNull Bitmap bitmap) {
        setPixels(imageConverter.convertImage(bitmap, ImageScaler.Scale.FIT_X_OR_Y), 0, 0);
    }

    @Override
    public void setPixels(@NonNull View view) {
        setPixels(loadBitmapFromView(view));
    }

    @Override
    public void setPixels(@NonNull TextWrapper text) {
        setPixels(imageConverter.convertText(text.text, text.textSize, text.textColor), 0, 0);
    }

    @Override
    public void setPixels(@NonNull String text) {
        setPixels(imageConverter.convertText(new TextWrapper(text)), 0, 0);
    }

    @Override
    public void refresh() throws IOException {
        if (shouldSleepAfterDisplay()) {
            turnDisplayOn();
            busyWait();
        }
        sendCommand(DATA_START_TRANSMISSION_1, buffer, false);
        sendCommand(DATA_STOP);
        sendCommand(DISPLAY_REFRESH);
        sleep(100);
        busyWait();
        if (shouldSleepAfterDisplay()) {
            turnDisplayOff();
        }
    }

    @Override
    protected byte[] createBuffer() {
        return new byte[specs.xDot / 2 * specs.yDot];
    }

    @Override
    protected void init() throws IOException {
        // Initialize display
        resetDriver();

        sendCommand(CMD_POWER_SETTING, new byte[]{0x37, 0x00});
        sendCommand(CMD_PANEL_SETTING, new byte[]{(byte) 0xCF, 0x08});
        sendCommand(CMD_BOOSTER_SOFT_START, new byte[]{(byte) 0xc7, (byte) 0xcc, (0x28)});
        sendCommand(CMD_POWER_ON);
        busyWait();

        sendCommand(CMD_PLL_CONTROL, new byte[]{(0x3c)});
        sendCommand(CMD_TEMPERATURE_CALIBRATION, new byte[]{(0x00)});
        sendCommand(CMD_VCOM_AND_DATA_INTERVAL_SETTING, new byte[]{(0x77)});
        sendCommand(CMD_TCON_SETTING, new byte[]{(0x22)});
        initResolution();
        sendCommand(CMD_VCM_DC_SETTING, new byte[]{
                (0x1E)      //decide by LUT file
        });
        sendCommand((byte) 0xe5, new byte[]{
                //FLASH MODE
                (0x03)
        });
    }

    protected void initResolution() throws IOException {
        if (specs.xDot == DeviceType.Preset.EPD7X5A.deviceType.xDot
                && specs.yDot == DeviceType.Preset.EPD7X5A.deviceType.yDot) {
            sendCommand(CMD_TCON_RESOLUTION, new byte[]{
                    0x02,     //source 640
                    (byte) 0x80,
                    0x01,     //gate 384
                    (byte) 0x80
            });
        } else if (specs.xDot == DeviceType.Preset.EPD5X8A.deviceType.xDot
                && specs.yDot == DeviceType.Preset.EPD5X8A.deviceType.yDot) {
            sendCommand(CMD_TCON_RESOLUTION, new byte[]{
                    0x02,     //source 600
                    (byte) 0x58,
                    0x01,     //gate 448
                    (byte) 0xC0
            });
        }
    }

    @Override
    protected void busyWait() throws IOException {
        while (!busyGpio.getValue()) {
            sleep(100);
        }
    }

    @Override
    void resetDriver() throws IOException {
        rstGpio.setValue(false);
        sleep(200);
        rstGpio.setValue(true);
        sleep(200);
    }

    private void setPixels(final PaletteImage paletteImage, int x, int y) {
        pixelBuffer.setImage(x, y, paletteImage);
        byte[] pixels = pixelBuffer.getDisplayPixels();
        System.arraycopy(pixels, 0, buffer, 0, Math.min(pixels.length, buffer.length));
    }

    private void turnDisplayOn() throws IOException {
        init();
    }

    private void turnDisplayOff() throws IOException {
        busyWait();
        sendCommand(CMD_POWER_OFF);
        busyWait();
        sendCommand(DEEP_SLEEP, new byte[]{(byte) 0xa5});
    }

    private Bitmap loadBitmapFromView(View view) {
        if (screenOrientation == Orientation.PORTRAIT) {
            view.setRotation(90f);
        }
        final int specWidth = MeasureSpec.makeMeasureSpec(specs.getOrientatedWidth(screenOrientation), MeasureSpec.EXACTLY);
        final int specHeight = MeasureSpec.makeMeasureSpec(specs.getOrientatedHeight(screenOrientation), MeasureSpec.EXACTLY);
        view.measure(specWidth, specHeight);
        final Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }

}
