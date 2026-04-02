# Stitch UI Adaptation (stitch_UI -> Android)

Source checked: `ui_exports/stitch_UI/*` (`code.html` + `screen.png` for each module).

## Mapping Used

- `stitch_UI/login` -> `app/src/main/res/layout/fragment_login.xml`
- `stitch_UI/registration` -> `app/src/main/res/layout/fragment_register.xml`
- `stitch_UI/dashboard` -> `app/src/main/res/layout/fragment_dashboard_admin.xml`, `fragment_dashboard_user.xml`
- `stitch_UI/tasks` -> `app/src/main/res/layout/fragment_tasks.xml`
- `stitch_UI/events` -> `app/src/main/res/layout/fragment_events.xml`
- `stitch_UI/members` -> `app/src/main/res/layout/fragment_profile.xml`
- `stitch_UI/finances` -> `app/src/main/res/layout/fragment_requests.xml`

## Design Tokens Applied

- Colors and surfaces from `obsidian_core/DESIGN.md` moved into:
  - `app/src/main/res/values/colors.xml`
  - `app/src/main/res/values/themes.xml`
  - `app/src/main/res/values-night/themes.xml`

- Reusable Stitch-like UI shells converted to drawables:
  - `bg_screen_gradient.xml`
  - `bg_surface_card*.xml`
  - `bg_input_field.xml`
  - `bg_button_primary.xml`
  - `bg_button_secondary.xml`
  - `bg_chip_*.xml`
  - `bg_glass_panel.xml`

## Notes

- Web-only effects (blur-heavy glassmorphism, remote image avatars, Tailwind utility classes) were replaced with Android-native XML equivalents.
- Layouts are still placeholders structurally (for data binding with Retrofit/repositories), but now match the Stitch visual direction and are ready for wiring to adapters and API data.
