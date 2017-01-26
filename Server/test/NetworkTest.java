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
    private static final int COUNT = 90;
    private Random rand = new Random();
    private float chance = 0.5f;
    private Network network;
    private String group = "Group1";

    @Before
    public void setUpBefore() throws Exception {
        connList = new ConnectionList();
        linkList = new LinkList();

        for (int index = 0; index < COUNT; index++) {
            Connection conn = new Connection(new BufferedOutputStream(
                    System.out), System.in);
            conn.setNode(12 * index);
            conn.setNetwork("Network" + (index % 3));
            conn.setup(1234);
            connList.addElement(conn);
        }
        for (int nodeIndex = 0; nodeIndex < COUNT; nodeIndex++) {
            for (int other = nodeIndex + 1; other < COUNT; other++) {
                if (rand.nextFloat() <= chance) {
                    Connection conn = connList.get(nodeIndex);
                    Connection connOther = connList.get(other);
                    if (conn.getNetwork().equals(connOther.getNetwork()))
                        linkList.addElement(new Link(conn.getNode(), connOther
                                .getNode(), group));
                }
            }
        }
        network = new Network(linkList, group, connList);
        assertTrue(network.hamiltonian());
    }

    @Test
    public void testOffset() {
        for (int nodeIndex = 0; nodeIndex < COUNT; nodeIndex++) {
            Connection conn = connList.get(nodeIndex);
            int node = conn.getNode();
            int target = network.offsetNode(node, 1);
            if (group.equals(conn.getNetwork())) {
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
