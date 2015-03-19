package jp.ramen;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

import jp.ramen.exceptions.NotEnoughPermissions;

public class GroupDAO extends DAO {
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
			if (g.getCode() == code)
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

	public void load() throws SQLException {
		Connection db = null;
		Statement stmt = null;
		
		try {
			db = DAO.connect();
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
			
		} catch (SQLException | NotEnoughPermissions e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			stmt.close();
			db.close();
		}
	}
	
	public void link(UserDAO udb, MessageDAO mdb) {
		Connection db = null;
		Statement stmt = null;
		
		try {
			db = DAO.connect();
			stmt = db.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT UID,SUPG,SUBG,OWNER,MEMB FROM GROUPS");
			while(rs.next()) {
				Group g = groups.get(rs.getLong(1));
				/* SUPG */
				g.setSupergroup(groups.get(rs.getLong(2)));
				/* SUBG */
				for (Long id : (Long[])rs.getArray(3).getArray()) {
					g.addSubgroup(groups.get(id));
				}
				/* OWNER */
				g.setOwner(udb.getUser(rs.getLong(4)));
				/* MEMB */
				for (Long id : (Long[])rs.getArray(5).getArray()) {
					g.addMember(udb.getUser(id));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateCodes() {
		for (Group g : groups.values()) {
			g.updateCode();
		}
	}
	
}
