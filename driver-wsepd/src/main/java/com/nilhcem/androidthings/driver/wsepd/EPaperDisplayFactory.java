package com.nilhcem.androidthings.driver.wsepd;

import java.io.IOException;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.SpiDevice;
import com.nilhcem.androidthings.driver.wsepd.DeviceType.Preset;
import com.nilhcem.androidthings.driver.wsepd.ImageConverter.Orientation;

public class EPaperDisplayFactory
{

  public static EPaperDisplay create(String spiName, String busyGpioName, String rstGpioName, String dcGpioName,
      Preset deviceType, Orientation orientation)
      throws IOException
  {
    final PeripheralManager manager = PeripheralManager.getInstance();
    final SpiDevice spiDevice = manager.openSpiDevice(spiName);
    final Gpio busyGpio = manager.openGpio(busyGpioName);
    final Gpio rstGpio = manager.openGpio(rstGpioName);
    final Gpio dcGpio = manager.openGpio(dcGpioName);
    return new EPaperHatDisplayWaveshare(spiDevice, busyGpio, rstGpio, dcGpio, deviceType.deviceType, orientation);
  }
}
