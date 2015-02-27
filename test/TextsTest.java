import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;


public class TextsTest {
	Set<String> s;
	List<String> texts;
	List<String> messages;
	final int COUNT = 100000;
	
	@Before
	public void setup() {
		texts = new ArrayList<String>();
		Texts.choose_messages(texts, COUNT, false);
		messages = new ArrayList<String>();
		Texts.choose_messages(messages, COUNT, true);
		
		s = new TreeSet<String>();
		for (int index = 0 ; index < COUNT ; index++) {
			s.add(Texts.choose_name(index));
		}		
	}
	@Test
	public void test() {
		System.out.println(Texts.choose_name(1));
		System.out.println(Texts.choose_name(2));
	}
	
	@Test
	public void test1() {
		assertEquals(s.size(), COUNT);
	}
	
	@Test
	public void test3() {
		System.out.println(messages.get(5));
		for (int index = 0 ; index < COUNT ; index++) {
			assertTrue(messages.get(index).length() > 35);
			assertTrue(messages.get(index).length() < 90);
		}
	}
	
	@Test
	public void test4() {
		System.out.println(texts.get(5));
		for (int index = 0 ; index < COUNT ; index++) {
			assertTrue(texts.get(index).length() > 40);
			assertTrue(texts.get(index).length() < 80);
		}
	}
}