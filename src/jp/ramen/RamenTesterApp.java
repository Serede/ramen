package jp.ramen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Collection;

import jp.ramen.exceptions.ForbiddenAction;
import jp.ramen.exceptions.GroupAlreadyExists;
import jp.ramen.exceptions.InvalidMessage;

public class RamenTesterApp {
	private static RAMEN app;
	private static boolean running=true;
	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Welcome to RAMEN");
			app = RAMEN.getInstance();
			System.out.println("Installing RAMEN...");
			//app.install("./ramen/db", "students.txt", "professors.txt");
			System.out.println("Initialising...");
			app.init();
			String user;
			String passwd;
			BufferedReader console = new BufferedReader(
					new InputStreamReader(System.in));
			do {
				System.out.println("Enter your user");
				user = console.readLine();
				System.out.println("Enter your password");
				passwd = console.readLine();
			} while (app.login(user, passwd) == false);
			
			String action;
			while(running) {
				System.out.println("Enter an instruction (help for list of commands)");
				System.out.print("> "); action = console.readLine();
				System.out.println("Result of the action: " + handleCommand(action));
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
		case "c":
		case "create": //Group creation
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
					return app.createGroup(words[2], words[3], app.getDAO().getGdb().getGroup(words[4]), social, priv, mod);
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
				if(words.length==3) {
					return app.joinGroup(app.getDAO().getGdb().getGroup(words[2]));
				}
				else {
					displayHelp();
					return false;
				}
			default:
				displayHelp();
				return false;
			}
			//end of join a group

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
				int i = Integer.parseInt(words[5]);
				if(app.getCurrentUser().getInbox().size()> i || i < 0) return false; 
				return app.sendAnswer(to, msg[1], msg[3], (Question)app.getCurrentUser().getInbox().get(i).getReference()  );
			default:
				displayHelp();
				return false;
			} //end of send a message
			
		case "handle": //handle a request
			if(words.length<2) return false;
			switch(words[1]) {
			case "r":
			case "request":
				if(words.length<4) return false;
				int i = Integer.parseInt(words[2]);
				boolean accepted=Boolean.parseBoolean(words[3]);
				app.handleRequest((Request) app.getCurrentUser().getInbox().get(i).getReference(), accepted);
			default:
				displayHelp();
				return false;
			}//end of handle a request
			
		case "l": //list
		case "list":
			if(words.length<2) return false;
			switch(words[1]) {
			case "i":
			case "inbox":
				System.out.println("Inbox");
				printCollection(app.listInbox());
				return true;
			case "g":
			case "groups":
				System.out.println("Groups");
				printCollection(app.listGroups());
				return true;
			case "u":
			case "users":
				System.out.println("Users");
				printCollection(app.listUsers());
				return true;
			default:
				displayHelp();
				return false;
			}//end of list
			
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
		case "r":
		case "read":
			if(words.length<2) return false;
			int index = Integer.parseInt(words[1]);
			if(index>app.listInbox().size()) return false;
			return app.readMessage(app.listInbox().get(index));
		case "q":
		case "exit":
		case "Q":
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
		System.out.println("\t > exit or Q");
		System.out.println("\t > create group <name> <desc> 'supg' <opts>");
		System.out.println("\t > send message <to> <\"subj\"> <\"text\"> ");
		System.out.println("\t > send question <to> <subj> <question>");
		System.out.println("\t > send answer <to> <\"subj\"> <\"answer\"> <question>");
		System.out.println("\t > join group <code>");
		System.out.println("\t > handle request <req> <answer>");
		System.out.println("\t > block <e>"); 
		System.out.println("\t > unblock <e>"); 
		System.out.println("\t > read <msg>"); 
		System.out.println("\t > list inbox");
		System.out.println("\t > list groups");
		System.out.println("\t > list users");
		System.out.println("To refer to users, use their name");
		System.out.println("To refer to groups, use their code");
		System.out.println("To refer to messages, use the index of the inbox");
	}
}
