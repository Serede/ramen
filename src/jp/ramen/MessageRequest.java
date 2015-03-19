package jp.ramen;

import java.sql.Date;

public class MessageRequest extends Request {
	private Message mref;
	
	/* DB */
	public MessageRequest(String subject, String text, Date time) {
		super(subject,text,true,time);
	}
	
	public MessageRequest(String subject, String text, User author, Entity to, Message mref) {
		super(getHeader() + subject, text, author, ((Group) to).getOwner());
		this.mref = mref;
	}

	private static String getHeader() {
		if(HEADER==null)
			HEADER = "[PostRequest] ";
		return HEADER;
	}

	public void setRequest(boolean accepted) {
		/* Check if the request was already handled */
		if(this.isAccepted() == true) return;
		this.setAccepted(true);
		mref.setAccepted(accepted);
		if(accepted) {
			mref.getTo().addToInbox(mref);
		}
	}

	public Message getRef() {
		return mref;
	}

	public void setRef(Message mref) {
		this.mref = mref;
	}

}
