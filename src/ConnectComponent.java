import java.awt.*;

/**
 * Use row-by-row labeling algorithm to label connected components
 * The algorithm makes two passes over the image: one pass to record
 * equivalences and assign temporary labels and the second to replace each
 * temporary label by the label of its equivalence class.
 * [Reference]
 * Linda G. Shapiro, Computer Vision: Theory and Applications.  (3.4 Connected
 * Components Labeling)
 * Rosenfeld and Pfaltz (1966)
 */
public class ConnectComponent
{
    int MAX_LABELS;
    int next_label = 1;
    private int[][] label2d, BinaryRobot, BinaryNonRobot;
    private int rows, cols;

    /**
     * label and re-arrange the labels to make the numbers of label continous
     * @param zeroAsBg Leaving label 0 untouched
     */
    public int[][] compactLabeling(int[][] m, Dimension d, boolean zeroAsBg)
    {
        int[] image =TransformImage2Dto1D(m, d);
        label2d = new int[d.height][d.width];
        MAX_LABELS = d.height*d.width;
        rows = d.height;
        cols = d.width;
        //label first
        int[] label= labeling(image,d,zeroAsBg);
        int[] stat= new int[next_label+1];
        for (int i=0;i<image.length;i++) {
            //if (label[i]>next_label)
                //System.err.println("bigger label than next_label found!");
            stat[label[i]]++;
        }

        stat[0]=0;              // label 0 will be mapped to 0
        // whether 0 is background or not
        int j = 1;
        for (int i=1; i<stat.length; i++) {
            if (stat[i]!=0) stat[i]=j++;
        }

        //System.out.println("From "+next_label+" to "+(j-1)+" regions");
        next_label= j-1;
        int locIDX=0;
        for (int i=0;i<d.height;i++){
            for (int ii=0;ii<d.width;ii++) {
                label[locIDX] = stat[label[locIDX]];
                label2d[i][ii] = label[locIDX];
                locIDX++;
            }
        }


        return label2d;
    }

    public int[] TransformImage2Dto1D(int[][] a, Dimension d){
        int[] ret = new int[d.height*d.width];
        int k=0;
        for (int i=0;i<d.height;i++){
            for (int j=0;j<d.width;j++){
                ret[k]=a[i][j];
                k++;
            }
        }

        return ret;
    }

    /**
     * return the max label in the labeling process.
     * the range of labels is [0..max_label]
     */
    public int getMaxLabel() {return next_label;}


    /**
     * Label the connect components
     * If label 0 is background, then label 0 is untouched;
     * If not, label 0 may be reassigned
     * [Requires]
     *   0 is treated as background
     * @param image data
     * @param d dimension of the data
     * @param zeroAsBg label 0 is treated as background, so be ignored
     */
    public int[] labeling(int[] image, Dimension d, boolean zeroAsBg)
    {
        int w= d.width, h= d.height;
        int[] rst= new int[w*h];
        int[] parent= new int[MAX_LABELS];
        int[] labels= new int[MAX_LABELS];
        // region label starts from 1;
        // this is required as union-find data structure
        int next_region = 1;
        for (int y = 0; y < h; ++y ){
            for (int x = 0; x < w; ++x ){
                if (image[y*w+x] == 0 && zeroAsBg) continue;
                int k = 0;
                boolean connected = false;
                // if connected to the left
                if (x > 0 && image[y*w+x-1] == image[y*w+x]) {
                    k = rst[y*w+x-1];
                    connected = true;
                }
                // if connected to the up
                if (y > 0 && image[(y-1)*w+x]== image[y*w+x] && (!connected || image[(y-1)*w+x] < k )) {
                    k = rst[(y-1)*w+x];
                    connected = true;
                }
                if ( !connected ) {
                    k = next_region;
                    next_region++;
                }

                /*
                if ( k >= MAX_LABELS ){
                    System.err.println("maximum number of labels reached. " +
                            "increase MAX_LABELS and recompile." );
                    System.exit(1);
                }*/

                rst[y*w+x]= k;
                // if connected, but with different label, then do union
                if ( x> 0 && image[y*w+x-1]== image[y*w+x] && rst[y*w+x-1]!= k )
                    uf_union( k, rst[y*w+x-1], parent );
                if ( y> 0 && image[(y-1)*w+x]== image[y*w+x] && rst[(y-1)*w+x]!= k )
                    uf_union( k, rst[(y-1)*w+x], parent );
            }
        }

        // Begin the second pass.  Assign the new labels
        // if 0 is reserved for background, then the first available label is 1
        next_label = 1;
        for (int i = 0; i < w*h; i++ ) {
            if (image[i]!=0 || !zeroAsBg) {
                rst[i] = uf_find( rst[i], parent, labels );
                // The labels are from 1, if label 0 should be considered, then
                // all the label should minus 1
                if (!zeroAsBg) rst[i]--;
            }
        }
        next_label--;   // next_label records the max label
        if (!zeroAsBg) next_label--;

        //System.out.println(next_label+" regions");

        return rst;
    }
    void uf_union( int x, int y, int[] parent)
    {
        while ( parent[x]>0 )
            x = parent[x];
        while ( parent[y]>0 )
            y = parent[y];
        if ( x != y ) {
            if (x<y)
                parent[x] = y;
            else parent[y] = x;
        }
    }

