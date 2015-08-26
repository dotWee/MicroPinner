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
+ **Restore functions**
<br>    Pins are saved until you swipe them away.
<br>    To restore your pins for example after a reboot, simply open the dialog.
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

<img src="art/sc_example.png" height="400px"/>
&nbsp;<img src="art/sc_example_min.png" height="400px"/>

## Changelog

View the [CHANGELOG.md](CHANGELOG.md).

## Questions / Issues / Bugs

Please check the [FAQ](/docs/FAQ.md) first.

## Build

This project is developed using JetBrain's IntelliJ IDEA 14.1 and the latest Gradle-wrapper.
To compile MicroPinner, simply import this project into Android Studio or IntelliJ IDEA and press the build-button.

## Todo

+ Choosable icon (not sure about including icons - that would increase the application size about 3kb per image)
+ <del>Create a FAQ<del> ([here](/docs/FAQ.md)) and a link inside the app
+ Choosable notification-category
+ Choosable notification color
+ <del>Allow hiding the pin-icon</del> (implemented, available since v1.4 - simply set the priority to 'min')
+ <del>Allow persistent pins</del> (implemented, available since v1.4)
+ <del>Implement min-priority</del> (implemented, available since v1.4)
+ Implement image-pins
+ (Code) Documentation
+ Single-line pin (in progress but help needed, check issue #2)

## License

Copyright (c) 2015 Lukas 'dotwee' Wolfsteiner
The source-code of MicroPinner is licensed under the [_Do What The Fuck You Want To_](/LICENSE.md) public license.
