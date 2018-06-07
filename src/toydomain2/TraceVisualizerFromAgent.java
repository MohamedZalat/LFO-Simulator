/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain2;

import javax.swing.JFrame;
import lfo.agents.Agent;
import lfo.simulator.Action;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.gui.TraceVisualizer;
import lfo.simulator.perception.Perception;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author santi
 */
public class TraceVisualizerFromAgent {

    public static void main(String []args) throws Exception {
        Agent a = null;
//        a = new ToyRandomAgent();
        a = new Toy2StraightLineAgent();
//        a = new Toy2StraightRandomAgent();
//        a = new Toy2FixedSequenceAgent();

        Perception p = new Toy2Perception();

        State s = new State(new SAXBuilder().build("maps/toy2-3x2.xml").getRootElement());
        Trace t = generateTrace(s, a, 100, p);
        TraceVisualizer ad = new TraceVisualizer(t, 800, 600, 1, "");
        JFrame frame = new JFrame("Visualizing Agent " + a.getClass().getSimpleName());
        frame.getContentPane().add(ad);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static Trace generateTrace(State s, Agent agent, int maxCycles, Perception perception) throws Exception {
        State state = (State)s.clone();
        int vacuumID = state.get(Toy2VacuumCleaner.class).getID();
        Trace t = new Trace(state,vacuumID);

        boolean first = true;

        for(int time = 0;time<maxCycles;time++) {
            Perception p = perception.perceive(state, state.get(vacuumID));
            Action a = agent.cycle(vacuumID, p, 1);
            if (a!=null) {
                a.setObjectID(vacuumID);
            }
            t.addEntry(new TraceEntry(time, (State)state.clone(), vacuumID, a));
            state.cycle(a, 1.0);
//            System.out.println("cycle " + time);

        }

        return t;
    }    
    
}
