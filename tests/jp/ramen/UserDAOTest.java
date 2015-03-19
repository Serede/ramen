/**
 * 
 */
package jp.ramen;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class UserDAOTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link jp.ramen.UserDAO#getInstance()}.
	 */
	@Test
	public void testGetInstance() {
		UserDAO a = UserDAO.getInstance();
		UserDAO b = UserDAO.getInstance();
		assertTrue(a.equals(b));
	}

	/**
	 * Test method for {@link jp.ramen.UserDAO#getUser(java.lang.String)}.
	 */
	@Test
	public void testGetUser() {
		UserDAO a = UserDAO.getInstance();
		try {
			//DAO.create("./db/","./students.txt","./professors.txt");
			DAO.init("./db/");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		User u = a.getUser("maria.martin@ddm.es");
		assertTrue(u.checkPassword("mamnds455"));
	}

	
	@Test
	public void testGetUserNoUser() {
		UserDAO a = UserDAO.getInstance();
		User u = a.getUser("da@ddm.es");
		assertTrue(u == null);
	}

	
	
	/**
	 * Test method for {@link jp.ramen.UserDAO#generateSHA(java.lang.String)}.
	 */
	@Test
	public void testGenerateSHA() {
		assertTrue(UserDAO.generateSHA("mamnds455").equals("7c739be1ced7d9a6719959ba2565a0b8446d1fa3"));
	}

	/**
	 * Test method for {@link jp.ramen.UserDAO#populate(java.lang.String, boolean)}.
	 */
	@Test
	public void testPopulate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.UserDAO#load()}.
	 */
	@Test
	public void testLoad() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link jp.ramen.UserDAO#link()}.
	 */
	@Test
	public void testLink() {
		fail("Not yet implemented");
	}

}
