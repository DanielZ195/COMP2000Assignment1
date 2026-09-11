import java.awt.Color;
import java.awt.Graphics;


public class Hawk extends Predator {
    private int restCounter = 0;

    public Hawk(int x, int y) {
        super(x, y, 100, 3, 6);
    }

    /** Bishop: always diagonal, never straight, so it cannot track an axis-aligned target exactly. */
    @Override
    protected int[] stepToward(int dx, int dy) {
        int sx = (dx == 0) ? 1 : Integer.signum(dx);
        int sy = (dy == 0) ? 1 : Integer.signum(dy);
        int m = Math.min(speed, Math.max(1, Math.max(Math.abs(dx), Math.abs(dy))));
        return new int[]{ sx * m, sy * m };
    }

    @Override
    protected void act(World world) {
        if (++restCounter % 4 == 0) return; // soars every fourth tick
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
        g.fillPolygon(new int[]{px(), px() - 6, px() + 6},
                      new int[]{py() - 8, py() + 6, py() + 6}, 3);
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
