/*
 * MapDialog.java - Finestra della mappa interattiva (versione ultra-semplificata)
 */
package it.uniba.lacasadicenere.view;

import it.uniba.lacasadicenere.model.Game;
import it.uniba.lacasadicenere.model.Room;
import it.uniba.lacasadicenere.model.RoomConnection;
import it.uniba.lacasadicenere.type.CommandType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Finestra separata che mostra la mappa interattiva del gioco.
 */
public class MapDialog extends JFrame {

    private static MapDialog instance;

    private MapPanel mapPanel;

    private MapDialog() {
        setTitle("Mappa - La Casa di Cenere");
        setPreferredSize(new Dimension(700, 650));
        setMinimumSize(new Dimension(700, 650));
        setResizable(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        getContentPane().setBackground(new Color(30, 30, 35));

        mapPanel = new MapPanel(Game.getInstance());
        add(mapPanel);
        pack();
    }

    /**
     * Restituisce l'istanza singleton della mappa.
     * @return l'istanza di MapDialog.
     */
    public static MapDialog getInstance() {
        if (instance == null) {
            instance = new MapDialog();
        }
        return instance;
    }

    /**
     * Aggiorna la mappa in base allo stato attuale del gioco.
     */
    public void updateMap() {
        if (mapPanel != null) {
            mapPanel.updateMap();
        }
    }

    /**
     * Pannello interno che disegna la mappa.
     */
    private class MapPanel extends JPanel {

        private Game game;
        private Map<String, Point> roomPositions;
        private Set<String> visitedRooms;
        private String hoveredRoom;

        // Colori
        private static final Color BG = new Color(30, 30, 35);
        private static final Color LIGHT = new Color(200, 220, 255);
        private static final Color ROOM_NORMAL = new Color(60, 70, 80);
        private static final Color ROOM_CURRENT = new Color(100, 150, 200);
        private static final Color ROOM_VISITED = new Color(80, 90, 100);
        private static final Color ROOM_HOVER = new Color(120, 170, 220);
        private static final Color CORRIDOR_LOCKED = new Color(150, 50, 50);
        private static final Color CORRIDOR_OPEN = new Color(80, 120, 80);

        // Dimensioni
        private static final int ROOM_W = 110;
        private static final int ROOM_H = 75;

        public MapPanel(Game game) {
            this.game = game;
            this.roomPositions = new HashMap<>();
            this.visitedRooms = new HashSet<>();
            this.hoveredRoom = null;

            setPreferredSize(new Dimension(700, 650));
            setBackground(BG);
            setToolTipText("");

            initRoomPositions();
            setupMouseListener();

            if (game.getCurrentRoom() != null) {
                visitedRooms.add(game.getCurrentRoom().getName());
            }
        }

        private void initRoomPositions() {
            roomPositions.put("Stanza1", new Point(120, 200));
            roomPositions.put("Stanza2", new Point(120, 340));
            roomPositions.put("Stanza3", new Point(300, 340));
            roomPositions.put("Stanza4", new Point(480, 340));
            roomPositions.put("Stanza5", new Point(480, 200));
        }

        private void setupMouseListener() {
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    String prev = hoveredRoom;
                    hoveredRoom = null;

                    for (Map.Entry<String, Point> entry : roomPositions.entrySet()) {
                        Point p = entry.getValue();
                        if (e.getX() >= p.x && e.getX() <= p.x + ROOM_W &&
                            e.getY() >= p.y && e.getY() <= p.y + ROOM_H) {
                            hoveredRoom = entry.getKey();
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            break;
                        }
                    }

                    if (hoveredRoom == null) {
                        setCursor(Cursor.getDefaultCursor());
                    }

                    if ((prev == null && hoveredRoom != null) || 
                        (prev != null && !prev.equals(hoveredRoom))) {
                        repaint();
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (hoveredRoom != null) {
                        showRoomInfo(hoveredRoom);
                    }
                }
            });
        }

