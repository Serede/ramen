package jp.ramen;
import java.util.Calendar;
import java.util.Date;

import jp.ramen.exceptions.InvalidMessage;

/**
 * The message class
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class Message {
	private final int MAX_LENGTH = 200;
	
	private String subject, text;
	private boolean accepted=true;
	private Calendar time;
	protected User author;
	private Entity to;
	
	/**
	 * DB constructor
	 * @param subject
	 * @param text
	 * @param acpt
	 * @param time
	 * @throws InvalidMessage
	 */
	public Message(String subject, String text, boolean acpt, Date time) throws InvalidMessage {
		if(text.length()>MAX_LENGTH)
			throw new InvalidMessage();
		this.subject = subject;
		this.text = text;
		this.accepted = acpt;
		this.time = Calendar.getInstance();
		this.time.setTime(time);
	}
	
	/**
	 * Constructor
	 * @param subject
	 * @param text
	 * @param author
	 * @param to
	 * @throws InvalidMessage
	 */
	public Message(String subject, String text, User author, Entity to) throws InvalidMessage {
		if(text.length()>MAX_LENGTH)
			throw new InvalidMessage();
		this.subject = subject;
		this.text = text;
		time = Calendar.getInstance();
		this.author = author;
		this.to = to;
	}

	/**
	 * 
	 * @return the subject of the message
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * 
	 * @return the text of the message
	 */
	public String getText() {
		return text;
	}

	/**
	 * 
	 * @return if the message is accepted
	 */
	public boolean isAccepted() {
		return accepted;
	}

	/**
	 * 
	 * @return the time of the message
	 */
	public Calendar getTime() {
		return time;
	}

	/**
	 * 
	 * @return the author of the message
	 */
	public User getAuthor() {
		return author;
	}
	
	/**
	 * Changes the author of a message (Used for the DB link)
	 * @param author
	 */
	public void setAuthor(User author) {
		this.author = author;
	}

	/**
	 * 
	 * @return the destination of the message
	 */
	public Entity getTo() {
		return to;
	}
	
	/**
	 * Changes the attribute (Used for the DB link)
	 * @param to
	 */
	public void setTo(Entity to) {
		this.to = to;
	}
	
	/**
	 * Sets the message as accepted or no
	 * @param accepted
	 */
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public String toString() {
		return "Message [subject=" + subject
				+ ", text=" + text + ", time=" + time.getTime() + ", author=" + author
				+ ", to=" + to + "]";
	}
}
