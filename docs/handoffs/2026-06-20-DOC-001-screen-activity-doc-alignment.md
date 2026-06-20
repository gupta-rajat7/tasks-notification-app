# DOC-001: Screen Activity Documentation Alignment

Date: 2026-06-20
Branch: `codex-open-readiness-docs-monetization-oauth`

## Summary

Aligned core product, architecture, roadmap, and ADR documentation with the implemented `SCR-002` optional screen-activity reminder mode.

## Product Decision Captured

- Standard reminders remain available without advanced permissions.
- Usage Access is not part of first-run onboarding.
- Screen-activity reminders are Settings-only, off by default, and require the user to grant Android Usage Access.
- The app uses derived recent-activity state for reminders and does not persist raw per-app usage history.
- Physical Android phone validation is still required before external beta.

## Files Updated

- `README.md`
- `docs/PROJECT_BRIEF.md`
- `docs/PRD.md`
- `docs/ARCHITECTURE.md`
- `docs/ROADMAP.md`
- `docs/SCREEN_ACTIVITY_FEASIBILITY.md`
- `docs/adr/0005-optional-usage-access-for-screen-activity.md`
- `docs/PROJECT_STATUS.md`

## Verification

- `git diff --check`: passed

## GitHub Sync

GitHub push and PR creation remain blocked in this Codex session until Git/GitHub credentials are refreshed.
