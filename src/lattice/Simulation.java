package lattice;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for simulations of a pyrochlore lattice. Each simulation step involves potentially * flipping the spin of
 * a random particle. The simulation supports warming up the lattice to allow it to reach an equilibrium state. Each
 * separate simulation then consists of setting the lattice temperature, running a smaller number of warm up iterations
 * and then executing a number of iterations while recording the state of the lattice.
 */
public class Simulation {
    private final static int INITIAL_WARM_UP_ITERATIONS = Position.count() * 20;
    public final static int SIMULATION_ITERATIONS = Position.count() * 25;
    private final static int FOLLOWING_WARM_UP_ITERATIONS = Position.count() * 5;

    private final Lattice lattice = new Lattice();

    public Lattice getMatrix() {
        return lattice;
    }

    /**
     * Allows the lattice to reach an equilibrium state before starting the initial simulation.
     */
    public void warmup(double temp) {
        lattice.setTemp(temp);
        for (int i = 0; i < INITIAL_WARM_UP_ITERATIONS; i++) {
            lattice.randomFlip();
        }
    }

    /**
     * Runs a simulation of the lattice at a given temperature.
     *
     * @param temp the temperature to set the lattice to before running the simulation.
     * @return the ordered list of lattice states after each simulation step
     */
    public List<State> simulate(double temp) {
        List<State> history = new ArrayList<>();
        lattice.setTemp(temp);
        for (int i = 0; i < FOLLOWING_WARM_UP_ITERATIONS; i++) {
            lattice.randomFlip();
        }
        for (int i = 0; i < SIMULATION_ITERATIONS; i++) {
            lattice.randomFlip();
            history.add(lattice.getState());
        }
        return history;
    }
}
