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
import lfo.evaluation.HMMDistanceGivenTraces;
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
public class AutomaticKLOutputEvaluatorLFO {
    public static void main(String []args) throws Exception {
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
        List<List<LearningTrace>> learningTraces = new LinkedList<List<LearningTrace>>();

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
                List<LearningTrace> tmp2 = new LinkedList<LearningTrace>();
                for(Trace t:tmp) tmp2.add(new LearningTrace(t,perception));
                learningTraces.add(tmp2);
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
//                    LFO lfo = null;
//                    LFO lfo = new Memorize();
//                LFO lfo = new KNN(1);
                    LFO lfo = new Level2Wrapper(2,new KNN(1));
                List<LearningTrace> l = new LinkedList<LearningTrace>();
                l.addAll(learningTraces.get(i));
                l.remove(mapN);
                LFOAgent agent = new LFOAgent(lfo, l);
//                Agent agent = new RandomAgent();

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
