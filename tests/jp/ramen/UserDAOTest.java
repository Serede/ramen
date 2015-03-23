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
public class UserDAOTest {

	UserDAO udb;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		udb = UserDAO.getInstance();
	}

	/**
	 * Test method for {@link jp.ramen.UserDAO#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		UserDAO udb2 = UserDAO.getInstance();
		assertTrue(udb2.equals(udb));
	}

	/**
	 * Test method for {@link jp.ramen.UserDAO#generateSHA(java.lang.String)}.
	 */
	@Test
	public void testGenerateSHA() {
		assertTrue(UserDAO.generateSHA("mamnds455").equals("7c739be1ced7d9a6719959ba2565a0b8446d1fa3"));
	}

}
