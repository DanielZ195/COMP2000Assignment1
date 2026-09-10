import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

public class SimPanel extends JPanel {
    /** Pixels per grid cell. */
    public static final int CELL_SIZE = 20;

    private final World world;

    public SimPanel(World world) {
        this.world = world;
        setBackground(new Color(30, 30, 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Safe zone: predators cannot enter this rectangle.
        g.setColor(new Color(80, 160, 255, 40));
        g.fillRect(world.getZoneX() * CELL_SIZE, world.getZoneY() * CELL_SIZE,
                   world.getZoneWidth() * CELL_SIZE, world.getZoneHeight() * CELL_SIZE);
        g.setColor(new Color(80, 160, 255));
        g.drawRect(world.getZoneX() * CELL_SIZE, world.getZoneY() * CELL_SIZE,
                   world.getZoneWidth() * CELL_SIZE, world.getZoneHeight() * CELL_SIZE);
        g.drawString("Safe zone", world.getZoneX() * CELL_SIZE + 6, world.getZoneY() * CELL_SIZE + 14);

        for (Entity e : world.allEntities()) {
            if (e.isAlive()) {
                e.draw(g);
            }
        }
        g.setColor(Color.WHITE);
        g.drawString("Hawks: " + world.getHawks().size(), 10, 15);
        g.drawString("Foxes: " + world.getFoxes().size(), 10, 30);
        g.drawString("Rabbits: " + world.getRabbits().size(), 10, 45);
        g.drawString("Mice: " + world.getMice().size(), 10, 60);
        g.drawString("Food: " + world.getFood().size(), 10, 75);
    }
}
