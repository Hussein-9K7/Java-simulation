package simulation.model;

public class SimulationResult {
    private int id;
    private int totalPatients;
    private int totalWaitTime;
    private int simulationTime;

    public SimulationResult(int id, int totalPatients, int totalWaitTime, int simulationTime) {
        this.id = id;
        this.totalPatients = totalPatients;
        this.totalWaitTime = totalWaitTime;
        this.simulationTime = simulationTime;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTotalPatients() { return totalPatients; }
    public void setTotalPatients(int totalPatients) { this.totalPatients = totalPatients; }
    public int getTotalWaitTime() { return totalWaitTime; }
    public void setTotalWaitTime(int totalWaitTime) { this.totalWaitTime = totalWaitTime; }
    public int getSimulationTime() { return simulationTime; }
    public void setSimulationTime(int simulationTime) { this.simulationTime = simulationTime; }
}
