package DataBase;
import Week.Bookings;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnect {
    //  Database credentials
    static final String DB_URL = "jdbc:postgresql://localhost:5432/hotel_booking";
    static final String USER = "postgres";
    static final String PASS = "Allabay123";

    public static Connection connect () {


        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return null;
        }

        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return null;
        }

        if (connection != null) {

        } else {
            System.out.println("Failed to make connection to database");
        }
        return connection;
    }
    public static boolean InitiateTables(){
        try {
            Connection connection = connect();
            assert connection != null;
            java.sql.Statement statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS rooms (id SERIAL PRIMARY KEY, name VARCHAR(255), price INT, space INT, extra BOOLEAN, type INT)";
            statement.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS bookings (id SERIAL PRIMARY KEY, name VARCHAR(255), date DATE, time TIME, duration INT, roomid INT, type INT, status INT)";
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}