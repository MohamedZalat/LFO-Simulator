/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import lfo.agents.matlab.DiscreteDBNAgent;
import lfo.evaluation.HMMDistanceGivenTraces;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import java.util.LinkedList;
import java.util.List;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class AutomaticKLOutputEvaluatorDBN {
    public static void main(String []args) throws Exception {
        int LEVEL = 3;

        int mapN = 0;
        List<State> maps = new LinkedList<State>();
        maps.add(new State(new SAXBuilder().build("maps/discreet-8x8.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/discreet-8x8-2.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/discreet-8x8-3.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/discreet-8x8-4.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/discreet-8x8-5.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/discreet-32x32.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/discreet-32x32-2.xml").getRootElement()));
//        Perception perception = new WindowPerception();
        Perception perception = new FourRayDistancePerception();

        List<List<Trace>> traces = new LinkedList<List<Trace>>();

        // load all the learning traces:
        {
            String experts[] = {"FixedSequenceAgent", "RandomAgent","SmartRandomAgent", "StraightLineAgent",
                                "SmartStraightLineAgent", "ZigZagAgent", "RandomExplorerAgent", "SmartRandomExplorerAgent",
                                "PauseEvery3SmartRandomAgent", "PauseEvery3SmartStraightLineAgent"};
            for(int i = 0;i<experts.length;i++) {
                List<Trace> tmp = new LinkedList<Trace>();
                for(int j = 0;j<7;j++) {
                    System.out.println("Loading " + i + " " + j);
                    String fileName = "traces\\trace-m" + j + "-" + experts[i] + ".xml";
                    tmp.add(new Trace(new SAXBuilder().build(fileName).getRootElement()));
                }
                traces.add(tmp);
            }
        }

//        int i = 2;
        for(int i = 0;i<10;i++)
        {
            mapN = 0;
//            State map = maps.get(0);
            List<Trace> agent_traces = new LinkedList<Trace>();
            for(State map:maps)
            {
                DiscreteDBNAgent agent = null;
                System.out.println("Loading agent...");
                switch(i) {
//                    case 0: agent = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-FixedSequenceAgent-" + (mapN+1) + ".txt"); break;
//                    case 1: agent = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-RandomAgent-" + (mapN+1) + ".txt"); break;
                    case 2: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-SmartRandomAgent-" + (mapN+1) + ".txt"); break;
                    case 3: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-StraightLineAgent-" + (mapN+1) + ".txt"); break;
                    case 4: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-SmartStraightLineAgent-" + (mapN+1) + ".txt"); break;
                    case 5: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-ZigZagAgent-" + (mapN+1) + ".txt"); break;
                    case 6: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-RandomExplorerAgent-" + (mapN+1) + ".txt"); break;
                    case 7: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-SmartRandomExplorerAgent-" + (mapN+1) + ".txt"); break;
                    case 8: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-PauseEvery3SmartRandomAgent-" + (mapN+1) + ".txt"); break;
                    case 9: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l" + LEVEL + "-PauseEvery3SmartStraightLineAgent-" + (mapN+1) + ".txt"); break;
                }
                if (agent!=null) {
                    System.out.println("Generating trace... " + i);
                    agent.start();
                    Trace t = GenerateDiscreteTraces.generateTrace(map,agent,1000,perception);
                    agent_traces.add(t);
//                    TraceVisualizer.newWindow("-", 800, 600, t).show();
                }
                mapN++;
            }

            double distance = HMMDistanceGivenTraces.distance(traces.get(i), agent_traces, 4, 10, false);
            System.out.println("Agent " + i + ": " + distance);
        }
    }
}
