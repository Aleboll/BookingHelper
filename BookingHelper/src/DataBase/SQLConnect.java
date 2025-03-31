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

        System.out.println("Testing connection to PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return null;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
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
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
        return connection;
    }
    public static boolean InitiateTables(){
        Connection connection = connect();
        if (connection != null) {
            try {
                java.sql.Statement statement = connection.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS bookings " +
                        "(id SERIAL PRIMARY KEY," +
                        " name VARCHAR(255)," +
                        " date VARCHAR(255)," +
                        " time VARCHAR(255)," +
                        " duration INT," +
                        " roomid INT," +
                        " type INT," +
                        " status INT)";
                statement.executeUpdate(sql);
                System.out.println("Table initiated successfully");
                return true;
            } catch (SQLException e) {
                System.out.println("Error creating table");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}