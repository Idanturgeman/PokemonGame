package GameData;
import Algorithms.DWGraphs_Algo;
import api.*;
import gameClient.Arena;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;

import java.util.HashSet;
import java.util.List;

/** a class to manage the movement of agents, in order
 * to keep agent movements to minimum while keeping the
 * score as high as possible. the class contains "libraries"
 * in form of Hash data structures for every agent data.
 */
public class MoveData {

    private static Arena _ar;
    private CL_Agent _agent;
    private AgentData _agT;
    private CL_Pokemon _pokemon;
    private CL_Pokemon _prevPok;
    private edge_data _prevEdge;
    private static HashSet<String> _blackList;
    private static HashSet<String> _whiteList;
    private static directed_weighted_graph _graph;
    private static DWGraphs_Algo _graphAlgo;
    private static game_service _game;
    private static int _reset = 0;
    private static int _AC;

    /** constructor of mover. sets all the data from the agents */
    public MoveData(Arena ar, directed_weighted_graph graph, game_service game, int numOfAgents){
        _ar = ar;
        _graph = graph;
        _game = game;
        _AC = numOfAgents;
        _blackList = new HashSet<>();
        _graphAlgo = new DWGraphs_Algo(new DWGraph_DS());
        DWGraph_DS temp = _graphAlgo.copy(_graph);
        _graphAlgo.init(temp);
    }

