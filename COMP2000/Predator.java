import java.util.List;

/**
 * Predator is-a Animal that can eat Prey. Hawk and Fox both extend this so
 * they share hunting and eating logic but move differently.
 *
 * tryEat() takes a Prey (not just any Animal) because only Prey is Edible -
 * the health gained comes from the prey's own getNutritionValue().
 */
public abstract class Predator extends Animal {
    protected int eatDistance = 0;

    public Predator(int x, int y, double startHealth, int speed, int visionRadius) {
        super(x, y, startHealth, speed, visionRadius);
    }

    @Override
    protected double breedThreshold() { return maxHealth * 0.75; }

    /**
     * The refuge check matters: World ejects predators from the safe zone only
     * after they have already acted, so without this a predator could step in,
     * eat, and be pushed back out in the same tick.
     */
    protected boolean tryEat(Prey prey, World world) {
        if (world.isInSafeZone(prey == null ? -1 : prey.getX(),
                               prey == null ? -1 : prey.getY())) return false;
        if (prey != null && prey.isAlive() && distanceTo(prey) <= eatDistance) {
            prey.kill();
            feed(prey.getNutritionValue());
            return true;
        }
        return false;
    }

    /** Hunt when hungry, otherwise look for a mate, otherwise wander. */
    @Override
    protected void act(World world) {
        tryBreed(world);

        List<Prey> visible =
            world.getGrid().occupantsWithin(getX(), getY(), visionRadius, Prey.class);
        visible.removeIf(p -> world.isInSafeZone(p.getX(), p.getY()));
        Prey target = findNearest(visible);
        if (target != null && !isWellFed()) {
            moveToward(target);
            tryEat(target, world);
            return;
        }

        if (canBreed()) {
            Animal mate = nearestMate(world);
            if (mate != null) {
                moveToward(mate);
                return;
            }
        }

        if (target != null) {
            moveToward(target);
            tryEat(target, world);
        } else {
            wander(world);
        }
    }
}
