package jp.ramen;

import java.io.*;
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
		GroupDAO gdb;
		MessageDAO mdb;
		Connection c = null;
		try {
			c = DriverManager.getConnection("jdbc:h2:" + path + "/" + DB_NAME
					+ ";IFEXISTS=TRUE", DB_USER, DB_PASS);
			udb = UserDAO.getInstance();
			gdb = GroupDAO.getInstance();
			mdb = MessageDAO.getInstance();
			udb.load();
			gdb.load();
			mdb.load();
			udb.link(gdb, mdb);
			gdb.link(udb, mdb);
			mdb.link(udb, gdb);
			gdb.updateCodes();
		} catch (SQLException e) { //TODO: ex
			throw e;
		} finally {
			c.close();
		}
	}
	
	public static void main(String[] args) {
		String file = "fs";
		String path = "~/ramen";
		File firstStart;
		BufferedReader buf = null;
		PrintWriter writer = null;
		try {
			firstStart = new File(file);
			if(firstStart.exists() && !firstStart.isDirectory()) {
				buf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				if((path = buf.readLine()) != null)
					init(path);
				return;
			} else {
				create(path,"students.txt","professors.txt");
				writer = new PrintWriter(file,"UTF-8");
				writer.println(path);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(buf!=null) buf.close();
				if(writer!=null) writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}