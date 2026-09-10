import java.awt.Color;
import java.awt.Graphics;

/** Rabbits flee from any predator in range, otherwise seek food, otherwise wander. */
public class Rabbit extends Prey {
    public Rabbit(int x, int y) {
        super(x, y, 60, 4, 35); // health, visionRadius, nutritionValue (worth to a predator)
    }

    @Override
    protected void act(World world) {
        updateSpeed();
        Predator threat = findNearest(nearbyPredators(world));
        if (threat != null) {
            moveAwayFrom(threat);
            return;
        }
        Food food = findNearest(world.getFood());
        if (food != null) {
            moveToward(food);
            tryEatFood(food);
        } else {
            wander(world);
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(px() - 5, py() - 5, 10, 10);
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }
}
