package Interface.Client;

import Interface.Client.Client;

import java.sql.*;
import java.util.*;

public class ClientDAO {
    public static List<Interface.Client.Client> getAllClients() {
        String dbPassword = System.getenv("PASSWORD");
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hotel_booking", "postgres", dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM clients")) {
            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
}