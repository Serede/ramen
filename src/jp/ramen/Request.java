package jp.ramen;

import java.sql.Date;
import java.sql.SQLException;

import jp.ramen.exceptions.InvalidMessage;

/**
 * Implementation of generalized behaviour of a request
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public abstract class Request extends Message {
	
	/**
	 * DB constructor
	 * @param subject
	 * @param text
	 * @param acpt
	 * @param time
	 * @throws InvalidMessage
	 */
	public Request(String subject, String text, boolean acpt, Date time) throws InvalidMessage {
		super(subject, text, acpt, time);
	}
	
	/**
	 * Constructor
	 * @param subject
	 * @param text
	 * @param author
	 * @param to
	 * @throws InvalidMessage
	 */
	public Request(String subject, String text, User author, Entity to) throws InvalidMessage {
		super(subject, text, author, to);
	}

	/**
	 * Handles the request 
	 * @param accepted
	 * @throws SQLException
	 */
	public abstract void setRequest(boolean accepted) throws SQLException;

}
