/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.uniba.lacasadicenere.view;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.CardLayout;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import it.uniba.lacasadicenere.service.GameFlowController;

/**
 * Gestore principale della GUI con correzioni per il caricamento delle risorse
 */
public class MainFrame extends JFrame {
    public static GamePanel game;
    
    /**
     * Costruttore della MainFrame
     */
    public MainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("La Casa di Cenere");
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);
        
        try {
            Image icona = ImageIO.read(new File("src/main/resources/img/icona.png"));
            setIconImage(icona);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        JPanel cards = new JPanel(new CardLayout());
        MenuPanel menu = new MenuPanel();
        game = new GamePanel();

        GameFlowController.setGamePanel(game);

        cards.add(menu, "MenuPanel");
        cards.add(game, "GamePanel");

        add(cards);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Chiude il gioco e torna al menu principale
     */
    public static void closeGame() {
        game.goBack();
    }
}