package jp.ramen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Group extends Entity {
	private String code, desc;
	private Group supergroup;
	private List<Group> subgroups;
	private User owner;
	protected List<User> members;
	private static final Group ADAM = null;
	
	public Group(String name, String desc) {
		this(name,desc,null,null);
	}
	
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
	
	public String getCode() {
		return code;
	}

	public void updateCode() {
		code = generateCode(this.getName(),this.supergroup);
	}
	
	private String generateCode(String name, Group supg) {
		return (supg==ADAM? "":generateCode(supg.getName(),supg.supergroup) + ".") + name.toLowerCase().replace(" ", "_");
	}
	
	public String getDesc() {
		return desc;
	}

	public Group getSupergroup() {
		return supergroup;
	}

	public void setSupergroup(Group supergroup) {
		this.supergroup = supergroup;
	}

	//TODO: Review carefully. Reviewed with unmodifiable
	public List<Group> getSubgroups() {
		return Collections.unmodifiableList(subgroups);
	}

	public boolean addSubgroup(Group g) { 
		if(subgroups.contains(g) == true) return false;
		return subgroups.add(g);
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	//TODO: Review carefully. Reviewed with unmodifiable
	public List<User> getMembers() {
		return Collections.unmodifiableList(members);
	}

	//public abstract boolean addMember(User u);
	//TODO: Weirdness
	public boolean addMember(User u) {
		if(members.contains(u) == true) return false;
		return members.add(u);
	}
	
	public boolean delMember(User u) {
		if(members.contains(u) == false) return false;
		return members.remove(u);
	}
	
	//It should public or private
	@Override
	public boolean addToInbox(Message m){
		boolean res = true;
		for (User u : members) {
			res = u.addToInbox(m) & res;
		}
		return res;
	}

	public boolean isPrivate() {
		return false;
	}
	
	public boolean isModerated() {
		return false;
	}
	
}
