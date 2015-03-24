package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
/**
 * Group test
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 *
 */

public class GroupTest {
	@SuppressWarnings("unused")
	private Group g1, g2, g11, g111;
	private User u1, u2;
	
	@Before
	public void setUp() throws Exception {
		u1 = new Sensei("u1", "pass");
		u2 = new Sensei("u2", "pass");
		g1 = new StudyGroup("g1", "desc", null,u1);
		g11 = new StudyGroup("g11","desc", g1,u1);
		g111 = new StudyGroup("g111","desc", g11,u1);
		g2 = new StudyGroup("g2", "desc", null,u2);
	}
	/**
	 * Add member
	 */
	@Test
	public void testAddMember() {
		g1.addMember(u2);
		assertTrue(g1.getMembers().contains(u1) && g1.getMembers().contains(u2));
		
	}
	
	/**
	 * Add a member who is already in the group
	 */
	@Test
	public void testAddMemberTwice() {
		g1.addMember(u2);
		assertFalse(g1.addMember(u2));
	}

	/**
	 * Add the creator twice
	 */
	@Test
	public void testAddMemberCreator() {
		assertFalse(g1.addMember(u1));
	}
	
	/**
	 * Get the code
	 */
	@Test
	public void testGetCode() {
		assertTrue(g1.getCode().equals("g1"));
	}

	/**
	 * Get the desc
	 */
	@Test
	public void testGetDesc() {
		assertTrue(g1.getDesc().equals("desc"));
	}

	/**
	 * Supergroup when there is no supergroup
	 */
	@Test
	public void testGetSupergroupNoSuperGroup() {
		assertTrue(g1.getSupergroup()==null);
	}

	/**
	 * Get supergroup
	 */
	@Test
	public void testGetSupergroup() {
		assertTrue(g11.getSupergroup()==g1);
	}
	
	/**
	 * Get owner
	 */
	@Test
	public void testGetOwner() {
		assertTrue(g1.getOwner()==u1);
	}

	/**
	 * Get subgroups
	 */
	@Test
	public void testGetSubgroups() {
		assertTrue(g1.getSubgroups().contains(g11));
	}

	/**
	 * Get members
	 */
	@Test
	public void testGetMembers() {
		assertTrue(g1.getMembers().contains(u1));
	}


}
