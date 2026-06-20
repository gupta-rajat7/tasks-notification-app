# Product Owner Windows Test Guide

This guide is for a nontechnical product owner testing the Android app from the Windows machine.

## What You Need

- The project folder: `C:\Users\Tanu Gupta\Documents\Tasks Notification App`
- The portable JDK and Android SDK already prepared under `C:\tmp\task-reminder-dev`
- Either an Android emulator or a real Android phone
- A Google account that has Google Tasks data

## Option A: Test On Windows Emulator

Use this when an Android emulator is available. This Windows machine currently has a local emulator named `TaskReminder_API35`.

1. Open PowerShell.
2. Go to the project folder:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
```

3. Set the local build tools:

```powershell
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:ANDROID_AVD_HOME='C:\tmp\task-reminder-dev\avd-home'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
```

4. Build the app:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
```

5. Start or confirm the emulator.

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\cmdline-tools\latest\bin\avdmanager.bat' list avd
& 'C:\tmp\task-reminder-dev\android-sdk\emulator\emulator.exe' -avd TaskReminder_API35
```

Leave the emulator window open. In a second PowerShell window, confirm Android Debug Bridge can see it:

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' devices
```

6. Install the debug app:

```powershell
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' install -r app\build\outputs\apk\debug\app-debug.apk
```

7. Open the app on the emulator and test the checklist below.

## Option B: Test On A Real Android Phone

Use this when emulator setup is slow or unavailable.

1. On the phone, enable Developer options and USB debugging.
2. Connect the phone by USB.
3. In PowerShell, run:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' devices
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
& 'C:\tmp\task-reminder-dev\android-sdk\platform-tools\adb.exe' install -r app\build\outputs\apk\debug\app-debug.apk
```

4. Accept the USB debugging prompt on the phone if asked.
5. Open the app on the phone.

## First Owner Test Checklist

- App opens without crashing.
- Bottom navigation has Today, Tasks, and Settings.
- Settings can be changed and still remain after closing and reopening the app.
- Settings > Quiet hours can be turned on and off.
- Settings > Quiet hours start and end can be changed.
- Google sign-in button starts a sign-in flow.
- If sign-in cannot complete, note the exact message shown.
- Today > Enable turns on notification permission when Android asks.
- Today > Check now shows a reminder notification when pending tasks are cached and eligible.
- Today > Check now does not show a notification during active quiet hours.
- Automatic reminder checks are scheduled after app startup, Settings reminder changes, Review now, Snooze, and Google Tasks sync.
- For quick testing, use Today > Check now. Waiting for the automatic reminder may take longer than the configured interval because Android schedules background work to protect battery.
- The current V1 app does not yet measure real cross-app screen activity. That remains a future optional Usage Access feature.
- Settings > Screen activity diagnostics can check Usage Access, open Android Usage Access settings, and scan recent activity event counts for the `SCR-001` feasibility spike.
- Reminder notification has Review, Snooze, and Done for now actions.
- Snooze removes the visible notification.
- Done for now removes the visible notification.
- App does not feel visually crowded.
- Text is readable in light and dark mode.
- No screen feels like an advertisement or sales page.

## Known Setup Dependencies

Google sign-in and Google Tasks sync require Google Cloud OAuth setup:

- Android OAuth client for package `com.guptarajat.screenactivetaskreminder`
- Correct debug signing SHA-1 configured in Google Cloud
- Google Tasks API enabled
- The app's web client ID configured for Credential Manager

See `docs/GOOGLE_SIGN_IN_SETUP.md` for the technical setup details.

## How To Report A Test Result To Codex

Send a short message with:

- What you tested: emulator or phone.
- What worked.
- What failed.
- Any screenshot or exact error text.
- Whether you were signed into Google successfully.
