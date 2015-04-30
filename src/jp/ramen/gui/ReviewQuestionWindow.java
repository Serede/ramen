package jp.ramen.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import com.alee.extended.panel.WebAccordion;
import com.alee.extended.panel.WebButtonGroup;
import com.alee.laf.button.WebButton;
import com.alee.laf.list.WebList;
import estadisticas.PieChartSample;
import jp.ramen.Question;

/**
 * Window to see the results of a question
 * @author Sergio Fuentes "sergio.fuentesd@estudiante.uam.es"
 * @author Daniel Perdices "daniel.perdices@estudiante.uam.es"
 */
public class ReviewQuestionWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final int D_HEIGHT = 400;
	private static final int D_WIDTH = 500;
	private WebButton stats = new WebButton("Statistics");
	private WebButton answers = new WebButton("Answers");
	private boolean ansCard = false;
	private WebButtonGroup topButtons = new WebButtonGroup(stats, answers);
	private Question question;
	
	private JPanel center = new JPanel();

	/**
	 * Constructor
	 * @param owner superframe
	 * @param question the observed question
	 */
	public ReviewQuestionWindow(JFrame owner, Question question) {
		this.question = question;

		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(D_WIDTH,D_HEIGHT);
		this.setMinimumSize(new Dimension(D_WIDTH, D_HEIGHT));
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
		stats.addActionListener(a ->{
			if (ansCard) {
				layout.first(center);
				ansCard = false;
			}
		});
		
		answers.addActionListener(a ->{
			if (!ansCard) {
				layout.next(center);
				ansCard = true;
			}
		});
	}

	/**
	 * Stats card
	 * @author Sergio Fuentes "sergio.fuentesd@estudiante.uam.es"
	 * @author Daniel Perdices "daniel.perdices@estudiante.uam.es"
	 */
	private class StatsCard extends JPanel {
		private static final long serialVersionUID = 1L;
		private static final int LIST_WIDTH = 180;
		private static final int BUTTON_HEIGHT = 32;
		private static final String ICN = "img/icons/chart_pie.png";
		
		private WebList list1, list2;
		private JLabel header1 = new JLabel(), header2 = new JLabel();
		private JButton showPie = new JButton("Show graph");

		public StatsCard() {
			SpringLayout l = new SpringLayout(); 
			this.setLayout(l);

			try{
				ImageIcon icon = new ImageIcon(ImageIO.read(new File(ICN)));
				showPie.setIcon(icon);
			} catch (Exception ignore) {}
			showPie.setPreferredSize(new Dimension(2*LIST_WIDTH, BUTTON_HEIGHT));

			Object[] data1 = question.whoAnswered().stream().map((u) -> u.getName()).toArray();
			
			Object[] data2 = question.whoDidntAnswer().stream().map((u) -> u.getName()).toArray();
			
			list1 = new WebList(data1.length==0? new Object[] {"None"} : data1);
			list1.setPreferredWidth(LIST_WIDTH);
			list1.setEditable(false);
			list2 = new WebList(data2.length==0? new Object[] {"None"} : data2);
			list2.setPreferredWidth(LIST_WIDTH);
			list2.setEditable(false);
			
			int total = question.howManyDidntAnswer()+question.howManyAnswered();
			header1 = new JLabel("Answered ("+question.howManyAnswered()+"/"+total+")");
			header2 = new JLabel("Not answered yet ("+question.howManyDidntAnswer()+"/"+total+")");
		
			JScrollPane scroll1 = new JScrollPane(list1);
			JScrollPane scroll2 = new JScrollPane(list2);
			this.add(header1);
			this.add(header2);
			this.add(scroll1);
			this.add(scroll2);
			this.add(showPie);
			
			l.putConstraint(SpringLayout.HORIZONTAL_CENTER, scroll1, -2*LIST_WIDTH/3, SpringLayout.HORIZONTAL_CENTER, this);
			l.putConstraint(SpringLayout.VERTICAL_CENTER, scroll1, -12, SpringLayout.VERTICAL_CENTER, this);
			l.putConstraint(SpringLayout.HORIZONTAL_CENTER, scroll2, 2*LIST_WIDTH/3, SpringLayout.HORIZONTAL_CENTER, this);
			l.putConstraint(SpringLayout.VERTICAL_CENTER, scroll2, -12, SpringLayout.VERTICAL_CENTER, this);
			l.putConstraint(SpringLayout.SOUTH, header1, -6, SpringLayout.NORTH, scroll1);
			l.putConstraint(SpringLayout.SOUTH, header2, -6, SpringLayout.NORTH, scroll2);
			l.putConstraint(SpringLayout.HORIZONTAL_CENTER, header1, 0, SpringLayout.HORIZONTAL_CENTER, scroll1);
			l.putConstraint(SpringLayout.HORIZONTAL_CENTER, header2, 0, SpringLayout.HORIZONTAL_CENTER, scroll2);
			l.putConstraint(SpringLayout.NORTH, showPie, 12, SpringLayout.SOUTH, scroll2);
			l.putConstraint(SpringLayout.HORIZONTAL_CENTER, showPie, 0, SpringLayout.HORIZONTAL_CENTER, this);
			
			showPie.addActionListener(a -> {
				if(question.howManyAnswered()!=0 || question.howManyDidntAnswer()!=0)
					PieChartSample.muestraGrafico("Statistics", question.howManyAnswered(), question.howManyDidntAnswer());
			});



		}
	}

	/**
	 * Answer panels
	 * @author Sergio Fuentes "sergio.fuentesd@estudiante.uam.es"
	 * @author Daniel Perdices "daniel.perdices@estudiante.uam.es"
	 */
	private class AnswerCard extends JPanel {
		private static final long serialVersionUID = 1L;
		WebAccordion center = new WebAccordion();

		public AnswerCard() {
			if(question.reviewAnswers().isEmpty()){
				this.add(new JLabel("No answers yet."));
			} else {
				center.setMultiplySelectionAllowed(false);
				center.setPreferredWidth(2*D_WIDTH/3);
				center.setPreferredHeight(2*D_HEIGHT/3);
				question.reviewAnswers().forEach((a) -> {
					JTextArea text = new JTextArea(a.getText());
					text.setEditable(false);
					text.setLineWrap(true);
					text.setWrapStyleWord(true);
					center.addPane(a.getAuthor().getName(), new JScrollPane(text));
				});
				this.add(new JScrollPane(center));
			}
		}
	}
}
