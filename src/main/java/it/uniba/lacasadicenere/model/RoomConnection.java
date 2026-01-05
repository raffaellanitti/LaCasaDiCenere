/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.model;

import it.uniba.lacasadicenere.type.CommandType;

/**
 * Classe che modella una singola connessione direzionale tra due stanze 
 */
public class RoomConnection {
    
    /**
     * Stanza di partenza
     */
    private Room startingRoom;
     
    /**
     * Stanza di destinazione
     */
    private Room arrivingRoom;
    
    /**
     * Direzione che il giocatore deve prendere dalla startingRoom 
     * per raggiungere la arrivingRoom
     */
    private CommandType direction;
    
    /**
     * Booleano che indica se il passaggio è bloccato o aperto
     */
    private boolean locked;
    
    /**
     * Restituisce la stanza di partenza
     * @return startingRoom
     */
    public Room getStartingRoom() {
        return startingRoom;
    }
    
    /**
     * Imposta la stanza di partenza
     * @param room 
     */
    public void setStartingRoom(Room room) {
        this.startingRoom = room;
    }
    
    /**
     * Restituisce la stanza di destinazione
     * @return arrivingRoom
     */
    public Room getArrivingRoom() {
        return arrivingRoom;
    }

    /**
     * Imposta la stanza di destinazione
     * @param room 
     */
    public void setArrivingRoom(Room room) {
        this.arrivingRoom = room;
    }
    
    /**
     * Restituisce la direzione della connessione
     * @return direction
     */
    public CommandType getDirection() {
        return direction;
    }

    /**
     * Imposta la direzione della connessione
     * @param direction 
     */
    public void setDirection(CommandType direction) {
        this.direction = direction;
    }
   
    /**
     * Restituisce lo stato di blocco della connessione
     * @return locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Imposta lo stato di blocco della connessione
     * @param locked 
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }  
}