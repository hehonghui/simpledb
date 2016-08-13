# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/dev/adt-bundle-mac-x86_64-20131030/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class com.simple.database.Builder {
    public * ;
}

-keep class com.simple.database.DatabaseHelper {
    public * ;
}

-keep class com.simple.database.dao.** {
    * ;
}

-keep class com.simple.database.crud.** {
    public * ;
}

-keep interface com.simple.database.listeners.** {
    * ;
}

-keepattributes Signature
