/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.simulator.objects;

import lfo.simulator.Action;
import lfo.simulator.State;
import lfo.simulator.shapes.Shape;
import lfo.simulator.objects.PhysicalObject;
import lfo.simulator.shapes.Square;
import util.XMLWriter;
import java.util.List;
import org.jdom.Element;

/**
 *
 * @author santi
 */
public class BoundingBox extends PhysicalObject {

    public BoundingBox(Square s, double x, double y)
    {
        position.x = x;
        position.y = y;
        angle = 0.0;
        shape = s;
    }

    public BoundingBox(Element e) {
        position.x = Double.parseDouble(e.getChildText("x"));
        position.y = Double.parseDouble(e.getChildText("y"));
        angle = 0.0;
        shape = Shape.fromxml(e.getChild("shape"));
        ID = Integer.parseInt(e.getAttributeValue("id"));
    }

    public void toxml(XMLWriter w) {
        w.tagWithAttributes("nn", "id = \"" + ID + "\"");
        w.tag("x",position.x);
        w.tag("y",position.y);
        w.tag("shape");
        shape.toxml(w);
        w.tag("/shape");
        w.tag("/nn");
    }

    public Object clone() {
        return new BoundingBox((Square)shape,position.x,position.y);
    }

    public void cycle(List<Action> actions, State s, double time) throws Exception {
    }
}
