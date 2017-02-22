import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.util.Random;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class NetworkTest {
    private static ConnectionList connList;
    private static LinkList linkList;
    private static final int COUNT = 20;
    private Random rand = new Random();
    private float chance = 1.0f;
    private Network network;
    private String group = "Group1";

    @Before
    public void setUpBefore() throws Exception {
        connList = new ConnectionList();
        linkList = new LinkList();

        for (int nodeIndex = 0; nodeIndex < COUNT; nodeIndex++) {
            Connection conn = new Connection(new BufferedOutputStream(
                    System.out), new MyStream());
            conn.setNode(12 * nodeIndex);
            final String netName = "Network" + (nodeIndex % 3);
            conn.setNetwork(netName);
            conn.setup(1234);
            connList.addElement(conn);

            for (int other = 0 ; other < nodeIndex; other++) {
                Connection connOther = connList.get(other);
                System.out.println(netName + " and " + connOther.getNetwork());
                if ((netName.equals(connOther.getNetwork())) && (rand.nextFloat() <= chance)) {
                   linkList.addElement(new Link(conn.getNode(), connOther.getNode(), netName));
                   System.out.println(conn.getNode() + " connected to " + connOther.getNode() + " in " + netName);
                }
            }
        }
        for (int index = 0 ; index < 1 ; index++) {
            network = new Network(linkList, "Network" + (index %3), connList);
            assertTrue(network.hamiltonian());
        }
    }

    @Test
    public void testOffset() {
        for (int nodeIndex = 0; nodeIndex < COUNT; nodeIndex++) {
            Connection conn = connList.get(nodeIndex);
            int node = conn.getNode();
            int target = network.offsetNode(node, 1);
            if (network.getName().equals(conn.getNetwork())) {
                assertTrue(linkList.isNeighbour(node, target));
            } else {
                assertEquals(node, target);
            }
        }
    }

    @Test
    public void testReturnOffset() {
        for (int index = 0; index < COUNT; index++) {
            Connection conn = connList.get(index);
            for (int distance = 1; distance < 4; distance++) {
                int next = network.offsetNode(conn.getNode(), distance);
                assertEquals(
                        "out and back (" + distance + ") from "
                                + conn.getNode(), conn.getNode(),
                        network.offsetNode(next, -distance));
            }
        }
    }

    @Test
    public void testDifferent() {
        for (int distance = 1; distance < 4; distance++) {
            TreeSet<Integer> answers = new TreeSet<Integer>();
            for (int index = 0; index < COUNT; index++) {
                Connection conn = connList.get(index);

                int next = network.offsetNode(conn.getNode(), distance);
                assertFalse(answers.contains(next));
                answers.add(next);
            }
        }
    }
}
