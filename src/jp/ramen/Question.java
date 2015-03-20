package jp.ramen;

import java.util.*;

import jp.ramen.exceptions.InvalidMessage;

public class Question extends Message {
	private List<Answer> answers;
	
	/* DB */
	public Question(String subject, String text, Date time) throws InvalidMessage {
		super(subject,text,true,time);
		answers = new ArrayList<Answer>();
	}
	
	public Question(String subject, String text, User author, Entity to) throws InvalidMessage {
		super(subject, text, author, to);
		answers = new  ArrayList<Answer>();
	}

	public boolean addAnswer(Answer a) {
		if(answers.contains(a)==true) return false;
		return answers.add(a);
	}

	public final List<Answer> reviewAnswers() {
		return answers; //TODO: Who didn't answer
	}

	public int howManyAnswered() {
		return answers.size();
	}

	public int howManyDidntAnswer() {
		return  this.whoDidntAnswer().size();
	}

	public List<User> whoAnswered() {
		List<User> ret = new ArrayList<>();
		for(Answer a: answers) {
			ret.add(a.getAuthor());
		}
		return ret;
	}
	public List<User> whoDidntAnswer() {
		Group g = (Group) this.getTo(); //TODO: Review
		List<User> ret =  new ArrayList<>(g.getMembers());
		List<User> ans = this.whoAnswered();
		for(User u: g.getMembers()) {
			if(ans.contains(u) || u.getClass() == Sensei.class) {
				ret.remove(u);
			}
		}

		return ret;
	}
}
