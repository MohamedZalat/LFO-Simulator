/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package toydomain;

import java.util.LinkedList;
import java.util.List;
import lfo.agents.Agent;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;

/**
 *
 * @author santi
 */
public class ToyInternalStateAgent extends Agent {
    int state = 1; // 1: going right, 2: going left
        
    public void start()
    {
        state = 1;
    }
    
    public Action cycle(int id,Perception p, double timeStep) {
        if (p.getInteger("state")==0) {
//            if (r.nextInt(5)==0) state = 3 - state;
            state = r.nextInt(2)+1;
        }
        if (state==1) {
            if (r.nextInt(10)==0) return new Action("left",id);
            return new Action("right",id);            
        } else {
            if (r.nextInt(10)==0) return new Action("right",id);
            return new Action("left",id);    
        }
    }

    public List<Integer> internalStateToBayesNetVariables() {
        List<Integer> l = new LinkedList<Integer>();
        l.add(state);
        return l;
    }

}
