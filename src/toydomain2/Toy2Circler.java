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
public class Toy2Circler extends Agent {
    int state1 = 1; // 1: going right, 2: going down, 3: going left, 4: going up
    
    public void start() {
        state1 = 1;
    }
    
    public Action cycle(int id,Perception p, double timeStep) {
        if (r.nextInt(4)==0) {
            state1 = ((state1+1)%4)+1;
        }
        if (state1==1) return new Action("right",id);
        if (state1==2) return new Action("down",id);
        if (state1==3) return new Action("left",id);
        return new Action("up",id);
    }

    public List<Integer> internalStateToBayesNetVariables() {
        List<Integer> l = new LinkedList<Integer>();
        l.add(state1);
        return l;
    }

}
