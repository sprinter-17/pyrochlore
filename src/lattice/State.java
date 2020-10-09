package lattice;

public record State(double energy, double energySquared, int magnetism) {

    public State(double energy, int magnetism) {
        this(energy, energy * energy, magnetism);
    }

    public State add(State other) {
        return new State(energy + other.energy,
                energySquared + other.energySquared,
                magnetism + other.magnetism);
    }
}
