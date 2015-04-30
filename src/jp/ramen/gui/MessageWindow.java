package jp.ramen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import jp.ramen.Entity;
import jp.ramen.Group;
import jp.ramen.RAMEN;
import jp.ramen.Sensei;
import jp.ramen.User;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.text.WebTextField;


public class MessageWindow extends JDialog{
	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 300;
	private static final int WIDTH = 480;
	private static final int MARGIN =  10;
	private MessageWindow frame;
	
	private RAMEN app = RAMEN.getInstance();
	private JButton cancel= new JButton("Discard");
	private JButton send= new JButton("Send"); 
	
	private JLabel subj = new JLabel("Subject: ");
	private WebTextField subjtf = new WebTextField(32){
		private static final long serialVersionUID = 1L;
		public void addNotify() {
	        super.addNotify();
	        requestFocus();
	    }
	};
	
	private JLabel text = new JLabel("Text: ");
	private JTextArea texttf = new JTextArea(6,50);
	
	private JLabel to = new JLabel("To: ");
	private ButtonGroup sendRadioButtons = new ButtonGroup();
	private JRadioButton user = new JRadioButton("User");
	private JRadioButton group = new JRadioButton("Group");
	private JComboBox<String> list;
	private DefaultComboBoxModel<String> mlist;
	private WebCheckBox question = new WebCheckBox("Question");
	private JLabel limit = new JLabel("200");
	
	private Entity addressee;
	private JFrame owner;
	
	public Runnable run = () -> {
		WebLookAndFeel.install();
		try {
			if (frame == null)
				frame = new MessageWindow(owner, addressee);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, e);
		}
		frame.setVisible(true);
	};
	
	public MessageWindow(JFrame owner, Entity addressee) {
		super(owner, "Send a message");
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.owner = owner;
		this.addressee = addressee;
		this.setSize(WIDTH,HEIGHT);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		
		if(addressee==null) addressee = app.getCurrentUser();
		this.setLayout(new BorderLayout());
		
		JPanel buttons = new JPanel();
		FlowLayout buttonsLayout = new FlowLayout(FlowLayout.RIGHT);
		buttons.setLayout(buttonsLayout);
		buttons.add(cancel);
		buttons.add(send);
		this.add(buttons, BorderLayout.SOUTH);
		
		JPanel box = new JPanel();
		SpringLayout layout = new SpringLayout();
		JPanel radioButtons = new JPanel();
		mlist = new DefaultComboBoxModel<String>();
		
		list = new JComboBox<String>(mlist);
		list.setPreferredSize(new Dimension(150, 30));
		radioButtons.setLayout(new BoxLayout(radioButtons,BoxLayout.Y_AXIS));
		box.add(to);
		
		//RadioButtons configuration
		ActionListener buttonSelected = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mlist.removeAllElements();
				if(user.isSelected()) RAMEN.getInstance().listUsers().stream().map(User::getName).forEach(c -> mlist.addElement(c));
				else RAMEN.getInstance().listGroups().stream().map(Group::getCode).forEach(c -> mlist.addElement(c));
			}
		};
		user.addActionListener(buttonSelected);
		group.addActionListener(buttonSelected);
		if(addressee instanceof User) user.doClick();
		else group.doClick();
		
		mlist.setSelectedItem(addressee.getName());
		sendRadioButtons.add(user);
		sendRadioButtons.add(group);
		radioButtons.add(user);
		radioButtons.add(group);
		
		
		box.add(radioButtons);
		box.add(list);
		
		
		box.add(subj);
		box.add(subjtf);
		
		JScrollPane scrollText = new JScrollPane(texttf);
		texttf.setLineWrap(true);
		scrollText.setAutoscrolls(true);
		scrollText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollText.setEnabled(true);
		box.add(question);
		if(app.getCurrentUser() instanceof Sensei) question.setEnabled(true);
		else {question.setEnabled(false);question.setVisible(false);}
		box.add(text);
		box.add(scrollText);
		box.add(limit);
		limit.setText("200");
		limit.setForeground(Color.GREEN);
		limit.setVisible(true);
		this.add(box, BorderLayout.CENTER);
		//To
		layout.putConstraint(SpringLayout.NORTH, radioButtons, MARGIN, SpringLayout.NORTH, box);
		layout.putConstraint(SpringLayout.WEST, radioButtons, 0, SpringLayout.WEST, subjtf);
		layout.putConstraint(SpringLayout.WEST, to, MARGIN, SpringLayout.WEST, box);
		layout.putConstraint(SpringLayout.WEST, list, MARGIN, SpringLayout.EAST, radioButtons);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, list, 0, SpringLayout.VERTICAL_CENTER, radioButtons);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, to, 0, SpringLayout.VERTICAL_CENTER, radioButtons);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, question, 0, SpringLayout.VERTICAL_CENTER, radioButtons);
		layout.putConstraint(SpringLayout.WEST, question, 0, SpringLayout.EAST, list);

		//Subj
		layout.putConstraint(SpringLayout.NORTH, subj, MARGIN+5, SpringLayout.SOUTH, radioButtons);
		layout.putConstraint(SpringLayout.WEST, subj, MARGIN, SpringLayout.WEST, box);
		layout.putConstraint(SpringLayout.NORTH, subjtf, MARGIN, SpringLayout.SOUTH, radioButtons);
		layout.putConstraint(SpringLayout.WEST, subjtf, MARGIN, SpringLayout.EAST, subj);getContentPane();
		layout.putConstraint(SpringLayout.EAST, subjtf, -MARGIN, SpringLayout.EAST, box);
		//Text
		layout.putConstraint(SpringLayout.NORTH, text, MARGIN+5, SpringLayout.SOUTH, subj);
		layout.putConstraint(SpringLayout.WEST, text, MARGIN, SpringLayout.WEST, box);
		layout.putConstraint(SpringLayout.NORTH, scrollText, MARGIN, SpringLayout.SOUTH, subjtf);
		layout.putConstraint(SpringLayout.WEST, scrollText, 0, SpringLayout.WEST, subjtf);
		layout.putConstraint(SpringLayout.SOUTH, scrollText, -MARGIN, SpringLayout.NORTH, limit);
		layout.putConstraint(SpringLayout.EAST, scrollText, -MARGIN, SpringLayout.EAST, box);
		layout.putConstraint(SpringLayout.WEST, limit, 0, SpringLayout.WEST, scrollText);
//		layout.putConstraint(SpringLayout.NORTH, limit, MARGIN, SpringLayout.SOUTH, scrollText);
		layout.putConstraint(SpringLayout.SOUTH, limit, 0, SpringLayout.SOUTH, box);

		box.setLayout(layout);
		
		texttf.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println(texttf.getText().length());
				limit.setText(String.valueOf(200-texttf.getText().length()));
				if(Integer.valueOf(limit.getText())>=0) limit.setForeground(Color.GREEN);
				else limit.setForeground(Color.RED);
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		cancel.addActionListener(a -> this.dispose());
		
		send.addActionListener(a -> {
			String s = subjtf.getText();
			String t = texttf.getText();
			s = s.replace("'", "");
			t = t.replace("'", "");
			Entity e = group.isSelected()?app.getDAO().getGdb().getGroup((String)list.getSelectedItem()):app.getDAO().getUdb().getUser((String)list.getSelectedItem());
			try {
				
				app.sendMessage(e,
				s, t, question.isSelected());
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, e1.toString(), "Send a message", JOptionPane.WARNING_MESSAGE);
				return;
			}
			this.dispose();
		});
		
	}
}
