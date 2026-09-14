# COMP2000 Worksheet 1 — Mid-Semester Submission

**Student name:** Daniel Zohar

**Student ID:** 47572221

**GitHub repo URL:** https://github.com/DanielZ195/COMP2000Assignment1

---

## 1. Version Control

**1.1.** Paste the first 10 lines of the output of `git log --graph --oneline --all` from your repository:

```
* 0c0f2c3 Refresh commit log and counts in worksheet section 1
* 447eb92 Fill worksheet section 4 and add log book entries for weeks 3 to 7
* 75dc073 Edit worksheet prose for tone
* 19bcc81 Add log book with Week 4 entry
* ad936b1 Add completed worksheet (section 4 left for log book)
* ca77273 Add UML class design diagram
*   76f279b Merge branch 'clearer-display'
|\
| * ea80eab Make the board readable: labels, grid lines, slower ticks
|/
```

**1.2.** Describe your workflow. Did you use branches? Pull requests?

Yes, throughout, in two phases.

Team phase. We worked in one shared repository, each on our own named
branch (`daniel`, `Anum-Taliya`, `James`, `Riya`), merging into `main` through pull
requests. My contributions came in through PR #3 (`Prey`, `Rabbit`, `Mouse`,
`SpawnException`) and PR #6 (`World`).

Individual phase. I forked the team repository so my submission had its own URL, then
worked in one branch per change, opened a pull request for each, and merged it before
starting the next. Seven merged PRs, in the order the work depended on each other:

| PR | Branch | Change |
|----|--------|--------|
| #1 | `seeded-rng` | One seeded `Random` replacing eleven `Math.random()` calls |
| #2 | `grid-coordinates` | Entities moved onto a 40×30 integer grid |
| #3 | `spatial-grid` | `Grid<T extends Entity>` for spatial queries |
| #4 | `chess-movement` | Per-species movement and the knight-hop escape |
| #5 | `energy-economy` | Breeding costs energy; all species reproduce |
| #6 | `population-stats` | History, death causes, end condition, graph |
| #7 | `config-and-exceptions` | `SimulationConfig` with validation |

I kept one rule throughout: every commit compiles and runs. The task summary makes a
working graphical simulation the minimum requirement, so I never left the build broken
between commits. PR #2 is the awkward case. Moving everything onto a grid made prey die out
faster, because the speed gap between predators and prey grew relative to the size of the
map. I said so in the pull request body instead of hiding it, and fixed it in the commit
that was meant to.

**1.3.** Estimate the percentage of commits you contributed relative to the total in your repository.

28 of 43 commits, or 65%. Excluding merge commits, 18 of 31, or 58%.

Two qualifications so the number is not misleading. First, I committed under two identities
(`daniel.zohark@gmail.com` and `daniel.zohar@students.mq.edu.au`), and the figure above
counts both. Second, a raw count understates the split during the team phase: of the 14
classes the team wrote, I wrote 5 (`Prey`, `World`, `Rabbit`, `Mouse` and
`SpawnException`), including the two largest files in the project.

---

## 2. Program Design

Don't forget to submit a pdf file of your program design along with this file.

> Submitted as `design.pdf` (also in the repository at `design/design.pdf`).

**2.1.** List every class in your project and write 1–2 sentences describing its responsibility.

Core hierarchy

- `Entity`: abstract root of everything that exists in the world. Holds a grid position and
  an alive flag, and knows how to draw itself as a labelled character.
- `Animal`: abstract `Entity` that moves, spends energy each tick, and can breed. Holds the
  movement, vision, metabolism and reproduction logic every animal shares.
- `Predator`: abstract `Animal` that eats `Prey`. Adds hunting, and the rule that predators
  must be well fed before they will breed.
- `Prey`: abstract `Animal` that eats `Food`, flees predators, and is itself edible. Adds
  foraging, the knight-hop escape, and the run for the refuge.
- `Hawk`: concrete `Predator` that moves diagonally like a bishop, sees furthest, and rests
  every fourth tick.
- `Fox`: concrete `Predator` that moves along one axis at a time like a rook, so it loses a
  tick whenever it has to change direction.
