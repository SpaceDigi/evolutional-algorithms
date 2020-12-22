package algorithm.parent;

import algorithm.ParentFunc;
import node.Node;

import java.util.ArrayList;
import java.util.Random;

public class SusParent implements ParentFunc {
    @Override
    public ArrayList<Node> chooseParentForNChild(ArrayList<Node> nodeList, int N) {
        double aggregateFitness = 0;
        for (Node candidate : nodeList) {
            aggregateFitness += getAdjustedFitness(candidate.getHealth(), true);
        }
        ArrayList<Node> selection = new ArrayList<>(N);
        // Pick a random offset between 0 and 1 as the starting point for selection.
        double startOffset = new Random().nextDouble();
        double cumulativeExpectation = 0;
        int index = 0;
        for (Node candidate : nodeList) {

            // Calculate the number of times this candidate is expected to
            // be selected on average and add it to the cumulative total
            // of expected frequencies.
            cumulativeExpectation += getAdjustedFitness(candidate.getHealth(),
                    true) / aggregateFitness * N;

            // If f is the expected frequency, the candidate will be selected at
            // least as often as floor(f) and at most as often as ceil(f). The
            // actual count depends on the random starting offset.
            while (cumulativeExpectation > startOffset) {
                for (int k = 0; k < N; k++) {
                    selection.add(candidate);
                }
                return selection;

            }
        }
        return selection;
    }

    private static double getAdjustedFitness(double rawFitness, boolean naturalFitness) {
        if (naturalFitness) {
            return rawFitness;
        } else {
            // If standardised fitness is zero we have found the best possible
            // solution.  The evolutionary algorithm should not be continuing
            // after finding it.
            return rawFitness == 0 ? Double.POSITIVE_INFINITY : 1 / rawFitness;
        }
    }

    @Override
    public String toString() {
        return "SUS";
    }
}
