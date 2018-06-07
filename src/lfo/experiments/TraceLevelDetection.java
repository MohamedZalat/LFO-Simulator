/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import java.util.LinkedList;
import java.util.List;
import lfo.evaluation.FisherLevelDetector;
import lfo.simulator.Trace;
import lfo.simulator.perception.FourRayDistancePerception;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class TraceLevelDetection {
    public static void main(String args[]) throws Exception {
        List<List<Trace>> traces = new LinkedList<List<Trace>>();
        int NSTATES = 4;
        int NMAPS = 7;
        String agents[]={
                         "FixedSequenceAgent",
                         "RandomAgent","SmartRandomAgent",
                         "WallFollowerAgent","SmartWallFollowerAgent",
                         "StraightLineAgent","SmartStraightLineAgent","ZigZagAgent",
                         "RandomExplorerAgent","SmartRandomExplorerAgent"
        };

        for(int agent = 0;agent<agents.length;agent++) {
            List<Trace> traces_agent = new LinkedList<Trace>();
            for(int map = 0;map<NMAPS;map++) {
                System.out.println("Loading trace... " + agent + " - " + map);
                traces_agent.add(new Trace(new SAXBuilder().build("traces-fourraydistance/trace-m" + map + "-" + agents[agent] + ".xml").getRootElement()));
            }
            traces.add(traces_agent);
        }

        
        for(int agent1 = 0;agent1<agents.length;agent1++) {
//            System.out.println("Fisher test for " + agents[agent1] + ": " + FisherLevelDetector.correlation(traces.get(agent1),new FourRayDistancePerception()));
            System.out.println("Log Fisher test for " + agents[agent1] + ": " + FisherLevelDetector.logCorrelation(traces.get(agent1),new FourRayDistancePerception()));
            for(Trace t:traces.get(agent1)) {
//            System.out.println("Fixher test for A" + agent1 + ": " + FisherLevelDetector.correlation(traces.get(agent1),new FourRayDistancePerception()));
//                System.out.println("Log Fixher test for A" + agent1 + ": " + FisherLevelDetector.logCorrelation(t,new FourRayDistancePerception()));
//                System.out.println("Fixher test for A" + agent1 + ": " + FisherLevelDetector.correlation(t,new FourRayDistancePerception()));
            }
        }
    }
}