- `Rabbit`: concrete `Prey`, the K-strategist. Breeds late, pays more per offspring, and is
  worth more to a predator.
- `Mouse`: concrete `Prey`, the r-strategist. Breeds early and cheaply, and breaks for the
  refuge from further away.
- `Food`: a passive `Entity` that does nothing but sit still and report its nutritional
  value.
- `Edible`: one-method interface implemented by both `Food` and `Prey`, so eating code can
  ask "how much energy is this worth" without caring what it just ate.

Engine

- `World`: owns every entity, the spatial grid, the random source and the population
  history, and drives one tick of the simulation. It does not extend `Entity`, because it
  is not one of the things in the world; it holds them.
- `Grid<T extends Entity>`: a fixed grid of cells, each holding any number of `T`,
  answering "what is near here?" in time proportional to the search radius instead of the
  population size.
- `SimulationConfig`: every startable parameter in one place, parsed from `key=value`
  arguments and validated once before anything runs.
- `SimulationState`: enum reporting whether the run is still going, and if not, why it
  stopped.
- `DeathCause`: enum recording whether an animal starved or was eaten, so a finished run
  can be read back afterwards.
- `SpawnException`: checked exception thrown when there is no valid cell for a newborn.
- `SimulationConfigException`: checked exception thrown when the simulation is asked to
  start with parameters it cannot run.

Display

- `SimPanel`: draws the grid, the refuge and every living entity, plus a legend and live
  counts.
- `GraphPanel`: draws population history as one line per species over the last 400 ticks,
  taking its line colours from the species themselves.
- `Main`: builds the configuration and the world, wires up the window, and runs the tick
  timer.

Test harnesses (plain `main()` classes, standard JRE only, no framework)

- `GridTest`: 11 assertions covering grid queries, type filtering, edge clipping and bounds
  behaviour.
- `ConfigTest`: 14 assertions covering configuration validation and both unchecked guards.
- `ChaseTest`: measures escape rates over 60 runs per scenario.
- `Ablation`: runs the simulation with the refuge on and off to compare outcomes.
- `Series`: prints a population time series for tuning.
- `Render`: paints both panels offscreen to a PNG, so figures can be produced without a
  window.

**2.2.** Identify any inheritance relationships. For each parent–child pair, list what the child inherits and what it overrides.

| Parent → Child | Inherits | Overrides / adds |
|---|---|---|
| `Entity` → `Animal` | position, alive flag, `distanceTo()`, `draw()` | implements `update()`; adds `health`, `maxHealth`, `speed`, `visionRadius`, movement, breeding; declares `act()` and `newOffspring()` abstract |
| `Entity` → `Food` | position, alive flag, `draw()` | implements `update()` as a no-op, `getColor()`, `getLabel()` |
| `Animal` → `Predator` | everything above | overrides `breedThreshold()` (0.75 of max) and `breedCost()` (0.40); implements `act()`; adds `tryEat()` |
| `Animal` → `Prey` | everything above | implements `act()`; adds `updateSpeed()`, `evade()`, `refugeRange()`, `tryEatFood()`; also implements `Edible` |
| `Predator` → `Hawk` | hunting, eating, breeding rules | overrides `stepToward()` (diagonal only) and `act()` (rests every fourth tick); implements `newOffspring()`, `getColor()`, `getLabel()`; adds `restCounter` |
| `Predator` → `Fox` | hunting, eating, breeding rules | overrides `stepToward()` (one axis per tick); implements `newOffspring()`, `getColor()`, `getLabel()` |
| `Prey` → `Rabbit` | fleeing, foraging, evasion | overrides `breedThreshold()` (0.35) and `breedCost()` (0.25); implements `newOffspring()`, `getColor()`, `getLabel()` |
| `Prey` → `Mouse` | fleeing, foraging, evasion | overrides `refugeRange()` (22 vs 12), `breedThreshold()` (0.22), `breedCost()` (0.15); implements `newOffspring()`, `getColor()`, `getLabel()` |

`Edible` is realised rather than inherited. `Food` extends `Entity` and `Prey` extends
`Animal`, so they sit in different branches, but both promise `getNutritionValue()`. That
is why `Edible` is an interface and not a class: the two things that are edible have no
useful common ancestor below `Entity`.

