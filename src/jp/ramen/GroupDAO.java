package jp.ramen;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class GroupDAO { //TODO does not extend
	private static GroupDAO gdb = null;
	private Map<Long, Group> groups;
	
	private GroupDAO() {
		this.groups = new TreeMap<>();
	}

	public static GroupDAO getInstance() {
		if (gdb == null)
			gdb = new GroupDAO();
		return gdb;
	}
	
	public Group getGroup(Long gid) {
		return groups.get(gid);
	}

	public Group getGroup(String code) {
		for (Group g : groups.values()) {
			if (code.equals(g.getCode()))
				return g;
		}
		return null; // TODO: Exception
	}
	
	public Long getID(Group g) {
		for (Map.Entry<Long,Group> e : groups.entrySet()) {
			if (g.equals(e.getValue()))
				return e.getKey();
		}
		return 0L; // TODO: Exception
	}

	public void load() throws Exception {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT GID,NAME,DESC,STUDY,PRIV,MOD FROM GROUPS");
			while(rs.next()) {
				Group g = null;
				if(rs.getBoolean(4))
					g = new StudyGroup(rs.getString(2),rs.getString(3));
				else
					g = new SocialGroup(rs.getString(2),rs.getString(3),rs.getBoolean(5),rs.getBoolean(6));
				groups.put(rs.getLong(1), g);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	public void link(UserDAO udb, MessageDAO mdb) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			ResultSet rs = null;
			
			/* OWNER */
			rs = stmt.executeQuery("SELECT GID,OWNER FROM GROUPS");
			while(rs.next()) {
				Group g = groups.get(rs.getLong(1));
				g.setOwner(udb.getUser(rs.getLong(2)));
			} 
			
			/* GROUP TREE */
			rs = stmt.executeQuery("SELECT * FROM G_TREE");
			while(rs.next()) {
				Group supg = groups.get(rs.getLong(1));
				Group subg = groups.get(rs.getLong(2));
				supg.addSubgroup(subg);
				subg.setSupergroup(supg);
			}
			
			/* MEMBERSHIP */
			rs = stmt.executeQuery("SELECT * FROM G_MEMBERS");
			while(rs.next()) {
				Group g = groups.get(rs.getLong(1));
				User u = udb.getUser(rs.getLong(2));
				g.addMember(u);
				u.subscribe(g);
			}	
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	public void updateCodes() {
		for (Group g : groups.values()) {
			g.updateCode();
		}
	}
	
	public boolean addGroup(Group g) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
					
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			UserDAO udb = UserDAO.getInstance();
			long gid;

			stmt = db.createStatement();
			stmt.executeUpdate("INSERT INTO ENTITIES VALUES()");
			ResultSet rs = stmt.executeQuery("SELECT MAX(EID) FROM ENTITIES");
			
			if(rs.next()) {
				gid = rs.getLong(1);
				groups.put(gid, g);
			}
			else
				return false;
			
			boolean study = (g instanceof StudyGroup);

			String insert_group = "INSERT INTO GROUPS VALUES("
					+ gid
					+ ","
					+ "'" + g.getName() + "'"
					+ ","
					+ "'" + g.getDesc() + "'"
					+ ","
					+ (study?"true":"false") 
					+ ","
					+ (study?false:((SocialGroup)g).isPrivate())
					+ ","
					+ (study?false:((SocialGroup)g).isModerated())
					+ ","
					+ udb.getID(g.getOwner())
					+ ")";
			String add_member = "INSERT INTO G_MEMBERS VALUES("
					+ gid
					+ ","
					+ udb.getID(g.getOwner())
					+ ")";
			
			stmt.executeUpdate(insert_group);
			stmt.executeUpdate(add_member);
			
			if(g.getSupergroup() != null) {
				String update_tree = "INSERT INTO G_TREE VALUES("
					+ gdb.getID(g.getSupergroup())
					+ ","
					+ gid
					+ ")";
				stmt.executeUpdate(update_tree);
			}

			return true;
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}

	public boolean addMember(Group g, User u) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		UserDAO udb = ddb.getUdb();
					
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			GroupDAO gdb = GroupDAO.getInstance();

			stmt = db.createStatement();
			String insert_member = "INSERT INTO G_MEMBERS VALUES("
					+ gdb.getID(g)
					+ ","
					+ udb.getID(u)
					+ ")";
			stmt.executeUpdate(insert_member);
			
			if(g.addMember(u)==false) return false;
			if(u.subscribe(g)==false) return false;
			return true;
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	public boolean delMember(Group g, User u) throws SQLException {
		Connection db = null;
		Statement stmt = null;
		DAO ddb = DAO.getInstance();
		UserDAO udb = ddb.getUdb();
					
		try {
			db = ddb.connect();
			stmt = db.createStatement();
			GroupDAO gdb = GroupDAO.getInstance();

			stmt = db.createStatement();
			String delete_member = "DELETE FROM G_MEMBERS WHERE "
					+ "GID=" + gdb.getID(g)
					+ "AND"
					+ "MEMB=" + udb.getID(u)
					+ ")";
			stmt.executeUpdate(delete_member);
			
			if(g.delMember(u)==false) return false;
			if(u.unsubscribe(g)==false) return false;
			return true;
		} catch (SQLException e) {
			throw e;
		} finally {
			if(stmt != null) stmt.close();
			if(db != null) db.close();
		}
	}
	
	public Collection<Group> listGroups() {
		return Collections.unmodifiableCollection(groups.values());
	}
}
