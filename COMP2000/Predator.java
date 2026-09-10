
public abstract class Predator extends Animal {
    protected int eatDistance = 1;

    public Predator(int x, int y, double health, int speed, int visionRadius) {
        super(x, y, health, speed, visionRadius);
    }

    protected boolean tryEat(Prey prey) {
        if (prey != null && prey.isAlive() && distanceTo(prey) <= eatDistance) {
            prey.kill();
            health = Math.min(health + prey.getNutritionValue(), 100);
            return true;
        }
        return false;
    }
}
