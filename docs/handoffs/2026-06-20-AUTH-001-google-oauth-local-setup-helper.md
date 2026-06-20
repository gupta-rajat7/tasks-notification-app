# AUTH-001: Google OAuth Local Setup Helper

Date: 2026-06-20
Branch: `codex/google-auth-setup-helper`

## Summary

Completed the repo-side Google OAuth setup flow by adding a local helper script and stricter Web Client ID validation.

## What Changed

- Added `tools/setup_google_oauth.ps1`.
- Tightened `GoogleSignInConfig.isConfigured` so malformed IDs do not enable sign-in.
- Updated the Windows readiness checker to validate the Web Client ID format.
- Updated PO and technical Google sign-in setup docs to use the helper script.
- Added backlog item `AUTH-001`.

## How To Use

Print the debug SHA-1 for the Google Cloud Android OAuth client:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -PrintDebugSha1
```

Save the Web OAuth Client ID locally:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com'
```

## Remaining Owner-Controlled Step

Codex cannot create the Google Cloud OAuth clients without access to the product owner's Google Cloud project. The app will continue showing the setup-needed message until a real Web Client ID is added locally.

## Verification

- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -WebClientId '1234567890-example.apps.googleusercontent.com' -ValidateOnly`: passed.
- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\setup_google_oauth.ps1 -PrintDebugSha1`: passed and printed the local debug SHA-1.
- Invalid Web Client ID smoke test rejected `bad-client-id` as expected.
- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -SkipGitHub`: passed with the expected Google Web Client ID warning.
- `GoogleSignInConfigTest`: 4 tests passed, including malformed Web Client ID rejection.
- `powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\check_windows_readiness.ps1 -SkipGitHub -RunBuild -RunTests`: passed.
  - `:app:assembleDebug`: passed.
  - `:app:testDebugUnitTest`: passed.
