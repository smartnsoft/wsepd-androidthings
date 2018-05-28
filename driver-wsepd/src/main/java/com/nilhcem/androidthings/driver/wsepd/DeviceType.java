package com.nilhcem.androidthings.driver.wsepd;

public final class DeviceType
{

  final int xDot;

  final int yDot;

  final byte[] lutDefaultFull;

  final byte[] lutDefaultPart;

  final boolean isBlackAndWhiteOnly;

  public DeviceType(int xDot, int yDot, byte[] lutDefaultFull, byte[] lutDefaultPart, boolean isBlackAndWhiteOnly)
  {
    this.xDot = xDot;
    this.yDot = yDot;
    this.lutDefaultFull = lutDefaultFull;
    this.lutDefaultPart = lutDefaultPart;
    this.isBlackAndWhiteOnly = isBlackAndWhiteOnly;
  }

  public enum Preset
  {
    EPD7X5A(new DeviceType(
        640,
        384,
        null,
        null,
        true)
    ),
    EPD7X5B(new DeviceType(
        640,
        384,
        null,
        null,
        false)
    ),
    EPD7X5C(new DeviceType(
        640,
        384,
        null,
        null,
        false)
    );

    public final DeviceType deviceType;

    Preset(DeviceType type)
    {
      deviceType = type;
    }

    public boolean isBlackAndWhiteOnly()
    {
      switch (this)
      {
        case EPD7X5B: // B&W + Red
        case EPD7X5C: // B&W + Yellow
          return false;
        default:
          return true;
      }
    }
  }
}
