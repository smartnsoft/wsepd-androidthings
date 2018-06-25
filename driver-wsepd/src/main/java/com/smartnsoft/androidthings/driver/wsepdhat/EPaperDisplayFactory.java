package com.smartnsoft.androidthings.driver.wsepdhat;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.SpiDevice;
import com.smartnsoft.androidthings.driver.wsepdhat.DeviceType.Preset;
import com.smartnsoft.androidthings.driver.wsepdhat.ImageConverter.Orientation;

import java.io.IOException;

public class EPaperDisplayFactory {

    public static EPaperDisplay create(String spiName, String busyGpioName, String rstGpioName, String dcGpioName,
                                       Preset deviceType, Orientation orientation)
            throws IOException {
        return EPaperDisplayFactory.create(spiName, busyGpioName, rstGpioName, dcGpioName, deviceType, orientation, true);
    }

    public static EPaperDisplay create(String spiName, String busyGpioName, String rstGpioName, String dcGpioName,
        Preset deviceType, Orientation orientation, boolean shouldSleepAfterDisplay)
        throws IOException {
        final PeripheralManager manager = PeripheralManager.getInstance();
        final SpiDevice spiDevice = manager.openSpiDevice(spiName);
        final Gpio busyGpio = manager.openGpio(busyGpioName);
        final Gpio rstGpio = manager.openGpio(rstGpioName);
        final Gpio dcGpio = manager.openGpio(dcGpioName);
        return new EPaperHatDisplayWaveshare(spiDevice, busyGpio, rstGpio, dcGpio, deviceType.deviceType, orientation, shouldSleepAfterDisplay);
    }
}
