package gameClient;

import api.GeoLocation;
import api.edge_data;
import api.geo_location;
import gameClient.util.Point3D;
import org.json.JSONObject;

public class CL_Pokemon implements Comparable<CL_Pokemon>{
	private edge_data _edge;
	private double _value;
	private int _type;
	private geo_location _pos;
	private double min_dist;
	private int min_ro;
	
	public CL_Pokemon(geo_location p, int t, double v, double s, edge_data e) {
		_type = t;
	//	_speed = s;
		_value = v;
		set_edge(e);
		_pos = p;
		min_dist = -1;
		min_ro = -1;
	}

	public CL_Pokemon(double v, geo_location p, edge_data e) {
		this._value = v;
		this._pos = new GeoLocation(p);
		this._edge = e;
	}

	public static CL_Pokemon init_from_json(String json) {
		CL_Pokemon ans = null;
		try {
			JSONObject p = new JSONObject(json);
			int id = p.getInt("id");

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return ans;
	}
	public String toString() {return "F:{v="+_value+", t="+_type+"}";}
	public edge_data get_edge() {
		return _edge;
	}

	public void set_edge(edge_data _edge) {
		this._edge = _edge;
	}

	public geo_location getLocation() {
		return _pos;
	}
	public int getType() {return _type;}
//	public double getSpeed() {return _speed;}
	public double getValue() {return _value;}

	public double getMin_dist() {
		return min_dist;
	}

	public void setMin_dist(double mid_dist) {
		this.min_dist = mid_dist;
	}

	public int getMin_ro() {
		return min_ro;
	}

	public void setMin_ro(int min_ro) {
		this.min_ro = min_ro;
	}




	public double grap(CL_Agent r, double dist) {
		double ans = 0.0D;
		if (this._edge != null && r != null) {
			int d = r.getNextNode();
			if (this._edge.getDest() == d) {
				geo_location rp = r.getLocation();
				if (dist > rp.distance2D(this._pos)) {
					ans = this._value;
				}
			}
		}

		return ans;
	}


	@Override
	public int compareTo(CL_Pokemon p) {
		Double v = this._value;
		return v.compareTo(p._value);
	}

    public static class CL_Pokemon {
        private edge_data _edge;
        private double _value;
        private int _type;
        private Point3D _pos;
        private double min_dist;
        private int min_ro;

        public CL_Pokemon(Point3D p, int t, double v, double s, edge_data e) {
            _type = t;
            //	_speed = s;
            _value = v;
            set_edge(e);
            _pos = p;
            min_dist = -1;
            min_ro = -1;
        }
        public static gameClient.CL_Pokemon init_from_json(String json) {
            gameClient.CL_Pokemon ans = null;
            try {
                JSONObject p = new JSONObject(json);
                int id = p.getInt("id");

            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return ans;
        }
        public String toString() {return "F:{v="+_value+", t="+_type+"}";}
        public edge_data get_edge() {
            return _edge;
        }

        public void set_edge(edge_data _edge) {
            this._edge = _edge;
        }

        public Point3D getLocation() {
            return _pos;
        }
        public int getType() {return _type;}
        //	public double getSpeed() {return _speed;}
        public double getValue() {return _value;}

        public double getMin_dist() {
            return min_dist;
        }

        public void setMin_dist(double mid_dist) {
            this.min_dist = mid_dist;
        }

        public int getMin_ro() {
            return min_ro;
        }

        public void setMin_ro(int min_ro) {
            this.min_ro = min_ro;
        }
    }
}
