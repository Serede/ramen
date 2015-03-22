/**
 * 
 */
package jp.ramen;

import jp.ramen.exceptions.*;

/**
 * Implementation of social groups
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class SocialGroup extends Group {
	private boolean priv=false;
	private boolean mod=false;
	
	/**
	 * DB constructor
	 * @param name
	 * @param desc
	 * @param priv
	 * @param mod
	 * @throws ForbiddenAction
	 */
	public SocialGroup(String name, String desc, boolean priv, boolean mod) throws ForbiddenAction{
		this(name,desc,null,null,priv,mod);
	}

	/**
	 * Constructor
	 * @param name
	 * @param desc
	 * @param supg
	 * @param owner
	 * @param priv
	 * @param mod
	 * @throws ForbiddenAction
	 */
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
