/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.agents.matlab;

import java.util.LinkedList;
import java.util.List;
import lfo.agents.Agent;
import lfo.matlab.BNetRemoteDiscrete;
import lfo.matlab.BNetRemoteForce;
import lfo.matlab.MatlabPerceptionTranslator;
import lfo.matlab.NNetRemote;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import util.Sampler;

/**
 *
 * @author santi
 */
public class DiscreteBNetAgent extends Agent {
    BNetRemoteDiscrete bnet = null;
        
    public DiscreteBNetAgent(List<String> traces, int XSIZE) {
      bnet = new BNetRemoteDiscrete(traces,XSIZE,1);  
    }

    public Action cycle(int id, Perception p, double timeStep) {
        Action actions[]={null,new Action("up",id),new Action("right",id),new Action("left",id),new Action("down",id)};
        List<Double> input = MatlabPerceptionTranslator.translateToMatlab(p, false);
        List<Double> output = bnet.run(input);
        int max = 0;
        
      //  System.out.println("cycle: " + input + " -> " + output);
        // probabilistic:
        List<Double> forSampling = new LinkedList<Double>();
        for(int i = 0;i<output.size();i++) {
            forSampling.add(Math.max(0,output.get(i)));
        }
        try {
            max = Sampler.weighted(forSampling);
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        // deterministic:
//        for(int i = 0;i<output.size();i++) {
//            if (output.get(i)>output.get(max)) max = i;
//        }
        //System.out.println(output + " -> " + max);
        return actions[max];
    }
}
