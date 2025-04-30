package simulation.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import simulation.model.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DoctorsPageController {

    @FXML
    private ListView<String> doctorsListView;

    @FXML
    public void initialize() {
        ObservableList<String> doctorList = FXCollections.observableArrayList();

        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT name, specialization FROM doctors")) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                doctorList.add(name + " - " + specialization);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        doctorsListView.setItems(doctorList);
    }
}
