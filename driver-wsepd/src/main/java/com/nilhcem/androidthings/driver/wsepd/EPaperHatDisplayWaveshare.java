package com.nilhcem.androidthings.driver.wsepd;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

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

    EPaperHatDisplayWaveshare(SpiDevice spiDevice, Gpio busyGpio, Gpio rstGpio, Gpio dcGpio, DeviceType deviceType) throws IOException {
        super(spiDevice, busyGpio, rstGpio, dcGpio, deviceType);
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
        sendCommand(CMD_TCON_RESOLUTION, new byte[]{
                0x02,     //source 640
                (byte) 0x80,
                0x01,     //gate 384
                (byte) 0x80
        });
        sendCommand(CMD_VCM_DC_SETTING, new byte[]{
                (0x1E)      //decide by LUT file
        });
        sendCommand((byte) 0xe5, new byte[]{
                //FLASH MODE
                (0x03)
        });
    }

    @Override
    public void clear() throws IOException {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) 0x44;
        }
        setPixels(buffer);
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

    @Override
    public void setPixels(byte[] pixels) throws IOException {
        busyWait();
        if (specs != DeviceType.Preset.EPD7X5.deviceType) {
            setPixelsOnColoredDisplay(pixels);
        } else {
            setPixelsOnMonochromeDisplay(pixels);
        }
    }

    private void setPixelsOnColoredDisplay(byte[] pixels) {
        for (int i = 0; i < buffer.length; i++) {
            byte temp1 = pixels[i];
            byte temp2;
            int j = 0;
            while (j < 4) {
                if ((temp1 & 0xC0) == 0xC0) {
                    temp2 = 0x03;
                } else if ((temp1 & 0xC0) == 0x00) {
                    temp2 = 0x00;
                } else {
                    temp2 = 0x04;
                }

                temp2 = (byte) ((temp2 << 4) & 0xFF);
                temp1 = (byte) ((temp1 << 2) & 0xFF);
                j += 1;
                if ((temp1 & 0xC0) == 0xC0) {
                    temp2 |= 0x03;
                } else if ((temp1 & 0xC0) == 0x00) {
                    temp2 |= 0x00;
                } else {
                    temp2 |= 0x04;
                }
                temp1 = (byte) ((temp1 << 2) & 0xFF);
                buffer[i] = temp2;
                j += 1;
            }
        }
    }

    private void setPixelsOnMonochromeDisplay(byte[] pixels) {
        for (int i = 0; i < buffer.length; i++) {
            byte temp1 = pixels[i];
            byte temp2;
            int j = 0;
            while (j < 8) {
                if ((temp1 & 0x80) == 0) {
                    temp2 = 0x03;
                } else {
                    temp2 = 0x00;
                }
                temp2 = (byte) ((temp2 << 4) & 0xFF);
                temp1 = (byte) ((temp1 << 1) & 0xFF);
                j += 1;
                if ((temp1 & 0x80) == 0) {
                    temp2 |= 0x03;
                } else {
                    temp2 |= 0x00;
                }
                temp1 = (byte) ((temp1 << 1) & 0xFF);
                buffer[i] = temp2;
                j += 1;
            }
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

    @Override
    public void refresh() throws IOException {
        turnDisplayOn();
        busyWait();
        sendCommand(DATA_START_TRANSMISSION_1, buffer, false);
        sendCommand(DISPLAY_REFRESH);
        sleep(100);
        busyWait();
        turnDisplayOff();
    }

//    def get_frame_buffer(self, image):
//    buf = [0x00] * (self.width * self.height / 4)
//            # Set buffer to value of Python Imaging Library image.
//        # Image must be in mode L.
//    image_grayscale = image.convert('L')
//    imwidth, imheight = image_grayscale.size
//        if imwidth != self.width or imheight != self.height:
//    raise ValueError('Image must be same dimensions as display \
//                             ({0}x{1}).' .format(self.width, self.height))
//
//    pixels = image_grayscale.load()
//            for y in range(self.height):
//            for x in range(self.width):
//            # Set the bits for the column of pixels at the current position.
//            if pixels[x, y] < 64:           # black
//    buf[(x + y * self.width) / 4] &= ~(0xC0 >> (x % 4 * 2))
//    elif pixels[x, y] < 192:     # convert gray to red
//    buf[(x + y * self.width) / 4] &= ~(0xC0 >> (x % 4 * 2))
//    buf[(x + y * self.width) / 4] |= 0x40 >> (x % 4 * 2)
//            else:                           # white
//    buf[(x + y * self.width) / 4] |= 0xC0 >> (x % 4 * 2)
//            return buf


}
