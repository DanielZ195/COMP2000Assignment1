import java.awt.Color;
import java.awt.Graphics;

/**
 * Mice are the r-strategist prey: smaller, shorter-sighted and worth less to a
 * predator, but they breed earlier and more cheaply, so they recover fastest
 * after a crash.
 */
public class Mouse extends Prey {
    public Mouse(int x, int y) {
        super(x, y, 80, 3, 40); // startHealth, visionRadius, nutritionValue
    }

    @Override
    protected Animal newOffspring(int x, int y) { return new Mouse(x, y); }

    /** Small enough to know every way back to cover, so it breaks for the refuge earlier. */
    @Override
    protected int refugeRange() { return 22; }

    @Override
    protected double breedThreshold() { return maxHealth * 0.22; }

    @Override
    protected double breedCost() { return maxHealth * 0.15; }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(px() - 3, py() - 3, 6, 6);
    }

    @Override
    public Color getColor() { return Color.GRAY; }
}
