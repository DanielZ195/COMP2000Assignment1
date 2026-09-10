import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


public class Hawk extends Predator {
    public Hawk(double x, double y) {
        super(x, y, 100, 3.0, 120);
    }

    @Override
    protected void act(World world) {
        List<Prey> targets = new ArrayList<>();
        targets.addAll(world.getMice());
        targets.addAll(world.getRabbits());
        Prey target = findNearest(targets);
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
        g.fillPolygon(new int[]{(int) getX(), (int) getX() - 6, (int) getX() + 6},
                      new int[]{(int) getY() - 8, (int) getY() + 6, (int) getY() + 6}, 3);
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
