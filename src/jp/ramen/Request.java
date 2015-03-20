package jp.ramen;

import java.sql.Date;
import java.sql.SQLException;

import jp.ramen.exceptions.InvalidMessage;

public abstract class Request extends Message {
	/* DB */
	public Request(String subject, String text, boolean acpt, Date time) throws InvalidMessage {
		super(subject, text, acpt, time);
	}
	
	public Request(String subject, String text, User author, Entity to) throws InvalidMessage {
		super(subject, text, author, to);
	}


	public abstract void setRequest(boolean accepted) throws SQLException;

}
