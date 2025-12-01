/*
 * MapDialog.java - Finestra della mappa interattiva
 */
package it.uniba.lacasadicenere.view;

import it.uniba.lacasadicenere.model.Game;
import it.uniba.lacasadicenere.model.Room;
import it.uniba.lacasadicenere.model.RoomConnection;
import it.uniba.lacasadicenere.type.CommandType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Finestra separata che mostra la mappa interattiva del gioco.
 */
public class MapDialog extends JFrame {

    private static MapDialog instance;
    private MapPanel mapPanel;

    /**
     * Costruttore privato per il pattern singleton.
     */
    private MapDialog() {
        initComponents();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Restituisce l'istanza singleton di MapDialog.
     */
    public static MapDialog getInstance() {
        if (instance == null) {
            instance = new MapDialog();
        }
        return instance;
    }

    /**
     * Aggiorna la mappa quando il giocatore si muove.
     */
    public void updateMap() {
        if (mapPanel != null) {
            mapPanel.updateMap();
        }
    }

    /**
     * Inizializza i componenti grafici della finestra.
     */
    private void initComponents() {
        final Color FOG_BACKGROUND = new Color(30, 30, 35);

        setTitle("🗺️ Mappa - La Casa di Cenere");
        setPreferredSize(new Dimension(700, 650));
        setMinimumSize(new Dimension(700, 650));
        setResizable(false);
        getContentPane().setBackground(FOG_BACKGROUND);

        mapPanel = new MapPanel(Game.getInstance());

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(mapPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(mapPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    /**
     * Pannello interno che disegna la mappa.
     */
    private class MapPanel extends JPanel {

        private Game game;
        private Map<String, Point> roomPositions;
        private Map<String, Dimension> roomSizes;
        private Map<String, Rectangle> roomBounds;

        private String hoveredRoom = null;

        // Colori tema
        private static final Color BG_COLOR = new Color(30, 30, 35);
        private static final Color COLD_LIGHT = new Color(200, 220, 255); // Aggiunto per consistenza
        private static final Color ROOM_COLOR = new Color(60, 70, 80);
        private static final Color CURRENT_ROOM_COLOR = new Color(100, 150, 200);
        private static final Color LOCKED_CORRIDOR_COLOR = new Color(150, 50, 50);
        private static final Color UNLOCKED_CORRIDOR_COLOR = new Color(80, 120, 80);
        private static final Color TEXT_COLOR = COLD_LIGHT; // Usato COLD_LIGHT
        private static final Color VISITED_ROOM_COLOR = new Color(80, 90, 100);
        private static final Color HOVER_COLOR = new Color(120, 170, 220);

        private java.util.Set<String> visitedRooms = new java.util.HashSet<>();

        public MapPanel(Game game) {
            this.game = game;
            this.roomPositions = new HashMap<>();
            this.roomSizes = new HashMap<>();
            this.roomBounds = new HashMap<>();

            setPreferredSize(new Dimension(700, 650));
            setBackground(BG_COLOR);

            initializeRoomPositions();
            setupMouseListeners();

            if (game.getCurrentRoom() != null) {
                visitedRooms.add(game.getCurrentRoom().getName());
            }
        }

        /**
         * Configura i listener del mouse per l'interattività
         */
        private void setupMouseListeners() {
            setToolTipText("");

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    String previousHovered = hoveredRoom;
                    hoveredRoom = null;

                    for (Map.Entry<String, Rectangle> entry : roomBounds.entrySet()) {
                        if (entry.getValue().contains(e.getPoint())) {
                            hoveredRoom = entry.getKey();
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            break;
                        }
                    }

                    if (hoveredRoom == null) {
                        setCursor(Cursor.getDefaultCursor());
                    }

                    if ((previousHovered == null && hoveredRoom != null) ||
                        (previousHovered != null && !previousHovered.equals(hoveredRoom))) {
                        repaint();
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleRoomClick(e.getPoint());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (hoveredRoom != null) {
                        hoveredRoom = null;
                        setCursor(Cursor.getDefaultCursor());
                        repaint();
                    }
                }
            });
        }

        /**
         * Gestisce il click su una stanza
         */
        private void handleRoomClick(Point clickPoint) {
            for (Map.Entry<String, Rectangle> entry : roomBounds.entrySet()) {
                if (entry.getValue().contains(clickPoint)) {
                    String roomName = entry.getKey();
                    Room clickedRoom = findRoomByName(roomName);

                    if (clickedRoom != null) {
                        showRoomInfo(clickedRoom);
                    }
                    break;
                }
            }
        }

        /**
         * Trova una stanza per nome
         */
        private Room findRoomByName(String roomName) {
            if (game.getCorridorMap() == null) return null;

            for (RoomConnection c : game.getCorridorMap()) {
                if (c.getStartingRoom().getName().equals(roomName)) {
                    return c.getStartingRoom();
                }
                if (c.getArrivingRoom().getName().equals(roomName)) {
                    return c.getArrivingRoom();
                }
            }
            return null;
        }

        /**
         * Mostra informazioni sulla stanza
         */
        private void showRoomInfo(Room room) {
            StringBuilder info = new StringBuilder();
            info.append("📍 ").append(room.getName()).append("\n\n");

            if (room.equals(game.getCurrentRoom())) {
                info.append("🔵 Sei qui!\n\n");
            } else if (visitedRooms.contains(room.getName())) {
                info.append("✓ Stanza visitata\n\n");
            } else {
                info.append("❓ Stanza non ancora esplorata\n\n");
            }

            if (room.getDescription() != null && !room.getDescription().isEmpty()) {
                info.append("Descrizione:\n");
                String shortDesc = room.getDescription();
                if (shortDesc.length() > 200) {
                    shortDesc = shortDesc.substring(0, 197) + "...";
                }
                info.append(shortDesc).append("\n\n");
            }

            if (room.getItems() != null && !room.getItems().isEmpty()) {
                info.append("🎒 Oggetti presenti: ").append(room.getItems().size()).append("\n");
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
            textArea.setForeground(TEXT_COLOR);
            textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JOptionPane.showMessageDialog(
                this,
                textArea,
                "Informazioni Stanza",
                JOptionPane.INFORMATION_MESSAGE
            );
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            for (Map.Entry<String, Rectangle> entry : roomBounds.entrySet()) {
                if (entry.getValue().contains(e.getPoint())) {
                    Room room = findRoomByName(entry.getKey());
                    if (room != null) {
                        if (room.equals(game.getCurrentRoom())) {
                            return "🔵 Posizione attuale - Click per dettagli";
                        } else if (visitedRooms.contains(room.getName())) {
                            return "✓ Stanza visitata - Click per dettagli";
                        } else {
                            return "❓ Stanza non esplorata - Click per dettagli";
                        }
                    }
                }
            }
            return null;
        }

        /**
         * Definisce le posizioni delle stanze
         */
        private void initializeRoomPositions() {
            int startX = 120;
            int startY = 120;
            int spacingX = 180;
            int spacingY = 140;

            roomPositions.put("Stanza1", new Point(startX, startY));
            roomPositions.put("Stanza2", new Point(startX, startY + spacingY));
            roomPositions.put("Stanza3", new Point(startX + spacingX, startY + spacingY));
            roomPositions.put("Stanza4", new Point(startX + spacingX * 2, startY + spacingY));
            roomPositions.put("Stanza5", new Point(startX + spacingX * 2, startY));

            Dimension defaultSize = new Dimension(110, 75);
            roomPositions.keySet().forEach(room -> {
                roomSizes.put(room, defaultSize);
                Point pos = roomPositions.get(room);
                roomBounds.put(room, new Rectangle(pos.x, pos.y, defaultSize.width, defaultSize.height));
            });
        }

        /**
         * Aggiorna la mappa
         */
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

            drawCorridors(g2);
            drawRooms(g2);
            drawTitle(g2); // La legenda è stata rimossa
        }

        private void drawCorridors(Graphics2D g2) {
            if (game.getCorridorMap() == null) return;

            for (RoomConnection corridor : game.getCorridorMap()) {
                String startName = corridor.getStartingRoom().getName();
                String endName = corridor.getArrivingRoom().getName();

                Point p1 = roomPositions.get(startName);
                Point p2 = roomPositions.get(endName);

                if (p1 == null || p2 == null) continue;

                Dimension d1 = roomSizes.get(startName);
                Dimension d2 = roomSizes.get(endName);

                int x1 = p1.x + d1.width / 2;
                int y1 = p1.y + d1.height / 2;
                int x2 = p2.x + d2.width / 2;
                int y2 = p2.y + d2.height / 2;

                if (corridor.isLocked()) {
                    g2.setColor(LOCKED_CORRIDOR_COLOR);
                    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        0, new float[]{8, 8}, 0));
                } else {
                    g2.setColor(UNLOCKED_CORRIDOR_COLOR);
                    g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                }

                g2.draw(new Line2D.Double(x1, y1, x2, y2));
                drawArrow(g2, x1, y1, x2, y2, corridor.getDirection());
            }
        }

        private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2, CommandType direction) {
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();

            String dirText = getDirectionSymbol(direction);
            int textWidth = fm.stringWidth(dirText);

            g2.setColor(BG_COLOR);
            g2.fillOval(midX - textWidth/2 - 5, midY - 9, textWidth + 10, 18);

            g2.setColor(TEXT_COLOR);
            g2.drawString(dirText, midX - textWidth/2, midY + 4);
        }

