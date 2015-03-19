package jp.ramen;
import java.util.Calendar;
import java.util.Date;

public class Message {
	private final int MAX_LENGTH = 200;
	private String subject, text;
	private boolean accepted=false;
	private Calendar time;
	private User author;
	private Entity to;
	public Message(String subject, String text, User author, Entity to) {
		this.subject = subject;
		if(text.length()>MAX_LENGTH); //TODO: Exc
		this.text = text;
		this.author = author;
		this.to = to;
		if(to.addToMessages(this)); //TODO: Exc
		time = Calendar.getInstance();
	}
	
	public Message(String subject, String text, User author, Entity to, Date date) {
		this.subject = subject;
		if(text.length()>MAX_LENGTH); //TODO: Exc
		this.text = text;
		this.author = author;
		this.to = to;
		if(to.addToMessages(this)); //TODO: Exc
		time.setTime(date);
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

	public Entity getTo() {
		return to;
	}
	
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	//TODO: too many getters
	
	
	
}
