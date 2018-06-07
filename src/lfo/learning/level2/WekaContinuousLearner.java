/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lfo.learning.level2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lfo.learning.LFO;
import lfo.simulator.Action;
import lfo.simulator.LearningTrace;
import lfo.simulator.LearningTraceEntry;
import lfo.simulator.perception.Perception;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.lazy.LWL;
import weka.classifiers.meta.RegressionByDiscretization;
import weka.core.*;

/**
 *
 * @author santi
 */
public class WekaContinuousLearner implements LFO {
    HashMap<String,Classifier> classifiers = new HashMap<String,Classifier>();
    String aType = null;
    List<String> XAttributes = new LinkedList<String>();
    List<String> YAttributes = new LinkedList<String>();
    
    public WekaContinuousLearner() {
    }

    public void learn(List<LearningTrace> traces) {
        // convert the traces to WEKA:
        List<LearningTraceEntry> examples = new LinkedList<LearningTraceEntry>();
        for(LearningTrace lt:traces) {
            for(LearningTraceEntry lte:lt.getEntries()) {
                examples.add(lte);
            }
        }
        Perception p = examples.get(0).perception;
        for(String name:p.getValues().keySet()) {
            XAttributes.add(name);
        }
        Action a = examples.get(0).action;
        aType = a.getName();
        for(String name:a.getAttributes().keySet()) {
            YAttributes.add(name);
        }
        
        for(String className:YAttributes) {
            FastVector attributes = new FastVector();
            attributes.addElement(new Attribute("Y" + className));
            for(String name:XAttributes) attributes.addElement(new Attribute("X" + name));
            
            Instances training = new Instances("LFO",attributes,examples.size());
            training.setClassIndex(0);

            for(LearningTraceEntry lte:examples) {
                Instance i = new Instance(attributes.size());
                int idx = 0;
                i.setValue(idx, lte.action.getParameterDouble(className));
                idx++;
                for(String name:XAttributes) {
                    i.setValue(idx, lte.perception.get(name).doubleValue());
                    idx++;
                }
                training.add(i);
            }
            
            System.out.println(training.toSummaryString());

            // Learn the regression model:
//            Classifier c = new LinearRegression();
            Classifier c = new RegressionByDiscretization();
//            Classifier c = new MultilayerPerceptron();
            try {
                c.buildClassifier(training);
            } catch (Exception e) {
                e.printStackTrace();
            }
            classifiers.put(className,c);
        }
    }

    public void start() {
    }

    public Action cycle(Perception s, int ID, int time) {
        Action a = new Action(aType, ID);
        for(String className:YAttributes) {        
            Instance i = new Instance(1 + XAttributes.size());
            int idx = 1;
            for(String name:XAttributes) {
                i.setValue(idx, s.get(name).doubleValue());
                idx++;
            }
            Classifier l = classifiers.get(className);
            if (l!=null) try {
                a.addParameter(className,l.classifyInstance(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return a;
    }

    public void end() {
    }
}
