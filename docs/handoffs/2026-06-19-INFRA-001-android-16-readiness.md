# INFRA-001 Android 16 Readiness

## Backlog Item

`INFRA-001`

## Branch

`codex/infra-001-android-16-readiness`

## Summary

Updated the Android project to target Android SDK 36 and upgraded the build stack to Gradle 8.13 with Android Gradle Plugin 8.13.2. Added owner-facing Windows test guidance, Google Play Store release guidance, and a lightweight UI/UX plan.

Local machine setup was also improved:

- Android SDK Platform 36 is installed.
- Android Build Tools 36.1.0 is installed.
- Gradle 8.13 wrapper cache was seeded locally after an interrupted wrapper download left a partial zip.
- Local emulator `TaskReminder_API35` was created for smoke testing.

## Files Changed

- `README.md`
- `app/build.gradle.kts`
- `docs/ARCHITECTURE.md`
- `docs/BACKLOG.md`
- `docs/DEVELOPMENT_WORKFLOW.md`
- `docs/LOCAL_ANDROID_SETUP.md`
- `docs/PLAY_STORE_RELEASE_GUIDE.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/QA_PLAN.md`
- `docs/RISKS.md`
- `docs/ROADMAP.md`
- `docs/UI_UX_PLAN.md`
- `gradle/libs.versions.toml`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradlew`
- `gradlew.bat`

## Verification

Commands run:

```powershell
git diff --check
$env:JAVA_HOME='C:\tmp\task-reminder-dev\jdk\jdk-17.0.19+10'
$env:ANDROID_SDK_ROOT='C:\tmp\task-reminder-dev\android-sdk'
$env:GRADLE_USER_HOME='C:\tmp\task-reminder-dev\gradle-home'
.\gradlew.bat --no-daemon --console=plain --version
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:compileDebugKotlin
.\gradlew.bat --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:testDebugUnitTest
.\gradlew.bat --offline --no-daemon --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process' :app:assembleDebug
& 'C:\tmp\task-reminder-dev\android-sdk\cmdline-tools\latest\bin\sdkmanager.bat' --sdk_root='C:\tmp\task-reminder-dev\android-sdk' --list_installed
& 'C:\tmp\task-reminder-dev\android-sdk\cmdline-tools\latest\bin\avdmanager.bat' list avd
& 'C:\tmp\task-reminder-dev\android-sdk\emulator\emulator.exe' -list-avds
```

Result:

- `git diff --check`: passed.
- Gradle wrapper: Gradle 8.13 launches on JDK 17.
- `:app:compileDebugKotlin`: passed.
- `:app:testDebugUnitTest`: passed.
- `:app:assembleDebug`: passed and produced `app\build\outputs\apk\debug\app-debug.apk`.
- Installed SDK includes `platforms;android-36` and `build-tools;36.1.0`.
- Local emulator list includes `TaskReminder_API35`.

## Open Questions

- None for this slice.

## Known Limitations

- Android 16 platform/build tools are installed, but the Android 16 emulator system image is not registered yet. A large API 36 system-image download timed out earlier. The current usable emulator is `TaskReminder_API35`, which is enough for smoke testing because the app supports API 26 and higher.
- Google sign-in and Google Tasks sync still require correct Google Cloud OAuth setup before a full owner test can complete.

## Recommended Next Step

Use `docs/PO_WINDOWS_TEST_GUIDE.md` to run the debug APK on `TaskReminder_API35`, then continue with `REM-002` notification scheduling.
