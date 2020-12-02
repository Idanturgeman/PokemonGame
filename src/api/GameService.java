package api;

import java.util.Date;

import Server.Stage;
import api.directed_weighted_graph;
import api.edge_data;
import api.game_service;
import api.geo_location;
import gameClient.util.Point3D;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;

public class GameService implements game_service{
    @Override
    public String getGraph() {
        return this._graph.toString();
    }

    @Override
    public String getPokemons() {
        ArrayList<String> ans = new ArrayList();

        for(int i = 0; i < this._pokemons.size(); ++i) {
            ans.add(((pokemon)this._pokemons.get(i)).toString());
        }

        return ans;
    }

    @Override
    public String getAgents() {
        String ans = "";

        for(int i = 0; i < this._agents.size(); ++i) {
            ans += ""+((agent)this._agents.get(i)).toString();
        }

        return ans;
    }

    @Override
    public boolean addAgent(int start_node) {
        boolean ans = false;
        if (this._agents.size() < this._agents_number && this._graph.getNode(start_node) != null) {
            AgentG r = new AgentG(this._graph, start_node);
            this._agents.add(r);
            ans = true;
        }

        return ans;
    }

    @Override
    public long startGame() {
        long ans = -1L;
        if (this._agents_number == this._agents.size() && !this.isRunning()) {
            _running = true;
            this._start_time = (new Date()).getTime();
            ans = this._start_time;
            this._grade = 0.0D;
            this._curr_log = ans + "," + this._curr_log;
        }

        return ans;
    }

    @Override
    public boolean isRunning() {
        boolean c = _running;
        long tt = this.timeToEnd();
        if (c && !_running) {
        }

        return _running;
    }

    @Override
    public long stopGame() {
        this._curr_log = this._curr_log + "," + this._number_of_moves + "," + this._grade;
        int grade = (int)this._grade;
        _running = false;
        if (_is_logged_in) {
            DB_Write.writeRes(_id, _game_level, this._number_of_moves, grade);
            if (_game_level == _max_level) {
                Stage st = new Stage(_game_level, this._number_of_moves, grade);
                this._stages.setCurr(_max_level);
                int ml = this._stages.testNext(st);
                if (ml > _max_level) {
                    _max_level = ml;
                    DB_Write.updateMaxLevel(_id, _max_level);
                }
            }
        }

        this._start_time = -1L;
        return (new Date()).getTime();
    }

    @Override
    public long chooseNextEdge(int id, int next_node) {
        long ans = -1L;
        if (!this.isRunning()) {
            return ans;
        } else {
            for(int i = 0; i < this._agents.size(); ++i) {
                String c = (agent)this._agents.get(i);
                if (c.getID() == id && !c.isMoving()) {
                    oop_edge_data e = this._graph.getEdge(c.getSrcNode(), next_node);
                    if (e != null) {
                        c.setNextNode(next_node);
                        ans = (new Date()).getTime();
                    }
                }
            }

            return ans;
        }
    }

    @Override
    public long timeToEnd() {
        if (!_running) {
            return -1L;
        } else {
            long now = (new Date()).getTime();
            long dt = now - this._start_time;
            long tt = this._time_out - dt;
            if (tt <= 0L) {
                long n = this.stopGame();
                tt = -1L;
            }

            return tt;
        }
    }

    @Override
    public String move() {
        if (!this.isRunning()) {
            return null;
        } else {
            ++this._number_of_moves;
            double g = 0.0D;

            for(int i = 0; i < this._agents.size(); ++i) {
                agent c = (robot)this._agents.get(i);
                c.move();
                this.play(c);
                g += c.getMoney();
            }

            this._grade = g;
            return this.getAgents();
        }
    }

    @Override
    public boolean login(long id) {
        if (!_running) {
            try {
                _max_level = DB_Write.login(id);
                if (_max_level >= 0) {
                    _is_logged_in = true;
                    _id = id;
                    _instance = null;
                }
            } catch (Exception var2) {
                var2.printStackTrace();
                _is_logged_in = false;
                _max_level = -1;
            }
        }

        return _is_logged_in;
    }
}
