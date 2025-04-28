package simulation.model;

public class SimulationResult {
    private int id;
    private int totalPatients;
    private int avgReceptionWaitTime;
    private int avgReceptionServiceTime;
    private int avgDoctorWaitTime;
    private int avgDoctorServiceTime;
    private int simulationTime;

    public SimulationResult(int id, int totalPatients, int avgReceptionWaitTime, int avgReceptionServiceTime, int avgDoctorWaitTime, int avgDoctorServiceTime, int simulationTime) {
        this.id = id;
        this.totalPatients = totalPatients;
        this.avgReceptionWaitTime = avgReceptionWaitTime;
        this.avgReceptionServiceTime = avgReceptionServiceTime;
        this.avgDoctorWaitTime = avgDoctorWaitTime;
        this.avgDoctorServiceTime = avgDoctorServiceTime;
        this.simulationTime = simulationTime;
    }

    public int getId() {
        return id;
    }

    public int getTotalPatients() {
        return totalPatients;
    }

    public int getAvgReceptionWaitTime() {
        return avgReceptionWaitTime;
    }

    public int getAvgReceptionServiceTime() {
        return avgReceptionServiceTime;
    }

    public int getAvgDoctorWaitTime() {
        return avgDoctorWaitTime;
    }

    public int getAvgDoctorServiceTime() {
        return avgDoctorServiceTime;
    }

    public int getSimulationTime() {
        return simulationTime;
    }
}
