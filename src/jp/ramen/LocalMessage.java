package jp.ramen;

/**
 * This module implements the LocalMessage as main structure of the inbox
 * @author Sergio Fuentes de Uña "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices Burrero "daniel.perdices@estudiante.uam.es"
 */
public class LocalMessage {
	private boolean read;
	private Message reference;
	
	/**
	 * Constructor
	 * @param m
	 */
	public LocalMessage(Message m) {
		this.reference = m;
		this.read = false;
	}
	
	/**
	 * 
	 * @return the message
	 */
	public Message getReference() {
		return this.reference;
	}
	
	/**
	 * Marks as read the message
	 * @return true if it was possible, false otherwise
	 */
	public boolean read() {
		if(read==true) return false; /* Already read */
		read = true;
		return true;
	}
	
	/**
	 * 
	 * @return whether it is read
	 */
	public boolean isRead() {
		return read;
	}
	
	@Override 
	public String toString() {
		char tick = (read)?'\u2611':'\u2610';
		return tick +" " + reference.toString();
	} 
}
