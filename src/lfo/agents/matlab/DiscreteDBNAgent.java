/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfo.agents.matlab;

import lfo.simulator.Action;
import lfo.simulator.perception.Perception;
import util.Sampler;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import lfo.agents.Agent;
import lfo.matlab.MatlabPerceptionTranslator;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

/**
 *
 * @author santi
 */
public class DiscreteDBNAgent  extends Agent {
    Random r = new Random();
    int layers = 1;
    int E_size = 1;
    int X_size = 8;
    int layer_size = E_size + X_size + 1;
    int VARS = layer_size * layers;
    boolean dag[][] = null;
    int var_sizes[] = null;
    List<double []> pdt_l = null;
    int state[];
    
    static int STATES = 4;

    int time_step = 0;
    
    public static MatlabProxy proxy = null;    
    
    public static DiscreteDBNAgent getLfODBNFromMATLAB(List<String> traces, String fileName, int a_XSIZE) throws FileNotFoundException, IOException {        
        String matlabCommand = "[dbn,bnetengine] = learnLfODBN([";;
        for(String trace:traces) {
             matlabCommand+="'" + trace + "';";
        }
        matlabCommand+="], 20 ," + STATES + "," + a_XSIZE + "," + 1 + ",'" + fileName + "');";

        System.out.println(matlabCommand);
        
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
        return new DiscreteDBNAgent(fileName);
    }    
    
    
    public static DiscreteDBNAgent getIOHMMFromMATLAB(List<String> traces, String fileName, int a_XSIZE) throws FileNotFoundException, IOException {        
        String matlabCommand = "[dbn,bnetengine] = learnIOHMM([";;
        for(String trace:traces) {
             matlabCommand+="'" + trace + "';";
        }
        matlabCommand+="], 20 ," + STATES + "," + a_XSIZE + "," + 1 + ",'" + fileName + "');";

        System.out.println(matlabCommand);
        
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
        
        return new DiscreteDBNAgent(fileName);
    }    
    
    
    public static DiscreteDBNAgent getHMMFromMATLAB(List<String> traces, String fileName, int a_XSIZE) throws FileNotFoundException, IOException {        
        String matlabCommand = "[dbn,bnetengine] = learnHMM([";;
        for(String trace:traces) {
             matlabCommand+="'" + trace + "';";
        }
        matlabCommand+="], 20 ," + STATES + "," + a_XSIZE + "," + 1 + ",'" + fileName + "');";

        System.out.println(matlabCommand);
        
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
        
        return new DiscreteDBNAgent(fileName);
    }        
    

    public DiscreteDBNAgent(String fileName) throws FileNotFoundException, IOException {
        BufferedReader fr = new BufferedReader(new FileReader(fileName));

        // header:
        List<Double> line = readLine(fr);
        layers = (int)(double) line.get(0);
        E_size = (int)(double) line.get(1);
        X_size = (int)(double) line.get(2);
//        Y_size = (int)(double) line.get(3);   // Y_size is always 1 for now
        layer_size = E_size + X_size + 1;
        VARS = layer_size * layers;

        // state:
        state = new int[VARS];
        for(int i = 0;i<VARS;i++) state[i] = 0;

        // dag:
        dag = new boolean[VARS][VARS];
        for(int i = 0; i<VARS; i++) {
            line = readLine(fr);
            for(int j = 0; j<VARS; j++) {
                if (((int)(double) line.get(j))!=0) dag[i][j] = true;
                                               else dag[i][j] = false;
            }
        }

        line = readLine(fr);
        var_sizes = new int[line.size()];
        for(int i = 0;i<VARS;i++) {
            var_sizes[i] = (int)(double) line.get(i);
            //System.out.println("Var " + i + " size: " + var_sizes[i]);
        }

        // PDTs:
        pdt_l = new LinkedList<double []>();
        for(int i = 0; i<VARS; i++) {
            line = readLine(fr);
            double []pdt = new double[line.size()];
            for(int j = 0;j<pdt.length;j++) pdt[j] = line.get(j);
            pdt_l.add(pdt);
        }
        
        // Output some debug information about states and actions:
        for(int i = 0;i<var_sizes[0];i++) {
            //System.out.println("State " + i);
            
            // probability of changing to other states (regardless of input):
            int V = 0;
            double []pdt = pdt_l.get(V);
            for(int j = 0;j<var_sizes[V];j++) {
                double accum = 0;
//                for(int k = j;k<pdt.length;k+=var_sizes[V]) accum+=pdt[k];
                int chunk = pdt.length/var_sizes[V];
                for(int k = chunk*j;k<chunk*(j+1);k+=var_sizes[V]) accum+=pdt[k];
                //System.out.println("  " + i + " -> " + j + " : " + accum);
            }

            // probability of each of the actions:
            V = E_size + X_size;
            pdt = pdt_l.get(V);
            for(int j = 0;j<var_sizes[V];j++) {
                double accum = 0;
//                for(int k = j;k<pdt.length;k+=var_sizes[V]) accum+=pdt[k];
                int chunk = pdt.length/var_sizes[V];
                for(int k = chunk*j;k<chunk*(j+1);k+=var_sizes[V]) accum+=pdt[k];
                //System.out.println("  " + j + " : " + accum);
            }
        }
        
    }

