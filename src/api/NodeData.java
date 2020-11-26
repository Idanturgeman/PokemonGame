package api;

public class NodeData implements node_data {

    private int _key;
    private static int Countkey = 0;
    private geo_location _location;
    private double _weight;
    private String _info = "";
    private int _tag;

    public NodeData(){
        this._key = Countkey++;
        this._location = null;
        this._weight = 0;
        this._info = "";
        this._tag = 0;
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

        this._location = p;
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
}