        private void showRoomInfo(String roomName) {
            Room room = findRoom(roomName);
            if (room == null) return;

            // Stanza non visitata
            if (!visitedRooms.contains(roomName)) {
                JOptionPane.showMessageDialog(this, 
                    "Stanza non ancora esplorata.", 
                    "Stanza Inesplorata", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Stanza visitata
            StringBuilder info = new StringBuilder();
            info.append(roomName).append("\n\n");

            if (room.equals(game.getCurrentRoom())) {
                info.append("⨀ Sei qui!\n\n");
            } else {
                info.append("Stanza visitata.\n\n");
            }

            if (room.getItems() != null && !room.getItems().isEmpty()) {
                info.append("Oggetti: ").append(room.getItems().size()).append("\n");
                for (var item : room.getItems()) {
                    info.append("  • ").append(item.getName()).append("\n");
                }
            } else {
                info.append("Nessun oggetto visibile\n");
            }

            JTextArea textArea = new JTextArea(info.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            textArea.setBackground(new Color(45, 50, 55));
            textArea.setForeground(LIGHT);
            textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JOptionPane.showMessageDialog(this, textArea, "Info Stanza", JOptionPane.INFORMATION_MESSAGE);
        }

        private Room findRoom(String name) {
            if (game.getCorridorMap() == null) return null;
            for (RoomConnection c : game.getCorridorMap()) {
                if (c.getStartingRoom().getName().equals(name)) {
                    return c.getStartingRoom();
                }
                if (c.getArrivingRoom().getName().equals(name)) {
                    return c.getArrivingRoom();
                }
            }
            return null;
        }

        public void updateMap() {
            if (game.getCurrentRoom() != null) {
                visitedRooms.add(game.getCurrentRoom().getName());
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawTitle(g2);
            drawCorridors(g2);
            drawRooms(g2);
        }

        private void drawTitle(Graphics2D g2) {
            g2.setFont(new Font("Monospaced", Font.BOLD, 32));
            g2.setColor(LIGHT);
            String title = "MAPPA - La Casa di Cenere";
            int x = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, (getWidth() - x)/2, 60);

            g2.setFont(new Font("Monospaced", Font.ITALIC, 16));
            g2.setColor(new Color(150, 150, 160));
            String sub = "(Click sulle stanze per avere suggerimenti)";
            int y = g2.getFontMetrics().stringWidth(sub);
            g2.drawString(sub, (getWidth() - y) / 2, 95);
        }

        private void drawCorridors(Graphics2D g2) {
            if (game.getCorridorMap() == null) return;

            Set<String> drawn = new HashSet<>();

            for (RoomConnection c : game.getCorridorMap()) {
                String s1 = c.getStartingRoom().getName();
                String s2 = c.getArrivingRoom().getName();
                String key = s1.compareTo(s2) < 0 ? s1 + "-" + s2 : s2 + "-" + s1;

                if (drawn.contains(key)) continue;
                drawn.add(key);

                Point p1 = roomPositions.get(s1);
                Point p2 = roomPositions.get(s2);
                if (p1 == null || p2 == null) continue;

                int x1 = p1.x + ROOM_W / 2;
                int y1 = p1.y + ROOM_H / 2;
                int x2 = p2.x + ROOM_W / 2;
                int y2 = p2.y + ROOM_H / 2;

                // Verifica se entrambe le direzioni sono aperte
                RoomConnection reverse = findReverse(c);
                boolean open = !c.isLocked() && (reverse == null || !reverse.isLocked());

                g2.setColor(open ? CORRIDOR_OPEN : CORRIDOR_LOCKED);
                g2.setStroke(new BasicStroke(open ? 4 : 3));
                g2.drawLine(x1, y1, x2, y2);

                // Etichetta direzioni
                drawDirectionLabel(g2, x1, y1, x2, y2, c.getDirection(), 
                    reverse != null ? reverse.getDirection() : null);
            }
        }

        private RoomConnection findReverse(RoomConnection c) {
            if (game.getCorridorMap() == null) return null;
            for (RoomConnection rc : game.getCorridorMap()) {
                if (rc.getStartingRoom().getName().equals(c.getArrivingRoom().getName()) &&
                    rc.getArrivingRoom().getName().equals(c.getStartingRoom().getName())) {
                    return rc;
                }
            }
            return null;
        }

        private void drawDirectionLabel(Graphics2D g2, int x1, int y1, int x2, int y2, 
                                        CommandType dir1, CommandType dir2) {
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            String text1 = getDirText(dir1);
            String text2 = dir2 != null ? getDirText(dir2) : null;

            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();

            int w = Math.max(fm.stringWidth(text1), text2 != null ? fm.stringWidth(text2) : 0);
            int h = text2 != null ? fm.getHeight() * 2 : fm.getHeight();

            // Background
            g2.setColor(new Color(30, 30, 35, 240));
            g2.fillRoundRect(midX - w/2 - 6, midY - h/2 - 2, w + 12, h + 4, 8, 8);

            g2.setColor(LIGHT);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(midX - w/2 - 6, midY - h/2 - 2, w + 12, h + 4, 8, 8);

            // Testo
            g2.setColor(Color.WHITE);
            if (text2 == null) {
                g2.drawString(text1, midX - fm.stringWidth(text1)/2, midY + fm.getAscent()/2);
            } else {
                g2.drawString(text1, midX - fm.stringWidth(text1)/2, midY - fm.getHeight()/4);
                g2.drawString(text2, midX - fm.stringWidth(text2)/2, midY + fm.getHeight() - fm.getHeight()/4);
            }
        }

        private String getDirText(CommandType dir) {
            return switch (dir) {
                case NORD -> "↑ N";
                case SUD -> "↓ S";
                case EST -> "→ E";
                case OVEST -> "← O";
                default -> "?";
            };
        }

        private void drawRooms(Graphics2D g2) {
            if (game.getCorridorMap() == null) return;

            Set<Room> allRooms = new HashSet<>();
            for (RoomConnection c : game.getCorridorMap()) {
                allRooms.add(c.getStartingRoom());
                allRooms.add(c.getArrivingRoom());
            }

            for (Room room : allRooms) {
                String name = room.getName();
                Point pos = roomPositions.get(name);
                if (pos == null) continue;

                boolean isCurrent = room.equals(game.getCurrentRoom());
                boolean isVisited = visitedRooms.contains(name);
                boolean isHover = name.equals(hoveredRoom);

                // Colore stanza
                Color color;
                if (isCurrent) color = ROOM_CURRENT;
                else if (isHover) color = ROOM_HOVER;
                else if (isVisited) color = ROOM_VISITED;
                else color = ROOM_NORMAL;

                // Disegna stanza
                g2.setColor(color);
                g2.fillRoundRect(pos.x, pos.y, ROOM_W, ROOM_H, 15, 15);

                g2.setColor(LIGHT);
                g2.setStroke(new BasicStroke(isCurrent ? 3 : 2));
                g2.drawRoundRect(pos.x, pos.y, ROOM_W, ROOM_H, 15, 15);

                // Marker "TU SEI QUI"
                if (isCurrent) {
                    g2.setFont(new Font("Monospaced", Font.BOLD, 10));
                    String marker = "● SEI QUI";
                    int mw = g2.getFontMetrics().stringWidth(marker);
                    g2.setColor(new Color(255, 200, 100));
                    g2.drawString(marker, pos.x + (ROOM_W - mw) / 2, pos.y + ROOM_H / 2 - 8);
                }

                // Nome stanza
                g2.setFont(new Font("Monospaced", Font.BOLD, 14));
                g2.setColor(LIGHT);
                String display = name.replace("Stanza", "S");
                int tw = g2.getFontMetrics().stringWidth(display);
                g2.drawString(display, pos.x + (ROOM_W - tw) / 2, pos.y + ROOM_H / 2 + 10);
            }
        }
    }
}