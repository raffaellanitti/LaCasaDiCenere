/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.service;

import it.uniba.lacasadicenere.database.DatabaseH2;
import it.uniba.lacasadicenere.model.Game;
import it.uniba.lacasadicenere.model.Item;
import it.uniba.lacasadicenere.view.GamePanel;
import it.uniba.lacasadicenere.view.MainFrame;
import it.uniba.lacasadicenere.controller.CommandHandler;
import it.uniba.lacasadicenere.controller.Parser;
import it.uniba.lacasadicenere.type.ParserOutput;
import it.uniba.lacasadicenere.util.TextAnimator;

import java.util.List;

/**
 * Classe che gestisce il flusso di input dell'utente e gli eventi speciali del gioco.
 */
public class InputService {

    public static int Event;

    private static Parser parser;
    private static CommandHandler commandExecutor;
    private static MirrorGame mirrorGame;

    /**
     * Punto di ingresso principale per processare l'input dell'utente.
     * Non visualizza l'input dell'utente - lo gestiscono i metodi specifici.
     */
    public static void gameFlow(final String text) {
        switch(Event) {
            case 0:
                parserFlow(text);
                break;
            case 1:
                mirrorGameFlow(text);
                break;
            case 2:
                endingFlow(text);
                break;
            default:
                parserFlow(text);
                break;
        }
    }

    /**
     * Gestisce il flusso normale del parser per i comandi di gioco.
     */
    private static void parserFlow(final String text) {

        while (TextAnimator.isWriting()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        OutputService.displayTextImmediate("> " + text);

        if(parser == null) {
            OutputService.displayText("Errore nell'inizializzazione del parser. Avviare di nuovo il gioco.");
            return;
        }
        ParserOutput parserOutput = parser.parse(text);

        if(parserOutput.getCommand() != null) {
            commandExecutor.execute(parserOutput);
        } else {
            OutputService.displayText("Comando non riconosciuto. Riprova.");
        }
    }
    
    /**
     * Gestisce il mini-gioco degli specchi.
     */
    private static void mirrorGameFlow(final String text) {

        while (TextAnimator.isWriting()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        OutputService.displayTextImmediate("> " + text);

        if(mirrorGame == null) {
            OutputService.displayText("Errore: gioco non inizializzato.");
            Event = 0;
            return;
        }        
        mirrorGame.checkAnswer(text);
    }

    /**
     * Gestisce il finale del gioco quando il giocatore completa tutti gli obiettivi.
     */
    private static void endingFlow(final String text) {
        while (TextAnimator.isWriting()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        if (text != null && !text.trim().isEmpty()) {
            OutputService.displayTextImmediate("> " + text);
        }
        
        String testo = "Mentre poggi la candela, l'amuleto e il diario sull'altare, un bagliore caldo avvolge la cripta. "
                + "La casa sospira, come liberata da un antico peso. Le ombre svaniscono, i muri anneriti sembrano respirare "
                + "e una sensazione di pace ti avvolge. Hai riportato la luce, la memoria e la protezione… "
                + "e La Casa di Cenere finalmente riposa.";

        TextAnimator scrittura = new TextAnimator(testo, 50);
        scrittura.start();

        new Thread(() -> {
            try {
                scrittura.join();
                
                TextAnimator pausa = new TextAnimator(3000);
                pausa.start();
                pausa.join();
                
                MainFrame.closeGame();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Configura il flusso di gioco per una nuova partita.
     */
    public static void setUpGameFlow(final Game game) {
        Event = 0;
        if(game.getCurrentRoom() == null) {
            OutputService.displayText("Errore nell'inizializzazione della stanza corrente. Avviare di nuovo il gioco.");
            return;
        }
        DatabaseH2.printFromDB("0", game.getCurrentRoom().getName(), "true", "0", "0");

        mirrorGame = MirrorGame.getInstance();
        parser = new Parser();
        commandExecutor = new CommandHandler(game);

        commandExecutor.setOnRoomChangeListener(() -> {
            GameFlowController.updateMap();
        });
    }
        
    /**
     * Configura il flusso di gioco per una partita caricata.
     */
    public static void setUpLoadedGameFlow(final Game game) {
        Event = 0;
        
        mirrorGame = MirrorGame.getInstance();
        parser = new Parser();
        commandExecutor = new CommandHandler(game);

        commandExecutor.setOnRoomChangeListener(() -> {
            GameFlowController.updateMap();
        });
        
        List<String> itemsNames = game.getInventory().stream().map(Item::getName).toList();
        String[] itemsNamesArray = itemsNames.toArray(new String[0]);
        GamePanel.updateInventoryTextArea(itemsNamesArray);
        if (game.getCurrentRoom().getName().equals("Stanza1")) {
            DatabaseH2.printFromDB("0", game.getCurrentRoom().getName(), "true", "0", "0");
        }
        else
        {
            DatabaseH2.printFromDB("Osserva", game.getCurrentRoom().getName(), "true", "0", "0");
        }
    }
}