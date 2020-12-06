package api;

public class GeoLocation implements geo_location {

    private double _x;
    private double _y;
    private double _z;

    public GeoLocation(double x, double y, double z){
        this._x = x;
        this._y = y;
        this._z = z;
    }

    public GeoLocation(geo_location g){
        this(g.x(), g.y(), g.z());
    }

    public GeoLocation(double x, double y){
        this(x, y, 0);
    }

    public GeoLocation(String s) {
        try {
            String[] a = s.split(",");
            _x = Double.parseDouble(a[0]);
            _y = Double.parseDouble(a[1]);
            _z = Double.parseDouble(a[2]);
        }
        catch(IllegalArgumentException e) {
            System.err.println("ERR: got wrong format string for GeoLocation init, got:"+s+"  should be of format: x,y,x");
            throw(e);
        }
    }

    @Override
    public double x() {
        return this._x;
    }

    @Override
    public double y() {
        return this._y;
    }

    @Override
    public double z() {
        return this._z;
    }

    @Override
    public double distance(geo_location g) {
        double dx = this.x() - g.x();
        double dy = this.y() - g.y();
        double dz = this.z() - g.z();
        double temp = (dx*dx*dy*dy*dz*dz);
        return Math.sqrt(temp);
    }


    public double distance2D(geo_location g2)
    {
        double dx = this.x() - g2.x();
        double dy = this.y() - g2.y();
        double t = (dx*dx+dy*dy);
        return Math.sqrt(t);
    }




    public String toString(){
        return _x+","+_y+","+_z;
    }

    public boolean equals(Object g){
        if (g==null || !(g instanceof GeoLocation))
            return false;
        GeoLocation g2 = (GeoLocation) g;
        return _x==g2._x && _y==g2._y && _z==g2._z;
    }

    public boolean equalsXY(GeoLocation g){
        return g._x == this._x && g._y == this._y;
    }
}