**2.3.** Pick the class that you think has the best design. Explain why.

`Grid<T extends Entity>`.

It does one thing, which is answer questions about what occupies which cell, and it does
that without knowing anything about the simulation. It has no idea that hawks hunt or that
rabbits breed. That ignorance is what makes it reusable and easy to test on its own
(`GridTest` builds a grid, drops entities into it, and checks the answers, without running
a simulation at all).

Three specific decisions I would defend:

1. The bound `T extends Entity` does real work. It lets the grid read an occupant's own
   position through `getX()` and `getY()` instead of being told the coordinates separately,
   which would let the two drift apart.

2. Cost scales with the search radius, not the population. `occupantsWithin(x, y, 5)` reads
   a fixed 11×11 box whether there are 10 animals or 1000. The alternative, scanning the
   world's entity lists and filtering by distance, is O(N) per animal and so O(N²) per
   tick. Putting the query on the grid also put it in the right place, since the grid owns
   positions and should be the thing that answers questions about them.

3. Out-of-range is handled differently depending on whether it is expected. `cellAt(x, y)`
   throws `IndexOutOfBoundsException` for an index that does not exist, while
   `occupantsWithin()` quietly clips at the same boundary. A hawk scanning five cells from
   the corner is normal; asking for a cell outside the grid means the caller has a bug.

**2.4.** Paste one code snippet that demonstrates your use of polymorphism or encapsulation. Include an explanation of _how_ this demonstrates polymorphim or encapsulation. Give a reference to a provided reading that talks about this type of polymorphism or encapsulation.

```java
// Animal.java — the default: free movement in any of eight directions
protected int[] stepToward(int dx, int dy) {
    return new int[]{ clampStep(dx), clampStep(dy) };
}

// Fox.java — rook: one axis at a time, so changing direction costs a tick
@Override
protected int[] stepToward(int dx, int dy) {
    if (Math.abs(dx) >= Math.abs(dy)) return new int[]{ clampStep(dx), 0 };
    return new int[]{ 0, clampStep(dy) };
}

// Hawk.java — bishop: always diagonal, never straight
@Override
protected int[] stepToward(int dx, int dy) {
    int sx = (dx == 0) ? 1 : Integer.signum(dx);
    int sy = (dy == 0) ? 1 : Integer.signum(dy);
    int m = Math.min(speed, Math.max(1, Math.max(Math.abs(dx), Math.abs(dy))));
    return new int[]{ sx * m, sy * m };
}

// Animal.java — the caller, which knows about none of the above
protected void moveToward(int targetX, int targetY) {
    int[] d = stepToward(targetX - getX(), targetY - getY());
    setPosition(getX() + d[0], getY() + d[1]);
}
```

How this demonstrates polymorphism. `moveToward()` is defined once in `Animal` and never
overridden. When a `Fox` calls it, the call to `stepToward()` resolves to `Fox`'s version.
When a `Hawk` calls the identical inherited code, it resolves to `Hawk`'s. The method is
chosen by the runtime type of the object, not by the type of the reference the code is
holding. `World.update()` iterates a `List<Entity>` and calls `update()` on each, with no
knowledge that foxes and hawks move differently.

This is subtype polymorphism, and it is what makes the three species behave like different
animals instead of one shared behaviour with different constants. Each supplies its own
movement geometry to a movement algorithm that none of them had to rewrite.

Reference. *Learning Java*, 3rd edition, Chapter 6, "Subclassing and Inheritance", section
"Overriding Methods", which was the prescribed reading for Week 4. It names this exact
mechanism: overriding methods to change an object's behaviour is described there as subtype
polymorphism. The chapter also explains that overridden methods are "located dynamically",
so the most derived implementation runs even when the object is being handled through a
superclass reference. That is what lets `World` hold `Entity` references and still get rook
movement out of a fox.

---

## 3. Generics and Exceptions

**3.1.** List every place your code uses generics (e.g. `ArrayList<Actor>`, `Optional<Cell>`, `HashMap<String, Team>`). If you deliberately used none, explain why.

Generic class with a bounded type parameter

