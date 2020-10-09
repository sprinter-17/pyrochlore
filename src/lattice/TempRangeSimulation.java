package lattice;

import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class TempRangeSimulation extends Task<List<Result>> {
    private final Simulation simulation;
    private final double tempMin;
    private final double tempMax;
    private final double tempStep;

    public TempRangeSimulation(Simulation simulation, double tempMin, double tempMax, double tempStep) {
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
        simulation.warmup();
        for (double temp = tempMin; temp <= tempMax; temp += tempStep) {
            simulation.setTemp(temp);
            simulation.simulate();
            results.add(Result.calculate(temp, simulation.getHistory()));
            updateValue(results);
            updateProgress(temp - tempMin, tempMax - tempMin);
        }
        return results;
    }
}
