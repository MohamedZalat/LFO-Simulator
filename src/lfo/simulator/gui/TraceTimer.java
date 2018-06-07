package lfo.simulator.gui;

import java.util.TimerTask;

public class TraceTimer extends TimerTask{
	
	private TraceVisualizer visual;
	
	public TraceTimer(TraceVisualizer visual){
		this.visual = visual;
	}
	

	@Override
	public void run() {
		this.visual.increaseStep();
	}

}
