package jp.ramen;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.sql.*;

import jp.ramen.exceptions.InvalidMessage;

/**
 * Implementation of the messages function for the db
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class MessageDAO {
	private final byte NONE_TYPE = 0;
	private final byte JREQ_TYPE = 1;
	private final byte MREQ_TYPE = 2;
	private final byte ANSW_TYPE = 3;
	private final byte QUES_TYPE = 4;
	private final String JOIN_TEXT;
	private static MessageDAO mdb = null;
	private Map<Long, Message> messages;

	/**
	 * Constructor (singleton)
	 */
	private MessageDAO() {
		JOIN_TEXT = " wants to join "; // TODO from file
		messages = new HashMap<>();
	}

	/**
	 * 
	 * @return the instance of the message db
	 */
	public static MessageDAO getInstance() {
		if (mdb == null)
			mdb = new MessageDAO();
		return mdb;
	}

	/**
	 * Searchs for a message with his ID
	 * @param mid
	 * @return the message if exists, null otherwise
	 */
	public Message getMessage(Long mid) {
		return messages.get(mid);
	}
	
	/**
	 * 
	 * @param m
	 * @return the id of the message or 0
	 */
	public Long getID(Message m) {
		for (Entry<Long,Message> e : messages.entrySet()) {
			if (m.equals(e.getValue()))
				return e.getKey();
		}
		return 0L; // TODO: Exception
	}

	/**
	 * Load the data into memory
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	public void load() throws SQLException, InvalidMessage {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MID,TYPE,SUBJ,TEXT,ACPT,TIME FROM MESSAGES");
			while(rs.next()) {
				Message m = null;
				switch(rs.getByte(2)) {
					case NONE_TYPE:
						m = new Message(rs.getString(3),rs.getString(4),rs.getBoolean(5),rs.getTimestamp(6));
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
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	/**
	 * Sets the attributes of the messages
	 * @param udb
	 * @param gdb
	 * @throws SQLException
	 */
	public void link(UserDAO udb, GroupDAO gdb) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			ResultSet rs;
			
			/* FROM AND TO */
			rs = stmt.executeQuery("SELECT MID,TYPE,AUTH,TO FROM MESSAGES");
			while(rs.next()) {
				Message m = messages.get(rs.getLong(1));
				long to;
				m.setAuthor(udb.getUser(rs.getLong(3)));
				if((to = rs.getLong(4))>UserDAO.MAX_UID)
					m.setTo(gdb.getGroup(to));
				else
					m.setTo(udb.getUser(to));
			}
			
			/* REFS */
			rs = stmt.executeQuery("SELECT M.MID,TYPE,`REF` FROM MESSAGES AS M NATURAL JOIN M_REFS AS R");
			while(rs.next()) {
				Message m = messages.get(rs.getLong(1));
				switch(rs.getByte(2)) {
					case JREQ_TYPE:
						((JoinRequest)m).setRef(gdb.getGroup(rs.getLong(3)));
						break;
					case MREQ_TYPE:
						((MessageRequest)m).setRef(mdb.getMessage(rs.getLong(3)));
						break;
					case ANSW_TYPE:
						((Answer)m).setRef((Question) mdb.getMessage(rs.getLong(3)));
						break;
					default:
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	/**
	 * Clears memory map
	 */
	public void clear() {
		messages = new TreeMap<>();
	}
	
	/**
	 * Adds a message to the db
	 * @param msg
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	public boolean addMessage(Message msg) throws SQLException {
			Connection db = null;
			Statement stmt = null;
			ResultSet rs;
			DAO ddb = DAO.getInstance();
			byte type;
			long ref;
			
			try {
				db = ddb.connect();
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
				
				String date = new Timestamp(msg.getTime().getTimeInMillis()).toString();
				date = date.substring(0, date.lastIndexOf('.'));
				
				String insert_message = "INSERT INTO MESSAGES VALUES("
						+ "DEFAULT"
						+ ","
						+ type
						+ ","
						+ "'" + msg.getSubject() + "'"
						+ ","
						+ "'" + msg.getText() + "'"
						+ ","
						+ msg.isAccepted()
						+ ","
						+ "TIMESTAMP '" + date + "'" 
						+ ","
						+ udb.getID(msg.author)
						+ ","
						+ ((msg.getTo() instanceof User) ?
								udb.getID((User) msg.getTo()) :
									gdb.getID((Group) msg.getTo()))
						+ ")";
				
				stmt.executeUpdate(insert_message);
				stmt.close();
				
				long mid;
				stmt = db.createStatement();
				rs = stmt.executeQuery("SELECT MAX(MID) FROM MESSAGES");
				if(rs.next())
					mid = rs.getLong(1);
				else
					return false;
				
				if(ref != 0) {
					String add_referece = "INSERT INTO M_REFS VALUES("
							+ mid
							+ ","
							+ ref
							+ ")";
					stmt.executeUpdate(add_referece);
				}
				
				messages.put(mid,msg);
				if(msg.isAccepted())
					return addLocalMessage(msg);
				return true;
			} catch (SQLException e) {
				throw e;
			} finally {
				if(stmt != null) stmt.close();
				if(db != null) db.close();
			}
	}

	/**
	 * Add a localMessage to the db
	 * @param m
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	private boolean addLocalMessage(Message m) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		UserDAO udb = ddb.getUdb();
		Entity to;
		
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			if((to = m.getTo()) instanceof User) {
				String query = "INSERT INTO U_INBOX VALUES("
						+ udb.getID((User) m.getTo())
						+ ","
						+ mdb.getID(m)
						+ ","
						+ false
						+ ")";
				if(!((User) to).getBlocked().contains(m.getAuthor()))
					stmt.execute(query);
			} else {
				for(User u : ((Group)to).getMembers()) {
					String query = "INSERT INTO U_INBOX VALUES("
							+ udb.getID(u)
							+ ","
							+ mdb.getID(m)
							+ ","
							+ false
							+ ")";
					if(!u.getBlocked().contains(to) && !u.getBlocked().contains(m.getAuthor()))
						stmt.execute(query);
				}
			}
			to.addToInbox(m);
			m.getAuthor().addSent(m);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			stmt.close();
			db.close();
		}		
	}
	
	/**
	 * Reads a message in the db
	 * @param u
	 * @param m
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	public boolean readLocalMessage(User u, Message m) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		UserDAO udb = ddb.getUdb();
		String query = "UPDATE U_INBOX SET READ=TRUE WHERE "
				+ "UID=" + udb.getID(u)
				+ " AND "
				+ "MSG=" + mdb.getID(m);
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			stmt.execute(query);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			stmt.close();
			db.close();
		}		
	}
	
	/**
	 * Delete a LocalMessage
	 * @param u
	 * @param m
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	public boolean delLocalMessage(User u, Message m) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		UserDAO udb = ddb.getUdb();
		String query = "DELETE FROM U_INBOX WHERE "
				+ "UID=" + udb.getID(u)
				+ " AND "
				+ "MSG=" + mdb.getID(m);
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			stmt.execute(query);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			stmt.close();
			db.close();
		}		
	}
	
	/**
	 * Adds a message request
	 * @param msg
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	public boolean addMessageRequest(Message msg) throws SQLException, InvalidMessage {
		Request req = new MessageRequest(msg.getSubject(), msg.getText(),
				msg.getAuthor(), msg.getTo(), msg);
		msg.setAccepted(false);
		if(addMessage(msg)==false) return false;
		if(addMessage(req)==false) return false;
		return true;
	}

	/**
	 * Adds a join request
	 * @param u
	 * @param g
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 * @throws InvalidMessage
	 */
	public boolean addJoinRequest(User u, Group g) throws SQLException, InvalidMessage {
		Request req = new JoinRequest(u.getName(), u.getName()
				+ JOIN_TEXT + g.getCode(), u, g);
		return addMessage(req);
	}

	/**
	 * Accepts a message request
	 * @param mr
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
	public boolean acceptMessage(MessageRequest mr) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		String query = "UPDATE MESSAGES SET ACPT=TRUE WHERE "
				+ "MID=" + mdb.getID(mr.getRef());
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			stmt.execute(query);
			mr.setRequest(true);
			addLocalMessage(mr.getRef());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			stmt.close();
			db.close();
		}		
	}
	
}
