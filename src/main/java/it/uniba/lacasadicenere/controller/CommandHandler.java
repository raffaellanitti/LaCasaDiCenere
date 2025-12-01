/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.uniba.lacasadicenere.controller;

import it.uniba.lacasadicenere.database.DatabaseH2;
import it.uniba.lacasadicenere.model.Item;
import it.uniba.lacasadicenere.model.ItemContainer;
import it.uniba.lacasadicenere.model.Game;
import it.uniba.lacasadicenere.model.RoomConnection;
import it.uniba.lacasadicenere.type.CommandType;
import it.uniba.lacasadicenere.type.CommandKey; 
import it.uniba.lacasadicenere.type.ParserOutput;
import it.uniba.lacasadicenere.service.OutputService;

import java.util.HashMap;
import it.uniba.lacasadicenere.type.CommandAction;

/**
 * Esegue i comandi del gioco in base all'input parsato dall'utente.
 */
public class CommandHandler {
    
    private Game game;
    
    private final HashMap<CommandKey, CommandAction> commandMap;
    
    private Engine gameLogic;

    /**
     * Crea un comportamento per i comandi di movimento direzionale.
     */
    private CommandAction createDirectionCommandBehavior(CommandType direction) {
    return p -> {
        RoomConnection corridor = game.getCorridorMap().stream()
                .filter(c -> c.getStartingRoom().getName().equals(game.getCurrentRoom().getName()) 
                          && c.getDirection() == direction)
                .findFirst()
                .orElse(null);
        
                if(corridor != null && !corridor.isLocked()) {
                game.setCurrentRoom(corridor.getArrivingRoom());
                DatabaseH2.printFromDB("Osserva", game.getCurrentRoom().getName(), "true", "0", "0");
            } else if (corridor != null && corridor.isLocked()) {
                OutputService.displayText("Il corridoio verso " + direction + " è bloccato!");
            } else {
                OutputService.displayText("Non c'è un passaggio verso " + direction + "!");
            }
        };  
    }

