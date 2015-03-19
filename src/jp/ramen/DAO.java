package jp.ramen;

import java.sql.*;

public class DAO {
	private final static String DB_NAME = "ramen";
	private final static String DB_INIT = "runscript from 'init.sql'";
	private final static String DB_USER = "eva02";
	private final static String DB_PASS = "asuka";
	private static String DB_URL = null;

	protected static Connection connect() throws SQLException {
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
	}

	public static void create(String db, String st, String pr)
			throws SQLException {
		DB_URL = "jdbc:h2:" + db + "/" + DB_NAME;
		UserDAO udb;
		Connection c = null;
		try {
			udb = UserDAO.getInstance();
			c = DriverManager.getConnection(DB_URL + ";INIT=" + DB_INIT,
					DB_USER, DB_PASS);
			udb.populate(st,false);
			udb.populate(pr,true);
		} catch (Exception e) { // TODO: ex
			e.printStackTrace();
		} finally {
			c.close();
		}
	}

	public static void init(String path) throws SQLException {
		UserDAO udb;
		Connection c = null;
		try {
			c = DriverManager.getConnection("jdbc:h2:" + path + "/" + DB_NAME
					+ ";IFEXISTS=TRUE", DB_USER, DB_PASS);
			udb = UserDAO.getInstance();
			udb.load();
		} catch (SQLException e) { //TODO: ex
			throw e;
		} finally {
			c.close();
		}
	}
	
	public static void main(String[] args) {
		try {
			create("~/ramen","students.txt","professors.txt");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

