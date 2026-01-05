/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.model;
import it.uniba.lacasadicenere.database.DatabaseH2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe che rappresenta una stanza all'interno del gioco
 */
public class Room {
    
    /**
     * Nome della stanza
     */
    private String name;
    
    /**
     * Descrizione della stanza
     */
    private String description;
    
    /**
     * Lista di oggetti attualmente presenti in questa stanza
     */
    private List<Item> items;
    
    /**
     * Lista statica e pubblica (collezione) che contiene tutte le istanze
     * delle stanze create nel gioco
     */
    public static List<Room> rooms = new ArrayList<>();
    
    /** 
     * Costrutture della stanza 
     * @param name il nome della stanza
     * @param description la descrizione della stanza
     * @param items la lista degli oggetti nella stanza
     */
    public Room(String name, String description, List<Item> items){
        this.name = name;
        this.description = description;
        this.items = (items != null) ? items : new ArrayList<>();
    }
    
    /**
     * Metodo statico per recuperare un'istanza di Room dalla lista rooms
     * in base al suo nome
     * @param position nome della stanza da cercare
     * @return oggetto Room corrispondente al nome
     */
    public static Room getRoomByName(String position) {
        for(Room room : rooms) {
            if(room.getName().equalsIgnoreCase(position)) {
                return room;
            }
        }
        return null;
    }
    
    /**
     * Restituisce il nome della stanza
     * @return name
     */
    public String getName() {
        return this.name;
    }
    
     /**
      * Restituisce la descrizione della stanza
     * @return description
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Restituisce la lista degli oggetti presenti nella stanza
     * @return items
     */
    public List<Item> getItems() {
        return this.items;
    }
    
    /**
     * Aggiunge uno o più oggetti alla lista degli oggetti presenti nella stanza
     * @param items items
     */
    public void addItems(Item...items) {
        this.items.addAll(Arrays.asList(items));
    }
    
    /**
     * Rimuove un oggetto dalla lista degli oggetti presenti nella stanza
     * in base al nome
     * @param name nome dell'oggetto da rimuovere
     */
    public void removeItem(String name) {
        this.items.removeIf(item -> item.getName().equals(name));
    }
    
    /**
     * Stampa la descrizione della stanza
     */
    public void printDescription() {
        DatabaseH2.printFromDB("Osserva", name, "true", "0", "0");
    }
}