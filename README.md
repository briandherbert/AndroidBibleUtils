# AndroidBibleUtils
General tools for integrating and navigating the Bible on Android

## Intended for use as a Jitpack library

Use the latest tagged version shown here:
[https://jitpack.io/#briandherbert/AndroidBibleUtils](https://jitpack.io/#briandherbert/AndroidBibleUtils)

In your projects's build.gradle file, add the jitpack repo:
```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
Then in the app module's build.gradle, add the dependency (use latest version):

`implementation 'com.github.briandherbert:AndroidBibleUtils:v3.0.3'`


## Usage
See the [example app](https://github.com/briandherbert/AndroidBibleUtils/blob/master/app/src/main/java/com/example/brianherbert/androidbibleutils/DemoBibleUtilsActivity.kt) for general usage.

