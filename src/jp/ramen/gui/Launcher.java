package jp.ramen.gui;

import javax.swing.*;

public class Launcher {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		switch (args.length) {
		case 1:
			if (args[0].equalsIgnoreCase("install")) {
				SwingUtilities.invokeLater(Installer.run);
			}
		default:
			launch();
		}
	}

	private static void launch() {
		// TODO Auto-generated method stub
		
	}	
}