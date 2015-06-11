# MicroPinner

MicroPinner is a lightweight dialog-only application, which lets you pin text to your statusbar.
<br>You can also customize the pins priority and visibility (Android 5.+ only).

<img src="art/ic_web.png"/>

## Features

+ **Material Design aligned**  
    Design follows the Material Design Dialog guidelines.
+ **Lightweight**  
    Weights deodexed less than a single megabyte.
+ **Zero battery & memory impact**  
    It won't drain your battery nor your memory. No background-services, no background-processes. Incase it does, your SystemUI is a b**ch.
+ **Permission free** (almost)  
    It will neither spy on your SMS's, nor call it's mothership over network. It just want to know when your devices finished its boot-procress, to restore your pins.
+ **Open source**  
    The complete source-code is available here on Github.
+ **Backwards compatible**  
    Works on Android 4.1 and up.
+ **Choosable priority**  
    Give your pin the priority you think it deserves.
+ **Editable pins**  
    Edit your pins easily by clicking on them.

#### Android 5.+ only

+ **Choosable visibility**  
    Hide your pin on the lockscreen by using Android 5's visibility-API: Choose between *public*, *private* and *secret*.

## Screenshots


<img src="art/sc_new.png" height="400px"/>
&nbsp;<img src="art/sc_new_used.png" height="400px"/>
&nbsp;<img src="art/sc_edit.png" height="400px"/>

## Changelog

1. Version: v1.0 (Initial release)
    - date: 11th June 2015
    - sha1 checksum: <i>ce67e91f42bca5d9d927f73c7d423071f887ad59</i>
    - download: [Github](https://github.com/dotWee/MicroPinner/releases) | [Play Store](https://play.google.com/store/apps/details?id=de.dotwee.micropinner)

## Build

This project is developed using JetBrain's IntelliJ IDEA 14.1 and the latest Gradle-wrapper.

To build this application, import this project into Android Studio or IntelliJ IDEA.

## Todo

+ Implement image-pins
+ Choosable icon
+ Choosable notification-category
+ Choosable notification color (sdk21+ only)

## License

Copyright (c) 2015 Lukas 'dotwee' Wolfsteiner
The source-code of MicroPinner is licensed under the [_Do What The Fuck You Want To_](/LICENSE) public license.
