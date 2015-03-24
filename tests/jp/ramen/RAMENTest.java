package jp.ramen;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;

import jp.ramen.exceptions.ForbiddenAction;
import jp.ramen.exceptions.GroupAlreadyExists;
import jp.ramen.exceptions.InvalidMessage;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Main test of the API. The tests are more precise because it is necessary to ensure the correct behaviour of the API.
 * Some tests depends one on the other, so all must be executed together
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class RAMENTest {
	private static RAMEN ramen;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		File f = new File("./db_test/ramen.mv.db");
		if(f.isFile()) f.delete();
		File g = new File("./path.lck");
		File gtemp = new File("./path.lck.tmp");
		if(g.isFile()) g.renameTo(gtemp);
		ramen = RAMEN.getInstance();
		ramen.install("./db_test", "students.txt", "professors.txt");
		ramen.init();
	}

	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		File f = new File("./db_test/ramen.mv.db");
		if(f.isFile()) f.delete();
		File g = new File("./path.lck");
		File gtemp = new File("./path.lck.tmp");
		if(g.isFile()) g.delete();
		if(gtemp.isFile()) gtemp.renameTo(g);
	}

	
	
	/**
	 * Test method for {@link jp.ramen.RAMEN#sendMessage(jp.ramen.Entity, java.lang.String, java.lang.String, boolean)}.
	 * @throws SQLException 
	 * @throws InvalidMessage 
	 * @throws ForbiddenAction 
	 */
	@Test
	public void testSendMessageUser() throws ForbiddenAction, InvalidMessage, SQLException {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertTrue(ramen.sendMessage(ramen.getDAO().getUdb().getUser("leonardo.martin@mail.gob"), "test", "test", false));
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.getCurrentUser().getInbox().size()==1);
	}

	/**
	 * Send question to an user, so it is forbidden
	 * @throws ForbiddenAction
	 * @throws InvalidMessage
	 * @throws SQLException
	 */
	@Test(expected=ForbiddenAction.class)
	public void testSendQuestionUser() throws ForbiddenAction, InvalidMessage, SQLException {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertFalse(ramen.sendMessage(ramen.getDAO().getUdb().getUser("leonardo.martin@mail.gob"), "test", "test", true));
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.getCurrentUser().getInbox().size()==1);
	}
	
	/**
	 * Send question when you are not the owner. It is forbidden
	 * @throws ForbiddenAction
	 * @throws InvalidMessage
	 * @throws SQLException
	 * @throws GroupAlreadyExists
	 */
	@Test(expected=ForbiddenAction.class)
	public void testSendQuestionStudyNoOwner() throws ForbiddenAction, InvalidMessage, SQLException, GroupAlreadyExists {
		ramen.login("oscar.casals@mail.gob", "orcsmb457");
		ramen.createGroup("studygroupofOscar", "desc", null, false, false, false);
		ramen.login("elvis.domingo@mail.gob", "esdomb467");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("studygroupofoscar"));
		assertFalse(ramen.sendMessage(ramen.getDAO().getGdb().getGroup("studygroupofoscar"), "question", "text", true));
	}
	
	
	
	/**
	 * Test method for {@link jp.ramen.RAMEN#login(java.lang.String, java.lang.String)}.
	 * No existing user
	 */
	@Test
	public void testLoginNoUser() {
		assertFalse(ramen.login("iamnotanuser", "thisisnotapassword"));
	}

	/**
	 * Login with a valid user but a wrong password
	 */
	@Test
	public void testLoginWrongPass() {
		assertFalse(ramen.login("maria.martin@ddm.es", "thisisnotapassword"));
	}
	
	/**
	 * Login in normal way
	 */
	@Test
	public void testLogin() {
		assertTrue(ramen.login("maria.martin@ddm.es", "mamnds455"));
	}
	
	/**
	 * Test method for {@link jp.ramen.RAMEN#createGroup()} when you are a Sense.
	 * @throws SQLException 
	 * @throws ForbiddenAction 
	 */

	@Test
	public void testCreateGroupStudySensei() throws ForbiddenAction, SQLException, GroupAlreadyExists {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.createGroup("group1", "desc", null, false, false, false));
	}
	
	/**
	 * You can not create a study group if you are a student
	 * @throws ForbiddenAction
	 * @throws SQLException
	 * @throws GroupAlreadyExists
	 */
	@Test(expected=ForbiddenAction.class)
	public void testCreateGroupStudyStudent() throws ForbiddenAction, SQLException, GroupAlreadyExists{
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertFalse(ramen.createGroup("group11", "desc", null, false, false, false));
	}
	
	/**
	 * Create a social group from a student account
	 * @throws ForbiddenAction
	 * @throws SQLException
	 * @throws GroupAlreadyExists
	 */
	@Test
	public void testCreateGroupSocialStudent() throws ForbiddenAction, SQLException, GroupAlreadyExists{
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertTrue(ramen.createGroup("group111", "desc", null, true, false, false));
	}
	
	/**
	 * You can not create a social group from a sensei account
	 * @throws ForbiddenAction
	 * @throws SQLException
	 * @throws GroupAlreadyExists
	 */
	@Test(expected=ForbiddenAction.class)
	public void testCreateGroupSocialSensei() throws ForbiddenAction, SQLException, GroupAlreadyExists {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertFalse(ramen.createGroup("group1111", "desc", null, true, false, false));
	}
	
	/**
	 * You can not create the same group twice
	 * @throws ForbiddenAction
	 * @throws SQLException
	 * @throws GroupAlreadyExists
	 */
	@Test(expected=GroupAlreadyExists.class)
	public void testCreateGroupTwice() throws ForbiddenAction, SQLException, GroupAlreadyExists {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		ramen.createGroup("group11111", "desc", null, false, false, false);
		assertTrue(ramen.createGroup("group11111", "desc", null, false, false, false));
	}
	
	/**
	 * Test method for join a study group as student
	 * @throws SQLException 
	 * @throws GroupAlreadyExists 
	 * @throws ForbiddenAction 
	 * @throws InvalidMessage 
	 */
	@Test
	public void testJoinGroupStudentsjoinsStudy() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		ramen.createGroup("group3", "desc", null, false, false, false);
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertTrue(ramen.joinGroup(ramen.getDAO().getGdb().getGroup("group3")));
	}

	/**
	 * Test for join a social group as a student
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	@Test
	public void testJoinGroupStudentsjoinsSocial() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		ramen.createGroup("group33", "desc", null, true, false, false);
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.joinGroup(ramen.getDAO().getGdb().getGroup("group33")));
	}
	
	/**
	 * Test for joining a group, a JoinRequest is generated
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	@Test
	public void testJoinGroupThrowingRequest() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		ramen.createGroup("sgpm", "desc", null, true, true, true);
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.joinGroup(ramen.getDAO().getGdb().getGroup("sgpm")));
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertTrue(ramen.getCurrentUser().getInbox().size()==1);
	}
	
	/**
	 * Joining a group implies joining the subgroups
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	@Test
	public void testJoinGroupStudentsGroupsAndSubgroups() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		ramen.createGroup("group333", "desc", null, true, false, false);
		ramen.createGroup("group3333", "desc", ramen.getDAO().getGdb().getGroup("group333"), true, false, false);
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.joinGroup(ramen.getDAO().getGdb().getGroup("group333")));
		assertTrue(ramen.getDAO().getGdb().getGroup("group333.group3333").getMembers().size()==2);
	}
	
	/**
	 * You can not answer question as a Sensei
	 * @throws SQLException 
	 * @throws InvalidMessage 
	 * @throws ForbiddenAction 
	 */
	@Test(expected=ForbiddenAction.class)
	public void testSendAnswerSensei() throws ForbiddenAction, InvalidMessage, SQLException {
		ramen.login("oscar.casals@mail.gob", "orcsmb457");
		ramen.sendMessage(ramen.getDAO().getGdb().getGroup("studygroupofoscar"), "The real question", "What do you think about CIREL?", true);
		ramen.login("elvis.domingo@mail.gob", "esdomb467");
		ramen.sendAnswer(ramen.getDAO().getGdb().getGroup("studygroupofoscar"), "I will fail", "fail?", (Question) ramen.getCurrentUser().getInbox().get(0).getReference());
	}
	
	/**
	 * Answer test
	 * @throws ForbiddenAction
	 * @throws InvalidMessage
	 * @throws SQLException
	 * @throws GroupAlreadyExists
	 */
	@Test
	public void testSendAnswerStudent() throws ForbiddenAction, InvalidMessage, SQLException, GroupAlreadyExists {
		ramen.login("oscar.casals@mail.gob", "orcsmb457");
		ramen.createGroup("studygroupofoscar2", "studygroupofoscar2", null, false, false, false);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("studygroupofoscar2"));
		ramen.login("oscar.casals@mail.gob", "orcsmb457");
		ramen.sendMessage(ramen.getDAO().getGdb().getGroup("studygroupofoscar2"), "The real question 2", "What do you think about SOPER?", true);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		assertTrue(ramen.sendAnswer(ramen.getDAO().getGdb().getGroup("studygroupofoscar2"), "I will fail", "fail?", (Question) ramen.getCurrentUser().getInbox().get(0).getReference()));
	}
	//TODO: can you answer twice? @Test
	//public void testSendAnswerTwice() {
	//	fail("Not yet implemented"); 
	//}
	
	/**
	 * Test method for sendMessage throwing a MessageRequest
	 * @throws SQLException 
	 * @throws GroupAlreadyExists 
	 * @throws ForbiddenAction 
	 * @throws InvalidMessage 
	 */
	@Test
	public void testSendMessageThrowingRequest() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("benjamin.reyes@etts.com","bnrsem747");
		ramen.createGroup("socialgroupofBenj", "desc", null, true, false, true);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("socialgroupofbenj"));
		ramen.sendMessage(ramen.getDAO().getGdb().getGroup("socialgroupofbenj"), "I will throw a request", "test", false);
		ramen.login("benjamin.reyes@etts.com","bnrsem747");
		assertTrue(ramen.listInbox().size()==1);
		assertTrue(ramen.listInbox().get(0).getReference() instanceof MessageRequest);
	}

	/**
	 * Handle a Message Request (true case)
	 * @throws SQLException
	 * @throws InvalidMessage
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 */
	@Test
	public void testHandleMessageRequestTrue() throws SQLException, InvalidMessage, ForbiddenAction, GroupAlreadyExists {
		ramen.login("jesus.reyes@alla.net","jsrsat447");
		ramen.createGroup("socialgroupofJesus", "desc", null, true, false, true);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("socialgroupofjesus"));
		ramen.sendMessage(ramen.getDAO().getGdb().getGroup("socialgroupofjesus"), "I will throw a request", "test", false);
		ramen.login("jesus.reyes@alla.net","jsrsat447");
		ramen.handleRequest((Request) ramen.listInbox().get(0).getReference(), true);
		assertTrue(ramen.listInbox().size()==2);
	}
	
	/**
	 * Handle a Message Request (false case)
	 * @throws SQLException
	 * @throws InvalidMessage
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 */
	@Test
	public void testHandleMessageRequestFalse() throws SQLException, InvalidMessage, ForbiddenAction, GroupAlreadyExists {
		ramen.login("pedro.reyes@etts.com","porsem447");
		ramen.createGroup("socialgroupofpedro", "desc", null, true, false, true);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("socialgroupofpedro"));
		ramen.sendMessage(ramen.getDAO().getGdb().getGroup("socialgroupofpedro"), "I will throw a request", "test", false);
		ramen.login("pedro.reyes@etts.com","porsem447");
		ramen.handleRequest((Request) ramen.listInbox().get(0).getReference(), false);
		assertTrue(ramen.listInbox().size()==1);
	}
	
	/**
	 * You can not handle a request twice
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	@Test(expected=ForbiddenAction.class)
	public void testHandleMessageRequestTwice() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("alvaro.reyes@ddm.es","aorsds545");
		ramen.createGroup("socialgroupofalvaro", "desc", null, true, false, true);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("socialgroupofalvaro"));
		ramen.sendMessage(ramen.getDAO().getGdb().getGroup("socialgroupofalvaro"), "I will throw a request", "test", false);
		ramen.login("alvaro.reyes@ddm.es","aorsds545");
		ramen.handleRequest((Request) ramen.listInbox().get(0).getReference(), false);
		ramen.handleRequest((Request) ramen.listInbox().get(0).getReference(), true);
		assertTrue(ramen.listInbox().size()==1);
	}
	
	/**
	 * Test method for handle a join request (true case)
	 * @throws SQLException 
	 * @throws GroupAlreadyExists 
	 * @throws ForbiddenAction 
	 * @throws InvalidMessage 
	 */
	@Test
	public void testHandleJoinRequestTrue() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("marta.reyes@delta.hom","marsdm448");
		ramen.createGroup("socialgroupofmarta", "desc", null, true, true, false);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("socialgroupofmarta"));
		ramen.login("marta.reyes@delta.hom","marsdm448");		
		ramen.handleRequest((Request) ramen.listInbox().get(0).getReference(), true);
		assertTrue(ramen.getDAO().getGdb().getGroup("socialgroupofmarta").getMembers().size()==2);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		assertTrue(ramen.getDAO().getGdb().getGroup("socialgroupofmarta").getMembers().contains(ramen.getCurrentUser()));
	}

	/**
	 * Test for handle a join request (false case)
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	@Test
	public void testHandleJoinRequestFalse() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("angel.moneda@etts.com","almaem457");
		ramen.createGroup("socialgroupofangel", "desc", null, true, true, false);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("socialgroupofangel"));
		ramen.login("angel.moneda@etts.com","almaem457");	
		ramen.handleRequest((Request) ramen.listInbox().get(0).getReference(), false);
		assertFalse(ramen.getDAO().getGdb().getGroup("socialgroupofangel").getMembers().size()==2);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		assertFalse(ramen.getDAO().getGdb().getGroup("socialgroupofangel").getMembers().contains(ramen.getCurrentUser()));
	}

	/**
	 * You can not handle a request twice
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	@Test(expected=ForbiddenAction.class)
	public void testHandleJoinRequestTwice() throws ForbiddenAction, GroupAlreadyExists, SQLException, InvalidMessage {
		ramen.login("blanca.atila@ddm.es","baaads545");
		ramen.createGroup("socialgroupofblanca", "desc", null, true, true, false);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		ramen.joinGroup(ramen.getDAO().getGdb().getGroup("socialgroupofblanca"));
		ramen.login("blanca.atila@ddm.es","baaads545");
		ramen.handleRequest((Request) ramen.listInbox().get(0).getReference(), false);
		ramen.handleRequest((Request) ramen.listInbox().get(0).getReference(), true);
		assertFalse(ramen.getDAO().getGdb().getGroup("socialgroupofblanca").getMembers().size()==2);
		ramen.login("almudena.alonso@alca.es", "aaaoas756");
		assertFalse(ramen.getDAO().getGdb().getGroup("socialgroupofblanca").getMembers().contains(ramen.getCurrentUser()));
	}

	
	/**
	 * Test method for block. Students blocks a Sensei (not allowed, return false)
	 * @throws SQLException 
	 */
	@Test
	public void testBlockStudentBlocksSensei() throws SQLException {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertFalse(ramen.block(ramen.getDAO().getUdb().getUser("leonardo.martin@mail.gob")));
		assertFalse(ramen.getCurrentUser().getBlocked().contains(ramen.getDAO().getUdb().getUser("leonardo.martin@mail.gob")));
	}
	
	/**
	 * A Students blocks another student
	 * @throws SQLException
	 */
	@Test
	public void testBlockStudentBlocksStudent() throws SQLException {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertTrue(ramen.block(ramen.getDAO().getUdb().getUser("luis.martin@etp.com")));
		assertTrue(ramen.getCurrentUser().getBlocked().contains(ramen.getDAO().getUdb().getUser("luis.martin@etp.com")));
	}

	/**
	 * A sensei blocks another Sensei
	 * @throws SQLException
	 */
	@Test
	public void testBlockSenseiBlocksSensei() throws SQLException {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.block(ramen.getDAO().getUdb().getUser("fernando.lopez@mail.gob")));
		assertTrue(ramen.getCurrentUser().getBlocked().contains(ramen.getDAO().getUdb().getUser("fernando.lopez@mail.gob")));
	}
	
	/**
	 * A sensei blocks a student
	 * @throws SQLException
	 */
	@Test
	public void testBlockSenseiBlocksStudent() throws SQLException {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.block(ramen.getDAO().getUdb().getUser("luis.martin@etp.com")));
	}

	/**
	 * A sensei blocks a group
	 * @throws SQLException
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 */
	@Test
	public void testBlockSenseiBlocksGroup() throws SQLException, ForbiddenAction, GroupAlreadyExists {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		ramen.createGroup("group2", "desc", null, true, false, false);
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.block(ramen.getDAO().getGdb().getGroup("group2")));
		assertTrue(ramen.getCurrentUser().getBlocked().contains(ramen.getDAO().getGdb().getGroup("group2")));
	}
	
	/**
	 * A student blocks a group
	 * @throws SQLException
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 */
	@Test
	public void testBlockStudentBlocksGroup() throws SQLException, ForbiddenAction, GroupAlreadyExists {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		ramen.createGroup("group22", "desc", null, false, false, false);
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertTrue(ramen.block(ramen.getDAO().getGdb().getGroup("group22")));
		assertTrue(ramen.getCurrentUser().getBlocked().contains(ramen.getDAO().getGdb().getGroup("group22")));
	}
	
	/**
	 * If you block a group, you also block the subgroups
	 * @throws SQLException
	 * @throws ForbiddenAction
	 * @throws GroupAlreadyExists
	 */
	@Test
	public void testBlockWithSubgroups() throws SQLException, ForbiddenAction, GroupAlreadyExists {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		ramen.createGroup("group221", "desc", null, false, false, false);
		ramen.createGroup("group2211", "desc", ramen.getDAO().getGdb().getGroup("group221"), false, false, false);
		ramen.login("maria.martin@ddm.es", "mamnds455");
		assertTrue(ramen.block(ramen.getDAO().getGdb().getGroup("group221")));
		assertTrue(ramen.getCurrentUser().getBlocked().contains(ramen.getDAO().getGdb().getGroup("group221")));
		assertTrue(ramen.getCurrentUser().getBlocked().contains(ramen.getDAO().getGdb().getGroup("group221.group2211")));
	}
	/**
	 * Test method for {@link jp.ramen.RAMEN#unblock(jp.ramen.Entity)}.
	 * @throws SQLException 
	 */
	@Test
	public void testUnblockAnUser() throws SQLException {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.block(ramen.getDAO().getUdb().getUser("luis.martin@etp.com")));
		assertTrue(ramen.unblock(ramen.getDAO().getUdb().getUser("luis.martin@etp.com")));
	}

	/**
	 * Test method for {@link jp.ramen.RAMEN#unblock(jp.ramen.Entity)}.
	 * @throws SQLException 
	 * @throws GroupAlreadyExists 
	 * @throws ForbiddenAction 
	 */
	@Test
	public void testUnblockAGroup() throws SQLException, ForbiddenAction, GroupAlreadyExists {
		ramen.login("maria.martin@ddm.es", "mamnds455");
		ramen.createGroup("group5", "desc", null, true, false, false);
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.block(ramen.getDAO().getGdb().getGroup("group5")));
		assertTrue(ramen.getCurrentUser().getBlocked().contains(ramen.getDAO().getGdb().getGroup("group5")));
		assertTrue(ramen.unblock(ramen.getDAO().getGdb().getGroup("group5")));
	}
	
	/**
	 * Test method for {@link jp.ramen.RAMEN#getCurrentUser()}.
	 */
	@Test
	public void testGetCurrentUser() {
		ramen.login("leonardo.martin@mail.gob", "lomnmb757");
		assertTrue(ramen.getCurrentUser().equals(ramen.getDAO().getUdb().getUser("leonardo.martin@mail.gob")));
	}
}
