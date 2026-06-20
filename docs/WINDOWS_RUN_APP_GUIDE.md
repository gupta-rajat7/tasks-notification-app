# Windows Run App Guide

This guide is for a nontechnical product owner who wants to run the Android app from the Windows machine.

Use this when you want to see the app on the Windows Android emulator.

## What You Will Use

- Project folder: `C:\Users\Tanu Gupta\Documents\Tasks Notification App`
- JDK: `C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10`
- Android SDK: `C:\tmp\task-reminder-dev\android-sdk`
- Emulator name: `TaskReminder_API35`
- App name: `Screen Active Task Reminder`

## Step 1: Open PowerShell

Open Windows PowerShell.

Copy and paste this:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:ANDROID_AVD_HOME="$env:USERPROFILE\.android\avd"
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
```

## Step 2: Build The App

Copy and paste this:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
```

Success means you see `BUILD SUCCESSFUL`.

The app file will be created here:

```text
app\build\outputs\apk\debug\app-debug.apk
```

## Step 3: Start The Emulator

In the same PowerShell window, copy and paste this:

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\cmdline-tools\latest\bin\avdmanager.bat' list avd
& 'C:\tmp\task-reminder-dev\android-sdk\emulator\emulator.exe' -avd TaskReminder_API35
```

Leave this PowerShell window open. The emulator command may keep running while the Android phone window is open.

Wait until the emulator shows the Android home screen or app drawer.

## Step 4: Open A Second PowerShell Window

Open a second Windows PowerShell window.

Copy and paste this:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
```

## Step 5: Confirm The Emulator Is Connected

Copy and paste this:

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' devices
```

Success looks similar to this:

```text
List of devices attached
emulator-5554   device
```

If it says `no devices`, wait 30 seconds and run the same command again.

## Step 6: Install The App

Copy and paste this:

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' install -r app\build\outputs\apk\debug\app-debug.apk
```

Success means you see:

```text
Success
```

## Step 7: Launch The App

Copy and paste this:

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' shell am start -n com.guptarajat.screenactivetaskreminder/.MainActivity
```

The app should open in the emulator.

## Step 8: If You Do Not See The App

Try these in order:

1. Open the emulator app drawer and search for `Screen Active Task Reminder`.
2. Run the launch command again:

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' shell am start -n com.guptarajat.screenactivetaskreminder/.MainActivity
```

3. Confirm the install succeeded:

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' shell pm list packages | Select-String screenactivetaskreminder
```

If the package appears, the app is installed.

## Step 9: What To Test First

Start with this quick check:

- App opens without crashing.
- Bottom tabs show Today, Tasks, and Settings.
- Settings opens.
- Reminder interval plus and minus buttons work.
- Quiet hours toggle works.
- Today screen opens.
- Tasks screen opens.

For the full test checklist, use `docs/PO_WINDOWS_TEST_GUIDE.md`.

## Google Sign-In Note

If the app says:

```text
Google sign-in needs OAuth setup before real accounts can connect.
```

that is expected until Google Cloud OAuth is configured.

Use `docs/PO_GOOGLE_OAUTH_SETUP_GUIDE.md` before retesting real Google sign-in.

## If The Emulator Is Slow

The Windows emulator can be slow even when the app is fine.

Before judging app speed:

- Wait until the emulator fully boots.
- Stop any running Gradle build.
- Close other heavy apps.
- Check whether Android Settings and the home screen are also slow.
- Retest on a real Android phone before beta decisions.

Use `docs/PERFORMANCE_TEST_GUIDE.md` for the full performance checklist.

## Optional: Fresh Install

Use this only if Codex asks you to reset the app. It deletes this app's local data from the emulator.

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' uninstall com.guptarajat.screenactivetaskreminder
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' install -r app\build\outputs\apk\debug\app-debug.apk
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' shell am start -n com.guptarajat.screenactivetaskreminder/.MainActivity
```

## Optional: Run On A Real Android Phone

Use a phone when emulator setup is slow or when you want more realistic performance.

1. Enable Developer options and USB debugging on the phone.
2. Connect the phone by USB.
3. Accept the USB debugging prompt on the phone.
4. In PowerShell, run:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' devices
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' install -r app\build\outputs\apk\debug\app-debug.apk
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' shell am start -n com.guptarajat.screenactivetaskreminder/.MainActivity
```

If more than one device is connected, ask Codex before continuing.
