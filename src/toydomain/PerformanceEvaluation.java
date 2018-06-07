/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package toydomain;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import lfo.agents.Agent;
import lfo.evaluation.ChiSquareConditionalLevel2TraceDistance;
import lfo.evaluation.ChiSquareConditionalLevel3TraceDistance;
import lfo.evaluation.ChiSquareLevel2TraceDistance;
import lfo.evaluation.Equation11Level2Risk;
import lfo.simulator.Action;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class PerformanceEvaluation {
    public static void main(String args[]) throws JDOMException, IOException, Exception {        
        List<Agent> agents = new LinkedList<Agent>();
        agents.add(new ToyFixedSequenceAgent());
        agents.add(new ToyRandomAgent());
        agents.add(new ToyLevel2AgentA());
        agents.add(new ToyLevel2AgentB());
        agents.add(new ToyLevel2AgentAStochastic());
        agents.add(new ToyLevel2AgentBStochastic());
        agents.add(new ToyStraightRandomAgent());
        agents.add(new ToyStraightLineAgent());
//        agents.add(new ToyInternalStateAgent());
        
        ToyPerception perception = new ToyPerception();
        
        DecimalFormat df = new DecimalFormat("#.##");
        String labels[]={"SEQ","RND","L2DETA","L2DETB","L2STOA","L2STOB","RNDS","INT"};
        for(int i = 0;i<agents.size();i++) {
            System.out.print(labels[i] + " ");
            for(int j = 0;j<agents.size();j++) {
                Agent a1 = agents.get(i);
                Agent a2 = agents.get(j);
                
                Trace ta1 = new Trace(new SAXBuilder().build("traces-toy/trace-1000-m0-" + a1.getClass().getSimpleName() + ".xml").getRootElement());
                Trace ta2 = new Trace(new SAXBuilder().build("traces-toy2/trace-1000-m0-" + a2.getClass().getSimpleName() + ".xml").getRootElement());
                
//                System.out.println("Comparing agent " + a1.getClass().getSimpleName() + " with " + a2.getClass().getSimpleName());
                
                a1.start();
                a2.start();
                
                double correct = 0;
                double total = 0;
                for(TraceEntry te:ta1.getEntries()) {
                    State s = te.state;
                    Action action1 = te.action;
                    Action action2 = a2.cycle(1, perception.perceive(s,s.get(1)), te.getTime());
                    if (action1.equals(action2)) correct++;
                    total++;
                }
//                System.out.print(" & " + (correct/total));
                System.out.print(" & " + df.format(Equation11Level2Risk.risk(ta1, ta2, perception, 1, 6)));
//                System.out.print(" & " + df.format(Equation11Level2Risk.risk(ta1, ta2, perception) - Equation11Level2Risk.risk(ta1, ta1, perception)));
//                System.out.print(" & " + df.format(ChiSquareLevel2TraceDistance.traceAverageChiSquare(ta1,ta2,perception)));

//                System.out.println("Accuracy: " + (correct/total) + " (" + ((int)correct) + "/" + ((int)total) + ")");
//                System.out.println("Stochastic Level 2 (Eq. 11): " + Equation11Level2Risk.risk(ta1, ta2, perception));
//                System.out.println("Fisher: " + ChiSquareLevel2TraceDistance.traceAverageChiSquare(ta1,ta2,perception));
//                System.out.println("Fisher order 2: " + ChiSquareLevel3TraceDistance.traceAverageChiSquare(ta1,ta2,perception,2));
            }                
            System.out.println(" \\\\");
        }
    }
}
