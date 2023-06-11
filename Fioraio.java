//Import per lavorare con il JDBC

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class Fioraio {
    public static void main(String[] args) {

        //-----------------------CONNESSIONE CON IL DATABASE-----------------------
        Connection connection = null;

        String url = "jdbc:mariadb://localhost:3306/sfida";
        //credenziali:
        String user = "root";
        String pwd = "2710775";

        try {
            //siccome potrebbe dare errore provo un try and catch
            connection = DriverManager.getConnection(url, user, pwd); 
            System.out.println("Succesfully connected to database.");


            //ESECUZIONE QUERY DA RIGA DI COMANDO

            System.out.println("Se vuoi inserire una pianta scrivi: inserisci_pianta\n" +  
            "Se vuoi aggiornare una proprieta di una pianta scrivi: update_pianta\n" +
            "Se vuoi eliminare un magazzino scrivi: elimina_magazzino\n" + 
            "Se vuoi eliminare una pianta scrivi: elimina_piante\n" +
            "Se vuoi sapere quante piante totali ci sono scrivi: piante_totali_magazini\n"
            );

            if (args.length > 0) {            //se fornisco qualche input
                String query = args[0];  //mi salvo quale query eseguire

                if (query.equals("inserisci_pianta")) { 
                    if (args.length == 5) {
                        int id = Integer.parseInt(args[1]);
                        String nome = args[2];
                        String descrizione = args[3];
                        String paese_origine = args[4];

                        inserisciPianta(connection, id, nome, descrizione, paese_origine);
                    } else {
                        System.out.println("Uso: gli argomenti per \"inserisci_pianta\" sono: <id> <nome> <descrizione> <paeseOrigine>");
                    }
                } else if (query.equals("update_pianta")) {
                    if (args.length == 4) {
                        int id = Integer.parseInt(args[1]);
                        String proprieta = args[2];
                        String nuovoValore = args[3];

                        updatePiantaProprieta(connection, id, proprieta, nuovoValore);
                    } else {
                        System.out.println("Uso: gli argomenti per \"update_pianta\" sono: <id> <proprietà> <nuovoValore>");
                    }
                } else if (query.equals("elimina_magazzino")) {
                    if (args.length == 2) {
                        int id = Integer.parseInt(args[1]);

                        eliminaMagazzino(connection, id);
                    } else {
                        System.out.println("Uso: gli argomenti per \"elimina_magazzino\" sono:  <id>");
                    }
                } else if (query.equals("elimina_pianta")) {
                    if (args.length == 2) {
                        int id = Integer.parseInt(args[1]);

                        eliminaPianta(connection, id);
                    } else {
                        System.out.println("Uso: gli argomenti per \"elimina_pianta\" sono: <id>");
                    }
                } else if (query.equals("piante_totali_magazzini")) {
                    PianteTotaliMagazzini(connection);
                } else {   //se c'è scritto qualcos'altro
                    System.out.println("Uso: java Fioraio <queryType>");
                    System.out.println("Tipi di query supportati: inserisci_pianta, update_pianta, elimina_magazzino, elimina_pianta, piante_totali_magazzini");
                }
            } //else {  //Se non c'è scritto nulla, ma gia lo copre sopra
                //System.out.println("Uso: java Fioraio <queryType>");
                //System.out.println("Tipi di query supportati: inserisci_pianta, update_pianta, elimina_magazzino, elimina_pianta, piante_totali_magazzini");
            //}






            //-----------------------ESECUZIONE QUERY TEST-----------------------
            //inserisciPianta(connection, 200, "La mia piantina", "pianta fatta per cambiare descrizione", "il_mio_pc");
            //updatePiantaProprieta(connection, 200, "descrizione", "cambio con il metodo");
            //eliminaMagazzino(connection, 100);
            //eliminaPianta(connection, 200);
            //PianteTotaliMagazzini(connection);            



            // Chiusura della connessione
            connection.close();
            System.out.println("Succesfully disconnected from database.");

        } catch (SQLException e) {
            System.out.println("Error during connection to database: " + e.getMessage());
        }
    }



    //-----------------------DEFINIZIONI DEI METODI-----------------------

    //Query 1: inserire una nuova pianta a database
    private static void inserisciPianta(Connection connection, int id, String nome, String descrizione, String paese_origine) throws SQLException {
        String query = "INSERT INTO piante (id, nome, descrizione, paese_origine) VALUES (?, ?, ?, ?)";
        
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1, id);
        statement.setString(2, nome);
        statement.setString(3, descrizione);
        statement.setString(4, paese_origine);
        
        int inserimento = statement.executeUpdate();
        if (inserimento > 0) {
            System.out.println("Nuova pianta inserita con successo!");
        }
    }

    //Query 2: modificare una proprietà di una pianta già presente a database
    private static void updatePiantaProprieta(Connection connection, int id, String proprieta, Object nuovoValore) throws SQLException {
        String query = "UPDATE piante SET " + proprieta + " = ? WHERE id = ?";
        
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setObject(1, nuovoValore);
        statement.setInt(2, id);
        
        int updateProprieta = statement.executeUpdate();
        if (updateProprieta > 0) {
            System.out.println("Proprietà aggiornata con successo!");
        }
    }

    //Query 3: eliminare un magazzino dal database
    private static void eliminaMagazzino(Connection connection, int id) throws SQLException {
        String query = "DELETE FROM magazzini WHERE id = ?";
        
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        
        int eliminazione = statement.executeUpdate();
        if (eliminazione > 0) {
            System.out.println("Magazzino eliminato con successo!");
        }
    }

    //Query 4: calcolare il totale piante conservate in ogni magazzino
    private static void PianteTotaliMagazzini(Connection connection) throws SQLException {
        String query = "SELECT Magazzini.ID, Magazzini.Nome, SUM(Assortimento.Quantita) AS Totale_piante " +
                       "FROM Magazzini LEFT JOIN Assortimento ON Magazzini.ID = Assortimento.IDm " + 
                       "GROUP BY Magazzini.ID, Magazzini.Nome";
        
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        while (resultSet.next()) {

            int idMagazzino = resultSet.getInt("ID");
            String nomeMagazzino = resultSet.getString("Nome");
            int totalePiante = resultSet.getInt("Totale_piante");
            
            System.out.println("Magazzino: " + idMagazzino + " " + nomeMagazzino + ", Totale piante: " + totalePiante);
        }
    }

//-----------------------EXTRA PER AIUTARMI-----------------------
    private static void eliminaPianta(Connection connection, int id) throws SQLException {
        String query = "DELETE FROM piante WHERE id = ?";
    
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
    
        int eliminazione = statement.executeUpdate();
        if (eliminazione > 0) {
            System.out.println("Pianta eliminata con successo!");
        }
    }

}
