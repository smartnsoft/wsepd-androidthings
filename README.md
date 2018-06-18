[ ![Download](https://api.bintray.com/packages/smartnsoft/maven/epaper-hat-driver/images/download.svg) ](https://bintray.com/smartnsoft/maven/epaper-hat-driver/_latestVersion)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![TeamCity status](https://ci.smartnsoft.com/app/rest/builds/buildType(id:AndroidThingsEPaperHatDriver)/statusIcon)](https://ci.smartnsoft.com/viewType.html?buildTypeId=AndroidThingsEPaperHatDriver)

# Waveshare e-Paper HAT Display module driver for Android Things

*A very simple Waveshare e-Ink display module driver implementation for Android Things*  

![Preview](assets/preview.jpg "Preview")  

## Download

```groovy
dependencies {
    compile 'com.smartnsoft.androidthings:epaper-hat-driver:0.0.1'
}
```

## Usage

*Tested on [Waveshare 7.5inch e-Paper Module (C)][module_wiki]*

```java
// Access the EPD7X5C display
EPaperDisplay display;
EPaperDisplay.DeviceType epd7x5c = EPaperDisplay.DeviceType.Preset.EPD7X5C.deviceType;
display = EPaperDisplayFactory.create(SPI_NAME, BUSY_GPIO, RESET_GPIO, DC_GPIO, epd7x5c, Orientation.PORTRAIT);

// Clear screen
display.clear();

// Set a bitmap
Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.rocket);
display.setPixels(bitmap);

// Refresh the screen
display.refresh();

// Close the display when finished
display.close();
```

### Hardware connection

| e-Paper | Raspberry Pi 3 |    Pico i.MX7D    |
| ------- | -------------- | ----------------- |
| 3.3V    | 3.3V           | 3.3V              |
| GND     | GND            | GND               |
| DIN     | MOSI (#19)     | MOSI (#19)        |
| CLK     | SCLK (#23)     | SCLK (#23)        |
| CS      | CE0 (#24)      | SPI3 (SS1) (#24)  |
| DC      | BCM25 (#22)    | GPIO5_IO00 (#22)  |
| RST     | BCM17 (#11)    | GPIO1_IO10 (#15)  |
| BUSY    | BCM24 (#18)    | GPIO6_IO12 (#18)  |

## Kudos to

* Novoda, and Blundell for their [InkypHat driver][inkyphat]
* Nilhcem for his [WSEPD driver][wsepd]

## License

This SDK is under the MIT license.

## Author

This Monero Miner was proudly made at [Smart&Soft](https://smartnsoft.com/), Paris FRANCE


[module_wiki]: https://www.waveshare.com/wiki/7.5inch_e-Paper_HAT_(B)
[inkyphat]: https://www.novoda.com/blog/porting-a-python-library-to-android-things-the-inkyphat/
[wsepd]: https://github.com/Nilhcem/wsepd-androidthings
