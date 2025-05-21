package algorithm;

import java.util.*;
import view.*;

public class GBFS {

    public static int calculateHeuristic(Board state, String type) {
        return switch (type.toLowerCase()) {
            case "manhattan" -> heuristicManhattan(state);
            case "blockingcars" -> heuristicBlockers(state);
            default -> throw new IllegalArgumentException("Unknown heuristic type: " + type);
        };
    }

    public static int heuristicManhattan(Board state) {
        Piece primary = state.getPieceMap().get('P');
        if (primary == null) return 0;

        if (primary.isHorizontal) {
            return Math.abs(state.getExitCol() - (primary.x + primary.length - 1));
        } else {
            return Math.abs(state.getExitRow() - (primary.y + primary.length - 1));
        }
    }

    public static int heuristicBlockers(Board state) {
        Piece primary = state.getPieceMap().get('P');
        if (primary == null) return 0;

        int blockers = 0;
        if (primary.isHorizontal) {
            int y = primary.y;
            for (int x = primary.x + primary.length; x <= state.getExitCol(); x++) {
                char cell = state.getGrid()[y][x];
                if (cell != '.' && cell != primary.id) blockers++;
            }
        } else {
            int x = primary.x;
            for (int y = primary.y + primary.length; y <= state.getExitRow(); y++) {
                char cell = state.getGrid()[y][x];
                if (cell != '.' && cell != primary.id) blockers++;
            }
        }
        return blockers;
    }

    public static List<Node> solve(Board initialBoard, String heuristicType) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.h));
        Set<String> visited = new HashSet<>();
        long startTime = System.currentTimeMillis();

        int h0 = calculateHeuristic(initialBoard, heuristicType);
        Node startNode = new Node(initialBoard, null, null, 0, h0);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.board.isGoal()) {
                long endTime = System.currentTimeMillis();
                System.out.println("Solusi ditemukan dalam " + current.g + " langkah");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                return Node.reconstructPath(current);
            }

            String key = current.board.getBoardKey();
            if (visited.contains(key)) continue;
            visited.add(key);

            for (Node nextNode : Node.generateNextNodes(current)) {
                String nextKey = nextNode.board.getBoardKey();
                if (!visited.contains(nextKey)) {
                    nextNode.h = calculateHeuristic(nextNode.board, heuristicType);
                    nextNode.g = current.g + 1; 
                    openSet.add(nextNode);
                }
            }
        }

        System.out.println("Tidak ada solusi");
        long endTime = System.currentTimeMillis();
        System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
        return null;
    }
}
