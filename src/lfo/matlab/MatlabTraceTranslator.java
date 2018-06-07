/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.matlab;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import lfo.simulator.Action;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.objects.PhysicalObject;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;

/**
 *
 * @author santi
 */
public class MatlabTraceTranslator {
    
    // translates a trace to Matlab format:
    public static void translateToMatlab(Trace t, Perception p,String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        PhysicalObject subject = t.getState().get(t.getSubject());
        
        for(TraceEntry te:t.getEntries()) {
            List<Double> input = MatlabPerceptionTranslator.translateToMatlab(p.perceive(te.state, te.state.get(t.getSubject())), false);
            List<Double> output = new LinkedList<Double>();
            
            Action a = te.action;
            if (a==null) {
                output.add(5.0);
            } else if (a.getName().equals("up")) {
                output.add(1.0);
            } else if (a.getName().equals("right")) {
                output.add(2.0);
            } else if (a.getName().equals("left")) {
                output.add(3.0);
            } else if (a.getName().equals("down")) {
                output.add(4.0);
            } else if (a.getName().equals("move")) {
                output.add(a.getParameterDouble("wheel1"));
                output.add(a.getParameterDouble("wheel2"));
            }
            // translate output
//            System.out.println(input);
            for(Double d:input) fw.write(d + " ");
            for(Double d:output) fw.write(d + " ");
            fw.write("\n");
        }
        
        fw.close();
    }
    
    public static void translateToMatlabForNNet(Trace t, Perception p,String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        PhysicalObject subject = t.getState().get(t.getSubject());
        
        for(TraceEntry te:t.getEntries()) {
            List<Double> input = MatlabPerceptionTranslator.translateToMatlab(p.perceive(te.state, te.state.get(t.getSubject())), true);
            List<Double> output = new LinkedList<Double>();
            
            Action a = te.action;
            if (a==null) {
                output.add(1.0);
                output.add(0.0);
                output.add(0.0);
                output.add(0.0);
                output.add(0.0);
            } else if (a.getName().equals("up")) {
                output.add(0.0);
                output.add(1.0);
                output.add(0.0);
                output.add(0.0);
                output.add(0.0);
            } else if (a.getName().equals("right")) {
                output.add(0.0);
                output.add(0.0);
                output.add(1.0);
                output.add(0.0);
                output.add(0.0);
            } else if (a.getName().equals("left")) {
                output.add(0.0);
                output.add(0.0);
                output.add(0.0);
                output.add(1.0);
                output.add(0.0);
            } else if (a.getName().equals("down")) {
                output.add(0.0);
                output.add(0.0);
                output.add(0.0);
                output.add(0.0);
                output.add(1.0);
            } else if (a.getName().equals("move")) {
                output.add(a.getParameterDouble("wheel1"));
                output.add(a.getParameterDouble("wheel2"));
            }
            // translate output
            for(Double d:input) fw.write(d + " ");
            for(Double d:output) fw.write(d + " ");
            fw.write("\n");
        }
        
        fw.close();
    }
    
  public static void main(String [] arg) throws JDOMException, IOException{
	  	Perception perception = new FourRayDistancePerception();

    	Trace t = new Trace(new SAXBuilder().build("LFO-traces-fourraydistance/bnet/trace-m0-WallFollowerAgent.xml").getRootElement());
    	MatlabTraceTranslator.translateToMatlab(t, perception, "LFO-traces-fourraydistance/bnet/trace-m0-WallFollowerAgent.txt");
    }
    
    
}
