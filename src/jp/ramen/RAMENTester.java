package jp.ramen;

public class RAMENTester {

	public static void main(String[] args) {
		RAMEN app = RAMEN.getInstance();
		System.out.println("Logging as a student");
		app.login("maria.martin@ddm.es", "mamnds455");
		app.createGroup("Manga-Anime fans", "We love awesome mangas and animes", null, false, false, true);
		app.createGroup("Naruto haters", "Too many hours of endless Naruto", null/*1g*/, true, true, true);
	}

}
