package Algorithms;

import api.*;
import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class DWGraphs_Algo implements dw_graph_algorithms {

    private DWGraph_DS _myGraph;


    public DWGraphs_Algo(directed_weighted_graph g){
        _myGraph = (DWGraph_DS) g;
    }

    @Override
    public void init(directed_weighted_graph g) {
        _myGraph = (DWGraph_DS) g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return _myGraph;
    }

    @Override
    public directed_weighted_graph copy() {
        DWGraph_DS newGr = new DWGraph_DS();
        List<node_data> nodes = (List<node_data>) _myGraph.getV();
        for(Iterator<node_data> node = nodes.iterator(); node.hasNext();)
        {
            newGr.addNode(new NodeData(node.next().getKey()));
        }
        for(Iterator<node_data> node = nodes.iterator(); node.hasNext();)
        {
            List<edge_data> edges = (List<edge_data>) _myGraph.getE(node.next().getKey());
            for(edge_data e : edges)
            {
                newGr.connect(e.getSrc(), e.getDest(), e.getWeight());
            }
        }
        return newGr;
    }



    @Override
    public boolean isConnected() {
        setTag();
        LinkedList<node_data> q = new LinkedList<>();
        List<node_data> nodes = (List<node_data>) _myGraph.getV();
        if(nodes.size() == 0 || nodes.size() == 1)
        {
            return true;
        }
        node_data src = nodes.get(0);
        q.add(src);
        while(!q.isEmpty())
        {
            src = q.remove();
            List<edge_data> edges = (List<edge_data>) _myGraph.getE(src.getKey());
            for(edge_data e : edges)
            {
                node_data node = _myGraph.getNode(e.getDest());
                if(node.getTag() == 0)
                {
                    q.add(node);
                }
                node.setTag(1);
            }
        }
        for(node_data node1 : nodes)
        {
            if(node1.getTag() == 0)
            {
                return false;
            }
        }
        setTag();
        q = new LinkedList<node_data>();
        src = nodes.get(0);
        q.add(src);
        while(!q.isEmpty())
        {
            src = q.remove();
            List<edge_data> edges = (List<edge_data>) _myGraph.getE2(src.getKey());
            for(edge_data e : edges)
            {
                node_data node = _myGraph.getNode(e.getSrc());
                if(node.getTag() == 0)
                {
                    q.add(node);
                }
                node.setTag(1);
            }
        }
        for(node_data node1 : nodes)
        {
            if(node1.getTag() == 0)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public double shortestPathDist(int src, int dest) {
        if(src == dest)
        {
            return 0;
        }
        this.setWeight();
        node_data node, nDest;
        node = _myGraph.getNode(src);
        nDest = _myGraph.getNode(dest);
        if(node != null && nDest != null)
        {
            node.setInfo("");
            PriorityQueue<node_data> que = new PriorityQueue<>(_myGraph.nodeSize(), _comp);
            que.add(node);
            while(!que.isEmpty())
            {
                node = que.remove();
                if(node.getKey() == dest)
                {
                    return node.getWeight();
                }
                List<edge_data> edges = (List<edge_data>) _myGraph.getE(node.getKey());
                for(edge_data e : edges)
                {
                    node_data temp = _myGraph.getNode(e.getDest());
                    double dist = node.getWeight() + e.getWeight();
                    if((dist < temp.getWeight() || temp.getWeight() == 0 )&& temp.getKey() != src)
                    {
                        if(dist < temp.getWeight())
                        {
                            que.remove(temp);
                        }
                        temp.setInfo(node.getInfo() + temp.getKey() + ",");
                        temp.setWeight(dist);
                        que.add(temp);
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        if(this.shortestPathDist(src, dest) != -1)
        {
            LinkedList<node_data> path = new LinkedList<>();
            node_data node = _myGraph.getNode(src);
            path.add(node);
            if(src == dest)
            {
                return path;
            }
            String info = _myGraph.getNode(dest).getInfo();
            while(!info.isEmpty())
            {
                int divider = info.indexOf(",");
                String key = info.substring(0,divider);
                node = _myGraph.getNode(Integer.parseInt(key));
                path.add(node);
                info = info.substring(divider+1);
            }
            return path;
        }
        return null;
    }

    @Override
    public boolean save(String file) {
        JsonObject Json_obj = new JsonObject();
        JsonObject Jedge;
        JsonObject Jnode;
        JsonArray edges = new JsonArray();
        JsonArray nodes = new JsonArray();
        List<node_data> V = (List<node_data>) _myGraph.getV();
        for(node_data node : V)
        {
            Jnode = new JsonObject();
            geo_location GPos = node.getLocation();
            String pos = GPos.x()+","+ GPos.y()+","+ GPos.z();
            Jnode.addProperty("pos",pos);
            Jnode.addProperty("id",node.getKey());
            nodes.add(Jnode);
            List<edge_data> Es = (List<edge_data>) _myGraph.getE(node.getKey());
            for(edge_data edge : Es)
            {
                Jedge = new JsonObject();
                Jedge.addProperty("src", edge.getSrc());
                Jedge.addProperty("w", edge.getWeight());
                Jedge.addProperty("dest", edge.getDest());
                edges.add(Jedge);
            }
        }
        Json_obj.add("Edges",edges);
        Json_obj.add("Nodes",nodes);
        try
        {
            FileWriter writer = new FileWriter(file);
            writer.write(Json_obj.toString());
            writer.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean load(String file) {
        DWGraph_DS newGr = new DWGraph_DS();
        JsonObject json_obj;
        try
        {
            String Jstr = new String(Files.readAllBytes(Paths.get(file)));
            json_obj = JsonParser.parseString(Jstr).getAsJsonObject();
            JsonArray Jnodes = json_obj.getAsJsonArray("Nodes");
            for(JsonElement node : Jnodes)
            {
                JsonObject temp = (JsonObject) node;
                int id = temp.get("id").getAsInt();
                NodeData newNode = new NodeData(id);
                String pos = temp.get("pos").getAsString();
                int firstComma = pos.indexOf(",");
                int lastComma = pos.lastIndexOf(",");
                double x = Double.parseDouble(pos.substring(0,firstComma));
                double y = Double.parseDouble(pos.substring(firstComma+1,lastComma));
                double z = Double.parseDouble(pos.substring(lastComma+1));
                GeoLocation GL = new GeoLocation(x,y,z);
                newNode.setLocation(GL);
                newGr.addNode(newNode);
            }
            JsonArray Jedges = json_obj.getAsJsonArray("Edges");
            for(JsonElement edge : Jedges)
            {
                JsonObject temp = (JsonObject) edge;
                int src = temp.get("src").getAsInt();
                int dest = temp.get("dest").getAsInt();
                double weight = temp.get("w").getAsDouble();
                newGr.connect(src,dest,weight);
            }
            _myGraph = newGr;
            return true;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    NodeComparator _comp = new NodeComparator();

    private class NodeComparator implements Comparator<node_data> {

        @Override
        public int compare(node_data o1, node_data o2) {
            if((o1.getWeight() - o2.getWeight()) > 0)
            {
                return 1;
            }
            else if((o1.getWeight() - o2.getWeight()) < 0)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }


    public DWGraph_DS copy(directed_weighted_graph temp){
        DWGraph_DS newGr = new DWGraph_DS();
        List<node_data> nodes = (List<node_data>) temp.getV();
        for(Iterator<node_data> node = nodes.iterator(); node.hasNext();)
        {
            newGr.addNode(new NodeData(node.next().getKey()));
        }
        for(Iterator<node_data> node = nodes.iterator(); node.hasNext();)
        {
            List<edge_data> edges = (List<edge_data>) temp.getE(node.next().getKey());
            for(edge_data e : edges)
            {
                newGr.connect(e.getSrc(), e.getDest(), e.getWeight());
            }
        }
        return newGr;
    }



    private void setWeight(){
        List<node_data> nodes = (List<node_data>) _myGraph.getV();
        for(Iterator<node_data> node = nodes.iterator(); node.hasNext();)
        {
            node.next().setWeight(0);
        }
    }



    private void setTag(){
        List<node_data> nodes = (List<node_data>) _myGraph.getV();
        for(Iterator<node_data> node = nodes.iterator(); node.hasNext();)
        {
            node.next().setTag(0);
        }
    }




}
