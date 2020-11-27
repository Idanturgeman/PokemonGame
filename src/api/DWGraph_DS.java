package api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class DWGraph_DS implements directed_weighted_graph{
    private HashMap<Integer,node_data> nodeMap = new HashMap<Integer, node_data>();
    private int sizeOfEdges = 0;
    private int myMc = 0;


    public DWGraph_DS(){}



    public DWGraph_DS(JSONObject graphJson) throws JSONException{
        JSONArray nodesJson = graphJson.getJSONArray("Nodes");
        for (int i = 0; i < nodesJson.length(); i++){
            addNode(new NodeData(nodesJson.getJSONObject(i)));
        }

        JSONArray edgesJson = graphJson.getJSONArray("Edges");
        for (int i = 0; i < edgesJson.length(); i++){
            connect(new EdgeData(edgesJson.getJSONObject(i)));
        }
    }

    /**
     *
     * @param key - the node_id
     * @return the node data with this key
     */

    @Override
    public node_data getNode(int key) {
        return nodeMap.get(key);
    }


    /**
     *
     * @param src - the source of the edge
     * @param dest - the destination of the edge
     * @return the edge data with this src and dest
     */
    @Override
    public edge_data getEdge(int src, int dest) {
        NodeData n = (NodeData) nodeMap.get(src);
        return n != null ? n.get(dest) : null;
    }


    /**
     * adding the node to the graph
     * @param n - the node data
     */
    @Override
    public void addNode(node_data n) {
        if (nodeMap.containsKey(n.getKey()))
            throw new RuntimeException("The graph already contains Node with this key"+n.getKey());
        nodeMap.put(n.getKey(),n);

    }

    /**
     * connecting the src and dest with the weight to a edge in the graph
     * @param src - the source of the edge.
     * @param dest - the destination of the edge.
     * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        EdgeData e = new EdgeData(src, dest, w);
        connect(e);

    }

    /**
     *
     * @param e will be the actual edge without a copy
     */

    public void connect(EdgeData e){
        NodeData n = (NodeData) nodeMap.get(e.getSrc());
        if (n != null && nodeMap.get(e.getDest()) != null){
            if (!n.containsKey(e.getDest())) {
                sizeOfEdges++;
            }
            n.put(e.getDest(), e);
        }
        else {
            throw new RuntimeException("Can't connect unexist vertices ("
                    +e.getSrc()+","+e.getDest()+"). The nodes are: "+getVnum());
        }
    }

    /**
     *
     * @return set of the vertexes
     */
    public Collection<Integer> getVnum(){
        return nodeMap.keySet();
    }

    /**
     *
     * @return collection of all the nodes in the graph
     */

    @Override
    public Collection<node_data> getV() {
        return nodeMap.values();
    }

    /**
     *
     * @param node_id - when the all edges that returns starts
     * @return collection of all edges with the given node id
     */

    @Override
    public Collection<edge_data> getE(int node_id) {
        NodeData n = (NodeData) nodeMap.get(node_id);
        return n != null ? n.values() : null;
    }

    /**
     *
     * @param key - the node id
     * @return the data of the node that will be remove
     */

    @Override
    public node_data removeNode(int key) {
        node_data del = nodeMap.remove(key);
        if (del != null){
            sizeOfEdges -= ((NodeData)del).size();
            for (Iterator<Integer> it = nodeMap.keySet().iterator(); it.hasNext();){
                removeEdge(it.next(), key);
            }
        }
        return del;
    }


    /**
     *
     * @param src - the source of the edge
     * @param dest - the destination of the edge
     * @return the data of the edge that will be remove
     */

    @Override
    public edge_data removeEdge(int src, int dest) {
        NodeData srcEdge = (NodeData) nodeMap.get(src);
        edge_data e = srcEdge != null ? srcEdge.remove(dest) : null;
        if (e != null){
            sizeOfEdges--;
        }
        return e;
    }


    /**
     *
     * @return the size of nodes in the graph
     */

    @Override
    public int nodeSize() {
        return nodeMap.size();
    }

    /**
     *
     * @return the size of edges in the graph
     */
    @Override
    public int edgeSize() {
        return sizeOfEdges;
    }

    /**
     *
     * @return the size of changes in the graph
     */
    @Override
    public int getMC() {
        return myMc;
    }

    /**
     *
     * @return new DWGraph with the same node but all the edges are in opposite direction
     */

    public DWGraph_DS getReversCopy() {
        DWGraph_DS copy = new DWGraph_DS();
        //copy Nodes
        for (Iterator<node_data> iterator = getV().iterator(); iterator.hasNext(); ) {
            NodeData n = new NodeData((NodeData) iterator.next());
            n.clear();
            copy.addNode(n);
        }

        for (Iterator<node_data> itNodes = getV().iterator(); itNodes.hasNext();) {
            NodeData n = (NodeData) itNodes.next();
            for (Iterator<edge_data> itEdges = getE(n.getKey()).iterator(); itEdges.hasNext(); ) {
                EdgeData e = (EdgeData) itEdges.next();
                copy.connect(e.getReversEdge());
            }
        }
        return copy;
    }


    /**
     * Compare with another DWgraph by comparing the amount of edges and all the nodes.
     */
    @Override
    public boolean equals(Object arg0){
        if (arg0 == null || !(arg0 instanceof DWGraph_DS))
            return false;
        DWGraph_DS dwGraphDs = (DWGraph_DS) arg0;
        return this.sizeOfEdges == dwGraphDs.sizeOfEdges && this.nodeMap.equals(dwGraphDs.nodeMap);
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(sizeOfEdges);
        sb.append(":\n");
        for (Iterator<node_data> it = nodeMap.values().iterator(); it.hasNext();){
            sb.append(it.next() + "\n");
        }
        if (nodeMap.size() > 0){
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }








}
