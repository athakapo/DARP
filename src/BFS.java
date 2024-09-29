import java.util.*;

class BFS {
    static int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

    // Function to perform BFS and calculate distance from start cell
    public static double[][] bfs(int[][] grid, int startX, int startY) {
        int rows = grid.length;
        int cols = grid[0].length;
        double[][] distances = new double[rows][cols];

        for (double[] row : distances) {
            Arrays.fill(row, -1); // Mark all cells as unvisited
        }

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startX, startY});
        distances[startX][startY] = 0;

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int x = cell[0], y = cell[1];

            // Explore all 4 directions (Up, Down, Left, Right)
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                // Check if the new cell is within bounds and unvisited
                if (newX >= 0 && newY >= 0 && newX < rows && newY < cols && distances[newX][newY] == -1 && grid[newX][newY] == 0) {
                    distances[newX][newY] = distances[x][y] + 1;
                    queue.offer(new int[]{newX, newY});
                }
            }
        }
        return distances;
    }

    public static void main(String[] args) {
        // Example grid (0 = free space, 1 = obstacle)
        int[][] grid = {
                {0, 1, 0, 0, 0},
                {0, 1, 0, 1, 0},
                {0, 0, 0, 1, 0},
                {0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0}
        };

        // Start BFS from (0, 0)
        double[][] distances = bfs(grid, 0, 0);

        // Print distance from start cell to all other cells
        for (double[] row : distances) {
            for (double dist : row) {
                System.out.print((dist == -1 ? "X" : dist) + " ");
            }
            System.out.println();
        }
    }
}
