import java.util.Random;


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

    protected static final double HOP_COST = 3;

    public Prey(int x, int y, double health, int visionRadius, double nutritionValue) {
        super(x, y, health, HUNGRY_SPEED, visionRadius);
        this.nutritionValue = nutritionValue;
        this.fedTicks = FED_DURATION;
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

    /**
     * Knight-hop clear of a predator: two cells directly away, one to the side.
     * A slider cannot follow that in a single tick. Costs HOP_COST, so a
     * starving animal cannot afford it and has to settle for backing away.
     */
    protected void evade(Predator threat, World world) {
        if (health <= HOP_COST) {
            moveAwayFrom(threat);
            return;
        }
        Random rng = world.getRandom();
        int dx = getX() - threat.getX();
        int dy = getY() - threat.getY();
        int sideways = rng.nextBoolean() ? 1 : -1;
        if (Math.abs(dx) >= Math.abs(dy)) {
            int away = (dx == 0) ? sideways : Integer.signum(dx);
            setPosition(getX() + 2 * away, getY() + sideways);
        } else {
            int away = (dy == 0) ? sideways : Integer.signum(dy);
            setPosition(getX() + sideways, getY() + 2 * away);
        }
        health -= HOP_COST;
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
