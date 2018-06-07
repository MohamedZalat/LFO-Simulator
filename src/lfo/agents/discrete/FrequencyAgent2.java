package lfo.agents.discrete;

import lfo.agents.Agent;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import lfo.simulator.perception.WindowPerception;

public class FrequencyAgent2 extends Agent {
	boolean horizontal_state = false;
	int leftCounter = 0;
	
	public Action cycle(int id,Perception p, double timeStep) {
	  boolean up = false;
      boolean down = false;
      boolean left = false;
      boolean right = false;

      if (p instanceof WindowPerception) {
          if (p.getInteger("u") == 0) up = true;
          if (p.getInteger("d") == 0) down = true;
          if (p.getInteger("l") == 0) left = true;
          if (p.getInteger("r") == 0) right = true;
      } else {
          if (p.getInteger("du")==0 && p.getInteger("u")==0) up = true;
          if (p.getInteger("dd")==0 && p.getInteger("d")==0) down = true;
          if (p.getInteger("dl")==0 && p.getInteger("l")==0) left = true;
          if (p.getInteger("dr")==0 && p.getInteger("r")==0) right = true;
      }
      
	  if (p.getInteger("dl")==0 && p.getInteger("l") == 1 || p.getInteger("dl")==1 && p.getInteger("l") == 1) leftCounter++;
	  //if (p.getInteger("dr")==0 && p.getInteger("r") == 0 && p.getInteger("du")==1 && p.getInteger("u") == 0 && p.getInteger("dl")==1 && p.getInteger("l") == 0 && p.getInteger("dd")==1 && p.getInteger("d") == 0) rightCounter++;
	  //if (p.getInteger("dr")==1 && p.getInteger("r") == 0 && p.getInteger("du")==1 && p.getInteger("u") == 0 && p.getInteger("dl")==0 && p.getInteger("l") == 0 && p.getInteger("dd")==1 && p.getInteger("d") == 0) rightCounter++;
      
	  if(up & horizontal_state){
    	  horizontal_state = false;
    	  return leftOrRight(id);
      }else if(down & !horizontal_state){
    	  horizontal_state = true;
    	  return leftOrRight(id);
      }else if(horizontal_state){
    	  return new Action("up",id);	
      }else if(!horizontal_state){
    	  return new Action("down",id);
      }
      return new Action("stand",id);
	}

private Action leftOrRight(int id){
	if(leftCounter%2==0){
		  return new Action("left",id);
	  }else{
		  return new Action("right",id);
	  }
}
}
