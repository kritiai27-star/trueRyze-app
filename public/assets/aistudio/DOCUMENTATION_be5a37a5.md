# TRUE RYZE: Website Documentation

## 1. Overview
TRUE RYZE is a global digital solutions and business-growth platform (team in India, clients worldwide).
**Currently offered:** website development; website maintenance and support.
**Coming soon (never presented as live):** AI Business Automation, Digital Marketing, Lead Generation, Business Growth & Scaling,
Startup Resources, More Digital Solutions, SEO & Search Visibility, Brand Identity & Design, Analytics & Reporting.

The site is a premium single-page application built with plain HTML, CSS and JavaScript: a dark graphite design with liquid-silver surfaces,
cobalt-blue light, animated waves, a real-footage handshake, a luxury lamp sign-in scene and a three-role portal (client / team / dealer).

**Honest-by-design rule:** any feature that needs a backend (form delivery, sign-in, portal data, AI) is clearly labelled and never fakes success.

## 2. Quick start
| Goal | How |
|---|---|
| Open locally | Double-click `index.html` |
| Local server | `python3 -m http.server 8080` then open http://localhost:8080 |
| Deploy | Upload the whole folder to any static host (Netlify, Vercel, Cloudflare Pages, GitHub Pages, S3, nginx, Apache) |
| One-file build | `python3 tools/build-single-file.py` writes `dist/true-ryze-single.html` |
| Tests | `pip install playwright && playwright install chromium`, then `python3 tests/test_suite.py` |

Fonts (Sora, Inter) load from Google Fonts when online and fall back to system fonts offline. Nothing else is fetched from the network.

## 3. Project structure
```
index.html                  Page shell: header + nav, footer, canvases (#bg), overlays (#intro, #sweep, #lwash, #bar, #cur/#dot),
                            SEO meta, JSON-LD, <noscript> notice, links to CSS/JS
css/styles.css              Design tokens (:root), layout, components, effects, responsive and reduced-motion rules
js/config.js                Editable content: PLANS, CMP, JOBS, WHATSAPP, PROFILES
js/app.js                   All behaviour (section 4)
assets/logo-large.webp      Logo for the intro and the hero logo panel (black background, designed for dark surfaces)
assets/logo-small.webp      Logo for header, footer and the rolling row
assets/favicon.png          "TR" mark
assets/handshake-sheet.webp 79 transparent frames (640x328 each, 8 columns) keyed from the supplied green-screen video
dist/true-ryze-single.html  Self-contained single-file build
docs/                       This file and PORTAL_ARCHITECTURE.md
tools/                      build-single-file.py, make_handshake_sheet.py
tests/test_suite.py         Automated browser tests
```
Script order matters: `config.js` first, then `app.js` (classic scripts share one global scope, so `app.js` reads the constants defined in `config.js`).

## 4. Architecture (js/app.js)
* **Router:** hash-based (`#home`, `#pricing`, ...). `R` maps a route name to a function returning the page HTML. `draw()` renders a route, sets the document title and
  meta description (`DM` map), updates `aria-current` in the nav, moves focus to `<main>` and runs `init()`. `route()` wraps `draw()` with the page transition
  (content fades out 300 ms, swaps at 380 ms, a silver/cobalt light sweep runs 0.95 s; rapid clicks are queued, never stacked). `#client-portal` is an alias of `#portal`.
* **`init()`** runs after every render: scroll reveals (staggered, then released), card glint layers, sparkles, the pause-off-screen observer, magnetic buttons,
  logo tilt, forms, dashboard tools, lamp, portal wiring and footer profile icons.
* **Effects engine:** one `requestAnimationFrame` loop (about 30 fps, paused when the tab is hidden) paints the `#bg` canvas and calls `window.__drawHands`.
* **Helpers:** `$()`, `icons` (inline SVG), `I()`, `esc()`, `spk()` (sparkles), `form()` (form markup), `submit()` (form handling), `howAccess()`, `waBlock()`,
  `profilesBlock()` / `profilesCard()`, `lampSVG()`.
* **Globals you may meet:** `window._handsT0` (intro finished), `window._mode` (route index), `window._m` (pointer), `window._prole` (selected portal role).

