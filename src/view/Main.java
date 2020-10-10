package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import lattice.Simulation;

/**
 * Application to run a simulation of the state of a pyrochlore lattice at a range of temperatures and plot the
 * results.
 */
public class Main extends Application {
    private final Simulation simulation = new Simulation();
    private final SimulationTask range = new SimulationTask(simulation, 23, 80, .5);
    private final ChartPanel chartPanel = new ChartPanel(range);
    private final FlowPane buttons = new FlowPane();
    private final Button showLattice = new Button("Show Lattice");
    private final Button exit = new Button("Exit");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Pyrochlore Simulation");
        BorderPane borderPane = new BorderPane();
        buttons.setHgap(5.0);
        buttons.getChildren().addAll(showLattice, exit);
        borderPane.setTop(buttons);
        exit.setOnAction(ae -> System.exit(0));
        showLattice.setOnAction(ae -> showLattice());
        borderPane.setCenter(chartPanel);
        primaryStage.setScene(new Scene(borderPane, 800, 840));
        primaryStage.show();
        new Thread(range).start();
    }

    private void showLattice() {
        Stage latticeStage = new Stage();
        LatticeScene latticeScene = new LatticeScene(simulation);
        latticeStage.setScene(latticeScene.getScene());
        latticeStage.show();
    }

    @Override
    public void stop() {
        range.cancel();
    }
}
