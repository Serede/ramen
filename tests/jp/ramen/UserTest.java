/**
 * 
 */
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
public class UserTest {
	private User u1, u2;
	private Group g1;
	private Entity e1;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		u1 = new Student("user", "pass", true);
		u2 = new Student("2", "pass", true);
		g1 = new SocialGroup("group1", "desc", null, u2, false, false);
	}

	/**
	 * Subscribe
	 */
	@Test
	public void testSubscribe() {
		assertTrue(u1.subscribe(g1));
	}
	
	/**
	 * You can not subscribe twice to the same group
	 */
	@Test
	public void testSubscribeTwice() {
		u1.subscribe(g1);
		assertFalse(u1.subscribe(g1));
	}

	/**
	 * Test method for {@link jp.ramen.User#unsubscribe(jp.ramen.Group)}.
	 */
	@Test
	public void testUnsubscribe() {
		u1.subscribe(g1);
		assertTrue(u1.unsubscribe(g1));
	}
	
	/**
	 * You can not unsubscribe if you have not subscribe 
	 */
	@Test
	public void testUnsubscribeNotAMember() {
		assertFalse(u1.unsubscribe(g1));
	}
	
	/**
	 * Test method for {@link jp.ramen.User#addToBlocked(jp.ramen.Entity)}.
	 */
	@Test
	public void testAddToBlocked() {
		assertTrue(u1.addToBlocked(e1));
	}

	/**
	 * You cannot block twice the same entity
	 */
	public void testAddToBlockedAlreadyBlocked() {
		u1.addToBlocked(e1);
		assertTrue(u1.addToBlocked(e1));
	}
	
	/**
	 * Test method for {@link jp.ramen.User#checkPassword(java.lang.String)}.
	 */
	@Test
	public void testCheckPassword() {
		assertTrue(u1.checkPassword("pass"));
	}
	
	/**
	 * A wrong password check
	 */
	@Test
	public void testCheckPasswordWrong() {
		assertFalse(u1.checkPassword("dsauj"));
	}

}