## 5. Pages and routes
| Route | Page | Notes |
|---|---|---|
| `#home` | Home | Hero (headline, partner copy, rotating quotes, logo above the handshake), rolling brand row, challenge cards, two neon service boxes, ecosystem (9 cards), global delivery, FAQ, portal/careers/account cards, closing call-to-action |
| `#services` | Services | Two available services (neon) with detail links + 9 "Coming soon" cards |
| `#website-development`, `#website-maintenance` | Service details | Included, who it is for, what we need, what is not included, process |
| `#pricing` | Pricing | Launch / Grow / Scale (placeholders), comparison table, maintenance note; buttons pre-select the plan on the contact form |
| `#how-it-works` | How it works | 4-phase, 10-step flowchart |
| `#about` | About | Mission / Vision / Approach (silver boxes) + Startup WhatsApp Community block |
| `#careers` | Careers | Empty state (no invented vacancies) + general application form |
| `#contact` | **Let's Build Together** | Enquiry form, "What happens next", "Find us online" (Google Maps + social profiles) |
| `#account` | Sign in & register | Lamp scene, how client access works, sign-in form, **Request client access** form |
| `#portal` | Portal | Client Portal (top), Team Portal, Dealer Portal; role sign-in panels |
| `#portal-client`, `#portal-team`, `#portal-dealer` | Portal previews | Sample-data dashboards |
| `#legal` | Privacy & terms | Clearly marked draft; the owner must confirm real policies |

## 6. Navigation
* Desktop: logo, Home, Services, Pricing, How it works, About, Careers, Portal, Sign in and the call-to-action **Let's Build Together ->**
  (silver pill, animated cobalt border, breathing glow, shimmer sweep, arrow that slides on hover). It links to `#contact`; that page's content, form and workflow are unchanged.
  Name rationale: the page is where a visitor starts a business relationship; the phrase says partnership, building and shared growth in three words and fits every desktop width.
* Mobile (up to 1000 px): burger menu with Escape-to-close and `aria-expanded`; the call-to-action is the last, full-width item.
* Footer: company, services, get-started links, and icons for any profile links you configure. The first focusable element on every page is a "Skip to main content" link.

## 7. Design system
**Tokens** (CSS variables on `:root` in `css/styles.css`): `--cream #090B0F` (page), `--cream2 #10131A`, `--surface rgba(24,28,36,.62)` (glass), `--ink #EEF1F5`, `--ink2 #C3C9D3`,
`--muted #9BA3AF`, `--indigo2 / --coral #7FA6FF / #8FB1FF` (accent text), `--line` (borders), `--g1` graphite gradient, `--g2` silver-cobalt gradient, `--prism` silver/cobalt edge gradient.
Signature accents: electric cobalt `#2F6BFF`, ice `#9FD0FF`; champagne gold only on the lamp; neon blue-to-green on the contact "trust" borders.
**Type:** Sora (display; headings use `text-wrap: balance`) and Inter (body).
**Components:** glass cards (`.card`), neon blue cards (`.neon`), silver cards (`.silver`, `.silvx`), trust cards (`.trust`), tags, buttons (`.btn.p` silver primary, `.btn.s` outline),
forms (labels, `aria-invalid`, status messages), tables, stepper, chips.
**Motion language:** slow background waves; quick springy interactions; brief glints; entrance fades under 0.6 s; nothing loops on the hero.
**Reduced motion:** all animation is switched off; the handshake shows its still frame; the intro is skipped; page changes are instant.

## 8. Signature features
### 8.1 Opening intro
Black overlay: the logo reveals from the centre with a silver/cobalt light sweep and diamond glints (about 2.6 s). Click or any key skips it.
It plays once per full page load, never during in-site navigation. If JavaScript fails, a CSS timeout removes it after 7 s.

### 8.2 Animated background
`#bg` canvas: silver ribbons with cobalt edge light, a few sparkles, a slow travelling light beam and a soft pointer glow. About 30 fps,
lower resolution on phones, paused when the tab is hidden, static under reduced motion.

