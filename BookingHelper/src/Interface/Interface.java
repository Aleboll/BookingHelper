package Interface;

import DataBase.SQLConnect;
import Interface.Client.ClientLibraryWindow;
import Week.Bookings;
import Week.Days;
import Week.Rooms;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicReference;

public class Interface {
    private static final String[] WEEK_DAYS = {"Room", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"};
    private static final Dimension SCROLL_PANE_SIZE = new Dimension(1200, 600);
    private static final int BUTTON_SPACING = 10;
    private static volatile boolean isUpdating = false;

    public static void initiateInterface() {
        if (!SQLConnect.InitiateTables()) {
            showErrorDialog("Ошибка при инициализации базы данных");
            return;
        }

        try {
            JFrame mainFrame = createMainFrame();
            AtomicReference<String> selectedDate = new AtomicReference<>(Days.GetTodaysWeekBeginingDate());

            JScrollPane calendarScrollPane = createCalendarScrollPane(selectedDate.get());
            JPanel weekNavigationPanel = createWeekNavigationPanel(selectedDate, calendarScrollPane, mainFrame);
            JPanel bookingManagementPanel = createBookingManagementPanel();

            assembleInterface(mainFrame, weekNavigationPanel, calendarScrollPane, bookingManagementPanel);

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
        JButton addRoomButton = new JButton("Добавить номер");
        JButton clientsButton = new JButton("Клиенты");
        clientsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clientsButton.addActionListener(e -> {
            new ClientLibraryWindow(); // открытие окна клиентов
        });
        bookingPanel.add(clientsButton); // или куда ты размещаешь кнопки

        addBookingButton.addActionListener(e -> AdBookingMenu.showAdBookingMenu());
        addRoomButton.addActionListener(e -> AddRoomMenu.showAddRoomMenu());

        bookingPanel.add(addBookingButton);
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


    private static JLabel createCalendarCell(String text, boolean isHeader, String date, boolean isBooked,
                                             int roomId, String dayDate) {
        String cellContent;
        if (isHeader && date != null) {
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
        cell.setOpaque(true);

        if (isBooked) {
            cell.setBackground(new Color(108, 151, 207));
        } else {
            cell.setBackground(Color.WHITE);
        }

        if (isHeader) {
            cell.setBackground(new Color(19, 95, 193));
            cell.setFont(cell.getFont().deriveFont(Font.BOLD));
            cell.setForeground(Color.WHITE);
        } else {
            cell.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    try {
                        handleCellClick(roomId, dayDate, isBooked, text);
                    } catch (SQLException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    cell.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                }
            });
            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        return cell;
    }

    private static void handleCellClick(int roomId, String date, boolean isBooked, String bookingInfo) throws SQLException, ParseException {
        if (isBooked) {
            String roomName = Rooms.getRoomById(roomId)[0];
            String formattedDate = formatDate(date);
            String[] options = {"Edit", "Delete", "Cancel"};

            int choice = JOptionPane.showOptionDialog(null,
                    "<html><b>Booking Info:</b><br>" +
                            "Room: " + roomName + "<br>" +
                            "Date: " + formattedDate + "<br>" +
                            "Details: " + bookingInfo + "</html>",
                    "Booking Management",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (choice == 0) { // Edit выбрана
                String[] bookingData = Bookings.getBookingById(
                        Bookings.getBookingIdByDateAndRoom(date, roomId)
                );
                EditBookingMenu.showEditBookingMenu(bookingData);
            }
            else if (choice == 1) { // Delete выбрана
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete this booking?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean deleted = Bookings.deleteBooking(
                            Bookings.getBookingIdByDateAndRoom(date, roomId)
                    );
                    if (deleted) {
                        JOptionPane.showMessageDialog(null,
                                "Booking deleted successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        Interface.updateCalendar();
                        // Обновляем интерфейс
                    }
                }
            }
        } else {
            String roomName = Rooms.getRoomById(roomId)[0];
            String formattedDate = formatDate(date);

            int option = JOptionPane.showConfirmDialog(null,
                    "<html>Создать новое бронирование?<br>" +
                            "Номер: " + roomName + "<br>" +
                            "Дата: " + formattedDate + "</html>",
                    "Новое бронирование",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                AdBookingMenu.showAdBookingMenu(roomId, date);
            }
        }
    }

    private static String formatDate(String date) {
        try {
            String[] parts = date.split("-");
            if (parts.length == 3) {
                return String.format("%s.%s.%s", parts[2], parts[1], parts[0]);
            }
        } catch (Exception _) {}
        return date;
    }

    private static void updateCalendar(String date, JScrollPane scrollPane) {
        SwingUtilities.invokeLater(() -> {
            scrollPane.setViewportView(createCalendarPanel(date));
        });
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

    public static void updateCalendar() {
        SwingUtilities.invokeLater(() -> {
            // 1. Получаем ссылку на главное окно
            JFrame mainFrame = null;
            for (Window window : Window.getWindows()) {
                if (window instanceof JFrame && "BookMe - Система бронирования".equals(((JFrame) window).getTitle())) {
                    mainFrame = (JFrame) window;
                    break;
                }
            }

            if (mainFrame != null) {
                // 2. Закрываем текущее окно
                mainFrame.dispose();

                // 3. Создаем и показываем новое окно (как при запуске)
                initiateInterface();
            }
        });
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
            panel.add(createCalendarCell(WEEK_DAYS[i], true, date, false, 0, null), gbc);
        }

        for (int roomId = 1; roomId <= roomCount; roomId++) {
            gbc.gridx = 0;
            gbc.gridy = roomId;
            panel.add(createCalendarCell(Rooms.getRoomById(roomId)[0], true, null, false, 0, null), gbc);

            for (int dayIndex = 1; dayIndex < WEEK_DAYS.length; dayIndex++) {
                gbc.gridx = dayIndex;
                if (dayIndex-1 < weekDates.length && Rooms.isRoomBooked(weekDates[dayIndex-1], roomId)) {
                    String bookingInfo = Bookings.getBookingById(Days.getBookingIdByDayAndRoom(weekDates[dayIndex-1], roomId))[0];
                    panel.add(createCalendarCell(bookingInfo, false, null, true, roomId, weekDates[dayIndex-1]), gbc);
                } else {
                    panel.add(createCalendarCell("", false, null, false, roomId, weekDates[dayIndex-1]), gbc);
                }
            }
        }

        return panel;
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
        SwingUtilities.invokeLater(Interface::initiateInterface);
    }
}