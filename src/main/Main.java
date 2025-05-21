package main;

import algorithm.*;
import java.util.*;
import view.*;
import util.*;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Masukkan nama file input (contohnya tc1.txt): ");
            String filename = scanner.nextLine();
            String inputPath = "test/" + filename;

            Board start = InputParser.parseFile(inputPath);
            if (start == null) {
                System.out.println("Gagal memuat papan.");
                return;
            }
            System.out.println("Papan berhasil dibaca.");

            int algoChoice = askForInt(scanner,
                "\nPilih algoritma pencarian:\n" +
                "1. Greedy Best First Search\n" +
                "2. Uniform Cost Search\n" +
                "3. A* Search\n" +
                "4. IDA* Search\n" +
                "Masukkan pilihan (1-4): ", 1, 4);

            List<Node> path = null;
            String heuristic = null;
            long startTime = System.currentTimeMillis();

            switch (algoChoice) {
                case 1 -> {
                    System.out.println("Greedy Best First Search dipilih.");
                    heuristic = askGBFSHeuristic(scanner);
                    path = GBFS.solve(start, heuristic);
                }
                case 2 -> {
                    System.out.println("Uniform Cost Search dipilih.");
                    path = UCS.solve(start);
                }
                case 3 -> {
                    System.out.println("A* Search dipilih.");
                    heuristic = askAStarHeuristic(scanner);
                    path = AStar.solve(start, heuristic); 
                }
                case 4 -> {
                    System.out.println("IDA* Search dipilih.");
                    heuristic = askAStarHeuristic(scanner);
                    path = IDA.solve(start, heuristic);
                }
            }

            long endTime = System.currentTimeMillis();
            long elapsedTimeMs = endTime - startTime;

            OutputWriter.printSolution(path, elapsedTimeMs);

            if (path != null) {
                System.out.print("\nApakah ingin menyimpan hasil ke file? (y/n): ");
                String saveChoice = scanner.nextLine().trim().toLowerCase();
                if (saveChoice.equals("y")) {
                    System.out.print("Masukkan nama file simpanan (misal: solusi.txt): ");
                    String outputFile = scanner.nextLine().trim();
                    if (!outputFile.contains("/")) {
                        outputFile = "test/" + outputFile;
                    }
                    OutputWriter.saveSolution(path, outputFile, elapsedTimeMs);
                }
            }
        }
    }

    private static int askForInt(Scanner scanner, String prompt, int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            System.out.print(prompt);
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < min || choice > max) {
                    System.out.println("Input tidak valid! Silakan masukkan angka antara " + min + "-" + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid! Silakan masukkan angka antara " + min + "-" + max + ".");
            }
        }
        return choice;
    }

    private static String askGBFSHeuristic(Scanner scanner) {
        int hChoice = askForInt(scanner,
            "Pilih heuristik:\n" +
            "1. Manhattan Distance Heuristic\n" +
            "2. Blocking Cars Heuristic\n" +
            "Masukkan pilihan (1-2): ", 1, 2);
        return (hChoice == 2) ? "blockingcars" : "manhattan";
    }

    private static String askAStarHeuristic(Scanner scanner) {
        int hChoice = askForInt(scanner,
            "Pilih heuristik:\n" +
            "1. Manhattan Distance Heuristic\n" +
            "2. Obstacle Heuristic\n" +
            "3. Combined Heuristic\n" +
            "Masukkan pilihan (1-3): ", 1, 3);
        return switch (hChoice) {
            case 2 -> "obstacle";
            case 3 -> "combined";
            default -> "manhattan";
        };
    }
}
