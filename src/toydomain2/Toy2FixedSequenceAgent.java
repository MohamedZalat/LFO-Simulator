/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain2;

import lfo.agents.discrete.*;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import lfo.agents.Agent;

/**
 *
 * @author santi
 */

// LEVEL 1 agent
public class Toy2FixedSequenceAgent extends Agent {
    String sequence[] = {"right", "right", "down", "left", "left","up","down","right","right","up","left","left"};
    int time_step = 0;
    
    public void start() {
        time_step = 0;
    }
   

    public Action cycle(int id,Perception p, double timeStep) {
        Action a = new Action(sequence[time_step%sequence.length],id);
        time_step++;
        return a;
    }

    public List<Integer> internalStateToBayesNetVariables() {
        List<Integer> l = new LinkedList<Integer>();
        l.add((time_step%sequence.length)+1); // we add 1, since for Matlab, it's better if the lowest value is 1
        return l;
    }

}
