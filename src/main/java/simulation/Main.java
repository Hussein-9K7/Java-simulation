package simulation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import simulation.controller.ChartPageController;
import simulation.controller.HomePageController;
import simulation.controller.MainLayoutController;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainLayout.fxml"));
        Scene scene = new Scene(loader.load());

        MainLayoutController mainLayoutController = loader.getController();

        TabPane tabPane = (TabPane) scene.lookup("#tabPane");

        for (Tab tab : tabPane.getTabs()) {
            if ("chartsTab".equals(tab.getId())) {
                FXMLLoader chartLoader = new FXMLLoader(getClass().getResource("/view/ChartPage.fxml"));
                tab.setContent(chartLoader.load());
                ChartPageController chartController = chartLoader.getController();
                mainLayoutController.setChartPageController(chartController);
            } else if ("homeTab".equals(tab.getId())) {
                FXMLLoader homeLoader = new FXMLLoader(getClass().getResource("/view/HomePage.fxml"));
                tab.setContent(homeLoader.load());
                HomePageController homeController = homeLoader.getController();
                homeController.setMainLayoutController(mainLayoutController);
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
