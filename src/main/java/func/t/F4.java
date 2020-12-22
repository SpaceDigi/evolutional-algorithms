package func.t;

import node.HealthFunc;
import node.Node;
import util.Utils;

import java.util.ArrayList;

public class F4 implements HealthFunc {
    @Override
    public double health(double gen) {
        return Math.pow(Math.E, -2 * Math.log(2) * Math.pow((gen - 0.08) / 0.854, 2)) * F3.getInstance().health(gen);
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
        double[] x = {0.08, 0.247, 0.451, 0.681, 0.934};
        double[] y = {1, 0.948, 0.77, 0.503, 0.25};
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


    private static final F4 F4 = new F4();

    public static F4 getInstance() {
        return F4;
    }

    @Override
    public String toString() {
        return "Deba 4";
    }
}
