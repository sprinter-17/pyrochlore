package view;

import javafx.concurrent.Task;
import lattice.Result;
import lattice.Simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Task to run a set of pyrochlore simulations for a range of temperatures and generate a {@link Result} for each
 * simulation. The task reports results after each simulation to allow them to be plotted on a graph as the simulation
 * continues.
 */
public class SimulationTask extends Task<List<Result>> {
    private final Simulation simulation;
    private final double tempMin;
    private final double tempMax;
    private final double tempStep;

    public SimulationTask(Simulation simulation, double tempMin, double tempMax, double tempStep) {
        this.simulation = simulation;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.tempStep = tempStep;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public double getTempStep() {
        return tempStep;
    }

    @Override
    protected List<Result> call() {
        List<Result> results = new ArrayList<>();
        simulation.warmup(tempMin);
        for (double temp = tempMin; temp <= tempMax; temp += tempStep) {
            results.add(Result.calculate(temp, simulation.simulate(temp)));
            updateValue(results);
            updateProgress(temp - tempMin, tempMax - tempMin);
        }
        return results;
    }
}
