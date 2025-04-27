package simulation.model;

public class Customer {
    private int id;
    private int arrivalTime;
    private int receptionStart;
    private int receptionEnd;
    private int doctorStart;
    private int doctorEnd;

    // إضافة المعاملات المفقودة في الكونسركتور (التي تحتوي على 6 معاملات)
    public Customer(int id, int arrivalTime, int receptionStart, int receptionEnd, int doctorStart, int doctorEnd) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.receptionStart = receptionStart;
        this.receptionEnd = receptionEnd;
        this.doctorStart = doctorStart;
        this.doctorEnd = doctorEnd;
    }

    // Getter and Setter Methods for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(int arrivalTime) { this.arrivalTime = arrivalTime; }

    public int getReceptionStart() { return receptionStart; }
    public void setReceptionStart(int receptionStart) { this.receptionStart = receptionStart; }

    public int getReceptionEnd() { return receptionEnd; }
    public void setReceptionEnd(int receptionEnd) { this.receptionEnd = receptionEnd; }

    public int getDoctorStart() { return doctorStart; }
    public void setDoctorStart(int doctorStart) { this.doctorStart = doctorStart; }

    public int getDoctorEnd() { return doctorEnd; }
    public void setDoctorEnd(int doctorEnd) { this.doctorEnd = doctorEnd; }
}
