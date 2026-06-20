# Screen Activity ADR

## Backlog Item

Planning slice for `ADR-0005`. This is not the future `SCR-001` implementation spike.

## Branch

`codex/scr-001-screen-activity-adr`

## Summary

Captured the screen-activity detection decision in `ADR-0005`. V1 remains notification-first and elapsed-review-interval based. Real cross-app screen-activity detection is now defined as a later optional Usage Access mode, not a first-run requirement.

No Android permission, service, manifest, or runtime code was added in this slice.

## Files Changed

- `README.md`
- `docs/PROJECT_BRIEF.md`
- `docs/PRD.md`
- `docs/ARCHITECTURE.md`
- `docs/BACKLOG.md`
- `docs/QA_PLAN.md`
- `docs/PO_WINDOWS_TEST_GUIDE.md`
- `docs/RISKS.md`
- `docs/ROADMAP.md`
- `docs/adr/0005-optional-usage-access-for-screen-activity.md`
- `docs/handoffs/2026-06-20-SCR-001-screen-activity-adr.md`

## Verification

Commands run:

```powershell
git diff --check
```

Result:

- Passed.

## Open Questions

- Product approval is still required before implementing optional Usage Access.
- Physical-device validation will be needed before treating Usage Access as reliable enough for beta.

## Known Limitations

- V1 still does not detect real cross-app screen activity.
- WorkManager reminder timing remains best-effort under Android background limits.

## Recommended Next Step

Implement `SCR-001: Usage Access Feasibility Spike` as a narrow technical spike, then decide whether `SCR-002: Optional Screen Activity Mode` should be built.
