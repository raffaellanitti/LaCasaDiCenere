/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package it.uniba.lacasadicenere.service;

import it.uniba.lacasadicenere.view.GamePanel;
import it.uniba.lacasadicenere.util.TextAnimator;

import java.awt.FontMetrics;

/**
 * Classe che gestisce la visualizzazione del testo nella GUI, 
 * formattando correttamente il testo con word wrapping.
 */
public class OutputService {

    private static final int MARGIN = 30; 
    private static final Object DISPLAY_LOCK = new Object(); 

    /**
     * Visualizza il testo formattato nella GUI.
     * @param text
     */
    public static void displayText(String text) {
        synchronized (DISPLAY_LOCK) {
            while (TextAnimator.isWriting()) {
                try {
                    DISPLAY_LOCK.wait(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            String formattedText = formatText(text);
            TextAnimator effetto = new TextAnimator(formattedText + "\n", 30);
            effetto.start();
            
            while (TextAnimator.isWriting()) {
                try {
                    DISPLAY_LOCK.wait(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    /**
     * Visualizza il testo immediatamente, senza effetti.
     * Usato per i comandi inseriti dall'utente.
     * 
     * @param text Il testo da visualizzare immediatamente
     */
    public static void displayTextImmediate(String text) {
        synchronized (DISPLAY_LOCK) {
            // Aspetta che eventuali effetti precedenti finiscano
            while (TextAnimator.isWriting()) {
                try {
                    DISPLAY_LOCK.wait(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            GamePanel.displayTextPaneAppendText(text + "\n");
        }
    }

    /**
     * Aggiunge il testo formattato alla GUI senza cancellare il testo esistente.
     * @param character
     */
    public static void appendChar(String character) {
        GamePanel.displayTextPaneAppendText(character);
    }

    /**
     * Aggiunge una nuova linea alla GUI.
     */
    public static void appendNewLine() {
        GamePanel.displayTextPaneAppendText("\n");
    }

    /**
     * Formatta il testo applicando word wrapping intelligente.
     * Divide il testo per parole, non per caratteri singoli.
     * 
     * @param text Il testo da formattare
     * @return Il testo formattato con a capo appropriati
     */
    private static String formatText(String text) {
        FontMetrics fontMetrics = GamePanel.getTextPaneFontMetrics();
        int maxWidth = GamePanel.getTextPaneWidth() - MARGIN;
        
        if (fontMetrics == null || maxWidth <= 0) {
            return text; 
        }
        
        StringBuilder result = new StringBuilder();
        String[] paragraphs = text.split("\n");
        
        for (int p = 0; p < paragraphs.length; p++) {
            String paragraph = paragraphs[p];
            
            if (paragraph.trim().isEmpty()) {
                result.append("\n");
                continue;
            }
            
            String[] words = paragraph.split("\\s+");
            StringBuilder currentLine = new StringBuilder();
            
            for (String word : words) {
                String testLine = currentLine.length() == 0 
                    ? word 
                    : currentLine + " " + word;
                
                int lineWidth = fontMetrics.stringWidth(testLine);
                
                if (lineWidth > maxWidth && currentLine.length() > 0) {
                    result.append(currentLine).append("\n");
                    currentLine = new StringBuilder(word);
                } else {
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                }
            }
            if (currentLine.length() > 0) {
                result.append(currentLine);
            }
            
            if (p < paragraphs.length - 1) {
                result.append("\n");
            }
        }
        
        return result.toString();
    }
}