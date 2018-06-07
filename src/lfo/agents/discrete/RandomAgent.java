/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.agents.discrete;

import java.util.LinkedList;
import java.util.List;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import java.util.Random;
import lfo.agents.Agent;
import util.Pair;

/**
 *
 * @author santi
 */

// LEVEL 2 agent
public class RandomAgent extends Agent {
    
    public List<Pair<Action,Double>> cycleDistribution(int id, Perception p, double timeStep) {
        List<Pair<Action,Double>> l = new LinkedList<Pair<Action,Double>>();
        l.add(new Pair<Action,Double>(null,0.2));
        l.add(new Pair<Action,Double>(new Action("up",id),0.2));
        l.add(new Pair<Action,Double>(new Action("right",id),0.2));
        l.add(new Pair<Action,Double>(new Action("left",id),0.2));
        l.add(new Pair<Action,Double>(new Action("down",id),0.2));
        return l;
    }


}
