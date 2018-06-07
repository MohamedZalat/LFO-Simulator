/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.simulator.perception;

import lfo.simulator.objects.BoundingBox;
import lfo.simulator.objects.Dirt;
import lfo.simulator.objects.PhysicalObject;
import lfo.simulator.State;
import lfo.simulator.shapes.Square;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author santi
 */
public class FourRayDistancePerception extends Perception {

    public FourRayDistancePerception() {

    }


    public FourRayDistancePerception(State s, PhysicalObject subject) throws Exception {
        // create a fake object to use for collision detection:
        Point2D.Double pos = new Point2D.Double(subject.getPosition().x,subject.getPosition().y);
        String feature[] = {"l","r","u","d"};
        
        double offsx[] = {-1,1,0,0};
        double offsy[] = {0,0,-1,1};
        double pos_x = pos.x;
        double pos_y = pos.y;

        for(int i = 0;i<feature.length;i++) {
            int distance = 0;
            do{
                pos.x+=offsx[i];
                pos.y+=offsy[i];
                if (s.collision(subject, pos, 0)) {
                    PhysicalObject c = s.collisionWithObjects(subject, pos, 0);
                    if (c==null) {
                        values.put("d" + feature[i], Math.min(distance,1));
                        values.put(feature[i], 0);
                        break;
                    } else {
                        if (c instanceof Dirt) {
                            values.put("d" + feature[i], Math.min(distance,1));
                            values.put(feature[i], 1);
                            break;
                        } else {
                            values.put("d" + feature[i], Math.min(distance,1));
                            values.put(feature[i], 0);
                            break;
                        }
                    }
                }
                distance++;
            }while(true);
            pos.x = pos_x;
            pos.y = pos_y;
        }
    }


    public Perception perceive(State s, PhysicalObject subject) {
        try {
            return new FourRayDistancePerception(s, subject);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
