/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.model;

import it.uniba.lacasadicenere.type.CommandType;
import java.util.List;

/**
 * Classe che rappresenta un comando nell'avventura testuale 
 */
public class Command {
    
    /**
     * Nome del comando
     */
    private final String name;
    
    /**
     * Lista di alias di un comando
     */
    private final List<String> aliases;
    
    /**
     * Tipo del comando
     */
    private final CommandType type;
    
    /**
     * Costrutture del comando 
     * 
     * @param name nome del comando
     * @param aliases lista di alias di un comando
     * @param type tipo del comando
     */
    public Command(String name, List<String> aliases, CommandType type) {
        this.name = name;
        this.aliases = aliases;
        this.type = type;
    }
    
    /**
     * Restituisce il nome del comando
     * @return name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Restituisce la lista di alias del comando
     * @return aliases
     */
    public List<String> getAliases() {
        return this.aliases;
    }
    
    /**
     * Restituisce il tipo del comando
     * @return type
     */
    public CommandType getType() {
        return this.type;
    } 
}
