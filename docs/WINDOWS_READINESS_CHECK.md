# Windows Readiness Check

This guide gives a nontechnical product owner one command to check whether the Windows machine is ready to build, run, test, and sync the app project.

Use this before asking Codex to run a new development slice or before testing the app on the emulator.

## Step 1: Open PowerShell

Copy and paste:

```powershell
cd 'C:\Users\Tanu Gupta\Documents\Tasks Notification App'
```

## Step 2: Run The Quick Check

Copy and paste:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1
```

The checker prints:

- `PASS`: ready.
- `WARN`: not blocking development, but needs attention before full testing or release.
- `FAIL`: must be fixed before that part can work.

## Step 3: Run Build And Unit Tests

Use this when you want a stronger check:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -RunBuild -RunTests
```

This can take several minutes.

## What The Checker Looks For

- Gradle wrapper in the project.
- Portable Gradle cache, with repo-local fallback if needed.
- JDK 17.
- Android SDK.
- Android SDK Platform 36.
- ADB.
- Android emulator tools.
- Local emulator named `TaskReminder_API35`.
- Connected emulator or phone.
- Google account support on the connected Android device.
- Google Web Client ID for real Google sign-in.
- GitHub CLI authentication.

## Common Results

### Google Sign-In Warning

If you see:

```text
[WARN] Google Web Client ID
```

the app can still open, but real Google sign-in is not ready. Use `docs\PO_GOOGLE_OAUTH_SETUP_GUIDE.md`.

After the Google Cloud Web OAuth client exists, run:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'
```

### GitHub Auth Warning

If you see:

```text
[WARN] GitHub CLI auth
```

Codex may be able to commit locally, but it cannot push to GitHub. Run:

```powershell
gh auth logout -h github.com -u gupta-rajat7
gh auth login -h github.com
```

Then ask Codex to retry pushing and creating the pull request.

### No Connected Android Device Warning

If you see:

```text
[WARN] Connected Android device
```

start the emulator or connect a real Android phone before installing the app.

Use `docs\WINDOWS_RUN_APP_GUIDE.md` for the full run steps.

### Google Account Support Warning

If you see:

```text
[WARN] Google account support
```

the connected emulator or phone can run the app, but cannot complete Google sign-in. Use a real Android phone, or create/run a Google Play or Google APIs emulator image, before testing Google sign-in.

### Portable Gradle Cache Warning

If you see:

```text
[WARN] Portable Gradle cache
```

the checker will try the project-local `.gradle-home` fallback. That folder is ignored by Git and is safe for temporary build files.
