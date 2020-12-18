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

    private static HashSet<String> _firstList;
    private static HashSet<String> _secondList;

    private CL_Pokemon _pokemon;
    private CL_Pokemon priorPokemon;
    private edge_data priorEdge;
    private static Arena arena;
    private CL_Agent _agent;
    private AgentData agentData;

    private static directed_weighted_graph _graph;
    private static DWGraphs_Algo _graphAlgo;
    private static game_service gameService;

    private static int z = 0;
    private static int check;

    private void listToZero(){
        _firstList.clear();
        z = 0;
    }



    public MoveData(Arena ar, directed_weighted_graph graph, game_service game, int numOfAgents){
        arena = ar;
        setGraph(graph);
        setGameService(game);
        check = numOfAgents;
        _firstList = new HashSet<>();
        _graphAlgo = new DWGraphs_Algo(new DWGraph_DS());
        DWGraph_DS temp = _graphAlgo.copy(_graph);
        _graphAlgo.init(temp);
    }


    /** calculate the time of the agents according to their targets.
     * @param nextNode the next node the agent is supposed to move to
     * @return the time the agent need to sleep
     */
    private long checkOnClock(int nextNode){
        int currNode = _agent.getSrcNode();
        edge_data pokEdge = _pokemon.get_edge();
        edge_data currEdge = _graph.getEdge(currNode,nextNode);
        geo_location srcNodePos;
        geo_location destNodePos;
        double edgeDist;
        double weight = currEdge.getWeight();
        double speed = _agent.getSpeed();
        if((pokEdge.getDest() == currEdge.getDest()) && (pokEdge.getSrc() == currEdge.getSrc()))
        {
            double dist2Pok = _agent.getLocation().distance(_pokemon.getLocation());
            srcNodePos = _graph.getNode(pokEdge.getSrc()).getLocation();
            destNodePos = _graph.getNode(pokEdge.getDest()).getLocation();
            edgeDist = srcNodePos.distance(destNodePos);
            double ratio = dist2Pok/edgeDist;
            weight = pokEdge.getWeight();
            weight *= ratio;
            agentData.setPriorEdge(currEdge);
            agentData.setPriorPokemon(_pokemon);
        }
        else if(priorEdge != null && priorEdge.getDest() == priorPokemon.get_edge().getDest() && priorEdge.getSrc() == priorPokemon.get_edge().getSrc())
        {
            notifyAll();
            weight = priorEdge.getWeight();
            currNode = _graph.getNode(priorEdge.getSrc()).getKey();
            nextNode = _graph.getNode(priorEdge.getDest()).getKey();
            srcNodePos = _graph.getNode(currNode).getLocation();
            destNodePos = _graph.getNode(nextNode).getLocation();
            edgeDist = srcNodePos.distance(destNodePos);
            double dist2Dest = destNodePos.distance(_agent.getLocation());
            double ratio = dist2Dest/edgeDist;
            weight *= ratio;
            agentData.setPriorEdge(_pokemon.get_edge());
        }
        weight *= 1000;
        double slpTime = weight/speed;
        slpTime++;
        return (long) slpTime;
    }


    /** the central method of mover.
     * @param agent the mover is currently working on.
     * @return the sleeping time of the agent.
     */
    public synchronized int init(AgentData thread, CL_Agent agent){
        setAgentData(thread);
        setAgent(agent);
        priorEdge = thread.getPriorEdge();
        priorPokemon = thread.getPriorPokemon();
        _secondList = thread.getList();
        int nextNode = moveAgents();
        if(z == 100* check)
        {
            listToZero();
        }
        double slpTime = 100;
        try
        {
            slpTime = checkOnClock(nextNode);
        }
        catch (NullPointerException ne)
        {
            try
            {
                listToZero();
                slpTime = 0;
                if(nextNode(_agent.getSrcNode()) == -1)
                {
                    wait();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return (int) slpTime;
    }

    /** communicate with the server to move and get
     * current position of elements on the graph.
     * @return the next node in the path of the agent
     */
    private int moveAgents() {
        String lg = gameService.move();
        List<CL_Agent> log = Arena.getAgents(lg, _graph);
        arena.setAgents(log);
        String fs = gameService.getPokemons();
        List<CL_Pokemon> ffs = Arena.json2Pokemons(fs);
        arena.setPokemons(ffs);
        _agent = log.get(_agent.getID());
        agentData.setAgent(_agent);
        int id = _agent.getID();
        int src = _agent.getSrcNode();
        double v = _agent.getValue();
        int dest = this.nextNode(src);
        gameService.chooseNextEdge(_agent.getID(), dest);
        if(_agent.getNextNode() == -1)
        {
            System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
        }
        return dest;
    }



    /** finds the closet pokemon with the highest value and reserve it.
     * @param pokes a list of all current pokemon on the graph.
     * @param src the node that the agent is currently on.
     * @return the node on which the pokemon is on
     */
    private int checkPokemon(List<CL_Pokemon> pokes, int src) {
        int ans = -1;
        double minDist = Integer.MAX_VALUE;
        for (CL_Pokemon pok : pokes)
        {
            String pos = pok.getLocation().toString();
            if(!_firstList.contains(pos) || _secondList.contains(pos))
            {
                edge_data edge = pok.get_edge();
                int edgeSrc = edge.getSrc();
                int edgeDest = edge.getDest();
                double value = edge.getWeight();
                if (edgeSrc == src)
                {
                    agentData.setFlag(true);
                    _pokemon = pok;
                    return edgeDest;
                }
                double dist = _graphAlgo.shortestPathDist(src,edgeSrc);
                if(dist != -1)
                {
                    dist += edge.getWeight();
                    dist /= value;
                }
                if (dist < minDist && dist > 0)
                {
                    minDist = dist;
                    ans = edge.getSrc();
                    _pokemon = pok;
                }
            }
        }
        if(_pokemon != null)
        {
            _firstList.add(_pokemon.getLocation().toString());
            _secondList.add(_pokemon.getLocation().toString());
            z++;
        }
        return ans;
    }

    /** reserve another target for the agent closest
     *  to it's current target.
     * @param pokes list of all the pokemon currently on the graph.
     * @param src the node of the agent.
     */
    private void findNextPokemon(List<CL_Pokemon> pokes, int src) {
        double minDist = Integer.MAX_VALUE;
        CL_Pokemon pokemon = null;
        for (CL_Pokemon pok : pokes)
        {
            String pos = pok.getLocation().toString();
            if(!_firstList.contains(pos) || _secondList.contains(pos))
            {
                edge_data edge = pok.get_edge();
                int edgeSrc = edge.getSrc();
                double value = edge.getWeight();
                double dist = _graphAlgo.shortestPathDist(src,edgeSrc);
                if(dist != -1)
                {
                    dist += edge.getWeight();
                    dist /= value;
                }
                if (dist < minDist && dist > 0)
                {
                    minDist = dist;
                    pokemon = pok;
                }
            }
        }
        if(pokemon != null)
        {
            _firstList.add(pokemon.getLocation().toString());
            _secondList.add(pokemon.getLocation().toString());
            z++;
        }
    }

    /** calculate the path of the agent.
     * @param src the starting node of the agent
     * @return the next node the agent's path
     */
    private int nextNode(int src) {
        int ans = -1;
        List<CL_Pokemon> pokes = arena.getPokemons();
        int finalDest = checkPokemon(pokes, src);
        if(agentData.getFlag())
        {
            agentData.setFlag(false);
            for(int i = 0; i < check -1; i++)
            {
                findNextPokemon(pokes, finalDest);
            }
            return finalDest;
        }
        List<node_data> path = _graphAlgo.shortestPath(src, finalDest);
        for(int i = 0; i < check -1; i++)
        {
            findNextPokemon(pokes, finalDest);
        }
        if(path != null)
        {
            path.remove(0);
            ans = path.get(0).getKey();
        }
        return ans;
    }



/////////////////////////////////Getters and Setters/////////////////////////////////////////////////////////

    public void setAgent(CL_Agent agent){
        _agent = agent;
    }

    public void setAgentData(AgentData agentData){
        this.agentData = agentData;
    }

    public void setPokemon(CL_Pokemon pokemon){
        _pokemon = pokemon;
    }

    public void setPriorPokemon(CL_Pokemon pokemon){
        priorPokemon = pokemon;
    }
    public void setPriorEdge(edge_data edge){
        priorEdge = edge;
    }

    public void setGraph(directed_weighted_graph graph){
        _graph = graph;
    }

    public void setAlgoGraph(DWGraphs_Algo graph){
        _graphAlgo = graph;
    }

    public void setGameService(game_service game){
        gameService = game;
    }

}