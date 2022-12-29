#!/bin/bash

## US
# Android
curl \
  -F "payload=@../apps/my-demo-app-android.apk" \
  -F name=my-demo-app-android.apk \
  -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  \
  'https://api.us-west-1.saucelabs.com/v1/storage/upload'
# iOS
curl \
  -F "payload=@../apps/SauceLabs-Demo-App.Simulator.zip" \
  -F name=SauceLabs-Demo-App.Simulator.zip \
  -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  \
  'https://api.us-west-1.saucelabs.com/v1/storage/upload'
curl \
  -F "payload=@../apps/SauceLabs-Demo-App.ipa" \
  -F name=SauceLabs-Demo-App.ipa \
  -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  \
  'https://api.us-west-1.saucelabs.com/v1/storage/upload'

## EU
# Android
curl \
  -F "payload=@../apps/my-demo-app-android.apk" \
  -F name=my-demo-app-android.apk \
  -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  \
  'https://api.eu-central-1.saucelabs.com/v1/storage/upload'
# iOS
curl \
  -F "payload=@../apps/SauceLabs-Demo-App.Simulator.zip" \
  -F name=SauceLabs-Demo-App.Simulator.zip \
  -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  \
  'https://api.eu-central-1.saucelabs.com/v1/storage/upload'
curl \
  -F "payload=@../apps/SauceLabs-Demo-App.ipa" \
  -F name=SauceLabs-Demo-App.ipa \
  -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  \
  'https://api.eu-central-1.saucelabs.com/v1/storage/upload'
