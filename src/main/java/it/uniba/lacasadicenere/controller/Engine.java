
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.uniba.lacasadicenere.controller;

import it.uniba.lacasadicenere.model.Game;
import it.uniba.lacasadicenere.model.Item;
import it.uniba.lacasadicenere.model.ItemContainer;
import it.uniba.lacasadicenere.database.DatabaseH2;
import it.uniba.lacasadicenere.service.MirrorGame;
import it.uniba.lacasadicenere.service.InputService;


/**
 * Classe per la gestione della logica di gioco.
 */
public class Engine {
    
    /**
     * Riferimento all'istanza del gioco.
     */
    private Game game;
    
    /**
     * Costrutture della classe GameLogic
     * @param game 
     */
    public Engine(Game game) {
        this.game = game;
    }
    
    /**
     * Metodo per eseguire l'uso di un singolo oggetto.
     * @param i
     * @return true se l'azione è stata eseguita con successo, false altrimenti
     */
    public boolean useSingle(Item i) {
        if(i.hasName("Telefono") && game.getCurrentRoom().getName().equals("Stanza4")) {
            MirrorGame mirrorGame = MirrorGame.getInstance();
            mirrorGame.startGame();
            return true;
        }
        return false;
    }

    /**
     * Metodo per eseguire l'uso di due oggetti.
     * @param item1
     * @param item2
     * @return true se l'azione è stata eseguita con successo, false altrimenti
     */
    public boolean useDouble(Item item1, Item item2) {
        // Uso dei fiammiferi sulla candela nella Stanza1
        if(item1.hasName("Fiammiferi") && item2.hasName("Candela") && game.getCurrentRoom().getName().equals("Stanza1")) {
            if (!game.getInventory().contains(item2)) {
                return false; 
            }
            game.removeInventory(item1);
             DatabaseH2.printFromDB("Usa", game.getCurrentRoom().getName(), 
            "true", "Fiammiferi", "Candela");
             game.unlockCorridor("Stanza1", "Stanza2");
            return true;
        }

        // Uso della chiave sullo scrigno nella Stanza2
        if(item1.hasName("Chiave") && item2.hasName("Scrigno") && game.getCurrentRoom().getName().equals("Stanza2")) {
            Item scrigno = game.getCurrentRoom().getItems().stream()
                    .filter(item -> item.hasName("Scrigno"))
                    .findFirst()
                    .orElse(null);

            if(scrigno == null || !(scrigno instanceof ItemContainer)) {
                return false; 
            }

            ItemContainer scrignoContainer = (ItemContainer) scrigno;
            
            if(scrignoContainer.getList() == null || scrignoContainer.getList().isEmpty()) {
                return false; 
            }

            scrignoContainer.setOpen(true);

            for (Item contained : scrignoContainer.getList()) {
                game.getCurrentRoom().addItems(contained);
            }
            scrignoContainer.getList().clear();

            game.removeInventory(item1);
            DatabaseH2.printFromDB("Usa", game.getCurrentRoom().getName(), 
            "true", "Chiave", "Scrigno");
            return true;
        }
        return false;
    }

    /**
     * Metodo per eseguire gli effetti post-raccolta di un oggetto.
     * @param i
     */
    public void postPickUp(Item i) {
        // Sblocca la porta tra Stanza3 e Stanza4 se si raccoglie il diario nella Stanza3
        if (i.hasName("Diario") && game.getCurrentRoom().getName().equals("Stanza3")) {
            game.unlockCorridor("Stanza3", "Stanza4");
        }

        // Sblocca la porta tra Stanza2 e Stanza3 se si raccoglie l'amuleto nella Stanza2
        if (i.hasName("Amuleto") && game.getCurrentRoom().getName().equals("Stanza2")) {
            game.unlockCorridor("Stanza2", "Stanza3");
        }
    }
    
    /**
     * Metodo per controllare la condizione di fine gioco.
     */
    public void checkEndGame() {
        if (game.getCurrentRoom().getName().equals("Stanza5")) {
            boolean hasCandela = game.getCurrentRoom().getItems().stream().anyMatch(i -> i.hasName("Candela"));
            boolean hasAmuleto = game.getCurrentRoom().getItems().stream().anyMatch(i -> i.hasName("Amuleto"));
            boolean hasDiario = game.getCurrentRoom().getItems().stream().anyMatch(i -> i.hasName("Diario"));

            if(hasCandela && hasAmuleto && hasDiario) {
                while(InputService.Event != 0) {
                    try {
                        Thread.sleep(100); 
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                InputService.Event = 2;
                InputService.gameFlow("");
            }
        }
    }
}