package jp.ramen.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 720;
	private static final int WIDTH = 1280;
	private static final int MIN_WIDTH = 420;
	private static final int MIN_HEIGHT = 520;

	private static Main frame;
	
	public static Runnable run = () -> {
		WebLookAndFeel.install();
		if (frame == null)
			frame = new Main();
		frame.setSize(WIDTH, HEIGHT);
		frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		frame.setVisible(true);
	};
	
	private final class Login extends JPanel {

		private static final long serialVersionUID = 1L;
		private static final String IMG = "img/ramen_digital_ver__by_kokororhythm-d6radm5.png";
		private static final int IMG_WIDTH = 320;
		private static final int IMG_HEIGHT = 320;
		private static final int FIELD_WIDTH = 32;
		
		private WebDecoratedImage image;
		private WebTextField user = new WebTextField(FIELD_WIDTH);
		private WebPasswordField pass = new WebPasswordField(FIELD_WIDTH);
		private JButton login = new JButton("Log In");
		
		public Login(Container pane) {
			super(new SpringLayout());
			SpringLayout layout = (SpringLayout) this.getLayout();

			ImageIcon img;
			try {
				img = new ImageIcon(ImageIO.read(new File(IMG)));
				img.setImage(img.getImage().getScaledInstance(IMG_WIDTH,
						IMG_HEIGHT, Image.SCALE_SMOOTH));
				image = new WebDecoratedImage(img);
				image.setDrawGlassLayer(false, false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			user.setInputPrompt("User");
			user.setHideInputPromptOnFocus(false);
			pass.setInputPrompt("Password");
			pass.setHideInputPromptOnFocus(false);
			
			this.add(image);
			this.add(user);
			this.add(pass);
			this.add(login);
			
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, -72, SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, user, 24, SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.NORTH, pass, 12, SpringLayout.SOUTH, user);
			layout.putConstraint(SpringLayout.NORTH, login, 12, SpringLayout.SOUTH, pass);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, user, 0, SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pass, 0, SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.EAST, login, 0, SpringLayout.EAST, pass);
			
			ActionListener proceed = e -> {
				// TODO shitty implementation
				CardLayout clayout = (CardLayout) pane.getLayout();
				clayout.next(pane);
			};
			login.addActionListener(proceed);
		}
		
	}
	
	private final class App extends JPanel {

		private static final long serialVersionUID = 1L;

		private static final int DIVIDER_LOCATION = 240;
		
		private JMenu menu = new JMenu();
		private JTree tree = new JTree();
		private WebSplitPane splitPane;
		private JTable inbox = new JTable();
		private JPanel details = new JPanel();
		
		
		public App() {
			super(new BorderLayout());
			
			splitPane = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, inbox, details);
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(DIVIDER_LOCATION);
			splitPane.setContinuousLayout ( true );
			
			//TODO test
			menu.add(new JMenuItem("This"));
			menu.add(new JMenuItem("Is"));
			menu.add(new JMenuItem("A"));
			menu.add(new JMenuItem("Test"));
			
			this.add(menu, BorderLayout.NORTH);
			this.add(tree, BorderLayout.WEST);
			this.add(splitPane, BorderLayout.CENTER);
			
			
		}
		
	}
	
	private Main() {
		super("RAMEN");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new CardLayout());
		Container pane = this.getContentPane();
		
		pane.add(new Login(pane));
		pane.add(new App());
		
	}

}
