package util;

import java.awt.Point;
import java.io.*;
import java.util.*;
import view.*;

public class OutputWriter {

    private static final String RED_TEXT = "\u001B[31m";
    private static final String WHITE_BG = "\u001B[47m";
    private static final String BLACK_TEXT = "\u001B[30m";
    private static final String RESET_COLOR = "\u001B[0m";
    private static final String YELLOW_BG = "\u001B[43m";

    public static void displayBoard(char[][] grid) {
        for (char[] row : grid) {
            for (char ch : row) {
                switch (ch) {
                    case 'P' -> System.out.print(RED_TEXT + ch + RESET_COLOR);
                    case 'K' -> System.out.print(WHITE_BG + BLACK_TEXT + ch + RESET_COLOR);
                    default -> System.out.print(ch);
                }
            }
            System.out.println();
        }
    }

    public static Map<Character, Set<Point>> extractPositions(char[][] grid) {
        Map<Character, Set<Point>> positionMap = new HashMap<>();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                char symbol = grid[row][col];
                if (symbol != '.') {
                    positionMap.computeIfAbsent(symbol, k -> new HashSet<>()).add(new Point(col, row));
                }
            }
        }
        return positionMap;
    }

    public static void displayBoardWithHighlight(char[][] currentGrid, char[][] prevGrid) {
        Set<Point> changed = new HashSet<>();

        if (prevGrid != null) {
            Map<Character, Set<Point>> prevPos = extractPositions(prevGrid);
            Map<Character, Set<Point>> currPos = extractPositions(currentGrid);

            for (char piece : currPos.keySet()) {
                Set<Point> oldPositions = prevPos.getOrDefault(piece, Collections.emptySet());
                Set<Point> newPositions = currPos.get(piece);

                for (Point p : oldPositions) {
                    if (!newPositions.contains(p)) changed.add(p);
                }
                for (Point p : newPositions) {
                    if (!oldPositions.contains(p)) changed.add(p);
                }
            }
        }

        for (int row = 0; row < currentGrid.length; row++) {
            for (int col = 0; col < currentGrid[row].length; col++) {
                char ch = currentGrid[row][col];
                Point pos = new Point(col, row);
                boolean isChanged = changed.contains(pos);

                if (isChanged) {
                    switch (ch) {
                        case 'P' -> System.out.print(YELLOW_BG + RED_TEXT + ch + RESET_COLOR);
                        case 'K' -> System.out.print(YELLOW_BG + BLACK_TEXT + ch + RESET_COLOR);
                        default -> System.out.print(YELLOW_BG + ch + RESET_COLOR);
                    }
                } else {
                    switch (ch) {
                        case 'P' -> System.out.print(RED_TEXT + ch + RESET_COLOR);
                        case 'K' -> System.out.print(WHITE_BG + BLACK_TEXT + ch + RESET_COLOR);
                        default -> System.out.print(ch);
                    }
                }
            }
            System.out.println();
        }
    }

    public static String convertBoardToString(char[][] grid) {
        StringBuilder builder = new StringBuilder();
        for (char[] row : grid) {
            for (char ch : row) {
                builder.append(ch);
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    public static void writeLinesToFile(String filename, List<String> lines) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (String line : lines) {
                out.println(line);
            }
            System.out.println("Berhasil menyimpan output ke " + filename);
        } catch (IOException e) {
            System.err.println("Gagal menulis file: " + e.getMessage());
        }
    }

    public static void writeBoardToFile(String filename, char[][] grid) {
        String boardStr = convertBoardToString(grid);
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.print(boardStr);
            System.out.println("Papan berhasil disimpan ke " + filename);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan papan ke file: " + e.getMessage());
        }
    }

    public static void printSolution(List<Node> solutionPath) {
        if (solutionPath == null) {
            System.out.println("Solusi tidak ditemukan.");
            return;
        }

        System.out.println("Konfigurasi awal:");
        displayBoard(solutionPath.get(0).board.getGrid());

        for (int i = 1; i < solutionPath.size(); i++) {
            Node step = solutionPath.get(i);
            System.out.println("\nLangkah ke-" + i + ": " + step.moveDesc);
            displayBoardWithHighlight(step.board.getGrid(), solutionPath.get(i - 1).board.getGrid());
        }
    }

    public static void printSolution(List<Node> solutionPath, long elapsedTimeMs) {
        if (solutionPath == null) {
            System.out.println("Solusi tidak ditemukan.");
            return;
        }
        System.out.println("Konfigurasi awal:");
        displayBoard(solutionPath.get(0).board.getGrid());
        for (int i = 1; i < solutionPath.size(); i++) {
            Node step = solutionPath.get(i);
            System.out.println("\nLangkah ke-" + i + ": " + step.moveDesc);
            displayBoardWithHighlight(step.board.getGrid(), solutionPath.get(i - 1).board.getGrid());
        }
        System.out.println("\nJumlah langkah: " + (solutionPath.size() - 1));
        System.out.println("Waktu pencarian: " + elapsedTimeMs + " ms");
    }

    public static void saveSolution(List<Node> solutionPath, String outputPath) {
        if (solutionPath == null) {
            System.err.println("Tidak ada solusi untuk disimpan.");
            return;
        }

        List<String> output = new ArrayList<>();
        output.add("Solusi ditemukan dalam " + (solutionPath.size() - 1) + " langkah:");
        output.add("Papan awal:");
        output.add(convertBoardToString(solutionPath.get(0).board.getGrid()));

        for (int i = 1; i < solutionPath.size(); i++) {
            Node step = solutionPath.get(i);
            output.add("");
            output.add("Langkah ke-" + i + ": " + step.moveDesc);
            output.add(convertBoardToString(step.board.getGrid()));
        }

        writeLinesToFile(outputPath, output);
    }

    public static void saveSolution(List<Node> solutionPath, String outputPath, long elapsedTimeMs) {
        if (solutionPath == null) {
            System.err.println("Tidak ada solusi untuk disimpan.");
            return;
        }
        List<String> output = new ArrayList<>();
        output.add("Solusi ditemukan dalam " + (solutionPath.size() - 1) + " langkah:");
        output.add("Waktu pencarian: " + elapsedTimeMs + " ms");
        output.add("Papan awal:");
        output.add(convertBoardToString(solutionPath.get(0).board.getGrid()));
        for (int i = 1; i < solutionPath.size(); i++) {
            Node step = solutionPath.get(i);
            output.add("");
            output.add("Langkah ke-" + i + ": " + step.moveDesc);
            output.add(convertBoardToString(step.board.getGrid()));
        }
        writeLinesToFile(outputPath, output);
    }
}
