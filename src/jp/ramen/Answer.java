package jp.ramen;

import java.sql.Date;

/**
 * 
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 *
 */
public class Answer extends Message {
	private Question qref;

	public Answer(String subject, String text, boolean acpt, Date time) {
		super(subject,text,acpt,time);
	}
	
	public Answer(String subject, String text, User author, Entity to, Question q) {
		super(subject, text, author, to);
		q.addAnswer(this);
	}

	public Question getRef() {
		return qref;
	}

	public void setRef(Question qref) {
		this.qref = qref;
		qref.addAnswer(this);
	}

}
