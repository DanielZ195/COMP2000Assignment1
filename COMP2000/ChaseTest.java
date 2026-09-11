public class ChaseTest {
    /** One predator, one prey, no food. Ticks until caught (max = escaped). */
    static int survives(String kind, boolean starving, long seed, int maxTicks) {
        World w = new World(40, 30, seed);
        Rabbit r = new Rabbit(20, 15);
        if (starving) r.health = 2.5;   // below HOP_COST: cannot afford to knight-hop
        w.addRabbit(r);
        if (kind.equals("fox")) w.addFox(new Fox(18, 15)); else w.addHawk(new Hawk(18, 15));
        for (int t = 1; t <= maxTicks; t++) {
            w.update();
            if (w.getRabbits().isEmpty()) return t;
        }
        return maxTicks;
    }
    public static void main(String[] a) {
        int max = 150, runs = 60;
        for (String kind : new String[]{"fox", "hawk"}) {
            for (boolean starving : new boolean[]{false, true}) {
                int caught = 0, total = 0;
                for (long s = 1; s <= runs; s++) {
                    int t = survives(kind, starving, s * 1000003L + 12345L, max);
                    total += t;
                    if (t < max) caught++;
                }
                System.out.printf("%-4s vs %-8s prey:  caught %2d/%d (%3d%%)   avg survival %3d ticks%n",
                    kind, starving ? "STARVING" : "healthy", caught, runs,
                    caught * 100 / runs, total / runs);
            }
        }
    }
}
