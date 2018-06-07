/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import lfo.agents.Agent;
import lfo.agents.matlab.DiscreteDBNAgent;
import lfo.agents.discrete.FixedSequenceAgent;
import lfo.agents.LFOAgent;
import lfo.agents.discrete.PauseEveryN;
import lfo.agents.discrete.RandomAgent;
import lfo.agents.discrete.RandomExplorerAgent;
import lfo.agents.discrete.SmartRandomAgent;
import lfo.agents.discrete.SmartRandomExplorerAgent;
import lfo.agents.discrete.SmartStraightLineAgent;
import lfo.agents.discrete.StraightLineAgent;
import lfo.agents.discrete.ZigZagAgent;
import lfo.learning.LFO;
import lfo.learning.level1.Memorize;
import lfo.learning.level2.KNN;
import lfo.learning.level3.Level2Wrapper;
import lfo.simulator.Action;
import lfo.simulator.LearningTrace;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.objects.Dirt;
import lfo.simulator.objects.VacuumCleaner;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import lfo.simulator.perception.WindowPerception;
import lfo.simulator.gui.TraceVisualizer;
import util.XMLWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class AutomaticPerformanceEvaluatorDBN {
    public static void main(String []args) throws Exception {
        int REPEATS = 10;
        int mapN = 0,agentN = 0;
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

        List<Agent> agents = new LinkedList<Agent>();

        agentN = 2;
//        int i = 2;
        for(int i = 0;i<10;i++)
        {
            mapN = 0;
            double avg_initialDirts = 0;
            double avg_minimumDirts = 0;
            double avg_timeAchieved = 0;
            double avg_predicted = 0;
//            State map = maps.get(0);
            for(State map:maps)
            {
                DiscreteDBNAgent agent = null;
                System.out.println("Loading agent...");
                switch(i) {
                    case 0: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-FixedSequenceAgent-" + (mapN+1) + ".txt"); break;
                    case 1: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-RandomAgent-" + (mapN+1) + ".txt"); break;
                    case 2: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-SmartRandomAgent-" + (mapN+1) + ".txt"); break;
                    case 3: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-StraightLineAgent-" + (mapN+1) + ".txt"); break;
                    case 4: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-SmartStraightLineAgent-" + (mapN+1) + ".txt"); break;
                    case 5: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-ZigZagAgent-" + (mapN+1) + ".txt"); break;
                    case 6: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-RandomExplorerAgent-" + (mapN+1) + ".txt"); break;
                    case 7: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-SmartRandomExplorerAgent-" + (mapN+1) + ".txt"); break;
                    case 8: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-PauseEvery3SmartRandomAgent-" + (mapN+1) + ".txt"); break;
                    case 9: agent = new DiscreteDBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-PauseEvery3SmartStraightLineAgent-" + (mapN+1) + ".txt"); break;
                }
                if (agent!=null) {
                    for(int j = 0;j<REPEATS;j++) {
                        System.out.println("Generating trace...");
                        agent.start();
                        Trace t = GenerateDiscreteTraces.generateTrace(map,agent,1000,perception);
    //                    TraceVisualizer.newWindow("-", 800, 600, t).show();

                        double initialDirts = 0;
                        double minimumDirts = 0;
                        double timeAchieved = 0;
                        initialDirts = minimumDirts = t.getEntries().get(0).state.getObjects(Dirt.class).size();
                        for(TraceEntry e:t.getEntries()) {
                            double tmp = e.state.getObjects(Dirt.class).size();
                            if (tmp<minimumDirts) {
                                minimumDirts = tmp;
                                timeAchieved = e.getTime();
                            }
                        }
                        avg_initialDirts += initialDirts;
                        avg_minimumDirts += minimumDirts;
                        avg_timeAchieved += timeAchieved;
                    }
                }
                mapN++;
            }

            avg_initialDirts /= mapN*REPEATS;
            avg_minimumDirts /= mapN*REPEATS;
            avg_timeAchieved /= mapN*REPEATS;

            System.out.println("Agent " + i);
            System.out.println("Performance evaluation: " + (avg_initialDirts-avg_minimumDirts)/avg_initialDirts + " at " + avg_timeAchieved);
            agentN++;
        }
    }

}
