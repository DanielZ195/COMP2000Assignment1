public class ConfigTest {
    static int fails = 0;
    static void check(String name, boolean ok) {
        System.out.println((ok ? "  PASS  " : "  FAIL  ") + name);
        if (!ok) fails++;
    }
    static String rejects(String... args) {
        try { SimulationConfig.fromArgs(args); return null; }
        catch (SimulationConfigException e) { return e.getMessage(); }
    }
    static boolean accepts(String... args) {
        try { SimulationConfig.fromArgs(args); return true; }
        catch (SimulationConfigException e) { return false; }
    }
    public static void main(String[] a) throws Exception {
        check("defaults are valid",                 accepts());
        check("seed passes through",                SimulationConfig.fromArgs(new String[]{"seed=42"}).getSeed() == 42);
        check("override applies",                   SimulationConfig.fromArgs(new String[]{"rabbits=7"}).getRabbits() == 7);

        check("rejects non-numeric value",          rejects("rabbits=lots") != null);
        check("  ...and keeps the cause",           causeOf("rabbits=lots") instanceof NumberFormatException);
        check("rejects unknown key",                rejects("wolves=3") != null);
        check("rejects malformed arg",              rejects("justtext") != null);
        check("rejects zero grid",                  rejects("cols=0") != null);
        check("rejects negative population",        rejects("rabbits=-1") != null);
        check("rejects overcrowded grid",           rejects("cols=5", "rows=5", "rabbits=100") != null);
        check("rejects a world with no prey",       rejects("rabbits=0", "mice=0") != null);
        check("rejects maxFood below foodPerTick",  rejects("maxFood=1", "foodPerTick=5") != null);

        check("dead animal cannot act",             deadAnimalThrows());
        check("grid rejects bad index",             gridThrows());

        System.out.println(fails == 0 ? "\nALL PASS" : "\n" + fails + " FAILURES");
        if (fails > 0) System.exit(1);
    }
    static Throwable causeOf(String arg) {
        try { SimulationConfig.fromArgs(new String[]{arg}); return null; }
        catch (SimulationConfigException e) { return e.getCause(); }
    }
    static boolean deadAnimalThrows() {
        World w = new World(40, 30, 1);
        Rabbit r = new Rabbit(5, 5);
        r.kill(DeathCause.STARVED);
        try { r.update(w); return false; } catch (IllegalStateException e) { return true; }
    }
    static boolean gridThrows() {
        try { new Grid<Entity>(10, 10).cellAt(99, 0); return false; }
        catch (IndexOutOfBoundsException e) { return true; }
    }
}
