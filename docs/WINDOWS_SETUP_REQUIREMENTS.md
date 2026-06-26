# Windows Setup Requirements

Use this checklist when setting up the project on a different Windows computer. The repository does not commit machine-specific files such as `local.properties`, SDK paths, debug signing keys, or Google OAuth IDs, so each Windows machine needs these local steps.

## Requirements

- Windows 10 or newer.
- Git for Windows.
- PowerShell.
- JDK 17.
- Android SDK Platform 36.
- Android SDK Build Tools 36.x.
- Android SDK Platform Tools.
- Android Emulator tools, if testing with an emulator.
- A real Android phone with Google Play services, or a Google Play / Google APIs emulator image for Google sign-in testing.
- Google Cloud OAuth setup for this app.

## Step 1: Clone The Repository

```powershell
cd "$env:USERPROFILE\Documents"
git clone https://github.com/gupta-rajat7/tasks-notification-app.git 'Tasks Notification App'
cd 'Tasks Notification App'
```

If the repository URL changes, copy the clone URL from GitHub and use that instead.

## Step 2: Install JDK 17

Install any normal JDK 17 distribution, then set `JAVA_HOME` for the current PowerShell window:

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-17'
```

Use the real folder on that computer. The folder must contain `bin\java.exe` and `bin\keytool.exe`.

The original development machine also supports this portable path:

```powershell
C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10
```

## Step 3: Install Android SDK Packages

Install Android Studio or Android command-line tools, then install these SDK packages:

- `platforms;android-36`
- `build-tools;36.x`
- `platform-tools`
- `emulator`

For Google sign-in testing on an emulator, also install a Google Play or Google APIs system image. A plain Android system image can run the app shell but cannot add Google accounts.

## Step 4: Point The Project To The Android SDK

Create `local.properties` in the repository root. Use the Android SDK path from the new Windows computer:

```properties
sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

For the current PowerShell window, set the same SDK path:

```powershell
$env:ANDROID_SDK_ROOT='C:\Users\<you>\AppData\Local\Android\Sdk'
```

If you use the repo's portable layout, use:

```properties
sdk.dir=C\:\\tmp\\task-reminder-dev\\android-sdk
```

`local.properties` is intentionally ignored by Git.

## Step 5: Build Once

Run:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
```

This creates the local Android debug keystore if it does not already exist.

## Step 6: Configure Google OAuth For This Machine

Print the debug SHA-1:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -PrintDebugSha1
```

In Google Cloud Console:

1. Use the app's Google Cloud project.
2. Enable the Google Tasks API.
3. Configure the OAuth consent screen.
4. Add the Google Tasks read-only scope: `https://www.googleapis.com/auth/tasks.readonly`.
5. Create or update the Android OAuth client:
   - Package: `com.guptarajat.screenactivetaskreminder`
   - SHA-1: the value printed from this Windows computer.
6. Create or reuse the Web OAuth client.
7. Add your test Google account to the OAuth consent screen test users if the app is still in testing mode.

Save the Web Client ID locally:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'
```

Do not commit OAuth client secrets. The Android app needs the Web Client ID, not a Web Client Secret.

## Step 7: Connect A Test Device

For the most reliable Google sign-in test, use a real Android phone:

1. Sign in to a Google account on the phone.
2. Enable Developer options.
3. Enable USB debugging.
4. Connect the phone by USB.
5. Accept the USB debugging prompt.
6. Confirm the phone appears as `device`:

```powershell
& "$env:ANDROID_SDK_ROOT\platform-tools\adb.exe" devices
```

If using an emulator, create one from a Google Play or Google APIs image. Do not use a plain `default` image for Google sign-in.

## Step 8: Run The Readiness Check

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -SkipGitHub
```

Fix any `FAIL` lines first. `WARN Google account support` means the connected emulator or phone cannot complete Google sign-in.

## Step 9: Run Tests

```powershell
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
```

## Step 10: Install And Launch The App

When a phone or emulator is connected:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1
```

Use `-StartEmulator` only when you want the script to start the local emulator:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1 -StartEmulator
```

## Troubleshooting

- `Android SDK was not found`: fix `sdk.dir` in `local.properties` or set `ANDROID_SDK_ROOT`.
- `JDK 17 was not found`: set `JAVA_HOME` to a JDK 17 folder.
- `Google Web Client ID` warning: run `tools\setup_google_oauth.ps1 -WebClientId ...`.
- `Google account support` warning: use a real Android phone or a Google Play / Google APIs emulator image.
- Phone shows `unauthorized` in `adb devices`: unlock the phone and accept the USB debugging prompt.
- More than one device is connected: close the emulator or disconnect extra phones before running install commands.
