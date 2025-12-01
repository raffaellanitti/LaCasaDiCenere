package it.uniba.lacasadicenere.view;

import it.uniba.lacasadicenere.service.GameFlowController;
import it.uniba.lacasadicenere.controller.GameController;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI principale del gioco con layout migliorato e dimensioni ottimizzate.
 */
public class GamePanel extends JPanel {

    // Componenti della ToolBar
    private JButton goBackButton;
    private JButton saveGameButton;
    private JButton helpButton;

    // Componenti di Visualizzazione
    private static JPanel imagePanel;
    private static JTextPane displayTextPane;
    private JScrollPane scrollPaneDisplayText;

    // Componenti di Inventario
    private static JTextArea inventoryTextArea;
    private JScrollPane scrollPaneInventoryText;

    // Componente di Input Utente
    private JTextField userInputField;
    private JToolBar toolBar;

    // Layout per il pannello delle immagini
    private static CardLayout cardLayout;

    public GamePanel() {
        UIManager.put("ScrollBar.width", 12);
        SwingUtilities.updateComponentTreeUI(this);
        initComponents();
        initImagePanel();
    }

    private void initImagePanel() {
        imagePanel.setPreferredSize(new Dimension(550, 400));
        imagePanel.setMaximumSize(new Dimension(550, 400));
        imagePanel.setMinimumSize(new Dimension(550, 400));
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 220, 255), 3));
        imagePanel.setBackground(new Color(30, 30, 35, 255));

        cardLayout = new CardLayout();
        cardLayout.setVgap(0);
        cardLayout.setHgap(0);
        imagePanel.setLayout(cardLayout);

        // Carica le 5 immagini delle stanze dal classpath
        for(int i = 1; i <= 6; i++) {
            final int roomNumber = i;
            final String imagePath = "src/main/resources/img/Stanza" + roomNumber + ".png";
            imagePanel.add(new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ImageIcon image = new ImageIcon(imagePath);
                    g.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }, "Stanza" + roomNumber);
        }
    }

    /**
     * Restituisce le metriche del font del displayTextPane.
     * @return metriche del font
     */
    public static FontMetrics getTextPaneFontMetrics() {
        return displayTextPane.getFontMetrics(displayTextPane.getFont());
    }

    /**
     * Restituisce la larghezza del displayTextPane.
     * @return larghezza del displayTextPane
     */
    public static int getTextPaneWidth() { 
        return displayTextPane.getWidth();
    }

    /**
     * Inizializza i componenti della GUI.
     */
    private void initComponents() {
        // DEFINIZIONE COLORI
        final Color COLD_LIGHT = new Color(200, 220, 255, 255); 
        final Color COLD_SELECT_COLOR = new Color(100, 120, 140, 150); 
        final Color FOG_BACKGROUND = new Color(30, 30, 35, 255); 
        final Color DARK_FOG_CONTENT = new Color(45, 50, 55, 255); 

        setBackground(FOG_BACKGROUND);
        setPreferredSize(new Dimension(800, 600));

        // Inizializzazione componenti
        imagePanel = new JPanel();
        toolBar = new JToolBar();
        goBackButton = new JButton();
        saveGameButton = new JButton();
        helpButton = new JButton();
        displayTextPane = new JTextPane();
        scrollPaneDisplayText = new JScrollPane();
        inventoryTextArea = new JTextArea();
        scrollPaneInventoryText = new JScrollPane();
        userInputField = new JTextField();

        // --- TOOLBAR ---
        toolBar.setBorderPainted(false);
        toolBar.setFloatable(false);
        toolBar.setBackground(FOG_BACKGROUND);
        toolBar.add(Box.createHorizontalStrut(5));

        // --- BUTTONS ---
        setupButton(goBackButton, "<", COLD_LIGHT, COLD_SELECT_COLOR, DARK_FOG_CONTENT, 14);
        goBackButton.addActionListener(this::goBackButtonActionPerformed);
        toolBar.add(goBackButton);
        toolBar.add(Box.createHorizontalStrut(10));

        setupButton(saveGameButton, "Salva", COLD_LIGHT, COLD_SELECT_COLOR, DARK_FOG_CONTENT, 14);
        saveGameButton.addActionListener(evt -> {
            try {
                saveGameButtonActionPerformed(evt);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        toolBar.add(saveGameButton);
        toolBar.add(Box.createHorizontalStrut(10));

        setupButton(helpButton, "?", COLD_LIGHT, COLD_SELECT_COLOR, DARK_FOG_CONTENT, 18);
        helpButton.setMargin(new Insets(0, 12, 0, 12));
        helpButton.addActionListener(this::helpButtonActionPerformed);
        toolBar.add(helpButton);

        // --- INVENTORY TEXT AREA ---
        inventoryTextArea.setEditable(false);
        inventoryTextArea.setOpaque(true);
        inventoryTextArea.setBackground(DARK_FOG_CONTENT);
        inventoryTextArea.setForeground(COLD_LIGHT);
        inventoryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inventoryTextArea.setText(" Inventario:\n");
        inventoryTextArea.setLineWrap(true);
        inventoryTextArea.setWrapStyleWord(true);

        scrollPaneInventoryText.setViewportView(inventoryTextArea);
        scrollPaneInventoryText.setPreferredSize(new Dimension(200, 550));
        scrollPaneInventoryText.setBorder(BorderFactory.createMatteBorder(0, 5, 5, 5, COLD_LIGHT));

        // --- DISPLAY TEXT PANE ---
        displayTextPane.setEditable(false);
        displayTextPane.setFocusable(false);
        displayTextPane.setFont(new Font("Monospaced", Font.PLAIN, 13));
        displayTextPane.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        displayTextPane.setOpaque(true);
        displayTextPane.setBackground(DARK_FOG_CONTENT);
        displayTextPane.setForeground(COLD_LIGHT);
        
        // Auto-scroll automatico
        DefaultCaret caret = (DefaultCaret) displayTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPaneDisplayText.setBackground(DARK_FOG_CONTENT);
        scrollPaneDisplayText.setViewportView(displayTextPane);
        scrollPaneDisplayText.setPreferredSize(new Dimension(550, 120));
        scrollPaneDisplayText.setMinimumSize(new Dimension(550, 120));
        scrollPaneDisplayText.setMaximumSize(new Dimension(550, 120));
        scrollPaneDisplayText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneDisplayText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneDisplayText.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 0, COLD_LIGHT));

        // --- USER INPUT FIELD ---
        userInputField.setMargin(new Insets(0, 8, 0, 8));
        userInputField.setForeground(COLD_LIGHT);
        userInputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        userInputField.setOpaque(true);
        userInputField.setBackground(DARK_FOG_CONTENT);
        userInputField.setCaretColor(COLD_LIGHT);
        userInputField.setBorder(BorderFactory.createMatteBorder(0, 5, 5, 0, COLD_LIGHT));
        userInputField.addActionListener(this::userInputFieldActionPerformed);
        
        GameFlowController.startInputListener();

        // --- LAYOUT ---
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(toolBar)
                .addGroup(layout.createSequentialGroup()
                    .addGap(10)
                    .addComponent(scrollPaneInventoryText, 200, 200, 200) 
                    .addGap(10)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(imagePanel, 550, 550, 550)
                        .addComponent(scrollPaneDisplayText, 550, 550, 550)
                        .addComponent(userInputField, 550, 550, 550))
                    .addGap(10))
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
                    .addGap(10)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(scrollPaneInventoryText, 10, 540, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(imagePanel, 400, 400, 400) 
                            .addComponent(scrollPaneDisplayText, 100, 100, 100)
                            .addComponent(userInputField, 40, 40, 40)))
                    .addGap(10))
        );
    }

    /**
     * Configura un JButton con le proprietà specificate.
     * @param button
     * @param text
     * @param foreground
     * @param selectColor
     * @param background
     * @param fontSize
     */
    private void setupButton(JButton button, String text, Color foreground, 
                            Color selectColor, Color background, int fontSize) {
        button.setUI(new MetalButtonUI() {
            protected Color getSelectColor() {
                return selectColor;
            }
        });
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(foreground, 2));
        button.setFont(new Font("Otacon", Font.BOLD, fontSize));
        button.setText(text);
        button.setFocusable(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }

    /**
     * Gestione dell'evento di click sul pulsante "saveGame".
     * @param evt
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void saveGameButtonActionPerformed(ActionEvent evt) throws IOException, ClassNotFoundException {
        Font font = new Font("Otacon", Font.PLAIN, 13);
        UIManager.put("OptionPane.messageFont", font);
        int save = JOptionPane.showConfirmDialog(this, "Vuoi salvare la partita?", "Salva", JOptionPane.YES_NO_OPTION);

        if (save == JOptionPane.YES_OPTION) {
            saveFile();
        } else {
            notSavedFile();
        }
    }

    /**
     * Salva lo stato attuale del gioco.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void saveFile() throws IOException, ClassNotFoundException {
        GameController gameManager = new GameController();
        
        gameManager.saveGame();
        JOptionPane.showMessageDialog(this, "Partita salvata con successo!", "Salva", JOptionPane.INFORMATION_MESSAGE);
        goBack();
    }

    /**
     * Notifica l'utente che la partita non è stata salvata.
     */
    private void notSavedFile() {
        JOptionPane.showMessageDialog(this, "Partita non salvata", "Salva", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Gestione dell'evento di click sul pulsante "goBack".
     * @param evt
     */
    private void goBackButtonActionPerformed(ActionEvent evt) {
        UIManager.put("OptionPane.messageFont", new Font("Otacon", Font.PLAIN, 13));
        int back = JOptionPane.showConfirmDialog(this, "Sei sicuro di voler tornare al Menu senza salvare?", "Indietro", JOptionPane.YES_NO_OPTION);

        if (back == JOptionPane.YES_OPTION) {
            goBack();
        }
    }

    /**
     * Torna al Menu principale del gioco.  
     */
    public void goBack() {
        CardLayout cl = (CardLayout) getParent().getLayout();
        cl.show(getParent(), "MenuPanel");
        displayTextPane.setText("");
        inventoryTextArea.setText(" Inventario:\n");
        userInputField.setText("");
    }

    /**
     * Gestione dell'evento di click sul pulsante "help".
     * @param evt
     */
    private void helpButtonActionPerformed(ActionEvent evt) {
        HelpDialog helpGUI = HelpDialog.getInstance();
        helpGUI.setVisible(true);
    }

    /**
     * Gestione dell'evento di invio del campo di input utente.
     * @param evt
     */
    private void userInputFieldActionPerformed(ActionEvent evt) {
        String text = userInputField.getText().trim();
        if (!text.isEmpty()) {
            userInputField.setText("");
            GameFlowController.setUserInput(text);
        }
    }

    /**
     * Imposta il testo del displayTextPane.
     * @param text
     */
    public static void displayTextPaneSetText(String text) {
        if (displayTextPane.getText().isEmpty()) {
            displayTextPane.setText(text);
        } else {
            displayTextPane.setText(displayTextPane.getText() + "\n" + text);
        }
        displayTextPane.setCaretPosition(displayTextPane.getDocument().getLength());
    }
    
    /**
     * Aggiunge del testo al displayTextPane.
     * @param text
     */
    public static void displayTextPaneAppendText(String text) {
        SwingUtilities.invokeLater(() -> {
            try {
                javax.swing.text.Document doc = displayTextPane.getDocument();
                doc.insertString(doc.getLength(), text, null);
                displayTextPane.setCaretPosition(doc.getLength());
            } catch (javax.swing.text.BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Imposta il pannello immagine corrente.
     * @param panelName
     */
    public static void setImagePanel(String panelName) {
        Timer timer = new Timer(600, _e -> cardLayout.show(imagePanel, panelName));
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Aggiorna il testo dell'inventario.
     * @param items
     */
    public static void updateInventoryTextArea(String[] items) {
        StringBuilder inventory = new StringBuilder(" Inventario:\n");
        
        for (String item : items) {
            inventory.append(" • ").append(item).append("\n");
        }

        inventoryTextArea.setText(inventory.toString());
    }

    /**
     * Restituisce il componente JTextArea dell'inventario.
     */
    public static JTextArea getInventoryTextArea() {
        return inventoryTextArea;
    }

    /**
     * Restituisce i nomi degli oggetti presenti nell'inventario.
     * @return array di nomi degli oggetti
     */
    public static String[] getInventoryItemNames() {
        String text = inventoryTextArea.getText();
        String[] lines = text.split("\n");
        List<String> itemNames = new ArrayList<>();
        
        for (int i = 1; i < lines.length; i++) {
            String name = lines[i].replace(" • ", "").replace(" - ", "").trim();
            if (!name.isEmpty()) {
                itemNames.add(name);
            }
        }
        return itemNames.toArray(new String[0]);
    }
}