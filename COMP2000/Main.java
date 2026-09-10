import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Dimension;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int width = 800, height = 600;

        // Pass a seed as args[0] to replay a run.
        long seed = (args.length > 0) ? Long.parseLong(args[0]) : System.currentTimeMillis();
        World world = new World(width, height, seed);
        System.out.println("Simulation seed: " + seed + "  (re-run with: java Main " + seed + ")");

        Random rng = world.getRandom();

        for (int i = 0; i < 2; i++) {
            double[] p = randomPointOutsideZone(world);
            world.addHawk(new Hawk(p[0], p[1]));
        }
        for (int i = 0; i < 3; i++) {
            double[] p = randomPointOutsideZone(world);
            world.addFox(new Fox(p[0], p[1]));
        }
        for (int i = 0; i < 15; i++) world.addRabbit(new Rabbit(rng.nextDouble() * width, rng.nextDouble() * height));
        for (int i = 0; i < 20; i++) world.addMouse(new Mouse(rng.nextDouble() * width, rng.nextDouble() * height));
        for (int i = 0; i < 30; i++) world.addFood(new Food(rng.nextDouble() * width, rng.nextDouble() * height));

        SimPanel panel = new SimPanel(world);
        panel.setPreferredSize(new Dimension(width, height));

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

    private static double[] randomPointOutsideZone(World world) {
        Random rng = world.getRandom();
        double x, y;
        do {
            x = rng.nextDouble() * world.getWidth();
            y = rng.nextDouble() * world.getHeight();
        } while (world.isInSafeZone(x, y));
        return new double[]{x, y};
    }
}
