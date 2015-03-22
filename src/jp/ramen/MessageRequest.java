package jp.ramen;

import java.sql.Date;

import jp.ramen.exceptions.InvalidMessage;

/**
 * Implementation of message request
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class MessageRequest extends Request {
	private static String HEADER = null;
	private Message mref;
	
	/**
	 * DB constructor
	 * @param subject
	 * @param text
	 * @param time
	 * @throws InvalidMessage
	 */
	public MessageRequest(String subject, String text, Date time) throws InvalidMessage {
		super(subject,text,true,time);
	}
	
	/**
	 * Constructor
	 * @param subject
	 * @param text
	 * @param author
	 * @param to
	 * @param mref
	 * @throws InvalidMessage
	 */
	public MessageRequest(String subject, String text, User author, Entity to, Message mref) throws InvalidMessage {
		super(getHeader() + subject, text, author, ((Group) to).getOwner());
		this.mref = mref;
	}

	/**
	 * 
	 * @return the header shown in the request
	 */
	private static String getHeader() {
		if(HEADER==null)
			HEADER = "[PostRequest] ";
		return HEADER;
	}

	@Override
	public void setRequest(boolean accepted) {
		if(this.isAccepted() == false) return;
		this.setAccepted(false); //Lock
		mref.setAccepted(accepted);
		if(accepted) {
			mref.getTo().addToInbox(mref);
		}
	}

	/**
	 * 
	 * @return the moderated message
	 */
	public Message getRef() {
		return mref;
	}

	/**
	 * Sets the moderated message
	 * @param mref
	 */
	public void setRef(Message mref) {
		this.mref = mref;
	}

}
