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
public class Toy2StraightLineAgent extends Agent {
    int state1 = 1; // 1: going right, 2: going left, 3: going up, 4: going down
    int state2 = 0; // number of steps until direction change
    
    public void start() {
        state1 = 1;
        state2 = 0;       
    }
    
    public Action cycle(int id,Perception p, double timeStep) {
        if (state2==0) {
            state1 = r.nextInt(4) + 1;
            state2 = 4;
        }
        state2--;
        if (state1==1) return new Action("right",id);
        if (state1==2) return new Action("left",id);
        if (state1==3) return new Action("up",id);
        return new Action("down",id);
    }

    public List<Integer> internalStateToBayesNetVariables() {
        List<Integer> l = new LinkedList<Integer>();
        l.add(state1);
        l.add(state2);
        return l;
    }

}
