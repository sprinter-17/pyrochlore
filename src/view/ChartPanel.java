package view;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import lattice.Result;

import java.util.List;

/**
 * Panel containing a line chart of the results of a simulation. Each element of the {@link Result} is plotted as a
 * separate series against the temperature of the simulation.
 */
public class ChartPanel extends BorderPane {
    private final SimulationTask rangeSimulation;
    private final XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> magnetismSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> heatCapacitySeries = new XYChart.Series<>();

    public ChartPanel(SimulationTask rangeSimulation) {
        this.rangeSimulation = rangeSimulation;
        NumberAxis values = new NumberAxis();
        NumberAxis temp = new NumberAxis("Temperature",
                rangeSimulation.getTempMin(), rangeSimulation.getTempMax(), rangeSimulation.getTempStep());
        LineChart<Number, Number> chart = new LineChart<>(temp, values);
        chart.setCreateSymbols(false);
        energySeries.setName("Energy");
        magnetismSeries.setName("Magnetism");
        heatCapacitySeries.setName("Heat Capacity");
        setCenter(chart);
        //noinspection unchecked
        chart.getData().addAll(magnetismSeries, heatCapacitySeries);
        chart.setVerticalGridLinesVisible(false);
        rangeSimulation.progressProperty().addListener((ov, l1, l2) -> showData());
    }

    private void showData() {
        List<Result> results = rangeSimulation.getValue();
        for (Result result : results.subList(energySeries.getData().size(), results.size())) {
            energySeries.getData().add(new XYChart.Data<>(result.temp(), result.averageEnergy()));
            magnetismSeries.getData().add(new XYChart.Data<>(result.temp(), result.averageMagnetism()));
            heatCapacitySeries.getData().add(new XYChart.Data<>(result.temp(), result.averageHeatCapacity()));
        }
    }
}
