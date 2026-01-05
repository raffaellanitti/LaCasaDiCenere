/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.model;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;

/**
 *  Classe che rappresenta un oggetto presente nel gioco.
 */
public class Item {
    
    /**
     * Nome dell'oggetto
     */
    private String name;

    /**
     * Lista di alias del nome dell'oggetto
     */
    private List<String> aliases;

    /**
     * Descrizione dell'oggetto
     */
    private String description;

    /**
     * Indica se l'oggetto può essere raccolto
     */
    private boolean isPickable;
    
    /**
     * Costruttore dell'oggetto Item.
     * 
     * @param name nome dell'oggetto
     * @param description descrizione dell'oggetto
     * @param pickable indica se l'oggetto può essere raccolto
     * @param aliases eventuali sinonimi del nome
     */
    public Item(String name, String description, boolean pickable, List<String> aliases) {
        this.name = name;
        this.description = description;
        this.isPickable = pickable;
        this.aliases = (aliases != null) ? aliases : new ArrayList<>();
    }

    /**
     * Costruttore di default dell'oggetto Item.
     */
    public Item() {
        this("", "", false, new ArrayList<>());
    }
    
    /**
     * Verifica se l'oggetto può essere raccolto.
     * @return true se l'oggetto è raccoglibile, false altrimenti
     */
    public boolean isPickable() {
        return isPickable;
    }
    
    /**
     * Imposta se l'oggetto può essere raccolto.
     * @param pickable 
     */
    public void setPickable(boolean pickable) {
        this.isPickable = pickable;
    }
    
    /**
     * Restituisce il nome dell'oggetto.
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * Imposta il nome dell'oggetto.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Restituisce la descrizione dell'oggetto.
     * @return
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Imposta la descrizione dell'oggetto.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Restituisce la lista di alias dell'oggetto.
     * @return aliases
     */
    public List<String> getAliases() {
        return aliases;
    }
    
    /**
     * Imposta la lista di alias dell'oggetto.
     * @param aliases
     */
    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }
    
    /**
     * Verifica se l'oggetto ha un determinato nome o alias.
     * @param name
     * @return true se il nome o alias corrisponde, false altrimenti
     */
    public boolean hasName(String name) {
        if (this.name.equalsIgnoreCase(name)) {
            return true;
        }
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Override dei metodi equals e hashCode basati su name e aliases.
     */
    @Override 
    public boolean equals(Object o) { 
        if(this == o) return true; 
        if(!(o instanceof Item)) return false; 
        Item item = (Item) o; 
        return Objects.equals(name, item.name) && 
                Objects.equals(aliases, item.aliases); 
    } 
    @Override 
    public int hashCode() { 
        return Objects.hash(name, aliases); 
    }
}