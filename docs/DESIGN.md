# Recall — Design decisions

A short, auditable record of the visual-design decisions for Recall and *why*
they were made. The guiding principle is **follow Android system design**: look
and behave like a first-class, modern Material 3 app that respects platform
conventions and user settings.

## Decision log

### D1 — Color strategy: hybrid (Material You + brand fallback)

**Decision:** Full Material 3 color scheme generated from a teal **seed**
(`#0F766E`). **Dynamic color (Material You) is supported and ON by default** on
Android 12+; the generated teal brand scheme is the fallback for older devices
and for branded surfaces.

**Why:** This matches Google's own reference app (Now in Android) — a real
generated scheme *plus* dynamic color layered on top. It keeps the app native to
the OS (Material You) while retaining a recognizable identity. Pure platform-only
would discard the approved teal direction; pure brand-only would hide the single
most Android-native capability worth demonstrating.

Rejected:
- **A — Platform-first only:** forgettable, throws away brand.
- **B — Brand-first, dynamic color off:** ignores Material You.

### D2 — Typography: platform default (system font)

**Decision:** Use the platform default Material 3 type scale (`Typography()`),
i.e. the system font (Roboto). No bundled custom fonts.

**Why:** "Follow Android system design" and a bundled custom font are in tension.
The system font honors the user's font-size / display accessibility settings,
adds zero asset weight, and is what Now in Android ships. This overrides the
Stitch mockups' Plus Jakarta Sans + Inter choice, which also conflicted with the
`stitch-design-taste` skill (Inter banned).

### D3 — Scheme generation: Material Theme Builder export (committed)

**Decision:** The brand scheme is generated once with Material Theme Builder from
the teal seed and committed as explicit `Color(0xFF…)` values in
`core/designsystem/.../theme/Color.kt`.

**Why:** A committed palette is auditable and reviewable (every one of the ~30
roles × light/dark is visible), aligns with the project's "decision note" proof
bar, and matches the Google reference workflow. Dynamic color still overrides it
at runtime, so there is no behavioral downside — only added transparency versus a
runtime-generated scheme.

## Where it lives

| Concern | Location |
|---|---|
| Color roles (light/dark) | `core/designsystem/src/main/kotlin/com/mabrouk/recall/core/designsystem/theme/Color.kt` |
| Theme + dynamic color logic | `core/designsystem/.../theme/Theme.kt` (`RecallTheme`) |
| Type scale | `core/designsystem/.../theme/Type.kt` (`RecallTypography`) |
| Seed color | `#0F766E` (teal) |

## References

- [Material 3 — Color system](https://m3.material.io/styles/color/system/overview)
- [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)
- [Dynamic color (Material You)](https://developer.android.com/develop/ui/compose/designsystems/material3#dynamic-color-schemes)
- [Now in Android — reference app](https://github.com/android/nowinandroid)
- [Compose Material 3 theming](https://developer.android.com/develop/ui/compose/designsystems/material3)
