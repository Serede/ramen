package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SocialGroupTest {
	private User u1;
	private SocialGroup g;
	
	@Before
	public void setUp() throws Exception {
		u1 = new Student("u1", "pass");
		g = new SocialGroup("subj","desc",null, u1, true, true);
	}

	@Test
	public void testIsModerated() {
		assertTrue(g.isModerated());
	}
	@Test
	public void testIsPrivate() {
		assertTrue(g.isPrivate());
	}

}
