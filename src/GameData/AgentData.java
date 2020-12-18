package GameData;

import api.*;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;

import java.util.Collection;
import java.util.HashSet;

/** the class is Runnable object for CL_Agent class.
 *  calls it's specific agent to move according to it's
 *  position and sleep until the agent is calculated to
 *  reach is goal. runs until the game ends
 */
class AgentData implements Runnable {

    private Collection<String> list;

    private static game_service gameService;
    private static MoveData _mover;
    private CL_Agent _agent;
    private CL_Pokemon pokemon;
    private edge_data edgeData;


    private boolean check = false;
    private int z = 0;

    /** agent's constructor. initialize all variables
     * @param agent the CL_Agent that the Agent is managing
     * @param game the main game service.
     * @param mover the agent moving algorithm.
     *
     */
    public AgentData(CL_Agent agent, game_service game, MoveData mover){
        setAgent(agent);
        setGameService(game);
        setMover(mover);
        list = new HashSet<>();
    }

    /** get the sleeping time from mover and wait
     *  to move the agent again.
     */
    @Override
    public void run() {
        long dt;
        while(gameService.isRunning())
        {
            dt = _mover.init(this, _agent);
            z++;
            if(z == 100)
            {
                list.clear();
            }
            try
            {
                Thread.sleep(dt);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
///////////////////////////Getters and Setters////////////////////////////////////////////////////////////////

    public void setAgent(CL_Agent agent) {
        _agent = agent;
    }

    public void setGameService(game_service game){
        gameService = game;
    }

    public void setMover(MoveData move){
        _mover = move;
    }

    public CL_Pokemon getPriorPokemon() {
        return pokemon;
    }


    public void setPriorPokemon(CL_Pokemon _prevPok) {
        this.pokemon = _prevPok;
    }


    public edge_data getPriorEdge() {
        return edgeData;
    }




    public boolean getFlag() {
        return check;
    }


    public void setFlag(boolean flag) {
        this.check = flag;
    }


    public void setPriorEdge(edge_data _prevEdge) {
        this.edgeData = _prevEdge;
    }


    public Collection<String> getList() {
        return list;
    }

}