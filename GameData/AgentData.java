package GameData;

import api.*;
import gameClient.CL_Agent;

public class AgentData implements Runnable {

    private CL_Agent _agent;
    private static game_service _gameServ;
    private static MoveData _move;

    public AgentData(CL_Agent agent, game_service game, MoveData mover){
        setAgent(agent);
        setGame(game);
        setMover(mover);
    }


    public void setAgent(CL_Agent agent){
        _agent = agent;
    }

    public void setGame(game_service game){
        _gameServ = game;
    }

    public void setMover(MoveData mover){
        _move = mover;
    }


    @Override
    public void run() {
        long dt = 0;
        while(_gameServ.isRunning()){
            dt = _move.init(_agent);
            try {
                Thread.sleep(dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


}
