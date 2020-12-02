package api;

import java.util.Date;

import Server.Game_Server;
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

import org.json.JSONException;
import org.json.JSONObject;

public class GameService implements game_service{

    public static final long SEED = 3331L;
    private Stages _stages = Stages.getStages_game_Ex4();
    private oop_graph _graph;
    private ArrayList<fruits> _fruits;
    private ArrayList<robot> _robot;
    private ArrayList<String> _log;
    private String _curr_log;
    private long _time_out;
    private long _start_time;
    private String data;
    private static boolean _running = false;
    private static int _game_level;
    private static int _id;
    private static int _max_level = -1;
    private static boolean _is_logged_in = false;
    private static game_service _instance = null;
    private double _grade;
    private int _robots_number;
    private int _number_of_moves;
    private int _fruits_number;
    private long _seed = 3331L;
    private Random _rand;

    private GameService() {
    }


    private GameService(String file_graph, int robot_num, int fruit_num, long time, long seed, int g) throws JSONException {
        this.data = file_graph;
        this._graph = new DWGraph_DS(file_graph);
        this._grade = 0.0D;
        this._robots_number = robot_num;
        this._fruits_number = fruit_num;
        this._seed = seed;
        this._rand = new Random(this._seed);
        this._time_out = time;
        this._start_time = -1L;
        this._curr_log = "" + g;
        this._fruits = new ArrayList();

        for(int i = 0; i < this._fruits_number; ++i) {
            this._fruits.add(this.randomFruit());
        }

        this._robot = new ArrayList();
    }


    public static game_service getServer(int g) {
        if (_instance != null && _instance.isRunning()) {
            System.err.println("The Server is still running!!");
            return null;
        } else if (_max_level >= 0 && g > _max_level) {
            throw new RuntimeException("ERR: you are trying to play in a level above yours (" + _max_level + ")");
        } else {
            long time;
            if (g >= 0 && g < 24) {
                _game_level = g;
                int gr = g / 4;
                int ro = 1;
                int fr = 1 + g % 6;
                if (g > 10) {
                    ro = 1 + g % 3;
                }

                time = 30000L;
                if (g % 2 == 1) {
                    time *= 2L;
                }

                _instance = new GameService("data/A" + gr, ro, fr, time, 3331L, g);
            }

            byte gr;
            byte ro;
            byte fr;
            if (g == -1) {
                _game_level = g;
                gr = 0;
                ro = 2;
                fr = 2;
                time = 600000L;
                _instance = new GameService("data/A" + gr, ro, fr, time, 3331L, g);
            }

            if (g == -31) {
                _game_level = g;
                gr = 5;
                ro = 10;
                fr = 21;
                time = 45000L;
                _instance = new GameService("data/A" + gr, ro, fr, time, 3331L, g);
            }

            if (g == -331) {
                _game_level = g;
                gr = 4;
                ro = 6;
                fr = 4;
                time = 45000L;
                _instance = new GameService("data/A" + gr, ro, fr, time, 3331L, g);
            }

            return _instance;
        }
    }

    public String toString() {
        return this.toJSON();
    }

    public String toJSON() {
        String ans = null;
        JSONObject res = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put("graph", this.data);
            data.put("fruits", this._fruits_number);
            data.put("grade", this._grade);
            data.put("moves", this._number_of_moves);
            data.put("robots", this._robots_number);
            data.put("game_level", _game_level);
            data.put("is_logged_in", _is_logged_in);
            data.put("max_user_level", _max_level);
            res.put("GameServer", data);
            ans = res.toString();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return ans;
    }

    private void play(agent r) {
        double dx = 0.1D;
        dx /= 100.0D;
        int i = 0;
        int rf = 0;

        while(i < this._Pokemons.size()) {
            fruits f = (pokemon_price)this._pokemons.get(i);
            double v = f.grap(r, dx);
            if (v > 0.0D) {
                r.addMoney(v);
                this._pokemons.remove(i);
                ++rf;
            } else {
                ++i;
            }
        }

        while(rf > 0) {
            this._pokemons.add(this.randomFruit());
            --rf;
        }

    }


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
