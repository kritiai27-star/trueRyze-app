#!/usr/bin/env python3
"""Turn a GREEN-SCREEN handshake video into the frame sheet used by the homepage handshake.

Requires:  pip install opencv-python numpy pillow
Usage:     python3 tools/make_handshake_sheet.py input.mp4 [--crop-top 64 --crop-bottom 720 --width 640]

1. Keys out the green (despilled, soft edges) and finds a seamless loop inside the shaking part of the clip.
2. Writes assets/handshake-sheet.webp: an 8-column grid of transparent frames  [entrance frames][loop frames].
   The entrance + loop are played once when Home appears; the loop alone is played once per pointer interaction.
3. Prints META. Paste it over  M={...}  next to  const SHEET=  in js/app.js.
Adjust --first (first frame where hands appear) and --crop-* after looking at your clip.
"""
import argparse, json, os
import cv2, numpy as np
from PIL import Image

ap = argparse.ArgumentParser()
ap.add_argument('video'); ap.add_argument('--crop-top', type=int, default=64); ap.add_argument('--crop-bottom', type=int, default=720)
ap.add_argument('--width', type=int, default=640); ap.add_argument('--first', type=int, default=8)
ap.add_argument('--loop-start-min', type=int, default=58); ap.add_argument('--loop-start-max', type=int, default=84)
ap.add_argument('--fps', type=int, default=20); ap.add_argument('--quality', type=int, default=70)
ap.add_argument('--out', default=os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'assets', 'handshake-sheet.webp'))
a = ap.parse_args()
cap = cv2.VideoCapture(a.video); raw = []
while True:
    ok, f = cap.read()
    if not ok: break
    raw.append(f)
n = len(raw); src_fps = cap.get(cv2.CAP_PROP_FPS) or 30
bg_g = float(np.median(raw[0][..., 1])); print('frames', n, 'size', raw[0].shape[1], 'x', raw[0].shape[0], 'green level', bg_g)

def key(f):
    rgb = f[..., ::-1].astype(np.float32); r, g, b = rgb[..., 0], rgb[..., 1], rgb[..., 2]
    gs = g - np.maximum(r, b); al = 1 - np.clip((gs - 18) / (105 - 18), 0, 1); al = np.where(al < .04, 0, al)
    inv = np.where(al > .05, 1 / np.maximum(al, .05), 0)
    r2 = np.clip(r * inv, 0, 255); b2 = np.clip(b * inv, 0, 255); g2 = np.clip((g - (1 - al) * bg_g) * inv, 0, 255)
    g2 = np.minimum(g2, np.maximum(r2, b2) + 6)
    return np.dstack([r2, g2, b2, al * 255]).astype(np.float32)

small = np.array([np.dstack([k[..., :3].mean(2), k[..., 3]]) for k in (key(cv2.resize(f, (160, 90), interpolation=cv2.INTER_AREA)) for f in raw)]); best = None
for i in range(a.loop_start_min, a.loop_start_max):
    for j in range(i + 38, min(n - 1, i + 66)):
        d = np.abs(small[i] - small[j]).mean() + .35 * np.abs((small[i] - small[i - 1]) - (small[j] - small[j - 1])).mean()
        if best is None or d < best[0]: best = (d, i, j)
_, i0, j0 = best; N = 8; print('seamless loop frames', i0, '->', j0)
def premul(k): al = k[..., 3:4] / 255; return np.dstack([k[..., :3] * al, k[..., 3:4]])
def unpre(p):
    al = p[..., 3:4] / 255; rgb = np.where(al > .02, p[..., :3] / np.maximum(al, .02), 0); return np.dstack([np.clip(rgb, 0, 255), p[..., 3:4]])
seq = [('f', k) for k in range(a.first, i0)]
loop = [('x', k, k - (j0 - i0), (k - (j0 - N) + 1) / N) if k >= j0 - N else ('f', k) for k in range(i0, j0)]
def pick(lst, step):
    out = []; x = 0.0
    while int(round(x)) < len(lst): out.append(lst[int(round(x))]); x += step
    return out
step = src_fps / a.fps; ent = pick(seq, step); lp = pick(loop, step)
W = a.width; H = int(round((a.crop_bottom - a.crop_top) * W / raw[0].shape[1]))
def render(it):
    k = key(raw[it[1]]) if it[0] == 'f' else unpre(premul(key(raw[it[1]])) * (1 - it[3]) + premul(key(raw[it[2]])) * it[3])
    return unpre(cv2.resize(premul(k[a.crop_top:a.crop_bottom]), (W, H), interpolation=cv2.INTER_AREA))
frames = [render(it) for it in ent + lp]; cols = 8; rows = (len(frames) + cols - 1) // cols
sheet = np.zeros((rows * H, cols * W, 4), np.uint8)
for q, fr in enumerate(frames):
    r_, c_ = divmod(q, cols); sheet[r_ * H:(r_ + 1) * H, c_ * W:(c_ + 1) * W] = np.clip(fr, 0, 255).astype(np.uint8)
os.makedirs(os.path.dirname(a.out), exist_ok=True)
Image.fromarray(sheet, 'RGBA').save(a.out, 'WEBP', quality=a.quality, alpha_quality=90, method=4)
print('wrote', a.out, os.path.getsize(a.out) // 1024, 'KB')
print('META =', json.dumps({'W': W, 'H': H, 'cols': cols, 'ent': len(ent), 'loop': len(lp), 'fps': a.fps}))
