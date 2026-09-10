import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;


public class Hawk extends Predator {
    public Hawk(int x, int y) {
        super(x, y, 100, 3, 6);
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
        g.fillPolygon(new int[]{px(), px() - 6, px() + 6},
                      new int[]{py() - 8, py() + 6, py() + 6}, 3);
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
