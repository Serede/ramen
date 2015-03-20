package jp.ramen;

import java.io.*;
import java.sql.*;

import jp.ramen.exceptions.*;

public class DAO {
	private final static String DB_NAME = "ramen";
	private final static String DB_INIT = "runscript from 'init.sql'";
	private final static String DB_USER = "eva02";
	private final static String DB_PASS = "asuka";
	private final static String DB_WARD = "path.lck";
	private static String DB_URL = null;
	private static DAO ddb = null;
	private static UserDAO udb = null;
	private static GroupDAO gdb = null;
	private static MessageDAO mdb = null;

	private DAO() {
		udb = UserDAO.getInstance();
		gdb = GroupDAO.getInstance();
		mdb = MessageDAO.getInstance();
	}
	
	public static DAO getInstance() {
		if(ddb == null)
			ddb = new DAO();
		return ddb;
	}
	
	public UserDAO getUdb() {
		return udb;
	}

	public GroupDAO getGdb() {
		return gdb;
	}

	public MessageDAO getMdb() {
		return mdb;
	}

	public Connection connect() throws SQLException {
		return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
	}

	public void create(String db, String st, String pr) throws Exception {
		Connection c = null;
		PrintWriter pw = null;
		DB_URL = "jdbc:h2:" + db + "/" + DB_NAME;
		try {
			File ward = new File(DB_WARD);
			if(ward.exists() && !ward.isDirectory()) {
				throw new DbAlreadyExists();
			} else {
				pw = new PrintWriter(DB_WARD,"UTF-8");
				pw.println(DB_URL);
				pw.close();
				c = DriverManager.getConnection(DB_URL + ";INIT=" + DB_INIT,
						DB_USER, DB_PASS);
				udb.populate(st,false);
				udb.populate(pr,true);
			}
		} catch (Exception e) { // TODO: ex
			throw e;
		} finally {
			if(c != null) c.close();
			if(pw != null) pw.close();
		}
	}

	public void init() throws Exception {
		Connection c = null;
		BufferedReader buf = null;
		try {
			File ward = new File(DB_WARD);
			if(ward.exists() && !ward.isDirectory()) {
				String path;
				buf = new BufferedReader(new InputStreamReader(new FileInputStream(DB_WARD)));
				if((path = buf.readLine()) != null) {
					DB_URL = path;
					c = DriverManager.getConnection(DB_URL + ";IFEXISTS=TRUE",
							DB_USER, DB_PASS);
					udb.load();
					gdb.load();
					mdb.load();
					udb.link(gdb, mdb);
					gdb.link(udb, mdb);
					mdb.link(udb, gdb);
					gdb.updateCodes();
				}
				else {
					throw new DbNotReachable();
				}
			} else {
				throw new DbUninitialised();
			}
		} catch (Exception e) { //TODO: ex
			throw e;
		} finally {
			if(c != null) c.close();
			if(buf != null) buf.close();
		}
	}
}