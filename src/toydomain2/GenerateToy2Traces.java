/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain2;

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
public class GenerateToy2Traces {
    public static void main(String []args) throws Exception {
        int mapN = 0,agentN;
        List<State> maps = new LinkedList<State>();
        maps.add(new State(new SAXBuilder().build("maps/toy2-3x2.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/toy2-3x4.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/toy2-6x4.xml").getRootElement()));

        for(State map:maps) {
            List<Agent> agents = new LinkedList<Agent>();
            // Level 1:
            agents.add(new Toy2FixedSequenceAgent());
            agents.add(new Toy2RandomAgent());

            // Level 2:
            // ...
            
            // Level 3:
            agents.add(new Toy2Circler());
            agents.add(new Toy2StraightLineAgent());
            agents.add(new Toy2StraightRandomAgent());


            // Level 4: (using different perceptions for expert and learner)
            // ...
            
            Perception perception = new Toy2Perception();

            agentN = 0;
            int length = 1000;
            for(Agent agent:agents) {
                agent.start();
                String fileName = "traces-toy2/trace-a-" + length + "-m" + mapN + "-" + agent.name();
                Trace t = generateTrace(map,agent,length,perception);
                FileWriter fw = new FileWriter(fileName + ".xml");
                t.toxml(new XMLWriter(fw));
                fw.close();               
                MatlabToy2TraceTranslator.translateToMatlab(t, perception, fileName+ ".txt");

                agent.start();
                fileName = "traces-toy2/trace-b-" + length + "-m" + mapN + "-" + agent.name();
                t = generateTrace(map,agent,length,perception);
                fw = new FileWriter(fileName + ".xml");
                t.toxml(new XMLWriter(fw));
                fw.close();               
                MatlabToy2TraceTranslator.translateToMatlab(t, perception, fileName+ ".txt");

                
                agentN++;
            }
            mapN++;
        }
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
