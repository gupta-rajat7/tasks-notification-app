# Local Android Build Setup

This project can build with a local portable toolchain. The current workspace uses:

- JDK 17: `C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10`
- Android SDK: `C:\tmp\task-reminder-dev\android-sdk`

`local.properties` should exist on the machine and point Gradle to the Android SDK:

```properties
sdk.dir=C\:\\tmp\\task-reminder-dev\\android-sdk
```

`local.properties` is ignored by Git because it is machine-specific.

## Build Commands

Run these from the repository root:

```powershell
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
.\gradlew.bat --no-daemon --max-workers=1 :app:assembleDebug
.\gradlew.bat --no-daemon --max-workers=1 :app:testDebugUnitTest
```

Use elevated shell permissions if Gradle reports a loopback connection error. Keep `--max-workers=1` on this Windows setup to avoid Gradle transform-cache move issues.

If Kotlin compilation hangs on this Windows setup, rerun with Kotlin compilation in-process. Quote the `-P` argument in PowerShell:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
```
