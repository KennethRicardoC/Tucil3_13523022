package view;

import java.util.*;

public class Board {
    private char[][] grid;
    private Map<Character, Piece> pieceMap;
    private int exitRow, exitCol;

    public Board(char[][] initialGrid, Map<Character, Piece> pieces, int exitRow, int exitCol) {
        this.grid = initialGrid;
        this.pieceMap = (pieces != null) ? new HashMap<>(pieces) : new HashMap<>();
        this.exitRow = exitRow;
        this.exitCol = exitCol;
    }

    public boolean isGoal() { 
        Piece main = pieceMap.get('P');
        if (main == null) return false;
        for (int offset = 0; offset < main.length; offset++) {
            int currX = main.x + (main.isHorizontal ? offset : 0);
            int currY = main.y + (main.isHorizontal ? 0 : offset);
            if (currX == exitRow && currY == exitCol) return true;
        }
        return false;
    }

    public String getBoardKey() { 
        StringBuilder keyBuilder = new StringBuilder();
        List<Character> ids = new ArrayList<>(pieceMap.keySet());
        Collections.sort(ids);
        for (char ch : ids) {
            Piece p = pieceMap.get(ch);
            keyBuilder.append(ch).append(p.x).append(',').append(p.y).append(';');
        }
        return keyBuilder.toString();
    }

    public Board createCopy() {
        int height = grid.length;
        int width = grid[0].length;
        char[][] copiedGrid = new char[height][width];

        for (int r = 0; r < height; r++) {
            copiedGrid[r] = Arrays.copyOf(grid[r], width);
        }

        Map<Character, Piece> copiedPieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : pieceMap.entrySet()) {
            Piece original = entry.getValue();
            copiedPieces.put(entry.getKey(),
                new Piece(original.id, original.x, original.y, original.length, original.isHorizontal, original.isPrimary));
        }

        return new Board(copiedGrid, copiedPieces, exitRow, exitCol);
    }

    public void shiftPiece(char pieceId, int moveAmount) {
        Piece target = pieceMap.get(pieceId);
        if (target == null) return;

        int proposedX = target.x + (target.isHorizontal ? moveAmount : 0);
        int proposedY = target.y + (target.isHorizontal ? 0 : moveAmount);

        if (proposedX < 0 || proposedY < 0 ||
            (target.isHorizontal && proposedX + target.length > grid[0].length) ||
            (!target.isHorizontal && proposedY + target.length > grid.length)) {
            return;
        }
        for (int i = 0; i < target.length; i++) {
            int clearX = target.x + (target.isHorizontal ? i : 0);
            int clearY = target.y + (target.isHorizontal ? 0 : i);
            grid[clearY][clearX] = '.';
        }

        target.x = proposedX;
        target.y = proposedY;
        for (int i = 0; i < target.length; i++) {
            int fillX = target.x + (target.isHorizontal ? i : 0);
            int fillY = target.y + (target.isHorizontal ? 0 : i);
            grid[fillY][fillX] = target.id;
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    public Map<Character, Piece> getPieceMap() {
        return pieceMap;
    }

    public int getExitRow() {
        return exitRow;
    }

    public int getExitCol() {
        return exitCol;
    }
}
