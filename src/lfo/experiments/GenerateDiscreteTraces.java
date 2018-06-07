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
import lfo.agents.discrete.*;
import lfo.matlab.MatlabTraceTranslator;
import lfo.simulator.objects.VacuumCleanerTurnable;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class GenerateDiscreteTraces {
    public static void main(String []args) throws Exception {
        int mapN = 0,agentN;
        List<State> maps = new LinkedList<State>();
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-empty4.xml").getRootElement()));
//        
        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-2.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-3.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-4.xml").getRootElement()));       
       maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-5.xml").getRootElement()));
      maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-32x32.xml").getRootElement()));
      maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-32x32-2.xml").getRootElement()));
//       maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-10x14-2.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-12x18-empty4.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-32x32-2.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-8x10-empty3.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-8x16-empty3.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-32x32.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-4.xml").getRootElement())); 

        for(State map:maps) {
            List<Agent> agents = new LinkedList<Agent>();
            // Level 1:
            //agents.add(new FixedSequenceAgent());
            //agents.add(new RandomAgent());

            // Level 2:
           // agents.add(new SmartRandomAgent());
         //   agents.add(new WallFollowerAgent());
          //  agents.add(new SmartWallFollowerAgent());

            // Level 3 
            //agents.add(new FrequencyAgent2());
            agents.add(new ZigZagAgent());
        //    agents.add(new DirtWallAgent());
          //  agents.add(new StraightLineAgent());
            //agents.add(new SmartStraightLineAgent());  

            // Level 3 
//            agents.add(new PauseEveryN(3,new SmartRandomAgent()));
//            agents.add(new PauseEveryN(3,new SmartStraightLineAgent()));

            // Level 3 
//            agents.add(new RandomExplorerAgent());
            //agents.add(new SmartRandomExplorerAgent());

            // Level 4: (using different perceptions for expert and learner)
            // ...
            
//            Perception perception = new WindowPerception();
            Perception perception = new FourRayDistancePerception();

            agentN = 0;
            for(Agent agent:agents) {
                String fileName = Config.LOCAL_DATA + "zigzag/trace-m" + mapN + "-" + agent.name();

                // This generates a trace, while saving it to Matlab format:
                Trace t = generateTrace(map,agent,200,perception);
               // FileWriter fw = new FileWriter(fileName + ".xml");
               // t.toxml(new XMLWriter(fw));
               // fw.close();
                
                MatlabTraceTranslator.translateToMatlab(t, perception, fileName+ ".txt");
               // MatlabTraceTranslator.translateToMatlabForNNet(t, perception, fileName+ "-nnet.txt");

//                JFrame w = TraceVisualizer.newWindow(agent.getClass().getSimpleName(), 800, 600, t);
//                w.setVisible(true);

                agentN++;
            }
            mapN++;
        }
    }

    public static Trace generateTrace(State s, Agent agent, int maxCycles, Perception perception) throws Exception {
        State state = (State)s.clone();
        int vacuumID = state.get(VacuumCleaner.class).getID();
        Trace t = new Trace(state,vacuumID);

        boolean first = true;

        for(int time = 0;time<maxCycles;time++) {
            Perception p = perception.perceive(state, state.get(vacuumID));
            Action a = agent.cycle(vacuumID, p, time);
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
