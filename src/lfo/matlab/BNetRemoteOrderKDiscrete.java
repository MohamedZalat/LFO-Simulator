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
public class BNetRemoteOrderKDiscrete {
    static int nextIndex = 1;
    
    int index = 0;
    int XSIZE = 8, ORDER = 2;
    public static MatlabProxy proxy = null;
    List<List<Double>> lastInputs = new LinkedList<List<Double>>();
    List<Integer> lastOutputs = new LinkedList<Integer>();
    
    public BNetRemoteOrderKDiscrete(List<String> traces, int a_XSIZE, int a_ORDER) {
        index = nextIndex++;
        XSIZE = a_XSIZE;
        ORDER = a_ORDER;
        String matlabCommand = "[bnet" + index + ",bnetengine" + index + "] = learnBNetOrderK([";;
        for(String trace:traces) {
             matlabCommand+="'" + trace + "';";
        }
        matlabCommand+="]," + XSIZE + "," + 1 + "," + ORDER + ",1);";

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
//        System.out.println("BNetRemove.run( " + input + " )");
        try {
            proxy.eval("evidence = cell(1," + (XSIZE + 1)*ORDER + ");");
            
            for(int i = 0;i<ORDER-1;i++) {
                if (lastInputs.size()>i) {
                    for(int j = 0;j<XSIZE;j++) 
                        proxy.eval("evidence{" + ((ORDER - (i + 2))*(XSIZE+1) + j + 1) + "} = " + lastInputs.get(i).get(j) + ";");
                    proxy.eval("evidence{" + ((ORDER - (i + 2))*(XSIZE+1) + XSIZE + 1) + "} = " + lastOutputs.get(i) + ";");
                } else {
                    for(int j = 0;j<XSIZE;j++) 
                        proxy.eval("evidence{" + ((ORDER - (i + 2))*(XSIZE+1) + j + 1) + "} = " + input.get(j) + ";");
                    proxy.eval("evidence{" + ((ORDER - (i + 2))*(XSIZE+1) + XSIZE + 1) + "} = 1;");
                }
                                
            }
            for(int i = 0;i<XSIZE;i++) {
                proxy.eval("evidence{" + ((ORDER-1)*(XSIZE+1) + i + 1) + "} = " + input.get(i) + ";");
            }
//            proxy.eval("evidence\n");
            proxy.eval("[tmpeng, tmpll] = enter_evidence(bnetengine" + index + ", evidence);");
            proxy.eval("tmp = [];\n");
            proxy.eval("marg1 = marginal_nodes(tmpeng, " + (XSIZE + 1)*ORDER + ");");
            proxy.eval("tmp = [tmp;marg1.T];");
            ret = (double [])(proxy.returningEval("tmp",1)[0]);
            List<Double> output = new LinkedList<Double>();
            for(double d:ret) {
                output.add(Math.max(0,d));
            }
            int action = Sampler.weighted(output);
//            System.out.println("output: " + output);
//            System.out.println("selected: " + action);
            
            lastInputs.add(0,input);
            lastOutputs.add(0,action+1);
            
            
            return action;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }        
    }
    
    public void replaceLastAction(int a) {
//        System.out.println("replaceLastAction, before: " + lastOutputs);
        lastOutputs.remove(0);
        lastOutputs.add(0,a+1);
//        System.out.println("replaceLastAction, after: " + lastOutputs);
    }

    
    public static void main(String args[]) {
        List<String> traces = new LinkedList<String>();
        traces.add("traces-fourraydistance/trace-m0-StraightLineAgent.txt");
        traces.add("traces-fourraydistance/trace-m1-StraightLineAgent.txt");
        BNetRemoteOrderKDiscrete net = new BNetRemoteOrderKDiscrete(traces,8,2);
        
        List<Double> input = new LinkedList<Double>();
        for(int i = 0;i<9*2;i++) input.add(1.0);
        
        int output = net.run(input);
        System.out.print("Output: " + output);
    }
}
