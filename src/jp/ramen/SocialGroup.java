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
	private boolean priv=false;
	private boolean mod=false;
	
	public SocialGroup(String name, String desc, boolean priv, boolean mod) throws NotEnoughPermissions{
		this(name,desc,null,null,priv,mod);
	}

	public SocialGroup(String name, String desc, Group parent, User owner, boolean priv, boolean mod) throws NotEnoughPermissions {
		super(name, desc, parent, owner);
		
		if(owner instanceof Sensei) throw new NotEnoughPermissions();
		this.priv = parent==null? priv:parent.isPrivate(); //TODO: inheritance
		this.mod = parent==null? mod:parent.isModerated();
	}
		
	@Override
	public boolean isPrivate() {
		return priv;
	}
	
	
	@Override
	public boolean isModerated() {
		return mod;
	}

}
