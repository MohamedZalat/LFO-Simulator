/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.simulator;

import lfo.simulator.Action;
import lfo.simulator.objects.Dirt;
import lfo.simulator.Map;
import lfo.simulator.objects.Obstacle;
import lfo.simulator.objects.PhysicalObject;
import lfo.simulator.objects.VacuumCleaner;
import lfo.simulator.objects.VacuumCleanerTurnable;
import util.XMLWriter;
import java.awt.geom.Point2D;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import lfo.simulator.objects.*;
import org.jdom.Element;
import toydomain.ToyVacuumCleaner;
import toydomain2.Toy2VacuumCleaner;

/**
 *
 * @author santi
 */
public class State implements Cloneable {
    Map map;
    List<PhysicalObject> objects;

    public State(Element e) {
        Element map_e = e.getChild("map");
        map = new Map(map_e);

        objects = new LinkedList<PhysicalObject>();
        for(Object o:e.getChild("objects").getChildren()) {
            Element object_e = (Element)o;

            if (object_e.getName().equals("vacuum")) {
                objects.add(new VacuumCleaner(object_e));
            } if (object_e.getName().equals("vacuumturnable")) {
                objects.add(new VacuumCleanerTurnable(object_e));
            } if (object_e.getName().equals("vacuumforce")) {
                objects.add(new VacuumCleanerForce(object_e));
            } if (object_e.getName().equals("toyvacuum")) {
                objects.add(new ToyVacuumCleaner(object_e));
            } if (object_e.getName().equals("toy2vacuum")) {
                objects.add(new Toy2VacuumCleaner(object_e));
            } else if (object_e.getName().equals("obstacle")) {
                objects.add(new Obstacle(object_e));
            } else if (object_e.getName().equals("dirt")) {
                objects.add(new Dirt(object_e));
            } else {

            }
        }
    }

    public State(Map m) {
        map = m;
        objects = new LinkedList<PhysicalObject>();
    }

    public State(Map m, List<PhysicalObject> l) {
        map = m;
        objects = l;
    }

    public Object clone() {
        State s = new State((Map) (map.clone()));
        for(PhysicalObject o:objects) {
            PhysicalObject oc = (PhysicalObject) o.clone();
            oc.setID(o.getID());
            s.objects.add(oc);
        }
        return s;
    }

    public PhysicalObject get(int i) {
        for(PhysicalObject o:objects) {
            if (o.getID()==i) return o;
        }
        return null;
    }

    public PhysicalObject get(Class c) {
        for(PhysicalObject o:objects) {
            if (c.isInstance(o)) return o;
        }
        return null;
    }

    public Map getMap() {
        return map;
    }


    public void cycle(List<Action> actions, double time) throws Exception {
        map.cycle();

        // We make a copy of "objects" to prevent problems if some of the "cycle" methods add or remove obejcts
        List<PhysicalObject> l = new LinkedList<PhysicalObject>();
        l.addAll(objects);
        for(PhysicalObject po:l) {
            if (objects.contains(po)) po.cycle(actions, this, time);
        }
    }

    public void cycle(Action action, double time) throws Exception {
        List<Action> actions = new LinkedList<Action>();
        if (action!=null) actions.add(action);
        map.cycle();

        // We make a copy of "objects" to prevent problems if some of the "cycle" methods add or remove obejcts
        List<PhysicalObject> l = new LinkedList<PhysicalObject>();
        l.addAll(objects);
        for(PhysicalObject po:l) {
            if (objects.contains(po)) po.cycle(actions, this, time);
        }
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        XMLWriter xmlw = new XMLWriter(sw);
        toxml(xmlw);
        return sw.toString();
    }

    public void toxml(XMLWriter w) {
       w.tag("simulationstate");
       map.toxml(w);
       w.tag("objects");
       for(PhysicalObject po:objects) po.toxml(w);
       w.tag("/objects");
       w.tag("/simulationstate");
    }

    public List<PhysicalObject> getObjects() {
        return objects;
    }

    public List<PhysicalObject> getObjects(Class c) {
        List<PhysicalObject> l = new LinkedList<PhysicalObject>();
        for(PhysicalObject o:objects) {
            if (c.isInstance(o)) l.add(o);
        }
        return l;
    }


    public void removeObject(PhysicalObject o) {
        objects.remove(o);
    }

    public boolean collision(PhysicalObject o) throws Exception {
        return collision(o,o.getPosition(), o.getAngle());
    }

    public boolean collision(PhysicalObject o, Point2D.Double p, double angle) throws Exception {
        if (map.collision(o,p, angle)) return true;

        for(PhysicalObject o2:objects) {
            if (o==o2) continue;
            if (o.collision(p,angle,o2,o2.getPosition(),o2.getAngle())) return true;
        }
        
        return false;
    }

    public PhysicalObject collisionWithObjects(PhysicalObject o) throws Exception {
        return collisionWithObjects(o,o.getPosition(), o.getAngle());
    }

    public PhysicalObject collisionWithObjects(PhysicalObject o, Point2D.Double p, double angle) throws Exception {
        for(PhysicalObject o2:objects) {
            if (o==o2) continue;
            if (o.collision(p,angle,o2,o2.getPosition(),o2.getAngle())) return o2;
        }

        return null;
    }


}
