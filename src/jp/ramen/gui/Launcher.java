package jp.ramen.gui;

import java.io.File;

import javax.swing.*;

import jp.ramen.DAO;

public class Launcher {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		switch (args.length) {
		case 1:
			if (args[0].equalsIgnoreCase("install")) {
				SwingUtilities.invokeAndWait(Installer.run);
				break;
			}
		default:
			File tok = new File(DAO.DB_WARD);
			if (tok.exists() && !tok.isDirectory()) {
				SwingUtilities.invokeLater(Main.run);
				break;
			}
			else
				SwingUtilities.invokeAndWait(Installer.run);
		}
	}
}