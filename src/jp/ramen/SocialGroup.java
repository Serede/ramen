/**
 * 
 */
package jp.ramen;

import jp.ramen.exceptions.*;

/**
 * @author e303132
 *
 */
public class SocialGroup extends Group {
	private boolean isPrivate=false;
	private boolean moderated=false;
	public SocialGroup(String name, String desc, Group parent, User creator) {
		super(name, desc, parent, creator);
		// TODO Auto-generated constructor stub
	}

	public SocialGroup(String name, String desc, Group parent, User creator, boolean isPrivate, boolean moderated) throws NotEnoughPermissions {
		super(name, desc, parent, creator);
		
		if(creator instanceof Sensei) throw new NotEnoughPermissions();
		this.isPrivate = parent==null? isPrivate:parent.isPrivate(); //TODO: inheritance
		this.moderated = parent==null? moderated:parent.isModerated();
	}
		
	@Override
	public boolean isPrivate() {
		return isPrivate;
	}
	
	
	@Override
	public boolean isModerated() {
		return moderated;
	}

}
