#!/usr/bin/env python3
"""TRUE RYZE: full browser test suite (Playwright, Chromium).

Setup:  pip install playwright && playwright install chromium
Run:    python3 tests/test_suite.py                 # tests ../index.html via file://
        python3 tests/test_suite.py http://localhost:8080/   # or any URL / the single-file build
Optional second argument: a section letter list, e.g. `ABC` to run only some sections.
Exit code 0 = everything passed.
"""
import os, sys
from playwright.sync_api import sync_playwright

url = sys.argv[1] if len(sys.argv) > 1 else 'file://' + os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'index.html')
only = sys.argv[2] if len(sys.argv) > 2 else 'ABCDEFGH'
ROUTES = ['home', 'services', 'pricing', 'how-it-works', 'about', 'careers', 'contact', 'account', 'portal', 'portal-client', 'portal-team',
          'portal-dealer', 'website-development', 'website-maintenance', 'legal']
fails = []; total = [0]
def chk(ok, msg):
    total[0] += 1; print(('PASS ' if ok else 'FAIL ') + msg, flush=True)
    if not ok: fails.append(msg)
HASH = "(()=>{const c=document.getElementById('hands');const d=c.getContext('2d').getImageData(0,0,c.width,c.height).data;let h=0;for(let i=0;i<d.length;i+=61)h=(h*31+d[i])|0;return h})()"
PX = "(()=>{const c=document.getElementById('hands'),x=c.getContext('2d').getImageData(0,0,c.width,c.height).data;let n=0;for(let i=3;i<x.length;i+=4)if(x[i]>40)n++;return n})()"
PLAY = "!!document.getElementById('hands')._play"

def open_page(b, w, h, **kw):
    ctx = b.new_context(viewport={'width': w, 'height': h}, **kw); pg = ctx.new_page(); errs = []
    pg.on('pageerror', lambda e: errs.append(str(e)[:120])); pg.on('console', lambda m: errs.append(m.text[:120]) if m.type == 'error' and 'ERR_' not in m.text else None)
    pg.goto(url); pg.wait_for_timeout(500); pg.keyboard.press('Escape'); pg.wait_for_timeout(400); return ctx, pg, errs
def go(pg, r, wait=900): pg.evaluate("location.hash='#%s'" % r); pg.wait_for_timeout(wait)
def still(pg, label):
    a = pg.evaluate(HASH); pg.wait_for_timeout(1200); b_ = pg.evaluate(HASH); pg.wait_for_timeout(1200); c = pg.evaluate(HASH)
    chk(a == b_ == c and not pg.evaluate(PLAY), label)

