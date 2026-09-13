import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class SimPanel extends JPanel {
    /** Pixels per grid cell. */
    public static final int CELL_SIZE = 18;

    private final World world;

    public SimPanel(World world) {
        this.world = world;
        setBackground(new Color(30, 30, 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Faint lattice so the discrete cell-by-cell movement is visible.
        g.setColor(new Color(48, 48, 48));
        for (int x = 0; x <= world.getWidth(); x++)
            g.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, world.getHeight() * CELL_SIZE);
        for (int y = 0; y <= world.getHeight(); y++)
            g.drawLine(0, y * CELL_SIZE, world.getWidth() * CELL_SIZE, y * CELL_SIZE);

        // Safe zone: predators cannot enter this rectangle.
        if (world.hasSafeZone()) {
        g.setColor(new Color(80, 160, 255, 40));
        g.fillRect(world.getZoneX() * CELL_SIZE, world.getZoneY() * CELL_SIZE,
                   world.getZoneWidth() * CELL_SIZE, world.getZoneHeight() * CELL_SIZE);
        g.setColor(new Color(80, 160, 255));
        g.drawRect(world.getZoneX() * CELL_SIZE, world.getZoneY() * CELL_SIZE,
                   world.getZoneWidth() * CELL_SIZE, world.getZoneHeight() * CELL_SIZE);
        g.drawString("Safe zone", world.getZoneX() * CELL_SIZE + 6, world.getZoneY() * CELL_SIZE + 14);
        }

        for (Entity e : world.allEntities()) {
            if (e.isAlive()) {
                e.draw(g);
            }
        }
        drawCounts(g);
    }

    /** Counts sit on their own panel so they never hide an entity underneath. */
    private void drawCounts(Graphics g) {
        String[] rows = {
            "H  Hawks    " + world.getHawks().size(),
            "F  Foxes    " + world.getFoxes().size(),
            "R  Rabbits  " + world.getRabbits().size(),
            "m  Mice     " + world.getMice().size(),
            "*  Food     " + world.getFood().size(),
        };
        Color[] colours = { new Hawk(0,0).getColor(), new Fox(0,0).getColor(),
                            new Rabbit(0,0).getColor(), new Mouse(0,0).getColor(),
                            new Food(0,0).getColor() };
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(4, 4, 124, 5 * 16 + 8);
        g.setColor(new Color(90, 90, 90));
        g.drawRect(4, 4, 124, 5 * 16 + 8);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        for (int i = 0; i < rows.length; i++) {
            g.setColor(colours[i]);
            g.drawString(rows[i], 12, 20 + i * 16);
        }
    }
}
