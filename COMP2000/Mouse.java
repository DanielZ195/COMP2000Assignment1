import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

/**
 * Mice behave like Rabbits (flee predators, eat food) but also reproduce:
 * when two mice that are both off cooldown come within CONTACT_DISTANCE of
 * each other, a new Mouse is spawned near them and both parents get a
 * cooldown so the population doesn't explode every single tick.
 */
public class Mouse extends Prey {
    private double reproduceCooldown = 0;
    private static final double CONTACT_DISTANCE = 10;
    private static final double COOLDOWN_TICKS = 50;

    public Mouse(double x, double y) {
        super(x, y, 40, 70, 20); // health, visionRadius, nutritionValue (worth to a predator)
    }

    @Override
    protected void act(World world) {
        updateSpeed();

        if (reproduceCooldown > 0) {
            reproduceCooldown--;
        }

        Predator threat = findNearest(nearbyPredators(world));
        if (threat != null) {
            moveAwayFrom(threat);
        } else {
            Food food = findNearest(world.getFood());
            if (food != null) {
                moveToward(food);
                tryEatFood(food);
            } else {
                wander(world);
            }
        }

        tryReproduce(world);
    }

    private void tryReproduce(World world) {
        if (reproduceCooldown > 0) return;

        List<Mouse> mice = world.getMice();
        for (Mouse other : mice) {
            if (other == this || !other.isAlive()) continue;
            if (other.reproduceCooldown > 0) continue;

            if (distanceTo(other) <= CONTACT_DISTANCE) {
                try {
                    world.spawnMouseNear(this);
                    this.reproduceCooldown = COOLDOWN_TICKS;
                    other.reproduceCooldown = COOLDOWN_TICKS;
                } catch (SpawnException e) {
                    // No valid spot this tick (e.g. too close to the world edge) - just skip.
                    System.out.println("Reproduction skipped: " + e.getMessage());
                }
                break;
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval((int) getX() - 3, (int) getY() - 3, 6, 6);
    }

    @Override
    public Color getColor() {
        return Color.GRAY;
    }
}
