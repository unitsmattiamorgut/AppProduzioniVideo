import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppProduzioniVideo extends JFrame {    // schermata di avvio, con il login dell'utente
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private Connection conn = null;

    public AppProduzioniVideo() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // segue creazione di tutti gli elementi da posizionare nella schermata
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 100));
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(350, 30));
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(350, 30));
        panel.add(passwordField);

        panel.add(new JLabel());
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(350, 40));
        panel.add(loginButton);

        add(panel);
        pack();
        setLocationRelativeTo(null);

        // azioni da eseguire per avviare la procedura di login
        passwordField.addActionListener(e -> loginButton.doClick());
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authenticate(username, password)) { // login andato a buon fine, si apre la schermata Home
                JOptionPane.showMessageDialog(AppProduzioniVideo.this, "Login effettuato!");
                dispose();
                try {
                    new Home(conn);
                } catch (SQLException exc) {
                    throw new RuntimeException(exc);
                }
            } else {    // login fallito, si resettano i dati inseriti per riprovare
                JOptionPane.showMessageDialog(AppProduzioniVideo.this, "Username o password errati.");
                resetLoginForm();
            }
        });
    }

    public static void main(String[] args) {    // avvio dell'applicazione con la creazione di una nuova istanza
        AppProduzioniVideo appProduzioniVideo = new AppProduzioniVideo();
        appProduzioniVideo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appProduzioniVideo.setVisible(true);
    }

    private void resetLoginForm() { // reset dei campi del login
        usernameField.setText("");
        passwordField.setText("");
    }

    private boolean authenticate(String username, String password) {    // metodo che autentica l'utente, provando ad aprire una connessione
        String url = "jdbc:mysql://localhost:3306/ProduzioniVideo";
        try {
            conn = DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}