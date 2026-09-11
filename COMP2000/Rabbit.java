import java.awt.Color;
import java.awt.Graphics;

/**
 * Rabbits are the K-strategist prey: bigger, further-sighted, worth more to a
 * predator, and slower to reproduce because they breed later and pay more.
 */
public class Rabbit extends Prey {
    public Rabbit(int x, int y) {
        super(x, y, 100, 4, 60); // startHealth, visionRadius, nutritionValue
    }

    @Override
    protected Animal newOffspring(int x, int y) { return new Rabbit(x, y); }

    @Override
    protected double breedThreshold() { return maxHealth * 0.35; }

    @Override
    protected double breedCost() { return maxHealth * 0.25; }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(px() - 5, py() - 5, 10, 10);
    }

    @Override
    public Color getColor() { return Color.WHITE; }
}
