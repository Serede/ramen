package jp.ramen;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of the DB functions related to groups
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class GroupDAO { //TODO does not extend
	private static GroupDAO gdb = null;
	private Map<Long, Group> groups;
	
	/**
	 * Constructor(singleton)
	 */
	private GroupDAO() {
		this.groups = new TreeMap<>();
	}

	/**
	 * 
	 * @return the instance of the GroupDAO
	 */
	public static GroupDAO getInstance() {
		if (gdb == null)
			gdb = new GroupDAO();
		return gdb;
	}
	
	/**
	 * Searches for a group with his ID in the db
	 * @param gid
	 * @return the group if exists, null otherwise
	 */
	public Group getGroup(Long gid) {
		return groups.get(gid);
	}

	/**
	 * Searches for a group with his code in the db
	 * @param code
	 * @return the group if exists, null otherwise
	 */
	public Group getGroup(String code) {
		for (Group g : groups.values()) {
			if (code.equals(g.getCode()))
				return g;
		}
		return null; // TODO: Exception
	}
	
	/**
	 * Gets the ID of a group
	 * @param g
	 * @return the ID if exists, 0 otherwise
	 */
	public Long getID(Group g) {
		for (Map.Entry<Long,Group> e : groups.entrySet()) {
			if (g.equals(e.getValue()))
				return e.getKey();
		}
		return 0L; // TODO: Exception
	}

	/**
	 * Loads the data into memory
	 * @throws Exception
	 */
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
	
	/**
	 * Sets the rest of attributes of every group
	 * @param udb user db
	 * @param mdb message db
	 * @throws SQLException
	 */
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
	
	/**
	 * Updates the codes of all groups
	 */
	public void updateCodes() {
		for (Group g : groups.values()) {
			g.updateCode();
		}
	}
	
	/**
	 * Clears memory map
	 */
	public void clear() {
		groups = new TreeMap<>();
	}
	
	/**
	 * Add a group to the db
	 * @param g
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
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

	/**
	 * Adds a member to a group
	 * @param g
	 * @param u
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
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
	
	/**
	 * Deletes a member from a group
	 * @param g
	 * @param u
	 * @return true if it was possible, false otherwise
	 * @throws SQLException
	 */
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
					+ " AND "
					+ "MEMB=" + udb.getID(u);
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
	
	/**
	 * Lists groups
	 * @return a collection of all groups in the system
	 */
	public Collection<Group> listGroups() {
		return Collections.unmodifiableCollection(groups.values());
	}
}
