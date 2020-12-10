package api;

public class GeoLocation implements geo_location {

    private double _x;
    private double _y;
    private double _z;

    public GeoLocation(double x, double y, double z){
        setX(x);
        setY(y);
        setZ(z);
    }

    public void setX(double x){
        _x = x;
    }

    public void setY(double y){
        _y = y;
    }

    public void setZ(double z){
        _z = z;
    }

    @Override
    public double x() {
        return _x;
    }

    @Override
    public double y() {
        return _y;
    }

    @Override
    public double z() {
        return _z;
    }

    @Override
    public double distance(geo_location g) {
        double dist = Math.sqrt(Math.pow((g.x() - _x),2) + Math.pow((g.y() - _y), 2) + Math.pow((g.z() - _z), 2));
        return dist;
    }

    public String toString(){
        String info = _x + "," + _y + "," + _z;
        return info;
    }
}
