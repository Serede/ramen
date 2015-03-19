package jp.ramen;

import java.util.Map;
import java.util.Map.Entry;
import java.sql.*;

public class MessageDAO extends DAO {
	private final byte NONE_TYPE = 0;
	private final byte JREQ_TYPE = 1;
	private final byte MREQ_TYPE = 2;
	private final byte ANSW_TYPE = 3;
	private final byte QUES_TYPE = 4;
	private final String DEFAULT_JOIN_TEXT;
	private static MessageDAO mdb = null;
	private Map<Long, Message> messages;

	private MessageDAO() {
		DEFAULT_JOIN_TEXT = " wants to join "; // TODO from file
	}

	public static MessageDAO getInstance() {
		if (mdb == null)
			mdb = new MessageDAO();
		return mdb;
	}

	public Message getMessage(Long mid) {
		return messages.get(mid);
	}
	
	public Long getID(Message m) {
		for (Entry<Long,Message> e : messages.entrySet()) {
			if (m.equals(e.getValue()))
				return e.getKey();
		}
		return 0L; // TODO: Exception
	}

	public void load() throws SQLException {
		Connection db = null;
		Statement stmt = null;
		
		try {
			db = DAO.connect();
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MID,TYPE,SUBJ,TEXT,ACPT,TIME FROM MESSAGES");
			while(rs.next()) {
				Message m = null;
				switch(rs.getByte(2)) {
					case NONE_TYPE:
						m = new Message(rs.getString(3),rs.getString(4),rs.getBoolean(5),rs.getDate(6));
						break;
					case JREQ_TYPE:
						m = new JoinRequest(rs.getString(3),rs.getString(4),rs.getDate(6));
						break;
					case MREQ_TYPE:
						m = new MessageRequest(rs.getString(3),rs.getString(4),rs.getDate(6));
						break;
					case ANSW_TYPE:
						m = new Answer(rs.getString(3),rs.getString(4),rs.getBoolean(5),rs.getDate(6));
						break;
					case QUES_TYPE:
						m = new Question(rs.getString(3),rs.getString(4),rs.getDate(6));
						break;
				}
				messages.put(rs.getLong(1), m);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			stmt.close();
			db.close();
		}
	}
	
	public void link(UserDAO udb, GroupDAO gdb) {
		Connection db = null;
		Statement stmt = null;
		
		try {
			db = DAO.connect();
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT UID,TYPE,AUTH,TO,REF FROM MESSAGES");
			while(rs.next()) {
				Message m = messages.get(rs.getLong(1));
				/* AUTH */
				m.setAuthor(udb.getUser(rs.getLong(3)));
				/* TO */
				long to;
				if((to = rs.getLong(4))>UserDAO.MAX_UID)
					m.setTo(gdb.getGroup(to));
				else
					m.setTo(udb.getUser(to));
				/* REF */
				long ref = rs.getLong(5);
				switch(rs.getByte(2)) {
					case JREQ_TYPE:
						((JoinRequest)m).setRef(gdb.getGroup(ref));
						break;
					case MREQ_TYPE:
						((MessageRequest)m).setRef(mdb.getMessage(ref));
						break;
					case ANSW_TYPE:
						((Answer)m).setRef((Question) mdb.getMessage(ref));
						break;
					default:
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean addMessage(Message msg) throws SQLException {
		if (msg.getTo().isModerated()) {
			return mdb.addMessageRequest(msg);
		} else {
			Connection db = null;
			Statement stmt = null;
			byte type;
			long ref;
			
			if(msg.getTo().addToInbox(msg)==false) return false;
			
			try {
				db = DAO.connect();
				stmt = db.createStatement();
				UserDAO udb = UserDAO.getInstance();
				GroupDAO gdb = GroupDAO.getInstance();
				
				if (msg instanceof JoinRequest) {
					type = JREQ_TYPE;
					ref = gdb.getID(((JoinRequest)msg).getRef());
				} else if (msg instanceof MessageRequest) {
					type = MREQ_TYPE;
					ref = mdb.getID(((MessageRequest)msg).getRef());
				} else if (msg instanceof Answer) {
					type = ANSW_TYPE;
					ref = mdb.getID(((Answer)msg).getRef());
				} else if (msg instanceof Question) {
					type = QUES_TYPE;
					ref = 0;
				} else {
					type = NONE_TYPE;
					ref = 0;
				}
				
				String query = "INSERT INTO MESSAGES VALUES("
						+ type
						+ ",'"
						+ msg.getSubject()
						+ "','"
						+ msg.getText()
						+ "',"
						+ msg.isAccepted()
						+ ","
						+ udb.getID(msg.author)
						+ ","
						+ ((msg.getTo() instanceof User) ?
								udb.getID((User) msg.getTo()) :
									gdb.getID((Group) msg.getTo()))
						+ ","
						+ ref
						+ ")";
				stmt.executeUpdate(query);
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} finally {
				stmt.close();
				db.close();
			}
		}
	}

	private boolean addMessageRequest(Message msg) throws SQLException {
		Request req = new MessageRequest(msg.getSubject(), msg.getText(),
				msg.getAuthor(), msg.getTo(), msg);
		return addMessage(req);
	}

	public boolean addJoinRequest(User u, Group g) throws SQLException {
		Request req = new JoinRequest(u.getName(), u.getName()
				+ DEFAULT_JOIN_TEXT + g.getCode(), u, g);
		return addMessage(req);
	}

}
