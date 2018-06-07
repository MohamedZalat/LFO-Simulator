/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.agents.matlab;

import java.util.List;
import lfo.agents.Agent;
import lfo.matlab.BNetRemoteForce;
import lfo.matlab.MatlabPerceptionTranslator;
import lfo.matlab.NNetRemote;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;

/**
 *
 * @author santi
 */
public class ForceBNetAgent extends Agent {
    BNetRemoteForce bnet = null;
    double maxForce = 5.0;
        
    public ForceBNetAgent(List<String> traces, int XSIZE) {
      bnet = new BNetRemoteForce(traces,XSIZE,2);  
    }

    public Action cycle(int id, Perception p, double timeStep) {
        List<Double> input = MatlabPerceptionTranslator.translateToMatlab(p, false);
        List<Double> output = bnet.run(input);
        double f1 = output.get(0);
        double f2 = output.get(1);
        if (f1<-maxForce) f1 = -maxForce;
        if (f1>maxForce) f1 = maxForce;
        if (f2<-maxForce) f2 = -maxForce;
        if (f2>maxForce) f2 = maxForce;
        return new Action("move",id,"wheel1",f1,"wheel2", f2);
    }
}
