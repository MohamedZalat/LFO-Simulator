/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import lfo.agents.Agent;
import lfo.agents.matlab.DiscreteDBNAgent;
import lfo.agents.LFOAgent;
import lfo.agents.discrete.SmartRandomAgent;
import lfo.agents.discrete.ZigZagAgent;
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
import lfo.agents.discrete.*;
import lfo.agents.matlab.DiscreteNNetAgent;
import lfo.simulator.objects.VacuumCleaner;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 *
 * @author santi
 */
public class TraceVisualizerFromAgent {

    public static void main(String []args) throws Exception {
        Agent a = null;
   //    a = new RandomAgent();
    a = new FrequencyAgent();
   //     a = new DirtWallAgent();
  //      a = new SmartStraightLineAgent();
    //    a = new WallFollowerAgent();
       // a = new SmartWallFollowerAgent();
       // a = new FixedSequenceAgent();
    //    a = new ZigZagAgent();
//        Agent a = new SmartRandomExplorerAgent();
/*
        {
            List<String> traces = new LinkedList<String>();
            traces.add("traces-fourraydistance/trace-m0-SmartRandomAgent-nnet.txt");
            traces.add("traces-fourraydistance/trace-m1-SmartRandomAgent-nnet.txt");
            traces.add("traces-fourraydistance/trace-m2-SmartRandomAgent-nnet.txt");
            traces.add("traces-fourraydistance/trace-m3-SmartRandomAgent-nnet.txt");
            traces.add("traces-fourraydistance/trace-m4-SmartRandomAgent-nnet.txt");
            traces.add("traces-fourraydistance/trace-m5-SmartRandomAgent-nnet.txt");
            traces.add("traces-fourraydistance/trace-m6-SmartRandomAgent-nnet.txt");
            a = new DiscreteNNetAgent(traces,8);
        }
        */
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-FixedSequenceAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-RandomAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-SmartRandomAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-StraightLineAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-SmartStraightLineAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-ZigZagAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-RandomExplorerAgent-2.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l2-SmartRandomExplorerAgent-1.txt");

//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-FixedSequenceAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-RandomAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-SmartRandomAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-StraightLineAgent-1.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-SmartStraightLineAgent-1.txt");
//        Agent a = new DBNAgent("/Users/santi/my-research/FullBNT-1.0.7/santi-experiments/learned-bnets/bnet-l3-ZigZagAgent-3.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-RandomExplorerAgent-2.txt");
//        Agent a = new DBNAgent("C:\\my-research\\FullBNT-1.0.7\\santi-experiments\\learned-bnets\\bnet-l3-SmartRandomExplorerAgent-1.txt");

//        Agent a = learnAgent(new KNN(1),"FixedSequenceAgent",5, new FourRayDistancePerception());
//        Agent a = learnAgent(new KNN(1),"RandomAgent",5, new FourRayDistancePerception());
//        Agent a = learnAgent(new KNN(1),"SmartRandomAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new KNN(1),"StraightLineAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new KNN(1),"SmartStraightLineAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new KNN(1),"ZigZagAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new KNN(1),"RandomExplorerAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new KNN(1),"SmartRandomExplorerAgent",6, new FourRayDistancePerception());

//        Agent a = learnAgent(new Level2Wrapper(2, new KNN(1)),"FixedSequenceAgent",0, new FourRayDistancePerception());
//        Agent a = learnAgent(new Level2Wrapper(2, new KNN(1)),"RandomAgent",0, new FourRayDistancePerception());
//        Agent a = learnAgent(new Level2Wrapper(2, new KNN(1)),"SmartRandomAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new Level2Wrapper(2, new KNN(1)),"StraightLineAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new Level2Wrapper(2, new KNN(1)),"SmartStraightLineAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new Level2Wrapper(2, new KNN(1)),"ZigZagAgent",6, new FourRayDistancePerception());
//        Agent a = learnAgent(new Level2Wrapper(2, new KNN(1)),"SmartRandomExplorerAgent",6, new FourRayDistancePerception());

        Perception p = new FourRayDistancePerception();
        //State s = new State(new SAXBuilder().build("maps/new_maps/discreet-10x14-2.xml").getRootElement());
     // State s = new State(new SAXBuilder().build("maps/new_maps/discreet-8x16-empty3.xml").getRootElement());
     State s = new State(new SAXBuilder().build("maps/new_maps/discreet-8x8.xml").getRootElement());
      //  State s = new State(new SAXBuilder().build("maps/discreet-32x32.xml").getRootElement());
     //   State s = new State(new SAXBuilder().build("maps/discreet-32x32-9.xml").getRootElement());
        Trace t = GenerateDiscreteTraces.generateTrace(s, a, 100, p);
        TraceVisualizer ad = new TraceVisualizer(t, 800, 600, 1, "");
        JFrame frame = new JFrame("Visualizing Agent " + a.getClass().getSimpleName());
        frame.getContentPane().add(ad);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static Agent learnAgent(LFO lfo, String expert, int leaveOutMap, Perception p) throws JDOMException, IOException {
        List<LearningTrace> learningTraces = new LinkedList<LearningTrace>();

        // load all the learning traces:
        {
            List<Trace> tmp = new LinkedList<Trace>();
            for(int j = 0;j<1;j++) {
                if (j!=leaveOutMap) {
                    System.out.println("Loading " + j);
                    String fileName = "LFO-traces-fourraydistance\\bnet\\trace-m" + j + "-" + expert + ".xml";
                    Trace t = new Trace(new SAXBuilder().build(fileName).getRootElement());
                    learningTraces.add(new LearningTrace(t,p));
                }
            }
        }

        return new LFOAgent(lfo, learningTraces);
    }
}
