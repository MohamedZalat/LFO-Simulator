/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain;

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
public class ToyStraightRandomAgent extends Agent {
    int state = 1; // 1: going right, 2: going left
        
    public void start()
    {
        state = 1;
    }
    
    public Action cycle(int id,Perception p, double timeStep) {
        if (p.getInteger("state")==0) {
            state = r.nextInt(2) + 1;
        } else {
            if (r.nextInt(6)==0) {
                state = 3 - state;
            }
        }
        if (state==1) return new Action("right",id);
        return new Action("left",id);
    }

    public List<Integer> internalStateToBayesNetVariables() {
        List<Integer> l = new LinkedList<Integer>();
        l.add(state);
        return l;
    }

}
