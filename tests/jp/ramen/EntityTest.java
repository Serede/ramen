package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EntityTest {
	private static Student st1, st2;
	private static String name1 = "dani";
	private static String name2 = "sergio";
	@Before
	public void setUp() throws Exception {
		st1 = new Student(name1, "pass");
		st2 = new Student(name2, "pass");
	}
	/**
	 * Tests the name and the constructor
	 */
	@Test
	public void testName() {
		assertTrue(name1.equals(st1.getName()));
	}
	
	/**
	 * A message is automatically added to the attribute,
	 * so there is no need of adding it again. This checked
	 * whether this is possible
	 */
	@Test
	public void testDoubleMessage() {
		Message m1 = new Message("test", "test",st1, st2);
		assertFalse(st2.addToMessages(m1));
	} 
	
}
