package simulation.model;

public class Doctor {
    private int busyUntil = 0; // الوقت الي يكون بيه مشغول

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
