/********************************************************************************
Organization		: Georgia Institute of Technology
Institute		: Cognitive Computing Group(CCL)
Authors			: Santiago Ontanon
Class			: Sampler
Function		: This class contains methods to sample
                          from a given distribution. Including support
                          for exploration vs exploitation.
 *********************************************************************************/
package util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Sampler {

    static Random generator = new Random();

    /*
     * Returns a random element in the distribution
     */
    public static int random(List<Double> distribution) {
        return generator.nextInt(distribution.size());
    }

    /*
     * Returns the element with maximum probability (ties are resolved randomly)
     */
    public static int max(List<Double> distribution) throws Exception {
        List<Integer> best = new LinkedList<Integer>();
        double max = distribution.get(0);

        for (int i = 0; i < distribution.size(); i++) {
            Double f = distribution.get(i);
            if (f == max) {
                best.add(new Integer(i));
            } else {
                if (f > max) {
                    best.clear();
                    best.add(new Integer(i));
                    max = f;
                }
            }
        }

        if (best.size() > 0) {
            return best.get(generator.nextInt(best.size()));
        }

        throw new Exception("Input distribution empty in Sampler.max!");
    }

    /*
     * Returns the score with maximum probability (ties are resolved randomly)
     */
    public static Double maxScore(List<Double> distribution) {
        List<Integer> best = new LinkedList<Integer>();
        double max = distribution.get(0);

        for (int i = 0; i < distribution.size(); i++) {
            Double f = distribution.get(i);
            if (f == max) {
                best.add(new Integer(i));
            } else {
                if (f > max) {
                    best.clear();
                    best.add(new Integer(i));
                    max = f;
                }
            }
        }

        return max;

    }

    /*
     * Returns an element in the distribution, using the weights as their relative probabilities
     */
    public static int weighted(List<Double> distribution) throws Exception {
        double total = 0, accum = 0, tmp;

        for (double f : distribution) {
            total += f;
        }
        
        if (total==0) return generator.nextInt(distribution.size());

        tmp = generator.nextDouble() * total;
        for (int i = 0; i < distribution.size(); i++) {
            accum += distribution.get(i);
            if (accum >= tmp) {
                return i;
            }
        }

        throw new Exception("Input distribution empty in Sampler.weighted!");
    }

    /*
     * Returns an element in the distribution following the probabilities, but using 'e' as the exploration factor.
     * For instance:
     * If "e" = 1.0, then it has the same effect as the "max" method
     * If "e" = 0.5, then it has the same effect as the "weighted" method
     * If "e" = 0, then it has the same effect as the "random" method
     */
    public static int explorationWeighted(List<Double> distribution, double e) throws Exception {
        /*
         * exponent = 1/(1-e)-1
         */

        double exponent = 0;
        double quotient = 1 - e;
        if (quotient != 0) {
            exponent = 1 / quotient - 1;
        } else {
            exponent = 1000;
        }
        List<Double> exponentiated = new LinkedList<Double>();

        for (Double f : distribution) {
            exponentiated.add(new Double(Math.pow(f, exponent)));
        }

        return weighted(exponentiated);
    }

/*
    // Example:
    public static void main(String args[]) {
        int histo[] = {0, 0, 0, 0, 0};
        List<Double> d = new LinkedList<Double>();

        d.add(0.1);
        d.add(0.5);
        d.add(0.89);
        d.add(0.9);
        d.add(0.9);

        try {
            for (int i = 0; i < 1000; i++) {
                histo[random(d)]++;
            }
            System.out.println("Random: [" + histo[0] + "," + histo[1] + "," + histo[2] + "," + histo[3] + "," + histo[4] + "]");
            histo[0] = histo[1] = histo[2] = histo[3] = histo[4] = 0;

            for (int i = 0; i < 1000; i++) {
                histo[max(d)]++;
            }
            System.out.println("Max: [" + histo[0] + "," + histo[1] + "," + histo[2] + "," + histo[3] + "," + histo[4] + "]");
            histo[0] = histo[1] = histo[2] = histo[3] = histo[4] = 0;

            for (int i = 0; i < 1000; i++) {
                histo[weighted(d)]++;
            }
            System.out.println("Weighted: [" + histo[0] + "," + histo[1] + "," + histo[2] + "," + histo[3] + "," + histo[4] + "]");
            histo[0] = histo[1] = histo[2] = histo[3] = histo[4] = 0;

            for (double e = 0; e <= 1.0; e += 0.015625) {
                for (int i = 0; i < 1000; i++) {
                    histo[explorationWeighted(d, e)]++;
                }
                System.out.println("explorationWeighted(" + e + "): [" + histo[0] + "," + histo[1] + "," + histo[2] + "," + histo[3] + "," + histo[4] + "]");
                histo[0] = histo[1] = histo[2] = histo[3] = histo[4] = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 */
}
