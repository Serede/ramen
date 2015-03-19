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
public class SenseiTest {
	private User s1, s2;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		s1 = new Sensei("s1", "pass");
		s2 = new Sensei("s2", "pass");
	}

	/**
	 * Test method for {@link jp.ramen.Sensei#block(jp.ramen.Entity)}.
	 */
	@Test
	public void testBlock() {
		assertTrue(s1.block(s2));
	}

	/**
	 * Test method for {@link jp.ramen.Sensei#canAnswer()}.
	 */
	@Test
	public void testCanAnswer() {
		assertFalse(s1.canAnswer());
	}

}
