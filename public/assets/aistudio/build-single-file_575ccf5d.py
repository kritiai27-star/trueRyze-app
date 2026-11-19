#!/usr/bin/env python3
"""Rebuild ONE self-contained HTML file from this project (CSS, JS and images inlined).

Usage:  python3 tools/build-single-file.py [output.html]
Default output: dist/true-ryze-single.html

Use it when a host needs a single file. Nothing is downloaded: fonts still load from Google Fonts when online and
fall back to system fonts offline.
"""
import base64, mimetypes, os, sys

root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
out = sys.argv[1] if len(sys.argv) > 1 else os.path.join(root, 'dist', 'true-ryze-single.html')

def read(path):
    with open(os.path.join(root, path), encoding='utf-8') as f:
        return f.read()

html = read('index.html')
html = html.replace('<link rel="stylesheet" href="css/styles.css">', '<style>\n' + read('css/styles.css') + '\n</style>')
html = html.replace('<link rel="preload" href="assets/handshake-sheet.webp" as="image">\n', '')
tags = '<script src="js/config.js"></script>\n<script src="js/app.js"></script>'
assert tags in html, 'script tags not found in index.html'
html = html.replace(tags, '<script>\n' + read('js/config.js') + '\n' + read('js/app.js') + '\n</script>')
assets = os.path.join(root, 'assets')
for name in sorted(os.listdir(assets)):
    mime = mimetypes.guess_type(name)[0] or 'application/octet-stream'
    with open(os.path.join(assets, name), 'rb') as f:
        uri = 'data:%s;base64,%s' % (mime, base64.b64encode(f.read()).decode())
    html = html.replace('assets/' + name, uri)
os.makedirs(os.path.dirname(out), exist_ok=True)
with open(out, 'w', encoding='utf-8') as f:
    f.write(html)
print('Wrote', out, '(%d KB)' % (len(html) // 1024))
