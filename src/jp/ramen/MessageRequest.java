package jp.ramen;

public class MessageRequest extends Request {
	private Message toModerate;
	public MessageRequest(String subject, String text, User author, Entity to, Message m) {
		super(subject, text, author, to);
		this.toModerate = m;
	}
	
	public void setRequest(boolean accepted) {
		/* Check if the request was already handled */
		if(this.isAccepted() == true) return;
		this.setAccepted(true);
		toModerate.setAccepted(accepted);
		if(accepted) {
			toModerate.getTo().addToInbox(toModerate);
		}
	}

}