    /**
    * Costruttore della classe CommandExecutor.
    * @param game
    */
    public CommandHandler(Game game) {
        this.game = game;
        this.gameLogic = new Engine(game);
        commandMap = new HashMap<>();
    
        commandMap.put(new CommandKey(CommandType.NORD, 0),
            createDirectionCommandBehavior(CommandType.NORD));
        commandMap.put(new CommandKey(CommandType.SUD, 0),
            createDirectionCommandBehavior(CommandType.SUD));
        commandMap.put(new CommandKey(CommandType.EST, 0),
            createDirectionCommandBehavior(CommandType.EST));
        commandMap.put(new CommandKey(CommandType.OVEST, 0),
            createDirectionCommandBehavior(CommandType.OVEST));
        
        commandMap.put(new CommandKey(CommandType.OSSERVA, 0),
        p -> DatabaseH2.printFromDB("Osserva", game.getCurrentRoom().getName(), "true", "0", "0"));

        commandMap.put(new CommandKey(CommandType.OSSERVA, 1),
        p -> {
            if(game.getCurrentRoom().getItems().contains(p.getItem1())) {
                DatabaseH2.printFromDB("Osserva", game.getCurrentRoom().getName(), "true", p.getItem1().getName(), "0");
            } else if(game.getInventory().contains(p.getItem1())) {
                OutputService.displayText("Hai " + p.getItem1().getName() + " nell'inventario.");
            } else {
                OutputService.displayText("Non c'è " + p.getItem1().getName() + " qui.");
            }
        });
    
        commandMap.put(new CommandKey(CommandType.PRENDI, 1),
        p -> {
            if (p.getItem1() == null) {
                OutputService.displayText("L'oggetto specificato non è stato riconosciuto o non è disponibile in questo momento.");
                return;
            }
                    
            if(game.getInventory().contains(p.getItem1())) {
                OutputService.displayText("Hai già " + p.getItem1().getName() + " nell'inventario.");
                return;
            } 

            if(game.getCurrentRoom().getItems().contains(p.getItem1())) {
                if(p.getItem1().isPickable()) {
                    game.addInventory(p.getItem1());
                    game.getCurrentRoom().removeItem(p.getItem1().getName());
                    gameLogic.postPickUp(p.getItem1());
                    OutputService.displayText("Hai raccolto " + p.getItem1().getName() + ".");
                } else {
                    OutputService.displayText(p.getItem1().getName() + " non può essere raccolto.");
                }
                return;
            }

            ItemContainer parentContainer = findContainer(p.getItem1());

            if (parentContainer != null) {
                if (p.getItem1().isPickable()) { 
                    if (!gameLogic.canPickUp(p.getItem1(), parentContainer)) {
                        return; 
                    }
                    parentContainer.remove(p.getItem1()); 
                    game.addInventory(p.getItem1());
                    gameLogic.postPickUpFromContainer(p.getItem1(), parentContainer); 
                    OutputService.displayText("Hai preso " + p.getItem1().getName() + " da " + parentContainer.getName() + ".");
                } else {
                    OutputService.displayText("Non puoi raccogliere " + p.getItem1().getName() + ".");
                }
                return;
            }
            OutputService.displayText(p.getItem1().getName() + " non è nella stanza.");
        });

        commandMap.put(new CommandKey(CommandType.USA, 1),
        p -> {
            if(game.getInventory().contains(p.getItem1())) {
                boolean used = gameLogic.useSingle(p.getItem1());
                if(!used) {
                    OutputService.displayText("Non succede nulla usando " + p.getItem1().getName() + ".");
                    return;
                }
            } else {
                OutputService.displayText("Non possiedi questo oggetto.");
            }
        });

        commandMap.put(new CommandKey(CommandType.USA, 2),
        p -> {
            if(!game.getInventory().contains(p.getItem1())) {
                OutputService.displayText("Non possiedi " + p.getItem1().getName() + ".");
                return; 
            }

            if(p.getItem2() == null) {
                OutputService.displayText("Devi specificare il secondo oggetto da usare.");
                return;
            }
                    
            boolean hasItem2InInventory = game.getInventory().contains(p.getItem2());
            boolean hasItem2InRoom = game.getCurrentRoom().getItems().contains(p.getItem2());
        
            if(!hasItem2InInventory && !hasItem2InRoom) {
                OutputService.displayText("Non puoi usare " + p.getItem1().getName() + 
                    " con " + p.getItem2().getName() + " perché non è disponibile.");
                return;
            }
                    
            boolean used = gameLogic.useDouble(p.getItem1(), p.getItem2());
            if(!used) {
                OutputService.displayText("Non succede nulla usando " + 
                    p.getItem1().getName() + " con " + p.getItem2().getName() + ".");
            }
        });
        
        commandMap.put(new CommandKey(CommandType.LASCIA, 1), 
        p -> {
            if (!game.getInventory().contains(p.getItem1())) {
                OutputService.displayText("Non possiedi " + p.getItem1().getName() + ".");
                return;
            }

            game.removeInventory(p.getItem1());
            if (game.getCurrentRoom() != null) {
                game.getCurrentRoom().addItems(p.getItem1());
            }
            OutputService.displayText("Hai lasciato " + p.getItem1().getName() + " nella stanza.");

            gameLogic.checkEndGame();
        });
    }
    
    /**
    * Cerca un oggetto all'interno dei contenitori nella stanza corrente o nell'inventario.
    */
    private ItemContainer findContainer(Item item) {
        
        // Cerca nei contenitori nella stanza corrente
        if (game.getCurrentRoom().getItems() != null) {
            for (Item roomItem : game.getCurrentRoom().getItems()) {
                if (roomItem instanceof ItemContainer) {
                    ItemContainer container = (ItemContainer) roomItem;
                    if (container.getList() != null && container.getList().stream()
                            .anyMatch(i -> i.hasName(item.getName()))) { 
                        return container;
                    }
                }
            }
        }
        
        // Cerca nei contenitori nell'inventario
        if (game.getInventory() != null) {
            for (Item inventoryItem : game.getInventory()) {
                if (inventoryItem instanceof ItemContainer) {
                    ItemContainer container = (ItemContainer) inventoryItem;
                    if (container.getList() != null && container.getList().stream()
                            .anyMatch(i -> i.hasName(item.getName()))) {  
                        return container;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Esegue il comando appropriato in base all'output del parser.
     * 
     * @param p L'output del parser contenente comando e oggetto
     */
    public void execute(ParserOutput p) {
        int args = p.getArgs();
        CommandKey key = new CommandKey(p.getCommand(), args);
        
        CommandAction behavior = commandMap.get(key);

        if (behavior != null) {
            behavior.execute(p);
        } else {
            OutputService.displayText("Comando non riconosciuto o non valido con gli argomenti forniti.");
        }
    }
}