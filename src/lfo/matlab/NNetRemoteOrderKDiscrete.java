/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.matlab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import matlabcontrol.*;
import util.Sampler;

/**
 *
 * @author santi
 */
public class NNetRemoteOrderKDiscrete {
    static int nextIndex = 1;
    
    int index = 0;
    int XSIZE = 8, YSIZE = 5, ORDER = 2;
    public static MatlabProxy proxy = null;
    List<List<Double>> lastInputs = new LinkedList<List<Double>>();
    List<Integer> lastOutputs = new LinkedList<Integer>();
    
    public NNetRemoteOrderKDiscrete(List<String> traces, int a_XSIZE, int a_YSIZE, int a_ORDER) {
        index = nextIndex++;
        XSIZE = a_XSIZE;
        ORDER = a_ORDER;
        String matlabCommand = "nnet" + index + " = learnNNOrderK([";;
        for(String trace:traces) {
             matlabCommand+="'" + trace + "';";
        }
        matlabCommand+="]," + XSIZE + "," + YSIZE + "," + ORDER + ",0);";

//        System.out.println(matlabCommand);
        
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
    
    
    public int run(List<Double> input) {                
        double []ret;
        
        String matlabCommand = "nnet" + index + "([";;
        for(int i = 0;i<ORDER-1;i++) {
            if (lastInputs.size()>i) {
                for(int j = 0;j<XSIZE;j++) 
                    matlabCommand+= lastInputs.get(i).get(j) + ";";
                for(int j = 0;j<YSIZE;j++) {
                    if (lastOutputs.get(i) == j) {
                        matlabCommand+= 1 + ";";
                    } else {
                        matlabCommand+= 0 + ";";
                    }
                }
            } else {
                for(int j = 0;j<XSIZE;j++) 
                    matlabCommand+= input.get(j) + ";";
                for(int j = 0;j<YSIZE;j++) 
                    matlabCommand+= 0 + ";";
            }

        }
        for(int i = 0;i<XSIZE;i++) {
            matlabCommand+= input.get(i) + ";";
        }
        matlabCommand+="])";   
        
//        System.out.println(matlabCommand);
        
        try {
            ret = (double [])(proxy.returningEval(matlabCommand,1)[0]);
            List<Double> output = new LinkedList<Double>();
            for(double d:ret) {
                output.add(Math.max(0,d));
            }
//            System.out.println(output);
//            int action = Sampler.weighted(output);
            int action = Sampler.max(output);
            
            lastInputs.add(0,input);            
            lastOutputs.add(0,action);
            
            return action;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }        
    }

    
    public void replaceLastAction(int a) {
//        System.out.println("replaceLastAction, before: " + lastOutputs);
        lastOutputs.remove(0);
        lastOutputs.add(0,a);
//        System.out.println("replaceLastAction, after: " + lastOutputs);
    }
    
    
    public static void main(String args[]) {
        List<String> traces = new LinkedList<String>();
        traces.add("traces-fourraydistance/trace-m0-StraightLineAgent.txt");
        traces.add("traces-fourraydistance/trace-m1-StraightLineAgent.txt");
        NNetRemoteOrderKDiscrete net = new NNetRemoteOrderKDiscrete(traces,8,5,2);
        
        List<Double> input = new LinkedList<Double>();
        for(int i = 0;i<9*2;i++) input.add(1.0);
        
        int output = net.run(input);
        System.out.print("Output: " + output);
    }
}
