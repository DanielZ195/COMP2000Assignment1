import java.awt.Color;
import java.awt.Graphics;

/** Hawks are fast, long-sighted hunters, but they soar every fourth tick instead of acting. */
public class Hawk extends Predator {
    private int restCounter = 0;

    public Hawk(int x, int y) {
        super(x, y, 150, 3, 5);
    }

    @Override
    protected Animal newOffspring(int x, int y) { return new Hawk(x, y); }

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
        if (++restCounter % 4 == 0) return;
        super.act(world);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillPolygon(new int[]{px(), px() - 6, px() + 6},
                      new int[]{py() - 8, py() + 6, py() + 6}, 3);
    }

    @Override
    public Color getColor() { return Color.RED; }
}