- `Grid<T extends Entity>` (`Grid.java`), the grid itself. The bound is what allows the grid
  to read an occupant's own coordinates.
- Its backing store is `List<List<T>>`, not `List<T>[][]`. Java does not allow the creation
  of generic arrays, because erasure leaves the array with no runtime type to check against.
  The alternative (an `Object[][]` with `@SuppressWarnings("unchecked")` on every read)
  would silence the warning without making the operation any safer, which is the heap
  pollution the Week 5 activity was built to demonstrate. I used nested lists instead and
  took the small indirection cost.

Generic methods

- `<T extends Entity> T findNearest(List<T> candidates)` (`Animal.java`), one search reused
  for hunting (`List<Prey>`), fleeing (`List<Predator>`), foraging (`List<Food>`) and
  mate-finding (`List<Animal>`), with no casting and no duplicated loop.
- `<U extends T> List<U> occupantsWithin(int, int, int, Class<U>)` (`Grid.java`), a method
  type parameter bounded by the *class's* own parameter. `occupantsWithin(x, y, 4,
  Predator.class)` returns a `List<Predator>`, so no call site ever casts.

Bounded wildcard

- `occupantsWithin(int, int, int, Predicate<? super T> filter)` (`Grid.java`), which accepts
  a predicate written against `Entity` or any supertype of `T`, not only against `T`
  exactly.

Parameterised collections

- `World`: `List<Hawk>`, `List<Fox>`, `List<Rabbit>`, `List<Mouse>`, `List<Food>`,
  `List<Entity>`, plus `Map<String, List<Integer>>` for the population history and
  `Map<String, Color>` for the species legend.
- `SimulationConfig`: `Map<String, Long>` for settings and defaults.
- `GraphPanel`: `Map<String, Color>`.
- `Animal`, `Prey`, `Predator`, `Grid`: `List<...>` locals throughout.

Where I deliberately did not use generics

`Edible` is not generic, and should not be. `getNutritionValue()` returns a `double`
whoever is eating and whatever is eaten, so there is no type that varies. Writing
`Edible<T>` would introduce a type parameter that appears nowhere in the interface's own
signature. That would be generics for their own sake.

The `Entity` hierarchy uses inheritance for the same reason. What varies between a hawk and
a rabbit is behaviour, which is what overriding is for, and not type, which is what
generics are for. An `Animal<MovementStyle>` would have been the wrong tool.

**3.2.** List every place your code handles exceptions (try/catch, throws, custom exception classes). What error is each protecting against?

The design follows one rule, taken from the Week 6 reading: expected, recoverable conditions
get checked exceptions, and programming errors get unchecked ones.

| Where | Type | Protects against |
|---|---|---|
| `World.spawnNear()` declares `throws SpawnException` | checked, custom | No valid cell next to the parent for a newborn, for example when the parent is against the world edge |
| `Animal.tryBreed()` has `try`/`catch (SpawnException)` | catch | Recovers by skipping the birth this tick, before either parent pays the energy cost |
| `SimulationConfig.fromArgs()` and `validate()` declare `throws SimulationConfigException` | checked, custom | Unparseable or invalid settings: a zero-sized grid, negative populations, more entities than cells, a world with no prey, `maxFood` below `foodPerTick` |
| `SimulationConfig.fromArgs()` has `catch (NumberFormatException)` | catch + rethrow | Non-numeric input, rethrown as `SimulationConfigException` with the original kept as its cause |
| `Main.main()` has `try`/`catch (SimulationConfigException)` | catch | Reports the problem and exits cleanly instead of failing later with an unrelated stack trace |
| `Grid.cellAt()` throws `IndexOutOfBoundsException` | unchecked, standard | An index that does not exist, which means the caller has a bug |
| `Animal.update()` throws `IllegalStateException` | unchecked, standard | An animal acting after death. Guards the invariant that `World` filters the dead out first |

The most important catch is in `Animal.tryBreed()`, and the reason is the ordering:

```java
try {
    Animal child = world.spawnNear(this);   // may throw
    payBreedCost();                         // only runs if it did not
    other.payBreedCost();
    child.health = (breedCost() + other.breedCost()) * 0.6;
} catch (SpawnException e) {
    // No room next to the parent this tick; neither parent pays.
}
```

The exception protects an invariant: an animal never pays the breeding cost without getting
a child. If the spawn fails, control leaves the block before either parent is charged.

Where I deliberately did not use exceptions

1. No `ExtinctionException`. The obvious move is to throw when the last prey animal dies
   and catch it in the main loop to stop the simulation. I did not, because extinction is
   an expected outcome of the simulation, arguably the point of it, and not an error.
   Routing the normal termination path through a mechanism designed for abnormal ones would
   be using exceptions for control flow. `World.getState()` returns a `SimulationState` enum
   instead.

2. I do not catch `ConcurrentModificationException`. The team's original
   `Mouse.tryReproduce()` iterated the live mouse list while spawning into it, and survived
   only because a `break` fired before the next iteration. One added statement would have
   thrown it. Catching it would have hidden a structural bug, so I removed the possibility
   instead: breeding now iterates a fresh list returned by the grid, and births and deaths
   are only applied at tick boundaries.

Reference. *Learning Java*, 3rd edition, Chapter 4, section 4.5 "Exceptions", subsection
"Checked and Unchecked Exceptions", which was the Week 6 reading. It draws the same line I
used: checked exceptions are for problems an application should handle gracefully, while
unchecked ones signal problems it would not normally be expected to recover from.
`SpawnException` is the first kind, and the `IndexOutOfBoundsException` in `Grid.cellAt()`
is the second.

**3.3.** Paste a code snippet showing either a generic class/method or a try/catch block.

```java
/**
 * A fixed grid of cells, each holding any number of T.
 *
 * T is bounded to Entity so the grid can read an occupant's own position
 * instead of being told it separately.
 *
 * Backed by List<List<T>> rather than List<T>[][] because Java cannot create
 * a generic array; the Object[][] alternative needs an unchecked cast on
 * every read, which suppresses the warning without making it safe.
 */
