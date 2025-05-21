package algorithm;

import java.util.*;
import view.*;

public class IDA {

    public static List<Node> solve(Board initialBoard, String heuristicType) {
        long startTime = System.currentTimeMillis();

        Piece primaryPiece = initialBoard.getPieceMap().get('P');
        int h0 = getHeuristic(initialBoard, primaryPiece, heuristicType);
        Node startNode = new Node(initialBoard, null, null, 0, h0);
        int threshold = h0;

        while (true) {
            Set<String> visited = new HashSet<>();
            Map<String, Integer> gScoreMap = new HashMap<>();

            Result result = search(startNode, threshold, heuristicType, visited, gScoreMap);

            if (result.found) {
                long endTime = System.currentTimeMillis();
                System.out.println("Solusi ditemukan dalam " + result.goalNode.g + " langkah");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                return Node.reconstructPath(result.goalNode);
            }

            if (result.minThreshold == Integer.MAX_VALUE) {
                long endTime = System.currentTimeMillis();
                System.out.println("Tidak ditemukan solusi");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                return null;
            }

            threshold = result.minThreshold;
        }
    }

    private static Result search(Node node, int threshold, String heuristicType,
                                 Set<String> visited, Map<String, Integer> gScoreMap) {
        int f = node.g + node.h;
        String key = node.board.getBoardKey();

        if (gScoreMap.containsKey(key) && gScoreMap.get(key) <= node.g) {
            return new Result(false, null, Integer.MAX_VALUE);
        }
        gScoreMap.put(key, node.g);

        if (f > threshold) {
            return new Result(false, null, f);
        }

        if (node.board.isGoal()) {
            return new Result(true, node, f);
        }

        visited.add(key);
        int min = Integer.MAX_VALUE;

        List<Node> children = Node.generateNextNodes(node);

        for (Node child : children) {
            child.g = node.g + 1;
            Piece nextPrimary = child.board.getPieceMap().get('P');
            child.h = getHeuristic(child.board, nextPrimary, heuristicType);

            Piece currentPrimary = node.board.getPieceMap().get('P');
            String moveDirection = "";
            if (nextPrimary.x > currentPrimary.x) {
                moveDirection = "ke kanan " + (nextPrimary.x - currentPrimary.x);
            } else if (nextPrimary.x < currentPrimary.x) {
                moveDirection = "ke kiri " + (currentPrimary.x - nextPrimary.x);
            } else if (nextPrimary.y > currentPrimary.y) {
                moveDirection = "ke bawah " + (nextPrimary.y - currentPrimary.y);
            } else if (nextPrimary.y < currentPrimary.y) {
                moveDirection = "ke atas " + (currentPrimary.y - nextPrimary.y);
            }
            child.moveDesc = "Geser P " + moveDirection;
        }

        children.sort(Comparator.comparingInt(c -> c.g + c.h));

        for (Node child : children) {
            String childKey = child.board.getBoardKey();
            if (visited.contains(childKey)) continue;

            Result result = search(child, threshold, heuristicType, visited, gScoreMap);
            if (result.found) {
                return result;
            }

            if (result.minThreshold < min) {
                min = result.minThreshold;
            }

            visited.remove(childKey);
        }

        visited.remove(key);
        return new Result(false, null, min);
    }

    private static int getHeuristic(Board board, Piece primaryPiece, String heuristicType) {
        if (primaryPiece == null) return 0;
        return switch (heuristicType.toLowerCase()) {
            case "obstacle" -> AStar.calculateObstacleHeuristic(board, primaryPiece);
            case "combined" -> AStar.calculateCombinedHeuristic(board, primaryPiece);
            default -> AStar.calculateManhattanHeuristic(board, primaryPiece);
        };
    }

    private static class Result {
        boolean found;
        Node goalNode;
        int minThreshold;

        Result(boolean found, Node goalNode, int minThreshold) {
            this.found = found;
            this.goalNode = goalNode;
            this.minThreshold = minThreshold;
        }
    }
}
