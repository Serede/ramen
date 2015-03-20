package jp.ramen;

import java.sql.Date;

import jp.ramen.exceptions.InvalidMessage;

public class MessageRequest extends Request {
	private static String HEADER = null;
	private Message mref;
	
	/* DB */
	public MessageRequest(String subject, String text, Date time) throws InvalidMessage {
		super(subject,text,true,time);
	}
	
	public MessageRequest(String subject, String text, User author, Entity to, Message mref) throws InvalidMessage {
		super(getHeader() + subject, text, author, ((Group) to).getOwner());
		this.mref = mref;
	}

	private static String getHeader() {
		if(HEADER==null)
			HEADER = "[PostRequest] ";
		return HEADER;
	}

	public void setRequest(boolean accepted) {
		if(this.isAccepted() == false) return;
		this.setAccepted(false); //Lock
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
