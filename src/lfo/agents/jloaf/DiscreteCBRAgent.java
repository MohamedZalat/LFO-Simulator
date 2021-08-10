/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.agents.jloaf;

import lfo.agents.Agent;
import lfo.jloaf.AgentModules.VacuumCleanerAgent;
import lfo.jloaf.CaseBaseCreation.LogFile2CaseBase;
import lfo.matlab.MatlabPerceptionTranslator;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import org.jLOAF.casebase.Case;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.inputs.Input;
import org.jLOAF.reasoning.TBReasoning;
import org.jLOAF.reasoning.WeightedKNN;
import org.jLOAF.sim.StateBased.KOrderedSimilarity;
import org.jLOAF.sim.StateBasedSimilarity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Moe
 */
public class DiscreteCBRAgent extends Agent {

    int time_step = 0;

    private VacuumCleanerAgent agent;
    private String modelName;
    private Case latestCase;
    private StateBasedSimilarity stateBasedSimilarity;

    public static DiscreteCBRAgent getNOrderedFromJLOAF(List<String> traces, int n) throws FileNotFoundException, IOException {
        StateBasedSimilarity stateBasedSimilarity = new KOrderedSimilarity(n);
        CaseBase cb = getCaseBaseFromTraces(traces, stateBasedSimilarity);

        VacuumCleanerAgent agent = new VacuumCleanerAgent();
        agent.setR(new WeightedKNN(5, cb));

        return new DiscreteCBRAgent("NOrderedAgent", agent, stateBasedSimilarity);
    }


    public static DiscreteCBRAgent getTBFromJLOAF(List<String> traces) throws FileNotFoundException, IOException {
        StateBasedSimilarity stateBasedSimilarity = new KOrderedSimilarity(1);
        CaseBase cb = getCaseBaseFromTraces(traces, stateBasedSimilarity);

        VacuumCleanerAgent agent = new VacuumCleanerAgent();
        agent.setR(new TBReasoning(cb));

        return new DiscreteCBRAgent("TBAgent", agent, stateBasedSimilarity);
    }

    public static CaseBase getCaseBaseFromTraces(List<String> traces, StateBasedSimilarity stateBasedSimilarity) {
        LogFile2CaseBase lfcb = new LogFile2CaseBase(stateBasedSimilarity);
        String[] outputs = new String[traces.size()];
        List<CaseBase> listOfCaseBases = new LinkedList<CaseBase>();

        String outputFile ="vcb";
        int i=0;

        for(String s:traces){
            outputs[i]=lfcb.parseLogFile(s,outputFile+i+".cb");
            i++;
        }

        for(String cbname : outputs) {
            listOfCaseBases.add(CaseBase.load(cbname));
        }

        CaseBase cb = new CaseBase();
        cb.addListOfCaseBases(listOfCaseBases);

        return cb;
    }

    public DiscreteCBRAgent(String modelName, VacuumCleanerAgent agent, StateBasedSimilarity stateBasedSimilarity) throws FileNotFoundException, IOException {
        this.modelName = modelName;
        this.agent = agent;
        this.stateBasedSimilarity = stateBasedSimilarity;
        this.latestCase = null;
    }


    public void start()
    {
        time_step = 0;
    }


    public Action cycle(int id,Perception p, double timeStep) {
        Action l[]={null,new Action("up",id),new Action("right",id),new Action("left",id),new Action("down",id)};

        // Get the matlab perception to be consistent with the log files.
        List<Integer> X = MatlabPerceptionTranslator.translateToMatlabInteger(p);

        // Add the current environment state to the input trace.
        Input stateBasedInput = LogFile2CaseBase.translateMatlabToStateBasedInput(this.latestCase, X, this.stateBasedSimilarity);

        // Get the action chosen by the reasoner.
        org.jLOAF.action.Action action = this.agent.run(stateBasedInput);

        // Create the latest case by adding the stateBasedInput and the action associated with it.
        this.latestCase = new Case(stateBasedInput, action);

        // Increment the time step.
        time_step++;

        // We subtract 1 to convert the action from matlab/jloaf to get the index of the action.
        return l[(int) Double.parseDouble(action.getName()) - 1];
    }

    public String name() {
        return modelName;
    }

}