public class Grid<T extends Entity> {
    private final int cols, rows;
    private final List<List<T>> cells;

    /** Out of range is a caller bug, not an expected condition, so this throws unchecked. */
    public List<T> cellAt(int x, int y) {
        if (!contains(x, y)) {
            throw new IndexOutOfBoundsException(
                "cell (" + x + ", " + y + ") is outside " + cols + "x" + rows);
        }
        return cells.get(y * cols + x);
    }

    /** Living occupants within Chebyshev `radius`. Clipped at the edges: scanning near a wall is normal. */
    public List<T> occupantsWithin(int x, int y, int radius, Predicate<? super T> filter) {
        List<T> found = new ArrayList<>();
        forEachCellNear(x, y, radius, occupant -> {
            if (filter.test(occupant)) found.add(occupant);
        });
        return found;
    }

    /** Only the occupants of the given type, without casting at the call site. */
    public <U extends T> List<U> occupantsWithin(int x, int y, int radius, Class<U> type) {
        List<U> found = new ArrayList<>();
        forEachCellNear(x, y, radius, occupant -> {
            if (type.isInstance(occupant)) found.add(type.cast(occupant));
        });
        return found;
    }
}
```

This one class shows three generic constructs: a class-level bounded type parameter
(`T extends Entity`), a method-level parameter bounded by the class's own (`<U extends T>`),
and a bounded wildcard (`Predicate<? super T>`). It also shows the unchecked-versus-clipping
split described in 3.2.

*Learning Java*, 3rd edition, Chapter 8, "Bounds", which was the Week 5 reading, describes a
bound as a constraint on a type parameter, written with `extends`, that limits which types
may be supplied. Here the constraint does real work: without `T extends Entity` the grid
could not call `getX()` on the things it stores.

---

## 4. Log Book

**4.1.** Attach or link your log book entries for Weeks 1–6.

My log book is submitted alongside this worksheet as `COMP2000-Logbook.docx`. It has entries
for Weeks 3, 4, 5, 6 and 7. Weeks 1 and 2 are not included.

The Week 3 entry has supporting evidence in the repository. The photograph of our butcher's
paper class sketch, and the rules we wrote on the day, were committed to the README and are
still there.

**4.2.** Which week's activity taught you the most? What did you learn?

Week 4, because it is the only week where something we decided in the room is still in the
code I am submitting.

We settled on `Animal` as a superclass with each species inheriting from it, and that
arrangement survived everything I did afterwards. What I did not expect is that the question
we could not answer that week ended up mattering more than the one we did. We argued about
how an object knows what else is in its cell, and whether that belongs in the object, in the
cell, or in both kept in step, and we left class without resolving it.

That question is what `Grid<T extends Entity>` answers. The cell holds the list of what is
in it, the animals hold nothing at all about their surroundings, and every question about
what is nearby goes through the grid. My instinct at the time was that storing it in both
places would be the mistake, and I still think that was right. What I could not see then is
that putting it in the cell also collects every spatial question into one class that can be
tested on its own, which is what `GridTest` does.

---

## 5. Uniqueness and Creativity

**5.1.** List everything you added to the project that was not part of the in-class activities.

The in-class activities covered version control, a class sketch, an inheritance hierarchy,
the type-erasure exercise and the barcode exception puzzle. The simulation my team built
from those had a working window, an `Entity`/`Animal`/`Predator`/`Prey` hierarchy, one
generic method and one custom exception. Everything below is what I added on my fork.

Structural

1. `Grid<T extends Entity>`, a generic spatial index with three query overloads, replacing
   whole-world list scans.
2. Grid coordinates. The world moved from continuous `double` positions to a 40×30 integer
   lattice.
3. `SimulationConfig`, which puts all parameters in one validated place, settable from the
   command line as `key=value`.
4. A second custom exception, `SimulationConfigException`, with exception chaining, plus two
   deliberate unchecked guards.
5. Six test harnesses (`GridTest`, `ConfigTest`, `ChaseTest`, `Ablation`, `Series`,
   `Render`) using only standard JRE classes.

Behavioural

6. Per-species movement geometry. The fox moves like a rook, the hawk like a bishop, and the
   hawk rests every fourth tick. Previously the two predators' `act()` methods were
   character-for-character identical apart from the order two lists were concatenated.
7. The knight-hop escape. Threatened prey jump two cells away and one to the side, a move no
   sliding piece can follow in one tick. It costs energy, so a starving animal cannot afford
   it.
8. An energy economy. Breeding costs energy, offspring are built from what their parents
   paid, and one animal breeds at most once per tick.
9. All four species reproduce. Previously only mice did, so predator numbers could only ever
   fall.
10. r/K differentiation. `Rabbit` and `Mouse` were near-duplicates, and now have opposed
    reproductive strategies and different refuge behaviour.
11. A working refuge. Prey run for the safe zone, and predators can no longer eat anything
    standing in it.
12. A seeded random source, so any run can be replayed exactly.
13. Shuffled turn order. `World.update()` iterated hawks, then foxes, then prey in a fixed
    order, which had handed predators the first move on every tick since the project began.

Instrumentation and display

14. Population history and a live graph (`GraphPanel`), with line colours taken from the
    species themselves.
15. Deaths tallied by cause (starved or eaten), plus births and escape attempts.
16. A termination condition, reported as a `SimulationState` enum.
17. A readable board: labelled characters, grid lines, a legend and configurable speed.

Bugs found in the inherited code

18. Prey could never escape. Both predators were strictly faster than even a fed prey, and
    fleeing moved in a straight line, so being detected meant certain death.
19. Prey were constructed with `fedTicks = 0`, so every animal was born at `HUNGRY_SPEED`
    and `FED_SPEED` was dead code.
20. The safe zone protected nothing. `World` ejects predators only after they have acted, so
    a predator could step in, eat, and be pushed back out in the same tick.
21. A latent `ConcurrentModificationException` in `Mouse.tryReproduce()`.
22. The food supply was roughly four times below demand, so every run ended in mass
    starvation regardless of what the predators did.

**5.2.** Which feature required the most independent research or problem-solving? What did you learn from it?

The knight-hop escape, because it did not work when I first wrote it, and it took a lot of thinking to figure out why.

I measured it instead of eyeballing it. `ChaseTest` puts one predator and one prey alone on
the grid with no food and counts how long the prey survives, over 60 runs. The first three
measurements all said 100% capture, and I nearly concluded the mechanic was worthless.
There were four separate causes, found in this order:

1. `World.update()` never shuffled. It iterated hawks, then foxes, then prey in list order,
   so predators moved first on every tick and prey never got to react.
2. `eatDistance` was 1. A speed-2 fox could close two cells and eat in the same tick, an
   effective reach of three against an escape that gains two, so no evasion could ever beat
   it. Predators now have to land exactly on their prey.
3. Prey were born hungry, so nothing ever moved at its fed speed.
4. My test was wrong. After fixing all three I still got 100% capture across seeds 1 to 60.
   The cause was `java.util.Random`: `new Random(n).nextInt(2)` is strongly correlated
   across small consecutive `n`, so every run drew the same coin flip and the fox won the
   shuffle every single time. Spreading the seeds out gave the real numbers immediately.

With those fixed the mechanic does what it was designed to:

| | caught | mean survival |
|---|---|---|
| Fox vs healthy prey | 58% | 66 ticks |
| Fox vs starving prey | 100% | 1 tick |
| Hawk vs healthy prey | 51% | 94 ticks |
| Hawk vs starving prey | 100% | 3 ticks |

Because the hop costs energy, well-fed prey escape and starving prey cannot, so predation
preferentially removes the hungry. Nothing in the code mentions selection. It falls out of
two numbers.

What I learned is more about method than about Java. A simulation will happily produce
confident, stable, completely wrong numbers, and watching it on screen tells you almost
nothing, because the bugs above are all invisible at 20 frames a second. Measuring the
specific thing I claimed to have built is what found them, and the fourth one taught me
that the measuring instrument needs checking too. I also now understand why
`java.util.Random` should be seeded once and centrally, and why simulation results quoted
without a seed are not reproducible results at all.

**5.3.** Paste one code snippet that you are especially proud of. Explain why it goes beyond what was done in class.

```java
/**
 * Knight-hop clear of a predator: two cells directly away, one to the side.
 * A slider cannot follow that in a single tick. Costs HOP_COST, and an
 * animal below a quarter health cannot afford it at all.
 */
