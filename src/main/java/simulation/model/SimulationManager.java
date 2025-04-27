package simulation.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class SimulationManager {

    private static final int WORK_START_TIME = 0; // 08:00
    private static final int WORK_END_TIME = 480; // 16:00 (8 hours)

    private static final int NUM_RECEPTIONISTS = 2;
    private static final int NUM_DOCTORS = 4;

    private Receptionist[] receptionists = new Receptionist[NUM_RECEPTIONISTS];
    private Doctor[] doctors = new Doctor[NUM_DOCTORS];

    private Queue<Customer> waitingReceptionQueue = new LinkedList<>();
    private Queue<Customer> waitingDoctorQueue = new LinkedList<>();

    private int totalWaitTime = 0;
    private int customerCount = 0;

    private int doctorAssignCounter = 0;

    public SimulationManager() {
        for (int i = 0; i < NUM_RECEPTIONISTS; i++) {
            receptionists[i] = new Receptionist();
        }
        for (int i = 0; i < NUM_DOCTORS; i++) {
            doctors[i] = new Doctor();
        }
    }

    public void startSimulation() {
        Random random = new Random();
        int numberOfCustomers = 90 + random.nextInt(61); // 90 to 150

        int intervalBetweenCustomers = WORK_END_TIME / numberOfCustomers; // spread over the day

        int currentTime = WORK_START_TIME;

        for (int i = 1; i <= numberOfCustomers; i++) {
            Customer customer = new Customer(i, currentTime, 0, 0, 0, 0);
            processCustomer(customer, currentTime);

            currentTime += intervalBetweenCustomers;
            if (currentTime > WORK_END_TIME) currentTime = WORK_END_TIME;
        }

        // بعد ما تخلص تسجل النتيجة
        SimulationResultsDatabase.saveSimulationResults(customerCount, totalWaitTime, WORK_END_TIME);
    }

    private void processCustomer(Customer customer, int arrivalTime) {
        customerCount++;

        int receptionServiceTime = 2 + new Random().nextInt(4); // 2-5 minutes
        int doctorServiceTime = 5 + new Random().nextInt(6);    // 5-10 minutes

        // Reception handling
        int receptionStart = findReceptionAvailableTime(arrivalTime);
        int receptionEnd = receptionStart + receptionServiceTime;

        int waitTimeReception = receptionStart - arrivalTime;
        totalWaitTime += waitTimeReception;

        // Doctor handling
        int doctorStart = findDoctorAvailableTime(receptionEnd);
        int doctorEnd = doctorStart + doctorServiceTime;

        // Set details
        customer.setReceptionStart(receptionStart);
        customer.setReceptionEnd(receptionEnd);
        customer.setDoctorStart(doctorStart);
        customer.setDoctorEnd(doctorEnd);

        saveCustomerData(customer);
    }

    private int findReceptionAvailableTime(int arrivalTime) {
        int earliest = Integer.MAX_VALUE;
        for (Receptionist receptionist : receptionists) {
            if (receptionist.isAvailableAt(arrivalTime)) {
                receptionist.setBusyUntil(arrivalTime);
                return arrivalTime;
            } else if (receptionist.getBusyUntil() < earliest) {
                earliest = receptionist.getBusyUntil();
            }
        }
        for (Receptionist receptionist : receptionists) {
            if (receptionist.getBusyUntil() == earliest) {
                receptionist.setBusyUntil(earliest);
                return earliest;
            }
        }
        return arrivalTime;
    }

    private int findDoctorAvailableTime(int readyTime) {
        int index = doctorAssignCounter % NUM_DOCTORS;
        doctorAssignCounter++;

        Doctor doctor = doctors[index];

        if (doctor.isAvailableAt(readyTime)) {
            doctor.setBusyUntil(readyTime);
            return readyTime;
        } else {
            int available = doctor.getBusyUntil();
            doctor.setBusyUntil(available);
            return available;
        }
    }

    private void saveCustomerData(Customer customer) {
        String query = "INSERT INTO customers (arrival_time, reception_start, reception_end, doctor_start, doctor_end) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, customer.getArrivalTime());
            statement.setInt(2, customer.getReceptionStart());
            statement.setInt(3, customer.getReceptionEnd());
            statement.setInt(4, customer.getDoctorStart());
            statement.setInt(5, customer.getDoctorEnd());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
