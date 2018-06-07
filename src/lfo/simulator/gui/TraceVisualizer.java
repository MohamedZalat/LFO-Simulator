/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.simulator.gui;

import lfo.simulator.Action;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.objects.VacuumCleaner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class TraceVisualizer extends JPanel implements ListSelectionListener{
    int current_step = 0;
    Trace trace = null;

    JPanel statePanel = null;
    JList Selector = null;
    JList<String> agentSelector = null;
    List<State> states = new LinkedList<State>();
    List<Point2D.Double> trajectory = new LinkedList<Point2D.Double>();
    
    private SettingsPanel panel = null;

    public static JFrame newWindow(String name,int dx,int dy,Trace t, int subjectID, String agentFile) throws Exception {
        TraceVisualizer ad = new TraceVisualizer(t, dx, dy, subjectID, agentFile);
        JFrame frame = new JFrame(name);
        frame.getContentPane().add(ad);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        return frame;
    }


    public TraceVisualizer(Trace t, int dx, int dy, int subject, String agentFile) throws Exception {
        current_step = 0;
        trace = t;

        // run the trace and get the states:
        // this method assumes that the interval among entries is always the same:
        {
//            State current = (State) t.getState().clone();
//            states.add((State)current.clone());
            for(TraceEntry te:trace.getEntries()) {
                states.add((State)te.state.clone());
                trajectory.add(te.state.get(te.subject).getPosition());
            }
        }
        if (agentFile.equals("")){
        	setPreferredSize(new Dimension(dx,dy));
        }else{
        	setPreferredSize(new Dimension(dx + 150,dy));
        }
        
        setSize(dx,dy);

        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
          System.out.println("Error setting native LAF: " + e);
        }

        setBackground(Color.WHITE);

        removeAll();
        setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

        statePanel = new TrajectoryStatePanel(t.getState(), trajectory, 0, 1000);
        statePanel.setPreferredSize(new Dimension((int)(dx*0.6),dy));
        add(statePanel);

        String []actionList = new String [t.getEntries().size()];
        Selector = new JList ();
        JScrollPane ListScrollPane = new JScrollPane(Selector);

        for(int i = 0;i<t.getEntries().size();i++) {
            if (t.getEntries().get(i).action!=null) {
                actionList[i] = t.getEntries().get(i).action.toSimpleString();
            } else {
                actionList[i] = "-";
            }
        }

        
//        Selector.setPreferredSize(new Dimension(100,dy*2));
//        ListScrollPane.setPreferredSize(new Dimension(100,dy*2));

        add(ListScrollPane);
        
        agentSelector = new JList<String>();
        JScrollPane agentScrollPane = new JScrollPane(agentSelector);
        if (agentFile.equals("")){
        	Selector.setListData(actionList);
            Selector.addListSelectionListener (this);
            Selector.setSelectedIndex (0);
            Selector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        	return;
        }
        
        BufferedReader r = new BufferedReader(new FileReader(agentFile));
        String line = r.readLine();
        String []resultList = new String[actionList.length];
        int i = 0;
        while(line != null){
        	String s[] = line.split(" ");
        	resultList[i] = s[s.length - 1];
        	i++;
        	line = r.readLine();
        }
        ((TrajectoryStatePanel)statePanel).addResultActions(resultList);
        
        agentScrollPane.getVerticalScrollBar().setModel(ListScrollPane.getVerticalScrollBar().getModel());
        
        Selector.setListData(actionList);
        Selector.addListSelectionListener (this);
        Selector.setSelectedIndex (0);
        Selector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        agentSelector.setListData(resultList);
        agentSelector.addListSelectionListener (this);
        agentSelector.setSelectedIndex (0);
        agentSelector.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        panel = new SettingsPanel((TrajectoryStatePanel) statePanel, this);
        ((TrajectoryStatePanel) statePanel).setSettingsPanel(panel);
        
        add(agentScrollPane);
        add(panel);
    }
    
    public void increaseStep(){
    	int selection  = Selector.getSelectedIndex();
    	
    	Selector.setSelectedIndex(selection + 1); 
    }

    public void resetStep(){
    	Selector.setSelectedIndex(0);
    	this.repaint();
    }
    
  public void valueChanged(ListSelectionEvent e) {
	  int selection = -1;
	  if (e.getSource().equals(Selector)){
		  selection = Selector.getSelectedIndex();
	  }else{
		  selection = agentSelector.getSelectedIndex();
	  }

    ((StatePanel)statePanel).setState(states.get(selection));
    ((TrajectoryStatePanel)statePanel).setLast(selection);
    
    Selector.setSelectedIndex(selection);
    agentSelector.setSelectedIndex(selection);
    
    this.repaint();
  }

  public static void main(String []args) throws JDOMException, IOException, Exception {
	  Trace Ltrace = new Trace(new SAXBuilder().build("traces-fourraydistance/trace-m1-DirtWallAgent.xml").getRootElement());
	  //Trace t = new Trace(new SAXBuilder().build("traces/newTrace.xml").getRootElement());
      JFrame tv1 = newWindow("Learner Demo", 800, 600, Ltrace, 1, "traces-fourraydistance/trace-m1-DirtWallAgent.txt");
      tv1.show();
 
    
  }
}
