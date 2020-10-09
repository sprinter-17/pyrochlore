package lattice;

import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * Record of the results of a simulation for the lattice at a particular temperature. The results include the average
 * energy, magnetism and heat capacity of each particle across all steps of the simualtion.
 */
public record Result(double temp, double averageEnergy, double averageMagnetism, double averageHeatCapacity) {
    /**
     * Constructs a result record for a given temperature and list of lattice states.
     *
     * @param temp the temperature the simulation was run at
     * @param states the state of the lattice after each simulation step
     * @return aggregated results for the entire simulation
     */
    public static Result calculate(double temp, List<State> states) {
        double averageEnergy = average(states, State::energy);
        double averageMagnetism = average(states, State::magnetism);
        double averageHeatCapacity = (average(states, State::energySquared) - Math.pow(averageEnergy, 2)) / Math.pow(temp, 2);
        return new Result(temp, averageEnergy, averageMagnetism, averageHeatCapacity);
    }

    private static double average(List<State> states, ToDoubleFunction<State> extractor) {
        return states.stream().mapToDouble(extractor).average().orElseThrow() / Position.count();
    }
}
