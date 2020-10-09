package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import lattice.Simulation;
import lattice.TempRangeSimulation;

public class Main extends Application {
    private final Simulation simulation = new Simulation(1.0);
    private final TempRangeSimulation range = new TempRangeSimulation(simulation, .25, 5.0, .25);
    private final ChartPanel chartPanel = new ChartPanel(range);
    private final FlowPane buttons = new FlowPane();
    private final Button showLattice = new Button("Show Lattice");
    private final Button exit = new Button("Exit");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Joe's Assignment");
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
