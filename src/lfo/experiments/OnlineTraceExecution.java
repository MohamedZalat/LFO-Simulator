package lfo.experiments;

import lfo.agents.Agent;
import lfo.agents.discrete.*;
import lfo.agents.jloaf.DiscreteCBRAgent;
import lfo.agents.matlab.DiscreteDBNAgent;
import lfo.agents.python.DiscreteLSTMAgent;
import lfo.agents.python.DiscreteMLPAgent;
import lfo.matlab.MatlabPerceptionTranslator;
import lfo.matlab.MatlabTraceTranslator;
import lfo.simulator.*;
import lfo.simulator.objects.VacuumCleaner;
import lfo.simulator.perception.FourRayDistancePerception;
import lfo.simulator.perception.Perception;
import org.jdom.input.SAXBuilder;
import util.XMLWriter;

import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

public class OnlineTraceExecution {

        public static void main(String []args) throws Exception {
            int mapN = 0,agentN;
            List<State> maps = new LinkedList<State>();
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-empty4.xml").getRootElement()));
//
            maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "/maps/discreet-8x8.xml").getRootElement()));
            maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "/maps/discreet-8x8-2.xml").getRootElement()));
            maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "/maps/discreet-8x8-3.xml").getRootElement()));
            maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "/maps/discreet-8x8-4.xml").getRootElement()));
            maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "/maps/discreet-8x8-5.xml").getRootElement()));
            maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "/maps/discreet-32x32.xml").getRootElement()));
            maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "/maps/discreet-32x32-2.xml").getRootElement()));
