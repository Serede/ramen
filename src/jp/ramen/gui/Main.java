package jp.ramen.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jp.ramen.*;

import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.panel.WebButtonGroup;
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
		
		private ToolBar bar = new ToolBar(JToolBar.HORIZONTAL);
		private JButton newMessage = new JButton("New message");
		private JButton createGroup = new JButton("Create group");
		private JButton joinGroup = new JButton("Join group");
		private JButton leaveGroup = new JButton("Leave group");
		private JButton block = new JButton("Block");
		private JButton unblock = new JButton("Unblock");
		private WebTextField search = new WebTextField(SEARCH_WIDTH);
		private JTree gTree;
		DefaultTreeModel tree;
		gTreeNode home = new gTreeNode("Home");
		gTreeNode pm = new gTreeNode("Mailbox");
		gTreeNode people = new gTreeNode("People");
		HashMap<Group, gTreeNode> gMap = new HashMap<>();
		private JPanel center = new JPanel(new CardLayout());
		boolean splitCard = true;
		private WebSplitPane splitPane;
		private JTable topPane;
		iModel iTable;
		HashMap<Integer, LocalMessage> iMap = new HashMap<>();
		Predicate<? super LocalMessage> iFilter = (lm) -> !lm.equals(null);
		private DetailsPanel botPane = new DetailsPanel();
		private WebTable fullPane;
		DefaultTableModel uTable;
		HashMap<Integer, User> uMap = new HashMap<>();
		Predicate<? super User> uFilter = (u) -> !u.equals(null);
		
		private Entity target = null;
		
		public App() {
			super(new BorderLayout());
			
			search.setInputPrompt("Search...");
			search.setHideInputPromptOnFocus(false);

			bar.setToolbarStyle(ToolbarStyle.attached);
			bar.setFloatable(false);
			WebButtonGroup groupButtons = new WebButtonGroup(true, createGroup, joinGroup, leaveGroup);
			groupButtons.setButtonsDrawFocus(false);
			WebButtonGroup blockButtons = new WebButtonGroup(true, block, unblock);
			blockButtons.setButtonsDrawFocus(false);
			bar.leftAdd(newMessage);
			bar.leftAdd(groupButtons);
			bar.leftAdd(blockButtons);
			bar.rightAdd(search);
			
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			tree = new DefaultTreeModel(root);
			tree.insertNodeInto(home, root, 0);
			tree.insertNodeInto(people, root, 1);
			tree.insertNodeInto(pm, people, 0);
			gTree = new JTree(tree);
			gTree.setRootVisible(false);
			gTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			gTree.setPreferredSize(new Dimension(APP_WIDTH/5,0));

			String[] iHeaders = {
					"Read",
					"Date",
					"To",
					"Author",
					"Subject"
			};
			iTable = new iModel(iHeaders,0);
			topPane = new WebTable(iTable);
			topPane.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			topPane.getColumnModel().getColumn(0).setMaxWidth(READ_WIDTH);
			
			splitPane = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, new JScrollPane(topPane), botPane);
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(APP_HEIGHT/3);
			splitPane.setContinuousLayout(true);
			
			String[] uHeaders = {"User"};
			uTable = new DefaultTableModel(uHeaders,0);
			fullPane = new WebTable(uTable);
			fullPane.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fullPane.setEditable(false);
			
			center.add(splitPane);
			center.add(new JScrollPane(fullPane));
	
			this.add(bar, BorderLayout.NORTH);
			this.add(new JScrollPane(gTree), BorderLayout.WEST);
			this.add(center, BorderLayout.CENTER);
			
			newMessage.addActionListener((e) -> {
				MessageWindow mw = new MessageWindow(frame, target);
				mw.setVisible(true);
				fetchInbox();
			});
			
			createGroup.addActionListener((e) -> {
				CreateGroupWindow cgw = new CreateGroupWindow(frame, (Group) target);
				cgw.setVisible(true);
				fetchGroups();
			});
			
			joinGroup.addActionListener((e) -> {
				Group g = (Group) target;
				if (JOptionPane.showConfirmDialog(frame, "Join this group?\n" + g.getName(), "Join group", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
					try {
						app.joinGroup(g);
						gTree.setSelectionPath(new TreePath(home.getPath()));
						gTree.setSelectionPath(new TreePath(gMap.get(g).getPath()));
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(frame, ex, "Join group error", JOptionPane.WARNING_MESSAGE);
					}
			});
			
			leaveGroup.addActionListener((e) -> {
				Group g = (Group) target;
				if (JOptionPane.showConfirmDialog(frame, "Leave this group?\n" + g.getName(), "Leave group", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
					try {
						app.leaveGroup(g);
						gTree.setSelectionPath(new TreePath(home.getPath()));
						gTree.setSelectionPath(new TreePath(gMap.get(g).getPath()));
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(frame, ex, "Leave group error", JOptionPane.WARNING_MESSAGE);
					}
			});
			
			block.addActionListener((e) -> {
				Entity en = target;
				if (JOptionPane.showConfirmDialog(frame, "Do you want to block " + en.getName() + "?", "Block", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
					try {
						app.block(en);
						if (en instanceof Group) {
							gTree.setSelectionPath(new TreePath(home.getPath()));
							gTree.setSelectionPath(new TreePath(gMap.get(en).getPath()));
						} else {
							//TODO users
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(frame, ex, "Block error", JOptionPane.WARNING_MESSAGE);
					}
			});
			
			unblock.addActionListener((e) -> {
				Entity en = target;
				if (JOptionPane.showConfirmDialog(frame, "Do you want to unblock " + en.getName() + "?", "Unblock", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
					try {
						app.unblock(en);
						if (en instanceof Group) {
							gTree.setSelectionPath(new TreePath(home.getPath()));
							gTree.setSelectionPath(new TreePath(gMap.get(en).getPath()));
						} else {
							//TODO users
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(frame, ex, "Unblock error", JOptionPane.WARNING_MESSAGE);
					}
			});
			
			gTree.addTreeSelectionListener((e) -> {
				CardLayout clayout = (CardLayout) center.getLayout();
				gTreeNode node = (gTreeNode) gTree.getLastSelectedPathComponent();
				if(node == null) return;
				
				if (node.equals(home)) {
					clayout.first(center);
					splitCard = true;
					createGroup.setEnabled(true);
					joinGroup.setEnabled(false);
					leaveGroup.setEnabled(false);
					block.setEnabled(false);
					unblock.setEnabled(false);
					fetchGroups();
					iFilter = (lm) -> !lm.equals(null);
					fetchInbox();
					target = null;
										
					return;
				} else if (node.equals(people)) {
					clayout.next(center);
					splitCard = false;
					createGroup.setEnabled(false);
					joinGroup.setEnabled(false);
					leaveGroup.setEnabled(false);
					block.setEnabled(false);
					unblock.setEnabled(false);
					iFilter = (lm) -> !lm.equals(null);
					fetchInbox();
					if (fullPane.getRowCount() != 0)
						fullPane.setSelectedRow(0);
					else
						target = null;

					return;
				} else if (node.equals(pm)) {
					clayout.first(center);
					splitCard = true;
					createGroup.setEnabled(false);
					joinGroup.setEnabled(false);
					leaveGroup.setEnabled(false);
					block.setEnabled(false);
					unblock.setEnabled(false);
					iFilter = (lm) -> lm.getReference().getTo().equals(app.getCurrentUser());
					fetchInbox();
					target = null;

					return;
				} else {
					Object info = node.getUserObject();
					if (info instanceof Group) {
						Group g = (Group) info;
						clayout.first(center);
						splitCard = true;
						createGroup.setEnabled(true);
						joinGroup.setEnabled(!g.getMembers().contains(app.getCurrentUser()));
						leaveGroup.setEnabled(!joinGroup.isEnabled());
						block.setEnabled(!app.getCurrentUser().getBlocked().contains(g));
						unblock.setEnabled(!block.isEnabled());
						iFilter = (lm) -> lm.getReference().getTo().equals(info);
						fetchInbox();
						target = (Group) info;

						return;
					}
				}
			});
			
			topPane.getSelectionModel().addListSelectionListener((e) -> {
				LocalMessage lm = iMap.get(topPane.getSelectedRow());
				if(lm != null)
					botPane.setText(lm.getReference().getText());
			});
			new TableCellListener(topPane, new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					TableCellListener tcl = (TableCellListener) e.getSource();
					int col = tcl.getColumn(), row = tcl.getRow();
					if(tcl.getNewValue().equals(true)) {
						try {
							app.readMessage(iMap.get(row));
						} catch (SQLException ex) {
							JOptionPane.showMessageDialog(frame, ex, "Message read error", JOptionPane.WARNING_MESSAGE);
						}
					} else
						topPane.setValueAt(true, row, col);	
				}
			});
			
			fullPane.getSelectionModel().addListSelectionListener((e) -> {
				User u = uMap.get(fullPane.getSelectedRow());
				target = u;
				block.setEnabled(!app.getCurrentUser().getBlocked().contains(u));
				unblock.setEnabled(!block.isEnabled());
			});
			
			this.addAncestorListener(new AncestorListener() {
				@Override
				public void ancestorAdded(AncestorEvent event) {
					fetchGroups();
					fetchUsers();
					gTree.setSelectionPath(new TreePath(home.getPath()));
				}
				@Override
				public void ancestorRemoved(AncestorEvent event) {}
				@Override
				public void ancestorMoved(AncestorEvent event) {}
			});
			
			search.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					String text = search.getText();
					if (splitCard) {
						if (text.isEmpty())
							fetchInbox();
						else
							fetchInbox((lm) -> {
								Message ref = lm.getReference();
								return ref.getAuthor().getName().contains(text)
										|| ref.getSubject().contains(text)
										|| ref.getText().contains(text);
							});
					} else {
						uFilter = text.isEmpty() ? (u) -> !u.equals(null)
								: (u) -> u.getName().contains(text);
						fetchUsers();
					}
					
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					changedUpdate(e);
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					changedUpdate(e);
				}
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
		
		private class ToolBar extends WebToolBar {
			private static final long serialVersionUID = 1L;
			private static final int BAR_HEIGHT = 32;
			private static final int HSPACE = 6;
			private SpringLayout layout = new SpringLayout();
			private Component lastLeft = null;
			private Component lastRight = null;
			
			public ToolBar(int orientation) {
				super(orientation);
				this.setLayout(layout);
				this.setPreferredHeight(BAR_HEIGHT);
			}
			
			public void leftAdd(Component comp) {
				super.add(comp);
				if (lastLeft == null)
					layout.putConstraint(SpringLayout.WEST, comp, 0, SpringLayout.WEST, this);
				else
					layout.putConstraint(SpringLayout.WEST, comp, HSPACE, SpringLayout.EAST, lastLeft);
				layout.putConstraint(SpringLayout.VERTICAL_CENTER, comp, 0, SpringLayout.VERTICAL_CENTER, this);
				lastLeft = comp;
			}
			
			public void rightAdd(Component comp) {
				super.add(comp);
				if (lastRight == null)
					layout.putConstraint(SpringLayout.EAST, comp, 0, SpringLayout.EAST, this);
				else
					layout.putConstraint(SpringLayout.EAST, comp, -HSPACE, SpringLayout.WEST, lastRight);
				layout.putConstraint(SpringLayout.VERTICAL_CENTER, comp, 0, SpringLayout.VERTICAL_CENTER, this);
				lastRight = comp;
			}
			
			@Override
			public void setLayout(LayoutManager mgr) {
				super.setLayout(layout);
			}
		}
		
		private class DetailsPanel extends JPanel {
			private static final long serialVersionUID = 1L;
			
			public JButton reply = new JButton("Reply");
			public JButton remove = new JButton("Remove");
			public JButton answer = new JButton("Answer");
			public JButton review = new JButton("Review");
			public JButton block = new JButton("Block user");
			private JTextArea text = new JTextArea();
			
			public DetailsPanel() {
				super(new BorderLayout());
				JScrollPane scroll = new JScrollPane(text);
				JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				WebButtonGroup msgButtons = new WebButtonGroup(true, reply, remove);
				WebButtonGroup qstButtons = new WebButtonGroup(true, answer, review);
				
				text.setEditable(false);
				
				bPanel.add(msgButtons);
				bPanel.add(qstButtons);
				bPanel.add(block);
				
				this.add(bPanel, BorderLayout.NORTH);
				this.add(scroll, BorderLayout.CENTER);
				
				reply.addActionListener((e) -> {
					MessageWindow mw;

					Entity to = iMap.get(topPane.getSelectedRow()).getReference().getTo();
					if (to instanceof User)
						to = iMap.get(topPane.getSelectedRow()).getReference().getAuthor();
					mw = new MessageWindow(frame, to);
					mw.setVisible(true);
					fetchInbox();
				});
				
				remove.addActionListener((e) -> {
					if (JOptionPane.showConfirmDialog(frame, "Delete this message?", "Message deletion", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
						try {
							app.delMessage(iMap.get(topPane.getSelectedRow()));
							fetchInbox();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, ex, "Message deletion error", JOptionPane.WARNING_MESSAGE);
						}
				});
			}
			
			public void setText(String text) {
				this.text.setText(text);
			}
			
		}
		
		private void fetchGroups() {
			gMap.clear();
			home.removeAllChildren();
			tree.reload(home);
			for (Group g : app.listGroups()) {
				gTreeNode supernode = g.getSupergroup() == null ? home
						: gMap.get(g.getSupergroup());
				gTreeNode node = new gTreeNode(g);
				tree.insertNodeInto(node, supernode, supernode.getChildCount());
				gMap.put(g, node);
			}
			for (int i = 0; i < gTree.getRowCount(); i++)
				gTree.expandRow(i);
		}
		
		private void fetchInbox() { fetchInbox((lm) -> true); }
		private void fetchInbox(Predicate<? super LocalMessage> filter) {
			int rows = iTable.getRowCount();
			iMap.clear();
			for (int i = rows-1; i>=0; i--)
				iTable.removeRow(i);
			app.listInbox()
					.stream()
					.filter(iFilter)
					.filter(filter)
					.sorted((a,b) -> {
						return b.getReference().getTime().compareTo(a.getReference().getTime());
					})
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
								iMap.put(iTable.getRowCount() - 1, (LocalMessage) entry[0]);
							});
		}
		
		private void fetchUsers() {
			int rows = uTable.getRowCount();
			uMap.clear();
			for (int i = rows-1; i>=0; i--)
				uTable.removeRow(i);
			app.listUsers().stream().filter(uFilter).map((u) -> {
				return new Object[] { u, new Object[] { u.getName() } };
			}).forEach((entry) -> {
				uTable.addRow((Object[]) entry[1]);
				uMap.put(uTable.getRowCount() - 1, (User) entry[0]);
			});
		}
	}

}
