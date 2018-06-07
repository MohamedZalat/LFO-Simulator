/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import java.io.FileWriter;

import lfo.agents.Agent;
import lfo.agents.matlab.DiscreteBNetAgent;
import lfo.agents.matlab.DiscreteBNetOrderKAgent;
import lfo.agents.matlab.DiscreteDBNAgent;
import lfo.agents.matlab.DiscreteNNetAgent;
import lfo.agents.matlab.DiscreteNNetOrderKAgent;
import lfo.agents.LFOAgent;
import lfo.agents.discrete.SmartRandomAgent;
import lfo.agents.discrete.ZigZagAgent;
import lfo.experiments.GenerateDiscreteTraces;
import lfo.learning.LFO;
import lfo.learning.level2.KNN;
import lfo.learning.level3.Level2Wrapper;
import lfo.simulator.LearningTrace;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import lfo.simulator.gui.TraceVisualizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import lfo.agents.discrete.*;
import lfo.agents.matlab.*;
import lfo.learning.level1.Memorize;
import lfo.matlab.NNetRemote;
import lfo.simulator.objects.VacuumCleaner;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import util.XMLWriter;


/**
 *
 * @author santi
 */
public class GenerateDiscreteTracesFromLFOAgent {

    public static void main(String []args) throws Exception {
        List<State> maps = new LinkedList<State>();
        maps.add(new State(new SAXBuilder().build("C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/maps/discreet-8x8.xml").getRootElement()));
        //maps.add(new State(new SAXBuilder().build("C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/maps/discreet-8x8-2.xml").getRootElement()));
        //maps.add(new State(new SAXBuilder().build("C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/maps/discreet-8x8-3.xml").getRootElement()));
        //maps.add(new State(new SAXBuilder().build("C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/maps/discreet-8x8-4.xml").getRootElement()));
        //maps.add(new State(new SAXBuilder().build("C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/maps/discreet-8x8-5.xml").getRootElement()));
        //maps.add(new State(new SAXBuilder().build("C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/maps/discreet-32x32.xml").getRootElement()));
        //maps.add(new State(new SAXBuilder().build("C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/maps/discreet-32x32-2.xml").getRootElement()));

        String experts[] ={
                           //"RandomAgent",
                           //"SmartRandomAgent",
                           "WallFollowerAgent",
                           //"SmartWallFollowerAgent",
                           //"StraightLineAgent",
                           //"SmartStraightLineAgent",
                           //"ZigZagAgent",
                           //"FixedSequenceAgent",
//                           "RandomExplorerAgent",
                           //"SmartRandomExplorerAgent",
//                           "PauseEvery3SmartRandomAgent",
//                           "PauseEvery3SmartStraightLineAgent",
                           };
        
        for(String expert:experts) {
            
            System.out.println("Expert: " + expert);

            Perception p = new FourRayDistancePerception();
            for(int map = 0;map<maps.size();map++) {
            	
            	String agentName = "";
            	Agent a = null;

                System.out.println("Map: " + map);
//                String agentName = "random";
//                Agent a = new RandomAgent();

//                String agentName = "memorize";
//                Agent a = learnAgent(new Memorize(),expert,map, p);
                
 //               String agentName = "nnet";
 //               Agent a = learnMatlabAgent(DiscreteNNetAgent.class, agentName, expert, 8,true,map, p);

                 agentName = "bnet";
                 a = learnMatlabAgent(DiscreteBNetAgent.class, agentName, expert, 8,false,map, p);

//                String agentName = "bnetk2";
//                Agent a = learnMatlabAgent(DiscreteBNetOrderKAgent.class, agentName, expert, 8,false,map, p);

//                String agentName = "nnetk2";
//                Agent a = learnMatlabAgent(DiscreteNNetOrderKAgent.class, agentName, expert, 8,true,map, p);

//                String agentName = "hmm";
//                Agent a = learnMatlabAgent(DiscreteDBNAgent.class, agentName, expert, 8,false,map, p);

//                String agentName = "iohmm";
//                Agent a = learnMatlabAgent(DiscreteDBNAgent.class, agentName, expert, 8,false,map, p);
                
//                String agentName = "lfodbn";
//                Agent a = learnMatlabAgent(DiscreteDBNAgent.class, agentName, expert, 8,false,map, p);

                if (args.length > 0){
                	switch(args[0]){
	            	case "BN":
	            		agentName = "bnet";
	            		a = learnMatlabAgent(DiscreteBNetAgent.class, agentName, expert, 8,false,map, p);
	            		break;
	            	case "BNk2":
	            		agentName = "bnetk2";
	            		a = learnMatlabAgent(DiscreteBNetOrderKAgent.class, agentName, expert, 8,false,map, p);
	            		break;
	            	case "NN":
	            		agentName = "nnet";
	            		a = learnMatlabAgent(DiscreteNNetAgent.class, agentName, expert, 8,true,map, p);
	            		break;
	            	case "NNk2":
	            		agentName = "nnetk2";
	            		a = learnMatlabAgent(DiscreteNNetOrderKAgent.class, agentName, expert, 8,true,map, p);
	            		break;
	            	case "IOHMM":
	            		agentName = "iohmm";
	            		a = learnMatlabAgent(DiscreteDBNAgent.class, agentName, expert, 8,false,map, p);
	            		break;
	            	case "DBN":
	            		agentName = "lfodbn";
	            		a = learnMatlabAgent(DiscreteDBNAgent.class, agentName, expert, 8,false,map, p);
	            		break;
	            	}
                }
                
                State s = maps.get(map);
                Trace t = GenerateDiscreteTraces.generateTrace(s, a, 1000, p);
                FileWriter fw = new FileWriter("C:/Users/sachagunaratne/workspace4/LFOSimulation/workspace/LFOsimulator/LFO-traces-fourraydistance/" + agentName + "_new2/trace-m" + map + "-" + expert + ".xml");
                t.toxml(new XMLWriter(fw));
                fw.close();
                System.out.println("Finished Writing");
            }
        }
    }

    
    
