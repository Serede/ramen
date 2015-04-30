package jp.ramen.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

import jp.ramen.DAO;
import jp.ramen.RAMEN;

import com.alee.extended.filechooser.WebFileChooserField;
import com.alee.extended.filechooser.WebPathField;
import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.CollapsiblePaneListener;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.extended.progress.WebStepProgress;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.progressbar.WebProgressBar;

public class Installer extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final int HEIGHT = 640;
	private static final int WIDTH = 640;
	private static final String DFLT_PATH = System.getProperty("user.home") + "/ramen";
	private static final String[] DFLT_FILES = {"students.txt", "professors.txt"};

	private static Installer frame = null;
	
	private String path = DFLT_PATH;
	private String[] files = {null, null};
	private boolean init = true;

	private WebStepProgress progress = new WebStepProgress(
			"Okaeri nasai!",
			"Choose your RAMEN",
			"Water is now boling",
			"Itadakimasu!"
			);
	private JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	private JButton next = new JButton("Next");
	private JButton cancel = new JButton("Cancel");
	JPanel cards = new JPanel(new CardLayout());
	InstallCard1 card1 = new InstallCard1();
	InstallCard2 card2 = new InstallCard2();
	InstallCard3 card3 = new InstallCard3();
	InstallCard4 card4 = new InstallCard4();

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

		progress.setSelectionEnabled(false);

		buttons.add(cancel);
		buttons.add(next);

		CardLayout clayout = (CardLayout) cards.getLayout();

		cards.add(card1, "Okaeri nasai!");
		cards.add(card2, "Choose your RAMEN");
		cards.add(card3, "Water is now boiling");
		cards.add(card4, "Itadakimasu!");

		Container pane = this.getContentPane();
		pane.add(progress, BorderLayout.NORTH);
		pane.add(cards, BorderLayout.CENTER);
		pane.add(buttons, BorderLayout.SOUTH);
		
		next.addActionListener(e -> {
			switch (progress.getSelectedStep()) {
			case 0:
				/* Settings for the card 1 */
				clayout.next(cards);
				progress.setSelectedStep(progress.getSelectedStep() + 1);
				next.setEnabled(false);
				next.setText("Install");
				updateNext();
				break;
			case 1:
				/* Settings for the card 2 */
				clayout.next(cards);
				progress.setSelectedStep(progress.getSelectedStep() + 1);
				cancel.setEnabled(false);
				next.setEnabled(false);
				next.setText("Continue");
				break;
			case 2:
				/* Setting for the card 3 */
				clayout.next(cards);
				progress.setSelectedStep(progress.getSelectedStep() + 1);
				next.setText("Finish");
				break;
			case 3:
				/* Setting for the card 3 */
				if (card4.runNow.isSelected()) {
					SwingUtilities.invokeLater(Main.run);
					frame.dispose();
				} else
					System.exit(0);
			}
		});

		cancel.addActionListener(e -> System.exit(0));
	}

	private final class InstallCard1 extends JPanel {
		
		private static final long serialVersionUID = 1L;

		private static final String IMG = "img/ebi_ramen_by_johnsu-d6sc3de.png";
		private static final int IMG_WIDTH = 480;
		private static final int IMG_HEIGHT = 480;

		private WebDecoratedImage image;
		private JLabel text = new JLabel("Welcome to the RAMEN installation wizard!");

		public InstallCard1() {
			super(new SpringLayout());
			SpringLayout layout = (SpringLayout) this.getLayout();

			ImageIcon img;
			try {
				img = new ImageIcon(ImageIO.read(new File(IMG)));
				img.setImage(img.getImage().getScaledInstance(IMG_WIDTH, IMG_HEIGHT, Image.SCALE_SMOOTH));
				image = new WebDecoratedImage(img);
				image.setRound(32, true);
			} catch (IOException e) {
				image = new WebDecoratedImage();
			}

			this.add(image);
			this.add(text);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, -12, SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, text, 12, SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text, 0, SpringLayout.HORIZONTAL_CENTER, image);
		}
	}

	private final class InstallCard2 extends JPanel {
		
		private static final long serialVersionUID = 1L;

		private static final int FIELD_WIDTH = 520;

		private JLabel pathText = new JLabel("Choose a location for RAMEN files:");
		private WebPathField inPath = new WebPathField(new File(path));
		private WebCollapsiblePane dbInit = new WebCollapsiblePane("Initialise RAMEN with the data below");
		private JLabel stText = new JLabel("Choose or drop students text file to load:");
		private WebFileChooserField stFile = new WebFileChooserField();
		private JLabel prText = new JLabel("Choose or drop professors text file to load:");
		private WebFileChooserField prFile = new WebFileChooserField();

		public InstallCard2() {
			super(new SpringLayout());
			SpringLayout layout = (SpringLayout) this.getLayout();
			JPanel exp = new JPanel();
			exp.setLayout(new BoxLayout(exp,BoxLayout.Y_AXIS));
			JCheckBox checked = new JCheckBox("",true);
			JCheckBox unchecked = new JCheckBox("",false);

			inPath.setPreferredWidth(FIELD_WIDTH);
			dbInit.setPreferredWidth(FIELD_WIDTH);
			dbInit.setExpanded(init,false);
			dbInit.setIcon((init? checked:unchecked).getIcon());
			dbInit.setContentMargin(12, 12, 12, 12);
			stFile.setMultiSelectionEnabled(false);
			stFile.setFilesDropEnabled(true);
			stFile.setShowFileExtensions(true);
			prFile.setMultiSelectionEnabled(false);
			prFile.setFilesDropEnabled(true);
			prFile.setShowFileExtensions(true);
			
			File st = new File(DFLT_FILES[0]);
			if (st.exists() && !st.isDirectory()) {
				stFile.setSelectedFile(st);
				files[0] = DFLT_FILES[0];
			}
			File pr = new File(DFLT_FILES[1]);
			if (pr.exists() && !pr.isDirectory()) {
				prFile.setSelectedFile(pr);
				files[1] = DFLT_FILES[1];
			}

			this.add(pathText);
			this.add(inPath);
			exp.add(stText);
			exp.add(stFile);
			exp.add(new JLabel("\n"));
			exp.add(prText);
			exp.add(prFile);
			stText.setAlignmentX(Component.LEFT_ALIGNMENT);
			stFile.setAlignmentX(Component.LEFT_ALIGNMENT);
			prText.setAlignmentX(Component.LEFT_ALIGNMENT);
			prFile.setAlignmentX(Component.LEFT_ALIGNMENT);
			dbInit.setContent(exp);
			this.add(dbInit);

			layout.putConstraint(SpringLayout.VERTICAL_CENTER, dbInit, 48, SpringLayout.VERTICAL_CENTER, this);

			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, inPath, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.SOUTH, inPath, -48, SpringLayout.NORTH, dbInit);
			layout.putConstraint(SpringLayout.WEST, pathText, 0, SpringLayout.WEST, inPath);
			layout.putConstraint(SpringLayout.SOUTH, pathText, -12, SpringLayout.NORTH, inPath);

			layout.putConstraint(SpringLayout.WEST, dbInit, 0, SpringLayout.WEST, inPath);
			
			dbInit.addCollapsiblePaneListener(new CollapsiblePaneListener() {
				@Override
				public void collapsed(WebCollapsiblePane arg0) {}

				@Override
				public void collapsing(WebCollapsiblePane arg0) {
					init = false;
					dbInit.setIcon(unchecked.getIcon());
				}

				@Override
				public void expanded(WebCollapsiblePane arg0) {}

				@Override
				public void expanding(WebCollapsiblePane arg0) {
					init = true;
					dbInit.setIcon(checked.getIcon());
				}
			});

			inPath.addPathFieldListener(e -> {
				if (inPath.getSelectedPath() != null)
					path = inPath.getSelectedPath().getAbsolutePath();
				else
					path = null;
				updateNext();
			});

			stFile.addSelectedFilesListener(e -> {
				List<File> file = stFile.getSelectedFiles();
				if (file.size() == 1 && file.get(0) != null)
					files[0] = file.get(0).getAbsolutePath();
				else
					files[0] = null;
				updateNext();
			});
			
			prFile.addSelectedFilesListener(e -> {
				List<File> file = prFile.getSelectedFiles();
				if (file.size() == 1 && file.get(0) != null)
					files[1] = file.get(0).getAbsolutePath();
				else
					files[1] = null;
				updateNext();
			});
		}
	}

	private final class InstallCard3 extends JPanel {
		
		private static final long serialVersionUID = 1L;
	
		private static final String IMG = "img/ahri_and_ramen_by_tonnelee-d5si885.jpg";
		private static final int IMG_WIDTH = 320;
		private static final int IMG_HEIGHT = 320;
		private static final int PROGRESS_WIDTH = 520;
	
		private JLabel text1 = new JLabel("The application is now being installed using the provided settings.");
		private JLabel text2 = new JLabel("Please be patient, your RAMEN will be ready soon.");
		private WebDecoratedImage image;
		private WebProgressBar progress = new WebProgressBar(0,100);
	
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
				image = new WebDecoratedImage();
			}
	
			progress.setPreferredWidth(PROGRESS_WIDTH);
			//progress.setIndeterminate(true);
			progress.setStringPainted(true);
			//progress.setString("Installing RAMEN...");
	
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
			
			this.addAncestorListener(new AncestorListener() {
				@Override
				public void ancestorAdded(AncestorEvent event) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					SwingWorker<Void, Void> task = new SwingWorker<Void,Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							PrintWriter pw = null;
							try {
								int percent = 0;
								setProgress(0);
								if (init)
									RAMEN.getInstance().install(path, files[0], files[1]);
								else {
									pw = new PrintWriter(DAO.DB_WARD, "UTF-8");
									pw.println(path);
								}
								while (percent < 100) {
									Thread.sleep(10);
									setProgress(++percent);
								}
							} catch (Exception e) {
								throw e;
							} finally {
								if (pw != null) pw.close();
							}
							return null;
						}
						
						@Override
						protected void done() {
							next.setEnabled(true);
				            setCursor(null);
						}
					};
					task.addPropertyChangeListener((evt) -> {
				        progress.setValue(task.getProgress());
					});
					task.execute();
				}
				@Override
				public void ancestorRemoved(AncestorEvent event) {}
				@Override
				public void ancestorMoved(AncestorEvent event) {}
			});
		}
	}

	private final class InstallCard4 extends JPanel {
		
		private static final long serialVersionUID = 1L;
	
		private JLabel text1 = new JLabel("RAMEN has been successfully installed.");
		private JLabel text2 = new JLabel("Thanks for using the application. Enjoy!");
		public JCheckBox runNow = new JCheckBox("Start RAMEN now", true);
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
				image = new JLabel();
			}
	
			this.add(image);
			this.add(text1);
			this.add(text2);
			this.add(runNow);
	
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, 0, SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.SOUTH, text1, -12, SpringLayout.NORTH, image);
			layout.putConstraint(SpringLayout.NORTH, text2, 12, SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.NORTH, runNow, 6, SpringLayout.SOUTH, text2);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text1, 0, SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, runNow, 0, SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, text2, 0, SpringLayout.HORIZONTAL_CENTER, image);
		}
	}
	
	private void updateNext() {
		boolean enableNext = true;
		enableNext &= (path != null);
		for (String s : files) {
			if (s != null) {
				File f = new File(s);
				enableNext &= f.exists();
			} else
				enableNext = false;
		}
		next.setEnabled(enableNext);
	}
}