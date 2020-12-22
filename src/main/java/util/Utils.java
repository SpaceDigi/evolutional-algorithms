package util;

import node.Node;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static java.util.Collections.emptyList;

public class Utils {

    /*public static double distance(double x1, double y1, double x2, double y2) {
        double ac = Math.pow(y2 - y1, 2);
        double cb = Math.pow(x2 - x1, 2);

        return Math.sqrt(ac + cb);
    }*/
    public static double distance(double x1, double y1, double x2, double y2) {
        double ac = Math.abs(y2 - y1);
        double cb = Math.abs(x2 - x1);

        return Math.hypot(ac, cb);
    }

    public static double distance(double x1, double x2) {
        double cb = x2 - x1;

        return cb * cb;
    }

    public static double distance(Node prevNode, Node currNode) {
        double partSum = 0;
        for (int index = 0; index < prevNode.getGenList().size(); index++) {
            partSum += distance(prevNode.getGenList().get(index), prevNode.getHealth(), currNode.getGenList().get(index), currNode.getHealth());
            // partSum += distance(prevNode.getGenList().get(index), currNode.getGenList().get(index));
        }
        //partSum = prevNode.getGenList().size();
        return Math.sqrt(partSum);
    }

    public static double sigma(ArrayList<Node> prev, ArrayList<Node> curr) {
        double diffSquareSum = 0.0;

        int N = Math.min(prev.size(),curr.size());
        for (int k = 0; k < N; k++) {
            Node prevNode = prev.get(k);
            Node currNode = curr.get(k);
            double partSum = 0;
            for (int index = 0; index < prevNode.getGenList().size(); index++) {
//                partSum += distance(prevNode.getGenList().get(index), prevNode.getHealth(), currNode.getGenList().get(index), currNode.getHealth());
                partSum += distance(prevNode.getGenList().get(index), currNode.getGenList().get(index));
            }
            //partSum = prevNode.getGenList().size();
            diffSquareSum += Math.sqrt(partSum);
        }
        double sigma = diffSquareSum / N;
        //NormalDistribution nd = new NormalDistribution(0, sigma);
        return sigma;
    }

    public static double getMutation(double sigma) {
        double upper = sigma;
        double lower = -sigma;
        double result = Math.random() * (upper - lower) + lower;
        return result;
    }

    public static boolean isNeedMutate(double prob) {
        return Math.random() < prob;
    }

    public static ArrayList<Node> findPeaks(ArrayList<Node> population) {
        if (population == null)
            return new ArrayList<>();
        population.sort(Comparator.comparing(Node::getHealth).reversed());
        ArrayList<Node> peaks = new ArrayList<>();
        boolean add = false;
        double eps = 0.03;
        for (Node node : population) {
            for (Node peak : peaks) {
                double distance = 0;
                double partSum = 0;
                for (int index = 0; index < node.getGenList().size(); index++) {
                    partSum += distance(node.getGenList().get(index), peak.getGenList().get(index));
                }
                distance += Math.sqrt(partSum) / population.size();
                if (distance <= eps && add) add = true;
            }
            if (add)
                peaks.add(node);
            add = false;
        }
        return peaks;
    }

}
