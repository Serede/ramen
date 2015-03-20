package jp.ramen;

public class LocalMessage {
	private boolean read;
	private Message reference;
	
	
	public LocalMessage(Message m) {
		this.reference = m;
		this.read = false;
	}
	
	public Message getReference() {
		return this.reference;
	}
	
	public boolean read() {
		if(read==true) return false; /* Already read */
		read = true;
		return true;
	}
	
	public boolean isRead() {
		return read;
	}
	
	@Override 
	public String toString() {
		return "["+ read + "]" + reference;
	} 
	//TODO: Complete?
}
