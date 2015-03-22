package jp.ramen;

import java.sql.Date;

import jp.ramen.exceptions.InvalidMessage;

/**
 * Implementation of the join request 
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class JoinRequest extends Request {
	private static String HEADER = null;
	private Group gref;
	
	/**
	 * DB constructor
	 * @param subject
	 * @param text
	 * @param time
	 * @throws InvalidMessage
	 */
	public JoinRequest(String subject, String text, Date time) throws InvalidMessage {
		super(subject, text, true, time);
	}
	
	/**
	 * Constructor
	 * @param subject
	 * @param text
	 * @param author
	 * @param to
	 * @throws InvalidMessage
	 */
	public JoinRequest(String subject, String text, User author, Entity to) throws InvalidMessage {
		super(getHeader() + subject, text, author, ((Group) to).getOwner());
		this.gref = (Group) to;
	}

	/**
	 * 
	 * @return the header shown in the request
	 */
	private static String getHeader() {
		if(HEADER==null)
			HEADER = "[JoinRequest] "; //TODO from file
		return HEADER;
	}

	@Override
	public void setRequest(boolean accepted) {
		if(this.isAccepted() == false) return;
		this.setAccepted(false); //Lock
		if(accepted) {
			gref.addMember(author);
			author.subscribe(gref);
		}
	}

	/**
	 * 
	 * @return the group to join
	 */
	public Group getRef() {
		return gref;
	}

	/**
	 * Sets the group to join
	 * @param gref
	 */
	public void setRef(Group gref) {
		this.gref = gref;
	}
}
