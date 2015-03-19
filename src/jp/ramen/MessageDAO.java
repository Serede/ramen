package jp.ramen;

public class MessageDAO extends DAO {
	private static MessageDAO mdb=null;
	private final String DEFAULT_JOIN_TEXT;

	private MessageDAO() {
		DEFAULT_JOIN_TEXT = "d"; //TODO: Load it from file
	}

	public static MessageDAO getInstance() {
		if(mdb==null) mdb=new MessageDAO();
		return mdb;
	}

	public boolean createMessageRequest(Message msg, Entity to) {
		Group g = (Group) to;
		Request req = new MessageRequest("MessageRequest: "+msg.getSubject(),msg.getText(),RAMEN.getInstance().getCurrentUser(), g.getOwner(), msg);
		return addMessage(req);
	}
	
	public boolean createJoinRequest(User u, Group g) {
		Request req = new JoinRequest("Join Request: "+u.getName(),DEFAULT_JOIN_TEXT,u, g.getOwner(), u ,g);
		return addMessage(req);
	}

	public boolean addMessage(Message msg) {
		if(msg.getTo().isModerated()==false) {
			return msg.getTo().addToInbox(msg);
		}
		else {
			return mdb.createMessageRequest(msg, msg.getTo());
		}
		
	}

}
