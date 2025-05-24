package Interface;

import Interface.Client.Client;
import Week.Bookings;
import Week.Days;
import Week.Rooms;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditBookingMenu {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public static void showEditBookingMenu(String[] bookingData) {
        JFrame frame = new JFrame("Редактировать бронь");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Поле для имени
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Имя:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(bookingData[0], 20);
        formPanel.add(nameField, gbc);

        // Выбор комнаты
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Номер:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> roomComboBox = new JComboBox<>(Rooms.getRoomsNames());
        roomComboBox.setSelectedIndex(Integer.parseInt(bookingData[4]) - 1);
        formPanel.add(roomComboBox, gbc);

        // Дата начала
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Дата начала:"), gbc);

        gbc.gridx = 1;
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setFormats(DATE_FORMAT);
        try {
            datePicker.setDate(DATE_FORMAT.parse(bookingData[1]));
        } catch (Exception e) {
            datePicker.setDate(new Date());
        }
        formPanel.add(datePicker, gbc);

        // Время начала
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Время начала:"), gbc);

        gbc.gridx = 1;
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        try {
            timeSpinner.setValue(TIME_FORMAT.parse(bookingData[2]));
        } catch (Exception e) {
            timeSpinner.setValue(new Date());
        }
        formPanel.add(timeSpinner, gbc);

        // Дата окончания
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Дата окончания:"), gbc);

        gbc.gridx = 1;
        JXDatePicker quitDatePicker = new JXDatePicker();
        quitDatePicker.setFormats(DATE_FORMAT);
        try {
            Date startDate = DATE_FORMAT.parse(bookingData[1]);
            int duration = Integer.parseInt(bookingData[3]);
            Date endDate = new Date(startDate.getTime() + (long) duration * 24 * 60 * 60 * 1000);
            quitDatePicker.setDate(endDate);
        } catch (Exception e) {
            quitDatePicker.setDate(new Date());
        }
        formPanel.add(quitDatePicker, gbc);

        // Кнопки
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            try {
                updateBooking(frame, nameField, roomComboBox, datePicker,
                        timeSpinner, quitDatePicker, bookingData[7]);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> frame.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel, gbc);

        frame.add(formPanel);
        frame.setVisible(true);
    }

    private static void updateBooking(JFrame frame, JTextField nameField,
                                      JComboBox<String> roomComboBox, JXDatePicker datePicker,
                                      JSpinner timeSpinner, JXDatePicker quitDatePicker,
                                      String bookingId) throws Exception {
        // Валидация данных
        if (nameField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Введите имя клиента");
        }

        Date startDate = datePicker.getDate();
        Date endDate = quitDatePicker.getDate();

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Укажите даты бронирования");
        }

        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }

        // Подготовка данных
        String[] updatedData = {
                nameField.getText(),
                DATE_FORMAT.format(startDate),
                TIME_FORMAT.format(timeSpinner.getValue()),
                String.valueOf(Days.getDaysBetweenDates(DATE_FORMAT.format(startDate), DATE_FORMAT.format(endDate))),
                String.valueOf(roomComboBox.getSelectedIndex() + 1),
                "0", // type
                "0", // status
                bookingId
        };

        // Проверка на конфликты
        checkBookingConflicts(updatedData, bookingId);

        // Обновление в базе
        if (!Bookings.updateBooking(updatedData)) {
            throw new Exception("Ошибка при обновлении брони");
        }

        // Обновление дат
        updateBookingDates(updatedData, bookingId);

        JOptionPane.showMessageDialog(frame, "Бронь успешно обновлена!",
                "Успех", JOptionPane.INFORMATION_MESSAGE);
        Interface.updateCalendar();
        frame.dispose();
    }

    private static void checkBookingConflicts(String[] bookingData, String originalBookingId) throws Exception {
        int roomId = Integer.parseInt(bookingData[4]);
        String[] dates = Days.GetWeekDates(bookingData[1]);

        for (String date : dates) {
            String[] existingBooking = Bookings.getBookingByDayAndRoom(date, roomId);
            if (existingBooking != null && existingBooking[7] != null &&
                    !existingBooking[7].equals(originalBookingId)) {
                throw new Exception("Конфликт бронирования: комната уже занята на " + date);
            }
        }
    }

    private static void updateBookingDates(String[] bookingData, String bookingId) {
        // Удаляем старые даты
        Days.deleteDatesByBookingId(Integer.parseInt(bookingId));

        // Добавляем новые
        String[] dates = Days.GetWeekDates(bookingData[1]);
        int duration = Integer.parseInt(bookingData[3]);

        for (int i = 0; i < duration && i < dates.length; i++) {
            String[] dateData = {
                    dates[i],
                    bookingData[4], // roomId
                    bookingId
            };
            Days.addDates(dateData);
        }
    }
}