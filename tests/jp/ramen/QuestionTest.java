/**
 * 
 */
package jp.ramen;

import static org.junit.Assert.*;
import jp.ramen.exceptions.InvalidMessage;

import org.junit.Before;
import org.junit.Test;

/**
 * Question test
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class QuestionTest {
	private User u1, u2, u3;
	private Question q;
	private Group g;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		u1 = new Sensei("u1", "pass");
		u2 = new Student("u2", "pass");
		u3 = new Student("u3", "pass");
		g = new StudyGroup("subj", "desc", null,u1);
		g.addMember(u2);
		g.addMember(u3);
		q = new Question("subj", "desc", u1, g);
	}

	/**
	 * Test method for {@link jp.ramen.Question#addAnswer(jp.ramen.Answer)}.
	 * @throws InvalidMessage 
	 */
	@Test
	public void testAddAnswer() throws InvalidMessage {
		@SuppressWarnings("unused")
		Answer a = new Answer("text", "text", u2, g, q);
		assertTrue(q.whoAnswered().contains(u2));
	}

	/**
	 * The same answer can not be added twice
	 * @throws InvalidMessage
	 */
	@Test
	public void testAddAnswerTwice() throws InvalidMessage {
		Answer a = new Answer("text", "text", u2, g, q);
		q.addAnswer(a);
		assertTrue(q.howManyAnswered()==1);
	}
	
	/**
	 * Test method for {@link jp.ramen.Question#howManyAnswered()}.
	 */
	@Test
	public void testHowManyAnsweredNoAnswers() {
		assertTrue(q.howManyAnswered()==0);
	}

	/**
	 * Test method for {@link jp.ramen.Question#howManyDidntAnswer()}.
	 */
	@Test
	public void testHowManyDidntAnswer() {
		assertTrue(q.howManyDidntAnswer()==2);
	}

	/**
	 * Test method for {@link jp.ramen.Question#whoAnswered()}.
	 * @throws InvalidMessage 
	 */
	@Test
	public void testWhoAnswered() throws InvalidMessage {
		@SuppressWarnings("unused")
		Answer a = new Answer("text", "text", u2, g, q);
		assertTrue(q.whoAnswered().contains(u2));
	}

	/**
	 * Test method for {@link jp.ramen.Question#whoDidntAnswer()}.
	 */
	@Test
	public void testWhoDidntAnswer() {
		assertTrue(q.whoDidntAnswer().contains(u2) && q.whoDidntAnswer().contains(u3));
	}

}
