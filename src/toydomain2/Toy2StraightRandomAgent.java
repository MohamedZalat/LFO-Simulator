/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain2;

import lfo.agents.discrete.*;
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
public class Toy2StraightRandomAgent extends Agent {
    int state = 1; // 1: going right, 2: going left, 3: going up, 4: going down
    
    public void start() {
        state = 1;
    }
    
    public Action cycle(int id,Perception p, double timeStep) {
        if (r.nextInt(4)==0) {
            state = r.nextInt(4) + 1;
        }
        if (state==1) return new Action("right",id);
        if (state==2) return new Action("left",id);
        if (state==3) return new Action("up",id);
        return new Action("down",id);
    }

    public List<Integer> internalStateToBayesNetVariables() {
        List<Integer> l = new LinkedList<Integer>();
        l.add(state);
        return l;
    }

}
