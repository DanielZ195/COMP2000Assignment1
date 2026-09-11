
/**
 * Prey is-a Animal that eats Food and flees Predators, and also implements
 * Edible - since Predator eats Prey. Rabbit and Mouse extend this so they
 * share fleeing/eating/speed logic; Mouse additionally adds reproduction
 * (see Mouse.java).
 *
 * Speed rule: a well-fed prey moves at FED_SPEED (2 steps); a hungry one
 * only moves at HUNGRY_SPEED (1 step). Eating food refills fedTicks, and
 * updateSpeed() counts that down every tick until it runs out.
 */
public abstract class Prey extends Animal implements Edible {
    protected static final int HUNGRY_SPEED = 1;
    protected static final int FED_SPEED = 2;
    protected static final int FED_DURATION = 80; // ticks of fast movement per meal

    protected int eatDistance = 1;
    protected double nutritionValue; // how much health a Predator gains from eating this
    protected int fedTicks = 0;

    public Prey(int x, int y, double health, int visionRadius, double nutritionValue) {
        super(x, y, health, HUNGRY_SPEED, visionRadius);
        this.nutritionValue = nutritionValue;
    }

    @Override
    public double getNutritionValue() {
        return nutritionValue;
    }

    /** Call once per tick before moving: sets this tick's speed from fed status. */
    protected void updateSpeed() {
        if (fedTicks > 0) {
            fedTicks--;
            speed = FED_SPEED;
        } else {
            speed = HUNGRY_SPEED;
        }
    }

    protected boolean tryEatFood(Food food) {
        if (food != null && food.isAlive() && distanceTo(food) <= eatDistance) {
            food.kill();
            health = Math.min(health + food.getNutritionValue(), 100);
            fedTicks = FED_DURATION;
            return true;
        }
        return false;
    }
}
