package simulation.model;

public class CustomerView {
    private int id;
    private String arrivalTime;
    private String receptionStart;
    private String receptionEnd;
    private String doctorStart;
    private String doctorEnd;

    public CustomerView(int id, int arrival, int receptionStart, int receptionEnd, int doctorStart, int doctorEnd) {
        this.id = id;
        this.arrivalTime = formatTime(arrival);
        this.receptionStart = formatTime(receptionStart);
        this.receptionEnd = formatTime(receptionEnd);
        this.doctorStart = formatTime(doctorStart);
        this.doctorEnd = formatTime(doctorEnd);
    }

    private String formatTime(int minutes) {
        int hours = 8 + (minutes / 60);
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }

    // Getters
    public int getId() { return id; }
    public String getArrivalTime() { return arrivalTime; }
    public String getReceptionStart() { return receptionStart; }
    public String getReceptionEnd() { return receptionEnd; }
    public String getDoctorStart() { return doctorStart; }
    public String getDoctorEnd() { return doctorEnd; }
}
