package algorithm;

import java.util.*;
import view.*;

public class AStar {

    public static int calculateObstacleHeuristic(Board board, Piece primaryPiece) {
        int obstacles = 0;
        int exitCol = board.getExitCol();
        int exitRow = board.getExitRow();
        int pieceX = primaryPiece.x;
        int pieceY = primaryPiece.y;

        for (Piece p : board.getPieceMap().values()) {
            if (p == primaryPiece) continue;

            if (p.isHorizontal && p.y == pieceY) {
                if ((pieceX < exitCol && p.x > pieceX && p.x < exitCol) ||
                    (pieceX > exitCol && p.x < pieceX && p.x + p.length > exitCol)) {
                    obstacles++;
                }
            } else if (!p.isHorizontal) {
                if (p.x >= Math.min(pieceX, exitCol) && p.x <= Math.max(pieceX, exitCol) &&
                    p.y <= pieceY && p.y + p.length > pieceY) {
                    obstacles++;
                }
            }
        }

        return obstacles;
    }

    public static int calculateManhattanHeuristic(Board board, Piece primaryPiece) {
        int dx = Math.abs(primaryPiece.x - board.getExitCol());
        int dy = Math.abs(primaryPiece.y - board.getExitRow()) / 2;
        return dx + dy;
    }

    public static int calculateCombinedHeuristic(Board board, Piece primaryPiece) {
        int obstacles = calculateObstacleHeuristic(board, primaryPiece);
        int manhattan = calculateManhattanHeuristic(board, primaryPiece);
        return obstacles + manhattan * 2;
    }

    public static List<Node> solve(Board initialBoard, String heuristicType) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.g + n.h));
        Set<String> visited = new HashSet<>();
        long startTime = System.currentTimeMillis();

        Piece primaryPiece = initialBoard.getPieceMap().get('P');

        int h0 = switch (heuristicType.toLowerCase()) {
            case "obstacle" -> calculateObstacleHeuristic(initialBoard, primaryPiece);
            case "combined" -> calculateCombinedHeuristic(initialBoard, primaryPiece);
            default -> calculateManhattanHeuristic(initialBoard, primaryPiece);
        };

        Node startNode = new Node(initialBoard, null, null, 0, h0);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.board.isGoal()) {
                long endTime = System.currentTimeMillis();
                List<Node> solution = Node.reconstructPath(current);
                System.out.println("Solusi ditemukan dalam " + current.g + " langkah");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                return solution;
            }

            String boardKey = current.board.getBoardKey();
            if (visited.contains(boardKey)) continue;
            visited.add(boardKey);

            List<Node> neighbors = Node.generateNextNodes(current);
            for (Node nextNode : neighbors) {
                String nextKey = nextNode.board.getBoardKey();
                if (!visited.contains(nextKey)) {
                    Piece nextPrimary = nextNode.board.getPieceMap().get('P');
                    int hNext = switch (heuristicType.toLowerCase()) {
                        case "obstacle" -> calculateObstacleHeuristic(nextNode.board, nextPrimary);
                        case "combined" -> calculateCombinedHeuristic(nextNode.board, nextPrimary);
                        default -> calculateManhattanHeuristic(nextNode.board, nextPrimary);
                    };
                    nextNode.h = hNext;
                    openSet.add(nextNode);
                }
            }
        }

        System.out.println("Tidak ditemukan solusi");
        long endTime = System.currentTimeMillis();
        System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
        return null;
    }
}
