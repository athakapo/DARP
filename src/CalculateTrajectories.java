import java.util.*;

/**
 * Created by atkap on 5/24/2016.
 */
public class CalculateTrajectories {

    private TreeSet[] nodes;               // Array of connected components
    private int MAX_NODES;
    private int rows, cols;
    private Vector MSTvector;
    private int MSTedges;
    private HashSet<Edge> AllEdges;
    private ArrayList<Integer[]> PathSequence;



    CalculateTrajectories(int r, int c, Vector MST) {
        // Constructor
        this.MAX_NODES = 4*r*c;
        this.PathSequence = new ArrayList<>();
        this.rows=r;
        this.cols=c;
        this. MSTvector = MST;
        this.MSTedges = MSTvector.size();
        this.AllEdges = new HashSet<>();
        this.nodes = new TreeSet[MAX_NODES];      // Create array for components
    }


    public void initializeGraph(boolean [][] A, boolean connect4) {

        for (int i=0;i<2*rows;i++){
            for (int j=0;j<2*cols;j++){
                if (A[i][j]){

                    if (i>0 && A[i-1][j]) {AddToAllEdges(i*2*cols+j, (i-1)*2*cols+j, 1);}
                    if (i<2*rows-1 && A[i+1][j]) {AddToAllEdges(i*2*cols+j, (i+1)*2*cols+j, 1);}
                    if (j>0 && A[i][j-1]) {AddToAllEdges(i*2*cols+j, i*2*cols+j-1, 1);}
                    if (j<2*cols-1 && A[i][j+1]) {AddToAllEdges(i*2*cols+j, i*2*cols+j+1, 1);}

                    if (!connect4){
                        if (i>0 && j>0 && A[i-1][j-1]) {AddToAllEdges(i*2*cols+j, (i-1)*2*cols+j-1, 1);}
                        if (i<2*rows-1 && j<2*cols-1 && A[i+1][j+1]) {AddToAllEdges(i*2*cols+j, (i+1)*2*cols+j+1, 1);}
                        if (i>2*rows-1 && j>0 && A[i+1][j-1]) {AddToAllEdges(i*2*cols+j, (i+1)*2*cols+j-1, 1);}
                        if (i>0 && j<2*cols-1 && A[i-1][j+1]) {AddToAllEdges(i*2*cols+j, (i-1)*2*cols+j+1, 1);}
                    }
                }
            }
        }

    }

    private void AddToAllEdges(int from, int to, int cost){
        //allEdges.add(new Edge(from, to, cost));  // Update priority queue
        AllEdges.add(new Edge(from, to, cost));  // Update priority queue
        if (nodes[from] == null) {
            // Create set of connect components [singleton] for this node
            nodes[from] = new TreeSet();
            //nodes[from].add(new Integer(from));
        }
        nodes[from].add(new Integer(to));

        if (nodes[to] == null) {
            // Create set of connect components [singleton] for this node
            nodes[to] = new TreeSet();
            //nodes[to].add(new Integer(to));
        }
        nodes[to].add(new Integer(from));
    }


    public void RemoveTheAppropriateEdges(){


        int alpha,maxN,minN;
        Edge eToRemove,eToRemoveMirr,eToRemove2,eToRemove2Mirr;
        for (int i=0;i<MSTedges;i++){
            Edge e = (Edge) MSTvector.get(i);
            maxN = Math.max(e.from, e.to);
            minN = Math.min(e.from, e.to);

            if (Math.abs(e.from - e.to)==1) {
                alpha = (4*minN+3) - 2*(maxN % cols);
                eToRemove = new Edge(alpha,alpha+2*cols,1);
                eToRemoveMirr = new Edge(alpha+2*cols,alpha,1);
                eToRemove2 = new Edge(alpha+1,alpha+1+2*cols,1);
                eToRemove2Mirr = new Edge(alpha+1+2*cols,alpha+1,1);

            }else{
                alpha = (4*minN+2*cols) - 2*(maxN % cols);
                eToRemove = new Edge(alpha,alpha+1,1);
                eToRemoveMirr = new Edge(alpha+1,alpha,1);
                eToRemove2 = new Edge(alpha+2*cols,alpha+1+2*cols,1);
                eToRemove2Mirr = new Edge(alpha+1+2*cols,alpha+2*cols,1);
            }


            if (AllEdges.contains(eToRemove)) {
                SafeRemoveEdge(eToRemove);
            }
            if (AllEdges.contains(eToRemoveMirr)) {
                SafeRemoveEdge(eToRemoveMirr);
            }
            if (AllEdges.contains(eToRemove2)) {
                SafeRemoveEdge(eToRemove2);
            }
            if (AllEdges.contains(eToRemove2Mirr)) {
                SafeRemoveEdge(eToRemove2Mirr);
            }

        }
    }


    private void SafeRemoveEdge(Edge curEdge){
        if (AllEdges.remove(curEdge)) {
            // successful removal from priority queue: allEdges

            nodes[curEdge.from].remove(curEdge.to);
            nodes[curEdge.to].remove(curEdge.from);

        } else {
            // This is a serious problem
            System.out.println("TreeSet should have contained this element!!");
            System.exit(1);
        }
    }

    public void CalculatePathsSequence(int StartingNode){

        int currentNode =StartingNode;
        HashSet<Integer> RemovedNodes = new HashSet<>();
        int prevNode,i,j,previ,prevj;
        do {
            RemovedNodes.add(currentNode);

            prevNode = currentNode;
            do {
                try{
                currentNode = (int) nodes[prevNode].last();
                }catch (NoSuchElementException ex){
                    int a=1;
                }
                //if (nodes[prevNode].size()<1 || currentNode==StartingNode) {return;}
                if (nodes[prevNode].size()<1) {return;}
                if (nodes[currentNode].contains(prevNode)) {nodes[currentNode].remove(prevNode);}
                if (nodes[prevNode].contains(currentNode)) {nodes[prevNode].remove(currentNode);}
            }while(RemovedNodes.contains(currentNode));

            i=currentNode/(2*cols);
            j=currentNode%(2*cols);
            previ=prevNode/(2*cols);
            prevj=prevNode%(2*cols);
            PathSequence.add(new Integer[]{previ,prevj,i,j});

        }while (true);
    }

    public ArrayList<Integer[]> getPathSequence() {return PathSequence;}

}
