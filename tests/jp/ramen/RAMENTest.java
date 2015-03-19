/**
 * 
 */
package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class RAMENTest {
	private RAMEN ramen;
	private User u;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ramen = RAMEN.getInstance();
		
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#init(java.lang.String)}.
	 */
	@Test
	public void testInit() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#sendMessage(jp.ramen.Entity, java.lang.String, java.lang.String, boolean)}.
	 */
	@Test
	public void testSendMessage() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#login(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testLogin() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#createGroup()}.
	 */
	@Test
	public void testCreateGroup() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#joinGroup(jp.ramen.Group)}.
	 */
	@Test
	public void testJoinGroup() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#sendAnswer(jp.ramen.Entity, java.lang.String, java.lang.String, jp.ramen.Question)}.
	 */
	@Test
	public void testSendAnswer() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#handleMessageRequest(jp.ramen.MessageRequest, boolean)}.
	 */
	@Test
	public void testHandleMessageRequest() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#handleJoinRequest(jp.ramen.JoinRequest, boolean)}.
	 */
	@Test
	public void testHandleJoinRequest() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#block(jp.ramen.Entity)}.
	 */
	@Test
	public void testBlock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#unblock(jp.ramen.Entity)}.
	 */
	@Test
	public void testUnblock() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#getCurrentUser()}.
	 */
	@Test
	public void testGetCurrentUser() {
		fail("Not yet implemented");
	}

}
