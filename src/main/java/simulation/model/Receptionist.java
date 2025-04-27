package simulation.model;

public class Receptionist {
    private int busyUntil = 0;

    public boolean isAvailableAt(int time) {
        return time >= busyUntil;
    }

    public int getBusyUntil() {
        return busyUntil;
    }

    public void setBusyUntil(int busyUntil) {
        this.busyUntil = busyUntil;
    }
}
