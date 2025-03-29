package Week;

public class Bookings {
    private final String name;
    private final String date;
    private final String time;
    private final String duration;
    private final String type;
    private final String status;

    public Bookings(String name, String date, String time, String duration, String type, String status) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.type = type;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
}
