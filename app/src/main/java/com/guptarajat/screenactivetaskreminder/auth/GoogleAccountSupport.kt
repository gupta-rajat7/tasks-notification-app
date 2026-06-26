package com.guptarajat.screenactivetaskreminder.auth

import android.content.Context
import android.content.pm.PackageManager

private const val GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms"

@Suppress("DEPRECATION")
fun Context.hasGoogleAccountSupport(): Boolean =
    try {
        applicationContext.packageManager.getPackageInfo(GOOGLE_PLAY_SERVICES_PACKAGE, 0)
        true
    } catch (error: PackageManager.NameNotFoundException) {
        false
    }

fun googleAccountSupportUnavailableMessage(): String =
    "This Android device does not include Google account support. Use a Google Play emulator or a real Android phone, then try signing in again."
