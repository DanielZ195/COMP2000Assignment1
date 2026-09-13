# -*- coding: utf-8 -*-
import io, os, sys

W, H = 1860, 1150
HDR, LINE, PAD = 30, 16, 9

PALETTE = {
    'abstract':  ('#eef2fb', '#3a5a9b'),
    'concrete':  ('#ffffff', '#5a6472'),
    'interface': ('#fdf3e3', '#b07d2a'),
    'enum':      ('#eef8ef', '#3f8a4d'),
    'exception': ('#fdeded', '#b3403a'),
    'view':      ('#f4eefb', '#7a4fa8'),
    'core':      ('#e9f4fa', '#2d7899'),
}

boxes = {}
order = []

def esc(s):
    return s.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;')

def box(bid, x, y, w, name, kind, attrs, methods, stereo=None, note=None):
    n = len(attrs) + len(methods)
    extra = 18 if stereo else 0
    sep = 12 if (attrs and methods) else 0
    h = HDR + extra + PAD * 2 + n * LINE + sep + (LINE if note else 0)
    boxes[bid] = dict(x=x, y=y, w=w, h=h, name=name, kind=kind, attrs=attrs,
                      methods=methods, stereo=stereo, note=note)
    order.append(bid)

# ---------------- Entity hierarchy (left) ----------------
box('entity', 300, 64, 360, 'Entity', 'abstract', [
    '- x, y : int',
    '- alive : boolean',
    '- deathCause : DeathCause',
], [
    '+ getX() / getY() : int',
    '+ kill(cause : DeathCause)',
    '+ distanceTo(other : Entity) : int',
    '# setPosition(x, y : int)',
    '# px() / py() : int',
    '+ draw(g : Graphics)',
    '+ update(world : World)  {abstract}',
    '+ getColor() : Color     {abstract}',
    '+ getLabel() : String    {abstract}',
], stereo='abstract')

box('edible', 740, 64, 270, 'Edible', 'interface', [], [
    '+ getNutritionValue() : double',
], stereo='interface')

box('animal', 140, 350, 400, 'Animal', 'abstract', [
    '# health, maxHealth : double',
    '# speed, visionRadius : int',
    '- bredThisTick : boolean',
], [
    '# isWellFed() : boolean',
    '# breedThreshold() / breedCost() : double',
    '# tryBreed(world : World)',
    '# nearestMate(world : World) : Animal',
    '# moveToward(target : Entity)   [overload]',
    '# moveAwayFrom(threat : Entity) [overload]',
    '# stepToward(dx, dy : int) : int[]',
    '# findNearest(List<T>) : T   [generic]',
    '# newOffspring(x, y) : Animal {abstract}',
    '# act(world : World)          {abstract}',
], stereo='abstract')

box('food', 660, 350, 300, 'Food', 'concrete', [
    '- NUTRITION_VALUE = 40',
], [
    '+ getNutritionValue() : double',
    '+ getLabel() : String   [ * ]',
])

box('predator', 60, 700, 330, 'Predator', 'abstract', [
    '# eatDistance = 0',
], [
    '# breedThreshold() = 0.75 x max',
    '# breedCost()      = 0.40 x max',
    '# tryEat(prey, world) : boolean',
    '# act(world : World)',
], stereo='abstract')

box('prey', 450, 700, 360, 'Prey', 'abstract', [
    '# HUNGRY_SPEED = 1  FED_SPEED = 2',
    '# HOP_COST = 8',
    '# nutritionValue : double',
], [
    '# updateSpeed()',
    '# evade(threat, world)  [knight-hop]',
    '# refugeRange() : int',
    '# tryEatFood(food : Food) : boolean',
    '# act(world : World)',
], stereo='abstract')

box('hawk', 40, 985, 240, 'Hawk', 'concrete', [
    '- restCounter : int',
], [
    '# stepToward()  [bishop]',
    '# act() rests every 4th tick',
])
box('fox', 305, 985, 225, 'Fox', 'concrete', [], [
    '# stepToward()  [rook]',
])
box('rabbit', 555, 985, 230, 'Rabbit', 'concrete', [], [
    '# breedThreshold 0.35  (K)',
    '# breedCost 0.25',
])
box('mouse', 810, 985, 235, 'Mouse', 'concrete', [], [
    '# refugeRange() = 22',
    '# breedThreshold 0.22  (r)',
])