    /** the central method of mover.
     *  moves the agents, check for congestions
     *  and check if an agent is stuck or without nodes.
     * @param agent the mover is currently working on.
     * @return the sleeping time of the agent.
     */
    public synchronized int init(AgentData thread, CL_Agent agent){
        _agent = agent;
        _agT = thread;
        _prevEdge = thread.getPrevEdge();
        _prevPok = thread.getPrevPok();
        _whiteList = thread.getWhiteList();
        int nextNode = moveAgents();
        if(_reset == 100* _AC){
            resetLists();
        }
        double slpTime = 100;
        try{
            slpTime = getTime(nextNode);
        }
        catch (NullPointerException ne){
            try{
                resetLists();
                slpTime = 0;
                if(nextNode(_agent.getSrcNode()) == -1){
                    wait();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return (int) slpTime;
    }

    /** calculate the sleeping time of the agents according to their targets.
     * @param nextNode the next node the agent is supposed to move to
     * @return the time the agent need to sleep
     */
    private long getTime(int nextNode){
        int currNode = _agent.getSrcNode();
        edge_data pokEdge = _pokemon.get_edge();
        edge_data currEdge = _graph.getEdge(currNode,nextNode);
        geo_location srcNodePos;
        geo_location destNodePos;
        double edgeDist;
        double weight = currEdge.getWeight();
        double speed = _agent.getSpeed();
        if((pokEdge.getDest() == currEdge.getDest()) && (pokEdge.getSrc() == currEdge.getSrc())){
            double dist2Pok = _agent.getLocation().distance(_pokemon.getLocation());
            srcNodePos = _graph.getNode(pokEdge.getSrc()).getLocation();
            destNodePos = _graph.getNode(pokEdge.getDest()).getLocation();
            edgeDist = srcNodePos.distance(destNodePos);
            double ratio = dist2Pok/edgeDist;
            weight = pokEdge.getWeight();
            weight *= ratio;
            _agT.setPrevEdge(currEdge);
            _agT.setPrevPok(_pokemon);
        }
        else if(_prevEdge != null && _prevEdge.getDest() == _prevPok.get_edge().getDest() && _prevEdge.getSrc() == _prevPok.get_edge().getSrc()){
            notifyAll();
            weight = _prevEdge.getWeight();
            currNode = _graph.getNode(_prevEdge.getSrc()).getKey();
            nextNode = _graph.getNode(_prevEdge.getDest()).getKey();
            srcNodePos = _graph.getNode(currNode).getLocation();
            destNodePos = _graph.getNode(nextNode).getLocation();
            edgeDist = srcNodePos.distance(destNodePos);
            double dist2Dest = destNodePos.distance(_agent.getLocation());
            double ratio = dist2Dest/edgeDist;
            weight *= ratio;
            _agT.setPrevEdge(_pokemon.get_edge());
        }
        weight *= 1000;
        double slpTime = weight/speed;
        slpTime++;
        return (long) slpTime;
    }

    /** communicate with the server to move and get
     * current position of elements on the graph.
     * @return the next node in the path of the agent
     */
    private int moveAgents() {
        String lg = _game.move();
        List<CL_Agent> log = Arena.getAgents(lg, _graph);
        _ar.setAgents(log);
        String fs = _game.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        _ar.setPokemons(ffs);
        _agent = log.get(_agent.getID());
        _agT.setAgent(_agent);
        int id = _agent.getID();
        int src = _agent.getSrcNode();
        double v = _agent.getValue();
        int dest = this.nextNode(src);
        _game.chooseNextEdge(_agent.getID(), dest);
        if(_agent.getNextNode() == -1){
            System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
        }
        return dest;
    }

    /** calculate the path of the agent.
     * @param src the starting node of the agent
     * @return the next node the agent's path
     */
    private int nextNode(int src) {
        int ans = -1;
        List<CL_Pokemon> pokes = _ar.getPokemons();
        int finalDest = findPkm(pokes, src);
        if(_agT.getFlag()){
            _agT.setFlag(false);
            for(int i = 0; i < _AC-1; i++){
                reserveNextPok(pokes, finalDest);
            }
            return finalDest;
        }
        List<node_data> path = _graphAlgo.shortestPath(src, finalDest);
        for(int i = 0; i < _AC-1; i++){
            reserveNextPok(pokes, finalDest);
        }
        if(path != null){
            path.remove(0);
            ans = path.get(0).getKey();
        }
        return ans;
    }

    /** finds the closet pokemon with the highest value and reserve it.
     * @param pokes a list of all current pokemon on the graph.
     * @param src the node that the agent is currently on.
     * @return the node on which the pokemon is on
     */
    private int findPkm(List<CL_Pokemon> pokes, int src) {
        int ans = -1;
        double minDist = Integer.MAX_VALUE;
        for (CL_Pokemon pok : pokes) {
            String pos = pok.getLocation().toString();
            if(!_blackList.contains(pos) || _whiteList.contains(pos)){
                edge_data edge = pok.get_edge();
                int edgeSrc = edge.getSrc();
                int edgeDest = edge.getDest();
                double value = edge.getWeight();
                if (edgeSrc == src) {
                    _agT.setFlag(true);
                    _pokemon = pok;
                    return edgeDest;
                }
                double dist = _graphAlgo.shortestPathDist(src,edgeSrc);
                if(dist != -1){
                    dist += edge.getWeight();
                    dist /= value;
                }
                if (dist < minDist && dist > 0) {
                    minDist = dist;
                    ans = edge.getSrc();
                    _pokemon = pok;
                }
            }
        }
        if(_pokemon != null){
            _blackList.add(_pokemon.getLocation().toString());
            _whiteList.add(_pokemon.getLocation().toString());
            _reset++;
        }
        return ans;
    }

    /** reserve another target for the agent closest
     *  to it's current target.
     * @param pokes list of all the pokemon currently on the graph.
     * @param src the node of the agent.
     */
    private void reserveNextPok(List<CL_Pokemon> pokes, int src) {
        double minDist = Integer.MAX_VALUE;
        CL_Pokemon pokemon = null;
        for (CL_Pokemon pok : pokes) {
            String pos = pok.getLocation().toString();
            if(!_blackList.contains(pos) || _whiteList.contains(pos)){
                edge_data edge = pok.get_edge();
                int edgeSrc = edge.getSrc();
                double value = edge.getWeight();
                double dist = _graphAlgo.shortestPathDist(src,edgeSrc);
                if(dist != -1){
                    dist += edge.getWeight();
                    dist /= value;
                }
                if (dist < minDist && dist > 0) {
                    minDist = dist;
                    pokemon = pok;
                }
            }
        }
        if(pokemon != null){
            _blackList.add(pokemon.getLocation().toString());
            _whiteList.add(pokemon.getLocation().toString());
            _reset++;
        }
    }

    private void resetLists(){
        _blackList.clear();
        _reset = 0;
    }
}