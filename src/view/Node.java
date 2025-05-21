package view;

import java.util.*;

public class Node {
    public Board board;
    public Node parent; 
    public String moveDesc; 
    public int g; 
    public int h; 

    public Node(Board board, Node parent, String moveDesc, int g, int h) {
        this.board = board;
        this.parent = parent;
        this.moveDesc = moveDesc;
        this.g = g;
        this.h = h;
    }

    public static List<Node> reconstructPath(Node goal) { 
        LinkedList<Node> path = new LinkedList<>();
        Node curr = goal;
        while (curr != null) {
            path.addFirst(curr);
            curr = curr.parent;
        }
        return path;
    }

    public static List<Node> generateNextNodes(Node current) {
        List<Node> successors = new ArrayList<>();
        Board board = current.board;

        for (Map.Entry<Character, Piece> entry : board.getPieceMap().entrySet()) {
            char pieceId = entry.getKey();
            Piece p = entry.getValue();

            if (p.isHorizontal) {
                for (int offset = 1; p.x - offset >= 0; offset++) {
                    int posX = p.x - offset;
                    int posY = p.y;
                    if (board.getGrid()[posY][posX] != '.' && !(posX == board.getExitRow() && posY == board.getExitCol()))
                        break;

                    Board copied = board.createCopy();
                    copied.shiftPiece(pieceId, -offset);
                    String move = "Geser " + pieceId + " ke kiri " + offset;
                    successors.add(new Node(copied, current, move, current.g + 1, 0));
                }

                for (int offset = 1; p.x + p.length - 1 + offset < board.getGrid()[0].length; offset++) {
                    int posX = p.x + p.length - 1 + offset;
                    int posY = p.y;
                    if (board.getGrid()[posY][posX] != '.' && !(posX == board.getExitRow() && posY == board.getExitCol()))
                        break;

                    Board copied = board.createCopy();
                    copied.shiftPiece(pieceId, offset);
                    String move = "Geser " + pieceId + " ke kanan " + offset;
                    successors.add(new Node(copied, current, move, current.g + 1, 0));
                }
            } else {
                for (int offset = 1; p.y - offset >= 0; offset++) {
                    int posX = p.x;
                    int posY = p.y - offset;
                    if (board.getGrid()[posY][posX] != '.' && !(posX == board.getExitRow() && posY == board.getExitCol()))
                        break;

                    Board copied = board.createCopy();
                    copied.shiftPiece(pieceId, -offset);
                    String move = "Geser " + pieceId + " ke atas " + offset;
                    successors.add(new Node(copied, current, move, current.g + 1, 0));
                }

                for (int offset = 1; p.y + p.length - 1 + offset < board.getGrid().length; offset++) {
                    int posX = p.x;
                    int posY = p.y + p.length - 1 + offset;
                    if (board.getGrid()[posY][posX] != '.' && !(posX == board.getExitRow() && posY == board.getExitCol()))
                        break;

                    Board copied = board.createCopy();
                    copied.shiftPiece(pieceId, offset);
                    String move = "Geser " + pieceId + " ke bawah " + offset;
                    successors.add(new Node(copied, current, move, current.g + 1, 0));
                }
            }
        }

        return successors;
    }
}
