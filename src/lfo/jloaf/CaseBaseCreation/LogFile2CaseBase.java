package lfo.jloaf.CaseBaseCreation;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.jLOAF.casebase.Case;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.inputs.*;
import org.jLOAF.sim.AtomicSimilarityMetricStrategy;
import org.jLOAF.sim.ComplexSimilarityMetricStrategy;
import org.jLOAF.sim.SimilarityMetricStrategy;
import org.jLOAF.sim.StateBasedSimilarity;
import org.jLOAF.sim.StateBased.KOrderedSimilarity;
import org.jLOAF.sim.StateBased.OrderedSimilarity;
import org.jLOAF.sim.atomic.EuclideanDistance;
import org.jLOAF.sim.complex.Mean;
import org.jLOAF.sim.complex.WeightedMean;
import org.jLOAF.weights.SimilarityWeights;

import lfo.jloaf.AgentModules.VacuumCleanerAction;
import lfo.jloaf.AgentModules.VacuumCleanerAction.Actions;
import lfo.jloaf.AgentModules.VacuumCleanerInput;


/*
 * LogFile2CaseBase Object
 * used to convert logfile to .cb files
 * @author Ibrahim Ali Fawaz
 * @since 2017 May
 */
public class LogFile2CaseBase {
	
	protected static AtomicSimilarityMetricStrategy atomicStrategy = new EuclideanDistance();
	protected ComplexSimilarityMetricStrategy complexStrategy = new Mean();
	protected static ComplexSimilarityMetricStrategy vacumStrategy = new Mean();
	protected StateBasedSimilarity stateBasedSimilarity;

	public LogFile2CaseBase(StateBasedSimilarity stateBasedSim) {
		this.stateBasedSimilarity = stateBasedSim;
	}
	
	
	/*
	 * outputs the casebase passed to it in a .cb file with the name also passed to it
	 * @param cb the casebase to be saved
	 * @param outputFile the file in which the casebase will be saved
	 */
	private void saveCaseBase(CaseBase cb, String outputFile) {
		
		CaseBase.save(cb, outputFile);
	}


	/*
	 * creates a casebase from a logfile passed to it.
	 * @param file a file to be parsed to a casebase
	 * @return a casebase created from the logfile.
	 */
	public String parseLogFile(String file1,String file2){
		File file= new File(file1);
		CaseBase cb = new CaseBase();
		try {
			Scanner sc = new Scanner(file);
			int index=0;
			double[] entry=new double[9];
			
			while(sc.hasNextLine()){
				
				
				try {
						
				entry[index%9] = sc.nextDouble();
				
				if(index%9==8){
					
					this.createCase(cb,entry);
				}
				
				index++;
				
				
				}catch (NoSuchElementException e){
					break;
				}
				
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			this.saveCaseBase(cb, file2);
			System.out.println("done with creating one caseBase");
		return file2;
	}


	/*
	 * creates a case from the double values passed to it and then adds it to the casebase
	 * @param cb2 the casebase of the observed expert
	 * @param entry an array of double values represent the parameters of the actions and the inputs
	 */
	public void createCase(CaseBase cb2, double[] entry) {
		VacuumCleanerAction act= new VacuumCleanerAction(String.valueOf(entry[8]));
		VacuumCleanerInput vci = new VacuumCleanerInput(VacuumCleanerInput.NAME,vacumStrategy);
		
			//ComplexInput ci =new ComplexInput("Input",complexStrategy);
			 AtomicInput a1 = new AtomicInput("Oup",new Feature(entry[0]),atomicStrategy);
			 AtomicInput a2 = new AtomicInput("Dup",new Feature(entry[1]),atomicStrategy);
			 AtomicInput a3 = new AtomicInput("Oright",new Feature(entry[2]),atomicStrategy);
			 AtomicInput a4 = new AtomicInput("Dright",new Feature(entry[3]),atomicStrategy);
			 AtomicInput a5 = new AtomicInput("Odown",new Feature(entry[4]),atomicStrategy);
			 AtomicInput a6 = new AtomicInput("Ddown",new Feature(entry[5]),atomicStrategy);
			 AtomicInput a7 = new AtomicInput("Oleft",new Feature(entry[6]),atomicStrategy);
			 AtomicInput a8 = new AtomicInput("Dleft",new Feature(entry[7]),atomicStrategy);
			 vci.add(a1);
			 vci.add(a2);
			 vci.add(a3);
			 vci.add(a4);
			 vci.add(a5);
			 vci.add(a6);
			 vci.add(a7);
			 vci.add(a8);
			 //vci.add(ci);
			//System.out.println(vci.getChildNames().size());
		Case c =new Case(vci,act);
		
		cb2.createThenAdd(vci,act,stateBasedSimilarity);
		
		
		
		
		
	}

	public static Input translateMatlabToStateBasedInput(Case latestCase, List<Integer> perception, StateBasedSimilarity stateBasedSimilarity) {
		StateBasedInput stateBasedInput = new StateBasedInput("StateBasedSim", stateBasedSimilarity);
		VacuumCleanerInput vci = new VacuumCleanerInput(VacuumCleanerInput.NAME,vacumStrategy);

		//ComplexInput ci =new ComplexInput("Input",complexStrategy);
		AtomicInput a1 = new AtomicInput("Oup",new Feature(perception.get(0)),atomicStrategy);
		AtomicInput a2 = new AtomicInput("Dup",new Feature(perception.get(1)),atomicStrategy);
		AtomicInput a3 = new AtomicInput("Oright",new Feature(perception.get(2)),atomicStrategy);
		AtomicInput a4 = new AtomicInput("Dright",new Feature(perception.get(3)),atomicStrategy);
		AtomicInput a5 = new AtomicInput("Odown",new Feature(perception.get(4)),atomicStrategy);
		AtomicInput a6 = new AtomicInput("Ddown",new Feature(perception.get(5)),atomicStrategy);
		AtomicInput a7 = new AtomicInput("Oleft",new Feature(perception.get(6)),atomicStrategy);
		AtomicInput a8 = new AtomicInput("Dleft",new Feature(perception.get(7)),atomicStrategy);
		vci.add(a1);
		vci.add(a2);
		vci.add(a3);
		vci.add(a4);
		vci.add(a5);
		vci.add(a6);
		vci.add(a7);
		vci.add(a8);

		stateBasedInput.setInput(vci);
		stateBasedInput.setCase(latestCase);

		return stateBasedInput;
	}
	

}
