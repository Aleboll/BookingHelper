package Interface;

import Week.Rooms;

import javax.swing.*;
import java.awt.*;

public class AddRoomMenu {
    public static void showAddRoomMenu() {
        JFrame addRoomFrame = new JFrame("Добавить комнату");
        addRoomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addRoomFrame.setSize(400, 300);
        addRoomFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Стилизация
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // Поле для имени
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Имя:");
        nameLabel.setFont(labelFont);
        addRoomFrame.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(fieldFont);
        addRoomFrame.add(nameField, gbc);

        // Поле для цены
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel priceLabel = new JLabel("Цена:");
        priceLabel.setFont(labelFont);
        addRoomFrame.add(priceLabel, gbc);

        gbc.gridx = 1;
        JTextField priceField = new JTextField(20);
        priceField.setFont(fieldFont);
        addRoomFrame.add(priceField, gbc);

        // Поле для количества мест
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel spaceLabel = new JLabel("Кол-во мест:");
        spaceLabel.setFont(labelFont);
        addRoomFrame.add(spaceLabel, gbc);

        gbc.gridx = 1;
        JTextField spaceField = new JTextField(20);
        spaceField.setFont(fieldFont);
        addRoomFrame.add(spaceField, gbc);

        // Поле для указания есть ли доп место
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel extraSpaceLabel = new JLabel("Доп. место:");
        extraSpaceLabel.setFont(labelFont);
        addRoomFrame.add(extraSpaceLabel, gbc);
        gbc.gridx = 1;
        JCheckBox extraSpaceCheckBox = new JCheckBox();
        extraSpaceCheckBox.setFont(fieldFont);
        addRoomFrame.add(extraSpaceCheckBox, gbc);

        // Поле для указания типа
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel typeLabel = new JLabel("Тип:");
        typeLabel.setFont(labelFont);
        addRoomFrame.add(typeLabel, gbc);
        gbc.gridx = 1;
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Обычный", "Люкс", "VIP", "Семейный", "Эконом"});
        typeComboBox.setFont(fieldFont);
        addRoomFrame.add(typeComboBox, gbc);


        // Кнопка "Добавить"
        JButton addButton = new JButton("Добавить");
        addButton.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        addRoomFrame.add(addButton, gbc);
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            int price = Integer.parseInt(priceField.getText());
            int space = Integer.parseInt(spaceField.getText());
            boolean extraSpace = extraSpaceCheckBox.isSelected();
            int type =  typeComboBox.getSelectedIndex();
            String[] roomdata = {name, String.valueOf(price), String.valueOf(space), String.valueOf(extraSpace), String.valueOf(type)};

            Rooms.addRoom(roomdata);


            System.out.println("Комната добавлена: " + name + ", Цена: " + price + ", Мест: " + space + ", Доп. место: " + extraSpace + ", Тип: " + type);
            addRoomFrame.dispose();

        });
        // Кнопка "Отмена"
        JButton cancelButton = new JButton("Отмена");
        cancelButton.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        addRoomFrame.add(cancelButton, gbc);
        cancelButton.addActionListener(e -> {
            addRoomFrame.dispose();
        });
        // Отображение окна
        addRoomFrame.setLocationRelativeTo(null);
        addRoomFrame.setVisible(true);
        addRoomFrame.setResizable(false);
        addRoomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);



    }
}
