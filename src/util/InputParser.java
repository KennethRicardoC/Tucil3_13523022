package util;

import java.io.*;
import java.util.*;
import view.*;

public class InputParser {

    public static Board parseFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {

            // Baca dimensi board
            String header = reader.readLine();
            if (header == null) {
                System.out.println("File kosong. Tidak bisa memproses input.");
                return null;
            }

            String[] dimensions = header.trim().split("\\s+");
            int expectedRows = Integer.parseInt(dimensions[0]);
            int expectedCols = Integer.parseInt(dimensions[1]);

            // Baca jumlah piece
            String countLine = reader.readLine();
            int expectedPieceCount;
            try {
                expectedPieceCount = Integer.parseInt(countLine.trim());
            } catch (NumberFormatException e) {
                System.out.println("Jumlah piece harus berupa angka valid.");
                return null;
            }

            // Baca representasi papan
            List<String> boardLines = new ArrayList<>();
            int maxWidth = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.matches("[A-Za-z\\.K ]*")) {
                    System.out.println("Karakter tidak valid ditemukan dalam papan.");
                    return null;
                }
                boardLines.add(line);
                maxWidth = Math.max(maxWidth, line.length());
            }

            int exitCol = -1, exitRow = -1;
            for (int row = 0; row < boardLines.size(); row++) {
                String rowStr = boardLines.get(row);
                for (int col = 0; col < rowStr.length(); col++) {
                    if (rowStr.charAt(col) == 'K') {
                        exitCol = col;
                        exitRow = row;
                    }
                }
            }

            if (exitCol == -1 || exitRow == -1) {
                System.out.println("Tidak ditemukan posisi keluar (K) dalam papan.");
                return null;
            }

            int actualRows = Math.max(expectedRows, boardLines.size());
            int actualCols = Math.max(expectedCols, maxWidth);

            char[][] grid = new char[actualRows][actualCols];
            for (int i = 0; i < actualRows; i++) {
                Arrays.fill(grid[i], ' ');
            }

            for (int i = 0; i < boardLines.size(); i++) {
                String rowStr = boardLines.get(i);
                for (int j = 0; j < rowStr.length(); j++) {
                    grid[i][j] = rowStr.charAt(j);
                }
            }

            Map<Character, Piece> pieceMap = new HashMap<>();
            Set<Character> processed = new HashSet<>();

            for (int y = 0; y < actualRows; y++) {
                for (int x = 0; x < actualCols; x++) {
                    char symbol = grid[y][x];
                    if (Character.isUpperCase(symbol) && symbol != 'K' && !processed.contains(symbol)) {
                        processed.add(symbol);
                        boolean horizontal = (x + 1 < actualCols && grid[y][x + 1] == symbol);
                        int length = 1;

                        if (horizontal) {
                            int nextX = x + 1;
                            while (nextX < actualCols && grid[y][nextX] == symbol) {
                                length++;
                                nextX++;
                            }
                        } else {
                            int nextY = y + 1;
                            while (nextY < actualRows && grid[nextY][x] == symbol) {
                                length++;
                                nextY++;
                            }
                        }

                        boolean isMain = (symbol == 'P');
                        pieceMap.put(symbol, new Piece(symbol, x, y, length, horizontal, isMain));
                    }
                }
            }

            int otherPieceCount = 0;
            for (char key : pieceMap.keySet()) {
                if (key != 'P') otherPieceCount++;
            }

            if (otherPieceCount != expectedPieceCount) {
                System.out.println("Jumlah piece tidak sesuai dengan angka yang tercantum.");
                return null;
            }

            return new Board(grid, pieceMap, exitCol, exitRow);

        } catch (IOException e) {
            System.out.println("Terjadi kesalahan saat membaca file: " + e.getMessage());
            return null;
        }
    }
}
