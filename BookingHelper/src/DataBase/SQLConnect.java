package DataBase;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SQLConnect {
    static String dbPassword = System.getenv("PASSWORD");
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/hotel_booking";
    private static final String USER = "postgres";
    private static final String PASS = dbPassword;
    private static final int MAX_CONNECTIONS = 10;
    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(MAX_CONNECTIONS);
    private static boolean driverLoaded = false;

    static {
        try {
            Class.forName("org.postgresql.Driver");
            driverLoaded = true;
            // Инициализация пула соединений
            for (int i = 0; i < MAX_CONNECTIONS; i++) {
                connectionPool.add(createNewConnection());
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Ошибка инициализации драйвера: " + e.getMessage());
        }
    }

    private static Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static Connection getConnection() throws SQLException {
        if (!driverLoaded) {
            throw new SQLException("Драйвер базы данных не загружен");
        }

        try {
            // Получаем соединение из пула с таймаутом
            Connection conn = connectionPool.poll(2, TimeUnit.SECONDS);
            if (conn == null || conn.isClosed()) {
                return createNewConnection();
            }
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Прервано ожидание соединения");
        }
    }

    public static void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connectionPool.offer(connection);
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при возврате соединения в пул: " + e.getMessage());
            }
        }
    }

    public static boolean InitiateTables() {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();

            String roomsTableSQL = "CREATE TABLE IF NOT EXISTS rooms (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "price INT, " +
                    "space INT, " +
                    "extra BOOLEAN, " +
                    "type INT)";
            statement.executeUpdate(roomsTableSQL);

            String bookingsTableSQL = "CREATE TABLE IF NOT EXISTS bookings (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "date DATE, " +
                    "time TIME, " +
                    "duration INT, " +
                    "roomid INT, " +
                    "type INT, " +
                    "status INT )" ;
            statement.executeUpdate(bookingsTableSQL);

            String bookingDatesTableSQL = "CREATE TABLE IF NOT EXISTS booking_dates (" +
                    "id SERIAL PRIMARY KEY, " +
                    "date DATE, " +
                    "roomid INT, " +
                    "bookid INT)";
            statement.executeUpdate(bookingDatesTableSQL);

            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при создании таблиц: " + e.getMessage());
            return false;
        } finally {
            if (statement != null) try { statement.close(); } catch (SQLException e) { /* игнорируем */ }
            releaseConnection(connection);
        }
    }

    public static void closeAllConnections() {
        for (Connection conn : connectionPool) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
        connectionPool.clear();
    }

    public static void closeResources(Object o, PreparedStatement stmt) {
    }
}