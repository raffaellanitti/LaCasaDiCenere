/*
 * MapDialog.java - Finestra della mappa interattiva (versione semplificata)
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
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
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
        initComponents();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public static MapDialog getInstance() {
        if (instance == null) {
            instance = new MapDialog();
        }
        return instance;
    }

    public void updateMap() {
        if (mapPanel != null) {
            mapPanel.updateMap();
        }
    }

    private void initComponents() {
        final Color FOG_BACKGROUND = new Color(30, 30, 35);

        setTitle("Mappa - La Casa di Cenere");
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
     * Pannello interno che disegna la mappa (versione semplificata).
     */
    private class MapPanel extends JPanel {

        private Game game;
        private Map<String, Point> roomPositions;
        private Map<String, Rectangle> roomBounds;
        private String hoveredRoom = null;
        private Set<String> visitedRooms = new HashSet<>();

        // Colori
        private static final Color BG_COLOR = new Color(30, 30, 35);
        private static final Color COLD_LIGHT = new Color(200, 220, 255);
        private static final Color ROOM_COLOR = new Color(60, 70, 80);
        private static final Color CURRENT_ROOM_COLOR = new Color(100, 150, 200);
        private static final Color LOCKED_COLOR = new Color(150, 50, 50);
        private static final Color UNLOCKED_COLOR = new Color(80, 120, 80);
        private static final Color VISITED_ROOM_COLOR = new Color(80, 90, 100);
        private static final Color HOVER_COLOR = new Color(120, 170, 220);

        // Dimensioni stanze
        private static final int ROOM_WIDTH = 110;
        private static final int ROOM_HEIGHT = 75;

        public MapPanel(Game game) {
            this.game = game;
            this.roomPositions = new HashMap<>();
            this.roomBounds = new HashMap<>();

            setPreferredSize(new Dimension(700, 650));
            setBackground(BG_COLOR);

            initializeRoomPositions();
            setupMouseListeners();

            if (game.getCurrentRoom() != null) {
                visitedRooms.add(game.getCurrentRoom().getName());
            }
        }

        private void initializeRoomPositions() {

            roomPositions.put("Stanza1", new Point(120, 120));
            roomPositions.put("Stanza2", new Point(120, 260));
            roomPositions.put("Stanza3", new Point(300, 260));
            roomPositions.put("Stanza4", new Point(480, 260));
            roomPositions.put("Stanza5", new Point(480, 120));

            for (String roomName : roomPositions.keySet()) {
                Point pos = roomPositions.get(roomName);
                roomBounds.put(roomName, new Rectangle(pos.x, pos.y, ROOM_WIDTH, ROOM_HEIGHT));
            }
        }

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

        private void showRoomInfo(Room room) {
            if(!visitedRooms.contains(room.getName())) {
                JTextArea textArea = new JTextArea(" Stanza non ancora esplorata.");
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
                textArea.setBackground(new Color(45, 50, 55));
                textArea.setForeground(COLD_LIGHT);
                textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

                JOptionPane.showMessageDialog(
                    this,
                    textArea,
                    "Stanza Inesplorata",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            StringBuilder info = new StringBuilder();
            info.append(room.getName()).append("\n\n");

            if (room.equals(game.getCurrentRoom())) {
                info.append("⨀ Sei qui!\n\n");
            } else {
                info.append("Stanza visitata.\n\n");
            }

            if (room.getItems() != null && !room.getItems().isEmpty()) {
                info.append("Oggetti presenti: ").append(room.getItems().size()).append("\n");
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
            textArea.setForeground(COLD_LIGHT);
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
                            return "⨀ Posizione attuale - Click per dettagli";
                        } else if (visitedRooms.contains(room.getName())) {
                            return "Stanza visitata - Click per dettagli";
                        } else {
                            return "Stanza non esplorata - Click per dettagli";
                        }
                    }
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

            drawCorridors(g2);
            drawRooms(g2);
            drawTitle(g2);
        }

        /**
         * Disegna i corridoi con frecce bidirezionali.
         * Mostra solo un corridoio per coppia di stanze con entrambe le direzioni.
         */
        private void drawCorridors(Graphics2D g2) {
            if (game.getCorridorMap() == null) return;

            // Set per tracciare le coppie già disegnate
            Set<String> drawnPairs = new HashSet<>();

            for (RoomConnection corridor : game.getCorridorMap()) {
                String startName = corridor.getStartingRoom().getName();
                String endName = corridor.getArrivingRoom().getName();

                // Crea una chiave unica per la coppia (indipendente dall'ordine)
                String pairKey = startName.compareTo(endName) < 0 
                    ? startName + "-" + endName 
                    : endName + "-" + startName;

                // Salta se questa coppia è già stata disegnata
                if (drawnPairs.contains(pairKey)) {
                    continue;
                }
                drawnPairs.add(pairKey);

                Point p1 = roomPositions.get(startName);
                Point p2 = roomPositions.get(endName);

                if (p1 == null || p2 == null) continue;

                int x1 = p1.x + ROOM_WIDTH / 2;
                int y1 = p1.y + ROOM_HEIGHT / 2;
                int x2 = p2.x + ROOM_WIDTH / 2;
                int y2 = p2.y + ROOM_HEIGHT / 2;

                // Trova il corridoio di ritorno per verificare lo stato
                RoomConnection returnCorridor = findReturnCorridor(corridor);
                
                // Il corridoio è aperto se ENTRAMBE le direzioni sono sbloccate
                boolean isOpen = !corridor.isLocked() && 
                                (returnCorridor == null || !returnCorridor.isLocked());

                if (isOpen) {
                    g2.setColor(UNLOCKED_COLOR);
                    g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                } else {
                    g2.setColor(LOCKED_COLOR);
                    g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        0, new float[]{8, 8}, 0));
                }

                // Disegna la linea
                g2.draw(new Line2D.Double(x1, y1, x2, y2));

                // Disegna frecce bidirezionali
                drawBidirectionalArrows(g2, x1, y1, x2, y2, corridor, returnCorridor);
            }
        }

        /**
         * Trova il corridoio di ritorno per una data connessione.
         */
        private RoomConnection findReturnCorridor(RoomConnection corridor) {
            if (game.getCorridorMap() == null) return null;

            for (RoomConnection c : game.getCorridorMap()) {
                if (c.getStartingRoom().getName().equals(corridor.getArrivingRoom().getName()) &&
                    c.getArrivingRoom().getName().equals(corridor.getStartingRoom().getName())) {
                    return c;
                }
            }
            return null;
        }

        /**
         * Disegna le frecce bidirezionali con le direzioni corrette.
         */
        private void drawBidirectionalArrows(Graphics2D g2, int x1, int y1, int x2, int y2, 
                                            RoomConnection forward, RoomConnection backward) {
            // Calcola il punto medio
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2;

            // Calcola l'angolo della linea
            double angle = Math.atan2(y2 - y1, x2 - x1);

            // Determina le direzioni
            String forwardDir = getDirectionSymbol(forward.getDirection());
            String backwardDir = backward != null ? getDirectionSymbol(backward.getDirection()) : "";

            // Disegna background per le frecce
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();

            String arrowText = forwardDir;
            if (!backwardDir.isEmpty()) {
                arrowText = forwardDir + " " + backwardDir;
            }

            int textWidth = fm.stringWidth(arrowText);
            int textHeight = fm.getHeight();

            // Background ovale
            g2.setColor(BG_COLOR);
            g2.fillOval(midX - textWidth/2 - 8, midY - textHeight/2, textWidth + 16, textHeight);

            // Testo delle frecce
            g2.setColor(COLD_LIGHT);
            g2.drawString(arrowText, midX - textWidth/2, midY + 4);

            // Disegna punte delle frecce ai lati
            drawArrowHead(g2, x1, y1, x2, y2, 0.3); // Freccia verso arrivo
            if (backward != null) {
                drawArrowHead(g2, x2, y2, x1, y1, 0.3); // Freccia verso partenza
            }
        }

        /**
         * Disegna la punta di una freccia.
         */
        private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2, double position) {
            // Calcola il punto sulla linea dove disegnare la freccia
            int arrowX = (int)(x1 + (x2 - x1) * position);
            int arrowY = (int)(y1 + (y2 - y1) * position);

            double angle = Math.atan2(y2 - y1, x2 - x1);
            int arrowSize = 10;

            Path2D.Double arrowHead = new Path2D.Double();
            arrowHead.moveTo(arrowX, arrowY);
            arrowHead.lineTo(
                arrowX - arrowSize * Math.cos(angle - Math.PI / 6),
                arrowY - arrowSize * Math.sin(angle - Math.PI / 6)
            );
            arrowHead.lineTo(
                arrowX - arrowSize * Math.cos(angle + Math.PI / 6),
                arrowY - arrowSize * Math.sin(angle + Math.PI / 6)
            );
            arrowHead.closePath();

            g2.fill(arrowHead);
        }

        private String getDirectionSymbol(CommandType direction) {
            return switch (direction) {
                case NORD -> "↑";
                case SUD -> "↓";
                case EST -> "→";
                case OVEST -> "←";
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
                String roomName = room.getName();
                Point pos = roomPositions.get(roomName);

                if (pos == null) continue;

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

                // Effetto glow per hover
                if (isHovered && !isCurrent) {
                    g2.setColor(new Color(120, 170, 220, 50));
                    RoundRectangle2D glowRect = new RoundRectangle2D.Double(
                        pos.x - 4, pos.y - 4, ROOM_WIDTH + 8, ROOM_HEIGHT + 8, 22, 22
                    );
                    g2.fill(glowRect);
                }

                // Stanza
                RoundRectangle2D rect = new RoundRectangle2D.Double(
                    pos.x, pos.y, ROOM_WIDTH, ROOM_HEIGHT, 18, 18
                );

                g2.setColor(roomColor);
                g2.fill(rect);

                g2.setColor(COLD_LIGHT);
                g2.setStroke(new BasicStroke(isCurrent ? 3 : (isHovered ? 2.5f : 2)));
                g2.draw(rect);

                // Nome stanza
                g2.setFont(new Font("Monospaced", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String displayName = roomName.replace("Stanza", "S");
                int textWidth = fm.stringWidth(displayName);
                int textHeight = fm.getHeight();

                g2.setColor(COLD_LIGHT);
                g2.drawString(displayName,
                    pos.x + (ROOM_WIDTH - textWidth) / 2,
                    pos.y + (ROOM_HEIGHT + textHeight) / 2 - 4);

                // Marker "TU SEI QUI"
                if (isCurrent) {
                    g2.setFont(new Font("Monospaced", Font.BOLD, 10));
                    String marker = "● TU SEI QUI";
                    int markerWidth = g2.getFontMetrics().stringWidth(marker);
                    g2.setColor(new Color(255, 200, 100));
                    g2.drawString(marker,
                        pos.x + (ROOM_WIDTH - markerWidth) / 2,
                        pos.y + ROOM_HEIGHT + 18);
                }

                // Numero oggetti
                if (room.getItems() != null && !room.getItems().isEmpty()) {
                    int itemCount = room.getItems().size();
                    g2.setFont(new Font("Monospaced", Font.BOLD, 11));
                    g2.setColor(new Color(180, 180, 180));
                    g2.drawString(" " + itemCount, pos.x + 5, pos.y + ROOM_HEIGHT - 5);
                }
            }
        }

        private void drawTitle(Graphics2D g2) {
            g2.setFont(new Font("Monospaced", Font.BOLD, 22));
            g2.setColor(COLD_LIGHT);
            String title = "MAPPA INTERATTIVA";
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(title)) / 2;
            g2.drawString(title, x, 40);

            g2.setFont(new Font("Monospaced", Font.ITALIC, 14));
            g2.setColor(new Color(150, 170, 200));
            String subtitle = "(Click sulle stanze per i dettagli)";
            int subX = (getWidth() - g2.getFontMetrics().stringWidth(subtitle)) / 2;
            g2.drawString(subtitle, subX, 60);
        }
    }
}