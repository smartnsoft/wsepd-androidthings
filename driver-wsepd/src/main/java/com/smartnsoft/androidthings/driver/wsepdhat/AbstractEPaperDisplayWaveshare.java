package com.smartnsoft.androidthings.driver.wsepdhat;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.SpiDevice;
import com.smartnsoft.androidthings.driver.wsepdhat.ImageConverter.Orientation;

import java.io.IOException;

abstract class AbstractEPaperDisplayWaveshare implements EPaperDisplay {

    protected static final boolean DC_COMMAND = false;
    protected static final boolean DC_DATA = true;

    protected final SpiDevice spiDevice;
    protected final Gpio busyGpio;
    protected final Gpio rstGpio;
    protected final Gpio dcGpio;
    protected final DeviceType specs;
    protected final Orientation screenOrientation;

    protected final byte[] buffer;

    AbstractEPaperDisplayWaveshare(SpiDevice spiDevice, Gpio busyGpio, Gpio rstGpio, Gpio dcGpio, DeviceType deviceType, Orientation orientation) throws IOException {
        this.spiDevice = spiDevice;
        this.busyGpio = busyGpio;
        this.rstGpio = rstGpio;
        this.dcGpio = dcGpio;
        this.specs = deviceType;
        this.screenOrientation = orientation;

        buffer = createBuffer();

        spiDevice.setMode(SpiDevice.MODE0);
        spiDevice.setFrequency(2_000_000); // max speed: 2MHz
        spiDevice.setBitsPerWord(8);
        spiDevice.setBitJustification(SpiDevice.BIT_JUSTIFICATION_MSB_FIRST); // MSB first
        spiDevice.setCsChange(false);

        busyGpio.setDirection(Gpio.DIRECTION_IN);
        busyGpio.setActiveType(Gpio.ACTIVE_HIGH);

        rstGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
        rstGpio.setActiveType(Gpio.ACTIVE_HIGH);

        dcGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        dcGpio.setActiveType(Gpio.ACTIVE_HIGH);

        init();
    }

    @Override
    public void close() throws IOException {
        spiDevice.close();
        busyGpio.close();
        rstGpio.close();
        dcGpio.close();
    }

    protected abstract void init() throws IOException;

    protected abstract byte[] createBuffer();

    protected abstract void busyWait() throws IOException;

    protected void sendCommand(byte command, /*Nullable*/ byte[] data) throws IOException {
        sendCommand(command, data, true);
    }

    protected void sendCommand(byte command, /*Nullable*/ byte[] data, boolean singleWrite) throws IOException {
        // Send command
        dcGpio.setValue(DC_COMMAND);
        spiDevice.write(new byte[]{command}, 1);

        // Send data
        if (data != null) {
            dcGpio.setValue(DC_DATA);

            if (singleWrite) {
                spiDevice.write(data, data.length);
            } else {
                for (byte b : data) {
                    spiDevice.write(new byte[]{b}, 1);
                }
            }
        }
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void sendCommand(byte command) throws IOException {
        sendCommand(command, null);
    }

    void resetDriver() throws IOException {
        rstGpio.setValue(false);
        sleep(100);
        rstGpio.setValue(true);
    }
}
