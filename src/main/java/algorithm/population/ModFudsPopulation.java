package algorithm.population;

import algorithm.PopulationFunc;
import node.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ModFudsPopulation implements PopulationFunc {
    @Override
    public ArrayList<Node> getPopulation(ArrayList<Node> nodes, int N) {
        return null;
    }

    @Override
    public ArrayList<Node> getPopulation(ArrayList<Node> nodeList, int N, double min, double max) {
        for (Node node : nodeList) {
            if (node.getHealth() > max)
                max = node.getHealth();
            if (node.getHealth() < min)
                min = node.getHealth();
        }
        int iter = nodeList.size();
        //for (int i = N; i < iter; i++) {
        int nInterval = (int) Math.sqrt(nodeList.size());
        double eps = (max - min) / nInterval;
        ArrayList<ArrayList<Node>> groupedNodes = new ArrayList<>();
        ArrayList<double[]> intervalList = new ArrayList<>();
        {
            double start = min;
            double end = start + eps;
            while (end < max + 2 * eps) {
                double[] part = new double[2];
                part[0] = start;
                part[1] = end;
                intervalList.add(part);
                start += eps;
                end += eps;
                groupedNodes.add(new ArrayList<>());
            }
        }
        nodeList.sort(Comparator.comparing(Node::getHealth));

        int added = 0;
        for (Node node : nodeList) {
            boolean isAdded = false;
            int k = 0;
            for (double[] interval : intervalList) {
                if (node.getHealth() >= interval[0] && node.getHealth() <= interval[1]) {
                    groupedNodes.get(k).add(node);
                    added++;
                    isAdded = true;
                    break;
                }
                k++;
            }
            if (!isAdded) {
                System.out.println(node);
                System.out.println("Max: " + max);
                System.out.println("Min: " + min);
                intervalList.forEach(e -> System.out.println(e[0] + " " + e[1]));
                if (node.getHealth() >= intervalList.get(intervalList.size() - 1)[0] && node.getHealth() <= intervalList.get(intervalList.size() - 1)[1]) {
                    System.out.println(Arrays.toString(intervalList.get(intervalList.size() - 1)));
                }
            }
        }
        if (added != nodeList.size()) {
            throw new NullPointerException();
        }
        int maxLenOfInterval = 0;
        for (ArrayList<Node> list : groupedNodes) {
            if (list != null) {
                if (list.size() > maxLenOfInterval)
                    maxLenOfInterval = list.size();
            }
        }

        ArrayList<ArrayList<Node>> groupedNodesWithSameCount = new ArrayList<>();
        for (ArrayList<Node> list : groupedNodes) {
            if (list != null) {
                if (list.size() == maxLenOfInterval)
                    groupedNodesWithSameCount.add(list);
            }
        }
        int indexWithMinHealth = 0;
        double minHealth = Double.MAX_VALUE;
        int k = 0;
        for (ArrayList<Node> list : groupedNodesWithSameCount) {
            double totalHealth = 0;
            for (Node node : list)
                totalHealth += node.getHealth();
            if (totalHealth < minHealth) {
                minHealth = totalHealth;
                indexWithMinHealth = k;
            }
            k++;
        }
        double minHealthInGroup = Double.MAX_VALUE;
        int indexForDelete = 0;
        for (k = 0; k < groupedNodes.get(indexWithMinHealth).size(); k++) {
            Node node = groupedNodes.get(indexWithMinHealth).get(k);
            if (node.getHealth() < minHealthInGroup) {
                indexForDelete = k;
                minHealthInGroup = node.getHealth();
            }
        }
        nodeList.remove(groupedNodesWithSameCount.get(indexWithMinHealth).get(indexForDelete));
        int debug = 0;
        // }
        if (nodeList.size() != N)
            return getPopulation(nodeList, N, min, max);
        return nodeList;
    }

    @Override
    public String toString() {
        return "MOD_FUDS";
    }
}
