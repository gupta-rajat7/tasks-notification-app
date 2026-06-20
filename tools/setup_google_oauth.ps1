param(
    [string]$WebClientId = "",
    [switch]$PrintDebugSha1,
    [switch]$ValidateOnly
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$devRoot = "C:\tmp\task-reminder-dev"
$jdkHome = Join-Path $devRoot "jdk\jdk-17.0.19+10"
$androidSdk = Join-Path $devRoot "android-sdk"
$localPropertiesPath = Join-Path $repoRoot "local.properties"
$webClientIdPattern = "^[A-Za-z0-9._-]+\.apps\.googleusercontent\.com$"

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "== $Message =="
}

function Convert-LocalPropertiesPath {
    param([string]$Value)

    if (-not $Value) {
        return $null
    }

    return $Value.Replace("\:", ":").Replace("\\", "\")
}

function Convert-ToLocalPropertiesPath {
    param([string]$Value)

    return $Value.Replace("\", "\\").Replace(":", "\:")
}

function Get-LocalProperty {
    param([string]$Name)

    if (-not (Test-Path $localPropertiesPath)) {
        return $null
    }

    $match = Get-Content $localPropertiesPath | Where-Object {
        $_ -match "^\s*$([regex]::Escape($Name))\s*="
    } | Select-Object -First 1

    if (-not $match) {
        return $null
    }

    return ($match -replace "^\s*$([regex]::Escape($Name))\s*=\s*", "").Trim()
}

function Set-LocalProperty {
    param(
        [string]$Name,
        [string]$Value
    )

    $lines = @()
    if (Test-Path $localPropertiesPath) {
        $lines = @(Get-Content $localPropertiesPath)
    }

    $propertyLine = "$Name=$Value"
    $wasUpdated = $false
    $updatedLines = foreach ($line in $lines) {
        if ($line -match "^\s*$([regex]::Escape($Name))\s*=") {
            $wasUpdated = $true
            $propertyLine
        } else {
            $line
        }
    }

    if (-not $wasUpdated) {
        $updatedLines += $propertyLine
    }

    Set-Content -Path $localPropertiesPath -Value $updatedLines -Encoding UTF8
}

function Get-DebugSha1 {
    $keytool = Join-Path $jdkHome "bin\keytool.exe"
    $debugKeystore = Join-Path $env:USERPROFILE ".android\debug.keystore"

    if (-not (Test-Path $keytool)) {
        throw "JDK keytool was not found at $keytool"
    }

    if (-not (Test-Path $debugKeystore)) {
        throw "Android debug keystore was not found at $debugKeystore. Build the app once, then rerun this command."
    }

    $output = & $keytool -list -v -alias androiddebugkey -keystore $debugKeystore -storepass android -keypass android 2>&1
    $sha1Line = $output | Where-Object { $_ -match "SHA1:" } | Select-Object -First 1
    if (-not $sha1Line) {
        throw "Could not read SHA1 from Android debug keystore."
    }

    return ($sha1Line -replace "^\s*SHA1:\s*", "").Trim()
}

Set-Location $repoRoot

Write-Host "Screen Active Task Reminder - Google OAuth Setup"
Write-Host "Repo: $repoRoot"

if ($PrintDebugSha1) {
    Write-Step "Debug SHA-1"
    Write-Host (Get-DebugSha1)
}

$existingSdkDir = Convert-LocalPropertiesPath (Get-LocalProperty "sdk.dir")
if (-not $existingSdkDir) {
    if (Test-Path $androidSdk) {
        Set-LocalProperty "sdk.dir" (Convert-ToLocalPropertiesPath $androidSdk)
        Write-Host "Added sdk.dir to local.properties."
    } else {
        Write-Host "WARNING: sdk.dir is not set and $androidSdk was not found."
    }
}

if (-not $WebClientId) {
    $WebClientId = $env:GOOGLE_WEB_CLIENT_ID
}

if (-not $WebClientId) {
    $existingWebClientId = Get-LocalProperty "google.web.client.id"
    if ($existingWebClientId) {
        $WebClientId = $existingWebClientId
    }
}

if (-not $WebClientId) {
    Write-Step "Missing Web Client ID"
    if ($PrintDebugSha1) {
        Write-Host "Debug SHA-1 was printed above."
        Write-Host "After creating the Web OAuth client, rerun:"
        Write-Host "powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'"
        exit 0
    }

    Write-Host "Create a Web OAuth client in Google Cloud, then rerun:"
    Write-Host "powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'"
    Write-Host ""
    Write-Host "Use docs\PO_GOOGLE_OAUTH_SETUP_GUIDE.md for Google Cloud steps."
    exit 1
}

if ($WebClientId -notmatch $webClientIdPattern) {
    throw "Web Client ID must end with .apps.googleusercontent.com. Value was: $WebClientId"
}

Write-Step "Web Client ID"
Write-Host "Format looks valid."

if ($ValidateOnly) {
    Write-Host "ValidateOnly was set; local.properties was not changed."
    exit 0
}

Set-LocalProperty "google.web.client.id" $WebClientId.Trim()
Write-Host "Saved google.web.client.id to local.properties."
Write-Host ""
Write-Host "Next: rebuild and reinstall the app:"
Write-Host "powershell -ExecutionPolicy Bypass -File .\tools\run_app_windows.ps1 -StartEmulator"
