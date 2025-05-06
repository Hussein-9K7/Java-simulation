package simulation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class MainLayoutController {

    private ChartPageController chartPageController;
    private StatisticsPageController statisticsPageController;
    private DoctorsPageController doctorsPageController;
    private SimulationController simulationController;

    @FXML
    private Tab doctorsTab;

    @FXML
    private Tab chartsTab;

    @FXML
    private Tab statisticsTab;

    @FXML
    private Tab simulationTab;


    public void setChartPageController(ChartPageController controller) {
        this.chartPageController = controller;
    }

    public void setStatisticsPageController(StatisticsPageController controller) {
        this.statisticsPageController = controller;
    }

    public void setDoctorsPageController(DoctorsPageController controller) {
        this.doctorsPageController = controller;
    }
    public void setSimulationController(SimulationController controller) {
        this.simulationController = controller;
    }
    public void notifySimulationCompleted() {
        if (chartPageController != null) {
            chartPageController.updateChart();
        }
        if (statisticsPageController != null) {
            statisticsPageController.updateStatistics();
        }
        if (doctorsPageController != null) {
            doctorsPageController.updateDoctors();
        }
        if (doctorsTab != null) {
            doctorsTab.setDisable(false);
        }
        if (chartsTab != null) {
            chartsTab.setDisable(false);
        }
        if (statisticsTab != null) {
            statisticsTab.setDisable(false);
        }
        if (simulationTab != null) {
            simulationTab.setDisable(false);
        }

    }

    public void resetTabs() {
        if (doctorsTab != null) {
            doctorsTab.setDisable(true);
        }
        if (chartsTab != null) {
            chartsTab.setDisable(true);
        }
        if (statisticsTab != null) {
            statisticsTab.setDisable(true);
        }
        if (simulationTab != null) {
            simulationTab.setDisable(true);
        }
    }

    @FXML
    public void initialize() {
        resetTabs();
    }
}
