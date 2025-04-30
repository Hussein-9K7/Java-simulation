package simulation.controller;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.util.Duration;
import simulation.model.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ChartPageController {

    @FXML
    private PieChart pieChart;

    @FXML
    public void initialize() {
    }

    public void updateChart() {
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT avg_reception_wait_time, avg_reception_service_time, avg_doctor_wait_time, avg_doctor_service_time " +
                             "FROM simulation_results ORDER BY id DESC LIMIT 1")) {

            if (resultSet.next()) {
                int receptionWait = resultSet.getInt("avg_reception_wait_time");
                int receptionService = resultSet.getInt("avg_reception_service_time");
                int doctorWait = resultSet.getInt("avg_doctor_wait_time");
                int doctorService = resultSet.getInt("avg_doctor_service_time");

                int total = receptionWait + receptionService + doctorWait + doctorService;

                if (total > 0) {
                    chartData.add(new PieChart.Data(String.format("Reception Wait (%.1f%%)", receptionWait * 100.0 / total), receptionWait));
                    chartData.add(new PieChart.Data(String.format("Reception Service (%.1f%%)", receptionService * 100.0 / total), receptionService));
                    chartData.add(new PieChart.Data(String.format("Doctor Wait (%.1f%%)", doctorWait * 100.0 / total), doctorWait));
                    chartData.add(new PieChart.Data(String.format("Doctor Service (%.1f%%)", doctorService * 100.0 / total), doctorService));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            pieChart.setTitle("Average Time Distribution (Last Simulation)");
            pieChart.setData(chartData);

            for (PieChart.Data data : chartData) {
                Node node = data.getNode();
                if (node != null) {
                    FadeTransition ft = new FadeTransition(Duration.millis(800), node);
                    ft.setFromValue(0);
                    ft.setToValue(1);
                    ft.play();
                }
            }
        });
    }
}
