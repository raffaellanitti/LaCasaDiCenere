/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.uniba.lacasadicenere.service.OutputService;

/**
 * Classe per la gestione della connessione al database H2.
 * Gestisce la connessione, l'inizializzazione e le query al database.
 */
public class DatabaseH2 {
    
    /**
     * Driver JDBC per H2 Database.
     */
    private static final String JDBC_DRIVER = "org.h2.Driver";

    /**
     * URL del database H2.
     */
    private static final String DB_URL = "jdbc:h2:./src/main/resources/database";

    /**
     * Credenziali di accesso al database.
     */
    private static final String USER = "sa";
    private static final String PASS = "";
    
    /**
     * Stabilisce una connessione al database H2 e inizializza le tabelle se necessario.
     * 
     * @return La connessione al database
     * @throws RuntimeException Se si verifica un errore durante la connessione
     */
    public static Connection connect() {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String start = "RUNSCRIPT FROM 'src/main/resources/database/db_start.sql'";
        String fill = "RUNSCRIPT FROM 'src/main/resources/database/db_info.sql'";

        boolean emptyDescr = true;
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.prepareStatement(start);
            stmt.execute();
            stmt.close();

            String checkQuery = "SELECT * FROM DESCRIZIONE";
            stmt = conn.prepareStatement(checkQuery);
            rs = stmt.executeQuery();
            while(rs.next()) {
                emptyDescr = false;
            }
            rs.close();
            
            if(emptyDescr) {
                stmt = conn.prepareStatement(fill);
                stmt.execute();
                stmt.close();
            }
            
            return conn;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } 
    }

    /**
     * Chiude la connessione al database.
     * 
     * @param conn La connessione da chiudere
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Stampa la descrizione dal database in base ai parametri forniti.
     * @param comando
     * @param stanza
     * @param stato
     * @param oggetto1
     * @param oggetto2
     */
    public static void printFromDB(String comando, String stanza, String stato, String oggetto1, String oggetto2) {
        String query = "SELECT DESCRIZIONE FROM DESCRIZIONE WHERE COMANDO = ? AND STANZA = ? AND STATO = ? AND OGGETTO1 = ? AND OGGETTO2 = ?";

        try (Connection conn = connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, comando.trim());
            stmt.setString(2, stanza.trim());
            stmt.setString(3, stato.trim());
            stmt.setString(4, oggetto1.trim());
            stmt.setString(5, oggetto2.trim());

            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                String desc = rs.getString("DESCRIZIONE");
                OutputService.displayText(desc);
            } else {
                OutputService.displayText("Nessuna descrizione per i parametri forniti.");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
 