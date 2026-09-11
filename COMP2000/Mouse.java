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
    private int reproduceCooldown = 0;
    private static final int CONTACT_DISTANCE = 1;
    private static final int COOLDOWN_TICKS = 50;

    public Mouse(int x, int y) {
        super(x, y, 40, 3, 20); // health, visionRadius, nutritionValue (worth to a predator)
    }

    @Override
    protected void act(World world) {
        updateSpeed();

        if (reproduceCooldown > 0) {
            reproduceCooldown--;
        }

        Predator threat = findNearest(world.getGrid().occupantsWithin(getX(), getY(), visionRadius, Predator.class));
        if (threat != null) {
            moveAwayFrom(threat);
        } else {
            Food food = findNearest(world.getGrid().occupantsWithin(getX(), getY(), visionRadius, Food.class));
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

        List<Mouse> mice = world.getGrid().occupantsWithin(getX(), getY(), CONTACT_DISTANCE, Mouse.class);
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
        g.fillOval(px() - 3, py() - 3, 6, 6);
    }

    @Override
    public Color getColor() {
        return Color.GRAY;
    }
}
