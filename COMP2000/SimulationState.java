/**
 * How the simulation ended, or RUNNING if it has not.
 *
 * Extinction is an expected outcome, not an error, so it is reported as a
 * return value rather than thrown - see the note in SpawnException.
 */
public enum SimulationState {
    RUNNING, PREY_EXTINCT, PREDATORS_EXTINCT, TIME_LIMIT
}
