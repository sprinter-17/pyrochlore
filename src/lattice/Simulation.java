package lattice;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private final static int INITIAL_WARM_UP_ITERATIONS = Position.count() * 20;
    private final static int FOLLOWING_WARM_UP_ITERATIONS = Position.count() * 2;
    private final static int SIMULATION_ITERATIONS = Position.count() * 15;

    private final Lattice lattice = new Lattice();
    private final List<State> history = new ArrayList<>();

    public Simulation(double temp) {
        setTemp(temp);
    }

    public void setTemp(double temp) {
        lattice.setTemp(temp);
    }

    public Lattice getMatrix() {
        return lattice;
    }

    public List<State> getHistory() {
        return history;
    }

    public void warmup() {
        history.clear();
        for (int i = 0; i < INITIAL_WARM_UP_ITERATIONS; i++) {
            lattice.randomFlip();
        }
    }

    public void simulate() {
        history.clear();
        for (int i = 0; i < FOLLOWING_WARM_UP_ITERATIONS; i++) {
            lattice.randomFlip();
        }
        for (int i = 0; i < SIMULATION_ITERATIONS; i++) {
            lattice.randomFlip();
            history.add(lattice.getState());
        }
    }
}