# ---------------- Engine (right) ----------------
box('world', 1080, 64, 400, 'World', 'core', [
    '- grid : Grid<Entity>',
    '- hawks : List<Hawk>   foxes : List<Fox>',
    '- rabbits : List<Rabbit>   mice : List<Mouse>',
    '- food : List<Food>',
    '- history : Map<String, List<Integer>>',
    '- rng : Random    seed : long',
    '- state : SimulationState',
    '- starved, eaten, births, hops : int',
], [
    '+ update()',
    '- rebuildGrid()',
    '+ spawnNear(parent) : Animal',
    '      throws SpawnException',
    '+ isInSafeZone(x, y) : boolean',
    '+ getGrid() : Grid<Entity>',
    '+ getHistory() : Map<String, List<Integer>>',
])

box('grid', 1530, 64, 300, 'Grid<T extends Entity>', 'core', [
    '- cells : List<List<T>>',
], [
    '+ cellAt(x, y) : List<T>',
    '     throws IndexOutOfBoundsException',
    '+ occupantsWithin(x, y, r) : List<T>',
    '+ occupantsWithin(.., Predicate<? super T>)',
    '+ <U extends T> occupantsWithin(.., Class<U>)',
    '+ add(occupant : T) / clear()',
], note='List<List<T>>: no generic arrays')

box('config', 1080, 500, 400, 'SimulationConfig', 'core', [
    '- values : Map<String, Long>',
    '- DEFAULTS : Map<String, Long>',
], [
    '+ fromArgs(String[]) : SimulationConfig',
    '      throws SimulationConfigException',
    '+ defaults() : SimulationConfig',
    '- validate() throws SimulationConfigException',
    '+ getSeed / getCols / getTickMs / ...',
    '+ hasSafeZone() : boolean',
])

box('cfgex', 1530, 400, 300, 'SimulationConfigException', 'exception', [], [
    '+ (message)   + (message, cause)',
], stereo='checked')

box('spawnex', 1530, 510, 300, 'SpawnException', 'exception', [], [
    '+ (message)',
], stereo='checked')

box('deathcause', 1530, 620, 145, 'DeathCause', 'enum', [], [
    'STARVED', 'EATEN', 'UNKNOWN',
], stereo='enumeration')

box('simstate', 1695, 620, 135, 'SimulationState', 'enum', [], [
    'RUNNING', 'PREY_EXTINCT', 'PREDATORS_EXTINCT', 'TIME_LIMIT',
], stereo='enumeration')

box('simpanel', 1080, 850, 255, 'SimPanel', 'view', [
    '+ CELL_SIZE = 18',
], [
    '# paintComponent(g)',
    '- drawCounts(g)',
], stereo='extends JPanel')

box('graphpanel', 1360, 850, 255, 'GraphPanel', 'view', [
    '- colours : Map<String, Color>',
], [
    '# paintComponent(g)',
    '- plot(value, peak, h) : int',
], stereo='extends JPanel')

box('main', 1640, 850, 190, 'Main', 'view', [], [
    '+ main(args : String[])',
    '- randomPointOutsideZone()',
])

rels = [
    ('animal', 'entity', 'inherit'), ('food', 'entity', 'inherit'),
    ('predator', 'animal', 'inherit'), ('prey', 'animal', 'inherit'),
    ('hawk', 'predator', 'inherit'), ('fox', 'predator', 'inherit'),
    ('rabbit', 'prey', 'inherit'), ('mouse', 'prey', 'inherit'),
    ('food', 'edible', 'realize'), ('prey', 'edible', 'realize'),
    ('world', 'grid', 'compose'), ('world', 'config', 'compose'),
    ('simpanel', 'world', 'depend'), ('graphpanel', 'world', 'depend'),
    ('main', 'world', 'depend'),
]

def top(b):    return (b['x'] + b['w'] / 2.0, b['y'])
def bottom(b): return (b['x'] + b['w'] / 2.0, b['y'] + b['h'])
def leftp(b):  return (b['x'], b['y'] + b['h'] / 2.0)
def rightp(b): return (b['x'] + b['w'], b['y'] + b['h'] / 2.0)

