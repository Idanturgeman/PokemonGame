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

    private Arena arena;
    private gameClient.util.Range2Range range;
    private game_service gameService;
    private Image image;
    private int scenario;


    private static boolean check = true;
    private static int _scenarioOfLis = 0;
    private static long _id = -1;


    private static JTextField _text;
    private static JFrame login;
    private static JComboBox _numBox;
    private static JButton _playButton;
    private static JButton _loginButton;

    public FrameData() {

    }

    public static void frameData(){
        JPanel panel = new JPanel();
        login = new JFrame();
        login.setSize(302,142);
        panel.setLayout(null);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.add(panel);
        JLabel user = new JLabel("User:");
        user.setBounds(12,22,82,27);

        JLabel scene = new JLabel("Scenario:");
        scene.setBounds(12,52,82,27);

        _text = new JTextField(22);
        _text.setBounds(102,22,162,27);

        String scenes[] = new String[24];
        for(int i = 0; i < 24; i++)
        {
            scenes[i] = String.valueOf(i);
        }
        _numBox = new JComboBox(scenes);
        _numBox.addActionListener(new FrameData());
        _numBox.setBounds(102,52,162,27);

        _playButton = new JButton("Free Play");
        _playButton.addActionListener(new FrameData());
        _playButton.setBounds(12,82,122,32);

        _loginButton = new JButton("Login");
        _loginButton.addActionListener(new FrameData());
        _loginButton.setBounds(172,82,122,32);

        panel.add(user);
        panel.add(scene);
        panel.add(_text);
        panel.add(_numBox);
        panel.add(_playButton);
        panel.add(_loginButton);
        login.setVisible(true);
    }

    /** a constructor fpr initializing the frame variables.
     * @param a the name of the frame.
     * @param game the game service object.
     * @param level the current level.
     */
    FrameData(String a, game_service game, int level) {
        super(a);
        setGameService(game);
        setScenario(level);
    }

    public void dispose(){
        login.dispose();
    }

    public boolean isOpen() {
        return check;
    }


    public void actionPerformed(ActionEvent e){
        if(e.getSource() == _numBox)
        {
            int scenario = _numBox.getSelectedIndex();
            _scenarioOfLis = scenario;
        }
        if(e.getSource() == _playButton)
        {
            check = false;
        }
        if(e.getSource() == _loginButton)
        {
            try
            {
                long id = Integer.parseInt(_text.getText());
                if(id > 0)
                {
                    _id = id;
                    check = false;
                }
            }
            catch (Exception ex){
            }
        }
    }




    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-20);
        Range ry = new Range(this.getHeight()-10,150);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = arena.getGraph();
        range = Arena.w2f(g,frame);
    }


    /** paints all the game components on the games GUI
     * @param g current graphics object.
     */
    @Override
    public void paintComponents(Graphics g){
        drawGraph(g);
        drawAgents(g);
        drawPokemons(g);
        drawClock(g);
        drawResult(g);
        drawInfo(g);
    }


    /** paints the game on the frame.
     *  draws, redraws all the game elements in the current states.
     * @param g the main graphics object.
     */
    public void paint(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        image = this.createImage(w,h);
        Graphics graphics = image.getGraphics();
        paintComponents(graphics);
        g.drawImage(image,0,0,this);
        updateFrame();
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while(iter.hasNext())
        {
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n,5,g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while(itr.hasNext())
            {
                edge_data e = itr.next();
                g.setColor(Color.gray);
                drawEdge(e, g);
            }
        }
    }

    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = arena.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this.range.world2frame(s);
        geo_location d0 = this.range.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
    }


    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this.range.world2frame(pos);
        g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*r);
    }


    private void drawInfo(Graphics g) {
        g.setFont(new Font("Arial",Font.BOLD,36));
        g.drawString("Level: "+ scenario,this.getWidth()/2 - 40, 70);
    }


    /** draws the agents' total score,
     * in addition of the score of each individual agent.
     */
    private void drawResult(Graphics g){
        List<CL_Agent> agents = arena.getAgents();
        double totalScore = 0;
        double score;
        for(CL_Agent agent : agents)
        {
            totalScore += agent.getValue();
            score = agent.getValue();
            g.setFont(new Font("Arial",Font.BOLD,16));
            g.drawString("agent "+agent.getID()+": "+String.valueOf(score), this.getWidth()-125, 90+20*agent.getID());
        }
        g.setFont(new Font("Arial",Font.BOLD,36));
        g.drawString(String.valueOf(totalScore), this.getWidth()-100, 70);
    }


    private void drawClock(Graphics g){
        g.setFont(new Font("Arial",Font.BOLD,36));
        int sec = (int) (gameService.timeToEnd()/1000);
        int min = (int) (gameService.timeToEnd()/60000);
        String time = min+":"+sec;
        g.drawString(time,20,70);
    }


    private void drawAgents(Graphics g) {
        List<CL_Agent> rs = arena.getAgents();
        g.setColor(Color.red);
        int i=0;
        while(rs!=null && i<rs.size())
        {
            geo_location c = rs.get(i).getLocation();
            int r=8;
            i++;
            if(c!=null)
            {
                geo_location fp = this.range.world2frame(c);
                g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
            }
        }
    }


    private void drawPokemons(Graphics g) {
        List<CL_Pokemon> fs = arena.getPokemons();
        if(fs!=null)
        {
            Iterator<CL_Pokemon> itr = fs.iterator();
            while(itr.hasNext())
            {
                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r=10;
                g.setColor(Color.green);
                if(f.getType()<0)
                {
                    g.setColor(Color.orange);
                }
                if(c!=null)
                {
                    geo_location fp = this.range.world2frame(c);
                    g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                }
            }
        }
    }



    /** updates the frame..
     *  resizing the graph to the current size of the frame window.
     * @param ar the current game arena
     */
    public void update(Arena ar) {
        this.arena = ar;
        updateFrame();
    }


//////////////////////////Getters and Setters////////////////////////////////////////////////////////////////

    public void setArena(Arena arena){
        this.arena = arena;
    }

    public void setScenario(int scenario){
        this.scenario = scenario;
    }

    public void setGameService(game_service game){
        gameService = game;
    }

    public long getID(){
        return _id;
    }

    public int getScenario(){
        return _scenarioOfLis;
    }

}





