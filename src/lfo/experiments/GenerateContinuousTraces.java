/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import lfo.agents.Agent;
import lfo.agents.discrete.FixedSequenceAgent;
import lfo.agents.discrete.PauseEveryN;
import lfo.agents.discrete.RandomAgent;
import lfo.agents.discrete.RandomExplorerAgent;
import lfo.agents.discrete.SmartRandomAgent;
import lfo.agents.discrete.SmartRandomExplorerAgent;
import lfo.agents.discrete.SmartStraightLineAgent;
import lfo.agents.discrete.StraightLineAgent;
import lfo.agents.discrete.ZigZagAgent;
import lfo.simulator.Action;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.objects.VacuumCleaner;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import util.XMLWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import lfo.agents.force.ForceSmartStraightLineAgent;
import lfo.agents.force.ForceSmarterStraightLineAgent;
import lfo.agents.force.ForceStraightLineAgent;
import lfo.matlab.MatlabTraceTranslator;
import lfo.simulator.objects.VacuumCleanerTurnable;
import lfo.simulator.perception.ForceFourRayDistancePerception;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class GenerateContinuousTraces {
    public static void main(String []args) throws Exception {
        int mapN = 0,agentN;
        List<State> maps = new LinkedList<State>();
        maps.add(new State(new SAXBuilder().build("maps/force-8x8.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-8x8-2.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-8x8-3.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-8x8-4.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-8x8-5.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-32x32.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-32x32-2.xml").getRootElement()));

        for(State map:maps) {
            List<Agent> agents = new LinkedList<Agent>();

            // Level 2:
            agents.add(new ForceStraightLineAgent());

            // Level 3 
            agents.add(new ForceSmartStraightLineAgent());
            agents.add(new ForceSmarterStraightLineAgent());
 
            // Level 4: (using different perceptions for expert and learner)
            // ...
            
//            Perception perception = new WindowPerception();
            Perception perception = new ForceFourRayDistancePerception(0.1);

            agentN = 0;
            for(Agent agent:agents) {
                String fileName = "traces-forcefourraydistance/trace-m" + mapN + "-" + agent.name();

                // This generates a trace, while saving it to Matlab format:
                Trace t = generateContinuousTrace(map,agent,2000,0.1,perception,1);
                FileWriter fw = new FileWriter(fileName + ".xml");
                t.toxml(new XMLWriter(fw));
                fw.close();
                
                MatlabTraceTranslator.translateToMatlab(t, perception, fileName+ ".txt");
//                MatlabTraceTranslator.translateToMatlabForNNet(t, perception, fileName+ "-nnet.txt");

//                JFrame w = TraceVisualizer.newWindow(agent.getClass().getSimpleName(), 800, 600, t);
//                w.setVisible(true);

                agentN++;
            }
            mapN++;
        }
    }
    
    
    public static Trace generateContinuousTrace(State s, Agent agent, int maxCycles, double step, Perception perception, int vacuumID) throws Exception {
        State state = (State)s.clone();
        Trace t = new Trace(state,vacuumID);
        double time = 0;

        boolean first = true;

        for(int cycle = 0;cycle<maxCycles;cycle++) {
            Perception p = perception.perceive(state, state.get(vacuumID));
            Action a = agent.cycle(vacuumID, p, step);
            if (a!=null) a.setObjectID(vacuumID);
            t.addEntry(new TraceEntry(time, (State)state.clone(), vacuumID, a));
            state.cycle(a, step);
            time += step;
//            System.out.println("cycle " + time);
        }

        return t;
    }


}
