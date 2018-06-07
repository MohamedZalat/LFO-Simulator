/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import lfo.agents.Agent;
import lfo.agents.LFOAgent;
import lfo.agents.force.ForceSmartStraightLineAgent;
import lfo.agents.force.ForceSmarterStraightLineAgent;
import lfo.agents.force.ForceStraightLineAgent;
import lfo.agents.matlab.DiscreteNNetAgent;
import lfo.agents.matlab.ForceBNetAgent;
import lfo.agents.matlab.ForceNNetAgent;
import lfo.learning.level2.WekaContinuousLearner;
import lfo.learning.level3.Level2WrapperWithoutActions;
import lfo.simulator.LearningTrace;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.gui.TraceVisualizer;
import lfo.simulator.perception.ForceFourRayDistancePerception;
import lfo.simulator.perception.Perception;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author santi
 */
public class TraceVisualizerFromForceAgent {

    public static void main(String []args) throws Exception {
        Agent a = null;
//        a = new ForceStraightLineAgent();
//        a = new ForceSmartStraightLineAgent();
//        a = new ForceSmarterStraightLineAgent();
//        a = learnForceNNetAgent("ForceStraightLineAgent",10);
//        a = learnForceBNetAgent("ForceStraightLineAgent",10);
        a = learnForceWekaAgent("ForceStraightLineAgent");
//        a = learnForceWekaAgent("ForceSmartStraightLineAgent");
//        a = learnForceWekaAgent("ForceSmarterStraightLineAgent");
/*
        {
            List<String> traces = new LinkedList<String>();
            traces.add("traces-forcefourraydistance/trace-m0-ForceStraightLineAgent.txt");
            traces.add("traces-forcefourraydistance/trace-m1-ForceStraightLineAgent.txt");
            traces.add("traces-forcefourraydistance/trace-m2-ForceStraightLineAgent.txt");
            traces.add("traces-forcefourraydistance/trace-m3-ForceStraightLineAgent.txt");
            traces.add("traces-forcefourraydistance/trace-m4-ForceStraightLineAgent.txt");
            traces.add("traces-forcefourraydistance/trace-m5-ForceStraightLineAgent.txt");
            traces.add("traces-forcefourraydistance/trace-m6-ForceStraightLineAgent.txt");
            a = new ForceNNetAgent(traces,10);
        }
    */
        
        Perception p = new ForceFourRayDistancePerception(0.1);
//        State s = new State(new SAXBuilder().build("maps/force-8x8-4.xml").getRootElement());
        State s = new State(new SAXBuilder().build("maps/force-32x32.xml").getRootElement());
//        State s = new State(new SAXBuilder().build("maps/force-32x32-2.xml").getRootElement());
        Trace t = GenerateContinuousTraces.generateContinuousTrace(s, a, 5000, 0.1, p, 1);
        TraceVisualizer ad = new TraceVisualizer(t, 800, 600, 1, "");
        JFrame frame = new JFrame("Visualizing Agent " + a.getClass().getSimpleName());
        frame.getContentPane().add(ad);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
    
    
    public static Agent learnForceNNetAgent(String expert, int XSIZE) {
        List<String> traces = new LinkedList<String>();
        traces.add("traces-forcefourraydistance/trace-m0-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m1-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m2-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m3-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m4-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m5-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m6-" + expert + ".txt");
        return  new ForceNNetAgent(traces,10);
    }

    public static Agent learnForceBNetAgent(String expert, int XSIZE) {
        List<String> traces = new LinkedList<String>();
        traces.add("traces-forcefourraydistance/trace-m0-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m1-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m2-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m3-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m4-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m5-" + expert + ".txt");
        traces.add("traces-forcefourraydistance/trace-m6-" + expert + ".txt");
        return new ForceBNetAgent(traces,10);
    }

    public static Agent learnForceWekaAgent(String expert) throws Exception {
        List<LearningTrace> traces = new LinkedList<LearningTrace>();
        Perception p = new ForceFourRayDistancePerception(0.1);
        traces.add(new LearningTrace(new Trace(new SAXBuilder().build("traces-forcefourraydistance/trace-m0-" + expert + ".xml").getRootElement()), p));
        traces.add(new LearningTrace(new Trace(new SAXBuilder().build("traces-forcefourraydistance/trace-m1-" + expert + ".xml").getRootElement()), p));
        traces.add(new LearningTrace(new Trace(new SAXBuilder().build("traces-forcefourraydistance/trace-m2-" + expert + ".xml").getRootElement()), p));
        traces.add(new LearningTrace(new Trace(new SAXBuilder().build("traces-forcefourraydistance/trace-m3-" + expert + ".xml").getRootElement()), p));
        traces.add(new LearningTrace(new Trace(new SAXBuilder().build("traces-forcefourraydistance/trace-m4-" + expert + ".xml").getRootElement()), p));
//        traces.add(new LearningTrace(new Trace(new SAXBuilder().build("traces-forcefourraydistance/trace-m5-" + expert + ".xml").getRootElement()), p));
        traces.add(new LearningTrace(new Trace(new SAXBuilder().build("traces-forcefourraydistance/trace-m6-" + expert + ".xml").getRootElement()), p));
//        return new LFOAgent(new WekaContinuousLearner(),traces);
        return new LFOAgent(new Level2WrapperWithoutActions(2,new WekaContinuousLearner()),traces);
//        return new LFOAgent(new Level2WrapperWithoutActions(3,new WekaContinuousLearner()),traces);
//        return new LFOAgent(new Level2WrapperWithoutActions(5,new WekaContinuousLearner()),traces);
    }

}
