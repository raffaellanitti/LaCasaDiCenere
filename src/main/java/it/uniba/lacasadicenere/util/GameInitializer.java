/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.uniba.lacasadicenere.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.uniba.lacasadicenere.model.Game;
import it.uniba.lacasadicenere.model.Item;
import it.uniba.lacasadicenere.model.ItemContainer;
import it.uniba.lacasadicenere.model.Room;
import it.uniba.lacasadicenere.model.RoomConnection;
import it.uniba.lacasadicenere.type.CommandType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Classe per l'inizializzazione del gioco.
 * Esegui questo main per creare i file JSON iniziali.
 */
public class GameInitializer {
    
    public static void main(String[] args) {
        
        try {
            List<Item> items = new ArrayList<>();
            Game game = Game.getInstance();
            
            // Oggetti iniziali nell'inventario
            Item telefono = new Item("Telefono", "Il tuo cellulare, pronto per essere usato.", true,
                    Arrays.asList("Telefonino", "Cellulare", "Phone"));
            items.add(telefono);
            
            Item fiammiferi = new Item("Fiammiferi", "Una scatola di fiammiferi secchi.", true, 
                    Arrays.asList("Fiammifero", "ScatolaFiammiferi", "Accendino"));
            items.add(fiammiferi);
            
            // Oggetti Stanza 1
            Item tavolo = new Item("Tavolo", "Un tavolino su cui giacciono una candela e un foglio piegato.", 
                    false, Arrays.asList("Tavolino", "Banco", "Tavolino", "Scrivania"));
            items.add(tavolo);
            
            Item candela = new Item("Candela", "Una candela spenta, consumata dal tempo.", true, 
                    Arrays.asList("Lume", "Luce", "Fiamma"));
            items.add(candela);
            
            Item foglio = new Item("Foglio", "Un foglio con un indizio: 'Verso nord, la luce trova ciò che l''ombra nasconde.'", 
                    true, Arrays.asList("Foglietto", "Indizio", "Carta", "Pagina"));
            items.add(foglio);
            
            // Oggetti Stanza 2
            Item camino = new Item("Camino", "Un grande camino annerito. Sopra c'è una vecchia chiave.", 
                    false, Arrays.asList("Caminetto", "Fuoco", "Focolare"));
            items.add(camino);
            
            Item tappeto = new Item("Tappeto", "Un vecchio tappeto logoro. Uno scrigno giace su di esso.", 
                    false, Arrays.asList("Tappetino", "Stuoia"));
            items.add(tappeto);
            
            Item chiave = new Item("Chiave", "Una vecchia chiave di ferro.", true, 
                    Arrays.asList("Chiavetta", "ChiaveFerro"));
            items.add(chiave);
            
            Item amuleto = new Item("Amuleto", "Un amuleto d'argento a forma di goccia.", true, 
                    Arrays.asList("Ciondolo", "Pendente", "Collana", "Protezione"));

            ItemContainer scrigno = new ItemContainer("Scrigno", "Uno scrigno antico con serratura arrugginita.", 
                    false, Arrays.asList("Cassetta", "Cofanetto", "Baule", "Cassa"));
            scrigno.add(amuleto);
            items.add(scrigno);
              
            // Oggetti Stanza 3
            Item scaffale = new Item("Scaffale", "Uno scaffale con libri bruciati. Un diario sembra intatto.", 
                    false, Arrays.asList("Ripiano", "Mensola", "Palchetto"));
            items.add(scaffale);
            
            Item diario = new Item("Diario", "Un diario con simboli e istruzioni per un rituale.", 
                    true, Arrays.asList("Libro", "Quaderno", "Manoscritto", "Memoria"));
            items.add(diario);
            
            // Oggetti Stanza 5
            Item altare = new Item("Altare", "Un altare di pietra con un'iscrizione.", 
                    false, Arrays.asList("Tavolo", "Piattaforma"));
            items.add(altare);
            
            Room stanza1 = new Room("Stanza1", "Ingresso della casa", null);
            Room stanza2 = new Room("Stanza2", "Salone con camino", null);
            Room stanza3 = new Room("Stanza3", "Biblioteca", null);
            Room stanza4 = new Room("Stanza4", "Stanza degli specchi", null);
            Room stanza5 = new Room("Stanza5", "Cripta finale", null);
            
            stanza1.addItems(items.stream()
                    .filter(i -> i.getName().equals("Tavolo") || 
                            i.getName().equals("Foglio") || 
                            i.getName().equals("Candela"))
                    .toArray(Item[]::new));
            
            stanza2.addItems(items.stream()
                    .filter(i -> i.getName().equals("Camino") || 
                            i.getName().equals("Tappeto") || 
                            i.getName().equals("Chiave") || 
                            i.getName().equals("Scrigno"))
                    .toArray(Item[]::new));
            
            stanza3.addItems(items.stream()
                    .filter(i -> i.getName().equals("Scaffale") || 
                            i.getName().equals("Diario"))
                    .toArray(Item[]::new));
            
            stanza5.addItems(items.stream()
                    .filter(i -> i.getName().equals("Altare"))
                    .toArray(Item[]::new));
            
            List<RoomConnection> corridoi = new ArrayList<>();
            
            RoomConnection c1a = new RoomConnection();
            c1a.setStartingRoom(stanza1);
            c1a.setDirection(CommandType.NORD);
            c1a.setLocked(true);
            c1a.setArrivingRoom(stanza2);
            corridoi.add(c1a);
            
            RoomConnection c1b = new RoomConnection();
            c1b.setStartingRoom(stanza2);
            c1b.setDirection(CommandType.SUD);
            c1b.setLocked(false);
            c1b.setArrivingRoom(stanza1);
            corridoi.add(c1b);
            
            RoomConnection c2a = new RoomConnection();
            c2a.setStartingRoom(stanza2);
            c2a.setDirection(CommandType.EST);
            c2a.setLocked(true);
            c2a.setArrivingRoom(stanza3);
            corridoi.add(c2a);
            
            RoomConnection c2b = new RoomConnection();
            c2b.setStartingRoom(stanza3);
            c2b.setDirection(CommandType.OVEST);
            c2b.setLocked(false);
            c2b.setArrivingRoom(stanza2);
            corridoi.add(c2b);
            
            RoomConnection c3a = new RoomConnection();
            c3a.setStartingRoom(stanza3);
            c3a.setDirection(CommandType.NORD);
            c3a.setLocked(true);
            c3a.setArrivingRoom(stanza4);
            corridoi.add(c3a);
            
            RoomConnection c3b = new RoomConnection();
            c3b.setStartingRoom(stanza4);
            c3b.setDirection(CommandType.SUD);
            c3b.setLocked(false);
            c3b.setArrivingRoom(stanza3);
            corridoi.add(c3b);
            
            RoomConnection c4a = new RoomConnection();
            c4a.setStartingRoom(stanza4);
            c4a.setDirection(CommandType.NORD);
            c4a.setLocked(true);
            c4a.setArrivingRoom(stanza5);
            corridoi.add(c4a);
            
            RoomConnection c4b = new RoomConnection();
            c4b.setStartingRoom(stanza5);
            c4b.setDirection(CommandType.SUD);
            c4b.setLocked(false);
            c4b.setArrivingRoom(stanza4);
            corridoi.add(c4b);
            
            game.setCorridorMap(corridoi);
            
            game.setCurrentRoom(stanza1);
            
            List<Item> inventario = new ArrayList<>();
            inventario.add(new Item("Telefono", "Il tuo cellulare, pronto per essere usato.", true,
                    Arrays.asList("Telefonino", "Cellulare", "Phone")));
            inventario.add(new Item("Fiammiferi", "Una scatola di fiammiferi secchi.", true, 
                    Arrays.asList("Fiammifero", "ScatolaFiammiferi", "Accendino")));
            game.getInventory().addAll(inventario);
            
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
                    

            String gameJson = gson.toJson(game);
            writeJsonToFile("src/main/resources/json/Game.json", gameJson);

            String itemsJson = gson.toJson(items);
            writeJsonToFile("src/main/resources/json/Items.json", itemsJson);
            
        } catch (Exception e) {
            System.err.println("\n ERRORE durante l'inizializzazione:");
            e.printStackTrace();
        }
    }

    /**
     * Scrive il contenuto JSON in un file.
     */
    private static void writeJsonToFile(String filePath, String jsonContent) {
        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(jsonContent);
            }
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura di: " + filePath);
            e.printStackTrace();
        }
    }
}