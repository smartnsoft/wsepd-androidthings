package com.nilhcem.androidthings.driver.wsepd;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

public class EPaperDisplayFactory {

    public static EPaperDisplay create(String spiName, String busyGpioName, String rstGpioName, String dcGpioName,
                                       DeviceType deviceType) throws IOException {
        PeripheralManager manager = PeripheralManager.getInstance();
        SpiDevice spiDevice = manager.openSpiDevice(spiName);
        Gpio busyGpio = manager.openGpio(busyGpioName);
        Gpio rstGpio = manager.openGpio(rstGpioName);
        Gpio dcGpio = manager.openGpio(dcGpioName);

        if (deviceType.isUsingHat) {
            return new EPaperHatDisplayWaveshare(spiDevice, busyGpio, rstGpio, dcGpio, deviceType);
        }
        return new EPaperDisplayWaveshare(spiDevice, busyGpio, rstGpio, dcGpio, deviceType);
    }
}
