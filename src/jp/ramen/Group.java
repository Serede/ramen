package jp.ramen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Group extends Entity {
	private String code, desc;
	private Group supergroup;
	private List<Group> subgroups;
	private User creator;
	protected List<User> members;
	private static final Group ADAM = null;
	
	public Group(String name, String desc, Group parent, User creator) {
		super(name);
		if(parent!=null)
			{
			if(parent.addSubgroup(this) == false); //TODO: Exception
			}
		this.desc = desc;
		this.supergroup = parent;
		this.subgroups = new ArrayList<Group>();
		this.members = new ArrayList<User>();
		members.add(creator);
		this.creator = creator;
		code = generateCode(name, parent);
	}
	
	private String generateCode(String name, Group parent) {
		return (parent==ADAM? "":parent.code + ".") + name.toLowerCase().replace(" ", "_");
	}
	
	private boolean addSubgroup(Group g) { 
		if(subgroups.contains(g) == true) return false;
		return subgroups.add(g);
	}
	
	//public abstract boolean addMember(User u);
	//TODO: Weirdness
	public boolean addMember(User u) {
		if(members.contains(u) == true) return false;
		return members.add(u);
	}
	
	
	//It should public or private
	@Override
	public boolean addToInbox(Message m) {
		boolean res = true;
		for (User u : members) {
			res = u.addToInbox(m) & res;
		}
		return res;
	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public Group getSupergroup() {
		return supergroup;
	}
	
	public User getOwner() {
		return creator;
	}
	//TODO: Review carefully. Reviewed with unmodifiable
	public List<Group> getSubgroups() {
		return Collections.unmodifiableList(subgroups);
	}
	
	//TODO: Review carefully. Reviewed with unmodifiable
	public List<User> getMembers() {
		return Collections.unmodifiableList(members);
	}
	
	public boolean isPrivate() {
		return false;
	}
	
}
