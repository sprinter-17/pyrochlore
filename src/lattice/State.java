package lattice;

/**
 * Record of the state of the lattice. The energy, energy squared and magnetism of the lattice are recorded.
 */
public record State(double energy, double energySquared, int magnetism) {

    public State(double energy, int magnetism) {
        this(energy, energy * energy, magnetism);
    }

    /**
     * Constructs a new state as the sum of this one and a given other state.
     *
     * @param other the other state to add
     * @return a state with all values summed
     */
    public State add(State other) {
        return new State(energy + other.energy,
                energySquared + other.energySquared,
                magnetism + other.magnetism);
    }
}
