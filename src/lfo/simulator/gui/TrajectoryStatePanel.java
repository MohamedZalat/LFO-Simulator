/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.simulator.gui;

import lfo.simulator.Map;
import lfo.simulator.State;
import lfo.simulator.objects.Dirt;
import lfo.simulator.objects.Obstacle;
import lfo.simulator.objects.PhysicalObject;
import lfo.simulator.objects.VacuumCleaner;
import lfo.simulator.objects.VacuumCleanerTurnable;
import lfo.simulator.shapes.Circle;
import lfo.simulator.shapes.Shape;
import lfo.simulator.shapes.Square;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.JPanel;
import lfo.simulator.objects.*;

/**
 *
 * @author santi
 */
public class TrajectoryStatePanel extends StatePanel {

    List<Point2D.Double> m_trajectory;  // The trajectory to be drawn
    int m_first;    // first element of the trajectory to display
    int m_last;     // last element to display
    
    private String[] resultActions;
    private SettingsPanel panel;
    public TrajectoryStatePanel(State s, List<Point2D.Double> trajectory, int first, int last) {
        super(s);
        m_trajectory = trajectory;
        m_first = first;
        m_last = last;
    }
    
    public void addResultActions(String []actions){
    	this.resultActions = actions;
    }
    
    public void setLast(int last) {
        m_last = last;
    }
    
    public void setSettingsPanel(SettingsPanel panel){
    	this.panel = panel;
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        int errors = 0;
        for(int i = m_first;i<m_last;i++) {
            g.setColor(Color.BLUE);
            Point2D.Double p1 = null;
            Point2D.Double p2 = null;
            for(double x = 0;x<=dx;x++) {
            	p1 = m_trajectory.get(i);
            	p2 = m_trajectory.get(i+1);
            	if (panel == null || !panel.hideExper()){
            		g.drawLine(toScreenX(p1.x), toScreenY(p1.y), toScreenX(p2.x), toScreenY(p2.y));
            	}
            }
            if (resultActions != null && resultActions.length >= dx){
            	if (panel != null){
            		if (!panel.getShowErrors()){
            			continue;
            		}
            	}
            	g.setColor(Color.RED);
            	Point2D.Double p3 = null;
            	int a = (int)Double.parseDouble(resultActions[i]);
            	switch(a){
            	case 0: // Stand
            		p3 = p1;
            		break;
            	case 1: // Up
            		p3 = new Point2D.Double(p1.x, p1.y - 1);
            		break;
            	case 2: // Right
            		p3 = new Point2D.Double(p1.x + 1, p1.y);
            		break;
            	case 3: //Left
            		p3 = new Point2D.Double(p1.x - 1, p1.y);
            		break;
            	case 4: // Down
            		p3 = new Point2D.Double(p1.x, p1.y + 1);
            		break;
            	}
            	if (!p2.equals(p3)){
            		errors++;
            		g.drawLine(toScreenX(p1.x), toScreenY(p1.y), toScreenX(p3.x), toScreenY(p3.y));
            	}
            	g.setColor(Color.BLUE);
            }
        }
        if (panel != null){
        	panel.setCounter(errors);
        }
    }
}
