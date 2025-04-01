package Week;

import DataBase.SQLConnect;

import java.sql.Connection;

import static Week.Rooms.IfRoomIsBooked;

public class Bookings {
    private  String name;
    private  String date;
    private  String time;
    private  int duration;
    private  int room;
    private  int type;
    private  int status;

    public Bookings(String name, String date, String time, int duration, int room, int type, int status) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.room = room;
        this.type = type;
        this.status = status;
    }
    public Bookings(int id) {
        try {
            Connection connection = SQLConnect.connect();
            assert connection != null;
            java.sql.Statement statement = connection.createStatement();
            String sql = "SELECT * FROM bookings WHERE id = " + id;
            java.sql.ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                this.name = resultSet.getString("name");
                this.date = resultSet.getString("date");
                this.time = resultSet.getString("time");
                this.room = Integer.parseInt(resultSet.getString("roomid"));
                this.duration = Integer.parseInt(resultSet.getString("duration"));
                this.type = Integer.parseInt(resultSet.getString("type"));
                this.status = Integer.parseInt(resultSet.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] GetBookingByDayAndRoom(String date, int room){
        String[] booking = new String[7];
        try {
            Connection connection = SQLConnect.connect();
            assert connection != null;
            java.sql.Statement statement = connection.createStatement();
            String sql = "SELECT * FROM bookings WHERE date = '" + date + "' AND roomid = " + room;
            java.sql.ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                booking[0] = resultSet.getString("name");
                booking[1] = resultSet.getString("date");
                booking[2] = resultSet.getString("time");
                booking[3] = String.valueOf(resultSet.getInt("duration"));
                booking[4] = String.valueOf(resultSet.getInt("roomid"));
                booking[5] = String.valueOf(resultSet.getInt("type"));
                booking[6] = String.valueOf(resultSet.getInt("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return booking;
    }
    public static boolean AddBooking(String[] booking){
        if (IfRoomIsBooked(booking[1], Integer.parseInt(booking[4]))) {
            System.out.println("Room is already booked");
            return false;
        }
        try {
            Connection connection = SQLConnect.connect();
            assert connection != null;
            java.sql.Statement statement = connection.createStatement();
            String sql = "INSERT INTO bookings (name, date, time, duration, roomid, type, status) VALUES ('" + booking[0] + "', '" + booking[1] + "', '" + booking[2] + "', " + booking[3] + ", " + booking[4] + ", " + booking[5] + ", " + booking[6] + ")";
            statement.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public String[] ToStringArray() {
        String[] booking = new String[7];
        booking[0] = name;
        booking[1] = date;
        booking[2] = time;
        booking[3] = String.valueOf(duration);
        booking[4] = String.valueOf(room);
        booking[5] = String.valueOf(type);
        booking[6] = String.valueOf(status);
        return booking;
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

    public int getDuration() {
        return duration;
    }

    public int getType() {
        return type;
    }

    public int getStatus() {
        return status;
    }

}
