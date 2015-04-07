package jp.ramen.gui;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

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
	private JButton next = new JButton("Next");
	private static boolean next_available[] = {false, false, false};
	private static String[] paths;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			this.add(image);
			this.add(text);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0,
					SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, -12,
					SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, text, 12, SpringLayout.SOUTH,
					image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text, 0,
					SpringLayout.HORIZONTAL_CENTER, image);
		}
	}

	private final class InstallCard2 extends JPanel {
		private static final long serialVersionUID = 1L;
	
		private static final int FIELD_WIDTH = 520;
	
		private JLabel pathText = new JLabel("Choose a location for RAMEN files:");
		private WebPathField path = new WebPathField();
		private JLabel stText = new JLabel("Choose students text file to load:");
		private WebFileChooserField stFile = new WebFileChooserField();
		private JLabel prText = new JLabel("Choose professors text file to load:");
		private WebFileChooserField prFile = new WebFileChooserField();
	
		public InstallCard2() {
			super(new SpringLayout());
			SpringLayout layout = (SpringLayout) this.getLayout();
			
			path.setPreferredWidth(FIELD_WIDTH);
			stFile.setPreferredWidth(FIELD_WIDTH);
			stFile.setMultiSelectionEnabled(false);
			prFile.setPreferredWidth(FIELD_WIDTH);
			prFile.setMultiSelectionEnabled(false);
	
			this.add(pathText);
			this.add(path);
			this.add(stText);
			this.add(stFile);
			this.add(prText);
			this.add(prFile);
			
			PathFieldListener pathListener = e -> {
				next_available[0] = true; 
				boolean res = true;
				for(Boolean b: next_available) {
					res &= b;
				}
				paths[0] = path.getPathField().toString();
				System.err.println(paths[0]);
				next.setEnabled(res);
				
			};
			path.addPathFieldListener(pathListener);
			
			FilesSelectionListener prListener = e -> {
				next_available[1] = true;
				boolean res = true;
				for(Boolean b: next_available) {
					res &= b;
				}
				java.util.List<File> file = prFile.getSelectedFiles();
				if(file.size() > 0) {
					paths[1] = file.get(0).getPath();
					next.setEnabled(res);
				}
			};
			prFile.addSelectedFilesListener(prListener);
			
			FilesSelectionListener stListener = e -> {
				next_available[2] = true;
				boolean res = true;
				for(Boolean b: next_available) {
					res &= b;
				}
				java.util.List<File> file = stFile.getSelectedFiles();
				if(file.size() > 0) {
					paths[2] = file.get(0).getPath();
					next.setEnabled(res);
				}
			};
			stFile.addSelectedFilesListener(stListener);
			
			
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, path, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, pathText, 96, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.WEST, pathText, 0, SpringLayout.WEST, path);
			layout.putConstraint(SpringLayout.NORTH, path, 12, SpringLayout.SOUTH, pathText);
	
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, stFile, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, stText, 48, SpringLayout.SOUTH, path);
			layout.putConstraint(SpringLayout.WEST, stText, 0, SpringLayout.WEST, path);
			layout.putConstraint(SpringLayout.NORTH, stFile, 12, SpringLayout.SOUTH, stText);
	
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, prFile, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, prText, 48, SpringLayout.SOUTH, stFile);
			layout.putConstraint(SpringLayout.WEST, prText, 0, SpringLayout.WEST, stFile);
			layout.putConstraint(SpringLayout.NORTH, prFile, 12, SpringLayout.SOUTH, prText);
	
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
	
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, 0, SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.SOUTH, text2, -12, SpringLayout.NORTH, image);
			layout.putConstraint(SpringLayout.SOUTH, text1, -6, SpringLayout.NORTH, text2);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, progress, 0, SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.NORTH, progress, 12, SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.WEST, text1, 0, SpringLayout.WEST, progress);
			layout.putConstraint(SpringLayout.WEST, text2, 0, SpringLayout.WEST, progress);
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
	
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, 0, SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.SOUTH, text1, -12, SpringLayout.NORTH, image);
			layout.putConstraint(SpringLayout.NORTH, text2, 12, SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text1, 0, SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text2, 0, SpringLayout.HORIZONTAL_CENTER, image);
		}
	}

	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 640;
	private static final int WIDTH = 640;

	private static Installer frame = null;
	
	public static Runnable run = ()  -> {
		WebLookAndFeel.install();
		try {
		if (frame == null)
			frame = new Installer();
		}catch(Exception e) {System.out.println(e);} //TODO: POPUP
		frame.setSize(WIDTH, HEIGHT);
		frame.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		frame.setVisible(true);
	};

	private Installer() throws Exception { //TODO: Exception popup
		super("RAMEN Installation");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		WebStepProgress overall = new WebStepProgress("Okaeri nasai!",
				"Choose your RAMEN", "Water is now boling", "Itadakimasu!");
		overall.setSelectionEnabled(false);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancel = new JButton("Cancel");
		
		buttons.add(cancel);
		buttons.add(next);

		JPanel cards = new JPanel(new CardLayout());
		CardLayout clayout = (CardLayout) cards.getLayout();
		cards.add(new InstallCard1(), "Okaeri nasai!");
		cards.add(new InstallCard2(), "Choose your RAMEN");
		cards.add(new InstallCard3(), "Water is now boiling");
		cards.add(new InstallCard4(), "Itadakimasu!");

		Container pane = this.getContentPane();
		pane.add(overall, BorderLayout.NORTH);
		pane.add(cards, BorderLayout.CENTER);
		pane.add(buttons, BorderLayout.SOUTH);
		

		ActionListener nextButton = e -> {
			// TODO improved next
			switch(overall.getSelectedStep()) {
			case 0: 
				clayout.next(cards);
				overall.setSelectedStep((overall.getSelectedStep() + 1)
								% overall.getStepsAmount());
				next.setEnabled(false);
				break;
			case 1:
				clayout.next(cards);
				overall.setSelectedStep((overall.getSelectedStep() + 1)
								% overall.getStepsAmount());
				next.setEnabled(false);
				break;
			case 2:
				try {
					RAMEN.getInstance().install(paths[0], paths[1], paths[2]);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			
			//clayout.next(cards);
			//overall.setSelectedStep((overall.getSelectedStep() + 1)
			//		% overall.getStepsAmount());
		};
		next.addActionListener(nextButton);

		

	}
}