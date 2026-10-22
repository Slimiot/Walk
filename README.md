# SLIMiot Walk

Android activity-tracking app for SlimIoT wearables. It reads motion data over Bluetooth LE and reports **steps, distance and calories burned**.

## Features

- Scan for and connect to SlimIoT BLE devices
- Step counting from the device's IMU (accelerometer), with moving-average smoothing
- Distance walked and calories burned
- Heart rate with HRV / stress (RMSSD, SDNN)
- User profile and cloud sync (Firebase)

## Build

1. Open the `SLIMiot Walk` folder in **Android Studio**.
2. Add your own Firebase config: download `google-services.json` from your Firebase project and place it in `SLIMiot Walk/app/`. (It is git-ignored and not included in this repo.)
3. Build and run on a phone with Bluetooth LE (Android 7.0 / API 24 or later).

## Tech stack

Java · Android SDK 33 · BLE via [FastBle](https://github.com/Jasonchenlijian/FastBle) and [RxAndroidBle](https://github.com/dariuszseweryn/RxAndroidBle) · [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) / [AndroidPlot](https://androidplot.com/) · Apache Commons Math · Firebase Auth & Firestore

---

Part of the [SlimIoT](https://www.slimiot.com) health-tech ecosystem. Questions or pilots: [info@slimiot.com](mailto:info@slimiot.com)
