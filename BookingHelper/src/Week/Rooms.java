package Week;

import DataBase.SQLConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public Rooms(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT * FROM rooms WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.id = resultSet.getInt("id");
                this.name = resultSet.getString("name");
                this.price = resultSet.getInt("price");
                this.space = resultSet.getInt("space");
                this.extra = resultSet.getBoolean("extra");
                this.type = resultSet.getInt("type");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке комнаты: " + e.getMessage());
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
    }


    public static boolean isRoomBooked(String date, int room) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT COUNT(*) FROM booking_dates WHERE date = ? AND roomid = ?";
            statement = connection.prepareStatement(sql);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            statement.setDate(1, sqlDate);
            statement.setInt(2, room);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке бронирования комнаты: " + e.getMessage());
            return false;
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
    }

    public static int getCountOfRooms() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM rooms");

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при получении количества комнат: " + e.getMessage());
            return 0;
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
    }

    public static boolean addRoom(String[] roomData) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "INSERT INTO rooms (name, price, space, extra, type) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);

            statement.setString(1, roomData[0]);
            statement.setInt(2, Integer.parseInt(roomData[1]));
            statement.setInt(3, Integer.parseInt(roomData[2]));
            statement.setBoolean(4, Boolean.parseBoolean(roomData[3]));
            statement.setInt(5, Integer.parseInt(roomData[4]));

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении комнаты: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, statement);
            SQLConnect.releaseConnection(connection);
        }
    }

    public static String[] getRoomById(int id) {
        String[] room = new String[5];
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT name, price, space, extra, type FROM rooms WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                room[0] = resultSet.getString("name");
                room[1] = String.valueOf(resultSet.getInt("price"));
                room[2] = String.valueOf(resultSet.getInt("space"));
                room[3] = String.valueOf(resultSet.getBoolean("extra"));
                room[4] = String.valueOf(resultSet.getInt("type"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении комнаты: " + e.getMessage());
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
        return room;
    }

    public static String[] getRoomsNames() {
        String[] roomNames = new String[getCountOfRooms()];
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT name FROM rooms");

            int index = 0;
            while (resultSet.next()) {
                roomNames[index++] = resultSet.getString("name");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении названий комнат: " + e.getMessage());
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
        return roomNames;
    }
    private static void closeResources(ResultSet resultSet, Statement statement) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }

    public static int getRoomId(String roomName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT id FROM rooms WHERE name = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, roomName);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
            return -1; // Комната не найдена
        } catch (SQLException e) {
            System.err.println("Ошибка при получении ID комнаты: " + e.getMessage());
            return -1;
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getSpace() { return space; }
    public boolean isExtra() { return extra; }
    public int getType() { return type; }
}