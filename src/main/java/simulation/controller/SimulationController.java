package simulation.controller;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import simulation.model.SimulationManager;
import simulation.model.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SimulationController {

    @FXML
    private Pane simulationPane; // Graafinen alue, johon animaatiot piirretään

    @FXML
    private Label waitLabel; // Tekstikenttä, joka näyttää odotusajat

    @FXML
    private Button startButton; // Käynnistyspainike

    private SimulationManager simulationManager; // Simulaatiomalli
    private Rectangle[] doctorsVisual; // Graafiset esitykset lääkäreistä (siniset suorakulmiot)

    public void initialize() {
        simulationManager = new SimulationManager();
        startButton.setOnAction(event -> startSimulation()); // Käynnistää simuloinnin painikkeella
    }

    public void startSimulation() {
        // Tyhjennä graafinen alue ja poista käynnistyspainike käytöstä
        simulationPane.getChildren().clear();
        startButton.setDisable(true);

        // Suorita simulointi erillisessä säikeessä (Thread)
        Task<Void> simulationTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Vaihe 1: Generoi simulointidata (tietokantaan)
                Platform.runLater(() -> waitLabel.setText("Generoidaan simulointidataa..."));


                // Vaihe 2: Luo lääkärit graafisesti
                Platform.runLater(() -> {
                    waitLabel.setText("Ladataan lääkäreitä...");
                    createDoctorsFromDatabase();
                });

                // Vaihe 3: Animaatio potilaiden liikkeelle
                Platform.runLater(() -> {
                    waitLabel.setText("Animoidaan potilaita...");
                    animatePatientsFromDatabase();
                });

                return null;
            }
        };

        // Kun simulointi on valmis, ota painike uudestaan käyttöön
        simulationTask.setOnSucceeded(e -> startButton.setDisable(false));

        // Käynnistä säie
        new Thread(simulationTask).start();
    }

    private void createDoctorsFromDatabase() {
        String query = "SELECT id, name FROM doctors ORDER BY id";
        doctorsVisual = new Rectangle[4]; // 4 lääkäriä (kuten SimulationManagerissa)

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            int x = 400; // Aloituspaikka X-akselilla
            int y = 100; // Aloituspaikka Y-akselilla
            int spacing = 100; // Lääkärien välinen etäisyys

            int i = 0;
            while (resultSet.next() && i < 4) {
                Rectangle doctor = new Rectangle(x, y, 50, 50);
                doctor.setFill(Color.BLUE); // Lääkärit sinisinä
                simulationPane.getChildren().add(doctor);

                // Lisää nimi kunkin lääkärin alle
                Label nameLabel = new Label(resultSet.getString("name"));
                nameLabel.setLayoutX(x);
                nameLabel.setLayoutY(y - 20);
                simulationPane.getChildren().add(nameLabel);

                doctorsVisual[i] = doctor; // Tallenna viite myöhempää käyttöä varten
                x += spacing;
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void animatePatientsFromDatabase() {
        String query = "SELECT id, arrival_time, reception_start, reception_end, doctor_start, doctor_end " +
                "FROM customers ORDER BY id";

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Aloituspaikka potilaille Y-akselilla
            int startY = 300;
            // Muuttuja potilaiden viivästykseen
            int delayIndex = 0;

            // Silmukka potilaiden tietojen hakemiseen tietokannasta
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int arrivalTime = resultSet.getInt("arrival_time");
                int receptionStart = resultSet.getInt("reception_start");
                int receptionEnd = resultSet.getInt("reception_end");
                int doctorStart = resultSet.getInt("doctor_start");
                int doctorEnd = resultSet.getInt("doctor_end");

                // Crea el círculo del paciente
                // Potilas on punainen ympyrä
                Circle patient = new Circle(50, startY, 15);
                patient.setFill(Color.RED);
                simulationPane.getChildren().add(patient);

                // Calcula los tiempos en segundos (usa Math.max para evitar negativos)
                // Potilaan saapumisaika, vastaanoton aloitus, vastaanoton lopetus, lääkärin aloitus ja lääkärin lopetus
                double receptionWaitSec = Math.max(0, (receptionStart - arrivalTime) / 2.0);
                // Lääkärin aloitusaika, vastaanoton lopetus ja lääkärin aloitusaika
                double doctorWaitSec = Math.max(0, (doctorStart - receptionEnd) / 2.0);

                // Agrega un pequeño retardo antes de lanzar la animación
                // Laske viive potilaan animaatiolle
                int delayMillis = delayIndex * 40;

                javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(delayMillis));

                // Usar final variables para la lambda
                final int patientIdFinal = id;
                final double startYFinal = startY;
                final double receptionWaitFinal = receptionWaitSec;
                final double doctorWaitFinal = doctorWaitSec;
                final Circle patientFinal = patient;

                delay.setOnFinished(ev -> {
                    try {
                        animatePatient(patientFinal, patientIdFinal, receptionWaitFinal, doctorWaitFinal, startYFinal);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                delay.play();

                startY += 40;
                delayIndex++;
            }

            System.out.println("Total pacientes animados: " + delayIndex);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void animatePatient(Circle patient, int patientId, double receptionWaitSec, double doctorWaitSec, double startY) {
        // Odota ennen vastaanotolle siirtymistä
        PauseTransition initialWait = new PauseTransition(Duration.seconds(receptionWaitSec));
        initialWait.setOnFinished(e -> {
            Platform.runLater(() -> waitLabel.setText("Potilas " + patientId + " vastaanotolla..."));

            // Liiku vastaanotolle (X: 200)
            TranslateTransition moveToReception = new TranslateTransition(Duration.seconds(3), patient);
            moveToReception.setToX(200 - patient.getCenterX());
            moveToReception.setToY(0);
            moveToReception.setOnFinished(e2 -> {
                // Odota ennen lääkärille siirtymistä
                PauseTransition waitForDoctor = new PauseTransition(Duration.seconds(doctorWaitSec));
                waitForDoctor.setOnFinished(e3 -> {
                    Platform.runLater(() -> waitLabel.setText("Potilas " + patientId ));

                    // Liiku lääkärin luo (jaa potilaat lääkäreille kierrätyksellä)
                    int doctorIndex = (patientId - 1) % 4;
                    Rectangle doctor = doctorsVisual[doctorIndex];

                    TranslateTransition moveToDoctor = new TranslateTransition(Duration.seconds(3), patient);
                    moveToDoctor.setToX(doctor.getX() - patient.getCenterX() + 25); // Keskitä lääkärin suorakulmion sisälle
                    moveToDoctor.setToY(doctor.getY() - startY + 25);
                    moveToDoctor.play();
                });
                waitForDoctor.play();
            });
            moveToReception.play();
        });
        initialWait.play();
    }
}