### 8.3 Handshake (homepage)
Real footage keyed from a green screen offline, stored as a transparent frame sheet and drawn on `<canvas id="hands">` (no `<video>` element, so it also works under strict hosting policies).
Exact behaviour:
1. **Site opens, page reloads, or you return to Home from another page:** one complete handshake (hands enter from both sides, clasp, shake once), then the hands hold the final frame, perfectly still.
2. **Pointer over or near the hands** (inside the canvas expanded by max(40 px, 8% of its width)): one complete handshake, then still again. Every new entry triggers one more handshake; resting inside does not loop.
3. **Touch:** tap the hands. **Keyboard:** focus the hands and press Enter or Space.
4. The hands never disappear. While a handshake plays, further triggers are ignored (no stacking).
5. On phones the hands sit below the first screen, so the opening handshake starts when they first scroll into view.
6. Reduced motion: still frame only.
Implementation: the `/*HANDS*/` block in `js/app.js`. The canvas redraws only when the frame, size or play state changes, so the resting state costs almost nothing.
Frame data `M={W:640,H:328,cols:8,ent:42,loop:37,fps:20}`: `ent` = entrance frames, `loop` = frames of one shake. To use another video run `tools/make_handshake_sheet.py`.

### 8.4 Rolling brand row
Continuous marquee. The two live services ("Website development", "Website maintenance") are neon pills with an animated cobalt border and sparkles; the logo separates items.

### 8.5 Card effects
Glass cards with a slow glint sweep; sparkle layers on the challenge and neon cards (positions re-randomise as each sparkle fades); neon blue service boxes; moving silver borders on the
ecosystem and About boxes; prismatic hover border; all animations pause while a card is off-screen.

### 8.6 Sign-in lamp scene (`#account`)
Your quote on the left, a vector luxury lamp in the centre (drum shade, gold rods, glowing LED spiral ribbon, shelves with plant, diffuser, frame and candle, round base), and a gold-and-chrome switch on the right.
Flipping the switch lights the whole lamp (spiral front and back, shelves, base ring, candle), washes the page in the chosen light, glows the form borders and the Sign in button,
floats dust motes and changes the quote to "Welcome in. Your journey starts here." A warm / daylight / cool slider (native range input) recolours everything.
The switch is a real `role="switch"` button (keyboard and screen-reader friendly).

