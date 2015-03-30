package jp.ramen.gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.alee.extended.progress.WebStepProgress;
import com.alee.laf.WebLookAndFeel;

public class Launcher {
	private static final int INSTALL_WIDTH = 640;
	private static final int INSTALL_HEIGHT = 640;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		switch (args.length) {
		case 1:
			if (args[0].equalsIgnoreCase("install")) {
				SwingUtilities.invokeLater(() -> {
					WebLookAndFeel.install();
					try {
						install();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
		default:
			launch();
		}
	}

	private static void launch() {
		// TODO Auto-generated method stub
		
	}

	private static void install() throws IOException {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame("RAMEN Installation");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		WebStepProgress overallProgress = new WebStepProgress("Okaeri nasai!", "Choose your RAMEN", "Water is boling", "Done. Itadakimasu!");
		
		JPanel cards = new JPanel(new CardLayout());
		cards.add(new InstallCard1(), "Okaeri nasai!");
		
		JPanel buttons = new InstallButtons();
		
		Container pane = frame.getContentPane();
		pane.add(overallProgress, BorderLayout.NORTH);
		pane.add(cards, BorderLayout.CENTER);
		pane.add(buttons, BorderLayout.SOUTH);
		
		frame.setSize(INSTALL_WIDTH, INSTALL_HEIGHT);
		frame.setMinimumSize(new Dimension(INSTALL_WIDTH, INSTALL_HEIGHT));
		frame.setVisible(true);
	}
}

final class InstallCard1 extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String IMG = "img/ebi_ramen_by_johnsu-d6sc3de.png";
	private static final int IMG_WIDTH = 480;
	private static final int IMG_HEIGHT = 480;
	
	private JLabel image;
	private JLabel text;
	
	InstallCard1() throws IOException {
		super(new SpringLayout());
		SpringLayout layout = (SpringLayout) this.getLayout();
		
		ImageIcon img = new ImageIcon(ImageIO.read(new File(IMG)));
		img.setImage(img.getImage().getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH));
		
		image = new JLabel(img);
		text = new JLabel("Welcome to the RAMEN installation wizard!");
		this.add(image);
		this.add(text);
		layout.putConstraint(SpringLayout.NORTH, text, 6, SpringLayout.SOUTH, image);
		layout.putConstraint(SpringLayout.NORTH, image, 6, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.SOUTH, text, -6, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0, SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text, 0, SpringLayout.HORIZONTAL_CENTER, this);
	}
}

final class InstallCard2 extends JPanel {
	private static final long serialVersionUID = 1L;

	InstallCard2() {
		super(new SpringLayout());	
	}
}

final class InstallCard3 extends JPanel {
	private static final long serialVersionUID = 1L;

	InstallCard3() {
		super(new SpringLayout());	
	}
}

final class InstallCard4 extends JPanel {
	private static final long serialVersionUID = 1L;

	InstallCard4() {
		super(new SpringLayout());	
	}
}

final class InstallButtons extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JButton next;
	private JButton cancel;

	InstallButtons() {
		super(new FlowLayout(FlowLayout.RIGHT));	
		next = new JButton("Next");
		cancel = new JButton("Cancel");
		this.add(cancel);
		this.add(next);
	}
}