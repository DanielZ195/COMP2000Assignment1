import java.util.List;


public abstract class Animal extends Entity {
    protected double health;
    protected double speed;
    protected double visionRadius;

    public Animal(double x, double y, double health, double speed, double visionRadius) {
        super(x, y);
        this.health = health;
        this.speed = speed;
        this.visionRadius = visionRadius;
    }

    public double getHealth() { return health; }

    protected void moveToward(Entity target) {
        moveToward(target.getX(), target.getY());
    }

    protected void moveToward(double targetX, double targetY) {
        double dx = targetX - getX();
        double dy = targetY - getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 0.001) {
            setPosition(getX() + (dx / dist) * speed, getY() + (dy / dist) * speed);
        }
    }

    protected void moveAwayFrom(Entity threat) {
        moveAwayFrom(threat.getX(), threat.getY());
    }

    protected void moveAwayFrom(double threatX, double threatY) {
        double dx = getX() - threatX;
        double dy = getY() - threatY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 0.001) {
            setPosition(getX() + (dx / dist) * speed, getY() + (dy / dist) * speed);
        }
    }

    /** Takes the World to reach its seeded Random. */
    protected void wander(World world) {
        java.util.Random rng = world.getRandom();
        setPosition(getX() + (rng.nextDouble() - 0.5) * speed, getY() + (rng.nextDouble() - 0.5) * speed);
    }

    protected <T extends Entity> T findNearest(List<T> candidates) {
        T nearest = null;
        double bestDist = Double.MAX_VALUE;
        for (T candidate : candidates) {
            if (!candidate.isAlive()) continue;
            double d = this.distanceTo(candidate);
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

    protected abstract void act(World world);

    @Override
    public String toString() {
        return super.toString() + String.format(", health %.0f", health);
    }
}