    List<Double> readLine(BufferedReader fr) throws IOException {
        List<Double> l = new LinkedList<Double>();
        String buffer = "";
        while(fr.ready()) {
            int c = fr.read();
            if (c==',' || c==13 || c==10) {
                l.add(Double.parseDouble(buffer));
                buffer = "";
                if (c==13 || c==10) return l;
            } else {
                buffer += (char)c;
            }
        }
        return l;
    }


    public void start()
    {
        time_step = 0;
    }


    public Action cycle(int id,Perception p, double timeStep) {
        Action l[]={null,new Action("up",id),new Action("right",id),new Action("left",id),new Action("down",id)};


        int current_layer = 0;
        if (time_step>=layers) {
            // copy the state of variables from layer i to layer i-1:
            for(int i = 0;i<layers-1;i++) {
                for(int j = 0;j<layer_size;j++) {
                    state[j+i*layer_size] = state[j+(i+1)*layer_size];
                }
            }
            current_layer = layers-1;
        } else {
            current_layer = time_step;
        }

        // write the observation values:
        List<Integer> X = MatlabPerceptionTranslator.translateToMatlabInteger(p);
        if (X.size()!=X_size) System.err.println("DBN X size mismatches with actual perception size!");
        for(int i = 0;i<X_size;i++) {
            state[E_size + i + current_layer*layer_size] = X.get(i)-1;  // we subtract 1 to convert from Matlab (first index = 1) to java (first index = 0)
        }

        // sample the internal state:
        for(int i = 0;i<E_size;i++) {
            int v = i + layer_size*current_layer;
            sampleVariable(v);
        }

        // sample the action variable:
        int action_variable = E_size + X_size + layer_size*current_layer;
        sampleVariable(action_variable);
        
        //System.out.print("DBN state: [ ");
        //for(int i = 0;i<state.length;i++) System.out.print(state[i] + " ");
        //System.out.println(" ]");
        //System.out.print("Sizes: [ ");
        //for(int i = 0;i<state.length;i++) System.out.print(var_sizes[i] + " ");
        //System.out.println(" ]");
        
        time_step++;

        return l[state[action_variable]];
    }


    void sampleVariable(int v) {
        int offset = 0;
        int skip = 1;

//        System.out.print("Sampling variable " + v + " [0 ... " + (var_sizes[v]-1) + "]: ");

        for(int j=0;j<VARS;j++) {
            if (dag[j][v]) {
                int value = state[j];
                offset += skip*value;
                if (value>=var_sizes[j]) {
                    System.err.println("Variable " + j + " has value " + value + " outside of its range!!! [0 - " + (var_sizes[j]-1) + "]");
                }
                skip *= var_sizes[j];
            }
        }

        List<Double> dist = new LinkedList<Double>();
        double pdt[] = pdt_l.get(v);
        for(int j = 0;j<var_sizes[v];j++) {
            dist.add(pdt[offset + j*skip]);
        }

//        System.out.print("State: ");
//        for(int j = 0;j<state.length;j++) System.out.print(state[j] + " ");
//        System.out.println("");

        try {
            //System.out.print("  Sampling " + v + " from: " + dist);
            state[v] = Sampler.weighted(dist);
            //System.out.println("   -> " + state[v]);
        } catch(Exception e) {
            e.printStackTrace();
            state[v] = 0;
        }
    }


	public void replaceLastAction(Action a) {
		int act = 0;
    	
    	
		if (a==null) act = 0;
		else if (a.getName().equals("up")) act = 1;
		else if (a.getName().equals("right")) act = 2;
		else if (a.getName().equals("left")) act = 3;
		else if (a.getName().equals("down"))act = 4;
		else {
		    System.err.println("DiscreteBNetOrderKAgent.replaceLastAction: Replacing an action with an invalid one!");
		}
    	
    	if (time_step==1){//accounts for the initial action
    	state[layer_size-1] = act; //fix the set values to represent the correct position in the array.
    	}else{
    	state[VARS-1] = act;//fix the set values to represent the correct position in the array.
    	}
		
	}

}
