package lfo.agents.discrete;

import lfo.agents.Agent;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;

public class DirtWallAgent extends Agent {
	private int[] objects;
	private String[] directions;
	private int rights;
	private int lefts;
	private boolean goingUp;
	private boolean goingLR;
	private int index=1;
	private int hit=0;
	
	
	public DirtWallAgent(){
		objects = new int[8];
		String[] k ={"u","d","l","r","du","dd","dl","dr"};
		directions=k;
		lefts=0;
		rights=0;
		goingUp=true;
		goingLR= false;
		
	}
	public Action cycle(int id,Perception p, double timeStep) {
			for(int i =0; i<8;i++){
				objects[i]=p.getInteger(directions[i]);
			}

			hit++;
		return decideNextAction(id);
	}
	private Action decideNextAction(int id) {
		Action a=null;
			if(goingLR){
				goingLR=false;
				a=decideNextDirection(id);	
			}
			
			if(a!=null){
				return a;
			}
			goingLR=true;
			if(index>0){
				
				return new Action("up",id);
			}else {
				return new Action("down",id);
			}
		
		
	}
	private Action decideNextDirection(int id) {
			
		if(seesDirt() && !hits()){
			lefts++;
			if(lefts%20000==0){
			return new Action("left",id);
			}
		}else if(seesWall() && !hits()){
			rights++;
			if(rights%20000==0){
			return new Action("right",id);
			}
		}else {
			
			index=index*-1;
			
		
		}
			
		if(hit%2==0){
			/*
			if(lefts>rights){
				return new Action("right",id);
			}else{
				return new Action("left",id);
			}
			*/
			if(lefts%2!=0){
				return new Action("right",id);
			}else{
				return new Action("left",id);
			}
		}
		return null;
		
		
	}
	private boolean seesWall() {
		
			if(objects.toString().equals("00001101")){
				return true;
			}
		
		return false;
	}
	private boolean hits() {
		if((objects[0]==0 && objects[4]==0)||(objects[1]==0 &&objects[5]==0)){
			return true;
		}
		return false;
	}
	private boolean seesDirt() {
		
			if(objects.toString().equals("00011110")){
				return true;
			}
		
		return false;
	}

}