dep_index = [0]

out = []
A = out.append
A('<svg xmlns="http://www.w3.org/2000/svg" width="%d" height="%d" viewBox="0 0 %d %d" '
  'font-family="Segoe UI, Helvetica, Arial, sans-serif">' % (W, H, W, H))
A('<rect width="%d" height="%d" fill="#ffffff"/>' % (W, H))
A('<defs>')
A('<marker id="inh" markerWidth="14" markerHeight="14" refX="13" refY="7" orient="auto">'
  '<path d="M0,0 L13,7 L0,14 z" fill="#ffffff" stroke="#3a4a5e" stroke-width="1.4"/></marker>')
A('<marker id="dep" markerWidth="11" markerHeight="11" refX="10" refY="5.5" orient="auto">'
  '<path d="M0,0.5 L10,5.5 L0,10.5" fill="none" stroke="#5a6472" stroke-width="1.5"/></marker>')
A('<marker id="dia" markerWidth="17" markerHeight="12" refX="1" refY="6" orient="auto">'
  '<path d="M0,6 L8,1.5 L16,6 L8,10.5 z" fill="#2d7899" stroke="#2d7899" stroke-width="1"/></marker>')
A('</defs>')

A('<text x="40" y="42" font-size="25" font-weight="700" fill="#1d2733">'
  'Predator and Prey Simulation &#8212; Class Design</text>')
A('<text x="%d" y="42" font-size="12.5" fill="#5a6472" text-anchor="end">'
  'COMP2000 Semester Project &#183; github.com/DanielZ195/COMP2000Assignment1</text>' % (W - 40))

for a, b, kind in rels:
    A_, B_ = boxes[a], boxes[b]
    if kind in ('inherit', 'realize'):
        if A_['y'] > B_['y'] + B_['h'] - 5:
            x1, y1 = top(A_)
            x2, y2 = bottom(B_)
            mid = (y1 + y2) / 2.0
            path = 'M%.1f,%.1f V%.1f H%.1f V%.1f' % (x1, y1, mid, x2, y2)
        else:
            x1, y1 = rightp(A_)
            x2, y2 = leftp(B_)
            path = 'M%.1f,%.1f H%.1f' % (x1, y1, x2)
        dash = ' stroke-dasharray="7,5"' if kind == 'realize' else ''
        A('<path d="%s" fill="none" stroke="#3a4a5e" stroke-width="1.7"%s marker-end="url(#inh)"/>'
          % (path, dash))
    elif kind == 'compose':
        if A_['x'] + A_['w'] < B_['x']:
            x1, y1 = rightp(A_)
            x2, y2 = leftp(B_)
            path = 'M%.1f,%.1f H%.1f' % (x1, y1, x2)
        else:
            x1, y1 = bottom(A_)
            x2, y2 = top(B_)
            path = 'M%.1f,%.1f V%.1f' % (x1, y1, y2)
        A('<path d="%s" fill="none" stroke="#2d7899" stroke-width="1.9" marker-start="url(#dia)"/>' % path)
    else:
        # Routed down the clear gutter between the hierarchy and the engine,
        # so dependency lines never cut through a class box.
        gutter = 1062 - dep_index[0] * 13
        dep_index[0] += 1
        x1, y1 = leftp(A_)
        x2, y2 = leftp(B_)
        A('<path d="M%.1f,%.1f H%.1f V%.1f H%.1f" fill="none" stroke="#5a6472" stroke-width="1.4" '
          'stroke-dasharray="6,5" marker-end="url(#dep)"/>' % (x1, y1, gutter, y2, x2))

