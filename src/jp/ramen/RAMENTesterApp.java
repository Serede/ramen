package jp.ramen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collection;

import jp.ramen.exceptions.ForbiddenAction;
import jp.ramen.exceptions.GroupAlreadyExists;
import jp.ramen.exceptions.InvalidMessage;

public class RAMENTesterApp {
	private static RAMEN app;
	private static boolean running=true;
	
	public static void main(String[] args) throws Exception{
		try {
			app = RAMEN.getInstance();
			
			if(args.length==4 && args[0].equalsIgnoreCase("install")) {
				System.out.println("Installing RAMEN...");
				app.install(args[1], args[2], args[3]);
				System.out.println("RAMEN successfully installed to " + args[1] + "!");
				return;
			}
			
			System.out.println("Welcome to RAMEN");
			System.out.println("Initialising...");
			app.init();
			String user;
			String passwd;
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			boolean correctPass = true;
			do {
				if(!correctPass)
					System.out.println("Wrong password! Try again.");
				System.out.println("Enter your user:");
				user = console.readLine();
				System.out.println("Enter your password:");
				passwd = console.readLine();
			} while ((correctPass = app.login(user, passwd)) == false);
			
			String action;
			while(running) {
				System.out.println("Enter an instruction (help for list of commands)");
				System.out.print("> "); action = console.readLine();
				try {
					System.out.println("Result of the action: " + handleCommand(action));
				} catch (ForbiddenAction | InvalidMessage | GroupAlreadyExists | SQLException e) {
					System.out.println(e);
				}
			}
			System.out.println("Closing the application");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static boolean handleCommand(String action) throws ForbiddenAction, SQLException, InvalidMessage, GroupAlreadyExists {
		String[] words = action.split(" ");
		String[] msg = action.split("\"");
		
		Entity to;
		switch(words[0]) {
		case "s": //send a message
		case "send":
			if(words.length<3) return false;
			if(msg.length<4) return false;
			to = getTo(words[2]);
			if(to==null) return false;
			boolean question = true;
			switch(words[1]) {
			case "m":
			case "message":
				question = false;
			case "q":
			case "question":
				return app.sendMessage(to, msg[1], msg[3], question);
			case "a":
			case "answer":
				if(words.length<5) return false;
				int aindex = Integer.parseInt(words[3]);
				if(app.getCurrentUser().getInbox().size() < aindex || aindex < 0) return false; 
				return app.sendAnswer(to, msg[1], msg[3], (Question)app.getCurrentUser().getInbox().get(aindex).getReference());
			default:
				displayHelp();
				return false;
			} //end of send a message
			
		case "c":
		case "create": //Create group
			if(words.length<2) return false;
			switch(words[1]) {
			case "g":
			case "group":
				boolean social=true, priv=false, mod=false;
				if(words.length>=4) {
					int i=4;
					while(words.length>i) {
						switch(words[i++]) {
						case "social":
							social = true;
							break;
						case "study" :
							social = false;
							break;
						case "priv" :
							priv = true;
							break;
						case "mod" :
							mod=true;
							break;
						}
					}
					return app.createGroup(msg[1], msg[3], app.getDAO().getGdb().getGroup(words[4]), social, priv, mod);
				}
				else {
					displayHelp();
					return false;
				}
				default:
					displayHelp();
					return false;
				
			} //end of group creation
			
		case "j":  
		case "join":  //join a group
			if(words.length<2) return false;
			switch(words[1]) {
			case "g":
			case "group":
				if(words.length==3)
					return app.joinGroup(app.getDAO().getGdb().getGroup(words[2]));
				else {
					displayHelp();
					return false;
				}
			default:
				displayHelp();
				return false;
			}
			//end of join a group

		case "lv":
		case "leave": //leave a group
			if(words.length<2) return false;
			switch(words[1]) {
			case "g":
			case "group":
				if(words.length==3)
					return app.leaveGroup(app.getDAO().getGdb().getGroup(words[2]));
				else {
					displayHelp();
					return false;
				}
			}
			
		case "l": //list
		case "list":
			if(words.length<2) return false;
			switch(words[1]) {
			case "i":
			case "inbox":
				System.out.println("Inbox:");
				printCollection(app.listInbox());
				return true;
			case "g":
			case "groups":
				System.out.println("Groups:");
				printCollection(app.listGroups());
				return true;
			case "u":
			case "users":
				System.out.println("Users:");
				printCollection(app.listUsers());
				return true;
			default:
				displayHelp();
				return false;
			} //end of list
			
		case "r":
		case "read":
			if(words.length<2) return false;
			int rindex = Integer.parseInt(words[1]);
			if(rindex>=app.listInbox().size()) return false;
			return app.readMessage(app.listInbox().get(rindex));
			
		case "d":
		case "delete":
			if(words.length<2) return false;
			int dindex = Integer.parseInt(words[1]);
			if(dindex>=app.listInbox().size()) return false;
			return app.delMessage(app.listInbox().get(dindex));
			
		case "hr":
		case "handle": //handle a request
			if(words.length<3) return false;
			int hindex = Integer.parseInt(words[1]);
			boolean accepted=Boolean.parseBoolean(words[2]);
			return app.handleRequest((Request) app.getCurrentUser().getInbox().get(hindex).getReference(), accepted);
			
		case "rv":
		case "review":
			if(words.length<2) return false;
			int rvindex = Integer.parseInt(words[1]);
			Question q = (Question) app.listInbox().get(rvindex).getReference();
			if(rvindex>=app.listInbox().size()) return false;;
			System.out.println(q.howManyAnswered() + " students answered:");
			printCollection(q.whoAnswered());
			System.out.println(q.howManyDidntAnswer() + " students didn't answer yet:");
			printCollection(q.whoDidntAnswer());
			System.out.println("Answers:");
			printCollection(app.reviewQuestion((Question) app.listInbox().get(rvindex).getReference()));
			return true;
			
		case "b":
		case "block":
			if(words.length<2) return false;
			to = getTo(words[1]);
			if(to==null) return false;
			return app.block(to);
			
		case "u":
		case "unblock":
			if(words.length<2) return false;
			to = getTo(words[1]);
			if(to==null) return false;
			return app.unblock(to);
			
		case "q":
		case "Q":
		case "quit":
		case "e":
		case "E":
		case "exit":
			System.out.println("Bye :)");
			running = false;
			return true;
			
		default:
		case "h":
		case "help":
			displayHelp();
			return false;
		}
	}

	private static Entity getTo(String string) {
		Entity u = app.getDAO().getUdb().getUser(string);
		if(u==null)
			u = app.getDAO().getGdb().getGroup(string);
		return u;
			
	}

	private static void printCollection(Collection<? extends Object> c) {
		int i = 0;
		for(Object t: c) {
			System.out.println("<"+(i++)+"> "+t);
			System.out.println();
		}
	}
	private static void displayHelp() {
		System.out.println("You can type the following commands:");
		System.out.println("\t > send message <to> <\"subj\"> <\"text\"> ");
		System.out.println("\t > send question <to> \"<subj>\" \"<question>\"");
		System.out.println("\t > send answer <to> <question> \"<subj>\" <\"answer\">");
		System.out.println("\t > review <question>");
		System.out.println("\t > join group <code>");
		System.out.println("\t > leave group <code>");
		System.out.println("\t > create group \"<name>\" \"<desc>\" supg (study) (priv) (mod)>");
		System.out.println("\t > read <msg>");
		System.out.println("\t > delete <msg>");
		System.out.println("\t > handle <req> <accept? true/false>");
		System.out.println("\t > block <user/group>"); 
		System.out.println("\t > unblock <user/group>"); 
		System.out.println("\t > list inbox");
		System.out.println("\t > list groups");
		System.out.println("\t > list users");
		System.out.println("\t > exit or Q");
		System.out.println("To refer to users, use their name");
		System.out.println("To refer to groups, use their code, or NONE");
		System.out.println("To refer to messages, use the index of the inbox");
	}
}
