package jp.ramen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Group class for the application
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public abstract class Group extends Entity {
	private String code, desc;
	private Group supergroup;
	private List<Group> subgroups;
	private User owner;
	protected List<User> members;
	private static final Group ADAM = null;
	
	/**
	 * DB constructor
	 * @param name
	 * @param desc
	 */
	public Group(String name, String desc) {
		this(name,desc,null,null);
	}
	
	/**
	 * Constructor
	 * @param name
	 * @param desc
	 * @param supg
	 * @param owner
	 */
	public Group(String name, String desc, Group supg, User owner) {
		super(name);
		code = generateCode(name, supg);
		this.desc = desc;
		this.supergroup = supg;
		if(supg!=null)
			supg.addSubgroup(this); //TODO: Exception
		this.subgroups = new ArrayList<Group>();
		this.members = new ArrayList<User>();
		this.owner = owner;
		if(owner!=null)
			members.add(owner);
		code = generateCode(name, supg);
	}
	
	/**
	 * 
	 * @return the code of the group
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Re-generates the code when the supergroup changes
	 */
	public void updateCode() {
		code = generateCode(this.getName(),this.supergroup);
	}
	
	/**
	 * Generates a code with the name of the group and the supergroup 
	 * @param name
	 * @param supg
	 * @return
	 */
	private String generateCode(String name, Group supg) {
		return (supg==ADAM? "":generateCode(supg.getName(),supg.supergroup) + ".") + name.toLowerCase().replace(" ", "_");
	}
	
	/**
	 * 
	 * @return the description of the group
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * 
	 * @return the supergroup of the group
	 */
	public Group getSupergroup() {
		return supergroup;
	}

	/**
	 * Changes the supergroup of the group
	 * @param supergroup
	 */
	public void setSupergroup(Group supergroup) {
		this.supergroup = supergroup;
	}

	/**
	 * 
	 * @return the list of the subgroups
	 */
	public List<Group> getSubgroups() {
		return Collections.unmodifiableList(subgroups);
	}

	/**
	 * Adds a subgroup to the group
	 * @param g the group
	 * @return true if it was possible, false otherwise
	 */
	public boolean addSubgroup(Group g) { 
		if(subgroups.contains(g) == true) return false;
		return subgroups.add(g);
	}

	/**
	 * 
	 * @return the owner of the group
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Changes the owner of the group
	 * @param owner
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * 
	 * @return the list of the members
	 */
	public List<User> getMembers() {
		return Collections.unmodifiableList(members);
	}

	/**
	 * Adds a member to the member list
	 * @param u
	 * @return true if it was possible, false otherwise
	 */
	public boolean addMember(User u) {
		if(members.contains(u) == true) return false;
		return members.add(u);
	}
	
	/**
	 * Deletes the member from the member list 
	 * @param u
	 * @return true if it was possible, false otherwise
	 */
	public boolean delMember(User u) {
		if(members.contains(u) == false) return false;
		return members.remove(u);
	}
	
	
	@Override
	public boolean addToInbox(Message m){
		boolean res = true;
		for (User u : members) {
			res = u.addToInbox(m) & res;
		}
		return res;
	}

	/**
	 * 
	 * @return if it is private
	 */
	public boolean isPrivate() {
		return false;
	}
	
	/**
	 * 
	 * @return if it is moderated
	 */
	public boolean isModerated() {
		return false;
	}

	@Override
	public String toString() {
		return "code=" + code + ", desc=" + desc + ", supg=" + supergroup + ", owner=" + owner;
	}
	
	
	
}
