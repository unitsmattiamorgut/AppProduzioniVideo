import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AzioneCliente extends Azioni implements ActionListener {

    private final JFrame frame;
    private final JButton backButton;
    private final JButton okButton;
    private final JTextField codiceClienteField;
    private JDialog resultDialog;

    public AzioneCliente(Connection conn) { // schermata che implementa la visualizzazione dei montaggi effettuati per un cliente
        super(conn);

        frame = new JFrame("Salvataggio Montaggi effettuati per Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 120);
        frame.setLocationRelativeTo(null);

        // segue creazione di tutti gli elementi da posizionare nella schermata
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel codiceClienteLabel = new JLabel("Codice Cliente: ");
        codiceClienteField = new JTextField();
        inputPanel.add(codiceClienteLabel);
        inputPanel.add(codiceClienteField);

        backButton = new JButton("Indietro");
        backButton.addActionListener(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        okButton = new JButton("Okay");
        okButton.addActionListener(this);
        mainPanel.add(okButton, BorderLayout.EAST);
        mainPanel.add(backButton, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {  // gestione pressione tasto "indietro"
            frame.dispose();
            try {
                new Home(connection);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } else if (e.getSource() == okButton) { // gestione pressione tasto "okay"
            String codiceCliente = codiceClienteField.getText();
            if (codiceCliente.isEmpty()) {  // se manca il parametro, segnalare l'errore
                JOptionPane.showMessageDialog(frame, "Inserire Codice Cliente!");
                resetForm();
            } else {
                try {   // chiamata procedura SQL
                    CallableStatement stmt = connection.prepareCall("{CALL sp_getSalvataggioMontaggi(?)}", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    stmt.setString(1, codiceCliente);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {   // segnalazione di risultati inesistenti
                        JOptionPane.showMessageDialog(frame, "Non sono presenti montaggi da mostrare");
                        resetForm();
                    } else {    // creazione nuova finestra per i risultati
                        rs.beforeFirst();
                        resultDialog = new JDialog(frame, "Salvataggio Montaggi", true);
                        resultDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                        resultDialog.addWindowListener(new WindowAdapter() {
                            public void windowClosing(WindowEvent e) {  // alla chiusura torna alla schermata precedente
                                resetForm();
                                resultDialog.dispose();
                            }
                        });
                        resultDialog.setSize(1000, 200);
                        resultDialog.setLocationRelativeTo(frame);
                        resultDialog.setLayout(new BorderLayout());

                        JTable table = createTable(rs);
                        table.setEnabled(false);
                        JScrollPane scrollPane = new JScrollPane(table);
                        JPanel panel = new JPanel(new GridBagLayout());
                        GridBagConstraints constraints = new GridBagConstraints();
                        constraints.gridx = 0;
                        constraints.gridy = 0;
                        constraints.weighty = 1.0;
                        panel.add(scrollPane, constraints);

                        JButton closeButton = new JButton("Chiudi");
                        JPanel btnPanel = new JPanel();
                        btnPanel.add(closeButton);

                        constraints = new GridBagConstraints();
                        constraints.gridx = 0;
                        constraints.gridy = 1;
                        panel.add(btnPanel, constraints);
                        resultDialog.add(new JScrollPane(table), BorderLayout.CENTER);

                        resultDialog.add(new JScrollPane(table), BorderLayout.CENTER);
                        closeButton.addActionListener(this);
                        resultDialog.add(closeButton, BorderLayout.SOUTH);
                        resultDialog.setVisible(true);

                    }
                    stmt.close();
                } catch (SQLException ex) { // gestione eccezione all'inserimento di un valore errato
                    JOptionPane.showMessageDialog(frame, "Valore inserito errato!");
                    resetForm();
                }

            }
        }
        if (resultDialog != null && e.getSource() instanceof JButton && ((JButton) e.getSource()).getText().equals("Chiudi")) {
            resetForm();
            resultDialog.dispose();  // chiusura finestra dei risultati
        }
    }

    private void resetForm() {  // reset form d'inserimento dei dati
        codiceClienteField.setText("");
    }

    private JTable createTable(ResultSet rs) throws SQLException {  // creazione tabella con i risultati ottenuti dalla query
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Video");
        tableModel.addColumn("Versione");
        tableModel.addColumn("Salvataggio");

        // allineamento di alcune colonne per una migliore visualizzazione dei risultati
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        JTable table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        while (rs.next()) { // ciclo per aggiungere le varie righe alla colonna
            String TL = rs.getString("Video");
            String DT = rs.getString("Versione");
            String S = rs.getString("Salvataggio");
            tableModel.addRow(new Object[]{TL, DT, S});
        }
        return table;
    }
}