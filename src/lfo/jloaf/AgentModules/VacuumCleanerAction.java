package lfo.jloaf.AgentModules;
import org.jLOAF.action.AtomicAction;


/*
 * VacuumCleanerAction Object
 * Represents a VacuumCleaner Action, where it could one of the following {stand,up,right,left,down}
 * @author Ibrahim Ali Fawaz
 * @since 2017 May
 */
public class VacuumCleanerAction extends AtomicAction {
	public enum Actions {STAND("STAND"),UP("UP"),RIGHT("RIGHT"),LEFT("LEFT"),DOWN ("DOWN");
		private String action;
		 Actions(String a){
			action=a;
		}
		 
		 public String getAction(){
			 return action;
		 }
	
	
	};
	
	private static final long serialVersionUID = 1L;
	/*
	 * Constructor
	 * @param name the name associated to the action
	 */
	public VacuumCleanerAction(String name) {
		
		super(name);
		
	}
	

}
