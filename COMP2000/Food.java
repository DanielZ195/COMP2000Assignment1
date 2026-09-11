import java.awt.Color;
import java.awt.Graphics;


public class Food extends Entity implements Edible {
    private static final double NUTRITION_VALUE = 40;

    public Food(int x, int y) {
        super(x, y);
    }

    @Override
    public void update(World world) {
       
    }

    @Override
    public double getNutritionValue() {
        return NUTRITION_VALUE;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillOval(px() - 3, py() - 3, 6, 6);
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }
}
