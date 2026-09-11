import java.util.List;
import java.util.Random;

/**
 * Animal is-a Entity that moves and spends energy. It factors out the
 * behaviour every animal shares (movement, vision, metabolism) so that
 * Predator and Prey only need to add what makes them different.
 *
 * findNearest() is generic: it works on a List<Hawk>, List<Prey>, or any
 * mixed list, so hunting and fleeing logic reuse one search.
 *
 * moveToward()/moveAwayFrom() are each overloaded: one takes an Entity,
 * the other a raw (x, y) cell.
 */
public abstract class Animal extends Entity {
    protected double health;
    protected int speed;
    protected int visionRadius;

    public Animal(int x, int y, double health, int speed, int visionRadius) {
        super(x, y);
        this.health = health;
        this.speed = speed;
        this.visionRadius = visionRadius;
    }

    public double getHealth() { return health; }

    protected void moveToward(Entity target) {
        moveToward(target.getX(), target.getY());
    }

    /** Overload: move toward a raw cell instead of a whole Entity. */
    protected void moveToward(int targetX, int targetY) {
        int[] d = stepToward(targetX - getX(), targetY - getY());
        setPosition(getX() + d[0], getY() + d[1]);
    }

    protected void moveAwayFrom(Entity threat) {
        moveAwayFrom(threat.getX(), threat.getY());
    }

    /** Overload: flee a raw cell instead of a whole Entity. */
    protected void moveAwayFrom(int threatX, int threatY) {
        int[] d = stepToward(getX() - threatX, getY() - threatY);
        setPosition(getX() + d[0], getY() + d[1]);
    }

    /**
     * How this animal covers a (dx, dy) gap in one tick, as {stepX, stepY}.
     * The default is free movement in any of 8 directions; Fox and Hawk
     * override it with their own restricted movement.
     */
    protected int[] stepToward(int dx, int dy) {
        return new int[]{ clampStep(dx), clampStep(dy) };
    }

    /** At most `speed` cells along an axis, never overshooting. */
    protected int clampStep(int delta) {
        return Math.max(-speed, Math.min(speed, delta));
    }

    /** Takes the World to reach its seeded Random. */
    protected void wander(World world) {
        Random rng = world.getRandom();
        setPosition(getX() + rng.nextInt(3) - 1, getY() + rng.nextInt(3) - 1);
    }

    /** Generic search: finds the closest living candidate within vision range. */
    protected <T extends Entity> T findNearest(List<T> candidates) {
        T nearest = null;
        int bestDist = Integer.MAX_VALUE;
        for (T candidate : candidates) {
            if (!candidate.isAlive()) continue;
            int d = this.distanceTo(candidate);
            if (d < bestDist) {
                bestDist = d;
                nearest = candidate;
            }
        }
        return (bestDist <= visionRadius) ? nearest : null;
    }

    @Override
    public void update(World world) {
        health -= 0.1;
        if (health <= 0) {
            kill();
            return;
        }
        act(world);
    }

    /** Each concrete animal decides what "acting" means for it. */
    protected abstract void act(World world);

    @Override
    public String toString() {
        return super.toString() + String.format(", health %.0f", health);
    }
}
