package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EntityTest {
	private static Student st1;
	private static String name1 = "dani";
	@Before
	public void setUp() throws Exception {
		st1 = new Student(name1, "pass");
	}
	/**
	 * Tests the name and the constructor
	 */
	@Test
	public void testName() {
		assertTrue(name1.equals(st1.getName()));
	}
	
}
