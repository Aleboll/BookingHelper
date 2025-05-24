package Interface.Client;



import Interface.Client.ClientDAO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ClientLibraryWindow extends JFrame {
    private JTable table;
    private JTextField searchField;

    public ClientLibraryWindow() {
        setTitle("Библиотека клиентов");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.setToolTipText("Поиск по имени, телефону или email");
        topPanel.add(new JLabel("Поиск: "), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        table = new JTable(new DefaultTableModel(new Object[]{"ID", "Имя", "Телефон", "Email"}, 0));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        updateTable(ClientDAO.getAllClients());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterClients(); }
            public void removeUpdate(DocumentEvent e) { filterClients(); }
            public void changedUpdate(DocumentEvent e) { filterClients(); }

            private void filterClients() {
                String query = searchField.getText().toLowerCase();
                List<Client> filtered = ClientDAO.getAllClients().stream()
                        .filter(c -> c.getName().toLowerCase().contains(query) ||
                                c.getPhone().toLowerCase().contains(query) ||
                                c.getEmail().toLowerCase().contains(query))
                        .collect(Collectors.toList());
                updateTable(filtered);
            }
        });

        setVisible(true);
    }

    private void updateTable(List<Client> clients) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Client c : clients) {
            model.addRow(new Object[]{c.getId(), c.getName(), c.getPhone(), c.getEmail()});
        }
    }
}
