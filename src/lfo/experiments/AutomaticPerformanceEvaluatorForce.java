/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import java.util.LinkedList;
import java.util.List;
import lfo.agents.Agent;
import lfo.agents.discrete.*;
import lfo.agents.force.ForceSmartStraightLineAgent;
import lfo.agents.force.ForceSmarterStraightLineAgent;
import lfo.agents.force.ForceStraightLineAgent;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.objects.Dirt;
import lfo.simulator.perception.ForceFourRayDistancePerception;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class AutomaticPerformanceEvaluatorForce {
    public static void main(String []args) throws Exception {
        int REPEATS = 1;
        int mapN = 0,agentN = 0;
        List<State> maps = new LinkedList<State>();
        maps.add(new State(new SAXBuilder().build("maps/force-8x8.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-8x8-2.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-8x8-3.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-8x8-4.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-8x8-5.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-32x32.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build("maps/force-32x32-2.xml").getRootElement()));
//        Perception perception = new WindowPerception();
        Perception perception = new ForceFourRayDistancePerception(0.1);

        List<Agent> agents = new LinkedList<Agent>();

        agentN = 2;
//        int i = 2;
        for(int i = 0;i<3;i++)
        {
            mapN = 0;
            double avg_initialDirts = 0;
            double avg_minimumDirts = 0;
            double avg_timeAchieved = 0;
            double avg_predicted = 0;
//            State map = maps.get(0);
            for(State map:maps)
            {
                Agent agent = null;
                for(int j = 0;j<REPEATS;j++) {
                    switch(i) {
                        case 0: agent = new ForceStraightLineAgent(); break;
                        case 1: agent = new ForceSmartStraightLineAgent(); break;
                        case 2: agent = new ForceSmarterStraightLineAgent(); break;
                    }
                    Trace t = GenerateContinuousTraces.generateContinuousTrace(map, agent, 1000, 0.1, perception, 1);
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
