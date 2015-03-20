/**
 * 
 */
package jp.ramen;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

public class UserDAO {
	public static long MAX_UID;
	private static UserDAO udb = null;
	private Map<Long, User> users;

	private UserDAO() {
		this.users = new TreeMap<>();
	}

	public static UserDAO getInstance() {
		if (udb == null)
			udb = new UserDAO();
		return udb;
	}
	
	public User getUser(Long uid) {
		return users.get(uid);
	}

	public User getUser(String name) {
		for (User u : users.values()) {
			if (u.getName().equals(name))
				return u;
		}
		return null; // TODO: Exception
	}
	
	public Long getID(User u) {
		for (Map.Entry<Long,User> e : users.entrySet()) {
			if (u.equals(e.getValue()))
				return e.getKey();
		}
		return 0L; // TODO: Exception
	}

	public static String generateSHA(String s) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(s.getBytes("UTF-8"));
			return new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return null;
		}
	}

	public void populate(String file, boolean sensei) throws Exception {
		BufferedReader buf = null;
		String line = null;
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();

		try {
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			db = ddb.connect();
			stmt = db.createStatement();
			
			while ((line = buf.readLine()) != null) {
				String[] words = line.split(":");
				if (words.length == 2) {
					stmt.executeUpdate("INSERT INTO ENTITIES VALUES()");
					ResultSet rs = stmt.executeQuery("SELECT MAX(EID) FROM ENTITIES");
					if (rs.next()) {
						String query = "INSERT INTO USERS VALUES("
								+ rs.getLong(1)
								+ ","
								+ sensei
								+ ","
								+ "'" + words[0] + "'"
								+ ","
								+ "'" + generateSHA(words[1]) + "'"
								+ ")";
						stmt.executeUpdate(query);
					}
				}
			}
			
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(UID) FROM USERS");
			if(rs.next())
				MAX_UID = rs.getLong(1);
		} catch (Exception e) { // TODO: ex
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
			if(buf != null) buf.close();
		}
	}

	public void load() throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT UID,SEN,NAME,PASS FROM USERS");
			
			while(rs.next()) {
				User u = null;
				if(rs.getBoolean(2))
					u = new Sensei(rs.getString(3), rs.getString(4));
				else
					u = new Student(rs.getString(3), rs.getString(4));
				users.put(rs.getLong(1), u);
			}
			
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	public void link(GroupDAO gdb, MessageDAO mdb) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			ResultSet rs;
			
			/* BLOCK LIST */
			rs = stmt.executeQuery("SELECT * FROM U_BLOCK");
			while(rs.next()) {
				User u = udb.getUser(rs.getLong(1));
				Entity e;
				Long eid;
				if((eid = rs.getLong(2)) > MAX_UID)
					e = gdb.getGroup(eid);
				else
					e = udb.getUser(eid);
				u.block(e);
			}
			
			/* INBOX */
			rs = stmt.executeQuery("SELECT * FROM U_INBOX");
			while(rs.next()) {
				User u = udb.getUser(rs.getLong(1));
				Message m = mdb.getMessage(rs.getLong(2));
				u.addToInbox(m,rs.getBoolean(3));
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	public boolean addBlock(User u, Entity blck) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
					
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			GroupDAO gdb = GroupDAO.getInstance();

			stmt = db.createStatement();
			String insert_block = "INSERT INTO U_BLOCK VALUES("
					+ udb.getID(u)
					+ ","
					+ ((blck instanceof User) ?
							udb.getID((User) blck) :
									gdb.getID((Group) blck))
					+ ")";
			stmt.executeUpdate(insert_block);
			
			return u.block(blck);
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	public boolean delBlock(User u, Entity blck) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
					
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			GroupDAO gdb = GroupDAO.getInstance();

			stmt = db.createStatement();
			String delete_block = "DELETE FROM U_BLOCK WHERE "
					+ "`UID`=" + udb.getID(u)
					+ "AND"
					+ "BLCK=" + ((blck instanceof User) ?
							udb.getID((User) blck) :
									gdb.getID((Group) blck))
					+ ")";
			stmt.executeUpdate(delete_block);

			return u.unblock(blck);
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
}
