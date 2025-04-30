package simulation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import simulation.model.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class StatisticsPageController {

    @FXML
    private Label totalPatientsLabel;

    @FXML
    private Label receptionWaitLabel;

    @FXML
    private Label receptionServiceLabel;

    @FXML
    private Label doctorWaitLabel;

    @FXML
    private Label doctorServiceLabel;

    public void updateStatistics() {
        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT * FROM simulation_results ORDER BY id DESC LIMIT 1")) {

            if (resultSet.next()) {
                int total = resultSet.getInt("total_patients");
                int recWait = resultSet.getInt("avg_reception_wait_time");
                int recService = resultSet.getInt("avg_reception_service_time");
                int docWait = resultSet.getInt("avg_doctor_wait_time");
                int docService = resultSet.getInt("avg_doctor_service_time");

                totalPatientsLabel.setText("Total Patients: " + total);
                receptionWaitLabel.setText("Reception Wait Avg: " + recWait + " min");
                receptionServiceLabel.setText("Reception Service Avg: " + recService + " min");
                doctorWaitLabel.setText("Doctor Wait Avg: " + docWait + " min");
                doctorServiceLabel.setText("Doctor Service Avg: " + docService + " min");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
