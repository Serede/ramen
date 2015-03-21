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
public class MessageRequestTest {
	private MessageRequest r;
	private Message m;
	private User u1;
	private User u2;
	private Group g;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		u1 = new Student("u1", "pass");
		u2 = new Sensei("u2", "pass");
		g = new SocialGroup("g1","desc",null,u1,false, true);
		g.addMember(u2);
		m = new Message("subj","text",u1,g);
		r = new MessageRequest("subj", "text", u1, g, m);
		u2.addToInbox(r);
	}

	
	@Test
	public void getAccepted() {
		assertTrue(r.isAccepted());
	} 
	
	@Test
	public void setAcceptedTrue() {
		r.setRequest(true);
		assertTrue(u2.getInbox().get(1).getReference()==m);
	}
	
	@Test
	public void setAcceptedFalse() {
		r.setRequest(false);
		assertFalse(u2.getInbox().contains(m));
	}
	
	@Test
	public void setAcceptedTwice() {
		r.setRequest(false);
		r.setRequest(true);
		assertFalse(u2.getInbox().contains(m));
	}

}
