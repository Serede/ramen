package jp.ramen;
/**
 * 
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 *
 */
public class Answer extends Message {

	public Answer(String subject, String text, User author, Entity to, Question q) {
		super(subject, text, author, to);
		q.addAnswer(this);
	}

}
