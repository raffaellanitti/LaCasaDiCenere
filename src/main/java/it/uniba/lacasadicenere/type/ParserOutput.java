/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.type;

import it.uniba.lacasadicenere.model.Item;

/**
 * Classe per rappresentare l'output del parser.
 */
public class ParserOutput {
    
    /**
     * Comando di gioco identificato.
     */
    private CommandType command;
    
    /**
     * Primo oggetto di gioco coinvolto nel comando.
     */
    private Item item1;

    /**
     * Secondo oggetto di gioco coinvolto nel comando.
     */
    private Item item2;
    
    /**
     * Numero di argomenti nel comando.
     */
    private int args;
    
    /**
     * Costruttore di default della classe ParserOutput.
     */
    public ParserOutput() {
        args = 0;
    }
    
    /**
     * Restituisce il numero di argomenti nel comando.
     * @return numero di argomenti
     */
    public int getArgs() {
        return args;
    }
    
    /**
     * Imposta il numero di argomenti nel comando.
     * @param args
     */
    public void setArgs(int args) {
        this.args = args;
    }
    
    /**
     * Restituisce il comando di gioco identificato.
     * @return comando di gioco
     */
    public CommandType getCommand() {
        return command;
    }
    
    /**
     * Imposta il comando di gioco identificato.
     * @param command
     */
    public void setCommand(CommandType command) {
        this.command = command;
    }
    
    /**
     * Imposta il primo oggetto di gioco coinvolto nel comando.
     * @param item1
     */
    public void setItem1(Item item1) {
        this.item1 = item1;
    }
    
    /**
     * Restituisce il primo oggetto di gioco coinvolto nel comando.
     * @return primo oggetto di gioco
     */
    public Item getItem1() {
        return item1;
    }

    /**
     * Imposta il secondo oggetto di gioco coinvolto nel comando.
     * @param item2
     */
    public void setItem2(Item item2) {
        this.item2 = item2;
    }

    /**
     * Restituisce il secondo oggetto di gioco coinvolto nel comando.
     * @return secondo oggetto di gioco
     */
    public Item getItem2() {
        return item2;
    }
    
    /**
     * Metodo equals per confrontare due oggetti ParserOutput.
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ParserOutput)) return false;
        ParserOutput that = (ParserOutput) o;
        return command == that.command &&
                (item1 != null ? item1.equals(that.item1) : that.item1 == null) &&
                (item2 != null ? item2.equals(that.item2) : that.item2 == null);
    }

    /**
     * Metodo hashCode per generare un codice hash per l'oggetto ParserOutput.
     */          
    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + (item1 != null ? item1.hashCode() : 0);
        result = 31 * result + (item2 != null ? item2.hashCode() : 0);
        return result;
    }    
}