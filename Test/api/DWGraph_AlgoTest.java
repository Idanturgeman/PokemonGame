package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    private static DWGraph_DS randGraph;
    private static DWGraph_Algo alg;
    private static GeoLocation geoLocation = new GeoLocation(0,0);


    @BeforeAll
    static void setRandomGraph() {
        randGraph = DWGraph_DSTest.getRandomGraph();
        alg = new DWGraph_Algo();
        alg.init(randGraph);
    }
    @Test
    void Save1(){
        String file = "messi.txt";
        Assertions.assertTrue(alg.save(file));

        System.out.println("first save succeeded");
    }




    @Test
    void Save2(){
        DWGraph_DS g0 = new DWGraph_DS();
        alg.init(g0);
        Assertions.assertTrue(alg.save("messi.txt"));

        System.out.println("second save succeeded");
    }





    @Test
    void Load1(){
        String file = "messi.txt";
        alg.save(file);
        randGraph.removeNode(5);

        Assertions.assertTrue(alg.load(file));
        Assertions.assertTrue(alg.isConnected());

        System.out.println("first load succeeded");
    }

    @Test
    void Load2(){
        DWGraph_DS g0 = new DWGraph_DS();
        alg.init(g0);
        alg.save("messi.txt");
        Assertions.assertTrue(alg.load("messi.txt"));

        System.out.println("second load succeeded");
    }




    @Test
    void testCopy() {
        DWGraph_DS g = DWGraph_DSTest.getRandomGraph();
        DWGraph_DS copy =(DWGraph_DS) new DWGraph_Algo().copy(g);
        assertEquals(g, copy);
    }

    @Test
    void testIsConnected() {
        DWGraph_DS g = new DWGraph_DS();
        alg.init(g);
        assertTrue(alg.isConnected());

        g.addNode(new NodeData(1,geoLocation));
        g.addNode(new NodeData(2,geoLocation));
        g.addNode(new NodeData(3,geoLocation));
        g.connect(1, 2, 1.1);
        g.connect(2, 3, 1.2);
        alg.init(g);
        assertFalse(alg.isConnected());

        g.connect(3, 1, 1.3);

        alg.init(g);
        assertTrue(alg.isConnected());
    }

    @Test
    void testShortestPathAndDist() {
        DWGraph_DS g = new DWGraph_DS();
        g.addNode(new NodeData(1,geoLocation));
        g.addNode(new NodeData(2,geoLocation));
        g.addNode(new NodeData(3,geoLocation));
        g.addNode(new NodeData(4,geoLocation));
        g.addNode(new NodeData(5,geoLocation));

        g.connect(1, 2, 1.1);
        g.connect(2, 3, 1.1);
        g.connect(3, 4, 1.1);
        g.connect(4, 5, 1.1);

        g.connect(1, 5, 10);

        alg.init(g);
        assertEquals(4.4, alg.shortestPathDist(1, 5));
        assertEquals(5, alg.shortestPath(1, 5).size());
    }

    @Test
    void save() {
    }

    @Test
    void load() {
    }
}