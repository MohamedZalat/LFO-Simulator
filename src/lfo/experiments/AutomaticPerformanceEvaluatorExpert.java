/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import java.util.LinkedList;
import java.util.List;
import lfo.agents.Agent;
import lfo.agents.discrete.*;
import lfo.simulator.Action;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.objects.Dirt;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class AutomaticPerformanceEvaluatorExpert {
    public static void main(String []args) throws Exception {
        int NMAPS = 7;
//        Perception perception = new WindowPerception();
        Perception perception = new FourRayDistancePerception();

        //String agent1 = new FixedSequenceAgent().name();
//        String agent1 = new RandomAgent().name();
//        String agent1 = new SmartRandomAgent().name();
//        String agent1 = new StraightLineAgent().name();
        String agent1 = new SmartStraightLineAgent().name();
        //String agent1 = new WallFollowerAgent();
        //String agent1 = new SmartWallFollowerAgent();
        //String agent1 = new ZigZagAgent();
//        Agent expert = new RandomAgent();
//        Agent expert = new SmartRandomAgent();
//        StraightLineAgent expert = new StraightLineAgent();
        SmartStraightLineAgent expert = new SmartStraightLineAgent();
        //Agent expert = new StraightLineAgent();
        //Agent expert = new SmartStraightLineAgent();
        //Agent expert = new RandomExplorerAgent();
        //Agent expert = new SmartRandomExplorerAgent();

        String expertName = expert.name();
        
        
        List<Trace> agent1Traces = new LinkedList<Trace>();

        for(int map = 0;map<NMAPS;map++) {
            System.out.println("Loading trace... " + expertName + " - " + map);
            agent1Traces.add(new Trace(new SAXBuilder().build("traces-fourraydistance/trace-m" + map + "-" + expertName + ".xml").getRootElement()));
        }
                
        double actions_total = 0;
        double actions_predicted = 0;
        for(int map = 0;map<NMAPS;map++) {
            Trace t1 = agent1Traces.get(map);
            expert.start();
            
            for(int t = 0;t<t1.getEntries().size();t++) {
                TraceEntry te1 = t1.getEntries().get(t);
                
                Action a1 = te1.action;
                Action a2 = expert.cycle(te1.subject, perception.perceive(te1.state, te1.state.get(te1.subject)), t);
                expert.setLastDirection(a1);
//                System.out.println(a1 + " - " + a2);
                
                if (a1==null && a2==null) {
                    actions_predicted++;
                } else {
                    if (a1!=null && a2!=null) {
                        if (a1.equals(a2)) actions_predicted++;   
                    }
                }
                actions_total++;
            }
        }

        System.out.println("Percentage of actions predicted: " + (actions_predicted/actions_total));
    }
}
