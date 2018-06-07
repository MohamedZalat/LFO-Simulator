/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.matlab;

import java.util.LinkedList;
import java.util.List;
import lfo.simulator.Trace;
import lfo.simulator.perception.ForceFourRayDistancePerception;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import lfo.simulator.perception.TurnableFourRayDistancePerception;
import toydomain.ToyPerception;
import toydomain2.Toy2Perception;

/**
 *
 * @author santi
 */
public class MatlabPerceptionTranslator {
    
    // translates a trace to Matlab format:
    public static List<Double> translateToMatlab(Perception p, boolean forNNet) {
        if ((p instanceof FourRayDistancePerception) ||
            (p instanceof TurnableFourRayDistancePerception)) {
            List<Double> l = new LinkedList<Double>();
            l.add((double)(p.getInteger("u")+(forNNet ? 0:1)));
            l.add((double)(p.getInteger("du")+(forNNet ? 0:1)));
            l.add((double)(p.getInteger("r")+(forNNet ? 0:1)));
            l.add((double)(p.getInteger("dr")+(forNNet ? 0:1)));
            l.add((double)(p.getInteger("d")+(forNNet ? 0:1)));
            l.add((double)(p.getInteger("dd")+(forNNet ? 0:1)));
            l.add((double)(p.getInteger("l")+(forNNet ? 0:1)));
            l.add((double)(p.getInteger("dl")+(forNNet ? 0:1)));
            return l;
        } else if (p instanceof ForceFourRayDistancePerception) {
            List<Double> l = new LinkedList<Double>();
            l.add((double)(p.getDouble("wheel1")));
            l.add((double)(p.getDouble("wheel2")));
            l.add((double)(p.getInteger("u")+(forNNet ? 0:1)));
            l.add(p.getDouble("du"));
            l.add((double)(p.getInteger("r")+(forNNet ? 0:1)));
            l.add(p.getDouble("dr"));
            l.add((double)(p.getInteger("d")+(forNNet ? 0:1)));
            l.add(p.getDouble("dd"));
            l.add((double)(p.getInteger("l")+(forNNet ? 0:1)));
            l.add(p.getDouble("dl"));
            return l;
        } else if (p instanceof ToyPerception) {
            List<Double> l = new LinkedList<Double>();
            l.add((double)(p.getInteger("state")+(forNNet ? 0:1)));
            return l;
        } else if (p instanceof Toy2Perception) {
            List<Double> l = new LinkedList<Double>();
            l.add((double)(p.getInteger("statex")+(forNNet ? 0:1)));
            l.add((double)(p.getInteger("statey")+(forNNet ? 0:1)));
            return l;
        } else {
            return null;
        }
    }

    public static List<Integer> translateToMatlabInteger(Perception p) {
        if ((p instanceof FourRayDistancePerception) ||
            (p instanceof TurnableFourRayDistancePerception)) {
            List<Integer> l = new LinkedList<Integer>();
            l.add(p.getInteger("u")+1);
            l.add(p.getInteger("du")+1);
            l.add(p.getInteger("r")+1);
            l.add(p.getInteger("dr")+1);
            l.add(p.getInteger("d")+1);
            l.add(p.getInteger("dd")+1);
            l.add(p.getInteger("l")+1);
            l.add(p.getInteger("dl")+1);
            return l;
        } else if (p instanceof ForceFourRayDistancePerception) {
            return null;
        } else {
            return null;
        }
    }

}
