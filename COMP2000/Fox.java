import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


public class Fox extends Predator {
    public Fox(int x, int y) {
        super(x, y, 100, 2, 4);
    }

    @Override
    protected void act(World world) {
        List<Prey> targets = new ArrayList<>();
        targets.addAll(world.getRabbits());
        targets.addAll(world.getMice());
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
        g.fillOval(px() - 6, py() - 4, 12, 8);
    }

    @Override
    public Color getColor() {
        return Color.ORANGE;
    }
}
