/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.simulator.shapes;

import lfo.simulator.objects.BoundingBox;
import lfo.simulator.shapes.Circle;
import lfo.simulator.shapes.Square;
import util.XMLWriter;
import java.awt.geom.Point2D;
import org.jdom.Element;

/**
 *
 * @author santi
 */
public abstract class Shape {
    public abstract boolean collision(Point2D.Double p, double a, Shape s2, Point2D.Double p2, double a2) throws Exception;
    public abstract BoundingBox getBoundingBox();
    public abstract void toxml(XMLWriter w);

    public static  Shape fromxml(Element e) {
        if (((Element)e.getChildren().get(0)).getName().equals("square")) {
            return new Square(((Element)e.getChildren().get(0)));
        } else if (((Element)e.getChildren().get(0)).getName().equals("circle")) {
            return new Circle(((Element)e.getChildren().get(0)));
        }
        return null;
    }
}
