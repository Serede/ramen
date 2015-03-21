package jp.ramen;

import jp.ramen.exceptions.ForbiddenAction;

public class StudyGroup extends Group {
	public StudyGroup(String name, String desc) {
		super(name,desc);
	}
	
	public StudyGroup(String name, String desc, Group parent, User creator) throws ForbiddenAction {
		super(name, desc, parent, creator);
		if(creator instanceof Student) throw new ForbiddenAction();
	}
	
	@Override
	public String toString() {
		return "StudyGroup "+super.toString();
	}
}
