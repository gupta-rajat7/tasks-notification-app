# Local Android Build Setup

For a fresh Windows computer, start with `docs/WINDOWS_SETUP_REQUIREMENTS.md`. This file records the local build paths and commands used by this workspace.

This project can build with a local portable toolchain. The current workspace uses:

- JDK 17: `C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10`
- Android SDK: `C:\tmp\task-reminder-dev\android-sdk`
- Android SDK Platform: API 36, Android 16
- Android Gradle Plugin: 8.13.2
- Gradle wrapper: 8.13

`local.properties` should exist on each machine and point Gradle to that machine's Android SDK:

```properties
sdk.dir=C\:\\tmp\\task-reminder-dev\\android-sdk
```

Use the real Android SDK path on a different Windows computer, for example:

```properties
sdk.dir=C\:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

`local.properties` is ignored by Git because it is machine-specific. Google OAuth values are also stored there locally and must be configured separately on each Windows computer.

## Build Commands

Run these from the repository root:

```powershell
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
```

Use elevated shell permissions if Gradle reports a loopback connection error. Keep `--max-workers=1` on this Windows setup to avoid Gradle transform-cache move issues.

The `-Pkotlin.compiler.execution.strategy=in-process` argument is intentional for this Windows machine. Quote the `-P` argument in PowerShell:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
```

## Installed SDK Check

To confirm the local Android SDK has the expected packages:

```powershell
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
& 'C:\tmp\task-reminder-dev\android-sdk\cmdline-tools\latest\bin\sdkmanager.bat' --sdk_root='C:\tmp\task-reminder-dev\android-sdk' --list_installed
```

Expected minimum packages:

- `platforms;android-36`
- `build-tools;36.1.0` or another installed 36.x build tools package
- `platform-tools`
- `emulator`

## Emulator Status

This project can be tested on either:

- A Windows Android emulator.
- A real Android phone connected by USB.

The portable SDK has Android 16 platform/build tools installed. An Android 16 emulator image may still need to be installed separately because emulator system images are large and can take a long time to download.

Current local emulator:

- `TaskReminder_API35`, based on Android 15, usable for smoke testing because the app supports Android API 26 and higher.

Google sign-in requires Google account support on the Android device. Use a real Android phone, or an emulator image labeled Google Play or Google APIs. A plain Android `default` emulator image can open the app shell but cannot add Google accounts.
