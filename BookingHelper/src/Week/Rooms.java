package Week;

import DataBase.SQLConnect;

import java.sql.Connection;

public class Rooms {
    private int id;
    private String name;
    private int price;
    private int space;
    private boolean extra;
    private int type;

    public Rooms(int id, String name, int price, int space, boolean extra, int type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.space = space;
        this.extra = extra;
        this.type = type;
    }

    public Rooms(int id){
        try {
            Connection connection = SQLConnect.connect();
            assert connection != null;
            java.sql.Statement statement = connection.createStatement();
            String sql = "SELECT * FROM rooms WHERE id = " + id;
            java.sql.ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                this.id = resultSet.getInt("id");
                this.name = resultSet.getString("name");
                this.price = resultSet.getInt("price");
                this.space = resultSet.getInt("space");
                this.extra = resultSet.getBoolean("extra");
                this.type = resultSet.getInt("type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean IfRoomIsBooked(String date, int room) {
        try {
            Connection connection = SQLConnect.connect();
            assert connection != null;
            java.sql.Statement statement = connection.createStatement();
            String sql = "SELECT * FROM bookings WHERE date = '" + date + "' AND roomid = " + room;
            java.sql.ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static int GetCountOfRooms(){
        try {
            Connection connection = SQLConnect.connect();
            assert connection != null;
            java.sql.Statement statement = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM rooms";
            java.sql.ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static boolean AddRoom(String[] room) {
        try {
            Connection connection = SQLConnect.connect();
            assert connection != null;
            java.sql.Statement statement = connection.createStatement();
            String sql = "INSERT INTO rooms (name, price, space, extra, type) VALUES ('" + room[0] + "', " + room[1] + ", " + room[2] + ", " + room[3] + ", " + room[4] + ")";
            statement.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