    public static Agent learnMatlabAgent(Class type, String agentName, String expert, int XSIZE, boolean NNet, int leaveOutMap, Perception p) throws JDOMException, IOException {
        List<String> learningTraces = new LinkedList<String>();

        // load all the learning traces:
        {
            List<Trace> tmp = new LinkedList<Trace>();
            for(int j = 0;j<2;j++) {
                if (j!=leaveOutMap) {
                    String fileName = "C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/traces-fourraydistance/trace-m" + j + "-" + expert + (NNet ? "-nnet":"") + ".txt";
                    learningTraces.add(fileName);
                }
               }
        }
        
        if (type == DiscreteNNetAgent.class) return new DiscreteNNetAgent(learningTraces,XSIZE);
        if (type == DiscreteBNetAgent.class) return new DiscreteBNetAgent(learningTraces,XSIZE);
        if (type == DiscreteBNetOrderKAgent.class) return new DiscreteBNetOrderKAgent(learningTraces,XSIZE, 2);
        if (type == DiscreteNNetOrderKAgent.class) return new DiscreteNNetOrderKAgent(learningTraces,XSIZE, 2);
        if (type == DiscreteDBNAgent.class) {
            if (agentName.equals("lfodbn")) {
                return DiscreteDBNAgent.getLfODBNFromMATLAB(learningTraces,"C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/Matlab/LfODBN-" + expert + "-m" + leaveOutMap + ".txt",XSIZE);
            } else if (agentName.equals("iohmm")) {
                return DiscreteDBNAgent.getIOHMMFromMATLAB(learningTraces,"C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/Matlab/IOHMM-" + expert + "-m" + leaveOutMap + ".txt",XSIZE);
            } else {
                return DiscreteDBNAgent.getHMMFromMATLAB(learningTraces,"C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/Matlab/HMM-" + expert + "-m" + leaveOutMap + ".txt",XSIZE);
            }
        }
        return null;
    }    
    
    
    static HashMap<String,Trace> tracesLoaded = new HashMap<String,Trace>();

    public static Agent learnAgent(LFO lfo, String expert, int leaveOutMap, Perception p) throws JDOMException, IOException {
        List<LearningTrace> learningTraces = new LinkedList<LearningTrace>();

        // load all the learning traces:
        {
            List<Trace> tmp = new LinkedList<Trace>();
            for(int j = 0;j<7;j++) {
                if (j!=leaveOutMap) {
                    String fileName = "C:/Users/sachagunaratne/Downloads/LFOSimulation/workspace/LFOsimulator/traces-fourraydistance/trace-m" + j + "-" + expert + ".xml";
                    Trace t = tracesLoaded.get(fileName);
                    if (t==null) {
                        t = new Trace(new SAXBuilder().build(fileName).getRootElement());
                        tracesLoaded.put(fileName,t);
                    }
                    learningTraces.add(new LearningTrace(t,p));
                }
            }
        }

        return new LFOAgent(lfo, learningTraces);
    }
}
