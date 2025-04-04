package Interface;

import DataBase.SQLConnect;
import Week.Bookings;
import Week.Days;
import Week.Rooms;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class Interface {
    private static final String[] WEEK_DAYS = {"Room", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"};
    private static final Dimension SCROLL_PANE_SIZE = new Dimension(1200, 600);
    private static final int BUTTON_SPACING = 10;
    private static volatile boolean isUpdating = false;

    public static void initiateInterface() {
        // Инициализация базы данных
        if (!SQLConnect.InitiateTables()) {
            showErrorDialog("Ошибка при инициализации базы данных");
            return;
        }

        try {
            // Создание главного окна
            JFrame mainFrame = createMainFrame();
            AtomicReference<String> selectedDate = new AtomicReference<>(Days.GetTodaysWeekBeginingDate());

            // Создание компонентов интерфейса
            JScrollPane calendarScrollPane = createCalendarScrollPane(selectedDate.get());
            JPanel weekNavigationPanel = createWeekNavigationPanel(selectedDate, calendarScrollPane, mainFrame);
            JPanel bookingManagementPanel = createBookingManagementPanel();

            // Компоновка интерфейса
            assembleInterface(mainFrame, weekNavigationPanel, calendarScrollPane, bookingManagementPanel);

            // Обработка закрытия окна
            mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    SQLConnect.closeAllConnections();
                    mainFrame.dispose();
                }
            });

            displayMainFrame(mainFrame);
        } catch (Exception e) {
            showErrorDialog("Ошибка при создании интерфейса: " + e.getMessage());
            SQLConnect.closeAllConnections();
        }
    }

    private static JPanel createBookingManagementPanel() {
        JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, BUTTON_SPACING, BUTTON_SPACING));
        JButton addBookingButton = new JButton("Добавить бронь");
        JButton deleteBookingButton = new JButton("Удалить бронь");
        JButton addRoomButton = new JButton("Добавить номер");

        addBookingButton.addActionListener(e -> {
            // Логика добавления бронирования
            AdBookingMenu.showAdBookingMenu();
        });

        deleteBookingButton.addActionListener(e -> {
            // Логика удаления бронирования
            System.out.println("Удаление бронирования...");
        });
        addRoomButton.addActionListener(e -> {
            AddRoomMenu.showAddRoomMenu();

        });

        bookingPanel.add(addBookingButton);
        bookingPanel.add(deleteBookingButton);
        bookingPanel.add(addRoomButton);
        return bookingPanel;
    }

    private static JFrame createMainFrame() {
        JFrame frame = new JFrame("BookMe - Система бронирования");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        return frame;
    }

    private static JScrollPane createCalendarScrollPane(String startDate) {
        JPanel calendarPanel = createCalendarPanel(startDate);
        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setPreferredSize(SCROLL_PANE_SIZE);
        return scrollPane;
    }

    private static JPanel createCalendarPanel(String startDate) {
        String[] weekDates = Days.GetWeekDates(startDate);
        int roomCount = Rooms.getCountOfRooms();
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(2, 2, 2, 2);

        // Добавление заголовков
        for (int i = 0; i < WEEK_DAYS.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            String date = (i > 0 && i-1 < weekDates.length) ? formatDate(weekDates[i-1]) : null;
            panel.add(createCalendarCell(WEEK_DAYS[i], true, date, false), gbc);
        }

        for (int roomId = 1; roomId <= roomCount; roomId++) {
            gbc.gridx = 0;
            gbc.gridy = roomId;
            panel.add(createCalendarCell(Rooms.getRoomById(roomId)[0], true, null, false), gbc);

            for (int dayIndex = 1; dayIndex < WEEK_DAYS.length; dayIndex++) {
                gbc.gridx = dayIndex;
                if (dayIndex-1 < weekDates.length && Rooms.isRoomBooked(weekDates[dayIndex-1], roomId)) {
                    String bookingInfo = Bookings.getBookingById(Days.getBookingIdByDayAndRoom(weekDates[dayIndex-1], roomId))[0];
                    panel.add(createCalendarCell(bookingInfo, false, null, true), gbc);
                } else {
                    panel.add(createCalendarCell("", false, null, false), gbc);
                }
            }
        }

        return panel;
    }

    // Вспомогательный метод для форматирования даты
    private static String formatDate(String date) {
        try {
            // Предполагаем, что date в формате yyyy-MM-dd
            String[] parts = date.split("-");
            if (parts.length == 3) {
                return String.format("%s.%s.%s", parts[2], parts[1], parts[0]);
            }
        } catch (Exception e) {
            // В случае ошибки просто вернем исходную дату
        }
        return date;
    }

    private static JLabel createCalendarCell(String text, boolean isHeader, String date, boolean isBooked) {
        String cellContent;
        if (isHeader && date != null) {
            // Для заголовка добавляем дату мелким шрифтом сверху
            cellContent = String.format("<html><div style='text-align: center;'><small>%s</small><br/>%s</div></html>",
                    date, text.replace("\n", "<br/>"));
        } else {
            cellContent = String.format("<html><div style='text-align: center;'>%s</div></html>",
                    text.replace("\n", "<br/>"));
        }

        JLabel cell = new JLabel(cellContent);
        cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        cell.setPreferredSize(new Dimension(100, 80));
        cell.setHorizontalAlignment(SwingConstants.CENTER);
        cell.setVerticalAlignment(SwingConstants.CENTER);


        if (isBooked) {
            cell.setBackground(new Color(108, 151, 207));
            cell.setOpaque(true);
        } else {
            cell.setBackground(Color.WHITE);
            cell.setOpaque(true);
        }
        if (isHeader) {
            cell.setBackground(new Color(19, 95, 193));
            cell.setOpaque(true);
            cell.setFont(cell.getFont().deriveFont(Font.BOLD));
            cell.setForeground(Color.WHITE);
        }
        return cell;
    }

    private static JPanel createWeekNavigationPanel(AtomicReference<String> selectedDate,
                                                    JScrollPane scrollPane,
                                                    JFrame frame) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, BUTTON_SPACING, BUTTON_SPACING));

        panel.add(createNavButton("Предыдущая", () -> {
            selectedDate.set(Days.GetDateMinusSevenDays(selectedDate.get()));
            updateCalendar(selectedDate.get(), scrollPane);
        }));

        panel.add(createNavButton("Сегодня", () -> {
            selectedDate.set(Days.GetTodaysWeekBeginingDate());
            updateCalendar(selectedDate.get(), scrollPane);
        }));

        panel.add(createNavButton("Следующая", () -> {
            selectedDate.set(Days.GetDatePlusSevenDays(selectedDate.get()));
            updateCalendar(selectedDate.get(), scrollPane);
        }));

        return panel;
    }

    private static JButton createNavButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> {
            if (!isUpdating) {
                isUpdating = true;
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        action.run();
                        return null;
                    }

                    @Override
                    protected void done() {
                        isUpdating = false;
                    }
                }.execute();
            }
        });
        return button;
    }

    private static void updateCalendar(String date, JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            scrollPane.setViewportView(createCalendarPanel(date));
        });
    }

    private static void assembleInterface(JFrame frame, JPanel weekPanel,
                                          JScrollPane calendarPane, JPanel bookingPanel) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(weekPanel, BorderLayout.NORTH);
        contentPanel.add(calendarPane, BorderLayout.CENTER);
        contentPanel.add(bookingPanel, BorderLayout.SOUTH);
        frame.add(contentPanel);
    }

    private static void displayMainFrame(JFrame frame) {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Interface::initiateInterface);
    }
    public static void updateInterface() {
        SwingUtilities.invokeLater(Interface::initiateInterface);;
    }
}