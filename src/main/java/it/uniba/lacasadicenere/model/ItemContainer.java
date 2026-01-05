/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta un contenitore di oggetti.
 */
public class ItemContainer extends Item {

    /**
     * Lista di oggetti contenuti nel contenitore
     */
    private List<Item> list = new ArrayList<>();

    private boolean isOpen = false;

    /**
     * Costruttore dell'oggetto ItemContainer.
     * @param name
     * @param description
     * @param pickable
     * @param aliases
     */
    public ItemContainer(String name, String description, boolean pickable, List<String> aliases) {
        super(name, description, pickable, aliases);
    }

    /**
     * Restituisce la lista di oggetti contenuti nel contenitore.
     * @return lista di oggetti
     */
    public List<Item> getList() {
        return list;
    }

    /**
     * Imposta la lista di oggetti contenuti nel contenitore.
     * @param list
     */
    public void setList(List<Item> list) {
        this.list = list;
    }

    /**
     * Aggiunge un oggetto al contenitore.
     * @param item
     */
    public void add(Item item) {
        list.add(item);
    }

    /**
     * Rimuove un oggetto dal contenitore.
     * @param item
     */
    public void remove(Item item) {
        list.remove(item);
    }

    /**
     * Verifica se il contenitore è aperto.
     * @return true se aperto, false se chiuso
     */
    public boolean isOpen() {
        return isOpen;
    }
    
    /**
     * Imposta lo stato di apertura del contenitore.
     * @param open true per aprire, false per chiudere
     */
    public void setOpen(boolean open) {
        this.isOpen = open;
    }
}