package Interface;

import Lib.Status;
import Week.Bookings;
import Week.Days;
import Week.Rooms;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdBookingMenu {
    public static void showAdBookingMenu() {
        JFrame addBookingFrame = new JFrame("Добавить бронь");
        addBookingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addBookingFrame.setSize(400, 450);
        addBookingFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Стилизация
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // 1. Поле для имени
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Имя:");
        nameLabel.setFont(labelFont);
        addBookingFrame.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(fieldFont);
        addBookingFrame.add(nameField, gbc);

        // 2. Выбор комнаты
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel roomLabel = new JLabel("Номер:");
        roomLabel.setFont(labelFont);
        addBookingFrame.add(roomLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> roomComboBox = new JComboBox<>();
        for (String name : Rooms.getRoomsNames()) {
            roomComboBox.addItem(name);
        }
        roomComboBox.setFont(fieldFont);
        addBookingFrame.add(roomComboBox, gbc);

        // 3. Выбор даты начала
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel dateLabel = new JLabel("Дата начала:");
        dateLabel.setFont(labelFont);
        addBookingFrame.add(dateLabel, gbc);

        gbc.gridx = 1;
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setFormats("yyyy-MM-dd");
        datePicker.setFont(fieldFont);
        addBookingFrame.add(datePicker, gbc);

        // 4. Выбор времени начала
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel timeLabel = new JLabel("Время начала:");
        timeLabel.setFont(labelFont);
        addBookingFrame.add(timeLabel, gbc);

        gbc.gridx = 1;
        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date());
        timeSpinner.setFont(fieldFont);
        addBookingFrame.add(timeSpinner, gbc);

        // 5. Выбор даты окончания
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel quitDateLabel = new JLabel("Дата окончания:");
        quitDateLabel.setFont(labelFont);
        addBookingFrame.add(quitDateLabel, gbc);

        gbc.gridx = 1;
        JXDatePicker quitDatePicker = new JXDatePicker();
        quitDatePicker.setFormats("yyyy-MM-dd");
        quitDatePicker.setFont(fieldFont);
        addBookingFrame.add(quitDatePicker, gbc);

        // Кнопки
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Добавить бронь");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(0, 0, 0));
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String roomName = (String) roomComboBox.getSelectedItem();
            Date startDate = datePicker.getDate();
            Date endDate = quitDatePicker.getDate();
            Date time = (Date) timeSpinner.getValue();

            if (name.isEmpty() || roomName == null || startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(addBookingFrame,
                        "Пожалуйста, заполните все поля.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            String date = dateFormat.format(startDate);
            String quitDate = dateFormat.format(endDate);
            String timeStr = timeFormat.format(time);
            String duration = String.valueOf(Days.getDaysBetweenDates(date, quitDate));

            String[] booking = {
                    name,
                    date,
                    timeStr,
                    duration,
                    String.valueOf(Rooms.getRoomId(roomName)),
                    "0",
                    "0",
            };

            if (!Bookings.addBooking(booking)) {
                JOptionPane.showMessageDialog(addBookingFrame,
                        "Ошибка при добавлении брони.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(addBookingFrame,
                    "Бронь добавлена успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);

            String[] bookingDates = getBookingDates(date, Integer.parseInt(duration));
            for (String bookingDate : bookingDates) {
                int bookid = Integer.parseInt(Bookings.getBookingByDayAndRoom(date, Rooms.getRoomId(roomName))[7]);
                String[] bookingData = {bookingDate, String.valueOf(Rooms.getRoomId(roomName)), String.valueOf(bookid)};
                if (!Days.addDates(bookingData)) {
                    JOptionPane.showMessageDialog(addBookingFrame,
                            "Ошибка при добавлении даты брони.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }


            addBookingFrame.dispose();
        });
        buttonPanel.add(addButton);

        JButton cancelButton = new JButton("Отмена");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> addBookingFrame.dispose());
        buttonPanel.add(cancelButton);

        addBookingFrame.add(buttonPanel, gbc);

        // Настройки окна
        addBookingFrame.pack();
        addBookingFrame.setLocationRelativeTo(null);
        addBookingFrame.setResizable(false);
        addBookingFrame.setVisible(true);
    }
    private static String[] getBookingDates(String date, int duration){
        String[] bookingDates = new String[duration];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = dateFormat.parse(date);
            for (int i = 0; i < duration; i++) {
                bookingDates[i] = dateFormat.format(startDate);
                startDate.setTime(startDate.getTime() + (1000 * 60 * 60 * 24));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookingDates;
    }
}