/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.model;

import it.uniba.lacasadicenere.view.GamePanel;
import it.uniba.lacasadicenere.service.OutputService;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta il gioco
 */
public class Game {
    
    /**
     * Lista degli oggetti nell'inventario del giocatore
     */
    private List<Item> inventory;
    
    /**
     * Stanza in cui si trova il giocatore
     */
    private Room currentRoom;
    
    /**
     * Lista di tutti i collegamenti tra le stanza
     */
    private List<RoomConnection> corridorMap;
    
    /**
     * Istanza statica e privata della classe Game stessa
     */
    private static Game game = new Game();
    
    /**
     * Costruttore del gioco
     */
    public Game() {
        this.inventory = new ArrayList<>();
        this.corridorMap = new ArrayList<>();
    }
    
    /**
     * Imposta l'istanza Singleton
     * @param game 
     */
    public static void setUpGame(Game game) {
        Game.game = game;
    }
    
    /**
     * Metodo di accesso all'unica instanza di Game
     * @return game
     */
    public static Game getInstance() {
        return game;
    }

    /**
     * Restituisce la lista degli oggetti nell'inventario del giocatore
     * @return inventory
     */
    public List<Item> getInventory() {
        return this.inventory;
    }

    /**
     * Aggiunge un oggetto all'inventario del giocatore
     * @param item 
     */
    public void addInventory(Item item) {
        game.inventory.add(item);
        List<String> itemsNames = game.inventory.stream().map(Item::getName).toList();
        String[] itemsNamesArray = itemsNames.toArray(new String[0]);
        GamePanel.updateInventoryTextArea(itemsNamesArray);
    }

    /** 
     * Rimuove un oggetto dall'inventario del giocatore
     * @param item 
     */
    public void removeInventory(Item item) {
        game.inventory.remove(item);
        List<String> itemsNames = game.inventory.stream().map(Item::getName).toList();
        String[] itemsNamesArray = itemsNames.toArray(new String[0]);
        GamePanel.updateInventoryTextArea(itemsNamesArray);
    }

    /**
     * Stampa la lista degli oggetti presenti nell'inventario
     */
    public void printInventory() {
        OutputService.displayText("Inventario: ");
        for (Item item : game.inventory) {
            OutputService.displayText("- " + item.getName());
        }
    }

    /**
     * Restituisce la stanza corrente del giocatore
     * @return currentRoom
     */
    public Room getCurrentRoom() {
        return game.currentRoom;
    }

    /**
     * Imposta la stanza corrente del giocatore
     * @param room 
     */
    public void setCurrentRoom(Room room) {
        if (game.corridorMap != null) {
            for (RoomConnection corridor : game.corridorMap) {
                if (corridor.getStartingRoom().equals(room)) {
                    game.currentRoom = corridor.getStartingRoom();
                    GamePanel.setImagePanel(game.currentRoom.getName());
                    return;
                }
            }
        }
        game.currentRoom = room;
        GamePanel.setImagePanel(game.currentRoom.getName());
    }
    
    /**
     * Restituisce la mappa delle stanze collegate
     * @return corridorMap
     */
    public List<RoomConnection> getCorridorMap() {
        return game.corridorMap;
    }

    /**
     * Imposta la mappa delle stanze collegate
     * @param corridorsMap 
     */
    public void setCorridorMap(List<RoomConnection> corridorsMap) {
        game.corridorMap = corridorsMap;
    }

    /**
     * Sblocca un corridoi specifico
     * @param r1 startingRoom
     * @param r2 arrivingRoom
     */
    public void unlockCorridor(String r1, String r2) {
        for (RoomConnection corridor : game.corridorMap) {
            if (corridor.getStartingRoom().getName().equals(r1) && corridor.getArrivingRoom().getName().equals(r2)) {
                corridor.setLocked(false);
            }
        }
    }
}