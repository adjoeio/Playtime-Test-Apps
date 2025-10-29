# Playtime Studio Sample App

Make sure to put your SDK hash in Android Manifest and adjoe-playtime.plist

```sh
    <meta-data
    android:name="io.adjoe.sdk.Playtime.SDK_HASH"
    android:value="<YOUR_SDK_HASH>" />
```

```sh
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
    <plist version="1.0">
    <dict>
        <key>sdkHash</key>
        <string>YOUR_SDK_HASH</string>
    </dict>
    </plist>
```
