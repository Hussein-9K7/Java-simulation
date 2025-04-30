package simulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import simulation.controller.*;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));
        Scene scene = new Scene(loader.load());

        MainLayoutController mainLayoutController = loader.getController();

        TabPane tabPane = (TabPane) scene.lookup("#tabPane");

        for (Tab tab : tabPane.getTabs()) {
            switch (tab.getId()) {
                case "homeTab":
                    FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/view/HomePage.fxml"));
                    tab.setContent(homeLoader.load());
                    HomePageController homeController = homeLoader.getController();
                    homeController.setMainLayoutController(mainLayoutController);
                    break;

                case "chartsTab":
                    FXMLLoader chartLoader = new FXMLLoader(getClass().getResource("/view/ChartPage.fxml"));
                    tab.setContent(chartLoader.load());
                    ChartPageController chartController = chartLoader.getController();
                    mainLayoutController.setChartPageController(chartController);
                    break;

                case "doctorsTab":
                    FXMLLoader doctorLoader = new FXMLLoader(getClass().getResource("/view/DoctorsPage.fxml"));
                    tab.setContent(doctorLoader.load());
                    DoctorsPageController doctorsController = doctorLoader.getController();
                    mainLayoutController.setDoctorsPageController(doctorsController);
                    break;

                case "statisticsTab":
                    FXMLLoader statsLoader = new FXMLLoader(getClass().getResource("/view/StatisticsPage.fxml"));
                    tab.setContent(statsLoader.load());
                    StatisticsPageController statsController = statsLoader.getController();
                    mainLayoutController.setStatisticsPageController(statsController);
                    break;
                case "simulationTab":
                    FXMLLoader simulationLoader = new FXMLLoader(getClass().getResource("/view/SimulationView.fxml"));
                    tab.setContent(simulationLoader.load());
                    SimulationController simulationController = simulationLoader.getController();
                    break;
            }
        }

        stage.setTitle("Health Center Simulation");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