        private String getDirectionSymbol(CommandType direction) {
            return switch (direction) {
                case NORD -> "↑ N";
                case SUD -> "↓ S";
                case EST -> "→ E";
                case OVEST -> "← O";
                default -> "?";
            };
        }

        private void drawRooms(Graphics2D g2) {
            if (game.getCorridorMap() == null) return;

            java.util.Set<Room> allRooms = new java.util.HashSet<>();
            for (RoomConnection c : game.getCorridorMap()) {
                allRooms.add(c.getStartingRoom());
                allRooms.add(c.getArrivingRoom());
            }

            for (Room room : allRooms) {
                String roomName = room.getName();
                Point pos = roomPositions.get(roomName);
                Dimension size = roomSizes.get(roomName);

                if (pos == null || size == null) continue;

                boolean isCurrent = room.equals(game.getCurrentRoom());
                boolean isVisited = visitedRooms.contains(roomName);
                boolean isHovered = roomName.equals(hoveredRoom);

                Color roomColor;
                if (isCurrent) {
                    roomColor = CURRENT_ROOM_COLOR;
                } else if (isHovered) {
                    roomColor = HOVER_COLOR;
                } else if (isVisited) {
                    roomColor = VISITED_ROOM_COLOR;
                } else {
                    roomColor = ROOM_COLOR;
                }

                if (isHovered && !isCurrent) {
                    g2.setColor(new Color(120, 170, 220, 50));
                    RoundRectangle2D glowRect = new RoundRectangle2D.Double(
                        pos.x - 4, pos.y - 4, size.width + 8, size.height + 8, 22, 22
                    );
                    g2.fill(glowRect);
                }

                RoundRectangle2D rect = new RoundRectangle2D.Double(
                    pos.x, pos.y, size.width, size.height, 18, 18
                );

                g2.setColor(roomColor);
                g2.fill(rect);

                g2.setColor(TEXT_COLOR);
                g2.setStroke(new BasicStroke(isCurrent ? 3 : (isHovered ? 2.5f : 2)));
                g2.draw(rect);

                g2.setFont(new Font("Monospaced", Font.BOLD, 14)); // Font leggermente più grande per coerenza
                FontMetrics fm = g2.getFontMetrics();
                String displayName = roomName.replace("Stanza", "S");
                int textWidth = fm.stringWidth(displayName);
                int textHeight = fm.getHeight();

                g2.setColor(TEXT_COLOR);
                g2.drawString(displayName,
                    pos.x + (size.width - textWidth) / 2,
                    pos.y + (size.height + textHeight) / 2 - 4);

                if (isCurrent) {
                    g2.setFont(new Font("Monospaced", Font.BOLD, 10));
                    String marker = "● TU SEI QUI";
                    int markerWidth = g2.getFontMetrics().stringWidth(marker);
                    g2.setColor(new Color(255, 200, 100));
                    g2.drawString(marker,
                        pos.x + (size.width - markerWidth) / 2,
                        pos.y + size.height + 18);
                }

                if (room.getItems() != null && !room.getItems().isEmpty()) {
                    int itemCount = room.getItems().size();
                    g2.setFont(new Font("Monospaced", Font.BOLD, 11)); // Font leggermente più grande e bold
                    g2.setColor(new Color(180, 180, 180));
                    g2.drawString("🎒 " + itemCount, pos.x + 5, pos.y + size.height - 5);
                }
            }
        }

        private void drawTitle(Graphics2D g2) {
            // Semplificazione del titolo e uso del font Monospaced (coerente con GamePanel)
            g2.setFont(new Font("Monospaced", Font.BOLD, 22));
            g2.setColor(TEXT_COLOR);
            String title = "🗺️ MAPPA INTERATTIVA";
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(title)) / 2;
            g2.drawString(title, x, 40); // Spostato più in alto

            // DIMENSIONE FONT AUMENTATA QUI (da 11 a 14)
            g2.setFont(new Font("Monospaced", Font.ITALIC, 14));
            g2.setColor(new Color(150, 170, 200));
            String subtitle = "(Click sulle stanze per i dettagli)";
            int subX = (getWidth() - g2.getFontMetrics().stringWidth(subtitle)) / 2;
            g2.drawString(subtitle, subX, 60); // Spostato più in alto
        }

        // Metodo drawLegend rimosso
    }
}