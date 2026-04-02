# ManageIt Java Package Guide

This package tree is organized by responsibility and kept intentionally lightweight:

- `activities/`: top-level hosts (`SplashActivity`, `AuthActivity`, `MainActivity`).
- `fragments/`: feature and flow UI pieces split by domain (`auth`, `dashboard`, `modules`).
- `models/`: plain Java models mapped from API/database payloads.
- `network/`: API client setup and connectivity utilities.
- `repository/`: data access layer for remote/local source composition.
- `managers/`: app-wide state/control classes (session, offline mode, role access).
- `navigation/`: role-based navigation decisions.
- `adapters/`: adapter classes for list/grid UI components.
- `utils/`: constants and small shared helper abstractions.
- `offline/blackjack/`: isolated fallback module for offline gameplay.

Naming conventions:
- Activity layouts: `activity_*`
- Fragment layouts: `fragment_*`
- Module fragments: `fragment_<module>`
- Dashboard fragments: `fragment_dashboard_<role>`
