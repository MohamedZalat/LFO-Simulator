/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.agents.force;

import lfo.agents.turnable.*;
import lfo.agents.discrete.*;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import lfo.simulator.perception.WindowPerception;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import lfo.agents.Agent;
import lfo.simulator.perception.TurnableFourRayDistancePerception;

/**
 *
 * @author santi
 */

// LEVEL 3 agent with bounded internal state (IOHMM)
public class ForceStraightLineAgent extends Agent {
    Random r = new Random();
    double maxForce = 5.0;
    int state = 0;  // last direction of turn (1 : left, 2: right)
    
    public Action cycle(int id,Perception p, double timeStep) {
        boolean canGoForward = true;
        boolean canGoLeft = true;
        boolean canGoRight = true;
        
        double targetSpeed1 = 0;
        double targetSpeed2 = 0;

        if (p.getDouble("du")<timeStep*5 && p.getInteger("u")==0) canGoForward = false;
        if (p.getDouble("dl")<timeStep*5 && p.getInteger("l")==0) canGoLeft = false;
        if (p.getDouble("dr")<timeStep*5 && p.getInteger("r")==0) canGoRight = false;

        if (canGoForward) {
            targetSpeed1 = 1;
            targetSpeed2 = 1;
        } else {
            if (canGoLeft) {
                targetSpeed1 = -1;
                targetSpeed2 = 1;
                state = 1;
            } else if (canGoRight) {
                targetSpeed1 = 1;
                targetSpeed2 = -1;
                state = 2;
            } else {
                if (state==1) {
                    targetSpeed1 = -1;
                    targetSpeed2 = 1;                    
                } else {
                    targetSpeed1 = 1;
                    targetSpeed2 = -1;                    
                }
            }
        }
        
        double dif1 = targetSpeed1 - p.getDouble("wheel1");
        double dif2 = targetSpeed2 - p.getDouble("wheel2");
        
        dif1/=timeStep;
        dif2/=timeStep;
        
        if (dif1>maxForce) dif1 = maxForce;
        if (dif1<-maxForce) dif1 = -maxForce;
        if (dif2>maxForce) dif2 = maxForce;
        if (dif2<-maxForce) dif2 = -maxForce;

        return new Action("move",id,"wheel1",dif1,"wheel2", dif2);
    }
}