//       maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-10x14-2.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-12x18-empty4.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-32x32-2.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-8x10-empty3.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/new_maps/discreet-8x16-empty3.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-32x32.xml").getRootElement()));
//        maps.add(new State(new SAXBuilder().build(Config.LOCAL_MAP + "maps/discreet-8x8-4.xml").getRootElement()));

            for(State map:maps) {
                if (mapN != 6) {
                    mapN++;
                    continue;
                }
                List<Agent> agents = new LinkedList<Agent>();
                List<Agent> experts = new LinkedList<Agent>();

                // Use four ray perception.
                Perception perception = new FourRayDistancePerception();

                // Add all experts to the experts list.
                // Need to have a maximum of one expert at a time in the case of pytorch models.
//                experts.add(new ZigZagAgent());
//                experts.add(new FixedSequenceAgent());
//                experts.add(new SmartRandomAgent());
//                experts.add(new WallFollowerAgent());
//                experts.add(new RandomAgent());
//                experts.add(new StraightLineAgent());
//                experts.add(new SmartStraightLineAgent());
                experts.add(new SmartRandomExplorerAgent());

                // Load all the learning traces:
                String DATA = Config.LOCAL_DATA;
                if (args.length >= 2){
                    DATA  = args[1];
                }
                System.out.println("Loading Data from : " + DATA);

                List<List<String>> MatlabTraceNames = new LinkedList<List<String>>();
                List<List<Trace>> traces = new LinkedList<List<Trace>>();
                List<List<LearningTrace>> learningTraces = new LinkedList<List<LearningTrace>>();

                for(int i = 0;i<experts.size();i++) {
                    List<Trace> tmp = new LinkedList<Trace>();
                    List<String> tmp2 = new LinkedList<String>();
                    List<String> tmp3 = new LinkedList<String>();
                    for(int j = 0;j<7;j++) {
                        //System.out.println("Loading " + i + " " + j);
                        //tmp.add(new Trace(new SAXBuilder().build(Config.LOCAL_DATA + "traces-fourraydistance2/trace-m" + j + "-" + experts[i] + ".xml").getRootElement()));
                        //tmp2.add(Config.LOCAL_DATA + "traces-fourraydistance2/trace-m" + j + "-" + experts[i] + ".txt");
                        //tmp3.add(Config.LOCAL_DATA + "traces-fourraydistance2/trace-m" + j + "-" + experts[i] + "-nnet.txt");
                        tmp.add(new Trace(new SAXBuilder().build(DATA + "/traces-fourraydistance/trace-m" + j + "-" + experts.get(i).name() + ".xml").getRootElement()));
                        tmp2.add(DATA + "/traces-fourraydistance/trace-m" + j + "-" + experts.get(i).name() + ".txt");
                    }
                    MatlabTraceNames.add(tmp2);
                    traces.add(tmp);
                    List<LearningTrace> tmp_lt = new LinkedList<LearningTrace>();
                    for(Trace t:tmp) tmp_lt.add(new LearningTrace(t,perception));
                    learningTraces.add(tmp_lt);
                }

//                // Train and add all the DBN agents.
//                for(int i = 0;i<learningTraces.size();i++) {
//
//                    List<String> l = new LinkedList<String>();
//
//                    // Setup LfoDBN.
//                    l.addAll(MatlabTraceNames.get(i));
//                    l.remove(mapN);
//                    Agent agent = DiscreteDBNAgent.getLfODBNFromMATLAB(l, Config.LOCAL_MAP + "/Matlab/LfODBN-EVAL_" + experts.get(i).name() + "_" + mapN, 8);
//                    agents.add(agent);
//                }
//
//                // Train and add all the TB agents.
//                for(int i = 0;i<learningTraces.size();i++) {
//
//                    List<String> l = new LinkedList<String>();
//
//                    // Setup TB.
//                    l.addAll(MatlabTraceNames.get(i));
//                    l.remove(mapN);
//                    Agent agent = DiscreteCBRAgent.getTBFromJLOAF(l);
//                    agents.add(agent);
//                }
//
//                // Train and add all the NOrdered agents.
//                for(int i = 0;i<learningTraces.size();i++) {
//
//                    List<String> l = new LinkedList<String>();
//
//                    // Setup NOrdered.
//                    l.addAll(MatlabTraceNames.get(i));
//                    l.remove(mapN);
//                    // N is set to 11 because Sacha set it to 11.
//                    Agent agent = DiscreteCBRAgent.getNOrderedFromJLOAF(l, 11);
//                    agents.add(agent);
//                }

                // Train and add all the LSTM agents.
                for(int i = 0;i<learningTraces.size();i++) {

                    List<String> l = new LinkedList<String>();

                    // Setup NOrdered.
                    l.addAll(MatlabTraceNames.get(i));
                    l.remove(mapN);
                    Agent agent = new DiscreteLSTMAgent(l);
                    agents.add(agent);
                }

                // Train and add all the MLP agents.
                for(int i = 0;i<learningTraces.size();i++) {

                    List<String> l = new LinkedList<String>();

                    // Setup NOrdered.
                    l.addAll(MatlabTraceNames.get(i));
                    l.remove(mapN);
                    Agent agent = new DiscreteMLPAgent(l);
                    agents.add(agent);
                }




                // Level 1:
                //agents.add(new FixedSequenceAgent());
                //agents.add(new RandomAgent());

                // Level 2:
                // agents.add(new SmartRandomAgent());
                //   agents.add(new WallFollowerAgent());
                //  agents.add(new SmartWallFollowerAgent());

                // Level 3
                //agents.add(new FrequencyAgent2());
//                agents.add(new ZigZagAgent());
                //    agents.add(new DirtWallAgent());
                //  agents.add(new StraightLineAgent());
                //agents.add(new SmartStraightLineAgent());

                // Level 3
//            agents.add(new PauseEveryN(3,new SmartRandomAgent()));
//            agents.add(new PauseEveryN(3,new SmartStraightLineAgent()));

                // Level 3
//            agents.add(new RandomExplorerAgent());
                //agents.add(new SmartRandomExplorerAgent());

                // Level 4: (using different perceptions for expert and learner)
                // ...

//            Perception perception = new WindowPerception();

                agentN = 0;
                for(Agent agent:agents) {
                    // The expert has a 1:1 correspondence with the trained models.
                    Agent expert = experts.get(agentN % experts.size());
                    String fileName = Config.LOCAL_DATA + "/debug-traces/trace-m" + mapN + "-" + agent.name()
                            + "-" + expert.name();

                    // This generates a trace, while saving it to Matlab format:
                    Trace t = generateLearningAgentTrace(map,agent, expert,1000,perception, mapN);
                    FileWriter fw = new FileWriter(fileName + ".xml");
                    t.toxml(new XMLWriter(fw));
                    fw.close();

                    generateExpertTrace(map, agent, expert, 1000, perception, mapN);

                    System.out.println("This is map number " + mapN + " and expert " + expert.name());

                    MatlabTraceTranslator.translateToMatlab(t, perception, fileName+ ".txt");
                    // MatlabTraceTranslator.translateToMatlabForNNet(t, perception, fileName+ "-nnet.txt");

//                JFrame w = TraceVisualizer.newWindow(agent.getClass().getSimpleName(), 800, 600, t);
//                w.setVisible(true);

                    agentN++;
                }
                mapN++;
            }
            System.out.println("Experiment completed!");
        }

        public static Trace generateLearningAgentTrace(State s, Agent agent, Agent expert, int maxCycles,
                                                       Perception perception, int mapN) throws Exception {
            return generateAgentTrace(s,agent, expert,maxCycles,perception, false, mapN);
        }

        public static Trace generateExpertTrace(State s, Agent agent, Agent expert, int maxCycles,
                                                Perception perception, int mapN) throws Exception {
            return generateAgentTrace(s,agent, expert,maxCycles,perception, true, mapN);
        }

        public static Trace generateAgentTrace(State s, Agent agent, Agent expert, int maxCycles, Perception perception,
                                               Boolean expertMode, int mapN) throws Exception {
            State state = (State)s.clone();
            int vacuumID = state.get(VacuumCleaner.class).getID();
            Trace t = new Trace(state,vacuumID);

            // Create a new Stats object.
            Stats stats;
            if (!expertMode)
                 stats = new Stats(expert.statsFileName() + "_agent_" + agent.statsFileName() + "_m" + mapN);
            else {
                stats = new Stats(expert.statsFileName() + "_expert_" + agent.statsFileName()+ "_m" + mapN);

                // Use the expert as the agent
                agent = expert;
            }

            boolean first = true;

            for(int time = 0;time<maxCycles;time++) {
                Perception p = perception.perceive(state, state.get(vacuumID));
                // We can stringify this and then add it to our Stats object. This is
                // the feature vector with 8 features.
                List<Integer> X = MatlabPerceptionTranslator.translateToMatlabInteger(p);
                String env = "";
                for(Integer i: X) {
                    env += i;
                }
                stats.logEnv(env);

                // Here we pass the perception to the learning agent first then the expert.
                Action a = agent.cycle(vacuumID, p, time);
                Action e = expert.cycle(vacuumID, p, time);
                String agentAction = "nothing";
                String expertAction = "nothing";

                // We set the action to perform the action on the learning agent.
                if (a!=null) {
                    a.setObjectID(vacuumID);
                    agentAction = a.getName();
                }

                if (e != null) {
                    expertAction = e.getName();
                }

                // Add the action of the learning agent and the expert to the stats object.
                 stats.logAct(agentAction, expertAction);

                // We add the action of the learning agent to its trace.
                t.addEntry(new TraceEntry(time, (State)state.clone(), vacuumID, a));

                // We apply the action of the learning agent to the state of the vacuum
                // cleaner environment to update the perception of the agent.
                state.cycle(a, 1.0);
//            System.out.println("cycle " + time);

            }

            // Save the stats of the agent.
            stats.save();

            return t;
        }
}
