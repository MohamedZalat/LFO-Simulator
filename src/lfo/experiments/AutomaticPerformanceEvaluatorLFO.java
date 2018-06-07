/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.experiments;

import lfo.agents.Agent;
import lfo.agents.LFOAgent;
import lfo.learning.LFO;
import lfo.learning.level2.KNN;
import lfo.learning.level3.Level2Wrapper;
import lfo.matlab.BNetRemoteDiscrete;
import lfo.matlab.BNetRemoteOrderKDiscrete;
import lfo.matlab.NNetRemote;
import lfo.matlab.NNetRemoteOrderKDiscrete;
import lfo.simulator.LearningTrace;
import lfo.simulator.State;
import lfo.simulator.Trace;
import lfo.simulator.TraceEntry;
import lfo.simulator.objects.Dirt;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import lfo.agents.matlab.DiscreteBNetAgent;
import lfo.agents.matlab.DiscreteBNetOrderKAgent;
import lfo.agents.matlab.DiscreteDBNAgent;
import lfo.agents.matlab.DiscreteNNetAgent;
import lfo.agents.matlab.DiscreteNNetOrderKAgent;
import lfo.simulator.Action;
import lfo.simulator.objects.VacuumCleaner;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author santi
 */
public class AutomaticPerformanceEvaluatorLFO {
    public static void main(String []args) throws Exception {
        String experts[] = {"WallFollowerAgent",
        		//"SmartWallFollowerAgent",
                           // "RandomAgent","SmartRandomAgent", 
        		//"StraightLineAgent",
                      //      "SmartStraightLineAgent", 
                        //    "ZigZagAgent", 
                         //   "FixedSequenceAgent",
 //                           "RandomExplorerAgent", "SmartRandomExplorerAgent",
//                                "PauseEvery3SmartRandomAgent", "PauseEvery3SmartStraightLineAgent"
                            };
//    	String experts[] = {
//                "RandomAgent", 
//                };
    	
        int REPEATS = 1;
        
        int mapN = 0;
        List<State> maps = new LinkedList<State>();
        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8.xml").getRootElement()));
        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-2.xml").getRootElement()));
       maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-3.xml").getRootElement()));
      maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-4.xml").getRootElement()));
       maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-5.xml").getRootElement()));
      maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-32x32.xml").getRootElement()));
      maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-32x32-2.xml").getRootElement()));
//        Perception perception = new WindowPerception();
        Perception perception = new FourRayDistancePerception();

        List<Agent> agents = new LinkedList<Agent>();

        List<List<Trace>> traces = new LinkedList<List<Trace>>();
        List<List<String>> MatlabTraceNames = new LinkedList<List<String>>();
        List<List<String>> MatlabNNetTraceNames = new LinkedList<List<String>>();
        List<List<LearningTrace>> learningTraces = new LinkedList<List<LearningTrace>>();
        
       SummaryStatistics s = new SummaryStatistics();
       SummaryStatistics fmeasu = new SummaryStatistics();

        String DATA = Config.LOCAL_DATA;
        if (args.length >= 2){
        	DATA  = args[1];
        }
        System.out.println("Loading Data from : " + DATA);
        
        // load all the learning traces:
        {
            for(int i = 0;i<experts.length;i++) {
                List<Trace> tmp = new LinkedList<Trace>();
                List<String> tmp2 = new LinkedList<String>();
                List<String> tmp3 = new LinkedList<String>();
                for(int j = 0;j<7;j++) {
                    //System.out.println("Loading " + i + " " + j);
                    //tmp.add(new Trace(new SAXBuilder().build(Config.LOCAL_DATA + "traces-fourraydistance2/trace-m" + j + "-" + experts[i] + ".xml").getRootElement()));
                    //tmp2.add(Config.LOCAL_DATA + "traces-fourraydistance2/trace-m" + j + "-" + experts[i] + ".txt");
                    //tmp3.add(Config.LOCAL_DATA + "traces-fourraydistance2/trace-m" + j + "-" + experts[i] + "-nnet.txt");
                    tmp.add(new Trace(new SAXBuilder().build(DATA + "traces-fourraydistance/trace-m" + j + "-" + experts[i] + ".xml").getRootElement()));
                    tmp2.add(DATA + "traces-fourraydistance/trace-m" + j + "-" + experts[i] + ".txt");
                    tmp3.add(DATA + "traces-fourraydistance/trace-m" + j + "-" + experts[i] + "-nnet.txt");
                    
                }
                MatlabTraceNames.add(tmp2);
                MatlabNNetTraceNames.add(tmp3);
                traces.add(tmp);
                List<LearningTrace> tmp_lt = new LinkedList<LearningTrace>();
                for(Trace t:tmp) tmp_lt.add(new LearningTrace(t,perception));
                learningTraces.add(tmp_lt);
            }
        }
        
        
        FileWriter f1 = new FileWriter("C:/Users/sachagunaratne/workspace4/LFOSimulation/workspace/LFOsimulator/evaluation/" + args[0] + ".txt");
        FileWriter f2 = new FileWriter("C:/Users/sachagunaratne/workspace4/LFOSimulation/workspace/LFOsimulator/evaluation/" + args[0] + "-fmeasure.txt");
        FileWriter f3 = new FileWriter("C:/Users/sachagunaratne/workspace4/LFOSimulation/workspace/LFOsimulator/evaluation/" + args[0] + "-other.txt");
        for(int i = 0;i<learningTraces.size();i++)
        {
            mapN = 0;
            double avg_initialDirts = 0;
            double avg_minimumDirts = 0;
            double avg_timeAchieved = 0;
            double avg_predicted = 0;
            double avg_fmeasure =0;
            
            long avgTotalTime = 0;
            //new arrays to hold precision and recall and fmeasure----------------
            Double [] precision = new Double [5];
            Double [] recall = new Double [5];
            Double [] fmeasure = new Double [5];
            //-------------------------------------------------------
            
            HashMap<String, HashMap<String, Integer>> matrix = new HashMap<String, HashMap<String, Integer>>();
//            State map = maps.get(0);
            for(State map:maps)
            {
                /*
                LFO lfo = new KNN(1);
                List<LearningTrace> l = new LinkedList<LearningTrace>();
                l.addAll(learningTraces.get(i));
                l.remove(mapN);
                LFOAgent agent = new LFOAgent(lfo, l);
                */
                /*
                List<String> l = new LinkedList<String>();
                l.addAll(MatlabNNetTraceNames.get(i));
                l.remove(mapN);
//                
                */
            	
            	
            	long ttt = System.currentTimeMillis();
            	Agent agent = null;
            	if (args.length > 0){
            		List<String> l = new LinkedList<String>();                     
	            	switch(args[0]){
	            	case "BN":
	            		l.addAll(MatlabTraceNames.get(i));
	            		l.remove(mapN);
	            		agent = new DiscreteBNetAgent(l,8);
	            		break;
	            	case "BNk2":
	            		l.addAll(MatlabTraceNames.get(i));
	            		l.remove(mapN);
	            		agent = new DiscreteBNetOrderKAgent(l,8,2);
	            		break;
	            	case "NN":
	            		l.addAll(MatlabNNetTraceNames.get(i));
	            		l.remove(mapN);
	            		agent = new DiscreteNNetAgent(l,8);
	            		break;
	            	case "NNk2":
	            		l.addAll(MatlabNNetTraceNames.get(i));
	            		l.remove(mapN);
	            		agent = new DiscreteNNetOrderKAgent(l,8, 2);
	            		break;
	            	case "IOHMM":
	            		l.addAll(MatlabTraceNames.get(i));
	            		l.remove(mapN);
	            		agent = DiscreteDBNAgent.getIOHMMFromMATLAB(l,Config.LOCAL_MAP + "Matlab/LfODBN-EVAL_" + experts[i] + "_" + mapN,8);
	            		break;
	            	case "DBN":
	            		l.addAll(MatlabTraceNames.get(i));
	            		l.remove(mapN);
	            		agent = DiscreteDBNAgent.getLfODBNFromMATLAB(l,Config.LOCAL_MAP + "Matlab/LfODBN-EVAL_" + experts[i] + "_" + mapN,8);
	            		break;
	            	}
	            	
            	}
            	ttt = (System.currentTimeMillis() - ttt);
//            	System.out.println("Learning Time : " + ttt);
            	avgTotalTime += ttt;
//                List<String> l = new LinkedList<String>();
//               l.addAll(MatlabTraceNames.get(i));
//                l.addAll(MatlabNNetTraceNames.get(i));
//                l.remove(mapN);
//				Agent agent = DiscreteDBNAgent.getLfODBNFromMATLAB(l,"M:/Desktop/workspace/LFOsimulator/Matlab/LfODBN-EVAL_" + experts[i] + "_" + mapN,8);
//				Agent agent = DiscreteDBNAgent.getIOHMMFromMATLAB(l,"M:/Desktop/workspace/LFOsimulator/Matlab/LfODBN-EVAL_" + experts[i] + "_" + mapN,8);
//				Agent agent = DiscreteDBNAgent.getHMMFromMATLAB(l,"M:/Desktop/workspace/LFOsimulator/Matlab/LfODBN-EVAL_" + experts[i] + "_" + mapN,8);
//                Agent agent = new DiscreteBNetAgent(l,8);
//                Agent agent = new DiscreteBNetOrderKAgent(l,8,2);
//                Agent agent = new DiscreteNNetAgent(l,8);
//                Agent agent = new DiscreteNNetOrderKAgent(l,8,2);
                for(int j = 0;j<REPEATS;j++) {
                    double initialDirts = 0;
                    double minimumDirts = 0;
                    double timeAchieved = 0;
                    double predicted = 0;

                    long totalTime = 0;
                    /*
                    System.out.println("Generating trace...");

                    agent.start();
                    Trace t = GenerateDiscreteTraces.generateTrace(map,agent,1000,perception);
                    agent.end();
//                    TraceVisualizer.newWindow("-", 800, 600, t).show();

                    initialDirts = minimumDirts = t.getEntries().get(0).state.getObjects(Dirt.class).size();
                    for(TraceEntry e:t.getEntries()) {
                        double tmp = e.state.getObjects(Dirt.class).size();
                        if (tmp<minimumDirts) {
                            minimumDirts = tmp;
                            timeAchieved = e.getTime();
                        }
                    }
                    avg_initialDirts += initialDirts;
                    avg_minimumDirts += minimumDirts;
                    avg_timeAchieved += timeAchieved;
                    */
                    
             //       System.out.print("Evaluating output... ");
                    agent.start();
                    int vacuumID = traces.get(i).get(mapN).getEntries().get(0).state.get(VacuumCleaner.class).getID();
                    LearningTrace targetTrace = learningTraces.get(i).get(mapN);
                    
                    for(int time = 0;time<targetTrace.getEntries().size();time++) {
                    	long length = System.currentTimeMillis();
                        Action al2;
                        Action a1 = agent.cycle(vacuumID, targetTrace.getEntries().get(time).perception, 1.0);
                        //it seems like it is getting the perception from the expert
                        al2 = targetTrace.getEntries().get(time).action;
//                        System.out.println("A: " + a1.getName() + " == C: " + al2.getName());
                        if (agent instanceof DiscreteNNetOrderKAgent) ((DiscreteNNetOrderKAgent)agent).replaceLastAction(al2);
                        if (agent instanceof DiscreteBNetOrderKAgent) ((DiscreteBNetOrderKAgent)agent).replaceLastAction(al2);
                        //there is no update last action for the DBN? need to make the replaceLastAction
                        if(agent instanceof DiscreteDBNAgent & args[0].equals("DBN")) ((DiscreteDBNAgent)agent).replaceLastAction(al2);
                        if (a1 == null && al2 ==null) {
                            predicted++;
                        } else {
                            if (a1!=null && al2!=null) {
                                if (a1.equals(al2)) predicted++;
                            }
                        }
                        String correct = (al2 == null) ? "NOACTION" : al2.getName();
                        String guess = (a1 == null) ? "NOACTION" : a1.getName();
                        if (!matrix.containsKey(correct)){
                        	matrix.put(correct, new HashMap<String, Integer>());
                        }
                        if (!matrix.get(correct).containsKey(guess)){
                        	matrix.get(correct).put(guess, 0);
                        }
                        int value = matrix.get(correct).get(guess).intValue();
                        matrix.get(correct).put(guess, value + 1);
                        totalTime += (System.currentTimeMillis() - length);
                    }
 //                   System.out.print(predicted/targetTrace.getEntries().size());
                    avg_predicted += predicted/targetTrace.getEntries().size();
                    s.addValue(predicted/targetTrace.getEntries().size());
 //                   System.out.println(" Total Time : " + totalTime);
                    avgTotalTime += totalTime / targetTrace.getEntries().size();
                }
                mapN++;
            }
            
            //addition of precision and recall calculation----------------------------
            int [][] conMat = GetConfusionMatrix(matrix);
            
            	for(int i1=0;i1<conMat.length;i1++){
            		//checking for divide by zero
            		if(sumOfCol(conMat,i1)!=0){
            			precision[i1] = (double) (conMat[i1][i1])/sumOfCol(conMat,i1);
            		}else{
            			precision[i1] = 0.0;
            		}
            		if(sumOfRow(conMat,i1)!=0){
            			recall[i1] = (double) (conMat[i1][i1])/sumOfRow(conMat,i1);
            		} else{
            			recall[i1] = 0.0;
            		}
            		
            		if(precision[i1]!=0 && recall[i1]!=0){
            			fmeasure[i1] = (2*precision[i1]*recall[i1])/(precision[i1]+recall[i1]);
            		}else{
            			fmeasure[i1] = 0.0;
            		}
            		fmeasu.addValue(fmeasure[i1]);
            		avg_fmeasure += fmeasure[i1];
        
            	}
            
            //-----------------------------------------------------------------------
            avg_initialDirts /= mapN*REPEATS;
            avg_minimumDirts /= mapN*REPEATS;
            avg_timeAchieved /= mapN*REPEATS;
            avg_predicted /= mapN*REPEATS;

            avgTotalTime /=  mapN;
            
            avg_fmeasure = avg_fmeasure/conMat.length;
            
            System.out.println("Agent " + experts[i] +" - Accuracy: " + avg_predicted + "+- " +  s.getStandardDeviation());
            System.out.println("Agent " + experts[i] +" - FMeasure: " + avg_fmeasure + "+- " +  fmeasu.getStandardDeviation());
//            System.out.println("Performance evaluation: " + (avg_initialDirts-avg_minimumDirts)/avg_initialDirts + " at " + avg_timeAchieved);
 //           System.out.println("Output evaluation: " + avg_predicted);
  //          System.out.println("Run time : " + avgTotalTime);
            System.out.println(getConfusionMatrixString(experts[i], matrix));
            
            //output accuracy
            f1.write(experts[i] + "-Accuracy:"+ avg_predicted + ",");
            f1.append("\r\n");
            
            //output fmeasure
            f2.write(experts[i] + "-fmeasure:"+ avg_fmeasure + ",");
            f2.append("\r\n");
            
            //output precision and recall
            f3.write(experts[i]+ "\r\n");
            for(int count = 0;count<precision.length;count++){
            	f3.write("precision:" + precision[count] + ",recall:" + recall[count]);
            	f3.append("\r\n");
            }
            
            s.clear();
            
        }
        f1.close();
        f2.close();
        f3.close();
        
//        if (DiscreteDBNAgent.proxy != null){
//        	DiscreteDBNAgent.proxy.disconnect();
//        }
        if (BNetRemoteDiscrete.proxy != null){
        	BNetRemoteDiscrete.proxy.disconnect();
        }
        if (NNetRemote.proxy != null){
        	NNetRemote.proxy.disconnect();
        }
        if (BNetRemoteOrderKDiscrete.proxy != null){
        	BNetRemoteOrderKDiscrete.proxy.disconnect();
        }
        if (NNetRemoteOrderKDiscrete.proxy != null){
        	NNetRemoteOrderKDiscrete.proxy.disconnect();
        }
        System.out.println("Finished Evaluation");	
    }

    
    public static String getConfusionMatrixString(String agentName, HashMap<String, HashMap<String, Integer>> confusionMatrix){
		String s = "";
		String header = "|          |";
		boolean firstIndex = true; 
		HashSet<String> titles = new HashSet<String>();
		titles.addAll(confusionMatrix.keySet());
		for (String s1 : confusionMatrix.keySet()){
			titles.addAll(confusionMatrix.get(s1).keySet());
		}
		for (String s1 : titles){
			s += "|" + String.format("%-10s", s1) + "|";
			for (String s2 : titles){
				if (firstIndex){
					header += String.format("%-10s", s2) + "|";
				}
				if (!confusionMatrix.containsKey(s1) || !confusionMatrix.get(s1).containsKey(s2)){
					s += String.format("%-10d", 0) + "|";
				}else{
					s += String.format("%-10d", confusionMatrix.get(s1).get(s2)) + "|";
				}
			}
			s += "\n";
			firstIndex = false;
		}
		s = s.substring(0, s.length() - 1);
		String label = "|" + String.format("%-" + (header.length() - 2) + "s", agentName + " Confusion Matrix") + "|";
		return label + "\n" + header + "\n" + s;
	}
    
