package jp.ramen;
import java.util.Calendar;
import java.util.Date;

import jp.ramen.exceptions.InvalidMessage;

public class Message {
	private final int MAX_LENGTH = 200;
	
	private String subject, text;
	private boolean accepted=true;
	private Calendar time;
	protected User author;
	private Entity to;
	
	/* DB */
	public Message(String subject, String text, boolean acpt, Date time) throws InvalidMessage {
		if(text.length()>MAX_LENGTH)
			throw new InvalidMessage();
		this.subject = subject;
		this.text = text;
		this.accepted = acpt;
		this.time = Calendar.getInstance();
		this.time.setTime(time);
	}
	
	public Message(String subject, String text, User author, Entity to) throws InvalidMessage {
		if(text.length()>MAX_LENGTH)
			throw new InvalidMessage();
		this.subject = subject;
		this.text = text;
		time = Calendar.getInstance();
		this.author = author;
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public Calendar getTime() {
		return time;
	}

	public User getAuthor() {
		return author;
	}
	
	public void setAuthor(User author) {
		this.author = author;
	}

	public Entity getTo() {
		return to;
	}
	
	public void setTo(Entity to) {
		this.to = to;
	}
	
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public String toString() {
		return "Message [subject=" + subject
				+ ", text=" + text + ", time=" + time.getTime() + ", author=" + author
				+ ", to=" + to + "]";
	}
	
	
	//TODO: too many getters	
}
