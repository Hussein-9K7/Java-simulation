package simulation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class MainLayoutController {

    private ChartPageController chartPageController;
    private StatisticsPageController statisticsPageController;
    private DoctorsPageController doctorsPageController;

    @FXML
    private Tab doctorsTab;

    @FXML
    private Tab chartsTab;

    @FXML
    private Tab statisticsTab;

    public void setChartPageController(ChartPageController controller) {
        this.chartPageController = controller;
    }

    public void setStatisticsPageController(StatisticsPageController controller) {
        this.statisticsPageController = controller;
    }

    public void setDoctorsPageController(DoctorsPageController controller) {
        this.doctorsPageController = controller;
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
    }

    @FXML
    public void initialize() {
        resetTabs();
    }
}
