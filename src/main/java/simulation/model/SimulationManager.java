package simulation.model;

import java.sql.*;
import java.util.*;

public class SimulationManager {

    private static final int WORK_START_TIME = 0;
    private static final int WORK_END_TIME = 480;
    private static final int NUM_RECEPTIONISTS = 2;
    private static final int NUM_DOCTORS = 4;

    private Receptionist[] receptionists = new Receptionist[NUM_RECEPTIONISTS];
    private Doctor[] doctors = new Doctor[NUM_DOCTORS];

    private int totalReceptionWaitTime = 0;
    private int totalReceptionServiceTime = 0;
    private int totalDoctorWaitTime = 0;
    private int totalDoctorServiceTime = 0;
    private int customerCount = 0;
    private int doctorAssignCounter = 0;

    private List<Integer> doctorIds = new ArrayList<>();

    public SimulationManager() {
        for (int i = 0; i < NUM_RECEPTIONISTS; i++) {
            receptionists[i] = new Receptionist();
        }
        for (int i = 0; i < NUM_DOCTORS; i++) {
            doctors[i] = new Doctor();
        }
    }

    public void startSimulation() {
        createDoctors();
        Random random = new Random();
        int currentTime = WORK_START_TIME;

        while (currentTime < WORK_END_TIME) {
            Customer customer = new Customer(customerCount + 1, currentTime, 0, 0, 0, 0);
            processCustomer(customer, currentTime);
            customerCount++;

            int interval;
            if (currentTime < 240) {
                interval = 2 + random.nextInt(4);
            } else if (currentTime < 360) {
                interval = 1 + random.nextInt(3);
            } else {
                interval = 2 + random.nextInt(6);
            }

            currentTime += interval;
            if (currentTime > WORK_END_TIME) currentTime = WORK_END_TIME;
        }

        int avgReceptionWaitTime = (customerCount == 0) ? 0 : totalReceptionWaitTime / customerCount;
        int avgReceptionServiceTime = (customerCount == 0) ? 0 : totalReceptionServiceTime / customerCount;
        int avgDoctorWaitTime = (customerCount == 0) ? 0 : totalDoctorWaitTime / customerCount;
        int avgDoctorServiceTime = (customerCount == 0) ? 0 : totalDoctorServiceTime / customerCount;

        System.out.println("=== Simulation Summary ===");
        System.out.println("Total Customers: " + customerCount);
        System.out.println("Avg Reception Wait Time: " + avgReceptionWaitTime + " minutes");
        System.out.println("Avg Reception Service Time: " + avgReceptionServiceTime + " minutes");
        System.out.println("Avg Doctor Wait Time: " + avgDoctorWaitTime + " minutes");
        System.out.println("Avg Doctor Service Time: " + avgDoctorServiceTime + " minutes");

        SimulationResultsDatabase.saveSimulationResults(customerCount, avgReceptionWaitTime, avgReceptionServiceTime, avgDoctorWaitTime, avgDoctorServiceTime, WORK_END_TIME);
    }

    private void createDoctors() {
        List<String> doctorNames = Arrays.asList(
                "Dr. Hussein", "Dr. Ali", "Dr. Rabab", "Dr. Noor", "Dr. Ahmed", "Dr. Sarah",
                "Dr. Youssef", "Dr. Karim", "Dr. Salma", "Dr. Mona", "Dr. Tarek", "Dr. Layla"
        );

        List<String> specializations = Arrays.asList(
                "Cardiology", "Neurology", "Pediatrics", "Orthopedics", "Dermatology", "Ophthalmology",
                "Oncology", "Gastroenterology", "Urology", "Psychiatry", "Radiology", "Anesthesiology"
        );

        Collections.shuffle(doctorNames);
        Collections.shuffle(specializations);

        try (Connection connection = DatabaseManager.connect();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM doctors");
            statement.executeUpdate("ALTER TABLE doctors AUTO_INCREMENT = 1");

            doctorIds.clear();

            for (int i = 0; i < NUM_DOCTORS; i++) {
                String name = doctorNames.get(i);
                String specialization = specializations.get(i);

                String insert = String.format("INSERT INTO doctors (name, specialization) VALUES ('%s', '%s')", name, specialization);
                statement.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int doctorId = generatedKeys.getInt(1);
                        doctorIds.add(doctorId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCustomer(Customer customer, int arrivalTime) {
        Random random = new Random();
        int receptionServiceTime = 4 + random.nextInt(4);
        int doctorServiceTime = 10 + random.nextInt(6);

        int receptionStart = findReceptionAvailableTime(arrivalTime, receptionServiceTime);
        int waitTimeReception = Math.max(0, receptionStart - arrivalTime);
        int receptionEnd = receptionStart + receptionServiceTime;

        totalReceptionWaitTime += waitTimeReception;
        totalReceptionServiceTime += receptionServiceTime;

        int doctorStart = findDoctorAvailableTime(receptionEnd);
        int waitTimeDoctor = Math.max(0, doctorStart - receptionEnd);
        int doctorEnd = doctorStart + doctorServiceTime;

        totalDoctorWaitTime += waitTimeDoctor;
        totalDoctorServiceTime += doctorServiceTime;

        customer.setReceptionStart(receptionStart);
        customer.setReceptionEnd(receptionEnd);
        customer.setDoctorStart(doctorStart);
        customer.setDoctorEnd(doctorEnd);

        saveCustomerData(customer);
    }

    private int findReceptionAvailableTime(int arrivalTime, int serviceTime) {
        Receptionist selectedReceptionist = null;
        int earliest = Integer.MAX_VALUE;

        for (Receptionist receptionist : receptionists) {
            if (receptionist.isAvailableAt(arrivalTime)) {
                receptionist.setBusyUntil(arrivalTime + serviceTime);
                return arrivalTime;
            } else if (receptionist.getBusyUntil() < earliest) {
                earliest = receptionist.getBusyUntil();
                selectedReceptionist = receptionist;
            }
        }

        if (selectedReceptionist != null) {
            selectedReceptionist.setBusyUntil(earliest + serviceTime);
            return earliest;
        }

        return arrivalTime;
    }

    private int findDoctorAvailableTime(int readyTime) {
        int index = doctorAssignCounter % NUM_DOCTORS;
        doctorAssignCounter++;

        Doctor doctor = doctors[index];
        Random random = new Random();

        if (doctor.isAvailableAt(readyTime)) {
            int delay = 1 + random.nextInt(3);
            int newStartTime = readyTime + delay;
            doctor.setBusyUntil(newStartTime);
            return newStartTime;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