for bid in order:
    b = boxes[bid]
    fill, stroke = PALETTE[b['kind']]
    x, y, w, h = b['x'], b['y'], b['w'], b['h']
    A('<rect x="%d" y="%d" width="%d" height="%d" rx="5" fill="%s" stroke="%s" stroke-width="1.8"/>'
      % (x, y, w, h, fill, stroke))
    ty = y + 21
    if b['stereo']:
        A('<text x="%.1f" y="%d" font-size="11.5" fill="%s" text-anchor="middle" font-style="italic">'
          '&#171;%s&#187;</text>' % (x + w / 2.0, ty, stroke, esc(b['stereo'])))
        ty += 18
    A('<text x="%.1f" y="%d" font-size="16.5" font-weight="700" fill="#16202b" text-anchor="middle">%s</text>'
      % (x + w / 2.0, ty, esc(b['name'])))
    ty += 11
    A('<line x1="%d" y1="%d" x2="%d" y2="%d" stroke="%s" stroke-width="1.2"/>' % (x, ty, x + w, ty, stroke))
    ty += 16
    for item in b['attrs']:
        A('<text x="%d" y="%d" font-size="11.6" font-family="Consolas, Courier New, monospace" '
          'fill="#33404f">%s</text>' % (x + 10, ty, esc(item)))
        ty += LINE
    if b['attrs'] and b['methods']:
        ty -= 3
        A('<line x1="%d" y1="%d" x2="%d" y2="%d" stroke="%s" stroke-width="0.9" opacity="0.5"/>'
          % (x, ty, x + w, ty, stroke))
        ty += 17
    for item in b['methods']:
        A('<text x="%d" y="%d" font-size="11.6" font-family="Consolas, Courier New, monospace" '
          'fill="#33404f">%s</text>' % (x + 10, ty, esc(item)))
        ty += LINE
    if b['note']:
        A('<text x="%d" y="%d" font-size="10.5" font-style="italic" fill="#7a8698">%s</text>'
          % (x + 10, ty, esc(b['note'])))

lx, ly = 40, 78
A('<rect x="%d" y="%d" width="246" height="252" rx="5" fill="#fafbfc" stroke="#c4ccd6" stroke-width="1.3"/>'
  % (lx, ly))
A('<text x="%d" y="%d" font-size="14" font-weight="700" fill="#1d2733">Legend</text>' % (lx + 12, ly + 24))
yy = ly + 50
for kind, label in [('inherit', 'extends (inheritance)'), ('realize', 'implements (realization)'),
                    ('compose', 'owns (composition)'), ('depend', 'uses (dependency)')]:
    if kind == 'inherit':
        A('<path d="M%d,%d H%d" stroke="#3a4a5e" stroke-width="1.7" marker-end="url(#inh)"/>' % (lx + 14, yy, lx + 74))
    elif kind == 'realize':
        A('<path d="M%d,%d H%d" stroke="#3a4a5e" stroke-width="1.7" stroke-dasharray="7,5" '
          'marker-end="url(#inh)"/>' % (lx + 14, yy, lx + 74))
    elif kind == 'compose':
        A('<path d="M%d,%d H%d" stroke="#2d7899" stroke-width="1.9" marker-start="url(#dia)"/>' % (lx + 17, yy, lx + 88))
    else:
        A('<path d="M%d,%d H%d" stroke="#5a6472" stroke-width="1.4" stroke-dasharray="6,5" '
          'marker-end="url(#dep)"/>' % (lx + 14, yy, lx + 74))
    A('<text x="%d" y="%d" font-size="11" fill="#33404f">%s</text>' % (lx + 96, yy + 4, label))
    yy += 25
yy += 8
for kind, label in [('abstract', 'abstract class'), ('interface', 'interface'),
                    ('core', 'engine / data structure'), ('enum', 'enumeration'),
                    ('exception', 'checked exception'), ('view', 'Swing view')]:
    f, s = PALETTE[kind]
    A('<rect x="%d" y="%d" width="26" height="13" rx="2" fill="%s" stroke="%s" stroke-width="1.3"/>'
      % (lx + 14, yy - 10, f, s))
    A('<text x="%d" y="%d" font-size="11.8" fill="#33404f">%s</text>' % (lx + 50, yy, label))
    yy += 20

A('<text x="40" y="%d" font-size="11" fill="#8a94a2">Test harnesses omitted (plain main() classes, '
  'standard JRE only): GridTest, ConfigTest, ChaseTest, Ablation, Series, Render.</text>' % (H - 24))
A('</svg>')

dest = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'design.svg')
io.open(dest, 'w', encoding='utf-8').write('\n'.join(out))
print('wrote %s  (%d classes, %d relationships)' % (dest, len(boxes), len(rels)))
