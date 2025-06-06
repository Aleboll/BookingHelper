package Week;

import DataBase.SQLConnect;

import java.sql.*;

public class Bookings {
    private String name;
    private String date;
    private String time;
    private int duration;
    private int room;
    private int type;
    private int status;

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
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT * FROM bookings WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                this.name = resultSet.getString("name");
                this.date = String.valueOf(resultSet.getDate("date"));
                this.time = String.valueOf(resultSet.getTime("time"));
                this.room = resultSet.getInt("roomid");
                this.duration = resultSet.getInt("duration");
                this.type = resultSet.getInt("type");
                this.status = resultSet.getInt("status");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке бронирования: " + e.getMessage());
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
    }

    public static String[] getBookingByDayAndRoom(String date, int room) {
        String[] booking = new String[8];
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT * FROM bookings WHERE date = ? AND roomid = ?";
            statement = connection.prepareStatement(sql);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            statement.setDate(1, sqlDate);
            statement.setInt(2, room);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                booking[0] = resultSet.getString("name");
                booking[1] = String.valueOf(resultSet.getDate("date"));
                booking[2] = String.valueOf(resultSet.getTime("time"));
                booking[3] = String.valueOf(resultSet.getInt("duration"));
                booking[4] = String.valueOf(resultSet.getInt("roomid"));
                booking[5] = String.valueOf(resultSet.getInt("type"));
                booking[6] = String.valueOf(resultSet.getInt("status"));
                booking[7] = String.valueOf(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении бронирования: " + e.getMessage());
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
        return booking;
    }


    public static boolean addBooking(String[] booking) {
        if (Rooms.isRoomBooked(booking[1], Integer.parseInt(booking[4]))) {
            System.out.println("Комната уже забронирована");
            return false;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "INSERT INTO bookings (name, date, time, duration, roomid, type, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            java.sql.Time timeValue = java.sql.Time.valueOf(booking[2] + ":00"); // Add seconds if missing
            java.sql.Date dateValue = java.sql.Date.valueOf(booking[1]); // Convert to java.sql.Date

            statement.setString(1, booking[0]);
            statement.setDate(2, dateValue);
            statement.setTime(3, timeValue);
            statement.setInt(4, Integer.parseInt(booking[3]));
            statement.setInt(5, Integer.parseInt(booking[4]));
            statement.setInt(6, Integer.parseInt(booking[5]));
            statement.setInt(7, Integer.parseInt(booking[6]));

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении бронирования: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, statement);
            SQLConnect.releaseConnection(connection);
        }
    }

    public static int getBookingIdByDateAndRoom(String date, int roomId){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT bookid FROM booking_dates WHERE date = ? AND roomid = ?";
            statement = connection.prepareStatement(sql);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            statement.setDate(1, sqlDate);
            statement.setInt(2, roomId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("bookid");
            }
            return -1; // Если не найдено
        } catch (SQLException e) {
            System.err.println("Ошибка при получении ID бронирования: " + e.getMessage());
            return -1;
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
    }
    private static void closeResources(ResultSet resultSet, Statement statement) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }

    public static boolean updateBooking(String[] updatedData) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = SQLConnect.getConnection();
            String sql = "UPDATE bookings SET name=?, date=?, time=?, duration=?, " +
                    "roomid=?, type=?, status=? WHERE id=?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, updatedData[0]);
            stmt.setDate(2, Date.valueOf(updatedData[1]));
            stmt.setTime(3, Time.valueOf(updatedData[2] + ":00"));
            stmt.setInt(4, Integer.parseInt(updatedData[3]));
            stmt.setInt(5, Integer.parseInt(updatedData[4]));
            stmt.setInt(6, Integer.parseInt(updatedData[5]));
            stmt.setInt(7, Integer.parseInt(updatedData[6]));
            stmt.setInt(8, Integer.parseInt(updatedData[7]));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении брони: " + e.getMessage());
            return false;
        } finally {
            SQLConnect.closeResources(null, stmt);
            SQLConnect.releaseConnection(conn);
        }
    }

    public String[] toStringArray() {
        return new String[] {
                name,
                date,
                time,
                String.valueOf(duration),
                String.valueOf(room),
                String.valueOf(type),
                String.valueOf(status)
        };
    }
    public static String[] getBookingById(int id){
        String[] booking = new String[8];
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT * FROM bookings WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                booking[0] = resultSet.getString("name");
                booking[1] = String.valueOf(resultSet.getDate("date"));
                booking[2] = String.valueOf(resultSet.getTime("time"));
                booking[3] = String.valueOf(resultSet.getInt("duration"));
                booking[4] = String.valueOf(resultSet.getInt("roomid"));
                booking[5] = String.valueOf(resultSet.getInt("type"));
                booking[6] = String.valueOf(resultSet.getInt("status"));
                booking[7] = String.valueOf(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении бронирования: " + e.getMessage());
        } finally {
            closeResources(resultSet, statement);
            SQLConnect.releaseConnection(connection);
        }
        return booking;
    }
    public static boolean deleteBooking(int bookingId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {

            connection = SQLConnect.getConnection();
            String sql = "DELETE FROM booking_dates WHERE bookid = ?";
            statement = connection.prepareStatement(sql);

            // Устанавливаем параметры (индексы 1 и 2)
            statement.setInt(1, bookingId);
            System.out.println(sql);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                sql = "DELETE FROM bookings WHERE id = ?";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, bookingId);
                System.out.println(sql);
                affectedRows = statement.executeUpdate();
            }

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Закрываем ресурсы в обратном порядке
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

    }
    // Геттеры
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getDuration() { return duration; }
    public int getType() { return type; }
    public int getStatus() { return status; }
    public int getRoom() { return room; }
}