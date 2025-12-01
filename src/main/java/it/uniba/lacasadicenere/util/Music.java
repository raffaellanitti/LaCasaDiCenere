/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.uniba.lacasadicenere.util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Gestisce la riproduzione della musica di sottofondo del gioco.
 * Usa il pattern Singleton per garantire una sola istanza.
 */
public class Music {
    private static Music instance;
    private Clip clip;
    private boolean isPlaying = false;

    private static final String MUSIC_FILE_PATH = "/audio/music.wav";

    private Music() {
        loadMusic();
    }

    private void loadMusic() {
        try {
    
            InputStream audioSrc = getClass().getResourceAsStream(MUSIC_FILE_PATH);
            
            if(audioSrc == null) {
                System.err.println("File audio non trovato: " + MUSIC_FILE_PATH);
                return;
            }

            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Formato audio non supportato!");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Linea audio non disponibile!");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Errore generico nel caricamento:");
            e.printStackTrace();
        }
    }

    /**
     * Restituisce l'istanza singleton di Music.
     * @return istanza di Music
     */
    public static Music getInstance() {
        if(instance == null) {
            instance = new Music();
        }
        return instance;
    }

    /**
     * Avvia la riproduzione della musica in loop.
     */
    public void startMusic() {
        if(clip == null) {
            return;
        }
        
        if(isPlaying) {
            return;
        }
        
        try {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            isPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ferma la riproduzione della musica.
     */
    public void stopMusic() {
        if(clip != null && isPlaying) {
            clip.stop();
            clip.setFramePosition(0);
            isPlaying = false;
        }
    }
    
    /**
     * Verifica se la musica è in riproduzione.
     * @return true se la musica è in riproduzione, false altrimenti
     */
    public boolean isPlaying() {
        return isPlaying;
    }
}