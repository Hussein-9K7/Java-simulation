package simulation.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationResultsDatabase {

    public static void saveSimulationResults(int totalPatients, int avgReceptionWaitTime, int avgReceptionServiceTime, int avgDoctorWaitTime, int avgDoctorServiceTime, int simulationTime) {
        String query = "INSERT INTO simulation_results (total_patients, avg_reception_wait_time, avg_reception_service_time, avg_doctor_wait_time, avg_doctor_service_time, simulation_time) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, totalPatients);
            statement.setInt(2, avgReceptionWaitTime);
            statement.setInt(3, avgReceptionServiceTime);
            statement.setInt(4, avgDoctorWaitTime);
            statement.setInt(5, avgDoctorServiceTime);
            statement.setInt(6, simulationTime);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<SimulationResult> getSimulationResults() {
        List<SimulationResult> results = new ArrayList<>();
        String query = "SELECT * FROM simulation_results";

        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int totalPatients = resultSet.getInt("total_patients");
                int avgReceptionWaitTime = resultSet.getInt("avg_reception_wait_time");
                int avgReceptionServiceTime = resultSet.getInt("avg_reception_service_time");
                int avgDoctorWaitTime = resultSet.getInt("avg_doctor_wait_time");
                int avgDoctorServiceTime = resultSet.getInt("avg_doctor_service_time");
                int simulationTime = resultSet.getInt("simulation_time");

                results.add(new SimulationResult(id, totalPatients, avgReceptionWaitTime, avgReceptionServiceTime, avgDoctorWaitTime, avgDoctorServiceTime, simulationTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
