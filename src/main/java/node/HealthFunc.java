package node;

import java.util.ArrayList;

public interface HealthFunc {

    double health(double gen);

    double health(ArrayList<Double> genList);

    int NP();

    int GP();

    int LP();

    int NSEEDS();

    default double FPR() {
        return (NSEEDS() - NP()) / (double) NSEEDS();
    }

    void makeStats(ArrayList<Node> population);

    void refresh();
}
