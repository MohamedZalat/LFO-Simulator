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
public class DiscreteNNetOrderKAgent extends Agent {
    NNetRemoteOrderKDiscrete nnet = null;
        
    public DiscreteNNetOrderKAgent(List<String> traces, int XSIZE, int ORDER) {
      nnet = new NNetRemoteOrderKDiscrete(traces,XSIZE,5,ORDER);  
    }

    public Action cycle(int id, Perception p, double timeStep) {
        Action actions[]={null,new Action("up",id),new Action("right",id),new Action("left",id),new Action("down",id)};
        List<Double> input = MatlabPerceptionTranslator.translateToMatlab(p, true);
        int output = nnet.run(input);

        return actions[output];
    }
    
    public void replaceLastAction(Action a) {
        if (a==null) nnet.replaceLastAction(0);
        else if (a.getName().equals("up")) nnet.replaceLastAction(1);
        else if (a.getName().equals("right")) nnet.replaceLastAction(2);
        else if (a.getName().equals("left")) nnet.replaceLastAction(3);
        else if (a.getName().equals("down")) nnet.replaceLastAction(4);
        else {
            System.err.println("DiscreteNNetOrderKAgent.replaceLastAction: Replacing an action with an invalid one!");
        }
    }
}
