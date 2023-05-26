import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class Home extends JFrame {

    private final Connection connection;

    public Home(Connection conn) throws SQLException {  // home page dell'applicazione, dove si seleziona l'azione da eseguire
        this.connection = conn;
        setTitle("Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);

        // segue creazione di tutti gli elementi da posizionare nella schermata
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Benvenuto, " + connection.getMetaData().getUserName());
        panel.add(welcomeLabel, BorderLayout.NORTH);

        /* Opzioni di scelta azioni da eseguire. Ottimizzazione futura: utilizzare "gruppi" di utenti e relativi permessi direttamente
           senza determinare le opzioni (come fatto di seguito) solamente guardando al nome utente.
        */
        String[] options;
        if(connection.getMetaData().getUserName().contains("dipendente")){
            options = new String[]{"Lavori da Consegnare", "Salvataggio Montaggi Cliente"};
        } else {
            options = new String[]{"Lavori Svolti da Dipendente", "Lavori da Consegnare", "Fatture non Pagate", "Salvataggio Montaggi Cliente"};
        }
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setPreferredSize(new Dimension(320, 100));
        panel.add(comboBox, BorderLayout.WEST);

        JButton selectButton = new JButton("Seleziona");
        panel.add(selectButton, BorderLayout.EAST);

        // avvio dell'operazione richiesta
        selectButton.addActionListener(e -> {
            String selectedOption = (String) comboBox.getSelectedItem();
            switch (Objects.requireNonNull(selectedOption)) {
                case "Lavori Svolti da Dipendente" -> {
                    dispose();
                    new AzioneDipendente(conn);
                }
                case "Lavori da Consegnare" -> {
                    dispose();
                    new AzioneLavori(conn);
                }
                case "Fatture non Pagate" -> {
                    dispose();
                    new AzioneFatture(conn);
                }
                case "Salvataggio Montaggi Cliente" -> {
                    dispose();
                    new AzioneCliente(conn);
                }
            }
        });

        JButton exitButton = new JButton("Esci");

        // gestione pressione tasto "esci"
        exitButton.addActionListener(e -> {
            dispose();
            try {
                connection.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });
        panel.add(exitButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}