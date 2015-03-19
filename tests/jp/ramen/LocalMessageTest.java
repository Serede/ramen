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
public class LocalMessageTest {
	Message m1;
	User u1, u2;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		u1 = new Student("u1", "pass");
		u2 = new Student("u2", "pass");
		m1 = new Message("subj", "text", u1, u2);
		u2.addToInbox(m1);
	}

	/**
	 * Test method for {@link jp.ramen.LocalMessage#getReference()}.
	 */
	@Test
	public void testGetReference() {
		System.out.println(u1.getInbox());
		assertTrue(u2.getInbox().get(0).getReference()==m1);
	}

	/**
	 * Test method for {@link jp.ramen.LocalMessage#read()}.
	 */
	@Test
	public void testRead() {
		assertTrue(u2.getInbox().get(0).read());
	}

	/**
	 * Test method for {@link jp.ramen.LocalMessage#isRead()}.
	 */
	@Test
	public void testIsReadFalse() {
		assertFalse(u2.getInbox().get(0).isRead());
	}
	
	@Test
	public void testIsReadTrue() {
		u2.getInbox().get(0).read();
		assertTrue(u2.getInbox().get(0).isRead());
	}

}
