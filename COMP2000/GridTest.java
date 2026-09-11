import java.util.List;
public class GridTest {
    static int fails = 0;
    static void check(String name, boolean ok) {
        System.out.println((ok ? "  PASS  " : "  FAIL  ") + name);
        if (!ok) fails++;
    }
    public static void main(String[] a) {
        Grid<Entity> g = new Grid<>(40, 30);
        Rabbit r1 = new Rabbit(10, 10);
        Rabbit r2 = new Rabbit(12, 10);   // 2 cells away
        Rabbit r3 = new Rabbit(20, 20);   // far
        Fox    f1 = new Fox(11, 11);      // adjacent
        Food   fd = new Food(10, 11);
        for (Entity e : new Entity[]{r1, r2, r3, f1, fd}) g.add(e);

        check("radius 1 finds 3 (self, fox, food)", g.occupantsWithin(10,10,1).size() == 3);
        check("radius 2 finds 4 (adds r2)",         g.occupantsWithin(10,10,2).size() == 4);
        check("radius 30 finds all 5",              g.occupantsWithin(10,10,30).size() == 5);

        List<Prey> prey = g.occupantsWithin(10,10,2, Prey.class);
        check("type filter returns only Prey (2)",  prey.size() == 2);
        List<Predator> preds = g.occupantsWithin(10,10,2, Predator.class);
        check("Predator.class matches Fox subclass", preds.size() == 1 && preds.get(0) == f1);

        check("predicate overload filters",
              g.occupantsWithin(10,10,2, e -> e instanceof Food).size() == 1);

        r2.kill();
        check("dead occupants excluded",            g.occupantsWithin(10,10,2).size() == 3);

        check("edge clipping at (0,0) no crash",    g.occupantsWithin(0,0,5) != null);
        check("contains() rejects out of range",    !g.contains(40,0) && !g.contains(-1,5));

        boolean threw = false;
        try { g.cellAt(40, 0); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("cellAt out of range throws unchecked", threw);

        g.clear();
        check("clear() empties grid",               g.occupantsWithin(10,10,30).isEmpty());

        System.out.println(fails == 0 ? "\nALL PASS" : "\n" + fails + " FAILURES");
        if (fails > 0) System.exit(1);
    }
}
