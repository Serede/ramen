package jp.ramen;

import java.util.*;

import jp.ramen.exceptions.InvalidMessage;

/**
 * The question class for the Senseis
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class Question extends Message {
	private List<Answer> answers;
	
	/**
	 * DB constructor
	 * @param subject
	 * @param text
	 * @param time
	 * @throws InvalidMessage
	 */
	public Question(String subject, String text, Date time) throws InvalidMessage {
		super(subject,text,true,time);
		answers = new ArrayList<Answer>();
	}
	
	/**
	 * Constructor
	 * @param subject
	 * @param text
	 * @param author
	 * @param to
	 * @throws InvalidMessage
	 */
	public Question(String subject, String text, User author, Entity to) throws InvalidMessage {
		super(subject, text, author, to);
		answers = new  ArrayList<Answer>();
	}

	/**
	 * Adds an answer to the question
	 * @param a
	 * @return true if it was possible, false otherwise
	 */
	public boolean addAnswer(Answer a) {
		if(answers.contains(a)==true) return false;
		return answers.add(a);
	}

	/**
	 * 
	 * @return the list of answers
	 */
	public final List<Answer> reviewAnswers() {
		return Collections.unmodifiableList(answers);
	}

	/**
	 * 
	 * @return how many people answered
	 */
	public int howManyAnswered() {
		return answers.size();
	}

	/**
	 * 
	 * @return how many people didnt answer
	 */
	public int howManyDidntAnswer() {
		return  this.whoDidntAnswer().size();
	}

	/**
	 * 
	 * @return who answered
	 */
	public List<User> whoAnswered() {
		List<User> ret = new ArrayList<>();
		for(Answer a: answers) {
			ret.add(a.getAuthor());
		}
		return ret;
	}
	
	/**
	 * 
	 * @return who didnt answer
	 */
	public List<User> whoDidntAnswer() {
		Group g = (Group) this.getTo(); //TODO: Review
		List<User> ret =  new ArrayList<>(g.getMembers());
		List<User> ans = this.whoAnswered();
		for(User u: g.getMembers()) {
			if(ans.contains(u) || u.getClass() == Sensei.class) {
				ret.remove(u);
			}
		}
		return ret;
	}
}
