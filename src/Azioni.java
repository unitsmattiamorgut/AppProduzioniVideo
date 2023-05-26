import java.sql.Connection;

public class Azioni {
    protected final Connection connection;

    public Azioni(Connection connection) {  // permette di gestire agevolmente il passaggio della connessione instaurata nella pagina di login
        this.connection = connection;
    }

    /*
        Aggiornamento futuro: è possibile implementare al meglio l'ereditarietà tra questa classe
        e tutte quelle che la estendono: alcune parti sono infatti in comune, come ad esempio il
        tasto indietro e le relative azioni da compiere in caso di pressione.
        In questa versione del progetto si è scelto di non procedere a un attento sviluppo per limitare
        la complessità.
        È possibile implementare in futuro anche l'inserimento di nuovi dati e l'esecuzione di altre
        operazioni di minore importanza.
     */
}
