# <img src="art/ic_web.png" height="50px"/> MicroPinner

MicroPinner is a lightweight dialog-only application, which lets you pin text to your statusbar.
<br>You can also customize the pins priority and visibility (Android 5.+ only).

<a href="https://play.google.com/store/apps/details?id=de.dotwee.micropinner">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

## Features

+ **Material Design aligned**
<br>    Design follows the [Material Design Dialog](https://www.google.com/design/spec/components/dialogs.html#dialogs-specs) guidelines.
+ **Lightweight**
<br>    Weights deodexed less than 750kb.
+ **Zero battery & memory impact**
<br>    It won't drain your battery nor your memory. No background-services, no background-processes. Incase it does, tell your SystemUI she's a b**ch.
+ **Backwards compatible**
<br>    Works on Android 4.1 and up. Lower than 4.1 breaks priority feature.
+ **Permission free**
<br>    It will neither spy on your SMS's, nor call it's mothership over network.
<br>    It just want to know when your devices finished its boot-procress, to restore your pins.
+ **Open source**
<br>    The whole source-code is available here on Github.
+ **Permanent pins**
<br>    Declare your pins as permenent and delete them by clicking on them.
+ **Choosable priority**
<br>    Give your pin the priority you think it deserves.
+ **Editable pins**
<br>    Edit your pins easily by clicking on them.

#### Android 5.+ only

+ **Choosable visibility**
<br>    Hide your pin on the lockscreen by using Android 5's visibility-API: Choose between *public*, *private* and *secret*.

## Screenshots

<img src="art/sc_new.png" height="400px"/>
&nbsp;<img src="art/sc_new_used.png" height="400px"/>
&nbsp;<img src="art/sc_edit.png" height="400px"/>

## Changelog

1. Version: v1.0 (Initial release)
    - date: 11th June 2015
    - sha1 checksum: *ce67e91f42bca5d9d927f73c7d423071f887ad59*
    - direct download: [Github](https://github.com/dotWee/MicroPinner/releases/download/release-v1.0/release_v1.0.apk)

2. Version: v1.1 (Disallow empty titles and let user decide if he wants to see the "new pin"-notification)
    - date: 12th June 2015
    - sha1 checksum: *3003cedb937a73f943e618c9f7cd3e82bc468e20*
    - direct download: [Github](https://github.com/dotWee/MicroPinner/releases/download/release-v1.1/release_v1.1.apk)

3. Version: v1.2 (Fix non-working visibility & priority)
    - date: 12th June 2015
    - sha1 checksum: *a01095619f18e4d9f6b35065125e907f64246948*
    - direct download: [Github](https://github.com/dotWee/MicroPinner/releases/download/release-v1.2/release_v1.2.apk);

4. Version: v1.3 (Reworked layout | German translation | MIN-priority | Persistent pins | Auto-capitalization | Hideable pin icon (you need to set the priority to min!))
    - date: 16th June 2015
    - sha1 checksum: *885f8ac5dfbf3ceabc1d6b37526ffc9b85b3d0cd*
    - direct download: [Github](https://github.com/dotWee/MicroPinner/releases/download/release-v1.3/release_v1.3.apk)

## Questions / Issues / Bugs

Please check the [FAQ](/docs/FAQ.md) first.

## Build

This project is developed using JetBrain's IntelliJ IDEA 14.1 and the latest Gradle-wrapper.
To compile MicroPinner, simply import this project into Android Studio or IntelliJ IDEA and press the build-button.

## Todo

+ Choosable icon (not sure about including icons - that would increase the application size about 3kb per image)
+ Create a FAQ and a link inside the app
+ Choosable notification-category
+ Choosable notification color
+ <del>Allow hiding the pin-icon</del> (implemented, simply set the priority to 'min')
+ <del>Allow persistent pins</del> (implemented, will be available in next update)
+ <del>Implement min-priority</del> (implemented, will also be available in next update)
+ Implement image-pins
+ (Code) Documentation
+ Single-line pin

## License

Copyright (c) 2015 Lukas 'dotwee' Wolfsteiner
The source-code of MicroPinner is licensed under the [_Do What The Fuck You Want To_](/LICENSE.md) public license.
