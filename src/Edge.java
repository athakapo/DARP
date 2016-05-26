import java.util.Comparator;

/**
 * Created by atkap on 5/24/2016.
 */
class Edge implements Comparator  {
    // Inner class for representing edge+end-points
    public int from, to, cost;
    public Edge() {
        // Default constructor for TreeSet creation
    }
    public Edge(int f, int t, int c) {
        // Inner class constructor
        from = f; to = t; cost = c;
    }

    public int compare(Object o1, Object o2) {
        // Used for comparisions during add/remove operations
        int cost1 = ((Edge) o1).cost;
        int cost2 = ((Edge) o2).cost;
        int from1 = ((Edge) o1).from;
        int from2 = ((Edge) o2).from;
        int to1   = ((Edge) o1).to;
        int to2   = ((Edge) o2).to;

        if (cost1<cost2)
            return(-1);
        else if (cost1==cost2 && from1==from2 && to1==to2)
            return(0);
        else if (cost1==cost2)
            return(-1);
        else if (cost1>cost2)
            return(1);
        else
            return(0);
    }

    public int hashCode(){
        return (Integer.toString(cost)+Integer.toString(from)+Integer.toString(to)).hashCode();
    }

    public boolean equals(Object obj) {
        // Used for comparisions during add/remove operations
        Edge e = (Edge) obj;
        return (cost==e.cost && from==e.from && to==e.to);
    }

}