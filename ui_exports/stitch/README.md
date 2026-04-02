# Stitch UI Exports

Purpose: keep design-source files versioned and traceable without polluting Android runtime resources.

- `raw/`: direct exports from Stitch (never edit).
- `reference/`: curated visual references (icons, spacing/tokens, annotated screenshots).
- `converted_android_xml/`: work-in-progress XML conversions before promotion to `app/src/main/res/layout/`.

Promotion flow:
1. Copy from `raw` to `converted_android_xml`.
2. Convert to Android XML and review against `docs/ui/STITCH_TO_ANDROID_MAPPING.md`.
3. Move only final XML to `app/src/main/res/layout/`.

Current status:
- `ui_exports/stitch_UI` exports were adapted and synced into `converted_android_xml` and `app/src/main/res/layout/`.
