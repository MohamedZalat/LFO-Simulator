/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import lfo.agents.Agent;
import lfo.simulator.Action;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.perception.Perception;
import org.jdom.input.SAXBuilder;
import util.XMLWriter;

/**
 *
 * @author santi
 */
public class GenerateToyTraces {
    public static void main(String []args) throws Exception {
        int mapN = 0,agentN;
        List<State> maps = new LinkedList<State>();
        maps.add(new State(new SAXBuilder().build("maps/toy-3x1.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build("maps/toy-6x1.xml").getRootElement()));

        for(State map:maps) {
            List<Agent> agents = new LinkedList<Agent>();
            // Level 1:
            agents.add(new ToyFixedSequenceAgent());
            agents.add(new ToyRandomAgent());

            // Level 2:
            agents.add(new ToyLevel2AgentA());
            agents.add(new ToyLevel2AgentB());
            agents.add(new ToyLevel2AgentAStochastic());
            agents.add(new ToyLevel2AgentBStochastic());
            
            // Level 3:
            agents.add(new ToyStraightLineAgent());
            agents.add(new ToyStraightRandomAgent());
            agents.add(new ToyInternalStateAgent());


            // Level 4: (using different perceptions for expert and learner)
            // ...
            
            Perception perception = new ToyPerception();

            agentN = 0;
            int length = 1000;
            for(Agent agent:agents) {
                String fileName = "traces-toy2/trace-" + length + "-m" + mapN + "-" + agent.name();

                // This generates a trace, while saving it to Matlab format:
                Trace t = generateTrace(map,agent,length,perception);
                FileWriter fw = new FileWriter(fileName + ".xml");
                t.toxml(new XMLWriter(fw));
                fw.close();
                
                MatlabToyTraceTranslator.translateToMatlab(t, perception, fileName+ ".txt");
                agentN++;
            }
            mapN++;
        }
    }

    public static Trace generateTrace(State s, Agent agent, int maxCycles, Perception perception) throws Exception {
        State state = (State)s.clone();
        int vacuumID = state.get(ToyVacuumCleaner.class).getID();
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
