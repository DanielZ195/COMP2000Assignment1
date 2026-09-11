/**
 * Thrown when the simulation is asked to start with parameters it cannot run.
 *
 * Checked, because the caller (Main) can always do something sensible about
 * it: report what was wrong and stop, instead of failing later with a stack
 * trace from somewhere unrelated.
 */
public class SimulationConfigException extends Exception {
    public SimulationConfigException(String message) {
        super(message);
    }

    public SimulationConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
