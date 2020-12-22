package algorithm.population;

import algorithm.PopulationFunc;
import node.Node;

import java.util.ArrayList;
import java.util.Comparator;

public class CrowdTourPopulation implements PopulationFunc {
    @Override
    public ArrayList<Node> getPopulation(ArrayList<Node> nodeList, int N) {
        ArrayList<Node> tourNodes = new ArrayList<>();
        for (Node node : nodeList) {
            tourNodes.add(node);
        }
        nodeList.forEach(node -> {
            if (!node.getChildList().isEmpty()) {
                ArrayList<Node> group = new ArrayList<>(node.getChildList());
                group.add(node);
                group.sort(Comparator.comparing(Node::getHealth).reversed());
                for (Node child : group) {
                    tourNodes.remove(child);
                }
                tourNodes.add(group.get(0));
            }
        });
        return tourNodes;
    }

    @Override
    public ArrayList<Node> getPopulation(ArrayList<Node> nodes, int N, double min, double max) {
        return getPopulation(nodes, N);
    }

    @Override
    public String toString() {
        return "CROWD_TOUR";
    }
}
