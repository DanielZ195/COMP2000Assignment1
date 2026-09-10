import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Dimension;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int cols = 40, rows = 30;

        // Pass a seed as args[0] to replay a run.
        long seed = (args.length > 0) ? Long.parseLong(args[0]) : System.currentTimeMillis();
        World world = new World(cols, rows, seed);
        System.out.println("Simulation seed: " + seed + "  (re-run with: java Main " + seed + ")");

        Random rng = world.getRandom();

        for (int i = 0; i < 2; i++) {
            int[] p = randomPointOutsideZone(world);
            world.addHawk(new Hawk(p[0], p[1]));
        }
        for (int i = 0; i < 3; i++) {
            int[] p = randomPointOutsideZone(world);
            world.addFox(new Fox(p[0], p[1]));
        }
        for (int i = 0; i < 15; i++) world.addRabbit(new Rabbit(rng.nextInt(cols), rng.nextInt(rows)));
        for (int i = 0; i < 20; i++) world.addMouse(new Mouse(rng.nextInt(cols), rng.nextInt(rows)));
        for (int i = 0; i < 30; i++) world.addFood(new Food(rng.nextInt(cols), rng.nextInt(rows)));

        SimPanel panel = new SimPanel(world);
        panel.setPreferredSize(new Dimension(cols * SimPanel.CELL_SIZE, rows * SimPanel.CELL_SIZE));

        JFrame frame = new JFrame("Predator and Prey Simulation - COMP2000");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

      
        Timer timer = new Timer(50, e -> {
            world.update();
            panel.repaint();
        });
        timer.start();
    }

    private static int[] randomPointOutsideZone(World world) {
        Random rng = world.getRandom();
        int x, y;
        do {
            x = rng.nextInt(world.getWidth());
            y = rng.nextInt(world.getHeight());
        } while (world.isInSafeZone(x, y));
        return new int[]{x, y};
    }
}
