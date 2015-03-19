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
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		u1 = new Student("u1", "pass");
		u2 = new Student("u2", "pass");
		m = new Message("subj","text",u1,u2);
		r = new MessageRequest("subj", "text", u1, u2, m);
		u2.addToInbox(r);
	}

	
	@Test
	public void getAccepted() {
		assertFalse(r.isAccepted());
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
