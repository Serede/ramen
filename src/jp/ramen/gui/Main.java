package jp.ramen.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import jp.ramen.*;

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
	
	private static final int APP_HEIGHT = 720;
	private static final int APP_WIDTH = 1280;
	private static final int MIN_WIDTH = 420;
	private static final int MIN_HEIGHT = 520;
	private static final String ICON = "img/ramen_digital_ver__by_kokororhythm-d6radm5.png";
	private static final int ICON_HEIGHT = 512;
	private static final int ICON_WIDTH = 512;

	private static Main frame = null;
	private Image icon;
	private static RAMEN app = RAMEN.getInstance();
	
	public static Runnable run = () -> {
		WebLookAndFeel.install();
		if (frame == null)
			frame = new Main();
		frame.setSize(APP_WIDTH, APP_HEIGHT);
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
		
		ImageIcon img;
		try {
			img = new ImageIcon(ImageIO.read(new File(ICON)));
			img.setImage(img.getImage().getScaledInstance(ICON_WIDTH,
					ICON_HEIGHT, Image.SCALE_SMOOTH));
			icon = img.getImage();
			setIconImage(icon);
		} catch (IOException ignore) {}

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
			
			ActionListener al = (e) -> {
				if(app.login(user.getText(), new String(pass.getPassword()))) {
					CardLayout clayout = (CardLayout) frame.getContentPane().getLayout();
					clayout.next(frame.getContentPane());
				} else {
					badLogin.setVisible(true);
				}
			};
			user.addActionListener(al);
			pass.addActionListener(al);
			login.addActionListener(al);
		}
		
	}
	
	private final class App extends JPanel {

		private static final long serialVersionUID = 1L;
		private static final int READ_WIDTH = 64;
		private static final int SEARCH_WIDTH = 16;
		private static final int BAR_HEIGHT = 32;
		
		private WebToolBar bar = new WebToolBar(WebToolBar.HORIZONTAL);
		private JButton sendMessage = new JButton("Send message");
		private WebTextField search = new WebTextField(SEARCH_WIDTH);
		private JTree gTree;
		DefaultTreeModel tree;
		gTreeNode lobby = new gTreeNode("Main Lobby");
		gTreeNode people = new gTreeNode("People");
		HashMap<Group, gTreeNode> groups = new HashMap<>();
		private JPanel center = new JPanel(new CardLayout());
		private WebSplitPane splitPane;
		private JTable topPane;
		iModel iTable;
		HashMap<Integer, LocalMessage> inbox = new HashMap<>();
		private DetailsPanel botPane = new DetailsPanel();
		private WebTable fullPane;
		DefaultTableModel uTable;
		
		public App() {
			super(new BorderLayout());
			
			search.setInputPrompt("Search...");
			search.setHideInputPromptOnFocus(false);

			bar.setToolbarStyle(ToolbarStyle.attached);
			bar.setPreferredHeight(BAR_HEIGHT);
			bar.setFloatable(false);
			SpringLayout barLayout = new SpringLayout();
			bar.setLayout(barLayout);
			bar.add(sendMessage);
			bar.add(search);
			barLayout.putConstraint(SpringLayout.WEST, sendMessage, 0, SpringLayout.WEST, bar);
			barLayout.putConstraint(SpringLayout.VERTICAL_CENTER, sendMessage, 0, SpringLayout.VERTICAL_CENTER, bar);
			barLayout.putConstraint(SpringLayout.EAST, search, 0, SpringLayout.EAST, bar);
			barLayout.putConstraint(SpringLayout.VERTICAL_CENTER, search, 0, SpringLayout.VERTICAL_CENTER, bar);
			
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			tree = new DefaultTreeModel(root);
			tree.insertNodeInto(lobby, root, 0);
			tree.insertNodeInto(people, root, 1);
			gTree = new JTree(tree);
			gTree.setRootVisible(false);
			gTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			gTree.setPreferredSize(new Dimension(APP_WIDTH/5,APP_HEIGHT));

			String[] iHeaders = {
					"Read",
					"Date",
					"To",
					"Author",
					"Subject"
			};
			iTable = new iModel(iHeaders,0);
			topPane = new WebTable(iTable);
			topPane.getColumnModel().getColumn(0).setMaxWidth(READ_WIDTH);
			
			splitPane = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, new JScrollPane(topPane), botPane);
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(APP_HEIGHT/3);
			splitPane.setContinuousLayout(true);
			
			String[] uHeaders = {
					"User"
			};
			uTable = new DefaultTableModel(uHeaders,0);
			fullPane = new WebTable(uTable);
			fullPane.setEditable(false);
			
			center.add(splitPane);
			center.add(new JScrollPane(fullPane));
	
			this.add(bar, BorderLayout.NORTH);
			this.add(new JScrollPane(gTree), BorderLayout.WEST);
			this.add(center, BorderLayout.CENTER);
			
			gTree.addTreeSelectionListener((e) -> {
				CardLayout clayout = (CardLayout) center.getLayout();
				gTreeNode node = (gTreeNode) gTree.getLastSelectedPathComponent();
				if(node == null) return;
				
				if (node.equals(lobby)) {
					clayout.first(center);
					fetchInbox((lm) -> !lm.equals(null));
					return;
				}
				if (node.equals(people)) {
					clayout.next(center);
					fetchInbox((lm) -> !lm.equals(null));
					return;
				}
				
				Object info = node.getUserObject();
				if (info instanceof Group) {
					clayout.first(center);
					fetchInbox((lm) -> lm.getReference().getTo().equals(info));
					return;
				}
				
			});
			
			topPane.getSelectionModel().addListSelectionListener((e) -> {
				botPane.setText(inbox.get(topPane.getSelectedRow()).getReference().getText());
			});
			
			this.addAncestorListener(new AncestorListener() {
				@Override
				public void ancestorAdded(AncestorEvent event) {
					fetchGroups();
					fetchInbox((lm) -> !lm.equals(null));
					fetchUsers();
				}
				@Override
				public void ancestorRemoved(AncestorEvent event) {}
				@Override
				public void ancestorMoved(AncestorEvent event) {}
			});
		}
		
		private class gTreeNode extends DefaultMutableTreeNode {
			private static final long serialVersionUID = 1L;

			public gTreeNode(Object info) {
				super(info);
			}

			@Override
			public String toString() {
				Object info = super.getUserObject();
				if (info instanceof Group)
					return ((Group) info).getName();
				return super.toString();
			}
		}	
		
		private class iModel extends DefaultTableModel {
			private static final long serialVersionUID = 1L;

			public iModel(String[] columnNames, int rowCount) {
				super(columnNames, rowCount);
			}

			@Override
			public boolean isCellEditable (int row, int col) {
				return col==0;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return getValueAt(0, columnIndex).getClass();
			}
		}
		
		private class DetailsPanel extends JPanel {
			private static final long serialVersionUID = 1L;
			
			public JButton reply = new JButton("Reply");
			public JButton remove = new JButton("Remove");
			public JButton block = new JButton("Block user");
			private JTextArea text = new JTextArea();
			
			public DetailsPanel() {
				super(new BorderLayout());
				JScrollPane scroll = new JScrollPane(text);
				JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				
				text.setEditable(false);
				
				buttons.add(reply);
				buttons.add(remove);
				buttons.add(block);
				
				this.add(buttons, BorderLayout.NORTH);
				this.add(scroll, BorderLayout.CENTER);
			}
			
			public void setText(String text) {
				this.text.setText(text);
			}
			
		}
		
		private void fetchGroups() {
			lobby.removeAllChildren();
			for (Group g : app.listGroups()) {
				gTreeNode supernode = g.getSupergroup() == null ? lobby
						: groups.get(g.getSupergroup());
				gTreeNode node = new gTreeNode(g);
				tree.insertNodeInto(node, supernode, supernode.getChildCount());
				groups.put(g, node);
			}
			for (int i = 0; i < gTree.getRowCount(); i++)
				gTree.expandRow(i);
		}

		private void fetchInbox(Predicate<LocalMessage> constraint) {
			int rows = iTable.getRowCount();
			for (int i = rows-1; i>=0; i--)
				iTable.removeRow(i);
			app.listInbox()
					.stream()
					.filter(constraint)
					.map((lm) -> {
						return new Object[] {
								lm,
								new Object[] {
										lm.isRead(),
										lm.getReference().getTime().getTime().toString(),
										lm.getReference().getTo().getName(),
										lm.getReference().getAuthor().getName(),
										lm.getReference().getSubject()
										}
								};
					})
					.forEach((entry) -> {
								iTable.addRow((Object[]) entry[1]);
								inbox.put(iTable.getRowCount() - 1, (LocalMessage) entry[0]);
							});
		}
		
		private void fetchUsers() {
			int rows = uTable.getRowCount();
			for (int i = rows-1; i>=0; i--)
				uTable.removeRow(i);
			app.listUsers()
					.stream()
					.map((u) -> {
						return new Object[] { u.getName() };
					}).forEach((entry) -> {
						uTable.addRow(entry);
					});
		}
	}

}
