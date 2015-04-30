package jp.ramen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import jp.ramen.Entity;
import jp.ramen.Question;
import jp.ramen.RAMEN;

import com.alee.laf.text.WebTextField;

public class AnswerWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 300;
	private static final int WIDTH = 480;
	private static final int MARGIN =  10;
	private AnswerWindow frame;
	
	private RAMEN app = RAMEN.getInstance();
	private JButton cancel= new JButton("Discard");
	private JButton send= new JButton("Send"); 
	
	private JLabel subj = new JLabel("Subject: ");
	private WebTextField subjtf = new WebTextField(32);
	
	private JLabel text = new JLabel("Text: ");
	private JTextArea texttf = new JTextArea(6,50){
		private static final long serialVersionUID = 1L;
		public void addNotify() {
	        super.addNotify();
	        requestFocus();
	    }
	};
	
	private Entity addressee;
	private String subject;
	
	public AnswerWindow(JFrame owner, Question question) {
		this.addressee = question.getTo();
		this.subject = "ANS: " + question.getSubject();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(WIDTH,HEIGHT);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setModal(true);
		this.setLocationRelativeTo(null);
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
		
		box.add(subj);
		box.add(subjtf);
		
		JScrollPane scrollText = new JScrollPane(texttf);
		texttf.setLineWrap(true);
		scrollText.setAutoscrolls(true);
		scrollText.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollText.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollText.setEnabled(true);
		box.add(text);
		box.add(scrollText);
		this.add(box, BorderLayout.CENTER);
		
		subjtf.setText(subject);
		subjtf.setEditable(false);
		subjtf.setEnabled(false);
		//Subj
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, subj, 0, SpringLayout.VERTICAL_CENTER, subjtf);
		layout.putConstraint(SpringLayout.WEST, subj, MARGIN, SpringLayout.WEST, box);
		layout.putConstraint(SpringLayout.NORTH, subjtf, MARGIN, SpringLayout.NORTH, box);
		layout.putConstraint(SpringLayout.WEST, subjtf, MARGIN, SpringLayout.EAST, subj);
		layout.putConstraint(SpringLayout.EAST, subjtf, -MARGIN, SpringLayout.EAST, box);
		//Text
		layout.putConstraint(SpringLayout.NORTH, text, MARGIN+5, SpringLayout.SOUTH, subj);
		layout.putConstraint(SpringLayout.WEST, text, MARGIN, SpringLayout.WEST, box);
		layout.putConstraint(SpringLayout.NORTH, scrollText, MARGIN, SpringLayout.SOUTH, subjtf);
		layout.putConstraint(SpringLayout.WEST, scrollText, 0, SpringLayout.WEST, subjtf);
		layout.putConstraint(SpringLayout.SOUTH, scrollText, 0, SpringLayout.SOUTH, box);
		layout.putConstraint(SpringLayout.EAST, scrollText, -MARGIN, SpringLayout.EAST, box);
		
		box.setLayout(layout);
		
		cancel.addActionListener(a -> this.dispose());
		final Entity addr = addressee;
		send.addActionListener(a -> {
			subject = subject.replace("'", "");
			String t = texttf.getText();
			t.replace("'", "");
			try {
				app.sendAnswer(addr, subject, t, question);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(frame, ex, "Send answer error", JOptionPane.WARNING_MESSAGE);
				return;
			}
			this.dispose();
		});
		
	}
	
}
