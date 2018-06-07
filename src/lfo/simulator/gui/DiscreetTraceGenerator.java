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
import lfo.simulator.perception.WindowPerception;
import util.XMLWriter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class DiscreetTraceGenerator extends JFrame {
    public int WINDOW_WIDTH = 1024;
    public int WINDOW_HEIGHT = 768;

    double time = 0;
    double timestep = 0.10;
    Trace trace;
    State state;
    StatePanel statePanel;

    public static void main(String []args) throws Exception {
        //Document doc = new SAXBuilder().build("maps/discreet-32x32-2.xml");
//        Document doc = new SAXBuilder().build("maps/circle-8x8.xml");
        Document doc = new SAXBuilder().build("maps/discreet-8x8.xml");
//        Document doc = new SAXBuilder().build("maps/discreet-8x8-5.xml");
        State s = new State(doc.getRootElement());

        new DiscreetTraceGenerator(s);
    }


    public void submitAction(Action a) throws Exception {
        TraceEntry te = new TraceEntry(time, (State)state.clone(), trace.getSubject(), a);
        state.cycle(a, timestep);
        trace.addEntry(te);
        time+=timestep;
        invalidate();
        repaint();
    }

    public DiscreetTraceGenerator(State s) {
		super("LFO TraceGenerator");

        state = s;
        trace = new Trace(state,1);

		setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
		setSize(WINDOW_WIDTH,WINDOW_HEIGHT);

        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
          System.out.println("Error setting native LAF: " + e);
        }

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);

        setBackground(Color.WHITE);

        getContentPane().removeAll();
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1,BoxLayout.X_AXIS));

        statePanel = new StatePanel(state);
        panel1.add(statePanel);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2,BoxLayout.Y_AXIS));

        // load/save buttons:
        {
            JButton button = new JButton("Load State");
            button.addActionListener(new TGFileLoadListener(this));
            panel2.add(button);
            button = new JButton("Save State");
            button.addActionListener(new TGFileSaveListener(this));
            panel2.add(button);
            button = new JButton("Save Trace");
            button.addActionListener(new TGFileSaveTraceListener(this));
            panel2.add(button);
        }

        List<Action> actions = state.get(1).executableActions();
        actions.add(null);
        ActionsPanel ap = new ActionsPanel(actions, this);
        panel2.add(ap);

        panel1.add(panel2);

        getContentPane().add(panel1);
        pack();
    }


    public String loadFile(Frame f, String title, String defDir, String fileType) {
        FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
        fd.setFile(fileType);
        fd.setDirectory(defDir);
        fd.setLocation(50, 50);
        fd.show();
        
        return fd.getDirectory() + fd.getFile();
    }


    public String saveFile(Frame f, String title, String defDir, String fileType) {
        FileDialog fd = new FileDialog(f, title, FileDialog.SAVE);
        fd.setFile(fileType);
        fd.setDirectory(defDir);
        fd.setLocation(50, 50);
        fd.show();

        return fd.getDirectory() + fd.getFile();
    }


    public class TGFileLoadListener implements ActionListener {
        DiscreetTraceGenerator traceGenerator;

        TGFileLoadListener(DiscreetTraceGenerator tg) {
            traceGenerator = tg;
        }

        public void actionPerformed(ActionEvent e) {
            String fileName = loadFile(new Frame(), "Open...", ".", "*.xml");
            Document doc;
            try {
                doc = new SAXBuilder().build(fileName);
                traceGenerator.state = new State(doc.getRootElement());
                traceGenerator.statePanel.setState(traceGenerator.state);
                traceGenerator.trace = new Trace(traceGenerator.state,1);
                traceGenerator.invalidate();
                traceGenerator.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Load a state from " + fileName);
        }
    }


    public class TGFileSaveListener implements ActionListener {
        DiscreetTraceGenerator traceGenerator;

        TGFileSaveListener(DiscreetTraceGenerator tg) {
            traceGenerator = tg;
        }

        public void actionPerformed(ActionEvent e) {
            String fileName = saveFile(new Frame(), "Save...", ".", "*.xml");
            try {
                FileWriter fw = new FileWriter(fileName);
                traceGenerator.state.toxml(new XMLWriter(fw));
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public class TGFileSaveTraceListener implements ActionListener {
        DiscreetTraceGenerator traceGenerator;

        TGFileSaveTraceListener(DiscreetTraceGenerator tg) {
            traceGenerator = tg;
        }

        public void actionPerformed(ActionEvent e) {
            String fileName = saveFile(new Frame(), "Save...", ".", "*.xml");
            try {
                FileWriter fw = new FileWriter(fileName);
                traceGenerator.trace.toxml(new XMLWriter(fw));
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
