package api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {
    static private DWGraph_DS randGraph;
    static private GeoLocation geoLocation = new GeoLocation(0,0,0);




    public static DWGraph_DS getRandomGraph() {
        DWGraph_DS g = new DWGraph_DS();
        int nodesSize = (int)(Math.random()*5)+5;
        for (int i = 0; i < nodesSize; i++) {
            g.addNode(new NodeData(i, geoLocation));
        }
        for (int i = 0; i < nodesSize; i++) {
            int edgesSize = (int)(Math.random()*nodesSize);
            for (int j = 0; j < edgesSize; j++) {
                int dest = (int)(Math.random()*nodesSize);
                if(i != dest)
                    g.connect(i, dest, 1);
            }
        }
        return g;
    }



    @BeforeAll
    static void setRandomGraph() {
        randGraph = getRandomGraph();
    }


    @Test
    void testAddAndGerNode() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(1,geoLocation));
        assertEquals(1, g.getNode(1).getKey());

        node_data nd = g.getNode(2);
        assertTrue(nd == null, "Node 2 doesn't exist");

        try {
            g.addNode(new NodeData(1,geoLocation));
            fail("the graph shouldn't allow to insert the same key twice");
        } catch (RuntimeException e) {}

    }



    @Test
    void getEdge() {
    }



    @Test
    void testConnect() {
        DWGraph_DS g = new DWGraph_DS();
        g.addNode(new NodeData(1,geoLocation));
        g.addNode(new NodeData(2,geoLocation));

        g.connect(1, 2, 1.1);

        try {
            g.connect(1, 3, 1.1);
            fail("the graph shouldn't allow to connect unexist Nodes (3)");
        } catch (Exception e) {}

        try {
            g.connect(3, 1, 1.1);
            fail("the graph shouldn't allow to connect unexist Nodes (3)");
        } catch (Exception e) {}

        try {
            g.connect(2, 1, -1);
            fail("the graph shouldn't allow to create edge with a negativ weight");
        } catch (Exception e) {}
    }


    @Test
    void getVnum() {
    }

    @Test
    void getV() {
    }

    @Test
    void getE() {
    }



    @Test
    void testRemoveNode() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(2,geoLocation));
        g.removeNode(2);

        node_data nd = g.getNode(2);
        assertTrue(nd == null, "Node 2 doesn't exist");
    }



    @Test
    void testRemoveEdge() {
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData(1,geoLocation));
        g.addNode(new NodeData(2,geoLocation));
        g.connect(1, 2, 1.1);
        g.removeEdge(1, 2);

        edge_data e = g.getEdge(1, 2);
        assertTrue(e == null, "Edge (1,2) doesn't exist");
    }



    @Test
    void testReversCopy() {
        DWGraph_DS g = new DWGraph_DS();
        g.addNode(new NodeData(1,geoLocation));
        g.addNode(new NodeData(2,geoLocation));
        g.addNode(new NodeData(3,geoLocation));
        g.connect(1, 2, 1.1);
        g.connect(2, 3, 1.2);
        g.connect(3, 1, 1.3);

        DWGraph_DS revers = new DWGraph_DS();
        revers.addNode(new NodeData(1,geoLocation));
        revers.addNode(new NodeData(2,geoLocation));
        revers.addNode(new NodeData(3,geoLocation));
        revers.connect(2, 1, 1.1);
        revers.connect(3, 2, 1.2);
        revers.connect(1, 3, 1.3);

        assertEquals(revers, g.getReversCopy(), "Reverse doesn't work good");

    }



    @Test
    void nodeSize() {
    }

    @Test
    void edgeSize() {
    }

    @Test
    void getMC() {
    }


    @Test
    void print(){
         for(int i = 0; i < 4; i++)
        {
            randGraph.connect(i, i+1, 2*i +1);
        }
        System.out.println(randGraph);
    }

    @Test
    void testEquals() {
    }

    @Test
    void testToString() {
    }
}