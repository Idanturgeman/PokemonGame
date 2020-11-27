package api;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class DWGraph_Algo implements dw_graph_algorithms {
    private static final int NOT_VISITED = 0, VISITED = 1, FINISH = 2;
    private DWGraph_DS myGraph;


    public DWGraph_Algo(){
        this(new DWGraph_DS());
    }


    public DWGraph_Algo(directed_weighted_graph g){
        init(g);
    }




    @Override
    public void init(directed_weighted_graph g) {
        if (g instanceof DWGraph_DS) {
            myGraph = (DWGraph_DS) g;
        }
        else {
            myGraph = copy(g);
        }

    }

    /**
     *
     * @return a pointer to the graph field
     */
    @Override
    public directed_weighted_graph getGraph() {
        return myGraph;
    }



    @Override
    public directed_weighted_graph copy() {
        return copy(myGraph);
    }

    /**
     *
     * @return a copy of g
     */
    public DWGraph_DS copy(directed_weighted_graph g){
        DWGraph_DS copy = new DWGraph_DS();
        Collection<node_data> nodes = g.getV();
        //copy nodes
        for (node_data n : nodes){
            copy.addNode(new NodeData(n));
        }
        //for each node, copy Edges
        for (node_data orgNode : nodes){
            for (edge_data edge : g.getE(orgNode.getKey())){
                copy.connect(new EdgeData(edge));
            }
        }
        return copy;
    }

    /**
     *
     * @return true if and only if there is a strong connection, there is a path
     * from EVERY node to each other node.
     */

    @Override
    public boolean isConnected() {
        if (myGraph == null || myGraph.nodeSize() > myGraph.edgeSize()) {
            return false;
        }
        if (myGraph.nodeSize() == 0){
            return true;
        }
        DWGraph_DS copy = (DWGraph_DS) copy();

        //check if can get from arbitrary node to each node
        for (Iterator<node_data> it = copy.getV().iterator(); it.hasNext();){
            it.next().setTag(NOT_VISITED);
        }
        NodeData arbitrary = (NodeData) copy.getV().iterator().next();
        DFS(copy, arbitrary);
        for (Iterator<node_data> it = copy.getV().iterator(); it.hasNext();){
            node_data node = it.next();
            if (node.getTag() != FINISH){
                return false;
            }
        }

        // check if can get from each node to same arbitrary node
        DWGraph_DS reverse = copy.getReversCopy();
        for (Iterator<node_data> it = reverse.getV().iterator(); it.hasNext();){
            it.next().setTag(NOT_VISITED);
        }
        arbitrary = (NodeData) reverse.getNode(arbitrary.getKey());
        DFS(reverse, arbitrary);
        for (Iterator<node_data> it = reverse.getV().iterator(); it.hasNext();){
            node_data node = it.next();
            if (node.getTag() != FINISH){
                return false;
            }
        }

        return true;
    }


    /**
     * Set tag to VISITED in every Node in g that has path from n to it
     * @param g - the graph to performs DFS on
     * @param n - the Node to start from
     */
    private void DFS(DWGraph_DS g, NodeData n){
        n.setTag(VISITED);
        for (Iterator<Integer> it = n.keySet().iterator(); it.hasNext();){
            NodeData neighbor = (NodeData) g.getNode(it.next());
            if (neighbor.getTag() == NOT_VISITED)
                DFS(g, neighbor);
        }
        n.setTag(FINISH);
    }



    //Dijkstra's Shortest path
    @Override
    public double shortestPathDist(int src, int dest) {
        NodeData s = (NodeData) myGraph.getNode(src);
        NodeData d = (NodeData) myGraph.getNode(dest);

        if (s == null || d == null) {
            int nullNode = s == null ? src : dest;
            throw new RuntimeException("Node dosn't exist (" + nullNode + ")");
        }
        // Mark all nodes unvisited and set weight 0
        for (Iterator<node_data> it = myGraph.getV().iterator(); it.hasNext();) {
            node_data n = it.next();
            n.setTag(NOT_VISITED);
            n.setWeight(Double.MAX_VALUE);
            ((NodeData) n).setFather(null);
        }

        s.setWeight(0);
        PriorityBlockingQueue<node_data> notVisited = new PriorityBlockingQueue<node_data>(myGraph.getV());

        while (!notVisited.isEmpty()) {
            node_data current = notVisited.remove();
            if (current.getWeight() == Double.MAX_VALUE || current.getKey() == dest)
                return current.getWeight();

            // change all current unvisited neighbors weight if found shorter path
            for (edge_data e : ((NodeData) current).values()) {

                node_data neighbour = myGraph.getNode(e.getDest());
                double newWeight = current.getWeight() + e.getWeight();
                if (neighbour.getTag() == NOT_VISITED && newWeight < neighbour.getWeight()) {
                    neighbour.setWeight(newWeight);
                    notVisited.remove(neighbour);
                    notVisited.add(neighbour);
                    ((NodeData) neighbour).setFather(current);
                }

            }

            current.setTag(VISITED);

        }

        return Double.MAX_VALUE;
    }


    /**
     * @param src - start node
     * @param dest - end (target) node
     * @return list of data of all the node in the shortest path across Dijkstra's algorithm
     */

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if (shortestPathDist(src, dest) < Double.MAX_VALUE) {
            ArrayList<node_data> ans = new ArrayList<node_data>();
            NodeData current = (NodeData) myGraph.getNode(dest);
            do {
                ans.add(0, current);
            } while ((current = current.getFather()) != null);
            return ans;
        }
        return null;
    }


    /** save the init graph to a text file for later use.
     * @param file - the file name (may include a relative path).
     * @return if the function was successful in saving the file.
     */
    @Override
    public boolean save(String file) {
     /*   try {
            PrintWriter out = new PrintWriter(file);
            out.print(myGraph);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
       */ return true;
    }




    @Override
    public boolean load(String file) {
        return false;
    }
}
