package com.nilhcem.androidthings.driver.wsepd;

public final class DeviceType {
    final int xDot;
    final int yDot;
    final byte[] lutDefaultFull;
    final byte[] lutDefaultPart;
    final boolean isUsingHat;

    public DeviceType(int xDot, int yDot, byte[] lutDefaultFull, byte[] lutDefaultPart, boolean isUsingHat) {
        this.xDot = xDot;
        this.yDot = yDot;
        this.lutDefaultFull = lutDefaultFull;
        this.lutDefaultPart = lutDefaultPart;
        this.isUsingHat = isUsingHat;
    }

    public enum Preset {
        EPD2X9(new DeviceType(
                128,
                296,
                new byte[]{(byte) 0x02, (byte) 0x02, (byte) 0x01, (byte) 0x11, (byte) 0x12, (byte) 0x12, (byte) 0x22, (byte) 0x22, (byte) 0x66, (byte) 0x69, (byte) 0x69, (byte) 0x59, (byte) 0x58, (byte) 0x99, (byte) 0x99, (byte) 0x88, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF8, (byte) 0xB4, (byte) 0x13, (byte) 0x51, (byte) 0x35, (byte) 0x51, (byte) 0x51, (byte) 0x19, (byte) 0x01, (byte) 0x00},
                new byte[]{(byte) 0x10, (byte) 0x18, (byte) 0x18, (byte) 0x08, (byte) 0x18, (byte) 0x18, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x13, (byte) 0x14, (byte) 0x44, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
                false)
        ),
        EPD02X13(new DeviceType(
                122,
                250,
                new byte[]{(byte) 0x22, (byte) 0x55, (byte) 0xAA, (byte) 0x55, (byte) 0xAA, (byte) 0x55, (byte) 0xAA, (byte) 0x11, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1E, (byte) 0x1E, (byte) 0x1E, (byte) 0x1E, (byte) 0x1E, (byte) 0x1E, (byte) 0x1E, (byte) 0x1E, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
                new byte[]{(byte) 0x18, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
                false)
        ),
        EPD1X54(new DeviceType(
                200,
                200,
                new byte[]{(byte) 0x02, (byte) 0x02, (byte) 0x01, (byte) 0x11, (byte) 0x12, (byte) 0x12, (byte) 0x22, (byte) 0x22, (byte) 0x66, (byte) 0x69, (byte) 0x69, (byte) 0x59, (byte) 0x58, (byte) 0x99, (byte) 0x99, (byte) 0x88, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF8, (byte) 0xB4, (byte) 0x13, (byte) 0x51, (byte) 0x35, (byte) 0x51, (byte) 0x51, (byte) 0x19, (byte) 0x01, (byte) 0x00},
                new byte[]{(byte) 0x10, (byte) 0x18, (byte) 0x18, (byte) 0x08, (byte) 0x18, (byte) 0x18, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x13, (byte) 0x14, (byte) 0x44, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
                false)
        ),
        EPD7X5(new DeviceType(
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
                true)
        );

        public final DeviceType deviceType;

        Preset(DeviceType type) {
            deviceType = type;
        }
    }
}
