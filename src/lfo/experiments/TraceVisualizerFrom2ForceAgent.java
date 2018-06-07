/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import lfo.agents.Agent;
import lfo.agents.force.ForceSmartStraightLineAgent;
import lfo.agents.force.ForceSmarterStraightLineAgent;
import lfo.simulator.Action;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.gui.TraceVisualizer;
import lfo.simulator.perception.ForceFourRayDistancePerception;
import lfo.simulator.perception.Perception;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author santi
 */
public class TraceVisualizerFrom2ForceAgent {

    public static void main(String []args) throws Exception {
//        Agent a = new ForceStraightLineAgent();
//        Agent a = new ForceSmartStraightLineAgent();
        Agent a1 = new ForceSmartStraightLineAgent();
        Agent a2 = new ForceSmartStraightLineAgent();
        Perception p = new ForceFourRayDistancePerception(0.1);
        State s = new State(new SAXBuilder().build("maps/force-32x32-2vacuums.xml").getRootElement());
        Trace t = generateContinuousTrace(s, a1, a2, 2000, 0.1, p, 1, 2);
        TraceVisualizer ad = new TraceVisualizer(t, 800, 600, 1, "");
        JFrame frame = new JFrame("Visualizing Agent " + a1.getClass().getSimpleName());
        frame.getContentPane().add(ad);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public static Trace generateContinuousTrace(State s, Agent agent1, Agent agent2, int maxCycles, double step, Perception perception, int vacuumID1, int vacuumID2) throws Exception {
        State state = (State)s.clone();
        Trace t = new Trace(state,vacuumID1);
        double time = 0;

        boolean first = true;

        for(int cycle = 0;cycle<maxCycles;cycle++) {
            Perception p1 = perception.perceive(state, state.get(vacuumID1));
            Perception p2 = perception.perceive(state, state.get(vacuumID2));
            Action a1 = agent1.cycle(vacuumID1, p1, step);
            Action a2 = agent2.cycle(vacuumID2, p2, step);
            if (a1!=null) a1.setObjectID(vacuumID1);
            if (a2!=null) a2.setObjectID(vacuumID2);
            t.addEntry(new TraceEntry(time, (State)state.clone(), vacuumID1, a1));
            List<Action> la = new LinkedList<Action>();
            la.add(a1);
            la.add(a2);
            state.cycle(la, step);
            time += step;
//            System.out.println("cycle " + time);
        }

        return t;
    }
    
    
}
