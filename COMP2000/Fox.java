import java.awt.Color;
import java.awt.Graphics;


public class Fox extends Predator {
    public Fox(int x, int y) {
        super(x, y, 100, 2, 4);
    }

    @Override
    protected void act(World world) {
        Prey target = findNearest(world.getGrid().occupantsWithin(getX(), getY(), visionRadius, Prey.class));
        if (target != null) {
            moveToward(target);
            tryEat(target);
        } else {
            wander(world);
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(px() - 6, py() - 4, 12, 8);
    }

    @Override
    public Color getColor() {
        return Color.ORANGE;
    }
}
