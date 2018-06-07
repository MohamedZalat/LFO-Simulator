package lfo.simulator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class SettingsPanel extends JPanel implements ActionListener{

//	public static void main(String args[]){
//		JFrame frame = new JFrame();
//		frame.add(new SettingsPanel(null, null));
//		
//		frame.setSize(800, 600);
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		
//	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TrajectoryStatePanel panel;

	private JCheckBox box;
	private JCheckBox regular;
	
	private JTextField counter;
	
	private TraceVisualizer visual;
	
	private Timer timer;
	private JTextField field;
	
	private JButton button;
	
	public SettingsPanel(TrajectoryStatePanel panel, TraceVisualizer visual){
		this.panel = panel;
		this.visual = visual;
		
		this.setLayout(new SpringLayout());
		
		box = new JCheckBox("Show Errors");
		box.setSelected(true);
		box.addActionListener(this);
		add(box);
		
		counter = new JTextField("0");
		counter.setEditable(false);
		add(counter);
		
		regular = new JCheckBox("Hide Expert Trace");
		regular.addActionListener(this);
		add(regular);
		
		add(new JPanel());
		
		button = new JButton("Play");
		button.addActionListener(this);
		add(button);
		
		field = new JTextField("10");
		add(field);
		
		JButton b = new JButton("Reset");
		b.addActionListener(this);
		add(b);
		
		add(new JPanel());
		
		SpringUtilities.makeGrid(this,
                4, 2, //rows, cols
                5, 5, //initialX, initialY
                5, 5);//xPad, yPad
		
		
	}
	
	public boolean getShowErrors(){
		return box.isSelected();
	}
	
	public boolean hideExper(){
		return this.regular.isSelected();
	}
	
	public void setCounter(int count){
		counter.setText("" + count);
		this.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (panel == null){
			return;
		}
		
		if (e.getSource() instanceof JButton){
			JButton b = (JButton)e.getSource();
			if (b.getText().equals("Play")){
				this.timer = new Timer();
				int time = 1000 / Math.max(Integer.parseInt(field.getText()), 1);
				System.out.println("Time : " + time);
				timer.scheduleAtFixedRate(new TraceTimer(visual), 0, time);
				b.setText("Stop");
			}else if (b.getText().equals("Stop")){
				timer.cancel();
				b.setText("Play");
			}else if (b.getText().equals("Reset")){
				timer.cancel();
				this.visual.resetStep();
				button.setText("Play");
			}
			
			
		}
		
		panel.repaint();
	}
}
