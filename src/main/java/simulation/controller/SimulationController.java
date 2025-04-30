package simulation.controller;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SimulationController {

    @FXML
    private Pane simulationPane;

    @FXML
    private Label waitLabel;

    @FXML
    private Button startButton;

    public void initialize() {
        startButton.setOnAction(event -> startSimulation());
    }

    public void startSimulation() {
        // Create doctors (blue squares)
        Rectangle doctor1 = new Rectangle(400, 100, 50, 50);
        Rectangle doctor2 = new Rectangle(500, 100, 50, 50);
        Rectangle doctor3 = new Rectangle(400, 200, 50, 50);
        Rectangle doctor4 = new Rectangle(500, 200, 50, 50);

        doctor1.setFill(Color.BLUE);
        doctor2.setFill(Color.BLUE);
        doctor3.setFill(Color.BLUE);
        doctor4.setFill(Color.BLUE);

        simulationPane.getChildren().addAll(doctor1, doctor2, doctor3, doctor4);

        // Create patients (red circles)
        Circle patient1 = new Circle(50, 300, 15);
        Circle patient2 = new Circle(80, 320, 15);
        Circle patient3 = new Circle(110, 340, 15);
        Circle patient4 = new Circle(140, 360, 15);

        patient1.setFill(Color.RED);
        patient2.setFill(Color.RED);
        patient3.setFill(Color.RED);
        patient4.setFill(Color.RED);

        simulationPane.getChildren().addAll(patient1, patient2, patient3, patient4);

        // Update waiting label
        waitLabel.setText("Waiting times vary...");

        // Simulate wait times and movement
        simulatePatientMovement(patient1, 3, 350, -200, "Patient 1 is seeing the doctor...");
        simulatePatientMovement(patient2, 5, 450, -200, "Patient 2 is seeing the doctor...");
        simulatePatientMovement(patient3, 2, 350, -100, "Patient 3 is seeing the doctor...");
        simulatePatientMovement(patient4, 4, 450, -100, "Patient 4 is seeing the doctor...");
    }

    private void simulatePatientMovement(Circle patient, int waitTimeSeconds, int toX, int toY, String message) {
        PauseTransition waitTime = new PauseTransition(Duration.seconds(waitTimeSeconds));
        waitTime.setOnFinished(event -> {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(2), patient);
            transition.setToX(toX);
            transition.setToY(toY);
            transition.play();
            waitLabel.setText(message);
        });

        waitTime.play();
    }
}