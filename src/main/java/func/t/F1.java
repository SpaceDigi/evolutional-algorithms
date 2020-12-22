package func.t;

import node.HealthFunc;
import node.Node;
import util.Utils;

import java.util.ArrayList;


public class F1 implements HealthFunc {
    public double health(double gen) {
        return Math.pow(Math.sin(5 * Math.PI * gen), 6);
    }

    @Override
    public double health(ArrayList<Double> genList) {
        double sum = 0;
        for (Double gen : genList)
            sum += health(gen);
        return sum / genList.size();
    }

    private int NP;
    private int GP;
    private int LP;

    @Override
    public int NP() {
        return NP;
    }

    @Override
    public int GP() {
        return GP;
    }

    @Override
    public int LP() {
        return LP;
    }

    @Override
    public int NSEEDS() {
        return nodes.size();
    }

    private ArrayList<Node> nodes;

    @Override
    public void makeStats(ArrayList<Node> population) {
        this.nodes = population;
        this.nodes = Utils.findPeaks(nodes);
        double beta = 0.01;
        double[] x = {0.1, 0.3, 0.5, 0.7, 0.9};
        double[] y = {1, 1, 1, 1, 1};
        for (int k = 0; k < x.length; k++) {
            for (Node peak : nodes) {
                if (Math.abs(x[k] - peak.getGenList().get(0)) < beta && Math.abs(y[k] - peak.getHealth()) < beta) {
                    NP++;
                    if (y[k] == 1) {
                        GP++;
                    }
                }
            }
        }
        LP = NP - GP;
    }

    @Override
    public void refresh() {
        NP=0;
        GP=0;
        LP=0;
        nodes=null;
    }

    private static final F1 F1 = new F1();

    public static F1 getInstance() {
        return F1;
    }

    @Override
    public String toString() {
        return "Deba 1";
    }
}
