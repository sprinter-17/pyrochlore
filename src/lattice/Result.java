package lattice;

import java.util.List;
import java.util.function.ToDoubleFunction;

public record Result(double temp, double averageEnergy, double averageMagnetism, double averageHeatCapacity) {
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
