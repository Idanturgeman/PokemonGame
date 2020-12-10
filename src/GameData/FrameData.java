package GameData;


import api.*;
import gameClient.Arena;
import gameClient.CL_Agent;
import gameClient.CL_Pokemon;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph - you are welcome to use this class - yet keep in mind
 * that the code is not well written in order to force you improve the
 * code and not to take it "as is".
 *
 */
public class FrameData extends JFrame implements ActionListener{
    private Arena _ar;
    private gameClient.util.Range2Range _w2f;
    private game_service _game;
    private Image graphImg;

    private static boolean _open = true;
    private static int _scenario = 0;
    private static int _id = -1;
    private static JComboBox _sceneNum;
    private static JButton _freePlay;
    private static JButton _loginButton;
    private static JTextField _userTxt;
    private static JFrame login;


    FrameData(String a, game_service game) {
        super(a);
        _game = game;
    }

    public void update(Arena ar) {
        this._ar = ar;
        updateFrame();
    }

    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-20);
        Range ry = new Range(this.getHeight()-10,150);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
    }
    public void paint(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        graphImg = this.createImage(w,h);
        Graphics graphics = graphImg.getGraphics();
        paintComponents(graphics);
        g.drawImage(graphImg,0,0,this);
        drawTimer(g);
        drawScore(g);
        updateFrame();
    }


    public void actionPerformed(ActionEvent e){
        if(e.getSource() == _sceneNum){
            int scenario = _sceneNum.getSelectedIndex();
            _scenario = scenario;
        }
        if(e.getSource() == _freePlay){
            _open = false;
        }
        if(e.getSource() == _loginButton){
            try{
                int id = Integer.parseInt(_userTxt.getText());
                if(id > 0){
                    _id = id;
                    _open = false;
                }
            }
            catch (Exception ex){
            }
        }
    }


    @Override
    public void paintComponents(Graphics g){
        drawPokemons(g);
        drawGraph(g);
        drawAgents(g);
        drawInfo(g);
        drawTimer(g);
        drawScore(g);
    }

    private void drawInfo(Graphics g) {
        List<String> str = _ar.get_info();
        String dt = "none";
        for(int i=0;i<str.size();i++) {
            g.drawString(str.get(i)+" dt: "+dt,100,60+i*20);
        }
    }

    private void drawTimer(Graphics g){
        g.setFont(new Font("Arial",Font.BOLD,36));
        int sec = (int) (_game.timeToEnd()/1000);
        int min = (int) (_game.timeToEnd()/60000);
        String time = min+":"+sec;
        g.drawString(time,20,70);
    }

    private void drawScore(Graphics g){
        List<CL_Agent> agents = _ar.getAgents();
        double score = 0;
        for(CL_Agent agent : agents){
            score += agent.getValue();
        }
        g.drawString(String.valueOf(score), this.getWidth()-100, 70);
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while(iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n,5,g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while(itr.hasNext()) {
                edge_data e = itr.next();
                g.setColor(Color.gray);
                drawEdge(e, g);
            }
        }
    }

    private void drawPokemons(Graphics g) {
        List<CL_Pokemon> fs = _ar.getPokemons();
        if(fs!=null) {
            Iterator<CL_Pokemon> itr = fs.iterator();
            while(itr.hasNext()) {
                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r=10;
                g.setColor(Color.green);
                if(f.getType()<0) {g.setColor(Color.orange);}
                if(c!=null) {
                    geo_location fp = this._w2f.world2frame(c);
                    g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                }
            }
        }
    }
    private void drawAgents(Graphics g) {
        List<CL_Agent> rs = _ar.getAgents();
        g.setColor(Color.red);
        int i=0;
        while(rs!=null && i<rs.size()) {
            geo_location c = rs.get(i).getLocation();
            int r=8;
            i++;
            if(c!=null) {
                geo_location fp = this._w2f.world2frame(c);
                g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
            }
        }
    }

    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*r);
    }

    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
    }

    public static void main(String[] a){
        //  loginPanel();
    }

    public static void loginPanel(){
        JPanel panel = new JPanel();
        login = new JFrame();
        login.setSize(330,160);
        panel.setLayout(null);
        // login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.add(panel);
        JLabel user = new JLabel("User:");
        user.setBounds(10,20,80,25);

        JLabel scene = new JLabel("Scenario:");
        scene.setBounds(10,50,80,25);

        _userTxt = new JTextField(20);
        _userTxt.setBounds(100,20,165,25);

        String scenes[] = new String[24];
        for(int i = 0; i < 24; i++){
            scenes[i] = String.valueOf(i);
        }
        _sceneNum = new JComboBox(scenes);
        _sceneNum.addActionListener(new FrameData());
        _sceneNum.setBounds(100,50,165,25);

        _freePlay = new JButton("Free Play");
        _freePlay.addActionListener(new FrameData());
        _freePlay.setBounds(10,80,120,30);

        _loginButton = new JButton("Login");
        _loginButton.addActionListener(new FrameData());
        _loginButton.setBounds(170,80,120,30);

        panel.add(user);
        panel.add(scene);
        panel.add(_userTxt);
        panel.add(_sceneNum);
        panel.add(_freePlay);
        panel.add(_loginButton);
        login.setVisible(true);
    }

    FrameData(){}



    public void dispose(){
        login.dispose();
    }

    public boolean isOpen() {
        return _open;
    }

    public int getID(){
        return _id;
    }

    public int getScenario(){
        return _scenario;
    }
}