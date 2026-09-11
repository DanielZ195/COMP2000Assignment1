import java.awt.Color;
import java.awt.Graphics;

/** Foxes are slower and shorter-sighted than hawks, and can only run down one axis at a time. */
public class Fox extends Predator {
    public Fox(int x, int y) {
        super(x, y, 150, 2, 3);
    }

    @Override
    protected Animal newOffspring(int x, int y) { return new Fox(x, y); }

    /** Rook: moves on one axis at a time, so changing direction costs a tick. */
    @Override
    protected int[] stepToward(int dx, int dy) {
        if (Math.abs(dx) >= Math.abs(dy)) return new int[]{ clampStep(dx), 0 };
        return new int[]{ 0, clampStep(dy) };
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(px() - 6, py() - 4, 12, 8);
    }

    @Override
    public Color getColor() { return Color.ORANGE; }
}
