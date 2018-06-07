/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain2;

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
public class Toy2Perception extends Perception {

    public Toy2Perception() {
        
    }

    public Toy2Perception(State s, PhysicalObject subject) throws Exception {
        values.put("statex",(int)(subject.getPosition().x));
        values.put("statey",(int)(subject.getPosition().y));
    }


    public Perception perceive(State s, PhysicalObject subject) {
        try {
            return new Toy2Perception(s, subject);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
