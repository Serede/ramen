package jp.ramen;

public class JoinRequest extends Request {
	private User u;
	private Group g;
	public JoinRequest(String subject, String text, User author, Entity to, User u, Group g) {
		super(subject, text, author, to);
		this.u = u;
		this.g = g;
	}

	@Override
	public void setRequest(boolean accepted) {
		if(this.isAccepted() == true) return;
		this.setAccepted(true);
		if(accepted) {
			g.addMember(u);
			u.subscribe(g);
		}
	}
}
