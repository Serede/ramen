package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
/**
 * SocialGroup test
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 *
 */
public class SocialGroupTest {
	private User u1;
	private SocialGroup g;
	
	@Before
	public void setUp() throws Exception {
		u1 = new Student("u1","pass");
		g = new SocialGroup("subj","desc",null,u1,true,true);
	}

	/**
	 * Is moderated
	 */
	@Test
	public void testIsModerated() {
		assertTrue(g.isModerated());
	}
	
	/**
	 * Is private
	 */
	@Test
	public void testIsPrivate() {
		assertTrue(g.isPrivate());
	}

}
