package jp.ramen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import com.alee.laf.text.WebTextField;

import jp.ramen.Group;
import jp.ramen.RAMEN;
import jp.ramen.Sensei;
import jp.ramen.SocialGroup;
import jp.ramen.StudyGroup;

/**
 * Create group window
 * @author Sergio Fuentes "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices "daniel.perdices@estudiante.uam.es"
 */
public class CreateGroupWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final int MARGIN = 20;
	private static final int HEIGHT = 300;
	private static final int WIDTH = 480;
	private RAMEN app = RAMEN.getInstance();

	//First line
	private JLabel inside = new JLabel("Inside: ");
	private ButtonGroup type = new ButtonGroup();
	private JRadioButton social = new JRadioButton("Social");
	private JRadioButton study = new JRadioButton("Study");
	private JComboBox<String> list;
	private DefaultComboBoxModel<String> mlist;
	private JCheckBox moderated = new JCheckBox("Moderated");
	private JCheckBox _private = new JCheckBox("Private");
	
	//Second Line
	private JLabel name = new JLabel("Name: ");
	private WebTextField nametf = new WebTextField(){
		private static final long serialVersionUID = 1L;
		public void addNotify() {
	        super.addNotify();
	        requestFocus();
	    }
	};;
	
	//Third Line
	private JLabel desc = new JLabel("Desc: ");
	private JTextArea desctf = new JTextArea(6,50);
	//Bottom buttons
	private JButton cancel = new JButton("Cancel");
	private JButton create = new JButton("Create");
	
	//attr
	private CreateGroupWindow frame;

	/**
	 * Constructor
	 * @param owner the superframe
	 * @param supergroup the parent group if exists or null
	 */
	public CreateGroupWindow(JFrame owner, Group supergroup) {
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.frame = this;
		this.setSize(WIDTH,HEIGHT);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		String code;
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		
		if(supergroup != null) code = supergroup.getCode();
		else code = "Home";
		JPanel buttons = new JPanel();
		FlowLayout buttonsLayout = new FlowLayout(FlowLayout.RIGHT);
		buttons.setLayout(buttonsLayout);
		buttons.add(cancel);
		buttons.add(create);
		this.add(buttons, BorderLayout.SOUTH);
		
		JPanel box = new JPanel();
		SpringLayout layout = new SpringLayout();
		box.setLayout(layout);
		JPanel radioButtons = new JPanel();
		JPanel checkBoxes = new JPanel();
		checkBoxes.setLayout(new BoxLayout(checkBoxes,BoxLayout.Y_AXIS));
		mlist = new DefaultComboBoxModel<String>();
		
		mlist.addElement("Home");
		app.listGroups().stream().map(Group::getCode).forEach(g ->mlist.addElement(g) );
		mlist.setSelectedItem(code);
		list = new JComboBox<String>(mlist);
		list.setPreferredSize(new Dimension(150, 30));
		radioButtons.setLayout(new BoxLayout(radioButtons,BoxLayout.Y_AXIS));
		radioButtons.add(social);
		radioButtons.add(study);
		checkBoxes.add(moderated);
		checkBoxes.add(_private);
		box.add(inside);
		type.add(social);
		type.add(study);
		
		study.addActionListener(a ->{
			moderated.setEnabled(false);
			moderated.setSelected(false);
			_private.setEnabled(false);
			_private.setSelected(false);
		});
		
		social.addActionListener(a -> {
			moderated.setEnabled(true);
			moderated.setSelected(false);
			_private.setEnabled(true);
			_private.setSelected(false);
		});
		
		if(app.getCurrentUser() instanceof Sensei) {
			study.doClick();
			social.setEnabled(false);
			study.setEnabled(false);
		}
		else {
			social.doClick();
			study.setEnabled(false);
			social.setEnabled(false);
		}
		
		
		if(supergroup instanceof SocialGroup) social.doClick();
		else if(supergroup instanceof StudyGroup) study.doClick();
		else social.doClick();
		
		if(!(supergroup == null)&&supergroup.isModerated()) moderated.doClick();
		if(!(supergroup == null)&&supergroup.isPrivate()) _private.doClick();
		if(supergroup != null) {
			social.setEnabled(false);
			study.setEnabled(false);
			_private.setEnabled(false);
			moderated.setEnabled(false);
		}
		box.add(radioButtons);
		box.add(list);
		box.add(checkBoxes);
		box.add(name);
		box.add(nametf);
		
		JScrollPane scrollDesc = new JScrollPane(desctf);
		desctf.setLineWrap(true);
		scrollDesc.setAutoscrolls(true);
		scrollDesc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollDesc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		box.add(desc);
		box.add(scrollDesc);
		//Inside
		layout.putConstraint(SpringLayout.WEST, inside, MARGIN, SpringLayout.WEST, box);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, inside, 0, SpringLayout.VERTICAL_CENTER, radioButtons);
		layout.putConstraint(SpringLayout.NORTH, radioButtons, MARGIN, SpringLayout.NORTH, box);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, list, 0, SpringLayout.VERTICAL_CENTER, radioButtons);
		layout.putConstraint(SpringLayout.WEST, list, MARGIN, SpringLayout.EAST, inside);
		layout.putConstraint(SpringLayout.WEST, radioButtons, MARGIN, SpringLayout.EAST, list);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, checkBoxes, 0, SpringLayout.VERTICAL_CENTER, radioButtons);
		layout.putConstraint(SpringLayout.WEST, checkBoxes, MARGIN, SpringLayout.EAST, radioButtons);
		//Name
		layout.putConstraint(SpringLayout.NORTH, name, MARGIN, SpringLayout.SOUTH, radioButtons);
		layout.putConstraint(SpringLayout.WEST, name, MARGIN, SpringLayout.WEST, box);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, nametf, 0, SpringLayout.VERTICAL_CENTER, name);
		layout.putConstraint(SpringLayout.WEST, nametf, MARGIN, SpringLayout.EAST, name);
		layout.putConstraint(SpringLayout.EAST, nametf, -MARGIN, SpringLayout.EAST, box);
		//Text
		layout.putConstraint(SpringLayout.WEST, desc, MARGIN, SpringLayout.WEST, box);
		layout.putConstraint(SpringLayout.NORTH, desc, MARGIN, SpringLayout.SOUTH, nametf);
		layout.putConstraint(SpringLayout.NORTH, scrollDesc, MARGIN, SpringLayout.SOUTH, nametf);
		layout.putConstraint(SpringLayout.EAST, scrollDesc, 0, SpringLayout.EAST, nametf);
		layout.putConstraint(SpringLayout.WEST, scrollDesc, 0, SpringLayout.WEST, nametf);
		layout.putConstraint(SpringLayout.SOUTH, scrollDesc, -MARGIN, SpringLayout.SOUTH, box);
		this.add(box, BorderLayout.CENTER);
		
		cancel.addActionListener(a -> this.dispose());
		create.addActionListener(a -> {
			String n=nametf.getText();
			String d=desctf.getText();
			n = n.replace("'", "");
			d = d.replace("'", "");
			Group superg;
			if(mlist.getSelectedItem().equals("Home")) superg =null;
			else superg = app.getDAO().getGdb().getGroup(((String)mlist.getSelectedItem()));
			try {
				app.createGroup(n,
						d,
						superg, social.isSelected(), _private.isSelected(), moderated.isSelected() );
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame, e, "Create group error", JOptionPane.WARNING_MESSAGE);			
				return;
			}
			this.dispose();
		});

		list.addActionListener(a ->{
			String c = ((String) mlist.getSelectedItem());
			if(c.equals("Home")) {
				social.setEnabled(true);
				study.setEnabled(true);
				_private.setEnabled(true);
				moderated.setEnabled(true);
			}
			else {
				Group g = app.getDAO().getGdb().getGroup(c);
				social.setEnabled(false);
				study.setEnabled(false);
				_private.setEnabled(false);
				moderated.setEnabled(false);
				_private.setSelected(g.isPrivate());
				moderated.setSelected(g.isModerated());
			}
			
			if(app.getCurrentUser() instanceof Sensei) {
				study.doClick();
				social.setEnabled(false);
				study.setEnabled(false);
			}
			else {
				social.doClick();
				study.setEnabled(false);
				social.setEnabled(false);
			}
		});
	}
}
