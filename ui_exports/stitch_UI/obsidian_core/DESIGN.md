# Design System Specification

## 1. Overview & Creative North Star

### The Creative North Star: "The Architectural Glasshouse"
This design system is built upon the philosophy of **The Architectural Glasshouse**. It rejects the cluttered, utility-first aesthetics of traditional ERP software in favor of a high-end, editorial experience. Inspired by the precision of premium software engineering, it prioritizes structural clarity, intentional negative space, and a sophisticated interplay of light and depth. 

The system breaks away from "standard" Android templates through:
*   **Intentional Asymmetry:** Using the `Spacing Scale` to create rhythmic layouts that guide the eye naturally rather than forcing content into rigid boxes.
*   **Atmospheric Depth:** Leveraging Material Design tiers not just for color, but for a sense of physical layering.
*   **Currency as an Anchor:** The Euro (€) symbol is treated with typographical reverence, using weight and scale to denote financial importance within the ERP ecosystem.

---

## 2. Colors

The color palette is a "Deep Slate" ecosystem designed for high-contrast legibility and professional prestige.

### The Palette
*   **Primary (`#4edea3`):** A vibrant emerald used sparingly for high-intent actions.
*   **Background (`#0c1324`):** A deep, midnight blue-black that serves as the canvas.
*   **Tonal Accents:** `tertiary` (#7bd0ff) is used for secondary data points like `Event` dates or `Announcement` timestamps.

### The "No-Line" Rule
**Explicit Instruction:** 1px solid borders are strictly prohibited for sectioning. Boundaries must be defined through background color shifts. To separate a `Task` list from an `Announcement` feed, transition from `surface-container-low` to `surface-container`.

### Surface Hierarchy & Nesting
Treat the UI as a series of stacked sheets.
*   **The Foundation:** `surface` (#0c1324)
*   **Secondary Content Areas:** `surface-container-low` (#151b2d)
*   **Interactive Cards:** `surface-container` (#191f31)
*   **Elevated Details (Modals/Overlays):** `surface-container-highest` (#2e3447)

### The "Glass & Gradient" Rule
For premium elements like the `Admin` dashboard summary or `Request` status badges, use Glassmorphism. Apply a 20% opacity to `surface-tint` with a 16px backdrop blur to create a "frosted glass" look that allows underlying data to bleed through subtly.

---

## 3. Typography

The system utilizes a dual-font strategy to balance architectural precision with editorial readability.

*   **Display & Headlines (Manrope):** Used for large data visualizations and section titles. Manrope’s geometric clarity provides a "Senior Engineer" feel—modern, clean, and uncompromising.
    *   *Headline-LG:* 2rem, tight tracking (-0.02em), used for `User` profile names.
*   **Body & Labels (Inter):** Used for the data-dense aspects of the ERP (e.g., `Task` descriptions, `ApiService` logs). Inter provides maximum legibility at small scales.
    *   *Body-MD:* 0.875rem, the workhorse for `Request` details.
*   **The Currency Exception:** When displaying the Euro (€) symbol, use `headline-sm` with a `primary` color token to ensure financial metrics are the most visible elements on the screen.

---

## 4. Elevation & Depth

### The Layering Principle
Depth is achieved through "Tonal Stacking." Place a `surface-container-lowest` card on a `surface-container-low` section to create a soft, natural lift.

### Ambient Shadows
Shadows must be invisible until noticed.
*   **Blur:** 20px - 40px
*   **Opacity:** 4% - 8%
*   **Color:** Use a tinted version of `on-surface` (#dce1fb) to simulate real-world light refraction.

### The "Ghost Border" Fallback
If a visual boundary is required for accessibility in `Input Fields`, use the `outline-variant` (#3c4a42) at **20% opacity**. Never use 100% opaque borders.

---

## 5. Components

### Buttons
*   **Primary:** Background: `primary` (#4edea3) | Label: `on-primary` (#003824). Roundedness: `lg` (0.5rem). Use for high-level actions like `submitRequest()` or `registerUser()`.
*   **Tertiary:** No background. `primary` text. Use for `viewProfile()` or `cancel`.

### Input Fields
*   **Style:** `surface-container-high` background with a `Ghost Border`. 
*   **States:** On focus, the border transitions to 100% `primary`. 
*   **Relationships:** `AuthService` login inputs should use `title-md` for the text to emphasize security and importance.

### Cards (The ERP Core)
Cards represent objects like `Task`, `Event`, and `Announcement`.
*   **Rule:** Forbid divider lines. Use `spacing-4` (0.9rem) of vertical white space to separate the `Task` title from its description.
*   **Interaction:** On tap, the card background shifts from `surface-container` to `surface-container-highest`.

### Status Chips
*   **Success (Completed Task):** `primary-container` (#10b981) background with `on-primary-container` text.
*   **Warning (Pending Request):** `tertiary-container` (#19aee8) with `on-tertiary-container` text.

---

## 6. Do's and Don'ts

### Do
*   **Do** use the Euro symbol (€) as a prefix for all `Request` values, styled in `title-lg` weight.
*   **Do** allow for ample white space (using `spacing-8` or `10`) between unrelated data blocks like `Admin` reports and `StandardUser` tasks.
*   **Do** use `Glassmorphism` for the `NetworkMonitor` status bar to ensure it feels like a floating, system-level utility.

### Don't
*   **Don't** use 1px dividers to separate `List` items. Use a subtle background toggle or 0.4rem spacing instead.
*   **Don't** use pure white (#FFFFFF) for text. Always use `on-surface` (#dce1fb) to reduce eye strain and maintain the "Deep Slate" aesthetic.
*   **Don't** use sharp corners. Everything must adhere to the `Roundedness Scale` (minimum `DEFAULT` 0.25rem, ideally `lg` 0.5rem) to maintain the premium, Apple-inspired softness.

### Relationship Mapping for Designers
*   **AuthService / ApiService:** These are system-level "behind-the-scenes" actions. Use subtle `label-sm` loading states and glassmorphic progress indicators.
*   **Admin vs. StandardUser:** Use `surface-bright` (#33394c) headers for Admin views to provide an immediate "Elevated Access" visual cue compared to the standard `surface` background for regular users.