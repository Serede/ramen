package jp.ramen;

import jp.ramen.exceptions.NotEnoughPermissions;

public class StudyGroup extends Group {
	public StudyGroup(String name, String desc) {
		super(name,desc);
	}
	
	public StudyGroup(String name, String desc, Group parent, User creator) throws NotEnoughPermissions {
		super(name, desc, parent, creator);
		if(creator instanceof Student) throw new NotEnoughPermissions();
	}

}
