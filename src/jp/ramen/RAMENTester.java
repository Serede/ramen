package jp.ramen;

public class RAMENTester {

	public static void main(String[] args) throws Exception {
		RAMEN app = RAMEN.getInstance();
		
		System.out.println("Installing RAMEN...");
		app.install("./ramen/db", "students.txt", "professors.txt");
		System.out.println("Initialising...");
		app.init();
		
		User u1 = app.getDAO().getUdb().getUser("leonardo.martin@mail.gob");
				
		System.out.println("Logging-in as María Martín...");
		app.login("maria.martin@ddm.es", "mamnds455");
		System.out.println("Sending messages to a sensei...");
		app.sendMessage(u1, "SOPER", "SOPER is too damn hard", false);
		app.sendMessage(u1, "CIREL", "CIREL was too damn hard", false);
		System.out.println("Creating anime group...");
		app.createGroup("Anime", "To be a supermoe and generic loli", null, true, true, true);
		Group anime = app.getDAO().getGdb().getGroup("anime");
		System.out.println("Creating anime.shakugan_no_shana subgroup...");
		app.createGroup("Shakugan no Shana", "Noizi Ito rulz", anime, true, true, true);
		
		System.out.println();
		
		System.out.println("Logging-in as Leo Domínguez...");
		app.login("leo.dominguez@alla.net", "lodzat287");
		System.out.println("Sending join request to anime group...");
		app.joinGroup(anime);
		System.out.println("Accepting join request from María's account...");
		app.login("maria.martin@ddm.es", "mamnds455");
		System.out.println(app.getCurrentUser().getInbox());
		app.handleRequest((Request) app.getCurrentUser().getInbox().get(0).getReference(), true);
		System.out.println(app.getCurrentUser().getInbox());
		System.out.println("Posting to anime back as Leo...");
		app.login("leo.dominguez@alla.net", "lodzat287");
		app.sendMessage(anime, "SOPERU", "SOPERU HAADO DESU YO", false);
		System.out.println("Rejecting post request from María's account...");
		app.login("maria.martin@ddm.es", "mamnds455");
		System.out.println(app.getCurrentUser().getInbox());
		app.handleRequest((Request) app.getCurrentUser().getInbox().get(1).getReference(), true);
		System.out.println(app.getCurrentUser().getInbox());
		
		System.out.println();
		
		System.out.println("Logging-in as a sensei...");
		app.login("leonardo.martin@mail.gob", "lomnmb757");
		System.out.println("Reading first message...");
		app.readMessage(app.getCurrentUser().getInbox().get(0));
		System.out.println("Printing inbox...");
		System.out.println(app.getCurrentUser().getInbox());
		System.out.println("Creating study group...");
		app.createGroup("PPROG", "Santinis Winery", null, false, false, false);
		System.out.println("Adding María to pprog group...");
		app.login("maria.martin@ddm.es", "mamnds455");
		app.joinGroup(app.getDAO().getGdb().getGroup("pprog"));
		System.out.println("Posting message and question back from sensei...");
		app.login("leonardo.martin@mail.gob", "lomnmb757");
		app.sendMessage(app.getDAO().getGdb().getGroup("pprog"), "Gonna post a question", "Be ready", false);
		app.sendMessage(app.getDAO().getGdb().getGroup("pprog"), "Tha question", "Did you receive tha question?", true);
		System.out.println(((Question) app.getDAO().getMdb().getMessage(7L)).howManyAnswered() + "/" + ((Question) app.getDAO().getMdb().getMessage(7L)).howManyDidntAnswer());
		System.out.println(((Question) app.getDAO().getMdb().getMessage(7L)).whoAnswered() + "/" + ((Question) app.getDAO().getMdb().getMessage(7L)).whoDidntAnswer());
		System.out.println(((Question) app.getDAO().getMdb().getMessage(7L)).reviewAnswers());
		System.out.println("Answering tha question from María...");
		app.login("maria.martin@ddm.es", "mamnds455");
		app.sendAnswer(app.getDAO().getGdb().getGroup("pprog"), "Yep", "I received tha question, leonardo-sensei.", (Question) app.getDAO().getMdb().getMessage(7L));
		System.out.println(((Question) app.getDAO().getMdb().getMessage(7L)).howManyAnswered() + "/" + ((Question) app.getDAO().getMdb().getMessage(7L)).howManyDidntAnswer());
		System.out.println(((Question) app.getDAO().getMdb().getMessage(7L)).whoAnswered() + "/" + ((Question) app.getDAO().getMdb().getMessage(7L)).whoDidntAnswer());
		System.out.println(((Question) app.getDAO().getMdb().getMessage(7L)).reviewAnswers());
	}

}
