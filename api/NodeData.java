package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class NodeData implements node_data{

    private HashMap<Integer, edge_data> _srcE = new HashMap<>();
    private HashMap<Integer, edge_data> _destE = new HashMap<>();
    private LinkedList<edge_data> _edges = new LinkedList<>();
    private LinkedList<edge_data> _edges2 = new LinkedList<>();

    private geo_location _geoLo = new GeoLocation(0,0,0);

    private int _key;
    private double _weight = 0;
    private int _tag = 0;
    private String _info;


    public NodeData(int key){
        setKey(key);
    }



    @Override
    public geo_location getLocation() {
        return _geoLo;
    }

    @Override
    public void setLocation(geo_location p) {
        _geoLo = p;
    }

    public boolean hasEdge(int dest){

        return _srcE.containsKey(dest);
    }

    public edge_data getEdge(int dest){
        if(_srcE.containsKey(dest))
        {
            return _srcE.get(dest);
        }
        return null;
    }


    public void addEdge(edge_data e){
        int dest = e.getDest();
        if(!_srcE.containsKey(dest))
        {
            _srcE.put(dest, e);
            _edges.add(e);
        }
    }


    public edge_data removeEdge(int dest){
        if(_srcE.containsKey(dest))
        {
            edge_data edge = _srcE.get(dest);
            _srcE.remove(dest);
            _edges.remove(edge);
            return edge;
        }
        return null;
    }


    public void ReversEdge(edge_data e){
        int src = e.getSrc();
        if(!_destE.containsKey(src))
        {
            _destE.put(src,e);
            _edges2.add(e);
        }
    }

    public void removeReversEdge(int src){
        if(_destE.containsKey(src))
        {
            edge_data edge = _destE.get(src);
            _destE.remove(src);
            _edges2.remove(edge);
        }
    }


    public Collection<edge_data> getEdgeCol(){

        return _edges;
    }

    public Collection<edge_data> getEdgeCol2(){

        return _edges2;
    }


    public String toString(){
        String info = "[" + _key + "]:";
        for(edge_data e : _edges)
        {
            info += " ["+e.getDest()+","+e.getWeight()+"]";
        }
        info +=". tag = " + _tag + ", ";
        info +="weight = " + _weight + ". ";
        info += _geoLo;
        return info;
    }


    /////////////////Getters and Setters/////////////////////////////////////////////////////////////////////////////
    @Override
    public int getKey() {
        return _key;
    }

    public void setKey(int key){
        _key = key;
    }

    @Override
    public double getWeight() {
        return _weight;
    }

    @Override
    public void setWeight(double w) {
        _weight = w;
    }

    @Override
    public String getInfo() {
        return _info;
    }

    @Override
    public void setInfo(String s) {
        _info = s;
    }

    @Override
    public int getTag() {
        return _tag;
    }

    @Override
    public void setTag(int t) {
        _tag = t;
    }


}
