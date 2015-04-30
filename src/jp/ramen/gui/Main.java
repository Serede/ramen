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
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jp.ramen.*;

import com.alee.extended.button.WebSwitch;
import com.alee.extended.image.WebDecoratedImage;
import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.extended.panel.WebCollapsiblePane;
import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.table.WebTable;
import com.alee.laf.text.WebPasswordField;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import com.alee.laf.toolbar.ToolbarStyle;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.managers.popup.PopupWay;
import com.alee.managers.popup.WebButtonPopup;

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
					user.clear();
					pass.clear();
					badLogin.setVisible(false);
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
		private static final int TYPE_WIDTH = 20;
		private static final int READ_WIDTH = 56;
		private static final int SEARCH_WIDTH = 16;
		private static final String NEWMESSAGE_ICN = "img/icons/round_plus.png";
		private static final int ICN_WIDTH = 12;
		private static final int ICN_HEIGHT = 12;
		private static final String SEARCH_ICN = "img/icons/zoom.png";
		private static final String REQ_ICN = "img/icons/bell.png";
		private static final String QST_ICN = "img/icons/spechbubble.png";
		private static final String ANS_ICN = "img/icons/spechbubble_2.png";
		private static final String MSG_ICN = "img/icons/mail_2.png";
		private static final String STU_ICN = "img/icons/pencil.png";
		private static final String SEN_ICN = "img/icons/book.png";
		private static final String UBLK_ICN = "img/icons/cancel.png";
		private static final String USR_ICN = "img/icons/user.png";
		private static final String FCH_ICN = "img/icons/reload.png";
		
		private ToolBar bar = new ToolBar(JToolBar.HORIZONTAL);
		private JButton newMessage = new JButton("New message");
		private JButton createGroup = new JButton("Create group");
		private JButton joinGroup = new JButton("Join group");
		private JButton leaveGroup = new JButton("Leave group");
		private JButton block = new JButton("Block");
		private JButton unblock = new JButton("Unblock");
		private WebTextField search = new WebTextField(SEARCH_WIDTH);
		private JTree gTree;
		private WebCollapsiblePane gDetails;
		DefaultTreeModel tree;
		gTreeNode home = new gTreeNode("Home");
		gTreeNode pm = new gTreeNode("Mailbox");
		gTreeNode people = new gTreeNode("People");
		HashMap<Group, gTreeNode> gMap = new HashMap<>();
		private JPanel center = new JPanel(new CardLayout());
		private WebStatusBar status = new WebStatusBar();
		private WebButton userButton = new WebButton();
		private JButton logout = new JButton("Log Out");
		private JButton refetch = new JButton();
		private WebMemoryBar memBar = new WebMemoryBar();
		boolean splitCard = true;
		private WebSplitPane splitPane;
		private WebTable topPane;
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
			try {
				ImageIcon icon = new ImageIcon(ImageIO.read(new File(SEARCH_ICN)));
				icon.setImage(icon.getImage().getScaledInstance(ICN_WIDTH+2, ICN_HEIGHT, Image.SCALE_SMOOTH));
				search.setTrailingComponent(new JLabel(icon));
			} catch (IOException ignore) {}

			bar.setToolbarStyle(ToolbarStyle.attached);
			bar.setFloatable(false);
			WebButtonGroup groupButtons = new WebButtonGroup(true, createGroup, joinGroup, leaveGroup);
			groupButtons.setButtonsDrawFocus(false);
			WebButtonGroup blockButtons = new WebButtonGroup(true, block, unblock);
			blockButtons.setButtonsDrawFocus(false);
			bar.leftAdd(newMessage);
			try {
				ImageIcon icon = new ImageIcon(ImageIO.read(new File(NEWMESSAGE_ICN)));
				icon.setImage(icon.getImage().getScaledInstance(ICN_WIDTH, ICN_HEIGHT, Image.SCALE_SMOOTH));
				newMessage.setIcon(icon);
			} catch (IOException ignore) {}
			bar.leftAdd(groupButtons);
			bar.leftAdd(blockButtons);
			bar.rightAdd(search);
			
			gDetails = new WebCollapsiblePane("", new GroupDetails());
			gDetails.setTitlePanePostion(SwingConstants.BOTTOM);
			gDetails.setVisible(false);
			
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			tree = new DefaultTreeModel(root);
			tree.insertNodeInto(home, root, 0);
			tree.insertNodeInto(people, root, 1);
			tree.insertNodeInto(pm, people, 0);
			gTree = new JTree(tree);
			gTree.setRootVisible(false);
			gTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			gTree.setPreferredSize(new Dimension(APP_WIDTH/5,0));
			gTree.setCellRenderer(new DefaultTreeCellRenderer() {
				private static final long serialVersionUID = 1L;
				private static final String HOME_ICN = "img/icons/home.png";
				private static final String PEOPLE_ICN = "img/icons/users.png";
				private static final String PM_ICN = "img/icons/inbox.png";
				private static final String GROUP_ICN = "img/icons/spechbubble_sq.png";
				private static final String SUB_ICN = "img/icons/spechbubble_sq_line.png";
				private static final String BLK_ICN = "img/icons/spechbubble_sq_cross.png";
				private ImageIcon icon;

				@Override
				public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
					super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					try {
						Object info = ((gTreeNode) value).getUserObject();
						if (info.equals(home.getUserObject()))
							icon = new ImageIcon(ImageIO.read(new File(HOME_ICN)));
						else if (info.equals(people.getUserObject()))
							icon = new ImageIcon(ImageIO.read(new File(PEOPLE_ICN)));
						else if (info.equals(pm.getUserObject()))
							icon = new ImageIcon(ImageIO.read(new File(PM_ICN)));
						else if (info instanceof Group) {
							Group g = (Group) info;
							if(app.getCurrentUser().getBlocked().contains(g))
								icon = new ImageIcon(ImageIO.read(new File(BLK_ICN)));
							else if(g.getMembers().contains(app.getCurrentUser()))
								icon = new ImageIcon(ImageIO.read(new File(SUB_ICN)));
							else
								icon = new ImageIcon(ImageIO.read(new File(GROUP_ICN)));
						}
						if (icon != null)
							setIcon(icon);
					} catch (IOException ignore) {}
					return this;
				}
			});
			
			JPanel leftPane = new JPanel(new BorderLayout());
			leftPane.add(new JScrollPane(gTree), BorderLayout.CENTER);
			leftPane.add(gDetails, BorderLayout.SOUTH);

			String[] iHeaders = {
					"Read",
					"",
					"Date",
					"To",
					"Author",
					"Subject"
			};
			iTable = new iModel(iHeaders,0);
			topPane = new WebTable(iTable);
			topPane.getTableHeader().setReorderingAllowed(false);
			topPane.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			topPane.setEditable(false);
			topPane.getColumnModel().getColumn(0).setMaxWidth(READ_WIDTH);
			topPane.getColumnModel().getColumn(1).setMaxWidth(TYPE_WIDTH);
			
			splitPane = new WebSplitPane(WebSplitPane.VERTICAL_SPLIT, new JScrollPane(topPane), botPane);
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerLocation(APP_HEIGHT/3);
			splitPane.setContinuousLayout(true);
			
			String[] uHeaders = {
					"",
					"User"
					};
			uTable = new uModel(uHeaders,0);
			fullPane = new WebTable(uTable);
			topPane.getTableHeader().setReorderingAllowed(false);
			fullPane.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			fullPane.setEditable(false);
			fullPane.getColumnModel().getColumn(0).setMaxWidth(TYPE_WIDTH);
			
			center.add(splitPane);
			center.add(new JScrollPane(fullPane));
			
			try {
				ImageIcon usrPic = new ImageIcon(ImageIO.read(new File(USR_ICN)));
				userButton.setIcon(usrPic);
			} catch (IOException ignore) {}
			WebButtonPopup userPopup = new WebButtonPopup(userButton, PopupWay.upRight);
			userPopup.setContent(logout);
			userPopup.setDefaultFocusComponent(logout);
			
			try {
				ImageIcon fchIcn = new ImageIcon(ImageIO.read(new File(FCH_ICN)));
				refetch.setIcon(fchIcn);
			} catch (IOException ignore) {
				refetch.setText("Reload");
			}
		
			status.add(userButton, ToolbarLayout.START);
			status.add(memBar, ToolbarLayout.END);
			status.add(refetch, ToolbarLayout.END);
	
			this.add(bar, BorderLayout.NORTH);
			this.add(leftPane, BorderLayout.WEST);
			this.add(center, BorderLayout.CENTER);
			this.add(status, BorderLayout.SOUTH);
			
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
							int row = fullPane.getSelectedRow();
							fetchUsers();
							fullPane.setSelectedRow(row);
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
							int row = fullPane.getSelectedRow();
							fetchUsers();
							fullPane.setSelectedRow(row);
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
					gDetails.setVisible(false);
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
					gDetails.setVisible(false);
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
					gDetails.setVisible(false);
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
						gDetails.setTitle(g.getName());
						((GroupDetails) gDetails.getContent()).setGroup(g);
						gDetails.setVisible(true);
						iFilter = (lm) -> lm.getReference().getTo().equals(info);
						fetchInbox();
						target = (Group) info;

						return;
					}
				}
			});
			
			topPane.getSelectionModel().addListSelectionListener((e) -> {
				LocalMessage lm = iMap.get(topPane.getSelectedRow());
				if(lm != null) {
					Message msg = lm.getReference();
					botPane.setText(msg.getText());
					botPane.reply.setEnabled(true);
					botPane.remove.setEnabled(true);
					botPane.accept.setEnabled(msg instanceof Request);
					botPane.decline.setEnabled(msg instanceof Request);
					botPane.answer.setEnabled(msg instanceof Question);
					botPane.review.setEnabled(msg instanceof Question);
					botPane.blockUser.setEnabled(!app.getCurrentUser().getBlocked().contains(msg.getAuthor()));
				}
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
					userButton.setText(app.getCurrentUser().getName());
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
										|| ref.getTo().getName().contains(text)
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
			
			logout.addActionListener((e) -> {
				CardLayout clayout = (CardLayout) frame.getContentPane().getLayout();
				clayout.first(frame.getContentPane());
			});
			
			refetch.addActionListener((e) -> {
				try {
					app.reload();
					fetchGroups();
					fetchUsers();
					gTree.setSelectionPath(new TreePath(home.getPath()));
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(frame, ex, "Reload error", JOptionPane.WARNING_MESSAGE);
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
		
		private class uModel extends DefaultTableModel {
			private static final long serialVersionUID = 1L;

			public uModel(String[] columnNames, int rowCount) {
				super(columnNames, rowCount);
			}

			@Override
			public boolean isCellEditable (int row, int col) {
				return false;
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
		
		private class GroupDetails extends JPanel {
			private static final long serialVersionUID = 1L;
			private static final int GTYPE_HEIGHT = 26;
			private static final int GTYPE_WIDTH = 64;
			
			private WebSwitch type = new CustomSwitch();
			private ImageIcon social, study;
			private JCheckBox priv = new JCheckBox("Private");
			private JCheckBox mod = new JCheckBox("Moderated");
			private WebTextArea desc = new WebTextArea();
			private JLabel owner = new JLabel();
			
			public GroupDetails() {
				super(new BorderLayout());
				
				type.setRound(6);
				type.setFocusable(false);
				type.setPreferredHeight(GTYPE_HEIGHT);
				type.setPreferredWidth(GTYPE_WIDTH);
				try {
					study = new ImageIcon(ImageIO.read(new File(STU_ICN)));
					type.setLeftComponent(new WebLabel(study));
				} catch (IOException e) {
					type.setLeftComponent(new WebLabel("Study"));
				}
				try {
					social = new ImageIcon(ImageIO.read(new File(SEN_ICN)));
					type.setRightComponent(new WebLabel(social));
				} catch (IOException e) {
					type.setRightComponent(new WebLabel("Social"));
				}

				priv.setModel(new CustomButtonModel());
				priv.setFocusable(false);
				
				mod.setModel(new CustomButtonModel());
				mod.setFocusable(false);
				
				desc.setEditable(false);
				desc.setBackground(this.getBackground());
				desc.setForeground(Color.DARK_GRAY);
				
				JPanel topPart = new JPanel(new FlowLayout(FlowLayout.LEFT));
				
				topPart.add(type);
				topPart.add(priv);
				topPart.add(mod);
				
				JPanel botPart = new JPanel(new BorderLayout());
				
				botPart.add(owner, BorderLayout.NORTH);
				botPart.add(desc, BorderLayout.CENTER);
				
				this.add(topPart, BorderLayout.NORTH);
				this.add(botPart, BorderLayout.CENTER);
			}
			
			public void setGroup(Group g) {
				((CustomSwitch) type).doSetSelected(g instanceof SocialGroup);
				((CustomButtonModel) priv.getModel()).doSetSelected(g.isPrivate());
				((CustomButtonModel) mod.getModel()).doSetSelected(g.isModerated());
				desc.setText(g.getDesc());
				owner.setText("Owner: " + g.getOwner().getName());
			}
			
			private class CustomSwitch extends WebSwitch {
				private static final long serialVersionUID = 1L;

				@Override
				public void setSelected(boolean enabled) {}
				
				public void doSetSelected(boolean b) {
					super.setSelected(b);
				}
			}
			
			private class CustomButtonModel extends DefaultButtonModel {
				private static final long serialVersionUID = 1L;

				@Override
				public void setSelected(boolean b) {}
				
				public void doSetSelected(boolean b) {
					super.setSelected(b);
				}
			}
			
		}
		
		private class DetailsPanel extends JPanel {
			private static final long serialVersionUID = 1L;
			
			public JButton reply = new JButton("Reply");
			public JButton remove = new JButton("Remove");
			public JButton accept = new JButton("Accept");
			public JButton decline = new JButton("Decline");
			public JButton answer = new JButton("Answer");
			public JButton review = new JButton("Review");
			public JButton blockUser = new JButton("Block user");
			private JTextArea text = new JTextArea();
			
			public DetailsPanel() {
				super(new BorderLayout());
				JScrollPane scroll = new JScrollPane(text);
				JPanel bPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
				WebButtonGroup msgButtons = new WebButtonGroup(true, reply, remove);
				msgButtons.setButtonsDrawFocus(false);
				WebButtonGroup reqButtons = new WebButtonGroup(true, accept, decline);
				reqButtons.setButtonsDrawFocus(false);
				WebButtonGroup qstButtons = new WebButtonGroup(true, answer, review);
				qstButtons.setButtonsDrawFocus(false);
				
				text.setEditable(false);
				
				reply.setEnabled(false);
				remove.setEnabled(false);
				accept.setEnabled(false);
				decline.setEnabled(false);
				answer.setEnabled(false);
				review.setEnabled(false);
				blockUser.setEnabled(false);
				
				bPanel.add(msgButtons);
				bPanel.add(reqButtons);
				bPanel.add(qstButtons);
				bPanel.add(blockUser);
				
				this.add(bPanel, BorderLayout.NORTH);
				this.add(scroll, BorderLayout.CENTER);
				
				reply.addActionListener((e) -> {
					MessageWindow mw;
					Message msg = iMap.get(topPane.getSelectedRow()).getReference();

					Entity to = msg.getTo();
					if (to instanceof User)
						to = msg.getAuthor();
					mw = new MessageWindow(frame, to);
					mw.setSubject("RE: " + msg.getSubject());
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
				
				accept.addActionListener((e) -> {
					if (JOptionPane.showConfirmDialog(frame, "Accept this request?", "Handle request", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
						try {
							app.handleRequest((Request) iMap.get(topPane.getSelectedRow()).getReference(), true);
							fetchInbox();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, ex, "Accept request error", JOptionPane.WARNING_MESSAGE);
						}
				});
				
				decline.addActionListener((e) -> {
					if (JOptionPane.showConfirmDialog(frame, "Decline this request?", "Handle request", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
						try {
							app.handleRequest((Request) iMap.get(topPane.getSelectedRow()).getReference(), false);
							fetchInbox();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, ex, "Decline request error", JOptionPane.WARNING_MESSAGE);
						}
				});
				
				answer.addActionListener((e) -> {
					AnswerWindow aw = new AnswerWindow(frame, (Question) iMap.get(topPane.getSelectedRow()).getReference());
					aw.setVisible(true);
					fetchInbox();
				});
				
				//TODO Review
				
				blockUser.addActionListener((e) -> {
					Entity u = iMap.get(topPane.getSelectedRow()).getReference().getAuthor();
					if (JOptionPane.showConfirmDialog(frame, "Do you want to block " + u.getName() + "?", "Block", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
						try {
							app.block(u);
							blockUser.setEnabled(false);
							fetchUsers();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(frame, ex, "Block error", JOptionPane.WARNING_MESSAGE);
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
						ImageIcon icon;
						Message msg = lm.getReference();
						try {
							if (msg instanceof Request)
								icon = new ImageIcon(ImageIO.read(new File(REQ_ICN)));
							else if (msg instanceof Question)
								icon = new ImageIcon(ImageIO.read(new File(QST_ICN)));
							else if (msg instanceof Answer)
								icon = new ImageIcon(ImageIO.read(new File(ANS_ICN)));
							else
								icon = new ImageIcon(ImageIO.read(new File(MSG_ICN)));
						} catch (Exception e) {
							icon = new ImageIcon();
						}
						return new Object[] {
								lm,
								new Object[] {
										lm.isRead(),
										icon,
										lm.getReference().getTime().getTime().toString(),
										lm.getReference().getTo().getName(),
										lm.getReference().getAuthor().getName(),
										lm.getReference().getSubject() } };
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
				ImageIcon icon;
				try {
					if (app.getCurrentUser().getBlocked().contains(u))
						icon = new ImageIcon(ImageIO.read(new File(UBLK_ICN)));
					else if (u instanceof Student)
						icon = new ImageIcon(ImageIO.read(new File(STU_ICN)));
					else
						icon = new ImageIcon(ImageIO.read(new File(SEN_ICN)));
				} catch (Exception e) {
					icon = new ImageIcon();
				}
				return new Object[] { u, new Object[] {
						icon,
						u.getName() } };
			}).forEach((entry) -> {
				uTable.addRow((Object[]) entry[1]);
				uMap.put(uTable.getRowCount() - 1, (User) entry[0]);
			});
		}
	}

}
