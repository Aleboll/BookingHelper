package Interface;
import Week.Bookings;
import Week.Rooms;

import javax.swing.*;
import java.awt.*;

public class Interface {
    public static void InitiateInterface(){
        // Create the main frame
        JFrame frame = new JFrame("BookMe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 1080);
        frame.setVisible(true);

        // Create the table
        String[] columnNames = {
                "Пн",
                "Вт",
                "Ср",
                "Чт",
                "Пт",
                "Сб",
                "Вс"
        };
        String[] weekDates = Week.Days.GetWeekDates(Week.Days.GetTodaysWeekBeginingDate());
        System.out.println(weekDates[0]);
        String[][] data = new String[Rooms.GetCountOfRooms()][];
        System.out.println(Rooms.GetCountOfRooms());
        for (int i = 0; i < Rooms.GetCountOfRooms(); i++) {
            data[i] = new String[7];
            for (int j = 0; j < 7; j++) {
                data[i][j] = Bookings.GetBookingByDayAndRoom(weekDates[j], i + 1)[0];
                System.out.println(data[i][j]);
            }
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(scrollPane, BorderLayout.NORTH);
        frame.setPreferredSize(new Dimension(450, 400));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
}
