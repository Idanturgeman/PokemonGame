package GameData;

import api.*;
import gameClient.CL_Agent;

public class AgentData implements Runnable {

    private CL_Agent _agent;
    private static game_service _game;
    private static moveData _mover;

    public AgentData(CL_Agent agent, game_service game, moveData mover){
        setAgent(agent);
        setGame(game);
        setMover(mover);
    }


    public void setAgent(CL_Agent agent){
        _agent = agent;
    }

    public void setGame(game_service game){
        _game = game;
    }

    public void setMover(moveData mover){
        _mover = mover;
    }


    @Override
    public void run() {
        long dt = 0;
        while(_game.isRunning()){
            dt = _mover.init(_agent);
            try {
                Thread.sleep(dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


}
