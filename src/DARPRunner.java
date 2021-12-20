import java.util.Arrays;

public class DARPRunner {

    public static void main(String[] arg){
        int MaxIter =80000;
        double CCvariation = 0.01;
        double randomLevel = 0.0001;
        int dcells = 2;
        boolean importance = false;

        int rows = 10;
        int cols = 10;
        int[][] initial_positions = {{0, 1}, {0, 3}, {0, 9}};
        double[] portions = {0.2, 0.3, 0.5};
        int[][] obstacles_positions = null;
        int[][] EnvironmentGrid = new int[rows][cols];

        for (int[] robot_pos:initial_positions) {
            EnvironmentGrid[robot_pos[0]][robot_pos[1]] = 2;
        }

        if (obstacles_positions != null) {
            for (int[] ob_pos : obstacles_positions) {
                EnvironmentGrid[ob_pos[0]][ob_pos[1]] = 1;
            }
        }

        DARP darp = new DARP(rows, cols, EnvironmentGrid, MaxIter, CCvariation, randomLevel, dcells, importance);
        darp.constructAssignmentM();
        System.out.println("Success: "+darp.getSuccess());
        System.out.println("Overall iterations needed: "+darp.getOverall_iter());
        int[][] A = darp.getAssignmentMatrix();
        for (int[] x : A)
        {
            for (int y : x)
            {
                System.out.print(y + " ");
            }
            System.out.println();
        }
    }

}
