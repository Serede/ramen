/**
 * 
 */
package jp.ramen;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Student test
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class StudentTest {
	private User s1, s2;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		s1 = new Student("st", "pass");
		s2 = new Sensei("se", "pass");
	}

	/**
	 * Test method for {@link jp.ramen.Student#block(jp.ramen.Entity)}.
	 */
	@Test
	public void testBlock() {
		assertFalse(s1.block(s2));
	}

	/**
	 * Test method for {@link jp.ramen.Student#canAnswer()}.
	 * A student can always answer
	 */
	@Test
	public void testCanAnswer() {
		assertTrue(s1.canAnswer());
	}

}