protected void evade(Predator threat, World world) {
    if (health < maxHealth * 0.25) {
        moveAwayFrom(threat);
        return;
    }
    Random rng = world.getRandom();
    int dx = getX() - threat.getX();
    int dy = getY() - threat.getY();
    int sideways = rng.nextBoolean() ? 1 : -1;
    if (Math.abs(dx) >= Math.abs(dy)) {
        int away = (dx == 0) ? sideways : Integer.signum(dx);
        setPosition(getX() + 2 * away, getY() + sideways);
    } else {
        int away = (dy == 0) ? sideways : Integer.signum(dy);
        setPosition(getX() + sideways, getY() + 2 * away);
    }
    health -= HOP_COST;
    world.recordHop();
}
```

It is twenty lines, and almost every one of them encodes a design decision instead of just
moving a coordinate.

The geometry is chosen deliberately. A knight's move is the one chess motion that no sliding
piece can answer in a single turn, because it lands off every rank, file and diagonal the
pursuer occupies. Since I had already made the fox a rook and the hawk a bishop, this is the
exact shape that forces a pursuer to spend a tick reorienting. The sideways component is
randomised so the escape cannot be anticipated, and it is drawn from the world's seeded
source so the run stays reproducible.

The energy gate is the part I like most. Because the hop costs 8 health and an animal below
a quarter of its maximum falls back to simply backing away, being well fed and being able to
escape end up as the same condition. That single `if` is what turns predation from a random
tax into a selective pressure. Measured over 60 runs, a healthy rabbit survives a fox for 66
ticks on average and a starving one for 1.

It goes beyond the class activities because those were about mechanism: writing a subclass,
causing a `ClassCastException`, making an exception propagate a set number of frames. This
one is about consequence. Three separate rules (the hop, its cost, and the rule that speed
follows health) interact to produce a behaviour that is nowhere written down. The result I
did not expect at all is that mice on their own peak at a population of 130, but alongside
rabbits they peak at 68 and then die out, while the two species together sustain a run
longer than either does alone. That is competitive exclusion, and the word "competition"
appears nowhere in the codebase.
