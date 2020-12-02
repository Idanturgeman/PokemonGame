package api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import api.GeoLocation;
public class NodeData extends HashMap<Integer, edge_data> implements node_data , Comparable<node_data> {

    private int _key;
    private GeoLocation _location;
    private double _weight;
    private String _info = "";
    private int _tag;// for algorithms
    private NodeData father;// for shortest path algo

    /**
     *
     * @param key - the node number
     * @param location - for GUI
     * @param weight - for algorithms
     * @param info - for algorithms
     * @param tag - for algorithms
     */
    public NodeData(int key, GeoLocation location, double weight, String info, int tag){
        this._key = key;
        this._location = new GeoLocation(location);
        this._weight = weight;
        this._info = info;
        this._tag = tag;
    }


    /**
     *
     * @param key - the node number
     * @param location - for GUI
     * @param weight - for algorithms
     */
    public NodeData(int key, GeoLocation location, double weight){
        this._key = key;
        this._location = new GeoLocation(location);
        this._weight = weight;
    }

    /**
     *
     * @param key - the node number
     * @param location - for GUI
     */

    public NodeData(int key, GeoLocation location){
        this._key = key;
        this._location = new GeoLocation(location);
    }

    /**
     * Deep copy constructor
     * @param n - the orgNode to copy
     */
    public NodeData(node_data n){
        this(n.getKey(), (GeoLocation) n.getLocation(), n.getWeight(), n.getInfo(), n.getTag());
    }


    public NodeData(JSONObject jsonObject) throws JSONException {
        this._key = jsonObject.getInt("id");
        String[] s = jsonObject.getString("pos").split(",");
        Double x = Double.parseDouble(s[0]);
        Double y = Double.parseDouble(s[1]);
        this._location = new GeoLocation(x,y);
    }

    @Override
    public int getKey() {
        return this._key;
    }

    @Override
    public geo_location getLocation() {
        return this._location;
    }

    @Override
    public void setLocation(geo_location p) {

        this._location = (GeoLocation) p;
    }

    @Override
    public double getWeight() {
        return this._weight;
    }

    @Override
    public void setWeight(double w) {

        this._weight = w;
    }

    @Override
    public String getInfo() {
        return this._info;
    }

    @Override
    public void setInfo(String s) {

        this._info = s;
    }

    @Override
    public int getTag() {
        return this._tag;
    }

    @Override
    public void setTag(int t) {

        this._tag = t;
    }

    @Override
    public int compareTo( node_data o) {
        Double comp = getWeight();
        return comp.compareTo(o.getWeight());
    }


    @Override
    public String toString(){
        return ""+this.getKey();
    }


   /* *//** return a string that hold all the information of the node.*//*
    public String toString() {
        String data = "[" + _key + "]:";

        for (edge_data ni : this.values())
        {
            data += " [" + ni.getDest() + "," + this.get(ni) + "]";
        }
        data += ". tag = " + _tag + ".";
        return data;
    }*/
    /**
     *
     * for algorithms such as "shortest path", to trace back the path
     */
    public NodeData getFather(){
        return this.father;
    }

    /**
     *
     * for algorithms such as "shortest path", to trace back the path
     */

    public void setFather(node_data f){
        this.father = (NodeData) f;
    }

    @Override
    public boolean equals(Object arg0){
        if (arg0 == null || !(arg0 instanceof NodeData))
            return false;
        NodeData n = (NodeData) arg0;
        return n.getKey() == this.getKey() && n.getLocation() == this.getLocation() && super.equals(arg0);
    }







}
