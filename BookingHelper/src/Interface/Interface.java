package Interface;
import Week.Bookings;
import Week.Rooms;

import javax.swing.*;
import java.awt.*;

public class Interface {
    public static void InitiateInterface() {
        // Create the main frame
        JFrame frame = new JFrame("BookMe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] days = {"Room", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] weekDates = Week.Days.GetWeekDates(Week.Days.GetTodaysWeekBeginingDate());

        int roomCount = Rooms.GetCountOfRooms();
        String[][] data = new String[roomCount + 1][8]; // +1 для заголовка

        // Инициализация массива пустыми строками
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = "";
            }
        }

        // Заполняем заголовки дней
        System.arraycopy(days, 0, data[0], 0, Math.min(days.length, 8));

        // Заполняем данные о комнатах и бронированиях
        for (int i = 1; i <= roomCount; i++) {
            // Название комнаты (индекс i-1, так как комнаты нумеруются с 1)
            String[] roomInfo = Rooms.GetRoomById(i);
            data[i][0] = (roomInfo != null && roomInfo.length > 0) ? roomInfo[0] : "Room " + i;

            for (int j = 1; j < 8; j++) {
                if (j-1 < weekDates.length && Rooms.IfRoomIsBooked(weekDates[j-1], i)) {
                    String[] bookingInfo = Bookings.GetBookingByDayAndRoom(weekDates[j-1], i);
                    data[i][j] = (bookingInfo != null && bookingInfo.length > 0) ? bookingInfo[0] : "";
                }
            }
        }

        JPanel mainPanel = new JPanel(new GridLayout(roomCount + 1, 8)); // +1 для заголовка

        // Добавляем компоненты в панель
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                JTextArea textArea = new JTextArea(data[i][j]);
                textArea.setEditable(false); // Делаем текст нередактируемым
                textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                mainPanel.add(textArea);
            }
        }

        // Настройка скроллинга
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setPreferredSize(new Dimension(1200, 600));

        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}