/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.agents.matlab;

import java.util.LinkedList;
import java.util.List;
import lfo.agents.Agent;
import lfo.matlab.*;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import util.Sampler;

/**
 *
 * @author santi
 */
public class DiscreteBNetOrderKAgent extends Agent {
    BNetRemoteOrderKDiscrete bnet = null;
        
    public DiscreteBNetOrderKAgent(List<String> traces, int XSIZE, int ORDER) {
      bnet = new BNetRemoteOrderKDiscrete(traces,XSIZE,ORDER);  
    }

    public Action cycle(int id, Perception p, double timeStep) {
        Action actions[]={null,new Action("up",id),new Action("right",id),new Action("left",id),new Action("down",id)};
        List<Double> input = MatlabPerceptionTranslator.translateToMatlab(p, false);
        int output = bnet.run(input);
        
        return actions[output];
    }
    
    public void replaceLastAction(Action a) {
        if (a==null) bnet.replaceLastAction(0);
        else if (a.getName().equals("up")) bnet.replaceLastAction(1);
        else if (a.getName().equals("right")) bnet.replaceLastAction(2);
        else if (a.getName().equals("left")) bnet.replaceLastAction(3);
        else if (a.getName().equals("down")) bnet.replaceLastAction(4);
        else {
            System.err.println("DiscreteBNetOrderKAgent.replaceLastAction: Replacing an action with an invalid one!");
        }
    }    
}
