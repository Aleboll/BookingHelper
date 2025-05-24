package Week;

import DataBase.SQLConnect;

import java.sql.*;


public enum Days {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6);

    private final int value;

    Days(int value) {
        this.value = value;
    }

    public static boolean deleteDatesByBookingId(int bookingId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = SQLConnect.getConnection();
            stmt = conn.prepareStatement("DELETE FROM booking_dates WHERE bookid = ?");
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении дат брони: " + e.getMessage());
            return false;
        } finally {
            SQLConnect.closeResources(null, stmt);
            SQLConnect.releaseConnection(conn);
        }
    }

    public int getValue() {
        return value;
    }
    public static int GetDayOfTheWeek(String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.get(java.util.Calendar.DAY_OF_WEEK) - 1;
    }
    public static String GetTodaysDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static String GetTodaysWeekBeginingDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static String[] GetWeekDates(String startDate) {
        String[] weekDates = new String[7];
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        String[] parts = startDate.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        calendar.set(year, month - 1, day);

        for (int i = 0; i < 7; i++) {
            weekDates[i] = String.format("%04d-%02d-%02d", year, month, day);
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
            year = calendar.get(java.util.Calendar.YEAR);
            month = calendar.get(java.util.Calendar.MONTH) + 1;
            day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        }
        return weekDates;
    }
    public static String GetDatePlusSevenDays(String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 7);
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH) + 1;
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
    public static int getDaysBetweenDates(String startDate, String endDate) {
        String[] startParts = startDate.split("-");
        String[] endParts = endDate.split("-");

        java.util.Calendar startCalendar = java.util.Calendar.getInstance();
        startCalendar.set(Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1]) - 1, Integer.parseInt(startParts[2]));

        java.util.Calendar endCalendar = java.util.Calendar.getInstance();
        endCalendar.set(Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1]) - 1, Integer.parseInt(endParts[2]));

        long diffInMillis = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
        return (int) (diffInMillis / (1000 * 60 * 60 * 24));
    }
    public  static String GetDateMinusSevenDays(String date) {
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month - 1, day);
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -7);
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH) + 1;
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%04d-%02d-%02d", year, month, day);
    }
    public static boolean addDates(String[] date){
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "INSERT INTO booking_dates (date, roomid, bookid) " +
                    "VALUES (?, ?, ?)";
            statement = connection.prepareStatement(sql);

            java.sql.Date sqlDate = java.sql.Date.valueOf(date[0]);

            statement.setDate(1, sqlDate);
            statement.setInt(2, Integer.parseInt(date[1]));
            statement.setInt(3, Integer.parseInt(date[2]));

            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении дней: " + e.getMessage());
            return false;
        } finally {
            closeResources(null, statement);
            SQLConnect.releaseConnection(connection);
        }
    }
    public static void closeResources(ResultSet resultSet, Statement statement) {
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
    public static int getBookingIdByDayAndRoom(String date, int room) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = SQLConnect.getConnection();
            String sql = "SELECT bookid FROM booking_dates WHERE date = ? AND roomid = ?";
            statement = connection.prepareStatement(sql);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            statement.setDate(1, sqlDate);
            statement.setInt(2, room);
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
}
