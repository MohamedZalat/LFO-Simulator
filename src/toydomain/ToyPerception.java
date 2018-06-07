/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain;

import lfo.simulator.perception.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import lfo.simulator.State;
import lfo.simulator.objects.Dirt;
import lfo.simulator.objects.PhysicalObject;

/**
 *
 * @author santi
 */
public class ToyPerception extends Perception {

    public ToyPerception() {
        
    }

    public ToyPerception(State s, PhysicalObject subject) throws Exception {
        values.put("state",(int)(subject.getPosition().x));
    }


    public Perception perceive(State s, PhysicalObject subject) {
        try {
            return new ToyPerception(s, subject);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
