import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class RouteTest {

    private static ConnectionList connList;
    private static LinkList linkList;
    private static PacketList msgList = new PacketList();
    private static Route r;
    private static final int COUNT = 5;
    private final static int NUM_GROUPS = 3;
    private final static int NUM_NODES = 23;

    @Before
    public void setUpBefore() throws Exception {
        connList = new ConnectionList();
        linkList = new LinkList();
        r = new Route(connList, linkList, msgList, 0);

        for (int index = 0; index < NUM_GROUPS * NUM_NODES; index++) {
            Connection conn = new Connection(new BufferedOutputStream(
                    System.out), System.in);
            conn.setNode(index);
            conn.setNetwork("Network" + (index % NUM_GROUPS));
            conn.setup(1234);
            connList.addElement(conn);
            linkList.addElement(new Link(index, (index + NUM_GROUPS)
                    % (NUM_GROUPS * NUM_NODES), "Group" + (index % NUM_GROUPS)));
        }
    }

    @Test
    public void testTopology() {
        r.updateStatus();
        System.out.println("TOP:" + r.queue.get(0));
        assertTrue(r.queue.get(0).toString().contains("topology"));
    }

    @Test
    public void testWhois() {
        linkList.setCheckwhois(true);
        linkList.setOffset(1);
        linkList.nextStage();
        r.updateStatus();
        System.out.println("WHO:" + r.queue.get(0));
        assertFalse(r.queue.get(0).toString().contains("topology"));
    }

    @Test
    public void testCorruptSend() {
        linkList.setCheckwhois(false);
        linkList.setOffset(1);
        linkList.setDelay(2);
        linkList.setDropRate(20);
        linkList.setCorruptionRate(10);
        linkList.nextStage();
        r.updateStatus();
        System.out.println("CORR:" + r.queue.get(0));
        assertFalse(r.queue.get(0).toString().contains("topology"));
    }

    @Test
    public void testSend() {
        linkList.setCheckwhois(false);
        linkList.setOffset(1);
        linkList.setDelay(0);
        linkList.setDropRate(0);
        linkList.setCorruptionRate(0);
        linkList.nextStage();
        r.updateStatus();
        System.out.println("SND:" + r.queue.get(0));
        assertFalse(r.queue.get(0).toString().contains("topology"));
    }

    @Test
    public void testStatusLength() {
        linkList.setCheckwhois(false);
        linkList.setOffset(1);
        linkList.setDelay(10);
        linkList.setDropRate(10);
        linkList.setCorruptionRate(0);
        linkList.nextStage();
        int maxLength = 0;
        for (int index = 0; index < COUNT; index++) {
            r.updateStatus();
            maxLength = Math.max(maxLength, r.queue.get(0).toString().length());
            System.out.println("LENGTH TEST:  " + r.queue.get(0).toString());
            r.sendNow(r.queue);
        }
        assertTrue(maxLength < 600);
    }

}
