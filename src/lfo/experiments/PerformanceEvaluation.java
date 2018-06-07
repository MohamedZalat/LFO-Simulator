/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.experiments;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import lfo.evaluation.ChiSquareConditionalLevel2TraceDistance;
import lfo.evaluation.ChiSquareConditionalLevel3TraceDistance;
import lfo.evaluation.Equation11Level2Risk;
import lfo.evaluation.Equation11Level2RiskLearningTrace;
import lfo.evaluation.KLLevel2TraceDistance;
import lfo.evaluation.KLLevel3TraceDistance;
import lfo.simulator.Action;
import lfo.simulator.LearningTrace;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class PerformanceEvaluation {
    public static void main(String []args) throws Exception {
        int NMAPS = 7;
//        Perception perception = new WindowPerception();
        Perception perception = new FourRayDistancePerception();
        String experts[] ={            
                           "WallFollowerAgent",
        //                   "SmartWallFollowerAgent",
       //                    "RandomAgent",
      //                     "SmartRandomAgent",
      //                     "StraightLineAgent",
     //                      "SmartStraightLineAgent",
    //                       "ZigZagAgent",
   //                        "FixedSequenceAgent",
  //                         "RandomExplorerAgent",
  //                         "SmartRandomExplorerAgent",
//                           "PauseEvery3SmartRandomAgent",
//                           "PauseEvery3SmartStraightLineAgent",
                           };

 //       String LFOName = "expert";
//        String LFOName = "random";
//        String LFOName = "memorize";
//        String LFOName = "nnet-stochastic";
        String LFOName = "nnet";
//        String LFOName = "nnetk2";
    //    String LFOName = "bnet";
//        String LFOName = "bnetk2";
  //      String LFOName = "lfodbn";
 //       String LFOName = "iohmm";
//        String LFOName = "hmm";
        
        
        double average_accuracy = 0;
        double average_risk = 0;
        
        DecimalFormat df = new DecimalFormat("#.##");
        for(String expertName:experts) {
            List<Trace> agent1Traces = new LinkedList<Trace>();
            List<Trace> agent2Traces = new LinkedList<Trace>();
            List<LearningTrace> agent1LTraces = new LinkedList<LearningTrace>();
            List<LearningTrace> agent2LTraces = new LinkedList<LearningTrace>();

            System.out.println(" ---- " + expertName + " ----------------");
            for(int map = 0;map<NMAPS;map++) {
//                System.out.println("Loading trace... " + expertName + " - " + map);
                Trace t1 = new Trace(new SAXBuilder().build("traces-fourraydistance/trace-m" + map + "-" + expertName + ".xml").getRootElement());
                agent1Traces.add(t1);
                agent1LTraces.add(new LearningTrace(t1,perception));
//                System.out.println("Loading trace... " + LFOName + " - " + map);
                Trace t2 = new Trace(new SAXBuilder().build("LFO-traces-fourraydistance/" + LFOName + "/trace-m" + map + "-" + expertName + ".xml").getRootElement());
                agent2Traces.add(t2);
                agent2LTraces.add(new LearningTrace(t2,perception));
            }

//            int NSTATES = 4;
            
            double correct = 0;
            double total = 0;
            for(int i = 0;i<agent1LTraces.size();i++) {
                LearningTrace ta1 = agent1LTraces.get(i);
                LearningTrace ta2 = agent2LTraces.get(i);
                for(int j = 0;j<ta1.getEntries().size();j++) {
                    Action action1 = ta1.getEntries().get(j).action;
                    Action action2 = ta2.getEntries().get(j).action;
                    if (action1==null) {
                        if (action2==null) correct++;
                    } else {
                        if (action1.equals(action2)) correct++;
                    }
                    total++;
                }                
            }
            
            System.out.println("Accuracy (from traces): " + (correct/total) + " (" + ((int)correct) + "/" + ((int)total) + ")");
            average_accuracy += correct/total;
//            System.out.println("Vapnik's Risk (1 - 1): " + Equation11Level2RiskLearningTrace.risk(agent1LTraces, agent1LTraces, perception, 1, 1280));
 //           double risk = Equation11Level2RiskLearningTrace.risk(agent1LTraces, agent2LTraces, perception, 1, 1280);
 //           average_risk += risk;
 //           System.out.println("Vapnik's Risk (1 - 2): " + risk);
//            System.out.println("Vapnik's Risk (2 - 1): " + Equation11Level2RiskLearningTrace.risk(agent2LTraces, agent1LTraces, perception, 1, 1280));
//            System.out.println("Vapnik's Risk (2 - 2): " + Equation11Level2RiskLearningTrace.risk(agent2LTraces, agent2LTraces, perception, 1, 1280));
  //          System.out.println("Chi-Square Distance: " + ChiSquareConditionalLevel2TraceDistance.tracesAverageChiSquare(agent1Traces,agent2Traces,new FourRayDistancePerception()));
    //        System.out.println("Statistically significant different states (p = 0.5) A" + expertName + " - A" + LFOName + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(agent1Traces,agent2Traces,new FourRayDistancePerception(), 0.5));
    //        System.out.println("Statistically significant different states (p = 0.3) A" + expertName + " - A" + LFOName + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(agent1Traces,agent2Traces,new FourRayDistancePerception(), 0.3));
    //        System.out.println("Statistically significant different states (p = 0.2) A" + expertName + " - A" + LFOName + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(agent1Traces,agent2Traces,new FourRayDistancePerception(), 0.2));
    //        System.out.println("Ratio of SS different States: (p = 0.1):" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(agent1Traces,agent2Traces,new FourRayDistancePerception(), 0.1));
    //        System.out.println("Statistically significant different states (p = 0.05) A" + expertName + " - A" + LFOName + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(agent1Traces,agent2Traces,new FourRayDistancePerception(), 0.05));
    //        System.out.println("Statistically significant different states (p = 0.01) A" + expertName + " - A" + LFOName + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(agent1Traces,agent2Traces,new FourRayDistancePerception(), 0.01));
    //        System.out.println("Statistically significant different states (p = 0.001) A" + expertName + " - A" + LFOName + ":" + ChiSquareLevel2TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(agent1Traces,agent2Traces,new FourRayDistancePerception(), 0.001));
    //        System.out.println("Chi-Square Distance (k=2):" + ChiSquareConditionalLevel3TraceDistance.tracesAverageChiSquare(agent1Traces,agent2Traces,new FourRayDistancePerception(), 2));
    //        System.out.println("Ratio of SS different States: (k=2) (p = 0.1):" + ChiSquareLevel3TraceDistance.tracesRatioOfStatisticallySignificantDifferentStates(agent1Traces,agent2Traces,new FourRayDistancePerception(), 0.1, 2));
    //      System.out.println("KL Distance:" + KLLevel2TraceDistance.tracesExpectedKL(agent1Traces,agent2Traces,new FourRayDistancePerception()));
    //      System.out.println("KL Distance (k=2):" + KLLevel3TraceDistance.tracesExpectedKL(agent1Traces,agent2Traces,new FourRayDistancePerception(), 2));
        }

        System.out.println("Average Accuracy (from traceS): " + (average_accuracy/experts.length));
  //      System.out.println("Average Risk: " + (average_risk/experts.length));
    }
}
