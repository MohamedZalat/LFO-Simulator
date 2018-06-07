/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import lfo.agents.Agent;
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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import lfo.agents.turnable.TurnableSmartStraightLineAgent;
import lfo.agents.turnable.TurnableStraightLineAgent;
import lfo.simulator.objects.VacuumCleanerTurnable;
import lfo.simulator.perception.TurnableFourRayDistancePerception;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author santi
 */
public class TraceVisualizerFromTurnableAgent {

    public static void main(String []args) throws Exception {
//        Agent a = new ContinuousStraightLineAgent();
        Agent a = new TurnableSmartStraightLineAgent();
        Perception p = new TurnableFourRayDistancePerception(0.1,0.1);
//        State s = new State(new SAXBuilder().build("maps/turnable-8x8.xml").getRootElement());
        State s = new State(new SAXBuilder().build("maps/turnable-8x8-4.xml").getRootElement());
        Trace t = GenerateContinuousTraces.generateContinuousTrace(s, a, 1000, 0.1, p, 1);
        TraceVisualizer ad = new TraceVisualizer(t, 800, 600, 1, "");
        JFrame frame = new JFrame("Visualizing Agent " + a.getClass().getSimpleName());
        frame.getContentPane().add(ad);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
