package jp.ramen.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;




import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;








import com.alee.extended.panel.WebAccordion;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;








import estadisticas.PieChartSample;
import jp.ramen.Answer;
import jp.ramen.Question;
import jp.ramen.RAMEN;
import jp.ramen.User;

public class ReviewQuestionWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final int HEIGHT = 640;
	private static final int WIDTH = 480;
	private WebButton stati = new WebButton("Stats");
	private WebButton answers = new WebButton("Answers");
	private WebButtonGroup topButtons = new WebButtonGroup(stati, answers);
	private Question question;
	
	private JPanel center = new JPanel();

	public ReviewQuestionWindow(JFrame owner, Question question) {
		this.question = question;

		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(WIDTH,HEIGHT);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setLayout(new BorderLayout());
		topButtons.setButtonsDrawFocus(false);


		JPanel buttons = new JPanel();
		buttons.add(topButtons);


		CardLayout layout = new CardLayout();
		center.setLayout(layout);

		center.add(new StatsCard());
		center.add(new AnswerCard());

		this.add(buttons, BorderLayout.NORTH);
		this.add(center,BorderLayout.CENTER);
		JButton close = new JButton("Close");
		this.add(close, BorderLayout.SOUTH);
		
		close.addActionListener(a -> this.dispose());
		stati.addActionListener(a ->{ 
			layout.next(center);
		});
		
		answers.addActionListener(a ->{ 
			layout.next(center); 
		});
	}

	private class StatsCard extends JPanel {
		private static final long serialVersionUID = 1L;
		private JTable table1 = new JTable(), table2 = new JTable();
		private JLabel header1 = new JLabel("sdja"), header2 = new JLabel("sdoija");
		private JButton showPie = new JButton("Show graph");
		private static final String ICN = "img/icons/chart_pie.png";

		public StatsCard() {
			BorderLayout l = new BorderLayout(); 
			l.setHgap(0);
			this.setLayout(l);
			JPanel right = new JPanel();
			right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
			JPanel left = new JPanel();
			left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
			this.add(showPie, BorderLayout.SOUTH);
			this.add(right, BorderLayout.WEST);
			this.add(left, BorderLayout.EAST);
			try{
				ImageIcon icon = new ImageIcon(ImageIO.read(new File(ICN)));
				showPie.setIcon(icon);
			} catch (Exception e1){}

			String[] array = {"Student"};

			DefaultTableModel mtable1 = new DefaultTableModel(array,0);
			System.out.println(question.whoAnswered());
			System.out.println(question.whoDidntAnswer());

			question.whoAnswered().forEach(u -> 
			mtable1.addRow(
					new Object[]{
							u.getName(),
							true
					}
					));
			DefaultTableModel mtable2 = new DefaultTableModel(array,0);
			
			question.whoDidntAnswer().forEach(u -> 
			mtable2.addRow(
					new Object[]{
							u.getName()
					}
					));
			table1 = new JTable(mtable1);
			table2 = new JTable(mtable2);
			int total = question.howManyDidntAnswer()+question.howManyAnswered();
			header1 = new JLabel("Answered "+question.howManyAnswered()+"/"+total);
			header2 = new JLabel("Not answered yet "+question.howManyDidntAnswer()+"/"+total);

			right.add(header1);
			left.add(header2);
			right.add(table1);
			left.add(table2);
			
			showPie.addActionListener(a -> {
				if(question.howManyAnswered()<=0&&question.howManyDidntAnswer()==0) return;
				PieChartSample.muestraGrafico("Statistics", question.howManyAnswered(), question.howManyDidntAnswer());
			});



		}
	}

	private class AnswerCard extends JPanel {
		private static final long serialVersionUID = 1L;
		WebAccordion center = new WebAccordion();

		public AnswerCard() {
			if(question.howManyAnswered()==0){
				this.add(new JLabel("There is no message"));
			}else {
				question.reviewAnswers().stream().forEach(a ->{
					System.out.println(a);
					JScrollPane scrollText = new JScrollPane();
					JTextArea text = new JTextArea(10,20);
					text.setText(a.getText());
					scrollText.add(text);
					scrollText.setMinimumSize(new Dimension(30,30));
					text.setEditable(false);
					center.addPane(a.getAuthor().getName(), text);
					this.add(center);
				});
			}
		}
	}
}
