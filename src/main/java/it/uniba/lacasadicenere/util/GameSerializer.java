/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.uniba.lacasadicenere.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import it.uniba.lacasadicenere.model.RoomConnection;
import it.uniba.lacasadicenere.model.Game;
import it.uniba.lacasadicenere.model.Item;
import it.uniba.lacasadicenere.model.Room;
import it.uniba.lacasadicenere.controller.GameController;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe per la conversione tra JSON e classi Java.
 */
public class GameSerializer {
    
    /**
     * Metodo per convertire i file JSON in classi Java.
     * @return Map<String, Item>
     */
    public Map<String, Item> convertJsonToJavaClass() {
        ensureResourceDirectoriesExist();
        return processJsonFiles("src/main/resources/json/Game.json", "src/main/resources/json/Items.json");
    }

    /**
     * Metodo per caricare lo stato di gioco da un file JSON.
     * @return Map<String, Item>
     */
    public Map<String, Item> loadGame() {
        return processJsonFiles("src/main/resources/LoadedGame.json", "src/main/resources/LoadedItems.json");
    }

    /**
     * Metodo per assicurarsi che le directory e i file necessari esistano.
     * In caso contrario, li crea.
     */
    private void ensureResourceDirectoriesExist() {
        try {
            Path jsonDir = Paths.get("src/main/resources/json");
            if (!Files.exists(jsonDir)) {
                Files.createDirectories(jsonDir);
            }
            Path gameJson = jsonDir.resolve("Game.json");
            Path itemsJson = jsonDir.resolve("Items.json");
            if (!Files.exists(gameJson)) {
                Files.write(gameJson, "{}".getBytes());
            }
            if (!Files.exists(itemsJson)) {
                Files.write(itemsJson, "[]".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to ensure resource directories exist", e);
        }
    }

    /**
     * Metodo per processare i file JSON e convertire in classi Java.
     * @param gameFilePath
     * @param itemsFilePath
     * @return Map<String, Item>
     */
    private Map<String, Item> processJsonFiles(String gameFilePath, String itemsFilePath) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Item.class, new ItemDeserializer())
                .create();

        Map<String, Item> items = new HashMap<>();
        Map<String, Room> rooms = new HashMap<>();

        try {
            Path gamePath = Paths.get(gameFilePath);
            if (gameFilePath.endsWith("LoadedGame.json") && !Files.exists(gamePath)) {
                return new HashMap<>(); 
            }
            
            byte[] fileBytes = Files.readAllBytes(Paths.get(gameFilePath));
            if (fileBytes.length == 0) return new HashMap<>();

            JsonReader reader = new JsonReader(new FileReader(gameFilePath));
            Game game = gson.fromJson(reader, Game.class);
            if(game == null) return null;

            Game.setUpGame(game);

            if(game.getInventory() != null) {
                game.getInventory().forEach(item -> items.put(item.getName(), item));
            }

            if(game.getCorridorMap() != null) {
                game.getCorridorMap().forEach(corridor -> {
                    Room start = corridor.getStartingRoom();
                    Room end = corridor.getArrivingRoom();

                    if(!rooms.containsKey(start.getName())) {
                        rooms.put(start.getName(), start);
                        if(start.getItems() != null) {
                            start.getItems().forEach(item -> items.put(item.getName(), item));
                        }
                    } else {
                        corridor.setStartingRoom(rooms.get(start.getName()));
                    }
                    
                    if(!rooms.containsKey(end.getName())) {
                        rooms.put(end.getName(), end);
                        if(end.getItems() != null) {
                            end.getItems().forEach(item -> items.put(item.getName(), item));
                        }
                    } else {
                        corridor.setArrivingRoom(rooms.get(end.getName()));
                    }
                });
            }

            if(game.getCurrentRoom() != null) {
                game.setCurrentRoom(rooms.get(game.getCurrentRoom().getName()));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            Path itemsPath = Paths.get(itemsFilePath);
            if (itemsFilePath.endsWith("LoadedItems.json") && !Files.exists(itemsPath)) {
                return items; 
            }
            
            byte[] fileBytes = Files.readAllBytes(Paths.get(itemsFilePath));
            if(fileBytes.length == 0) {
                return items; 
            }
            try (JsonReader reader = new JsonReader(new FileReader(itemsFilePath))) {
                Type itemListType = new TypeToken<List<Item>>(){}.getType();
                List<Item> itemList = gson.fromJson(reader, itemListType);
    
                if (itemList != null) {
                    for (Item item : itemList) {
                        items.put(item.getName(), item);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return items;
    }

    /**
     * Metodo per salvare lo stato di gioco in un file JSON.
     */
    public void convertGameToJson() {
        Gson gson = new Gson();
        Game game = Game.getInstance();
        String json = gson.toJson(game);

        try {
            Files.write(Paths.get("src", "main", "resources", "LoadedGame.json"), json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo per salvare gli oggetti di gioco in un file JSON.
     */
    public void convertItemsToJson() {
        Gson gson = new Gson();
        Game game = Game.getInstance();
        GameController gameManager = new GameController();
        Set<Item> items = gameManager.getItems();

        Set<Room> rooms = game.getCorridorMap().stream()
                .map(RoomConnection::getStartingRoom)
                .collect(Collectors.toSet());

        Set<Item> itemsToSave = items.stream()
                .filter(item -> !game.getInventory().contains(item))
                .filter(item -> rooms.stream()
                        .noneMatch(room -> room.getItems().contains(item)))
                    .collect(Collectors.toSet());
        
        String json = gson.toJson(itemsToSave);
        try {
            Files.write(Paths.get("src", "main", "resources", "LoadedItems.json"), json.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}