# <img src="art/ic_web.png" height="50px"/> MicroPinner

MicroPinner is a lightweight dialog-only application, which lets you create your own notifications.
<br>You can also customize the notifications title and content, as well as its priority and visibility (Android 5.+ only).

<a href="https://play.google.com/store/apps/details?id=de.dotwee.micropinner">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/en_generic_rgb_wo_45.png" />
</a>

## Features

+ **Material Design aligned**
Design follows the [Material Design](https://www.google.com/design/spec/components/dialogs.html#dialogs-specs) dialog-guidelines.

+ **Lightweight**
Weights less than 730kb.

+ **Zero battery & memory impact**  
It won't drain your battery nor your memory.
No background-services, no background-processes.

+ **Backwards compatible**  
Works on Android 4.1 and up. Lower than 4.1 breaks priority feature.

+ **Permission free**
It will neither spy on your SMS's, nor call it's mothership over network.

+ **Open source**
The whole source-code is available here on Github.

+ **Restore functions**
Pins are saved until you swipe them away.

+ **Permanent pins**
Declare your pins as permanent and delete them by clicking on them.

+ **Choosable priority**
Give your pin the priority you think it deserves.

+ **Editable pins**
Edit your pins easily by clicking on them.

#### Android 5.+ only

+ **Choosable visibility**
Hide your pin on the lockscreen by using Android 5's visibility-API: Choose between *public*, *private* and *secret*.

## Screenshots

<img src="art/sc_new.png" height="400px"/>
&nbsp;<img src="art/sc_new_advanced.png" height="400px"/>
&nbsp;<img src="art/sc_new_filled.png" height="400px"/>

<img src="art/sc_pin_example.png" height="400px"/>
&nbsp;<img src="art/sc_edit_filled.png" height="400px"/>
&nbsp;<img src="art/sc_edit_advanced.png" height="400px"/>

<img src="art/sc_light_new.png" height="400px"/>
&nbsp;<img src="art/sc_light_new_advanced.png" height="400px"/>
&nbsp;<img src="art/sc_light_new_filled.png" height="400px"/>

## Changelog

View the [CHANGELOG.md](/docs/CHANGELOG.md).

## Questions / Issues / Bugs

Please check the [FAQ](/docs/FAQ.md) first.

## Build

This project is developed using JetBrain's latest IntelliJ IDEA and the latest Gradle-wrapper.
To compile MicroPinner, simply import this project into **Android Studio or IntelliJ IDEA** and press the build-button.
**You may need to sign the built apk.** Read the official [documentation about signing applications](https://developer.android.com/tools/publishing/app-signing.html) for a how-to.

## Todo

+ <del>Choosable icon</del> Won't be included. To much data-bloat, I want to keep this app as slim as possible.
+ <del>Create a FAQ</del> Read it ([here](/docs/FAQ.md).
+ Choosable notification-category
+ Choosable notification color
+ <del>Allow hiding the pin-icon</del> Available since v1.4 - simply set the priority to 'min'.
+ <del>Allow persistent pins</del> Implemented in v1.4 - use the persistent-checkbox.
+ <del>Implement min-priority</del> Also implemented in Version v1.4.
+ (Code) Documentation
+ Single-line pin (in progress but i need some help, check issue #2)

## License

Copyright (c) 2015 Lukas 'dotwee' Wolfsteiner
The source-code of MicroPinner is licensed under the [_Do What The Fuck You Want To_](/LICENSE.md) public license.
