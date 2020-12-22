package algorithm;

import node.Node;

import java.util.ArrayList;

public interface PopulationFunc {

    ArrayList<Node> getPopulation(ArrayList<Node> nodes, int N);

    default ArrayList<Node> getPopulation(ArrayList<Node> nodes, int N,double min, double max) {
        return null;
    }

}
