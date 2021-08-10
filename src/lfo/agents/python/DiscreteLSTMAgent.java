/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.agents.python;

import lfo.agents.Agent;
import lfo.matlab.MatlabPerceptionTranslator;
import lfo.python.PytorchHttpClient;
import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;
import util.Sampler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author santi
 */
public class DiscreteLSTMAgent extends Agent {
    private int time_step = 0;
    private PytorchHttpClient proxy;

    public DiscreteLSTMAgent(List<String> fileNames) throws FileNotFoundException, IOException {
        this.proxy = new PytorchHttpClient("http://localhost:5000/lstm/train",
                "http://localhost:5000/lstm/predict",
                "http://localhost:5000/lstm/reset");

        // Reset the network.
        this.proxy.reset();
        if (this.proxy.train(fileNames)) {
            System.out.println("Successfully trained LSTM.");
        }
        else {
            System.out.println("Failed to train LSTM.");
        }
    }

    public void start()
    {
        time_step = 0;
    }


    public Action cycle(int id,Perception p, double timeStep) {
        Action l[]={null,new Action("up",id),new Action("right",id),new Action("left",id),new Action("down",id)};


        // Get the matlab perception to be consistent with the log files.
        List<Integer> X = MatlabPerceptionTranslator.translateToMatlabInteger(p);

        int action = this.proxy.predict(X);
        time_step++;

        return l[action];
    }

}
