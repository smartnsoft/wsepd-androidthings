package com.nilhcem.androidthings.driver.wsepd.sample;

import java.io.IOException;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.nilhcem.androidthings.driver.wsepd.DeviceType;
import com.nilhcem.androidthings.driver.wsepd.EPaperDisplay;
import com.nilhcem.androidthings.driver.wsepd.EPaperDisplayFactory;

public class SampleActivity
    extends Activity
{

  private static final String TAG = SampleActivity.class.getSimpleName();

  public static class ScreenPinout
  {

    public static final ScreenPinout raspberry = new ScreenPinout("SPI0.0", "BCM24", "BCM17", "BCM25");

    private final String spiName;

    private final String busyGPIO;

    private final String resetGPIO;

    private final String dcGPIO;

    ScreenPinout(String spiName, String busyGPIO, String resetGPIO, String dcGPIO)
    {
      this.spiName = spiName;
      this.busyGPIO = busyGPIO;
      this.resetGPIO = resetGPIO;
      this.dcGPIO = dcGPIO;
    }
  }

  private EPaperDisplay display;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    try
    {
      final ScreenPinout screenPinout;
      if (Build.BOARD.equals("rpi3"))
      {
        screenPinout = ScreenPinout.raspberry;
      }
      else
      {
        screenPinout = null;
      }

      display = EPaperDisplayFactory.create(screenPinout.spiName, screenPinout.busyGPIO, screenPinout.resetGPIO, screenPinout.dcGPIO, DeviceType.Preset.EPD7X5B.deviceType);

      // Clear screen
      // display.clear();
      // Thread.sleep(1000);

      Log.d(TAG, "Refreshing");
      // Draw a black-on-white bitmap

      final View root = getLayoutInflater().inflate(R.layout.dummy_user_review, null, false);
      final ViewGroup informationLayout = root.findViewById(R.id.informations);
      informationLayout.addView(new InformationLayout(getApplicationContext(), "Quality", 4.55f));
      informationLayout.addView(new InformationLayout(getApplicationContext(), "Price", 3.15f));
      informationLayout.addView(new InformationLayout(getApplicationContext(), "Usefulness", 1.00f));
      display.setPixels(root);
      display.refresh();

      Log.d(TAG, "Refreshed !");

    }
    catch (IOException e)
    {
      Log.e(TAG, "Error initializing display", e);
    }
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();

    try
    {
      display.close();
    }
    catch (IOException e)
    {
      Log.e(TAG, "Error closing display", e);
    }
  }

}
