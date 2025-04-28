package simulation.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import simulation.model.CustomerView;
import simulation.model.DatabaseManager;
import simulation.model.SimulationManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainController {

    @FXML
    private Label clockLabel;

    @FXML
    private Button startButton;

    @FXML
    private TableView<CustomerView> customerTable;

    @FXML
    private TableColumn<CustomerView, Integer> idColumn;
    @FXML
    private TableColumn<CustomerView, String> arrivalTimeColumn;
    @FXML
    private TableColumn<CustomerView, String> receptionStartColumn;
    @FXML
    private TableColumn<CustomerView, String> receptionEndColumn;
    @FXML
    private TableColumn<CustomerView, String> doctorStartColumn;
    @FXML
    private TableColumn<CustomerView, String> doctorEndColumn;

    @FXML
    private Label totalCustomersLabel;
    @FXML
    private Label avgReceptionWaitLabel;
    @FXML
    private Label avgReceptionServiceLabel;
    @FXML
    private Label avgDoctorWaitLabel;
    @FXML
    private Label avgDoctorServiceLabel;
    @FXML
    private ListView<String> doctorsListView;

    private int currentTime = 0;
    private SimulationManager simulationManager;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        receptionStartColumn.setCellValueFactory(new PropertyValueFactory<>("receptionStart"));
        receptionEndColumn.setCellValueFactory(new PropertyValueFactory<>("receptionEnd"));
        doctorStartColumn.setCellValueFactory(new PropertyValueFactory<>("doctorStart"));
        doctorEndColumn.setCellValueFactory(new PropertyValueFactory<>("doctorEnd"));

        startButton.setOnAction(e -> {
            currentTime = 0;
            clearAllTables();
            showSuccessAlert();
            startSimulation();
            simulationManager = new SimulationManager();
            simulationManager.startSimulation();
            loadCustomerData();
        });
    }

    private void startSimulation() {
        Thread thread = new Thread(() -> {
            while (currentTime <= 480) {
                try {
                    int hours = 8 + (currentTime / 60);
                    int minutes = currentTime % 60;
                    String formattedTime = String.format("%02d:%02d", hours, minutes);
                    javafx.application.Platform.runLater(() -> clockLabel.setText("Time: " + formattedTime));
                    Thread.sleep(100);
                    currentTime++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            javafx.application.Platform.runLater(this::showSimulationResults);
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void loadCustomerData() {
        ObservableList<CustomerView> customers = FXCollections.observableArrayList();
        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM customers")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int arrivalTime = resultSet.getInt("arrival_time");
                int receptionStart = resultSet.getInt("reception_start");
                int receptionEnd = resultSet.getInt("reception_end");
                int doctorStart = resultSet.getInt("doctor_start");
                int doctorEnd = resultSet.getInt("doctor_end");

                customers.add(new CustomerView(id, arrivalTime, receptionStart, receptionEnd, doctorStart, doctorEnd));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        customerTable.setItems(customers);
    }

    private void clearAllTables() {
        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM customers");
            statement.executeUpdate("ALTER TABLE customers AUTO_INCREMENT = 1");
            statement.executeUpdate("DELETE FROM doctors");
            statement.executeUpdate("ALTER TABLE doctors AUTO_INCREMENT = 1");
            statement.executeUpdate("DELETE FROM simulation_results");
            statement.executeUpdate("ALTER TABLE simulation_results AUTO_INCREMENT = 1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        customerTable.getItems().clear();
        insertRandomDoctors();
    }

    private void insertRandomDoctors() {
        String[] names = {"John Smith", "Emily Johnson", "Michael Brown", "Sophia Davis", "Daniel Wilson", "Olivia Moore", "David Taylor", "Emma Anderson"};
        String[] specializations = {"Cardiology", "Neurology", "Pediatrics", "Orthopedics", "Dermatology", "Oncology", "Psychiatry", "Radiology"};

        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement()) {
            for (int i = 0; i < 4; i++) {
                String randomName = names[(int) (Math.random() * names.length)];
                String randomSpecialization = specializations[(int) (Math.random() * specializations.length)];
                String insertQuery = String.format("INSERT INTO doctors (name, specialization) VALUES ('%s', '%s')", randomName, randomSpecialization);
                statement.executeUpdate(insertQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tables Reset");
        alert.setHeaderText(null);
        alert.setContentText("All tables have been successfully cleared! The simulation will now start ✅");
        alert.showAndWait();
    }

    private void showSimulationResults() {
        int totalCustomers = 0;
        int totalReceptionWait = 0;
        int totalReceptionService = 0;
        int totalDoctorWait = 0;
        int totalDoctorService = 0;

        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM customers")) {
            while (resultSet.next()) {
                totalCustomers++;
                int arrival = resultSet.getInt("arrival_time");
                int receptionStart = resultSet.getInt("reception_start");
                int receptionEnd = resultSet.getInt("reception_end");
                int doctorStart = resultSet.getInt("doctor_start");
                int doctorEnd = resultSet.getInt("doctor_end");

                totalReceptionWait += (receptionStart - arrival);
                totalReceptionService += (receptionEnd - receptionStart);
                totalDoctorWait += (doctorStart - receptionEnd);
                totalDoctorService += (doctorEnd - doctorStart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int avgReceptionWait = totalCustomers > 0 ? totalReceptionWait / totalCustomers : 0;
        int avgReceptionService = totalCustomers > 0 ? totalReceptionService / totalCustomers : 0;
        int avgDoctorWait = totalCustomers > 0 ? totalDoctorWait / totalCustomers : 0;
        int avgDoctorService = totalCustomers > 0 ? totalDoctorService / totalCustomers : 0;

        totalCustomersLabel.setText("Total Customers: " + totalCustomers);
        avgReceptionWaitLabel.setText("Avg Reception Wait: " + avgReceptionWait + " min");
        avgReceptionServiceLabel.setText("Avg Reception Service: " + avgReceptionService + " min");
        avgDoctorWaitLabel.setText("Avg Doctor Wait: " + avgDoctorWait + " min");
        avgDoctorServiceLabel.setText("Avg Doctor Service: " + avgDoctorService + " min");

        ObservableList<String> doctorList = FXCollections.observableArrayList();
        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM doctors")) {
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
