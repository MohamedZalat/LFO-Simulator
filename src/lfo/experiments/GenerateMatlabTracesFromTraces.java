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
import lfo.matlab.MatlabTraceTranslator;
import lfo.matlab.NNetRemote;
import lfo.simulator.objects.VacuumCleaner;
import lfo.simulator.perception.ForceFourRayDistancePerception;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import util.XMLWriter;

/**
 *
 * @author santi
 */
public class GenerateMatlabTracesFromTraces {

    public static void main(String[] args) throws Exception {
        String experts[] = {
            "FixedSequenceAgent",
//            "WallFollowerAgent",
//            "SmartWallFollowerAgent",
//            "RandomAgent",
            "SmartRandomAgent",
//            "StraightLineAgent",
            "SmartStraightLineAgent",
            "ZigZagAgent"
        };

        for (String expert : experts) {

            System.out.println("Expert: " + expert);

            Perception p = new FourRayDistancePerception();
            for (int map = 0; map < 7; map++) {

                System.out.println("Map: " + map);
//                String agentName = "expert";
                String agentName = "random";
//                String agentName = "memorize";
//                String agentName = "nnet";
//                String agentName = "bnet";
//                String agentName = "bnetk2";
//                String agentName = "nnetk2";
//                String agentName = "lfodbn";
//                String agentName = "iohmm";
                
            	if (args.length > 0){
	            	switch(args[0]){
	            	case "BN":
	            		agentName = "bnet";
	            		break;
	            	case "BNk2":
	            		agentName = "bnetk2";
	            		break;
	            	case "NN":
	            		agentName = "nnet";
	            		break;
	            	case "NNk2":
	            		agentName = "nnetk2";
	            		break;
	            	case "IOHMM":
	            		agentName = "iohmm";
	            		break;
	            	case "DBN":
	            		agentName = "lfodbn";
	            		break;
	            	}
	            	
            	}

                String fileName = "M:/Desktop/workspace/LFOsimulator/LFO-traces-fourraydistance2/" + agentName + "/trace-m" + map + "-" + expert;
                Trace t = new Trace(new SAXBuilder().build(fileName + ".xml").getRootElement());;
                Perception perception = new FourRayDistancePerception();
                MatlabTraceTranslator.translateToMatlab(t, perception, fileName + ".txt");
                MatlabTraceTranslator.translateToMatlabForNNet(t, perception, fileName + "-nnet.txt");

            }
        }
    }
}
