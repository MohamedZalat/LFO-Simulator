/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import lfo.evaluation.HMMDistanceGivenTraces;
import lfo.evaluation.HMMDistanceRandomTraces;
import lfo.simulator.Trace;
import java.util.LinkedList;
import java.util.List;
import lfo.evaluation.ChiSquareConditionalLevel2TraceDistance;
import lfo.simulator.perception.FourRayDistancePerception;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class TraceSimilarity {
    public static void main(String args[]) throws Exception {
        List<List<Trace>> traces = new LinkedList<List<Trace>>();
        int NSTATES = 4;
        int NMAPS = 7;
        String agents[]={
                         "FixedSequenceAgent",
                         "RandomAgent","SmartRandomAgent",
                         "WallFollowerAgent","SmartWallFollowerAgent",
//                         "StraightLineAgent","SmartStraightLineAgent","ZigZagAgent",
//                         "RandomExplorerAgent","SmartRandomExplorerAgent"
        };

        for(int agent = 0;agent<agents.length;agent++) {
            List<Trace> traces_agent = new LinkedList<Trace>();
            for(int map = 0;map<NMAPS;map++) {
                System.out.println("Loading trace... " + agent + " - " + map);
                traces_agent.add(new Trace(new SAXBuilder().build("traces-fourraydistance/trace-m" + map + "-" + agents[agent] + ".xml").getRootElement()));
            }
            traces.add(traces_agent);
        }

        // Compute the statistics directly from all the traces:
        for(int agent1 = 0;agent1<agents.length;agent1++) {
            for(int agent2 = 0;agent2<agents.length;agent2++) {
//                System.out.println("Similarity A" + agent1 + " - A" + agent2 + ":" + HMMDistanceRandomTraces.distance(traces.get(agent1),traces.get(agent2),NSTATES, 10,10));
//                System.out.println("Similarity A" + agent1 + " - A" + agent2 + ":" + HMMDistanceGivenTraces.distance(traces.get(agent1),traces.get(agent2),NSTATES, 20, false));

                System.out.println("Distance A" + agent1 + " - A" + agent2 + ":" + ChiSquareConditionalLevel2TraceDistance.tracesAverageChiSquare(traces.get(agent1),traces.get(agent2),new FourRayDistancePerception()));
//                System.out.println("Statistically significant different states (p = 0.5) A" + agent1 + " - A" + agent2 + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(traces.get(agent1),traces.get(agent2),new FourRayDistancePerception(), 0.5));
//                System.out.println("Statistically significant different states (p = 0.3) A" + agent1 + " - A" + agent2 + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(traces.get(agent1),traces.get(agent2),new FourRayDistancePerception(), 0.3));
//                System.out.println("Statistically significant different states (p = 0.2) A" + agent1 + " - A" + agent2 + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(traces.get(agent1),traces.get(agent2),new FourRayDistancePerception(), 0.2));
                System.out.println("Statistically significant different states (p = 0.1) A" + agent1 + " - A" + agent2 + ":" + ChiSquareConditionalLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(traces.get(agent1),traces.get(agent2),new FourRayDistancePerception(), 0.1));
//                System.out.println("Statistically significant different states (p = 0.05) A" + agent1 + " - A" + agent2 + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(traces.get(agent1),traces.get(agent2),new FourRayDistancePerception(), 0.05));
//                System.out.println("Statistically significant different states (p = 0.01) A" + agent1 + " - A" + agent2 + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(traces.get(agent1),traces.get(agent2),new FourRayDistancePerception(), 0.01));
//                System.out.println("Statistically significant different states (p = 0.001) A" + agent1 + " - A" + agent2 + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(traces.get(agent1),traces.get(agent2),new FourRayDistancePerception(), 0.001));
            }
        }

        // Compute the statistics trace by trace and then do the average:
        /*
        for(int agent1 = 0;agent1<agents.length;agent1++) {
            for(int agent2 = 0;agent2<agents.length;agent2++) {
                double average = 0;
                double averagess = 0;
                for(int t1 = 0;t1<NMAPS;t1++) {
                    for(int t2 = 0;t2<NMAPS;t2++) {
                        double d = ChiSquareLevel2TraceDistance.traceAverageChiSquare(traces.get(agent1).get(t1),
                                                                                      traces.get(agent2).get(t2),
                                                                                      new FourRayDistancePerception());
                        double ss = ChiSquareLevel2TraceDistance.traceRatioOfStatisticallySignificantDifferentStates(traces.get(agent1).get(t1),
                                                                                               traces.get(agent2).get(t2),
                                                                                               new FourRayDistancePerception(),
                                                                                               0.1);
//                        System.out.println(d + " - " + ss);
                        average+=d;
                        averagess+=ss;
                    }
                }
                System.out.println("Average of A" + agent1 + " - A" + agent2 + ": " + (average/(NMAPS*NMAPS)));
                System.out.println("Averagr of Statistically significant different states (p = 0.1)  A" + agent1 + " - A" + agent2 + ": " + (averagess/(NMAPS*NMAPS)));
            }
        }
        */
    }
}
