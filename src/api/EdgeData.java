package api;

import org.json.JSONException;
import org.json.JSONObject;


public class EdgeData implements edge_data {

    private int _src;
    private int _dest;
    private double _weight;
    private String _info = "";
    private int _tag;


    /**
     *
     * @param src - the source {@link NodeData}
     * @param dest - the destination {@link NodeData}
     * @param weight - used to calculate distance
     */
    public EdgeData(int src, int dest, double weight){
        this(src,dest,weight,"",0);
    }

    /**
     *
     * @param src - the source {@link NodeData}
     * @param dest - the destination {@link NodeData}
     * @param weight - used to calculate distance
     * @param info - for algorithms use
     * @param tag - for algorithms use
     */

    public EdgeData(int src, int dest, double weight, String info, int tag){
        if(weight <= 0)
            throw new RuntimeException("Can't set negative weight ("+_weight+")");

        if(src == dest)
            throw new RuntimeException("Can't connect vertex to itself");

        this._src = src;
        this._dest = dest;
        this._weight = weight;
        this._info = info;
        this._tag = tag;

    }


    /**
     * Deep copy constructor
     * @param e - the EdgeData to copy
     */

    public EdgeData(edge_data e){
        this(e.getSrc(), e.getDest(), e.getWeight(), e.getInfo(), e.getTag());
    }



    public EdgeData(JSONObject jsonObject) throws JSONException{
        this._src = jsonObject.getInt("src");
        this._dest = jsonObject.getInt("dest");
        this._weight = jsonObject.getDouble("w");
    }

    /**
     *
     * @return - copy of this edge in the opposite direction
     */
    public EdgeData getReversEdge(){
        EdgeData e = new EdgeData(this);
        int temp = e._dest;
        e._dest = e._src;
        e._src = temp;
        return e;
    }


    @Override
    public String toString(){
        return "E("+_src+","+_dest+")";
    }



    public boolean equals(Object arg0){
        if(arg0==null || !(arg0 instanceof EdgeData))
            return false;
        EdgeData e = (EdgeData) arg0;
        return getSrc() == e.getSrc() && getDest() == e.getDest() && getWeight() == e.getWeight();
    }

    //////Getters and Setters///////

    @Override
    public int getSrc() {
        return this._src;
    }


    @Override
    public int getDest() {
        return this._dest;
    }

    @Override
    public double getWeight() {
        return this._weight;
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
}
