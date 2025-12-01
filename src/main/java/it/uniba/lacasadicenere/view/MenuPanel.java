/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.uniba.lacasadicenere.view;

import it.uniba.lacasadicenere.model.Game;
import it.uniba.lacasadicenere.model.Item;
import it.uniba.lacasadicenere.controller.GameController;
import it.uniba.lacasadicenere.service.InputService;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Menu principale del gioco con sfondo caricato correttamente dal classpath
 */
public class MenuPanel extends JPanel {
    
    private JPanel backgroundPanel;
    private JButton newGame;
    private JButton help;
    private JButton loadGame;
    private JButton credits;
    
    GameController gameManager = new GameController();
    
    /**
     * Costruttore del menu principale
     */
    public MenuPanel() {
        initComponents();
    }
    
    /**
     * Inizializza i componenti grafici del menu principale
     */
    private void initComponents() {
        
        // DEFINIZIONE COLORI 
        final Color COLD_LIGHT = new Color(200, 220, 255); 
        final Color SEMI_TRANSPARENT_BG = new Color(50, 60, 70, 60); 
        final Color COLD_SELECT_COLOR = new Color(100, 120, 140, 100); 
        final Color FOG_BACKGROUND = new Color(30, 30, 35);
        
        // Background panel con immagine caricata dal classpath
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon img = new ImageIcon("src/main/resources/img/sfondo.png");
                Image image = img.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
            
        
        newGame = new JButton();
        newGame.setAlignmentX(Component.LEFT_ALIGNMENT);
        help = new JButton();
        help.setText("?");
        loadGame = new JButton();
        credits = new JButton();
        
        setPreferredSize(new Dimension(800, 600));
        setSize(new Dimension(800, 600));
        
        backgroundPanel.setMinimumSize(new Dimension(800, 600));
        backgroundPanel.setPreferredSize(new Dimension(800, 600));
        backgroundPanel.setRequestFocusEnabled(false);
        backgroundPanel.setBackground(FOG_BACKGROUND);
        
        // --- BUTTON NUOVA PARTITA ---
        newGame.setUI(new MetalButtonUI() {
               protected Color getSelectColor() {
                   return COLD_SELECT_COLOR;
               }
        });
        newGame.setFocusPainted(false);
        newGame.setBackground(SEMI_TRANSPARENT_BG);
        newGame.setForeground(COLD_LIGHT);
        newGame.setFont(new Font("Otacon", Font.BOLD, 24));
        newGame.setBorderPainted(true);
        newGame.setBorder(BorderFactory.createLineBorder(COLD_LIGHT, 3));
        newGame.setText("NUOVA PARTITA");
        newGame.setOpaque(true);
        newGame.setContentAreaFilled(true);
        newGame.setMaximumSize(new Dimension(240, 60));
        newGame.setMinimumSize(new Dimension(240, 60));
        newGame.setPreferredSize(new Dimension(240, 60));
        newGame.addActionListener(this::newGameActionPerformed);
        
        // --- BUTTON HELP ---
        help.setUI(new MetalButtonUI() {
            protected Color getSelectColor () {
                return COLD_SELECT_COLOR;
            }
        });
        help.setFocusPainted(false);
        help.setBackground(SEMI_TRANSPARENT_BG);
        help.setForeground(COLD_LIGHT);
        help.setFont(new Font("Otacon", Font.BOLD, 24));
        help.setBorderPainted(true);
        help.setBorder(BorderFactory.createLineBorder(COLD_LIGHT, 2));
        help.setMargin(new Insets(0, 0, 0, 0));
        help.setMaximumSize(new Dimension(40, 40));
        help.setMinimumSize(new Dimension(40, 40));
        help.setPreferredSize(new Dimension(40, 40));
        help.setOpaque(true);
        help.setContentAreaFilled(true);
        help.addActionListener(this::helpActionPerformed);

        // --- BUTTON CARICA PARTITA ---
        loadGame.setUI(new MetalButtonUI() {
            protected Color getSelectColor () {
                return COLD_SELECT_COLOR;
            }
        });
        loadGame.setFocusPainted(false);
        loadGame.setBackground(SEMI_TRANSPARENT_BG);
        loadGame.setForeground(COLD_LIGHT);
        loadGame.setFont(new Font("Otacon", Font.BOLD, 24));
        loadGame.setBorderPainted(true);
        loadGame.setBorder(BorderFactory.createLineBorder(COLD_LIGHT, 3));
        loadGame.setText("CARICA PARTITA");
        loadGame.setMaximumSize(new Dimension(240, 60));
        loadGame.setMinimumSize(new Dimension(240, 60));
        loadGame.setPreferredSize(new Dimension(240, 60));
        loadGame.setOpaque(true);
        loadGame.setContentAreaFilled(true);
        loadGame.addActionListener(evt -> {
            try {
                loadGameActionPerformed(evt);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        // --- BUTTON CREDITS ---
        credits.setUI(new MetalButtonUI() {
            protected Color getSelectColor () {
                return COLD_SELECT_COLOR;
            }
        });
        credits.setFocusPainted(false);
        credits.setBackground(SEMI_TRANSPARENT_BG);
        credits.setForeground(COLD_LIGHT);
        credits.setFont(new Font("Otacon", Font.BOLD, 24));
        credits.setBorderPainted(true);
        credits.setBorder(BorderFactory.createLineBorder(COLD_LIGHT, 3));
        credits.setText("RICONOSCIMENTI");
        credits.setMaximumSize(new Dimension(240, 60));
        credits.setMinimumSize(new Dimension(240, 60));
        credits.setPreferredSize(new Dimension(240, 60));
        credits.setOpaque(true);
        credits.setContentAreaFilled(true);
        credits.addActionListener(this::creditsActionPerformed);

        // --- LAYOUT ---
        GroupLayout backgroundPanelLayout = new GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        
        final int BUTTON_WIDTH = 240;
        final int HORIZONTAL_GAP = 50; 
        final int TOTAL_BUTTONS_WIDTH = (BUTTON_WIDTH * 2) + HORIZONTAL_GAP;
        final int HORIZONTAL_PADDING = (800 - TOTAL_BUTTONS_WIDTH) / 2;
        
        backgroundPanelLayout.setHorizontalGroup(
                backgroundPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(backgroundPanelLayout.createSequentialGroup()
                                .addGap(25)
                                .addComponent(help, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(credits, GroupLayout.PREFERRED_SIZE, BUTTON_WIDTH, GroupLayout.PREFERRED_SIZE)
                                .addGap(25))
                        .addGroup(backgroundPanelLayout.createSequentialGroup()
                                .addGap(25)
                                .addGap(HORIZONTAL_PADDING) 
                                .addComponent(newGame, GroupLayout.PREFERRED_SIZE, BUTTON_WIDTH, GroupLayout.PREFERRED_SIZE)
                                .addGap(HORIZONTAL_GAP) 
                                .addComponent(loadGame, GroupLayout.PREFERRED_SIZE, BUTTON_WIDTH, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(HORIZONTAL_PADDING, Short.MAX_VALUE))
        );
        
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(backgroundPanelLayout.createSequentialGroup()
                    .addGap(25) 
                    .addGroup(backgroundPanelLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(help, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                        .addComponent(credits, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)) 
                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(backgroundPanelLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(newGame, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addComponent(loadGame, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
                    .addGap(40))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(backgroundPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(backgroundPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }
    
    /**
     * Azione eseguita al click del bottone "Nuova Partita"
     * @param evt
     */
    private void newGameActionPerformed(ActionEvent evt) {
        gameManager.createGame();
        Game game = Game.getInstance();
        
        new Thread(() -> InputService.setUpGameFlow(game)).start();
        
        CardLayout cl = (CardLayout) getParent().getLayout();
        cl.show(getParent(), "GamePanel");
        
        Timer timer = new Timer(1000, e -> {
            GamePanel.updateInventoryTextArea(
            Game.getInstance().getInventory().stream()
                    .map(Item::getName)
                    .toArray(String[]::new)
            );
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Azione eseguita al click del bottone "?"
     * @param evt
     */
    private void helpActionPerformed(ActionEvent evt) {
        try {
            HelpDialog helpGUI = HelpDialog.getInstance();
            helpGUI.setLocationRelativeTo(null);
            helpGUI.setVisible(true);
        } catch(Exception e) {
            showMessageDialog(this, "Errore nell'apertura della guida", "Errore", ERROR_MESSAGE);
        }
    }
    
    /**
     * Azione eseguita al click del bottone "Carica Partita"
     * @param evt
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void loadGameActionPerformed(ActionEvent evt) throws IOException, ClassNotFoundException {
        gameManager.resetItems();
        boolean loadedGameSuccessfully = gameManager.loadGame();
        
        if(loadedGameSuccessfully) {
            Game game = Game.getInstance();
            CardLayout cl = (CardLayout) getParent().getLayout();
            cl.show(getParent(), "GamePanel");
            
            new Thread(() -> InputService.setUpLoadedGameFlow(game)).start();
        } else {
            showMessageDialog(null, "Nessuna partita salvata trovata.", "Errore", ERROR_MESSAGE);
        }
    }

    /**
    * Azione eseguita al click del bottone "Riconoscimenti"
    * Apre la pagina HTML dei crediti nel browser predefinito.
    * @param evt
    */
    private void creditsActionPerformed(ActionEvent evt) {
        try {
            String apiUrl = "http://localhost:8080/api/credits";
        
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    desktop.browse(new java.net.URI(apiUrl));
                } else {
                    showMessageDialog(this, 
                        "Il sistema non supporta l'apertura del browser.\n" +
                        "Apri manualmente: " + apiUrl, 
                        "Info", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                showMessageDialog(this, 
                    "Desktop non supportato su questo sistema.\n" +
                    "Apri manualmente: " + apiUrl, 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
         }
        
        } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
            showMessageDialog(this, 
                "URL non valido: " + e.getMessage(), 
                "Errore", 
                ERROR_MESSAGE);
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
         showMessageDialog(this, 
                "Impossibile aprire il browser.\n" +
                "Assicurati che il server REST sia avviato.\n\n" +
                "Puoi aprire manualmente: http://localhost:8080/api/credits\n\n" +
                "Errore: " + e.getMessage(), 
                "Errore", 
                ERROR_MESSAGE);
            
        } catch (Exception e) {
            e.printStackTrace();
            showMessageDialog(this, 
                "Errore imprevisto: " + e.getMessage(), 
                "Errore", 
                ERROR_MESSAGE);
        }
    }
}