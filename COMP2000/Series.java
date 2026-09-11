public class Series {
    public static void main(String[] a) {
        long seed = Long.parseLong(a[0]);
        World w = new World(40,30,seed);
        java.util.Random r = w.getRandom();
        for (int i=0;i<2;i++)  w.addHawk(new Hawk(r.nextInt(40), r.nextInt(30)));
        for (int i=0;i<3;i++)  w.addFox(new Fox(r.nextInt(40), r.nextInt(30)));
        for (int i=0;i<25;i++) w.addRabbit(new Rabbit(r.nextInt(40), r.nextInt(30)));
        for (int i=0;i<30;i++) w.addMouse(new Mouse(r.nextInt(40), r.nextInt(30)));
        for (int i=0;i<40;i++) w.addFood(new Food(r.nextInt(40), r.nextInt(30)));
        for (int t=0;t<=800;t++) {
            if (t % 40 == 0) {
                int inZone = 0;
                for (Entity e : w.allEntities())
                    if (e instanceof Prey && w.isInSafeZone(e.getX(), e.getY())) inZone++;
                System.out.printf("t=%-4d H=%-2d F=%-3d R=%-3d M=%-3d Food=%-3d  preyInRefuge=%d%n",
                    t, w.getHawks().size(), w.getFoxes().size(), w.getRabbits().size(),
                    w.getMice().size(), w.getFood().size(), inZone);
            }
            w.update();
        }
    }
}
