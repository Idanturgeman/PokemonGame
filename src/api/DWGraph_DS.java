package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class DWGraph_DS implements directed_weighted_graph {

    private int _MC = 0;
    private int _edgeSize = 0;
    private HashMap<Integer, node_data> _nodesMap = new HashMap<>();
    private LinkedList<node_data> _nodesList = new LinkedList<>();


    @Override
    public node_data getNode(int key) {
        if (_nodesMap.containsKey(key))
        {
            return _nodesMap.get(key);
        }
        return null;
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        if (_nodesMap.containsKey(src) && _nodesMap.containsKey(dest) && src != dest)
        {
            NodeData node = (NodeData) _nodesMap.get(src);
            return node.getEdge(dest);
        }
        return null;
    }

    @Override
    public void addNode(node_data n) {
        if (!_nodesMap.containsKey(n.getKey()))
        {
            _nodesMap.put(n.getKey(), n);
            _nodesList.add(n);
            _MC++;
        }
    }

    @Override
    public void connect(int src, int dest, double w) {
        if (_nodesMap.containsKey(src) && _nodesMap.containsKey(dest) && w > 0 && src != dest)
        {
            NodeData srcNode = (NodeData) _nodesMap.get(src);
            NodeData destNode = (NodeData) _nodesMap.get(dest);
            if (!srcNode.hasEdge(dest))
            {
                EdgeData edge = new EdgeData(_nodesMap.get(src), _nodesMap.get(dest), w);
                srcNode.addEdge(edge);
                destNode.ReversEdge(edge);
                _edgeSize++;
            }
        }
    }

    @Override
    public Collection<node_data> getV() {
        return _nodesList;
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        if (_nodesMap.containsKey(node_id))
        {
            NodeData node = (NodeData) _nodesMap.get(node_id);
            return node.getEdgeCol();
        }
        LinkedList<edge_data> emptyList = new LinkedList<>();
        return emptyList;
    }



    @Override
    public node_data removeNode(int key) {
        if (_nodesMap.containsKey(key))
        {
            NodeData node = (NodeData) _nodesMap.get(key);
            Collection<edge_data> edges = node.getEdgeCol();
            edge_data temp[] = edges.toArray(new edge_data[0]);
            for(edge_data e : temp)
            {
                this.removeEdge(e.getSrc(),e.getDest());
            }
            edges = node.getEdgeCol2();
            temp = edges.toArray(new edge_data[0]);
            for(edge_data e : temp)
            {
                this.removeEdge(e.getSrc(),e.getDest());
            }
            _nodesMap.remove(key);
            _nodesList.remove(node);
            _MC++;
            return node;
        }
        return null;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        if (_nodesMap.containsKey(src) && _nodesMap.containsKey(dest))
        {
            NodeData srcNode = (NodeData) _nodesMap.get(src);
            NodeData destNode = (NodeData) _nodesMap.get(dest);
            destNode.removeReversEdge(src);
            _edgeSize--;
            return srcNode.removeEdge(dest);
        }
        return null;
    }

    public Collection<edge_data> getE2(int node_id) {
        if (_nodesMap.containsKey(node_id))
        {
            NodeData node = (NodeData) _nodesMap.get(node_id);
            return node.getEdgeCol2();
        }
        LinkedList<edge_data> emptyList = new LinkedList<>();
        return emptyList;
    }


    @Override
    public int nodeSize() {
        return _nodesList.size();
    }

    @Override
    public int edgeSize() {
        return _edgeSize;
    }

    @Override
    public int getMC() {
        return _MC;
    }

    /** a toString function for the graph.
     * prints the number of nodes edges in the graph,
     * along with a list of every node and his neighbors edges and tags.
     * @return
     */
    public String toString(){
        String sGraph = "nodes: "+ _nodesMap.size()+", edges: "+ _edgeSize;
        Collection<node_data> graphList = this.getV();
        for(node_data node : graphList)
        {
            NodeData nodeI = (NodeData) node;
            sGraph += "\n" + nodeI.toString();
        }
        return sGraph;
    }


}


