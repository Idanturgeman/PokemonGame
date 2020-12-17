package api;

public class EdgeData implements edge_data{

        private node_data _src;
        private node_data _dest;
        private double _weight;
        private int _tag = 0;
        private String _info;

        public EdgeData(node_data src, node_data dest, double weight) {
            setSrc(src);
            setDest(dest);
            setWeight(weight);
        }

///////////////////////Getters and Setters////////////////////////////////////////////////////////////////////////////
        @Override
        public int getSrc() {
            return _src.getKey();
        }

        @Override
        public int getDest() {
            return _dest.getKey();
        }

        @Override
        public double getWeight() {
            return _weight;
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

        public void setSrc(node_data src){
            _src = src;
        }

        public void setDest(node_data dest){
        _dest = dest;
       }

       public void setWeight(double weight){
            _weight = weight;
       }

    }

