import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AzioneFatture extends Azioni implements ActionListener {

    private final JFrame frame;
    private final JPanel panel;
    private final JButton backButton;

    public AzioneFatture(Connection conn) { // schermata che implementa la visualizzazione delle fatture ancora non pagate
        super(conn);
        frame = new JFrame("Fatture non Pagate");
        frame.setSize(700, 200);
        panel = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // segue creazione di tutti gli elementi da posizionare nella schermata
        panel.setLayout(new BorderLayout());
        backButton = new JButton("Indietro");
        backButton.addActionListener(this);
        panel.add(backButton, BorderLayout.SOUTH);
        createTable();
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void createTable() {    // creazione tabella con i risultati ottenuti dalla query
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("NumeroFattura");
        model.addColumn("DataEmissione");
        model.addColumn("Importo€");
        model.addColumn("CodiceCliente");
        model.addColumn("NomeCliente");
        model.addColumn("TitoloVideo");
        model.addColumn("Stato");
        try {   // creazione tabella con i risultati
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM fattureNonPagate");
            if (!rs.next()) {   // segnalazione di risultati inesistenti
                JOptionPane.showMessageDialog(frame, "Non sono presenti fatture da mostrare");
                rs.beforeFirst();
            }
            while (rs.next()) {
                Object[] row = {rs.getInt("NumeroFattura"), rs.getString("DataEmissione"),
                        rs.getString("ImportoEuro"), rs.getString("CodiceCliente"), rs.getString("NomeCliente"),
                        rs.getString("TitoloVideo"), rs.getString("Stato")};
                model.addRow(row);
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JTable table = new JTable(model);
        table.setEnabled(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // allineamento di alcune colonne per una migliore visualizzazione dei risultati
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        TableColumnModel columnModel = table.getColumnModel();
        for (int columnIndex = 0; columnIndex < columnModel.getColumnCount(); columnIndex++) {
            columnModel.getColumn(columnIndex).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1000, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {  // gestione pressione tasto "indietro"
            frame.dispose();
            try {
                new Home(connection);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}