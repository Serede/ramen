package jp.ramen.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;

import estadisticas.PieChartSample;
import jp.ramen.Answer;
import jp.ramen.Question;
import jp.ramen.RAMEN;
import jp.ramen.User;

public class ReviewAnswersWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 640;
	private static final int WIDTH = 480;
	private static final int MARGIN =  10;
	private WebButton students = new WebButton("Students");
	private WebButton answers = new WebButton("Answers");
	private WebButtonGroup topButtons = new WebButtonGroup(students, answers);
	private JFrame owner;
	private Question question;
	
	private RAMEN app = RAMEN.getInstance();
	private JPanel center = new JPanel();
	
	public ReviewAnswersWindow(JFrame owner, Question question) {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(WIDTH,HEIGHT);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setLayout(new BorderLayout());
		topButtons.setButtonsDrawFocus(false);
		JPanel buttons = new JPanel();
		buttons.add(topButtons);
		this.add(buttons, BorderLayout.NORTH);
		center.setLayout(new CardLayout());
		
		center.add(students, "Student");

		
		
		this.add(students,BorderLayout.CENTER);
	}
	
	private class StudentsCard extends JPanel {
		private JTable table1, table2;
		private JLabel header1, header2;
		private JButton showPie = new JButton("Show graph");
		private static final String ICN = "";
		public StudentsCard() {
			SpringLayout layout = new SpringLayout();
			this.setLayout(layout);
			this.add(table1);
			this.add(table2);
			this.add(header1);
			this.add(header2);
			
			try{
				ImageIcon icon = new ImageIcon(ImageIO.read(new File(ICN)));
				showPie.setIcon(icon);
			} catch (Exception e1){}
			
			DefaultTableModel mtable1 = new DefaultTableModel();
			question.whoAnswered().forEach(u -> 
				mtable1.addRow(
						new Object[]{
								u.getName(),
								true
						}
			));
			
			DefaultTableModel mtable2 = new DefaultTableModel();
			question.whoDidntAnswer().forEach(u -> 
			mtable2.addRow(
					new Object[]{
							u.getName(),
							false
					}
		));
			table1 = new JTable(mtable1);
			table2 = new JTable(mtable2);
			header1 = new JLabel("Answered "+question.howManyAnswered()+"/"+question.howManyDidntAnswer()+question.howManyAnswered());
			header2 = new JLabel("Not answered yet "+question.howManyDidntAnswer()+"/"+question.howManyDidntAnswer()+question.howManyAnswered());
			
			layout.putConstraint(SpringLayout.NORTH, header1, MARGIN, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.NORTH, header2, MARGIN, SpringLayout.NORTH, this);
			
			layout.putConstraint(SpringLayout.NORTH, table1, MARGIN, SpringLayout.SOUTH, header1);
			layout.putConstraint(SpringLayout.WEST, table1, MARGIN, SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.EAST, table2, MARGIN, SpringLayout.EAST, this);
			layout.putConstraint(SpringLayout.EAST, table1, 2*MARGIN, SpringLayout.WEST, table2);
			
			layout.putConstraint(SpringLayout.SOUTH, showPie, MARGIN, SpringLayout.SOUTH, this);
			layout.putConstraint(SpringLayout.EAST, showPie, 0, SpringLayout.EAST, table2);
			layout.putConstraint(SpringLayout.WEST, showPie, 0, SpringLayout.WEST, table1);
			layout.putConstraint(SpringLayout.SOUTH, showPie, MARGIN, SpringLayout.SOUTH, this);

			showPie.addActionListener(a -> {
				PieChartSample.muestraGrafico("Statistics", question.howManyAnswered(), question.howManyDidntAnswer());
			});

			
		}
	}
	
	public static void main(String[] args) throws Exception {
		RAMEN.getInstance().init();
		RAMEN.getInstance().login("dani", "dani");
		new ReviewAnswersWindow(new JFrame(), null).setVisible(true);
	}
}
