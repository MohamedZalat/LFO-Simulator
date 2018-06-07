/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.matlab;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import matlabcontrol.*;

/**
 *
 * @author santi
 */
public class NNetRemote {
    static int nextIndex = 1;
    
    int index = 0;
    public static MatlabProxy proxy = null;
    
    public NNetRemote(List<String> traces, int XSIZE, int YSIZE) {
        index = nextIndex++;
        String matlabCommand = "net" + index + " = learnNN([";;
        for(String trace:traces) {
             matlabCommand+="'" + trace + "';";
        }
        matlabCommand+="]," + XSIZE + "," + YSIZE + ");";

        MatlabProxyFactoryOptions options = null;
        MatlabProxyFactory factory = null;
        if (proxy==null) {
            options = new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).build();
            factory = new MatlabProxyFactory(options);
        }
        try {
            if (proxy==null) proxy = factory.getProxy();
            proxy.eval(matlabCommand);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public List<Double> run(List<Double> input) {
        String matlabCommand = "net" + index + "([";;
        for(Double i:input) {
             matlabCommand+=i + ";";
        }
        matlabCommand+="])";
        double []ret;
        try {
            ret = (double [])(proxy.returningEval(matlabCommand,1)[0]);
            List<Double> output = new LinkedList<Double>();
            for(double d:ret) {
                output.add(d);
            }
            return output;
        } catch (MatlabInvocationException ex) {
            ex.printStackTrace();
            return null;
        }        
    }

    
    public double[] run(double[] input) {
        String matlabCommand = "net" + index + "([";;
        for(Double i:input) {
             matlabCommand+=i + ";";
        }
        matlabCommand+="])";
        double []ret;
        try {
            return (double [])(proxy.returningEval(matlabCommand,1)[0]);
        } catch (MatlabInvocationException ex) {
            ex.printStackTrace();
            return null;
        }       
    }

    
    public static void main(String args[]) {
        List<String> traces = new LinkedList<String>();
        traces.add("traces-fourraydistance/trace-m0-SmartRandomExplorerAgent-nnet.txt");
        traces.add("traces-fourraydistance/trace-m1-SmartRandomExplorerAgent-nnet.txt");
        NNetRemote net = new NNetRemote(traces,8,5);
        
        double []input={0,0,0,0,0,0,0,0};
        double []output;
        
        output = net.run(input);
        
        for(double o:output) System.out.print(o + " ");
    }
}
