package jp.ramen.gui;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;

import jp.ramen.RAMEN;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.ToolbarStyle;
import com.alee.laf.toolbar.WebToolBar;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final int HEIGHT = 720;
	private static final int WIDTH = 1280;
	private static final int MIN_WIDTH = 420;
	private static final int MIN_HEIGHT = 520;

	private static Main frame = null;
	private static RAMEN app = RAMEN.getInstance();
	
	public static Runnable run = () -> {
		WebLookAndFeel.install();
		if (frame == null)
			frame = new Main();
		frame.setSize(WIDTH, HEIGHT);
		frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		frame.setVisible(true);
	};
	
	private Main() {
		super("RAMEN");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new CardLayout());
		Container pane = this.getContentPane();
		
		pane.add(new Login());
		pane.add(new App());
		
		try {
			app.init();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e);
		}
	}

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
		private JLabel badLogin = new JLabel("Invalid login! Check your data and try again.");
		
		public Login() {
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
				image = new WebDecoratedImage();
			}
			
			user.setInputPrompt("User");
			user.setHideInputPromptOnFocus(false);
			pass.setInputPrompt("Password");
			pass.setHideInputPromptOnFocus(false);
			badLogin.setForeground(Color.RED);
			badLogin.setVisible(false);
			
			this.add(image);
			this.add(user);
			this.add(pass);
			this.add(login);
			this.add(badLogin);
			
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, image, 0, SpringLayout.HORIZONTAL_CENTER, this);
			layout.putConstraint(SpringLayout.VERTICAL_CENTER, image, -72, SpringLayout.VERTICAL_CENTER, this);
			layout.putConstraint(SpringLayout.NORTH, user, 24, SpringLayout.SOUTH, image);
			layout.putConstraint(SpringLayout.NORTH, pass, 12, SpringLayout.SOUTH, user);
			layout.putConstraint(SpringLayout.NORTH, login, 12, SpringLayout.SOUTH, pass);
			layout.putConstraint(SpringLayout.NORTH, badLogin, 12, SpringLayout.SOUTH, pass);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, user, 0, SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, pass, 0, SpringLayout.HORIZONTAL_CENTER, image);
			layout.putConstraint(SpringLayout.EAST, login, 0, SpringLayout.EAST, pass);
			layout.putConstraint(SpringLayout.WEST, badLogin, 0, SpringLayout.WEST, pass);
			
			login.addActionListener(e -> {
				if(app.login(user.getText(), new String(pass.getPassword()))) {
					CardLayout clayout = (CardLayout) frame.getContentPane().getLayout();
					clayout.next(frame.getContentPane());
				} else {
					badLogin.setVisible(true);
				}
			});
		}
		
	}
	
	private final class App extends JPanel {

		private static final long serialVersionUID = 1L;

		private static final int DIVIDER_LOCATION = 240;
		
		private WebToolBar bar = new WebToolBar(WebToolBar.HORIZONTAL);
		private JTree tree = new JTree();
		private WebSplitPane splitPane;
		private WebTable inbox;
		private JPanel details = new JPanel();
		
		public App() {
			super(new BorderLayout());
			
			String[] headers = {
					"Read",
					"Date",
					"Group",
					"Author",
					"Subject"
			};
			DefaultTableModel model = new DefaultTableModel(headers,0);
			inbox = new WebTable(model);
			inbox.setEditable(false);
			
			splitPane = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, new JScrollPane(inbox), details);
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(DIVIDER_LOCATION);
			splitPane.setContinuousLayout(true);
			
			bar.setToolbarStyle(ToolbarStyle.attached);
			bar.setFloatable(false);
			bar.add(new JButton("This"));
			bar.add(new JButton("Is"));
			bar.add(new JButton("A"));
			bar.add(new JButton("Test"));
	
			this.add(bar, BorderLayout.NORTH);
			this.add(tree, BorderLayout.WEST);
			this.add(splitPane, BorderLayout.CENTER);
			
			this.addAncestorListener(new AncestorListener() {
				@Override
				public void ancestorAdded(AncestorEvent event) {
					java.util.List<Object[]> data;
					data = app.listInbox().stream().map((lm) -> {
						return new Object[] {
								lm.isRead(),
								lm.getReference().getTime().toString(),
								lm.getReference().getTo().getName(),
								lm.getReference().getAuthor().getName(),
								lm.getReference().getSubject()
						};
					}).collect(Collectors.toList());
					for (Object[] d :data)
						model.addRow(d);
				}
				@Override
				public void ancestorRemoved(AncestorEvent event) {}
				@Override
				public void ancestorMoved(AncestorEvent event) {}
			});
		}
		
	}

}
