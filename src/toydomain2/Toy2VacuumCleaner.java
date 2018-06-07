/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package toydomain2;

import lfo.simulator.objects.*;
import lfo.simulator.Action;
import lfo.simulator.shapes.Shape;
import lfo.simulator.State;
import lfo.simulator.objects.Dirt;
import util.XMLWriter;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import org.jdom.Element;

/**
 *
 * @author santi
 */
public class Toy2VacuumCleaner extends PhysicalObject {

    public Toy2VacuumCleaner(Shape s, double x, double y, double a) {
        position.x = x;
        position.y = y;
        angle = a;
        shape = s;
    }

    public Toy2VacuumCleaner(Element e) {
        position.x = Double.parseDouble(e.getChildText("x"));
        position.y = Double.parseDouble(e.getChildText("y"));
        angle = Double.parseDouble(e.getChildText("angle"));
        shape = Shape.fromxml(e.getChild("shape"));
        ID = Integer.parseInt(e.getAttributeValue("id"));
    }

    public List<Action> executableActions() {
        List<Action> l = new LinkedList<Action>();
        l.add(new Action("left",1));
        l.add(new Action("right",1));
        return l;
    }


    public void toxml(XMLWriter w) {
        w.tagWithAttributes("vacuum", "id = \"" + ID + "\"");
        w.tag("x",position.x);
        w.tag("y",position.y);
        w.tag("angle",angle);
        w.tag("shape");
        shape.toxml(w);
        w.tag("/shape");
        w.tag("/vacuum");
    }

    public void cycle(List<Action> actions, State s, double time) throws Exception {
        for(Action a:actions) {
            if (a.getObjectID()==ID) {
                // execute action:
                if (a.getName().equals("right")) {
                    position.x+=time;
                    if (position.x>=s.getMap().getDx()) position.x-=s.getMap().getDx();
                } else if (a.getName().equals("left")) {
                    position.x-=time;
                    if (position.x<0) position.x+=s.getMap().getDx();
                } else if (a.getName().equals("up")) {
                    position.y-=time;
                    if (position.y<0) position.y+=s.getMap().getDy();
                } else if (a.getName().equals("down")) {
                    position.y-=time;
                    if (position.y<0) position.y+=s.getMap().getDy();
                }
            }
        }
    }

    
    public Object clone() {
        return new Toy2VacuumCleaner(shape,position.x,position.y, angle);
    }

    

}
