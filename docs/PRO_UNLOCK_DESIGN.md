# Pro Unlock Design

Backlog item: `MON-001`

Status: Design complete. Billing implementation is intentionally deferred.

## Product Decision

Do not add Google ads in V1.

Reason: the app's promise is to reduce distraction and build user trust. Ads would add visual noise, require additional privacy disclosures, and make the first release harder to review.

Preferred monetization path:

1. Launch a useful free V1.
2. Validate that people return to the app.
3. Add a one-time Pro unlock only after real users ask for advanced controls.

Avoid paid subscriptions at first. A subscription adds support, cancellation, refund, and value-pressure expectations before the product has proven daily usefulness.

## Free Vs Pro Matrix

| Area | Free V1 | Future Pro |
| --- | --- | --- |
| Google Tasks read-only sync | Included | Included |
| Today task review | Included | Included |
| Manual sync | Included | Included |
| Watched task-list selection | Included | Included |
| Reminder interval | Basic presets | More custom intervals |
| Snooze duration | Basic presets | More custom snooze controls |
| Quiet hours | Included | Multiple quiet-hour schedules |
| Notification recovery | Included | Included |
| Screen-activity diagnostics | Included while experimental | Advanced activity-trigger rules if approved |
| Reminder history | Not included | Optional local-only reminder history |
| Theme | System/light/dark | Accent color options |
| Ads | Not included | Not included |

## Upgrade Screen Mock Copy

Title: `Unlock Pro controls`

Body: `Keep the simple reminder flow, and add more control when you need it.`

Feature bullets:

- Custom reminder intervals.
- Multiple quiet-hour schedules.
- Advanced snooze choices.
- Optional local reminder history.
- Future screen-activity rules if approved.

Primary button: `Unlock Pro`

Secondary button: `Not now`

Footer: `The free app keeps core Google Tasks reminders. Pro adds deeper controls, not ads.`

## Billing Guardrails

Do not implement billing until product owner approves a paid feature scope.

When billing is approved:

- Use Google Play Billing Library.
- Implement purchase restore before launch.
- Keep the free app useful.
- Do not add ads as a fallback.
- Update privacy policy, Play Data safety, store listing, and QA plan.
- Test purchases only on Play internal testing tracks.

## Initial Price Hypothesis

Start with a one-time unlock, not subscription.

Suggested first experiment:

- US: $2.99 to $4.99 one-time unlock.
- India: local equivalent with Play regional pricing.

Do not finalize pricing until internal testers confirm which Pro features they actually value.

## Success Criteria Before Billing Work

Billing work should wait until at least one of these is true:

- Internal testers use the free app repeatedly for a week.
- Testers ask for advanced reminder controls.
- The app has clear retention evidence from manual feedback.
- The core Google Tasks sync and reminder flow is stable.

## Official Reference

- Google Play Billing Library integration: https://developer.android.com/google/play/billing/integrate
