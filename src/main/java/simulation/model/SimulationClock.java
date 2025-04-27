package simulation.model;

public class SimulationClock {
    private int currentTime = 0;

    public void tick() {
        currentTime++;
    }

    public int getTime() {
        return currentTime;
    }

    public String getFormattedTime() {
        int hour = 8 + (currentTime / 60);
        int minute = currentTime % 60;
        return String.format("%02d:%02d", hour, minute);
    }

    public boolean isWithinWorkingHours() {
        return currentTime < 480;
    }
}
