# TRUE RYZE website

Premium dark-graphite "liquid silver and cobalt" website for TRUE RYZE, a global digital solutions and business-growth platform.
Static front end: plain HTML, CSS and JavaScript. No build step, no dependencies.

## Run it
* **Quickest:** double-click `index.html` (works from `file://`).
* **Local server:** `python3 -m http.server 8080` then open http://localhost:8080
* **Deploy:** upload this whole folder to any static host (Netlify, Vercel, Cloudflare Pages, GitHub Pages, S3, any web server).
* **One file instead:** `python3 tools/build-single-file.py` writes `dist/true-ryze-single.html` (already included).

## Folder map
```
index.html                  page shell (header, footer, canvases, links to css/js)
css/styles.css              all styles and design tokens (CSS variables at the top)
js/config.js                EDIT HERE: pricing plans, comparison rows, jobs, WhatsApp link, Google Maps + social links
js/app.js                   application logic: router, pages, effects, handshake, lamp, portal, forms
assets/                     logo-large.webp, logo-small.webp, favicon.png, handshake-sheet.webp
dist/true-ryze-single.html  self-contained single-file build
docs/DOCUMENTATION.md       full documentation (read this first)
docs/PORTAL_ARCHITECTURE.md backend plan for the client / team / dealer portal
tools/build-single-file.py  rebuild the single-file version
tools/make_handshake_sheet.py  regenerate the handshake frames from a green-screen video
tests/test_suite.py         automated browser tests (Playwright)
```

## The things you will most likely edit
1. Pricing, comparison table, jobs, WhatsApp community link, Google Maps / social links: `js/config.js`
2. Make the forms deliver: set `CONFIG.formEndpoint` in `js/app.js` (docs section 9)
3. Colours and fonts: CSS variables at the top of `css/styles.css`
4. Page text: the page templates in `js/app.js` (search for the route name, e.g. `services:()=>`)
5. Handshake video: `tools/make_handshake_sheet.py`

## Honest status
Form delivery, client/team/dealer sign-in and live portal data need a backend and are **not connected**. The portal dashboards are
previews with clearly labelled sample data. Nothing pretends to work: every unconnected feature says so (docs section 16).
