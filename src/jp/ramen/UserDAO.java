/**
 * 
 */
package jp.ramen;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author e303132
 *
 */
public class UserDAO extends DAO {
	private static UserDAO udb = null;
	private Map<Long, User> users;

	private UserDAO() {
		this.users = new HashMap<>();
	}

	public static UserDAO getInstance() {
		if (udb == null)
			udb = new UserDAO();
		return udb;
	}

	public User getUser(String name) {
		for (User u : users.values()) {
			if (u.getName() == name)
				return u;
		}
		return null; // TODO: Exception
	}

	public static String generateSHA(String s) { // TODO para luego cambiar el
													// otro fichero
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(s.getBytes("UTF-8"));
			return new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void populate(String file, boolean sensei) throws Exception {
		BufferedReader buf = null;
		String line = null;
		Connection db = null;
		Statement stmt = null;

		try {
			buf = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));
			db = DAO.connect();
			while ((line = buf.readLine()) != null) {
				String[] words = line.split(":");
				if (words.length == 2) {
					stmt = db.createStatement();
					stmt.executeUpdate("INSERT INTO ENTITIES VALUES()");
					ResultSet rs = stmt
							.executeQuery("SELECT MAX(EID) FROM ENTITIES");
					if (rs.next()) {
						String query = "INSERT INTO USERS VALUES("
								+ rs.getLong(1) + "," + sensei + ",'"
								+ words[0] + "','" + generateSHA(words[1])
								+ "',NULL,NULL,NULL,NULL)";
						stmt.executeUpdate(query);
					}
				}
			}
			buf.close();
		} catch (Exception e) { // TODO: ex
			e.printStackTrace();
		} finally {
			stmt.close();
			db.close();
			buf.close();
		}
	}

	public void load() throws SQLException {
		Connection db = null;
		Statement stmt = null;
		
		try {
			db = DAO.connect();
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT UID,SEN,NAME,PASS FROM USERS");
			while(rs.next()) {
				if(rs.getBoolean(2)) {
					Sensei s = new Sensei(rs.getString(3), rs.getString(4));
					users.put(rs.getLong(1), s);
				} else {
					Student s = new Student(rs.getString(3), rs.getString(4));
					users.put(rs.getLong(1), s);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			stmt.close();
			db.close();
		}
	}
	
	public void link() {
		Connection db = null;
		Statement stmt = null;
		
		try {
			db = DAO.connect();
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT UID,BLOCK,SUBS,SENT,INBOX FROM USERS");
			while(rs.next()) {
				for (Long id : (Long[])rs.getArray(2).getArray()) {
					users.get(rs.getLong(1)).block(users.get(id));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
