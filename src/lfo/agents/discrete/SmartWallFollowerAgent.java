/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.agents.discrete;

import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import lfo.simulator.perception.WindowPerception;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import lfo.agents.Agent;
import lfo.simulator.perception.FourRayDistancePerception;

/**
 *
 * @author santi
 */

// LEVEL 3 agent with bounded internal state (IOHMM)
public class SmartWallFollowerAgent extends Agent {
	
	String sequence[] = {"up", "up", "right", "right", "down", "down", "left", "left"};
	int time_step = 0;
	
	int dirtCounter = 0;
	
	boolean same_dirt_up = false;
	boolean same_dirt_down = false;
	boolean same_dirt_left = false;
	boolean same_dirt_right = false;
    
    public Action cycle(int id,Perception p, double timeStep) {
//        Action l[]={new Action("up",id),new Action("down",id),new Action("left",id),new Action("right",id),null};
        // Are there walls around?
     	
    	if (dirtCounter <2){
        boolean up = false;
        boolean down = false;
        boolean left = false;
        boolean right = false;
        boolean upDirt = false;
        boolean downDirt = false;
        boolean leftDirt = false;
        boolean rightDirt = false;

        if (p instanceof FourRayDistancePerception) {
            if (p.getInteger("du")==0 && p.getInteger("u")==0) up = true;
            if (p.getInteger("dd")==0 && p.getInteger("d")==0) down = true;
            if (p.getInteger("dl")==0 && p.getInteger("l")==0) left = true;
            if (p.getInteger("dr")==0 && p.getInteger("r")==0) right = true;
            if (p.getInteger("u") == 1) upDirt = true;
            if (p.getInteger("d") == 1) downDirt = true;
            if (p.getInteger("l") == 1) leftDirt = true;
            if (p.getInteger("r") == 1) rightDirt = true;
        }
        
        if (upDirt) {
        	if (!same_dirt_up){
        		same_dirt_up = true;
        		dirtCounter++;
        	}
        	return new Action("up",id);
        }
        if (rightDirt){
        	if (!same_dirt_right){
        		same_dirt_right = true;
        		dirtCounter++;
        	}
        	return new Action("right",id);
        }
        if (downDirt) {
        	if (!same_dirt_down){
        		same_dirt_down = true;
        		dirtCounter++;
        	}
        	return new Action("down",id);
        }
        if (leftDirt) {
        	if (!same_dirt_left){
        		same_dirt_left = true;
        		dirtCounter++;
        	}
        	return new Action("left",id);
        }
        same_dirt_up = false;
        same_dirt_down = false;
    	same_dirt_left = false;
    	same_dirt_right = false;
        
        if (!up && !right && !down && !left) return new Action("right",id);
        if (!up && !right && !down &&  left) return new Action("up",id);
        if (!up && !right &&  down && !left) return new Action("left",id);
        if (!up && !right &&  down &&  left) return new Action("up",id);
        if (!up &&  right && !down && !left) return new Action("down",id);
        if (!up &&  right && !down &&  left) return new Action("down",id);
        if (!up &&  right &&  down && !left) return new Action("left",id);
        if (!up &&  right &&  down &&  left) return new Action("up",id);
        if ( up && !right && !down && !left) return new Action("right",id);
        if ( up && !right && !down &&  left) return new Action("right",id);
        if ( up && !right &&  down && !left) return new Action("right",id);
        if ( up && !right &&  down &&  left) return new Action("left",id);
        if ( up &&  right && !down && !left) return new Action("down",id);
        if ( up &&  right && !down &&  left) return new Action("down",id);
        if ( up &&  right &&  down && !left) return new Action("left",id);
        if ( up &&  right &&  down &&  left) return null;
        return null;
    	}
    	else
    	{
    		Action a = new Action(sequence[time_step%sequence.length],id);
            time_step++;
            return a;
    	}
    }
}
