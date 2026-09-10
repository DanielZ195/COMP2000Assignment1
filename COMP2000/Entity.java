import java.awt.Color;
import java.awt.Graphics;

/**
 * Root of the hierarchy. Anything in the world (an animal OR a piece of food)
 * is-a Entity: it has a grid position and can be alive or dead.
 *
 * x and y are cell coordinates, not pixels. px()/py() convert for drawing.
 */
public abstract class Entity {
    private int x, y;
    private boolean alive = true;

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isAlive() { return alive; }
    public void kill() { alive = false; }

    /** The only way to move an entity - subclasses use this instead of touching fields. */
    protected void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** Chebyshev (king-move) distance: how many grid steps apart. */
    public int distanceTo(Entity other) {
        return Math.max(Math.abs(this.x - other.x), Math.abs(this.y - other.y));
    }

    /** Pixel centre of this entity's cell. */
    protected int px() { return x * SimPanel.CELL_SIZE + SimPanel.CELL_SIZE / 2; }
    protected int py() { return y * SimPanel.CELL_SIZE + SimPanel.CELL_SIZE / 2; }

    public abstract void update(World world);
    public abstract void draw(Graphics g);
    public abstract Color getColor();

    @Override
    public String toString() {
        return String.format("%s at (%d, %d)", getClass().getSimpleName(), x, y);
    }
}
