package jp.ramen;

import java.sql.Date;

import jp.ramen.exceptions.InvalidMessage;

public class JoinRequest extends Request {
	private static String HEADER = null;
	private Group gref;
	
	/* DB */
	public JoinRequest(String subject, String text, Date time) throws InvalidMessage {
		super(subject, text, true, time);
	}
	
	public JoinRequest(String subject, String text, User author, Entity to) throws InvalidMessage {
		super(getHeader() + subject, text, author, ((Group) to).getOwner());
		this.gref = (Group) to;
	}

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

	public Group getRef() {
		return gref;
	}

	public void setRef(Group gref) {
		this.gref = gref;
	}
}
