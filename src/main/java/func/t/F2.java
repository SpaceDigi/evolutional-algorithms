package func.t;

import node.HealthFunc;
import node.Node;
import util.Utils;

import java.util.ArrayList;

public class F2 implements HealthFunc {
    public double health(double gen) {
        return Math.pow(Math.E, -2 * Math.log(2) * Math.pow((gen - 0.1) / 0.8, 2)) * F1.getInstance().health(gen);
    }

    @Override
    public double health(ArrayList<Double> genList) {
        double sum = 0;
        for (Double gen : genList)
            sum += health(gen);
        return sum;
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
        double[] y = {1, 0.9172, 0.7078, 0.4595, 0.2510};
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
        NP = 0;
        GP = 0;
        LP = 0;
        nodes = null;
    }


    private static final F2 F2 = new F2();

    public static F2 getInstance() {
        return F2;
    }

    @Override
    public String toString() {
        return "Deba 2";
    }
}
