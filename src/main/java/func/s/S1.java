package func.s;

import node.HealthFunc;
import node.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

public class S1 implements HealthFunc {
    @Override
    public double health(double gen) {
        return 0;
    }

    @Override
    public double health(ArrayList<Double> genList) {
        if (genList.size() != 2)
            throw new NotImplementedException();
        double x1 = genList.get(0);
        double x2 = genList.get(1);
        return -((4 - 2.1 * Math.pow(x1, 2) + Math.pow(x1, 4) / 3) * x1 * x1 + x1 * x2 + 4 * (x2 * x2 - 1) * x2 * x2);
    }

    @Override
    public int NP() {
        return 0;
    }

    @Override
    public int GP() {
        return 0;
    }

    @Override
    public int LP() {
        return 0;
    }

    @Override
    public int NSEEDS() {
        return 0;
    }

    @Override
    public double FPR() {
        return 0;
    }

    @Override
    public void makeStats(ArrayList<Node> population) {

    }

    @Override
    public void refresh() {

    }
}
