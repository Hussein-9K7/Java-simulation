package simulation.controller;

public class MainLayoutController {

    private ChartPageController chartPageController;

    public void setChartPageController(ChartPageController controller) {
        this.chartPageController = controller;
    }

    public void notifySimulationCompleted() {
        if (chartPageController != null) {
            chartPageController.updateChart();
        }
    }
}
