import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class RouteTest {

	private static ConnectionList connList;
	private static LinkList linkList;
	private static MessageList msgList = new MessageList();
	private static Route r;
	private static final int COUNT = 5;
	
	@Before
	public void setUpBefore() throws Exception {
		connList = new ConnectionList();
		linkList = new LinkList();
		r = new Route(connList,linkList,msgList);

	  for (int index = 0 ; index < 70; index++) {
		  Connection conn = new Connection(new OutputStreamWriter(System.out), new InputStreamReader(System.in));
		  conn.setNode(index);
		  conn.setGroup("Group" + (index % 3));
		  conn.pickName();
   		connList.addElement(conn);
  	  linkList.addElement(new Link(index, (index + 3) % 70, "Group" + (index % 3)));
		}
	}

	@Test
	public void testTopology() {
		r.updateStatus();
		System.out.println("TOP:" + r.statusMessages.get(0));
		assertTrue(r.statusMessages.get(0).toString().contains("topology"));
	}

	@Test
	public void testWhois() {
		linkList.setCheckwhois(true);
		linkList.setOffset(1);
		linkList.nextStage();
		r.updateStatus();
		System.out.println("WHO:" + r.statusMessages.get(0));
		assertFalse(r.statusMessages.get(0).toString().contains("topology"));
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
		System.out.println("CORR:" + r.statusMessages.get(0));
		assertFalse(r.statusMessages.get(0).toString().contains("topology"));
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
		System.out.println("SND:" + r.statusMessages.get(0));
		assertFalse(r.statusMessages.get(0).toString().contains("topology"));
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
		for (int index = 0 ; index < COUNT ; index++) {
  		r.updateStatus();
  		maxLength = Math.max(maxLength, r.statusMessages.get(0).toString().length());
  		System.out.println("LENGTH TEST:  " + r.statusMessages.get(0).toString());
  		r.sendNow(r.statusMessages);
		}
	  assertTrue(maxLength < 600);
	}
	
	@Test
	public void testFind() {
		for (int index = 0; index < 50 ; index++) {
				assertTrue(linkList.isNeighbour(index, r.find(index, 1)));
		}
	}
	
	@Test
	public void testFind2() {
		for (int index = 0; index < 50 ; index++) {
				assertFalse(linkList.isNeighbour(index, r.find(index, 2)));
		}
	}
	
	@Test
	public void testFind3() {
		for (int index = 0; index < 50 ; index++) {
			for (int distance = 1; distance < 10 ; distance++) { 
				assertFalse(r.find(index, distance) == r.find(r.find(index,  distance), distance));
			}
		}
	}

	@Test
	public void testFind4() {
		TreeSet<Integer> answers = new TreeSet<Integer>();
		for (int index = 0; index < 70 ; index++) {
				assertFalse(answers.contains(r.find(index, 1)));
				answers.add(r.find(index, 1));
			}
		}

	@Test
	public void testFind5() {
		TreeSet<Integer> answers = new TreeSet<Integer>();
		for (int index = 0; index < 70 ; index++) {
				assertFalse(answers.contains(r.find(index, 3)));
				answers.add(r.find(index, 3));
			}
		}
	@Test
	public void testFind6() {
		TreeSet<Integer> answers = new TreeSet<Integer>();
		for (int index = 0; index < 70 ; index++) {
				assertFalse(answers.contains(r.find(index, -3)));
				answers.add(r.find(index, -3));
			}
		}
	}
	
