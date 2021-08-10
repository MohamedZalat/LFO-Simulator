package lfo.jloaf.AgentModules;

import org.jLOAF.Agent;
import org.jLOAF.action.Action;
import org.jLOAF.action.AtomicAction;
import org.jLOAF.inputs.Input;

/*
 * VacuumCleanerAgent Object
 * 
 * 
 * @author Ibrahim Ali Fawaz
 * @since 2017 May
 */
public class VacuumCleanerAgent extends Agent{
	
	
	/*
	 * constructor 
	 * 
	 * @param casebase the casebase with all the cases the agent has observed.
	 */
	public VacuumCleanerAgent() {
		super(null,null,null,null);
		
		this.mc = new VacuumCleanerMotorControl();
		
		this.p = new VacuumCleanerPerception();
	
	}
	
	

	@Override
	public Action run(Input input) {
		AtomicAction a = (AtomicAction) this.r.selectAction(input);
		return (VacuumCleanerAction) a;
	}

	
	

}
