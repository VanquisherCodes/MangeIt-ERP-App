# Stitch Export To Android Mapping

Use this checklist when converting design exports into Android code.

1. Put raw Stitch output in `ui_exports/stitch/raw/` (do not edit original files).
2. Put reusable icons/images/tokens in `ui_exports/stitch/reference/`.
3. Put converted draft XML files in `ui_exports/stitch/converted_android_xml/`.
4. Move finalized XML screens into `app/src/main/res/layout/` with naming conventions:
   - `activity_*` for activities
   - `fragment_*` for fragments
5. Keep one fragment per screen section and host them from `AuthActivity` or `MainActivity`.
6. Document any deviation from Stitch designs here before implementation.

Android layout note:
- Android resource folders do not support nested screen-type subfolders inside `res/layout`.
- Separation is maintained by strong naming prefixes and fragment package structure.