### 8.7 Client access flow
Visitor explores -> Contact form (Let's Build Together) -> deal done -> **Request client access** (`#account`) -> TRUE RYZE approves -> unique Client ID (TR-C-######) ->
client creates a Portal account with that ID -> Client Portal monitor. Details in `docs/PORTAL_ARCHITECTURE.md`.

### 8.8 Portal
* **Landing:** Client Portal featured on top, Team and Dealer below, and an honest "not connected" notice.
* **Sign-in panels:** Client (sign in or create account with a Client ID; Google button shown as "needs setup"), Team (ID + password, no sign-up), Dealer (ID + password + access key).
  Inputs are validated (formats TR-C-######, TR-T-####, TR-D-###, password length and confirmation) and then an honest message explains that sign-in is not connected. No credentials are sent anywhere.
* **Previews (labelled sample data):** Client monitor (10-stage tracker, team ID only, loyalty ring toward 1 free month, bonds and tiers, growth chart, services and pricing, updates, AI assistant card),
  Team portal (works, Client IDs only, pay, empty-state toggle, delivery card), Dealer portal (requests with Approve that shows a sample ID, orders monitor with filter, team workload).
Business rule built into the UI: client and team never connect directly; work flows team -> TRUE RYZE -> client, linked only by IDs.

### 8.9 Other pages
Pricing comparison table (accessible; scrolls on small screens); How-it-works flowchart (phases with arrows and light-flow connectors; stacks vertically on mobile);
About WhatsApp community block (exclusive, verification-based, "Coming soon", empty QR slot, safe link slot); Contact "Find us online" (Google Maps + social profiles).

## 9. Forms and data handling
Forms: enquiry (`#f-enquiries`), job application (`#f-applications`), client access request (`#f-access`), sign-in (`#f-signin`) and the portal sign-ins.
* Validation: required fields, email and URL formats; the first invalid field receives focus and `aria-invalid`.
* Spam: hidden honeypot field `hp_field` (if filled, the submission is silently dropped).
* States: button disabled while sending, duplicate submits blocked, result announced in an `aria-live` region.
* **Delivery:** set `CONFIG.formEndpoint` (above `submit()` in `js/app.js`) to your endpoint, e.g. `https://formspree.io/f/xxxx` or your own API.
  The site POSTs JSON: `{ name, email, business, service, plan, budget, message, role, link, submittedAt, tz, kind }` where `kind` is `enquiries`, `applications` or `access`.
  A 2xx response shows success; any other result shows "Not sent".
* **Without an endpoint** (plain static hosting) every form shows "Not sent ... nothing was saved". The site never claims an enquiry arrived when it did not.
* Sign-in forms never send passwords anywhere; they validate and explain that sign-in is not connected.
* `dash()` (messages, support and assistant tools on the client preview) uses the Claude artifact runtime (`window.claude`); on normal hosting it shows a sign-in-required card.

## 10. Configuration and content editing
| What | Where | How |
|---|---|---|
| Pricing plans | `js/config.js` -> `PLANS` | Edit names, audience, `price` label ("Price to be confirmed"), inclusions. Put real prices only once confirmed. `hot:1` highlights a plan |
| Comparison table | `js/config.js` -> `CMP` | Rows `[feature, launch, grow, scale]` with `'y'` or `'n'` |
| Job vacancies | `js/config.js` -> `JOBS` | Add `{title,type,skills,duties}` objects. Empty = empty state + general application |
| WhatsApp community | `js/config.js` -> `WHATSAPP` | `link:'https://chat.whatsapp.com/...'`, optional `qrImage:'https://...'` or `data:image/...`. When set: tag becomes "Invitation only", button becomes **Join the community**, QR shows. Only https WhatsApp links are accepted |
| Google Maps + social profiles | `js/config.js` -> `PROFILES` | `googleMaps, linkedin, instagram, youtube, x` (https only). Empty ones show "Soon" on Contact and are hidden in the footer |
| Form delivery | `js/app.js` -> `CONFIG.formEndpoint` | Section 9 |
| Portal notice and sample data | `js/app.js` -> `PORTAL_STATUS`, `SAMPLE` | Replace `SAMPLE` with API data once a backend exists |
| Page titles and descriptions | `js/app.js` -> `DM` | One line per route |
| Colours, radius, fonts | `css/styles.css` -> `:root` | Change tokens; components follow |
| Page text | `js/app.js` | Search the route name, e.g. `pricing:()=>` |
| Legal text | `js/app.js` -> `legal:()=>` | Replace the draft with approved text |
| Logo | `assets/logo-*.webp`, `favicon.png` | Keep the black background (the logo is designed for dark surfaces) |

## 11. Accessibility (target WCAG 2.2 AA; not formally audited)
Skip link; landmarks (`header`, `nav`, `main`, `footer`); focus moves to `<main>` on every route change; `aria-current` on the active nav item; one `<h1>` per page;
visible 3 px focus rings; labelled forms with `aria-required`, `aria-invalid` and `aria-live` status messages; `role="switch"` lamp switch with keyboard support;
the handshake canvas is a labelled button (Enter/Space); primary controls are at least 44 px high, every control at least 24 px; decorative layers are `aria-hidden` and never intercept the pointer;
text contrast on dark surfaces checked programmatically (approximate); full `prefers-reduced-motion` support; the native cursor is never hidden.
Not done: a formal audit with screen readers on real devices.

## 12. Performance
* No frameworks and no runtime dependencies; fonts are the only external request.
* One animation loop (about 30 fps, paused when hidden); lower canvas resolution on phones.
* The handshake canvas redraws only when something changes, so the resting state is nearly free. Its frame sheet is decoded once (about 67 MB of decoded image memory while Home is open).
* Off-screen card animations are paused; blur and shadow effects are limited.
* Sizes: `handshake-sheet.webp` about 0.7 MB (largest), `logo-large.webp` 41 KB, `logo-small.webp` 15 KB, CSS about 51 KB, JS about 75 KB.
* Not measured: Lighthouse scores and real-device frame rates. Measure on your target devices before launch.

## 13. Browser support and fallbacks
Targets current Chrome, Edge, Firefox and Safari. Features used: CSS custom properties, `@property` (animated borders; degrade to static), `backdrop-filter` (degrades to solid),
`aspect-ratio`, canvas 2D, IntersectionObserver, `color-mix`, `text-wrap` (progressive). Tested in Chromium only; test Safari and Firefox before launch.
With JavaScript disabled, a short notice explains that the site needs it.

## 14. Security and privacy notes
* No secrets in the front end. Never put API keys or credentials in `config.js` or `app.js`.
* Text from users is escaped before it is shown; only validated https links become links (`rel="noopener noreferrer"`).
* Client-side validation is a convenience. Real validation, rate limiting, authentication and authorisation must run on the server.
* Portal role separation (clients see only their data, team sees Client IDs only, dealer sees everything) must be enforced server-side with row-level security. Hiding buttons is not security.
* The privacy policy and terms are drafts. The owner must confirm the legal entity, data collected, retention, storage location and any cookies or analytics.

## 15. Testing and QA
`tests/test_suite.py` (Playwright, Chromium). Sections: A routes/layout/errors at 8 widths (320, 360, 390, 768, 1024, 1366, 1440, 1920); B navigation call-to-action; C handshake rules (desktop and mobile);
D forms (validation, honest failure, endpoint success and failure paths); E lamp scene; F portal; G audit (duplicate ids, alt text, control names, tap targets, links, `rel`); H reduced motion.
Run all with `python3 tests/test_suite.py`, or one section, e.g. `python3 tests/test_suite.py file:///path/index.html C`.
Results for this release are in section 20.

## 16. Known limitations and what needs a backend
| Item | Status |
|---|---|
| Form delivery | Works once `CONFIG.formEndpoint` is set; otherwise an honest "Not sent" |
| Client / team / dealer sign-in, Client ID issuing, approvals, loyalty redemption, live monitor data | **Not connected.** UI complete; previews use sample data. Plan in `docs/PORTAL_ARCHITECTURE.md` |
| Google sign-in | Button shown as "needs setup" (requires OAuth configuration) |
| AI assistant | Needs a server-side AI key and per-user data; shown as a labelled card |
| File uploads (resume, deliveries) | Need storage; the application form asks for a link instead |
| Final prices, contact details, legal text, WhatsApp / Maps / social links | The owner supplies these |
| SEO | Pages are generated by JavaScript. Titles and descriptions update per route, but crawlers and social previews may see little. Add pre-rendering or server rendering if search traffic matters |
| Real-device, Safari and Firefox testing | Not performed |

## 17. Maintenance guide
* **Add a page:** write a function returning the HTML, add it to the `R` map in `app.js`, add a `DM` description, link to `#your-route`, and (for the nav) add a `data-r` link in `index.html`.
* **Add an ecosystem card:** add an icon to `icons` and a row to the `soon` array in `app.js`.
* **Change the nav call-to-action text:** edit the `.navcta` link in `index.html` and the matching footer link.
* **Swap the handshake video:** run `tools/make_handshake_sheet.py`, replace `assets/handshake-sheet.webp`, paste the printed META over `M={...}` in `app.js`.
* **Restyle:** edit tokens in `css/styles.css` first; avoid hard-coded colours.
* **After any change:** run `tests/test_suite.py` and look at 390 px and 1440 px.

## 18. Roadmap suggestions
Connect a backend (for example Supabase) for forms, authentication and portal data; add pre-rendered pages for SEO; real photography or illustration where useful; analytics with consent;
multi-currency pricing once prices are final; a formal accessibility audit; Lighthouse tuning on real devices.

## 19. Release notes (this master release)
* Handshake simplified to the real footage only; options removed; strict once-per-trigger mechanism with pointer-near, tap and keyboard triggers; hologram code and data removed (about 0.6 MB lighter).
* "Contact us" renamed **Let's Build Together** with a premium animated call-to-action (page content and workflow unchanged); footer and in-page references aligned.
* Closing homepage call-to-action redesigned as a neon panel; FAQ aligned; outdated client-portal wording updated to Portal; headings use balanced wrapping.
* Full project export: HTML, CSS, config, app code, assets, single-file build, tools, tests and this documentation.
