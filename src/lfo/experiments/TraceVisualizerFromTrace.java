/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import lfo.agents.Agent;
import lfo.agents.matlab.DiscreteDBNAgent;
import lfo.agents.LFOAgent;
import lfo.agents.discrete.SmartRandomAgent;
import lfo.agents.discrete.ZigZagAgent;
import lfo.experiments.GenerateDiscreteTraces;
import lfo.learning.LFO;
import lfo.learning.level2.KNN;
import lfo.learning.level3.Level2Wrapper;
import lfo.simulator.LearningTrace;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import lfo.simulator.gui.TraceVisualizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import lfo.agents.discrete.*;
import lfo.agents.matlab.DiscreteNNetAgent;
import lfo.simulator.objects.VacuumCleaner;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author santi
 */
public class TraceVisualizerFromTrace {

	private static Trace t;
	private static TraceVisualizer ad;
	private static JFrame frame;
	
    public static void main(String []args) throws Exception {

//        Trace t = new Trace(new SAXBuilder().build("traces-fourraydistance/trace-m2-SmartWallFollowerAgent.xml").getRootElement());
//        Trace t = new Trace(new SAXBuilder().build("LFO-traces-fourraydistance/nnetk2/trace-m0-RandomAgent.xml").getRootElement());
//        Trace t = new Trace(new SAXBuilder().build("LFO-traces-fourraydistance/nnetk2/trace-m2-ZigZagAgent.xml").getRootElement());
//        Trace t = new Trace(new SAXBuilder().build("LFO-traces-fourraydistance/bnetk2/trace-m1-ZigZagAgent.xml").getRootElement());
//        Trace t = new Trace(new SAXBuilder().build("LFO-traces-fourraydistance/lfodbn/trace-m3-FixedSequenceAgent.xml").getRootElement());
    	String xmlFile = "zz/trace-m0-ZigZagAgent.xml";
        String txtFile = "PredictedTraceFile - ZigZagAgent - CBR,TB,none,none,kunordered,none - m0.txt";
    	t = new Trace(new SAXBuilder().build(System.getProperty("user.dir") + "/../jLOAF-VacuumCleaner/Traces/" + xmlFile).getRootElement());
        ad = new TraceVisualizer(t, 800, 600, 1, System.getProperty("user.dir") + "/../jLOAF-VacuumCleaner/Statistics/" + txtFile);
        frame = new JFrame("Visualizing Trace");
        
        JMenuBar bar = new JMenuBar();
        frame.setJMenuBar(bar);
        
        JMenu file = new JMenu("File");
        bar.add(file);
        
        final JMenuItem setExpert = new JMenuItem("Expert File");
        final JMenuItem setAgent = new JMenuItem("Agent File");
        final JMenuItem refresh = new JMenuItem("Refresh");
        
        setExpert.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(setExpert);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File s = chooser.getSelectedFile();
					try {
						t = new Trace(new SAXBuilder().build(s).getRootElement());
					} catch (JDOMException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
        });
        
        setAgent.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT", "txt");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(setExpert);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					String s = chooser.getSelectedFile().getAbsolutePath();
					try {
						ad = new TraceVisualizer(t, 800, 600, 1, s);
					} catch (JDOMException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
        });
        
        refresh.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(ad);
				frame.pack();
			}
        });
        
        file.add(setExpert);
        file.add(setAgent);
        file.add(refresh);
        
        frame.getContentPane().add(ad);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
