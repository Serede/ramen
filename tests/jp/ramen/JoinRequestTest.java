package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
/**
 * JoinRequest test
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 *
 */
public class JoinRequestTest {
	private SocialGroup g;
	private User u1;
	private User u2;
	private JoinRequest r;
	@Before
	public void setUp() throws Exception {
		u1 = new Student("u1", "pass");
		u2 = new Student("u2", "pass");
		g = new SocialGroup("g", "desc", null, u1, true, false);
		r = new JoinRequest("msg", "text", u2, g);
	}

	/**
	 * Get accepted
	 */
	@Test
	public void getAccepted() {
		assertTrue(r.isAccepted());
	} 
	
	/**
	 * Set accepted
	 */
	@Test
	public void setAcceptedTrue() {
		r.setRequest(true);
		assertTrue(g.getMembers().contains(u2));
	}
	
	/**
	 * Set accepted
	 */
	@Test
	public void setAcceptedFalse() {
		r.setRequest(false);
		assertFalse(g.getMembers().contains(u2));
	}
	
	/**
	 * Once you accept, the request is handled
	 */
	@Test
	public void setAcceptedTwice() {
		r.setRequest(false);
		r.setRequest(true);
		assertFalse(g.getMembers().contains(u2));
	}
}
