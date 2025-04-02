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

        // Создание панели для календаря
        JPanel calendarPanel = new JPanel();
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Отступы
        calendarPanel.setLayout(new BorderLayout());
        calendarPanel.add(mainPanel);

        // Создание панели для кнопок управления неделями
        JPanel weekControlPanel = new JPanel();
        weekControlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Отступы между кнопками

        JButton previousWeekButton = new JButton("Previous Week");
        previousWeekButton.addActionListener(e -> {
            // Логика перехода к предыдущей неделе
            System.out.println("Previous Week button clicked");
        });
        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> {
            // Логика перехода к текущей неделе
            System.out.println("Today button clicked");
        });
        JButton nextWeekButton = new JButton("Next Week");
        nextWeekButton.addActionListener(e -> {
            // Логика перехода к следующей неделе
            System.out.println("Next Week button clicked");
        });

        weekControlPanel.add(previousWeekButton);
        weekControlPanel.add(todayButton);
        weekControlPanel.add(nextWeekButton);

        // Создание панели для кнопок управления бронированием
        JPanel bookingControlPanel = new JPanel();
        bookingControlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Отступы между кнопками

        JButton addBookingButton = new JButton("Add Booking");
        addBookingButton.addActionListener(e -> {
            // Логика добавления бронирования
            System.out.println("Add Booking button clicked");
        });
        JButton deleteBookingButton = new JButton("Delete Booking");
        deleteBookingButton.addActionListener(e -> {
            // Логика удаления бронирования
            System.out.println("Delete Booking button clicked");
        });
        JButton editBookingButton = new JButton("Edit Booking");
        editBookingButton.addActionListener(e -> {
            // Логика редактирования бронирования
            System.out.println("Edit Booking button clicked");
        });

        bookingControlPanel.add(addBookingButton);
        bookingControlPanel.add(deleteBookingButton);
        bookingControlPanel.add(editBookingButton);

        // Настройка скроллинга
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setPreferredSize(new Dimension(1200, 600));

        // Добавление панелей в основной фрейм
        frame.setLayout(new BorderLayout());
        // Удалите эту строку, так как calendarPanel уже содержит mainPanel
// frame.add(calendarPanel, BorderLayout.NORTH);

// Создаем основную панель для всего содержимого
        JPanel contentPanel = new JPanel(new BorderLayout());

// Добавляем панель управления неделями в верхнюю часть
        contentPanel.add(weekControlPanel, BorderLayout.NORTH);

// Добавляем скроллируемую панель с календарем в центр
        contentPanel.add(scrollPane, BorderLayout.CENTER);

// Создаем панель для кнопок управления бронированием
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(bookingControlPanel, BorderLayout.CENTER);
// Можно добавить другие компоненты в bottomPanel при необходимости

// Добавляем нижнюю панель
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

// Добавляем основную панель в фрейм
        frame.add(contentPanel);

// Установите предпочтительный размер для scrollPane
        scrollPane.setPreferredSize(new Dimension(1200, 400));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // Настройка скроллинга



    }
}