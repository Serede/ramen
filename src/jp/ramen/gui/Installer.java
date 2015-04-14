package jp.ramen.gui;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import java.util.List;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jp.ramen.RAMEN;

import com.alee.extended.filechooser.FilesSelectionListener;
import com.alee.extended.filechooser.PathFieldListener;
import com.alee.extended.filechooser.WebFileChooserField;
import com.alee.extended.filechooser.WebPathField;
import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.progress.WebStepProgress;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.progressbar.WebProgressBar;

public class Installer extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 640;
	private static final int WIDTH = 640;
	private static final String[] DFLT_PATHS = {
			System.getProperty("user.home") + "/ramen", "students.txt",
			"professors.txt" };

	private static Installer frame = null;

	private JButton next = new JButton("Next");
	private JButton cancel = new JButton("Cancel");
	private static String[] paths = { DFLT_PATHS[0], null, null };

	public static Runnable run = () -> {
		WebLookAndFeel.install();
		try {
			if (frame == null)
				frame = new Installer();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e);
		}
		frame.setSize(WIDTH, HEIGHT);
		frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		frame.setVisible(true);
	};

	private Installer() throws Exception {
		super("RAMEN Installation");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		WebStepProgress overall = new WebStepProgress("Okaeri nasai!",
				"Choose your RAMEN", "Water is now boling", "Itadakimasu!");
		overall.setSelectionEnabled(false);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		buttons.add(cancel);
		buttons.add(next);

		JPanel cards = new JPanel(new CardLayout());
		CardLayout clayout = (CardLayout) cards.getLayout();
		InstallCard1 card1 = new InstallCard1();
		InstallCard2 card2 = new InstallCard2();
		InstallCard3 card3 = new InstallCard3();
		InstallCard4 card4 = new InstallCard4();
		cards.add(card1, "Okaeri nasai!");
		cards.add(card2, "Choose your RAMEN");
		cards.add(card3, "Water is now boiling");
		cards.add(card4, "Itadakimasu!");

		Container pane = this.getContentPane();
		pane.add(overall, BorderLayout.NORTH);
		pane.add(cards, BorderLayout.CENTER);
		pane.add(buttons, BorderLayout.SOUTH);

		ActionListener nextButton = e -> {
			switch (overall.getSelectedStep()) {
			case 0:
				/* Settings for the card 1 */
				clayout.next(cards);
				overall.setSelectedStep(overall.getSelectedStep() + 1);
				next.setEnabled(false);
				next.setText("Install");
				updateNext();
				break;
			case 1:
				/* Settings for the card 2 */
				clayout.next(cards);
				overall.setSelectedStep(overall.getSelectedStep() + 1);
				cancel.setEnabled(false);
				next.setEnabled(false);
				next.setText("Continue");
				try {
					RAMEN.getInstance().install(paths[0], paths[1], paths[2]);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, e1);
				}
				next.setEnabled(true);
				break;
			case 2:
				/* Setting for the card 3 */
				clayout.next(cards);
				overall.setSelectedStep(overall.getSelectedStep() + 1);
				next.setText("Finish");
				break;
			case 3:
				/* Setting for the card 3 */
				System.exit(0);
			}
		};
		next.addActionListener(nextButton);

		ActionListener cancelButton = e -> {
			System.exit(0);
		};
		cancel.addActionListener(cancelButton);
	}

	private final class InstallCard1 extends JPanel {
		private static final long serialVersionUID = 1L;

		private static final String IMG = "img/ebi_ramen_by_johnsu-d6sc3de.png";
		private static final int IMG_WIDTH = 480;
		private static final int IMG_HEIGHT = 480;

		private WebDecoratedImage image;
		private JLabel text = new JLabel(
				"Welcome to the RAMEN installation wizard!");

		public InstallCard1() {
			super(new SpringLayout());
			SpringLayout layout = (SpringLayout) this.getLayout();

			ImageIcon img;
			try {
				img = new ImageIcon(ImageIO.read(new File(IMG)));
				img.setImage(img.getImage().getScaledInstance(IMG_WIDTH,
						IMG_HEIGHT, Image.SCALE_SMOOTH));
				image = new WebDecoratedImage(img);
				image.setRound(32, true);
			} catch (IOException e) {
				image = new WebDecoratedImage();
			}

			this.add(image);
			this.add(text);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0,
					SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, -12,
					SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, text, 12,
					SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text, 0,
					SpringLayout.HORIZONTAL_CENTER, image);
		}
	}

	private final class InstallCard2 extends JPanel {
		private static final long serialVersionUID = 1L;

		private static final int FIELD_WIDTH = 520;

		private JLabel pathText = new JLabel(
				"Choose a location for RAMEN files:");
		private WebPathField path = new WebPathField(new File(paths[0]));
		private JCheckBox init = new JCheckBox(
				"RAMEN has already been installed to given location");
		private JLabel stText = new JLabel(
				"Choose or drop students text file to load:");
		private WebFileChooserField stFile = new WebFileChooserField();
		private JLabel prText = new JLabel(
				"Choose or drop professors text file to load:");
		private WebFileChooserField prFile = new WebFileChooserField();

		public InstallCard2() {
			super(new SpringLayout());
			SpringLayout layout = (SpringLayout) this.getLayout();

			path.setPreferredWidth(FIELD_WIDTH);
			stFile.setPreferredWidth(FIELD_WIDTH);
			stFile.setMultiSelectionEnabled(false);
			stFile.setFilesDropEnabled(true);
			stFile.setShowFileExtensions(true);
			prFile.setPreferredWidth(FIELD_WIDTH);
			prFile.setMultiSelectionEnabled(false);
			prFile.setFilesDropEnabled(true);
			prFile.setShowFileExtensions(true);
			

			File st = new File(DFLT_PATHS[1]);
			if (st.exists() && !st.isDirectory()) {
				stFile.setSelectedFile(st);
				paths[1] = DFLT_PATHS[1];
			}
			File pr = new File(DFLT_PATHS[2]);
			if (pr.exists() && !pr.isDirectory()) {
				prFile.setSelectedFile(pr);
				paths[2] = DFLT_PATHS[2];
			}

			this.add(pathText);
			this.add(path);
			this.add(init);
			this.add(stText);
			this.add(stFile);
			this.add(prText);
			this.add(prFile);

			layout.putConstraint(SpringLayout.VERTICAL_CENTER, init, 0,
					SpringLayout.VERTICAL_CENTER, this);

			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, path, 0,
					SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.SOUTH, path, -96,
					SpringLayout.NORTH, init);
			layout.putConstraint(SpringLayout.WEST, pathText, 0,
					SpringLayout.WEST, path);
			layout.putConstraint(SpringLayout.SOUTH, pathText, -12,
					SpringLayout.NORTH, path);

			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, stFile, 0,
					SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, stText, 24,
					SpringLayout.SOUTH, init);
			layout.putConstraint(SpringLayout.WEST, stText, 0,
					SpringLayout.WEST, path);
			layout.putConstraint(SpringLayout.NORTH, stFile, 12,
					SpringLayout.SOUTH, stText);

			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, prFile, 0,
					SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, prText, 24,
					SpringLayout.SOUTH, stFile);
			layout.putConstraint(SpringLayout.WEST, prText, 0,
					SpringLayout.WEST, stFile);
			layout.putConstraint(SpringLayout.NORTH, prFile, 12,
					SpringLayout.SOUTH, prText);

			layout.putConstraint(SpringLayout.WEST, init, 0, SpringLayout.WEST,
					path);
			
			ChangeListener initListener = e -> {
				if (init.isSelected()) {
					stFile.setEnabled(false);
					prFile.setEnabled(false);
				} else {
					stFile.setEnabled(true);
					prFile.setEnabled(true);
				}
			};
			init.addChangeListener(initListener);

			PathFieldListener pathListener = e -> {
				if (path.getSelectedPath() != null)
					paths[0] = path.getSelectedPath().getAbsolutePath();
				else
					paths[0] = null;
				updateNext();
			};
			path.addPathFieldListener(pathListener);

			FilesSelectionListener stListener = e -> {
				List<File> file = stFile.getSelectedFiles();
				if (file.size() == 1 && file.get(0) != null)
					paths[1] = file.get(0).getAbsolutePath();
				else
					paths[1] = null;
				updateNext();
			};
			stFile.addSelectedFilesListener(stListener);
			
			FilesSelectionListener prListener = e -> {
				List<File> file = prFile.getSelectedFiles();
				if (file.size() == 1 && file.get(0) != null)
					paths[2] = file.get(0).getAbsolutePath();
				else
					paths[2] = null;
				updateNext();
			};
			prFile.addSelectedFilesListener(prListener);
		}
	}

	private final class InstallCard3 extends JPanel {
		private static final long serialVersionUID = 1L;

		private static final String IMG = "img/ahri_and_ramen_by_tonnelee-d5si885.jpg";
		private static final int IMG_WIDTH = 320;
		private static final int IMG_HEIGHT = 320;
		private static final int PROGRESS_WIDTH = 520;

		private JLabel text1 = new JLabel(
				"The application is now being installed using the provided settings.");
		private JLabel text2 = new JLabel(
				"Please be patient, your RAMEN will be ready soon.");
		private WebDecoratedImage image;
		private WebProgressBar progress = new WebProgressBar();

		public InstallCard3() {
			super(new SpringLayout());
			SpringLayout layout = (SpringLayout) this.getLayout();

			ImageIcon img;
			try {
				img = new ImageIcon(ImageIO.read(new File(IMG)));
				img.setImage(img.getImage().getScaledInstance(IMG_WIDTH,
						IMG_HEIGHT, Image.SCALE_SMOOTH));
				image = new WebDecoratedImage(img);
				image.setDrawGlassLayer(false, false);
				image.setRound(0, true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			progress.setPreferredWidth(PROGRESS_WIDTH);
			progress.setIndeterminate(true);
			progress.setStringPainted(true);
			progress.setString("Installing RAMEN...");

			this.add(image);
			this.add(text1);
			this.add(text2);
			this.add(progress);

			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, 0,
					SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0,
					SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.SOUTH, text2, -12,
					SpringLayout.NORTH, image);
			layout.putConstraint(SpringLayout.SOUTH, text1, -6,
					SpringLayout.NORTH, text2);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, progress, 0,
					SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.NORTH, progress, 12,
					SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.WEST, text1, 0,
					SpringLayout.WEST, progress);
			layout.putConstraint(SpringLayout.WEST, text2, 0,
					SpringLayout.WEST, progress);
		}
	}

	private final class InstallCard4 extends JPanel {
		private static final long serialVersionUID = 1L;

		private JLabel text1 = new JLabel(
				"RAMEN has been successfully installed.");
		private JLabel text2 = new JLabel(
				"Thanks for using the application. Enjoy!");
		private static final String IMG = "img/minato_kushina_ramen_by_daisyanimeluvr-d35zv41.png";
		private static final int IMG_WIDTH = 320;
		private static final int IMG_HEIGHT = 320;

		private JLabel image;

		public InstallCard4() {
			super(new SpringLayout());
			SpringLayout layout = (SpringLayout) this.getLayout();

			ImageIcon img;
			try {
				img = new ImageIcon(ImageIO.read(new File(IMG)));
				img.setImage(img.getImage().getScaledInstance(IMG_WIDTH,
						IMG_HEIGHT, Image.SCALE_SMOOTH));
				image = new JLabel(img);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.add(image);
			this.add(text1);
			this.add(text2);

			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, 0,
					SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0,
					SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.SOUTH, text1, -12,
					SpringLayout.NORTH, image);
			layout.putConstraint(SpringLayout.NORTH, text2, 12,
					SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text1, 0,
					SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text2, 0,
					SpringLayout.HORIZONTAL_CENTER, image);
		}
	}
	
	private void updateNext() {
		boolean enableNext = true;
		String[] lol = paths;
		for (String p : paths) {
			if (p != null) {
				File f = new File(p);
				enableNext &= f.exists();
			} else
				enableNext = false;
		}
		next.setEnabled(enableNext);
	}
}