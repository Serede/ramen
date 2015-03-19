package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MessageTest {
	private static Message m1;
	private static Student st1, st2;
	private static String name1 = "dani";
	private static String name2 = "sergio";
	private static String subj = "test subject";
	private static String text = "test text";
	@Before
	public void setUp() throws Exception {
		st1 = new Student(name1, "pass");
		st2 = new Student(name2, "pass");
		m1 = new Message(subj, text, st1, st2);
	}

	@Test
	public void testGetSubject() {
		assertTrue(m1.getSubject().equals(subj));
	}

	@Test
	public void testGetText() {
		assertTrue(m1.getText().equals(text));
	}

//	@Test
//	public void testIsAccepted() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetTime() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testGetAuthor() {
		assertTrue(m1.getAuthor().equals(st1));
	}

	@Test
	public void testGetTo() {
		assertTrue(m1.getTo().equals(st2));
	}

}
