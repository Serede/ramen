package jp.ramen;

import jp.ramen.exceptions.ForbiddenAction;

/**
 * This module implements the study groups
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class StudyGroup extends Group {
	public StudyGroup(String name, String desc) {
		super(name,desc);
	}
	
	/**
	 * Constructor
	 * @param name
	 * @param desc
	 * @param parent
	 * @param creator
	 * @throws ForbiddenAction
	 */
	public StudyGroup(String name, String desc, Group parent, User creator) throws ForbiddenAction {
		super(name, desc, parent, creator);
		if(creator instanceof Student) throw new ForbiddenAction("Only a sensei can create a study group.");
	}
	
	@Override
	public String toString() {
		return "StudyGroup "+super.toString();
	}
}
