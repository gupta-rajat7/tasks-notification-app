param(
    [switch]$StartEmulator,
    [switch]$SkipBuild,
    [switch]$SkipInstall,
    [switch]$ResetAppData,
    [switch]$CheckOnly,
    [string]$AvdName = "TaskReminder_API35",
    [int]$BootTimeoutSeconds = 180
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$devRoot = "C:\tmp\task-reminder-dev"
$jdkHome = Join-Path $devRoot "jdk\jdk-17.0.19+10"
$androidSdkFallback = Join-Path $devRoot "android-sdk"
$gradleHome = Join-Path $devRoot "gradle-home"
$fallbackGradleHome = Join-Path $repoRoot ".gradle-home"
$packageName = "com.guptarajat.screenactivetaskreminder"
$activityName = "$packageName/.MainActivity"

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "== $Message =="
}

function Get-LocalProperty {
    param([string]$Name)

    $path = Join-Path $repoRoot "local.properties"
    if (-not (Test-Path $path)) {
        return $null
    }

    $match = Get-Content $path | Where-Object { $_ -match "^\s*$([regex]::Escape($Name))\s*=" } | Select-Object -First 1
    if (-not $match) {
        return $null
    }

    return ($match -replace "^\s*$([regex]::Escape($Name))\s*=\s*", "").Trim()
}

function Convert-LocalPropertiesPath {
    param([string]$Value)

    if (-not $Value) {
        return $null
    }

    return $Value.Replace("\:", ":").Replace("\\", "\")
}

function Require-Path {
    param(
        [string]$Path,
        [string]$Description
    )

    if (-not (Test-Path $Path)) {
        throw "$Description was not found at $Path"
    }
}

function Test-DirectoryWritable {
    param([string]$Path)

    try {
        if (-not (Test-Path $Path)) {
            New-Item -ItemType Directory -Path $Path -Force -ErrorAction Stop | Out-Null
        }

        $testFile = Join-Path $Path ".run-app-write-test-$PID.tmp"
        Set-Content -Path $testFile -Value "ok" -NoNewline -ErrorAction Stop
        Remove-Item -Path $testFile -Force -ErrorAction Stop
        return $true
    } catch {
        return $false
    }
}

function Get-ConnectedDeviceLines {
    param([string]$AdbPath)

    $output = & $AdbPath devices
    return @($output | Select-Object -Skip 1 | Where-Object { $_ -match "\sdevice$" })
}

function Wait-ForDevice {
    param(
        [string]$AdbPath,
        [int]$TimeoutSeconds
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        $devices = Get-ConnectedDeviceLines $AdbPath
        if ($devices.Count -gt 0) {
            return $true
        }
        Start-Sleep -Seconds 5
    } while ((Get-Date) -lt $deadline)

    return $false
}

Set-Location $repoRoot

$sdkFromLocalProperties = Convert-LocalPropertiesPath (Get-LocalProperty "sdk.dir")
$androidSdk = $env:ANDROID_SDK_ROOT
if (-not $androidSdk) {
    $androidSdk = $sdkFromLocalProperties
}
if (-not $androidSdk) {
    $androidSdk = $androidSdkFallback
}

$adb = Join-Path $androidSdk "platform-tools\adb.exe"
$emulator = Join-Path $androidSdk "emulator\emulator.exe"
$gradleWrapper = Join-Path $repoRoot "gradlew.bat"
$apkPath = Join-Path $repoRoot "app\build\outputs\apk\debug\app-debug.apk"

Write-Host "Screen Active Task Reminder - Windows Run Script"
Write-Host "Repo: $repoRoot"

Require-Path $gradleWrapper "Gradle wrapper"
Require-Path $jdkHome "Portable JDK"
Require-Path $androidSdk "Android SDK"
Require-Path $adb "ADB"
if ($StartEmulator) {
    Require-Path $emulator "Android emulator"
}

$env:JAVA_HOME = $jdkHome
$env:ANDROID_SDK_ROOT = $androidSdk
$env:ANDROID_AVD_HOME = Join-Path $env:USERPROFILE ".android\avd"
if ((Test-Path $gradleHome) -and (Test-DirectoryWritable $gradleHome)) {
    $env:GRADLE_USER_HOME = $gradleHome
} elseif (Test-DirectoryWritable $fallbackGradleHome) {
    $env:GRADLE_USER_HOME = $fallbackGradleHome
}

Write-Host "JAVA_HOME: $env:JAVA_HOME"
Write-Host "ANDROID_SDK_ROOT: $env:ANDROID_SDK_ROOT"
Write-Host "ANDROID_AVD_HOME: $env:ANDROID_AVD_HOME"
if ($env:GRADLE_USER_HOME) {
    Write-Host "GRADLE_USER_HOME: $env:GRADLE_USER_HOME"
}

if ($CheckOnly) {
    Write-Step "Check only"
    Write-Host "Run script prerequisites are present."
    Write-Host "Next: run without -CheckOnly to build, install, and launch."
    exit 0
}

if (-not $SkipBuild) {
    Write-Step "Build debug APK"
    & $gradleWrapper --no-daemon --max-workers=1 --console=plain "-Pkotlin.compiler.execution.strategy=in-process" :app:assembleDebug
    if ($LASTEXITCODE -ne 0) {
        throw "Debug APK build failed with exit code $LASTEXITCODE"
    }
}

Require-Path $apkPath "Debug APK"

$devices = Get-ConnectedDeviceLines $adb
if (($devices.Count -eq 0) -and $StartEmulator) {
    Write-Step "Start emulator"
    Start-Process -FilePath $emulator -ArgumentList @("-avd", $AvdName)
}

if ((Get-ConnectedDeviceLines $adb).Count -eq 0) {
    Write-Step "Wait for Android device"
    if (-not (Wait-ForDevice $adb $BootTimeoutSeconds)) {
        throw "No Android emulator or phone is connected. Start the emulator, connect a phone, or rerun with -StartEmulator."
    }
}

if (-not $SkipInstall) {
    if ($ResetAppData) {
        Write-Step "Reset app data"
        & $adb uninstall $packageName | Out-Host
    }

    Write-Step "Install debug APK"
    & $adb install -r $apkPath
    if ($LASTEXITCODE -ne 0) {
        throw "APK install failed with exit code $LASTEXITCODE"
    }
}

Write-Step "Launch app"
& $adb shell am start -n $activityName
if ($LASTEXITCODE -ne 0) {
    throw "App launch failed with exit code $LASTEXITCODE"
}

Write-Host ""
Write-Host "App launch command completed."
Write-Host "Use docs\PO_WINDOWS_TEST_GUIDE.md for the test checklist."
