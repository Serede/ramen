package jp.ramen;

import java.sql.Date;

import jp.ramen.exceptions.InvalidMessage;

/**
 * This module implements the answer system for the questions
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 *
 */
public class Answer extends Message {
	private Question qref;

	/**
	 * DB constructor
	 * @param subject
	 * @param text
	 * @param acpt
	 * @param time
	 * @throws InvalidMessage
	 */
	public Answer(String subject, String text, boolean acpt, Date time) throws InvalidMessage {
		super(subject,text,acpt,time);
	}
	
	/**
	 * Constructor
	 * @param subject
	 * @param text
	 * @param author
	 * @param to
	 * @param q
	 * @throws InvalidMessage
	 */
	public Answer(String subject, String text, User author, Entity to, Question q) throws InvalidMessage {
		super(subject, text, author, to);
		qref = q;
		q.addAnswer(this);
	}

	/**
	 * 
	 * @return the answered question
	 */
	public Question getRef() {
		return qref;
	}

	/**
	 * Set the question 
	 * @param qref the question
	 */
	public void setRef(Question qref) {
		this.qref = qref;
		qref.addAnswer(this);
	}

}
