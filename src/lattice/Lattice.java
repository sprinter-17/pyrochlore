package lattice;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Pyrochlore lattice in which each particle has a spin of -1 or +1. The size of the lattice is determined by the set of
 * legal positions as returned by {@link Position#getAll()}.
 */
public class Lattice {
    private final static double B = 20.0;
    private final static double K = 6 * B / 10.0;
    private final static double delta = 0.1;
    //    private final static double deltaH = K * Math.pow(delta, 2.0);
    private final static double deltaH = 120;
    private final static double deltaS = 3.0 * Math.log(5.0);
    private final static int interactionDistance = 8;

    private final Random random = new Random();
    private final Map<Position, Integer> values = new HashMap<>();
    private double temp = 0;
    private double gibbs = 0;
    private State currentState;

    public Lattice() {
        Position.getAll().forEach(pos -> values.put(pos, 1));
        currentState = calculateState();
    }

    public void setTemp(double temp) {
        this.temp = temp;
        this.gibbs = deltaH - temp * deltaS;
    }

    public void randomFlip() {
        Position position = Position.random(random);
        flip(position);
        State newState = calculateState();
        double change = newState.energy() - currentState.energy();
        if (change < 0 || Math.exp(-change / temp) > random.nextDouble()) {
            // keep new state
            currentState = newState;
        } else {
            // flip the spin back to its original state
            flip(position);
        }
    }

    private void flip(Position position) {
        values.compute(position, (p, v) -> -v);
    }

    public void forEach(BiConsumer<Position, Integer> action) {
        values.forEach(action);
    }

    private double interactionEnergy(Position position, double gibbs) {
        int spin = values.get(position);
        double energy = gibbs * spin;
        for (Map.Entry<Position, Integer> entry : position.positionsWithin(interactionDistance).entrySet()) {
            Position neighbour = entry.getKey();
            int distance = entry.getValue();
            if (distance > 0) {
                energy += values.get(neighbour) * spin / Math.pow(distance, 2.0);
            }
        }
        return energy;
    }

    /**
     * Gets the current state of the lattice. The state is calculated by summing the states of all particles in the
     * lattice. Each particle's energy includes both it's own energy and the energy associated with connections to
     * nearby particles.
     *
     * @return the current state of the lattice
     */
    public State getState() {
        return currentState;
    }

    private State calculateState() {
        return Position.getAll()
                .map(pos -> new State(interactionEnergy(pos, gibbs), values.get(pos)))
                .reduce(new State(0, 0), State::add);
    }
}
