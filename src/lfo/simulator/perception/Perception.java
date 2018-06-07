/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.simulator.perception;

import java.io.StringWriter;
import java.util.*;
import lfo.simulator.State;
import lfo.simulator.objects.PhysicalObject;
import org.jdom.Element;
import util.XMLWriter;

/**
 *
 * @author santi
 */
public class Perception {
    protected HashMap<String,Number> values;

    public Perception() {
        values = new LinkedHashMap<String,Number>();
    }

    public Perception(Perception p) {
        values = new LinkedHashMap<String,Number>();
        values.putAll(p.values);
    }

    public HashMap<String,Number> getValues() {
        return values;
    }

    public Number get(String feature) {
        return values.get(feature);
    }

    public Integer getInteger(String feature) {
        return (Integer)values.get(feature);
    }

    public Double getDouble(String feature) {
        return (Double)values.get(feature);
    }

    public Perception(Element e) {
        values = new HashMap<String,Number>();
        for(Object o:e.getChildren()) {
            Element e2 = (Element)o;
            values.put(e2.getName(),Integer.parseInt(e2.getText()));
        }
    }

    public void toxml(XMLWriter w) {
       w.tag("perception");
       for(String pname:values.keySet()) {
           if (values.get(pname)==null) {
               w.rawXML("<" + pname + "></" + pname + ">");
           } else {
               w.rawXML("<" + pname + ">" + values.get(pname) + "</" + pname + ">");
           }
       }
       w.tag("/perception");
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        XMLWriter xmlw = new XMLWriter(sw);
        toxml(xmlw);
        return sw.toString();
    }
    
    public boolean equals(Object o) {
        if (o instanceof Perception) {
            Perception p = (Perception)o;
            if (p.values.keySet().size() != values.keySet().size()) return false;
            for(String k:p.values.keySet()) {
                Number n1 = values.get(k);
                Number n2 = p.values.get(k);
                if (!n1.equals(n2)) return false;
            }
        } 
        return true;
    }
        
    public Perception perceive(State s, PhysicalObject subject) {
        return new Perception();
    }
}