  public static int[][] GetConfusionMatrix(HashMap<String, HashMap<String, Integer>> confusionMatrix){
	    HashSet<String> titles = new HashSet<String>();
	   
	    
		titles.addAll(confusionMatrix.keySet());
		for (String s1 : confusionMatrix.keySet()){
			titles.addAll(confusionMatrix.get(s1).keySet());
		}
		
		int [][] matrix = new int [titles.size()][titles.size()];
		int col = 0;
		int row = 0;
		for (String s1 :titles){
			for(String s2 : titles){
				if (!confusionMatrix.containsKey(s1) || !confusionMatrix.get(s1).containsKey(s2)){
					matrix[row][col] = 0;
				}else{
					matrix[row][col] = confusionMatrix.get(s1).get(s2);
				}
				row++;
			}
			col++;
			row = 0;
		}
	  return matrix;
  }  
  
  public static double sumOfRow(int [][] matrix, int row){
	  double sum = 0;
	  for(int i = 0;i<matrix.length;i++){
	  sum += matrix[row][i];
	  }
	  return sum;
  }
  
  public static double sumOfCol(int [][] matrix, int col){
	  double sum = 0;
	  for(int i = 0;i<matrix.length;i++){
	  sum += matrix[i][col];
	  }
	  return sum;
  }
}
