import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * World owns every entity and drives the simulation. It stores each kind
 * of entity in its own typed List (List<Hawk>, List<Mouse>, etc.) so
 * animals can query "give me all the mice" without casting, and it also
 * offers allEntities() as a single List<Entity> for generic operations
 * like drawing or the main update loop. width/height are in cells.
 *
 * World owns the one seeded Random the simulation draws from, so a given
 * seed always replays the same run.
 */
public class World {
    private final int width, height;
    private final long seed;
    private final Random rng;
    private final List<Hawk> hawks = new ArrayList<>();
    private final List<Fox> foxes = new ArrayList<>();
    private final List<Rabbit> rabbits = new ArrayList<>();
    private final List<Mouse> mice = new ArrayList<>();
    private final List<Food> food = new ArrayList<>();
    private final Grid<Entity> grid;
    private int tickCount = 0;

    // A rectangular refuge that Predators are physically barred from entering.
    // Rabbit and Mouse are ordinary Prey, so nothing stops them going in.
    private final int zoneX, zoneY, zoneWidth, zoneHeight;

    public World(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        // Mixed, not passed straight in: new Random(n) for small consecutive n
        // produces near-identical first draws, so seeds 1 and 2 would open alike.
        this.rng = new Random(seed * 6364136223846793005L + 1442695040888963407L);
        this.grid = new Grid<>(width, height);
        this.zoneWidth = (int) (width * 0.22);
        this.zoneHeight = (int) (height * 0.3);
        this.zoneX = width - zoneWidth - 1;
        this.zoneY = height - zoneHeight - 1;
    }

    public Grid<Entity> getGrid() { return grid; }
    public Random getRandom() { return rng; }
    public long getSeed() { return seed; }

    public boolean isInSafeZone(int px, int py) {
        return px >= zoneX && px <= zoneX + zoneWidth
            && py >= zoneY && py <= zoneY + zoneHeight;
    }

    public int getZoneX() { return zoneX; }
    public int getZoneY() { return zoneY; }
    public int getZoneWidth() { return zoneWidth; }
    public int getZoneHeight() { return zoneHeight; }

    public List<Hawk> getHawks() { return hawks; }
    public List<Fox> getFoxes() { return foxes; }
    public List<Rabbit> getRabbits() { return rabbits; }
    public List<Mouse> getMice() { return mice; }
    public List<Food> getFood() { return food; }

    public void addHawk(Hawk h) { hawks.add(h); }
    public void addFox(Fox f) { foxes.add(f); }
    public void addRabbit(Rabbit r) { rabbits.add(r); }
    public void addMouse(Mouse m) { mice.add(m); }
    public void addFood(Food f) { food.add(f); }

    /** Throws SpawnException if the new point would land outside the world. */
    public void spawnMouseNear(Mouse parent) throws SpawnException {
        int x = parent.getX() + rng.nextInt(3) - 1;
        int y = parent.getY() + rng.nextInt(3) - 1;
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new SpawnException("Spawn point (" + x + ", " + y + ") is out of bounds");
        }
        mice.add(new Mouse(x, y));
    }

    public List<Entity> allEntities() {
        List<Entity> all = new ArrayList<>();
        all.addAll(hawks);
        all.addAll(foxes);
        all.addAll(rabbits);
        all.addAll(mice);
        all.addAll(food);
        return all;
    }

    public void update() {
        tickCount++;
        rebuildGrid();

        // Shuffled every tick: allEntities() returns hawks, then foxes, then
        // prey, so a fixed order would hand predators the first move forever.
        List<Entity> actors = allEntities();
        Collections.shuffle(actors, rng);

        for (Entity e : actors) {
            if (e.isAlive()) {
                e.update(this);
                clampToBounds(e);
                keepPredatorsOutOfZone(e);
            }
        }
        removeDead();

        if (tickCount % 30 == 0) {
            food.add(new Food(rng.nextInt(width), rng.nextInt(height)));
        }
    }

    /**
     * Rebuilt once per tick, before anything moves, so every animal perceives
     * the same world state regardless of where it falls in the update order.
     */
    private void rebuildGrid() {
        grid.clear();
        for (Entity e : allEntities()) {
            if (e.isAlive() && grid.contains(e.getX(), e.getY())) grid.add(e);
        }
    }

    /** setPosition() is protected on Entity, but World is a same-package collaborator, so it can call it. */
    private void clampToBounds(Entity e) {
        int clampedX = Math.max(0, Math.min(e.getX(), width - 1));
        int clampedY = Math.max(0, Math.min(e.getY(), height - 1));
        e.setPosition(clampedX, clampedY);
    }

    /** If a Predator ended its move inside the safe zone, push it back out to the nearest edge. */
    private void keepPredatorsOutOfZone(Entity e) {
        if (!(e instanceof Predator)) return;
        if (!isInSafeZone(e.getX(), e.getY())) return;

        int distLeft = e.getX() - zoneX;
        int distRight = (zoneX + zoneWidth) - e.getX();
        int distTop = e.getY() - zoneY;
        int distBottom = (zoneY + zoneHeight) - e.getY();
        int nearest = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));

        if (nearest == distLeft) e.setPosition(zoneX - 1, e.getY());
        else if (nearest == distRight) e.setPosition(zoneX + zoneWidth + 1, e.getY());
        else if (nearest == distTop) e.setPosition(e.getX(), zoneY - 1);
        else e.setPosition(e.getX(), zoneY + zoneHeight + 1);
    }

    private void removeDead() {
        hawks.removeIf(h -> !h.isAlive());
        foxes.removeIf(f -> !f.isAlive());
        rabbits.removeIf(r -> !r.isAlive());
        mice.removeIf(m -> !m.isAlive());
        food.removeIf(f -> !f.isAlive());
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
