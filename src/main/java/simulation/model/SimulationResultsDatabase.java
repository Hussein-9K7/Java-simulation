package simulation.model;

import simulation.model.SimulationResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationResultsDatabase {

    public static void saveSimulationResults(int totalPatients, int totalWaitTime, int simulationTime) {
        String query = "INSERT INTO simulation_results (total_patients, total_wait_time, simulation_time) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, totalPatients);
            statement.setInt(2, totalWaitTime);
            statement.setInt(3, simulationTime);

            statement.executeUpdate();  // Execute query to insert data
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<SimulationResult> getSimulationResults() {
        List<SimulationResult> results = new ArrayList<>();
        String query = "SELECT * FROM simulation_results";

        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int totalPatients = resultSet.getInt("total_patients");
                int totalWaitTime = resultSet.getInt("total_wait_time");
                int simulationTime = resultSet.getInt("simulation_time");

                results.add(new SimulationResult(id, totalPatients, totalWaitTime, simulationTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }
}