    /**
     * This function is called to return the root label
     * Returned label starts from 1 because label array is inited to 0 as first
     * [Effects]
     *   label array records the new label for every root
     */
    int uf_find( int x, int[] parent, int[] label)

    {
        while ( parent[x]>0 )
            x = parent[x];
        if ( label[x] == 0 )
            label[x] = next_label++;
        return label[x];
    }


    public void constructBinaryImages(int robotsLabel){

        BinaryRobot = deepCopyMatrix(label2d);
        BinaryNonRobot = deepCopyMatrix(label2d);

        for (int i=0; i<rows;i++){
            for (int j=0; j<cols;j++){
                if (label2d[i][j] == robotsLabel) {
                    BinaryRobot[i][j] = 1;
                    BinaryNonRobot[i][j] = 0;
                }else if (label2d[i][j] !=0){
                    BinaryRobot[i][j] = 0;
                    BinaryNonRobot[i][j] = 1;
                }
            }
        }
    }


    private int[][] deepCopyMatrix(int[][] input) {
        if (input == null)
            return null;
        int[][] result = new int[input.length][];
        for (int r = 0; r < input.length; r++) {
            result[r] = input[r].clone();
        }
        return result;
    }


    /**
     * Calculate the normalized euclidean distance transform of a binary image with
     * foreground pixels set to 1 and background set to 0.
     */
    public float[][] NormalizedEuclideanDistanceBinary(boolean RobotR) {

        float[][] Region = new float[rows][cols];

        float [] f = new float[Math.max(rows, cols)];
        float [] d = new float[f.length];
        int [] v = new int[f.length];
        float [] z = new float[f.length + 1];

        for (int x=0; x<cols; x++) {
            for (int y=0; y<rows; y++) {
                if (RobotR)
                    f[y] = BinaryRobot[y][x] == 0 ? Float.MAX_VALUE : 0;
                else
                    f[y] = BinaryNonRobot[y][x] == 0 ? Float.MAX_VALUE : 0;
            }

            DT1D(f, d, v, z);
            for (int y = 0; y < rows; y++) {
                Region[y][x] = d[y];
            }
        }

        float maxV=0, minV=Float.MAX_VALUE;
        for (int y = 0; y < rows; y++) {
            DT1D(getVector(Region,y), d, v,  z);

            for (int x = 0; x < cols; x++) {
                Region[y][x] = (float) Math.sqrt(d[x]);
                if (maxV < Region[y][x]) {maxV = Region[y][x];}
                if (minV > Region[y][x]) {minV = Region[y][x];}
            }
        }


        //Normalization
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (RobotR)
                    Region[y][x] = (Region[y][x] - minV) * (1/(maxV-minV)) +1;
                else
                    Region[y][x] = (Region[y][x] - minV) * (1/(maxV-minV));
            }
        }


        return Region;
    }

    private void DT1D(float [] f, float [] d, int [] v , float [] z) {
        int k = 0;
        v[0] = 0;
        z[0] = -Float.MAX_VALUE;
        z[1] = Float.MAX_VALUE;

        for (int q = 1; q < f.length; q++) {
            float s  = ((f[q] + q * q) - (f[v[k]] + v[k] * v[k])) / (2 * q - 2 * v[k]);

            while (s <= z[k]) {
                k--;
                s  = ((f[q] + q * q) - (f[v[k]] + v[k] * v[k])) / (2 * q - 2 * v[k]);
            }
            k++;
            v[k] = q;
            z[k] = s;
            z[k + 1] = Float.MAX_VALUE;
        }

        k = 0;
        for (int q = 0; q < f.length; q++) {
            while (z[k + 1] < q) {k++;}

            d[q] = (q - v[k]) * (q - v[k]) + f[v[k]];
        }
    }

    private float[] getVector(float[][] A, int row) {
        float[] ret = new float[cols];

        for (int i=0;i<cols;i++){ret[i] = A[row][i];}

        return ret;
    }

}