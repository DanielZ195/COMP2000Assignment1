import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * A fixed grid of cells, each holding any number of T.
 *
 * T is bounded to Entity so the grid can read an occupant's own position
 * instead of being told it separately.
 *
 * Backed by List<List<T>> rather than List<T>[][] because Java cannot create
 * a generic array; the Object[][] alternative needs an unchecked cast on
 * every read, which suppresses the warning without making it safe.
 */
public class Grid<T extends Entity> {
    private final int cols, rows;
    private final List<List<T>> cells;

    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.cells = new ArrayList<>(cols * rows);
        for (int i = 0; i < cols * rows; i++) {
            cells.add(new ArrayList<>());
        }
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }

    public boolean contains(int x, int y) {
        return x >= 0 && x < cols && y >= 0 && y < rows;
    }

    /** Out of range is a caller bug, not an expected condition, so this throws unchecked. */
    public List<T> cellAt(int x, int y) {
        if (!contains(x, y)) {
            throw new IndexOutOfBoundsException(
                "cell (" + x + ", " + y + ") is outside " + cols + "x" + rows);
        }
        return cells.get(y * cols + x);
    }

    public void add(T occupant) {
        cellAt(occupant.getX(), occupant.getY()).add(occupant);
    }

    public void clear() {
        for (List<T> cell : cells) {
            cell.clear();
        }
    }

    /** Living occupants within Chebyshev `radius`. Clipped at the edges: scanning near a wall is normal. */
    public List<T> occupantsWithin(int x, int y, int radius) {
        return occupantsWithin(x, y, radius, e -> true);
    }

    public List<T> occupantsWithin(int x, int y, int radius, Predicate<? super T> filter) {
        List<T> found = new ArrayList<>();
        forEachCellNear(x, y, radius, occupant -> {
            if (filter.test(occupant)) found.add(occupant);
        });
        return found;
    }

    /** Only the occupants of the given type, without casting at the call site. */
    public <U extends T> List<U> occupantsWithin(int x, int y, int radius, Class<U> type) {
        List<U> found = new ArrayList<>();
        forEachCellNear(x, y, radius, occupant -> {
            if (type.isInstance(occupant)) found.add(type.cast(occupant));
        });
        return found;
    }

    private void forEachCellNear(int x, int y, int radius, java.util.function.Consumer<T> action) {
        int minX = Math.max(0, x - radius), maxX = Math.min(cols - 1, x + radius);
        int minY = Math.max(0, y - radius), maxY = Math.min(rows - 1, y + radius);
        for (int cy = minY; cy <= maxY; cy++) {
            for (int cx = minX; cx <= maxX; cx++) {
                for (T occupant : cells.get(cy * cols + cx)) {
                    if (occupant.isAlive()) action.accept(occupant);
                }
            }
        }
    }
}
