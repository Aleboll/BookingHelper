import DataBase.SQLConnect;
import Interface.Interface;
import Week.Bookings;
import Week.Days;
import Week.Rooms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        SQLConnect.InitiateTables();
        Interface.initiateInterface();

        Bookings booking1 = new Bookings(1);
        System.out.println(Arrays.toString(booking1.toStringArray()));
        System.out.println(Days.GetTodaysWeekBeginingDate());
    }
}
