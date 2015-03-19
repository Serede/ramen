package jp.ramen;
import java.util.Calendar;
import java.util.Date;

public class Message {
	private final int MAX_LENGTH = 200;
	
	private String subject, text;
	private boolean accepted=false;
	private Calendar time;
	protected User author;
	private Entity to;
	
	/* DB */
	public Message(String subject, String text, boolean acpt, Date time) {
		this.subject = subject;
		this.text = text;
		this.accepted = acpt;
		this.time.setTime(time);
	}
	
	public Message(String subject, String text, User author, Entity to) {
		this.subject = subject;
		//if(text.length()>MAX_LENGTH); //TODO: Exc
		this.text = text;
		time = Calendar.getInstance();
		this.author = author;
		this.to = to;
		to.addToMessages(this); //TODO: Exc
	}
	
	public Message(String subject, String text, User author, Entity to, Date time) {
		this.subject = subject;
		if(text.length()>MAX_LENGTH); //TODO: Exc
		this.text = text;
		this.time.setTime(time);
		this.author = author;
		this.to = to;
		to.addToMessages(this); //TODO: Exc
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
	//TODO: too many getters	
}
