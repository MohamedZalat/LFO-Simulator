/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.simulator.gui;

import lfo.simulator.Action;
import lfo.simulator.State;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author santi
 */
public class ActionsPanel extends JPanel {

    public class LFOActionListener implements ActionListener {
        Action action;
        DiscreetTraceGenerator traceGenerator;

        LFOActionListener(Action a, DiscreetTraceGenerator tg) {
            action = a;
            traceGenerator = tg;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                traceGenerator.submitAction(action);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public ActionsPanel(List<Action> actions, DiscreetTraceGenerator tg) {

        setAlignmentX(CENTER_ALIGNMENT);
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, 200));

        for(Action a:actions) {
            if (a==null) {
                JButton button = new JButton("No Action");
                button.addActionListener(new LFOActionListener(a, tg));
                add(button);
            } else {
                JButton button = new JButton(a.getName());
                button.addActionListener(new LFOActionListener(a, tg));
                add(button);
            }
        }
    }


}
