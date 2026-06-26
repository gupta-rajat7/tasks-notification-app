param(
    [switch]$RunBuild,
    [switch]$RunTests,
    [switch]$SkipGitHub
)

$ErrorActionPreference = "Continue"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$expectedDevRoot = "C:\tmp\task-reminder-dev"
$expectedJdk = Join-Path $expectedDevRoot "jdk\jdk-17.0.19+10"
$expectedAndroidSdk = Join-Path $expectedDevRoot "android-sdk"
$expectedGradleHome = Join-Path $expectedDevRoot "gradle-home"
$fallbackGradleHome = Join-Path $repoRoot ".gradle-home"
$expectedAvdName = "TaskReminder_API35"
$webClientIdPattern = "^[A-Za-z0-9._-]+\.apps\.googleusercontent\.com$"

$script:failures = 0
$script:warnings = 0

function Write-Result {
    param(
        [string]$Status,
        [string]$Name,
        [string]$Detail = ""
    )

    $line = "[$Status] $Name"
    if ($Detail.Trim().Length -gt 0) {
        $line = "$line - $Detail"
    }
    Write-Host $line

    if ($Status -eq "FAIL") {
        $script:failures++
    } elseif ($Status -eq "WARN") {
        $script:warnings++
    }
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

function Test-CommandExists {
    param([string]$CommandName)
    return $null -ne (Get-Command $CommandName -ErrorAction SilentlyContinue)
}

function Test-DirectoryWritable {
    param([string]$Path)

    try {
        if (-not (Test-Path $Path)) {
            New-Item -ItemType Directory -Path $Path -Force -ErrorAction Stop | Out-Null
        }

        $testFile = Join-Path $Path ".readiness-write-test-$PID.tmp"
        Set-Content -Path $testFile -Value "ok" -NoNewline -ErrorAction Stop
        Remove-Item -Path $testFile -Force -ErrorAction Stop
        return $true
    } catch {
        return $false
    }
}

function Invoke-LoggedCommand {
    param(
        [string]$Name,
        [string]$FilePath,
        [string[]]$Arguments
    )

    Write-Host ""
    Write-Host "Running $Name..."
    & $FilePath @Arguments
    if ($LASTEXITCODE -eq 0) {
        Write-Result "PASS" $Name
    } else {
        Write-Result "FAIL" $Name "exit code $LASTEXITCODE"
    }
}

Set-Location $repoRoot

Write-Host "Screen Active Task Reminder - Windows Readiness Check"
Write-Host "Repo: $repoRoot"
Write-Host ""

if (Test-Path (Join-Path $repoRoot "gradlew.bat")) {
    Write-Result "PASS" "Gradle wrapper" "gradlew.bat found"
} else {
    Write-Result "FAIL" "Gradle wrapper" "gradlew.bat is missing"
}

if ((Test-Path $expectedGradleHome) -and (Test-DirectoryWritable $expectedGradleHome)) {
    Write-Result "PASS" "Portable Gradle cache" $expectedGradleHome
    if (-not $env:GRADLE_USER_HOME) {
        $env:GRADLE_USER_HOME = $expectedGradleHome
        Write-Result "INFO" "GRADLE_USER_HOME" "set for this script run"
    }
} else {
    Write-Result "WARN" "Portable Gradle cache" "missing or not writable; trying repo-local .gradle-home"
    if (Test-DirectoryWritable $fallbackGradleHome) {
        $env:GRADLE_USER_HOME = $fallbackGradleHome
        Write-Result "PASS" "Repo-local Gradle cache" $fallbackGradleHome
    } else {
        Write-Result "FAIL" "Gradle cache" "no writable Gradle cache is available"
    }
}

if (Test-Path $expectedJdk) {
    Write-Result "PASS" "Portable JDK" $expectedJdk
    if (-not $env:JAVA_HOME) {
        $env:JAVA_HOME = $expectedJdk
        Write-Result "INFO" "JAVA_HOME" "set for this script run"
    }
} elseif ($env:JAVA_HOME -and (Test-Path $env:JAVA_HOME)) {
    Write-Result "PASS" "JAVA_HOME" $env:JAVA_HOME
} elseif (Test-CommandExists "java") {
    Write-Result "WARN" "Java" "java exists on PATH, but portable JDK was not found"
} else {
    Write-Result "FAIL" "Java" "install/configure JDK 17; see docs\LOCAL_ANDROID_SETUP.md"
}

$sdkDirFromLocalProperties = Convert-LocalPropertiesPath (Get-LocalProperty "sdk.dir")
$androidSdk = $env:ANDROID_SDK_ROOT
if (-not $androidSdk) {
    $androidSdk = $sdkDirFromLocalProperties
}
if (-not $androidSdk -and (Test-Path $expectedAndroidSdk)) {
    $androidSdk = $expectedAndroidSdk
    $env:ANDROID_SDK_ROOT = $androidSdk
}

$androidSdkIsValid = $androidSdk -and (Test-Path $androidSdk)

if ($androidSdkIsValid) {
    Write-Result "PASS" "Android SDK" $androidSdk
    if (-not $env:ANDROID_AVD_HOME) {
        $env:ANDROID_AVD_HOME = Join-Path $env:USERPROFILE ".android\avd"
        Write-Result "INFO" "ANDROID_AVD_HOME" "set for this script run"
    }
} else {
    Write-Result "FAIL" "Android SDK" "expected $expectedAndroidSdk or sdk.dir in local.properties"
}

if ($androidSdkIsValid) {
    $platform36 = Join-Path $androidSdk "platforms\android-36"
    $adb = Join-Path $androidSdk "platform-tools\adb.exe"
    $emulator = Join-Path $androidSdk "emulator\emulator.exe"
    $avdmanager = Join-Path $androidSdk "cmdline-tools\latest\bin\avdmanager.bat"

    if (Test-Path $platform36) {
        Write-Result "PASS" "Android SDK Platform 36" $platform36
    } else {
        Write-Result "FAIL" "Android SDK Platform 36" "install Android SDK Platform 36"
    }

    if (Test-Path $adb) {
        Write-Result "PASS" "ADB" $adb
    } else {
        Write-Result "FAIL" "ADB" "platform-tools missing"
    }

    if (Test-Path $emulator) {
        Write-Result "PASS" "Android Emulator" $emulator
    } else {
        Write-Result "WARN" "Android Emulator" "missing; real phone testing can still work"
    }

    if (Test-Path $avdmanager) {
        $avdOutput = & $avdmanager list avd 2>&1
        if (($avdOutput | Out-String) -match [regex]::Escape($expectedAvdName)) {
            Write-Result "PASS" "Android virtual device" $expectedAvdName
        } else {
            Write-Result "WARN" "Android virtual device" "$expectedAvdName not listed"
        }
    } else {
        Write-Result "WARN" "AVD manager" "cmdline tools missing"
    }

    if (Test-Path $adb) {
        $deviceOutput = & $adb devices 2>&1
        $connectedDevices = @($deviceOutput | Select-Object -Skip 1 | Where-Object { $_ -match "\sdevice$" })
        if ($connectedDevices.Count -gt 0) {
            Write-Result "PASS" "Connected Android device" "emulator or phone is visible to ADB"
            $googlePackages = & $adb shell pm list packages 2>&1 | Where-Object {
                $_ -match "com\.google\.android\.gms|com\.google\.android\.gsf|com\.android\.vending"
            }
            if (@($googlePackages).Count -gt 0) {
                Write-Result "PASS" "Google account support" "Google services are present on the connected device"
            } else {
                Write-Result "WARN" "Google account support" "connected emulator/device has no Google services; use a Google Play emulator or real Android phone for Google sign-in"
            }
        } else {
            Write-Result "WARN" "Connected Android device" "none visible; start emulator or connect phone before install"
        }
    }
}

$webClientId = Get-LocalProperty "google.web.client.id"
if (-not $webClientId) {
    $webClientId = $env:GOOGLE_WEB_CLIENT_ID
}
if ($webClientId -and ($webClientId -match $webClientIdPattern)) {
    Write-Result "PASS" "Google Web Client ID" "configured"
} elseif ($webClientId) {
    Write-Result "FAIL" "Google Web Client ID" "value is present but does not look like a Web OAuth client ID"
} else {
    Write-Result "WARN" "Google Web Client ID" "run tools\setup_google_oauth.ps1 after creating the Web OAuth client"
}

if (-not $SkipGitHub) {
    if (Test-CommandExists "gh") {
        $ghOutput = & gh auth status -h github.com 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Result "PASS" "GitHub CLI auth" "logged in"
        } else {
            Write-Result "WARN" "GitHub CLI auth" "not ready; run gh auth login -h github.com"
            $ghOutput | ForEach-Object { Write-Host "      $_" }
        }
    } else {
        Write-Result "WARN" "GitHub CLI" "gh is not installed or not on PATH"
    }
}

if ($RunBuild) {
    Invoke-LoggedCommand "Debug APK build" (Join-Path $repoRoot "gradlew.bat") @("--no-daemon", "--max-workers=1", "--console=plain", "-Pkotlin.compiler.execution.strategy=in-process", ":app:assembleDebug")
}

if ($RunTests) {
    Invoke-LoggedCommand "JVM unit tests" (Join-Path $repoRoot "gradlew.bat") @("--no-daemon", "--max-workers=1", "--console=plain", "-Pkotlin.compiler.execution.strategy=in-process", ":app:testDebugUnitTest")
}

Write-Host ""
Write-Host "Summary: $script:failures failure(s), $script:warnings warning(s)"

if ($script:failures -gt 0) {
    Write-Host "Next: fix FAIL items first, then rerun this checker."
    exit 1
}

if ($script:warnings -gt 0) {
    Write-Host "Next: app development can continue, but review WARN items before beta testing."
    exit 0
}

Write-Host "Next: local Windows setup looks ready."
