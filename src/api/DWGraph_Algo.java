package api;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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


    private void DFS(DWGraph_DS g, NodeData n){
        n.setTag(VISITED);
        for (Iterator<Integer> it = n.keySet().iterator(); it.hasNext();){
            NodeData neighbor = (NodeData) g.getNode(it.next());
            if (neighbor.getTag() == NOT_VISITED)
                DFS(g, neighbor);
        }
        n.setTag(FINISH);
    }


    @Override
    public double shortestPathDist(int src, int dest) {
        return 0;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        return null;
    }

    @Override
    public boolean save(String file) {
        return false;
    }

    @Override
    public boolean load(String file) {
        return false;
    }
}