with sync_playwright() as p:
    b = p.chromium.launch()
    if 'A' in only:   # routes, layout, errors
        for w, h in [(320, 640), (360, 740), (390, 844), (768, 1024), (1024, 768), (1366, 768), (1440, 900), (1920, 1080)]:
            ctx, pg, errs = open_page(b, w, h); bad = []
            for r in ROUTES:
                go(pg, r, 800); d = pg.evaluate("[document.documentElement.scrollWidth-innerWidth,document.querySelector('main').innerText.length,document.querySelectorAll('main h1').length]")
                if d[0] > 1 or d[1] < 100 or d[2] != 1: bad.append((r, d))
            chk(not bad and not errs, 'A %dpx: %d routes render, no overflow, one h1 each, no JS errors %s %s' % (w, len(ROUTES), bad, errs[:1])); ctx.close()
    if 'B' in only:   # navigation
        for w in (1001, 1100, 1440):
            ctx, pg, _ = open_page(b, w, 700)
            d = pg.evaluate("(()=>{const c=document.querySelector('header .navcta').getBoundingClientRect(),n=document.querySelector('header nav');return {t:document.querySelector('header .navcta').innerText.replace(/\\s+/g,' '),fits:c.right<=innerWidth&&n.scrollWidth<=n.clientWidth+1&&document.querySelector('header .wrap').getBoundingClientRect().height<=72,href:document.querySelector('header .navcta').getAttribute('href')}})()")
            chk('Let’s Build Together' in d['t'] and d['fits'] and d['href'] == '#contact', 'B nav CTA label/fit/target at %dpx %s' % (w, d)); ctx.close()
        ctx, pg, _ = open_page(b, 390, 844); pg.locator('#burger').click(); pg.wait_for_timeout(600)
        chk(pg.locator('#nav .navcta').is_visible() and 'Contact us' not in pg.evaluate("document.querySelector('header').innerText"), 'B mobile menu shows the Let’s Build Together button, no "Contact us"')
        pg.locator('#nav .navcta').click(); pg.wait_for_timeout(1000); chk(pg.evaluate("location.hash") == '#contact' and pg.evaluate("document.querySelector('#burger').getAttribute('aria-expanded')") == 'false', 'B menu button opens the contact page and closes the menu')
        chk(pg.evaluate("document.querySelector('main h1').innerText.includes('build')") and pg.evaluate("!!document.querySelector('#f-enquiries')"), 'B contact page content/form unchanged'); ctx.close()
    if 'C' in only:   # handshake (desktop)
        ctx, pg, errs = open_page(b, 1440, 900); pg.mouse.move(60, 700); pg.wait_for_timeout(500)
        t = pg.evaluate("document.body.innerText"); chk('Real footage' not in t and 'Hologram' not in t and pg.evaluate("document.querySelectorAll('[data-hm],.hs-ctl,.hs-hint').length") == 0, 'C no Real footage / Hologram options anywhere')
        chk(pg.evaluate(PLAY), 'C opening: handshake plays'); pg.wait_for_timeout(5200); still(pg, 'C stationary after the single handshake'); chk(pg.evaluate(PX) > 20000, 'C hands stay permanently visible')
        box = pg.locator('#hands').bounding_box(); cx = box['x'] + box['width'] / 2; cy = box['y'] + box['height'] / 2
        for n in (1, 2, 3):
            pg.mouse.move(60, 700); pg.wait_for_timeout(250); pg.mouse.move(cx, cy, steps=3); pg.wait_for_timeout(450); pl = pg.evaluate(PLAY); pg.wait_for_timeout(3300)
            chk(pl and not pg.evaluate(PLAY), 'C pointer over hands #%d: one handshake then stop' % n)
        still(pg, 'C stationary between interactions')
        pg.mouse.move(60, 700); pg.wait_for_timeout(250); pg.mouse.move(box['x'] + box['width'] + 25, cy, steps=3); pg.wait_for_timeout(450); chk(pg.evaluate(PLAY), 'C pointer NEAR the hands triggers'); pg.wait_for_timeout(3300)
        pg.mouse.move(60, 700); pg.wait_for_timeout(300); pg.mouse.move(300, 520, steps=4); pg.wait_for_timeout(400); chk(not pg.evaluate(PLAY), 'C pointer far away does not trigger')
        pg.mouse.move(cx, cy, steps=3); pg.wait_for_timeout(300); pg.mouse.move(cx + 10, cy + 8); pg.wait_for_timeout(3600); pg.mouse.move(cx - 10, cy - 5); pg.wait_for_timeout(500); chk(not pg.evaluate(PLAY), 'C resting inside never auto-loops')
        pg.mouse.move(60, 700); go(pg, 'pricing', 1400); go(pg, 'home', 1400); chk(pg.evaluate(PLAY), 'C return to Home from another page: one handshake'); pg.wait_for_timeout(5400); still(pg, 'C stationary again')
        pg.reload(); pg.wait_for_timeout(400); pg.keyboard.press('Escape'); pg.mouse.move(60, 700); pg.wait_for_timeout(900); chk(pg.evaluate(PLAY), 'C reload: one handshake'); pg.wait_for_timeout(5400); still(pg, 'C stationary after reload')
        pg.evaluate("document.getElementById('hands').focus()"); pg.keyboard.press('Enter'); pg.wait_for_timeout(300); chk(pg.evaluate(PLAY), 'C keyboard Enter triggers'); chk(not errs, 'C no errors %s' % errs[:1]); ctx.close()
        ctx, pg, _ = open_page(b, 390, 844, has_touch=True, is_mobile=True); pg.wait_for_timeout(500)
        chk(not pg.evaluate(PLAY), 'C mobile: handshake waits until the hands are on screen'); pg.evaluate("document.getElementById('hands').scrollIntoView({block:'center'})"); pg.wait_for_timeout(500)
        chk(pg.evaluate(PLAY), 'C mobile: plays when scrolled into view'); pg.wait_for_timeout(5400); still(pg, 'C mobile: stationary afterwards')
        bb = pg.locator('#hands').bounding_box(); pg.touchscreen.tap(bb['x'] + bb['width'] / 2, bb['y'] + bb['height'] / 2); pg.wait_for_timeout(450); chk(pg.evaluate(PLAY), 'C mobile: tap triggers one handshake'); pg.wait_for_timeout(3300); chk(not pg.evaluate(PLAY), 'C mobile: stops after one'); ctx.close()
    if 'D' in only:   # forms
        ctx, pg, _ = open_page(b, 1440, 900); go(pg, 'contact', 1200); f = '#f-enquiries'
        pg.locator(f + ' button[type=submit]').click(); pg.wait_for_timeout(300); chk(pg.evaluate("document.activeElement.name") == 'name' and pg.evaluate("document.querySelector('[name=name]').getAttribute('aria-invalid')") == 'true', 'D empty submit: error + first invalid field focused')
        pg.fill(f + ' [name=name]', 'T'); pg.fill(f + ' [name=email]', 'bad'); pg.fill(f + ' [name=message]', 'hi'); pg.locator(f + ' button[type=submit]').click(); pg.wait_for_timeout(300); chk(pg.evaluate("document.querySelector('[name=email]').getAttribute('aria-invalid')") == 'true', 'D invalid email rejected')
        pg.fill(f + ' [name=email]', 'a@b.co'); pg.locator(f + ' button[type=submit]').click(); pg.wait_for_timeout(600); t = pg.locator('#m-enquiries').inner_text()
        chk('Not sent' in t and 'Thank you' not in t, 'D no endpoint: honest failure, never a fake success')
        calls = []; pg.route('https://example.test/**', lambda route: (calls.append(route.request.post_data), route.fulfill(status=200, content_type='application/json', body='{}')))
        pg.evaluate("CONFIG.formEndpoint='https://example.test/submit'"); pg.locator(f + ' button[type=submit]').click(); pg.wait_for_timeout(800)
        chk('Thank you' in pg.locator('#m-enquiries').inner_text() and calls and '"kind":"enquiries"' in calls[0] and 'hp_field' not in calls[0], 'D with endpoint: JSON delivered, success shown')
        pg.unroute('https://example.test/**'); pg.route('https://example.test/**', lambda route: route.fulfill(status=500, body='x')); pg.fill(f + ' [name=name]', 'T'); pg.fill(f + ' [name=email]', 'a@b.co'); pg.fill(f + ' [name=message]', 'hi')
        pg.locator(f + ' button[type=submit]').click(); pg.wait_for_timeout(800); chk('Not sent' in pg.locator('#m-enquiries').inner_text(), 'D endpoint error: honest failure')
        for r, fid in (('careers', 'f-applications'), ('account', 'f-access')):
            go(pg, r, 1000); pg.evaluate("CONFIG.formEndpoint=''"); pg.fill('#%s [name=name]' % fid, 'T'); pg.fill('#%s [name=email]' % fid, 'a@b.co'); pg.fill('#%s [name=message]' % fid, 'hi'); pg.locator('#%s button[type=submit]' % fid).click(); pg.wait_for_timeout(600)
            chk('Not sent' in pg.locator('#m-%s' % fid[2:]).inner_text(), 'D %s form honest failure' % r)
        ctx.close()
    if 'E' in only:   # lamp + account
        ctx, pg, errs = open_page(b, 1440, 900); go(pg, 'account', 1500)
        chk(pg.evaluate("!!document.querySelector('#lswitch')&&!document.querySelector('#cbead')&&document.body.innerText.includes('light up your journey')"), 'E switch + quote present, no pull cord')
        chk(pg.evaluate("(()=>{const q=document.querySelector('.sq').getBoundingClientRect(),l=document.querySelector('.lamp').getBoundingClientRect(),s=document.querySelector('#lswitch').getBoundingClientRect();return q.right<l.left&&s.left>l.right})()"), 'E quote left, lamp centre, switch right')
        pg.locator('#lswitch').click(); pg.wait_for_timeout(1500)
        chk(pg.evaluate("document.querySelector('#signin').classList.contains('lit')&&document.documentElement.classList.contains('lamp-lit')&&document.querySelector('#sqt').innerText.includes('Welcome in')"), 'E switch ON: lamp lit, page wash, welcome message')
        c = {}
        for n_, v in (('warm', 0), ('white', 50), ('cool', 100)):
            pg.evaluate("(()=>{const r=document.querySelector('#lcolor');r.value=%d;r.dispatchEvent(new Event('input',{bubbles:true}))})()" % v); pg.wait_for_timeout(300)
            c[n_] = pg.evaluate("[getComputedStyle(document.querySelector('#signin')).getPropertyValue('--lcr').trim(),getComputedStyle(document.querySelector('.lamp .sg:not(.sgb)')).stroke,getComputedStyle(document.querySelector('#f-signin button')).backgroundImage.slice(0,45)]")
        chk(len({c[k][0] for k in c}) == 3 and len({c[k][1] for k in c}) == 3 and len({c[k][2] for k in c}) >= 2, 'E colour slider recolours light, spiral and Sign in button')
        pg.evaluate("document.querySelector('#lswitch').focus()"); pg.keyboard.press('Space'); pg.wait_for_timeout(800); chk(not pg.evaluate("document.querySelector('#signin').classList.contains('lit')"), 'E keyboard switches off')
        pg.fill('#f-signin [name=email]', 'a@b.co'); pg.fill('#f-signin [name=password]', 'x'); pg.locator('#f-signin button[type=submit]').click(); pg.wait_for_timeout(300); chk('isn’t available' in pg.locator('#m-signin').inner_text(), 'E sign-in honest')
        go(pg, 'pricing', 1300); chk(not pg.evaluate("document.documentElement.classList.contains('lamp-lit')"), 'E leaving clears the light wash'); chk(not errs, 'E no errors'); ctx.close()
    if 'F' in only:   # portal
        ctx, pg, errs = open_page(b, 1440, 900); go(pg, 'client-portal', 1500)
        chk(pg.evaluate("[...document.querySelectorAll('.pc h2')].map(e=>e.innerText)") == ['Client Portal', 'Team Portal', 'Dealer Portal'], 'F portal order Client / Team / Dealer; old #client-portal alias works')
        pg.fill('#f-pc [name=cid]', 'bad'); pg.fill('#f-pc [name=password]', 'x'); pg.locator('#f-pc button[type=submit]').click(); pg.wait_for_timeout(300); chk(pg.evaluate("document.querySelector('#f-pc [name=cid]').getAttribute('aria-invalid')") == 'true', 'F Client ID format validated')
        pg.fill('#f-pc [name=cid]', 'TR-C-123456'); pg.locator('#f-pc button[type=submit]').click(); pg.wait_for_timeout(300); chk('isn’t connected' in pg.locator('#m-pc').inner_text(), 'F client sign-in honest (no fake login)')
        pg.locator('[data-prole=dealer]').first.click(); pg.wait_for_timeout(500); pg.fill('#f-pd [name=cid]', 'TR-D-001'); pg.fill('#f-pd [name=password]', 'x'); pg.locator('#f-pd button[type=submit]').click(); pg.wait_for_timeout(300); chk(pg.evaluate("document.querySelector('#f-pd [name=key]').getAttribute('aria-invalid')") == 'true', 'F dealer needs the access key')
        go(pg, 'portal-client', 1500); chk(pg.evaluate("document.querySelectorAll('.stepper li').length") == 10 and pg.evaluate("document.querySelectorAll('.stepper li.now').length") == 1 and 'TR-T-0457' in pg.evaluate("document.body.innerText"), 'F client preview: tracker, team ID only')
        go(pg, 'portal-team', 1500); pg.locator('#tempty').click(); pg.wait_for_timeout(300); chk(pg.evaluate("document.querySelector('#kw').innerText") == '0', 'F team preview empty state')
        go(pg, 'portal-dealer', 1500); pg.locator('[data-approve="0"]').click(); pg.wait_for_timeout(300); chk('nothing saved' in pg.locator('.res').first.inner_text(), 'F dealer approve is a labelled sample'); pg.select_option('#ofilter', 'due'); pg.wait_for_timeout(300)
        chk(pg.evaluate("[...document.querySelectorAll('#obody tr')].filter(r=>!r.hidden).length") == 2, 'F dealer filter works'); chk(not errs, 'F no errors'); ctx.close()
    if 'G' in only:   # audit
        ctx, pg, _ = open_page(b, 1280, 800); AUD = """(()=>{const o={},ids={};document.querySelectorAll('[id]').forEach(e=>ids[e.id]=(ids[e.id]||0)+1);o.dup=Object.entries(ids).filter(([k,v])=>v>1).map(x=>x[0]);o.noAlt=[...document.querySelectorAll('img')].filter(i=>i.getAttribute('alt')===null).length;
        o.unnamed=[...document.querySelectorAll('a,button,input,select,textarea,[role=switch]')].filter(e=>{if(e.type==='hidden'||e.closest('[aria-hidden=true]'))return false;const r=e.getBoundingClientRect();if(!r.width&&!r.height)return false;return !((e.getAttribute('aria-label')||e.innerText||e.title||(e.labels&&e.labels[0]&&e.labels[0].innerText)||'').trim())}).length;
        o.small=[...document.querySelectorAll('a,button')].filter(e=>{if(e.closest('[aria-hidden=true]'))return false;const r=e.getBoundingClientRect();return r.width>0&&r.height>0&&(r.height<24||r.width<24)&&getComputedStyle(e).display!=='inline'}).length;
        o.bad=[...document.querySelectorAll('a[href^="#"]')].map(a=>a.getAttribute('href').slice(1)).filter(h=>h&&!%s.includes(h)).length;o.blank=[...document.querySelectorAll('a[target=_blank]')].filter(a=>!/noopener/.test(a.rel)).length;return o})()""" % ROUTES
        bad = {}
        for r in ROUTES:
            go(pg, r, 900); o = pg.evaluate(AUD); o = {k: v for k, v in o.items() if v}
            if o: bad[r] = o
        chk(not bad, 'G audit (duplicate ids, alt text, control names, tap targets, links, rel): %s' % bad); ctx.close()
    if 'H' in only:   # reduced motion
        ctx = b.new_context(viewport={'width': 1280, 'height': 800}, reduced_motion='reduce'); pg = ctx.new_page(); pg.goto(url); pg.wait_for_timeout(1500)
        chk(not pg.evaluate("!!document.querySelector('#intro')") and pg.evaluate(PX) > 20000 and not pg.evaluate(PLAY), 'H reduced motion: intro skipped, handshake shown as still frame')
        pg.evaluate("document.getElementById('hands').scrollIntoView()"); pg.mouse.move(640, 400); pg.wait_for_timeout(600); chk(not pg.evaluate(PLAY), 'H reduced motion: no handshake animation on hover')
        go(pg, 'account', 800); pg.locator('#lswitch').click(); pg.wait_for_timeout(300); chk(pg.evaluate("document.querySelector('#signin').classList.contains('lit')"), 'H reduced motion: lamp switch still works'); ctx.close()
    b.close()
print('\nTOTAL %d checks, FAILS: %s' % (total[0], fails)); sys.exit(1 if fails else 0)
