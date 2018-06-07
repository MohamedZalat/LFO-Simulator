/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package toydomain;

import lfo.matlab.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import lfo.simulator.Action;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.objects.PhysicalObject;
import lfo.simulator.perception.Perception;

/**
 *
 * @author santi
 */
public class MatlabToyTraceTranslator {
    
    // translates a trace to Matlab format:
    public static void translateToMatlab(Trace t, Perception p,String fileName) throws IOException {
        FileWriter fw = new FileWriter(fileName);
        PhysicalObject subject = t.getState().get(t.getSubject());
        
        for(TraceEntry te:t.getEntries()) {
            List<Double> input = MatlabPerceptionTranslator.translateToMatlab(p.perceive(te.state, te.state.get(t.getSubject())), false);
            List<Double> output = new LinkedList<Double>();
            
            Action a = te.action;
            if (a.getName().equals("right")) {
                output.add(1.0);
            } else if (a.getName().equals("left")) {
                output.add(2.0);
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
            if (a.getName().equals("right")) {
                output.add(0.0);
            } else if (a.getName().equals("left")) {
                output.add(1.0);
            }
            // translate output
            for(Double d:input) fw.write(d + " ");
            for(Double d:output) fw.write(d + " ");
            fw.write("\n");
        }
        
        fw.close();
    }
    
    
}
