/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.matlab;

import java.util.LinkedList;
import java.util.List;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

/**
 *
 * @author santi
 */
public class NNet {
    int inputs;
    int hidden;
    int outputs;
    
    double IW[], HW[];
    double HB[], OB[];
    
    public NNet(List<String> traces, int XSIZE, int YSIZE) {
        String matlabCommand = "learnNNWeights([";;
        for(String trace:traces) {
             matlabCommand+="'" + trace + "';";
        }
        matlabCommand+="]," + XSIZE + "," + YSIZE + ");";

        MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).build();
        MatlabProxyFactory factory = new MatlabProxyFactory(options);
        try {
            MatlabProxy proxy = factory.getProxy();
            Object[] RA = proxy.returningEval(matlabCommand,5);
            proxy.disconnect();

            double ranges[] = (double [])RA[4];
            
            // create the NNet:
            inputs = XSIZE;
            outputs = YSIZE;
            
            // check if any input/output variable had 0 range (matlab ignores them):
            int reducedXSIZE = 0;
            int reducedYSIZE = 0;
            int inputMapping[] = new int[XSIZE];
            int outputMapping[] = new int[YSIZE];
            for(int i = 0;i<XSIZE;i++) {
                if (ranges[i]==ranges[i+XSIZE+YSIZE]) {
                    inputMapping[i] = -1;
                } else {
                    inputMapping[i] = reducedXSIZE++;
                }
            }
            for(int i = 0;i<YSIZE;i++) {
                if (ranges[i+XSIZE]==ranges[i+XSIZE+XSIZE+YSIZE]) {
                    outputMapping[i] = -1;
                } else {
                    outputMapping[i] = reducedYSIZE++;
                }
            }
            
            // copy the weights:
            double []mlIW = (double [])RA[0];
            double []mlLW = (double [])RA[1];
            
            hidden = mlIW.length/reducedXSIZE;
            IW = new double[inputs*hidden];
            HW = new double[hidden*outputs];
            for(int i = 0;i<inputs;i++) {
                int ri = inputMapping[i];
                if (ri!=-1) {
                    for(int h = 0;h<hidden;h++) {
                        IW[i*hidden+h] = mlIW[ri*hidden+h];
                    }
                }                
            }
            for(int h = 0;h<hidden;h++) {
                for(int o = 0;o<outputs;o++) {
                    int ro = outputMapping[o];
                    if (ro!=-1) {
                        HW[h*outputs+o] = mlLW[h*reducedYSIZE+ro];
                    }
                }                
            }
            
            // copy the biases:
            double []mlB1 = (double [])RA[2];
            double []mlB2 = (double [])RA[3];
            
            HB = new double[hidden];
            OB = new double[outputs];
            for(int h = 0;h<hidden;h++) {
                HB[h] = mlB1[h];
            }
            for(int o = 0;o<outputs;o++) {
                int ro = outputMapping[o];
                if (ro!=-1) {
                    OB[o] = mlB2[ro];
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public List<Double> run(List<Double> input) {
        List<Double> output = new LinkedList<Double>();
        double []h_activation = new double[hidden];
        
        // hidden layer:
        for(int h = 0;h<hidden;h++) {
            double accum = 0;
            for(int i = 0;i<inputs;i++) {
                accum += input.get(i)*IW[i*hidden+h];
            }
            h_activation[h] = activationSigmoid(accum,HB[h]);
        }
        
        for(int o = 0;o<outputs;o++) {
            double accum = 0;
            for(int h = 0;h<hidden;h++) {
                accum += h_activation[h]*HW[h*outputs+o];
            }
            output.add(activationLinear(accum,OB[o]));
        }        
        return output;
    }

    
    public double[] run(double[] input) {
        double []output = new double[outputs];
        double []h_activation = new double[hidden];
        
        // hidden layer:
        for(int h = 0;h<hidden;h++) {
            double accum = 0;
            for(int i = 0;i<inputs;i++) {
                accum += input[i]*IW[i*hidden+h];
            }
            h_activation[h] = activationSigmoid(accum,HB[h]);
            System.out.println(h_activation[h]);
        }
        
        for(int o = 0;o<outputs;o++) {
            double accum = 0;
            for(int h = 0;h<hidden;h++) {
                accum += h_activation[h]*HW[h*outputs+o];
            }
            output[o]=activationLinear(accum,OB[o]);
        }        
        return output;
    }

    
    double activationSigmoid(double input, double bias) {
        
        double tmp = 1.0/(1.0+Math.exp(-(input+bias)));
        if (tmp<0) return -1;
        if (tmp>0) return 1;
        return 0;
    }

    double activationLinear(double input, double bias) {
        return input + bias;
    }
    
    
    public static void main(String args[]) {
        List<String> traces = new LinkedList<String>();
        traces.add("traces-fourraydistance/trace-m0-SmartRandomExplorerAgent-nnet.txt");
        traces.add("traces-fourraydistance/trace-m1-SmartRandomExplorerAgent-nnet.txt");
        NNet net = new NNet(traces,8,5);
        
        double []input={0,0,0,0,0,0,0,0};
        double []output;
        
        output = net.run(input);
        
        for(double o:output) System.out.print(o + " ");
    }
}
