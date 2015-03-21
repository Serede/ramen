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
	
	public SocialGroup(String name, String desc, boolean priv, boolean mod) throws ForbiddenAction{
		this(name,desc,null,null,priv,mod);
	}

	public SocialGroup(String name, String desc, Group supg, User owner, boolean priv, boolean mod) throws ForbiddenAction {
		super(name, desc, supg, owner);
		
		if(owner instanceof Sensei) throw new ForbiddenAction();
		this.priv = supg==null? priv:supg.isPrivate(); //TODO: inheritance
		this.mod = supg==null? mod:supg.isModerated();
	}
		
	@Override
	public boolean isPrivate() {
		return priv;
	}
	
	@Override
	public boolean isModerated() {
		return mod;
	}

	@Override
	public String toString() {
		return "SocialGroup "+super.toString();
	}
}
